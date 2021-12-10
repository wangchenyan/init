package me.wcy.init.compiler

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import me.wcy.init.annotation.InitTask
import me.wcy.init.annotation.ModuleTaskRegister
import me.wcy.init.annotation.TaskInfo
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Filer
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements
import javax.lang.model.util.Types

/**
 * Created by wcy on 2020/12/10.
 */
class TaskProcessor : AbstractProcessor() {
    private lateinit var filer: Filer
    private lateinit var elementUtil: Elements
    private lateinit var typeUtil: Types
    private lateinit var moduleName: String

    override fun init(processingEnv: ProcessingEnvironment) {
        super.init(processingEnv)

        filer = processingEnv.filer
        elementUtil = processingEnv.elementUtils
        typeUtil = processingEnv.typeUtils
        Log.setLogger(processingEnv.messager)

        val moduleName = processingEnv.options["moduleName"]
        if (moduleName == null || moduleName.isEmpty()) {
            throw IllegalArgumentException(
                "[InitTask] Can not find apt argument 'moduleName', check if has add the code like this in module's build.gradle:\n" +
                        "    In Kotlin:\n" +
                        "    \n" +
                        "    kapt {\n" +
                        "        arguments {\n" +
                        "          arg(\"moduleName\", project.name)\n" +
                        "        }\n" +
                        "    }\n"
            )
        }

        this.moduleName = ProcessorUtils.formatModuleName(moduleName)

        Log.i("[InitTask] Start to deal module ${this.moduleName}")
    }

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        val supportAnnotationTypes = mutableSetOf<String>()
        supportAnnotationTypes.add(InitTask::class.java.canonicalName)
        return supportAnnotationTypes
    }

    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.latestSupported()
    }

    @DelicateKotlinPoetApi("")
    override fun process(
        annotations: MutableSet<out TypeElement>?,
        roundEnv: RoundEnvironment
    ): Boolean {
        val taskElements = roundEnv.getElementsAnnotatedWith(InitTask::class.java)
        if (taskElements == null || taskElements.size == 0) {
            return false
        }

        Log.i("[InitTask] Found tasks, size is ${taskElements.size}")

        val taskType = elementUtil.getTypeElement("me.wcy.init.api.IInitTask")

        /**
         * Param type: MutableList<TaskInfo>
         *
         * There's no such type as MutableList at runtime so the library only sees the runtime type.
         * If you need MutableList then you'll need to use a ClassName to create it.
         * [https://github.com/square/kotlinpoet/issues/482]
         */
        val inputMapTypeName =
            ClassName(
                "kotlin.collections",
                "MutableList"
            ).parameterizedBy(TaskInfo::class.asTypeName())

        /**
         * Param name: taskList: MutableList<TaskInfo>
         */
        val groupParamSpec =
            ParameterSpec.builder(ProcessorUtils.PARAM_NAME, inputMapTypeName).build()

        /**
         * Method: override fun register(taskList: MutableList<TaskInfo>)
         */
        val loadTaskMethodBuilder = FunSpec.builder(ProcessorUtils.METHOD_NAME)
            .addModifiers(KModifier.OVERRIDE)
            .addParameter(groupParamSpec)

        for (element in taskElements) {
            val typeMirror = element.asType()
            val task = element.getAnnotation(InitTask::class.java)
            if (typeUtil.isSubtype(typeMirror, taskType.asType())) {
                Log.i("[InitTask] Found task: $typeMirror")

                val taskCn = (element as TypeElement).asClassName()

                /**
                 * Statement: taskList.add(TaskInfo(name, background, priority, process, depends, task));
                 */
                loadTaskMethodBuilder.addStatement(
                    "%N.add(%T(%S, %L, %L, %L, %L, %T()))",
                    ProcessorUtils.PARAM_NAME,
                    TaskInfo::class.java,
                    task.name,
                    task.background,
                    task.priority,
                    ProcessorUtils.formatArray(task.process),
                    ProcessorUtils.formatArray(task.depends),
                    taskCn
                )
            }
        }

        /**
         * Write to file
         */
        FileSpec.builder(ProcessorUtils.PACKAGE_NAME, "TaskRegister\$$moduleName")
            .addType(
                TypeSpec.classBuilder("TaskRegister\$$moduleName")
                    .addKdoc(ProcessorUtils.JAVADOC)
                    .addSuperinterface(ModuleTaskRegister::class.java)
                    .addFunction(loadTaskMethodBuilder.build())
                    .build()
            )
            .build()
            .writeTo(filer)

        return true
    }
}