package me.wcy.init.compiler

import com.squareup.javapoet.*
import me.wcy.init.annotation.InitTask
import me.wcy.init.annotation.TaskInfo
import me.wcy.init.annotation.TaskLoader
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Filer
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.Modifier
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

    @ExperimentalStdlibApi
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
        val taskInfoCn = ClassName.get(TaskInfo::class.java)

        /**
         * Param type: List<ITaskInfo>
         */
        val inputMapTypeName = ParameterizedTypeName.get(
            ClassName.get(List::class.java),
            ClassName.get(TaskInfo::class.java)
        )

        /**
         * Param name: taskList
         */
        val groupParamSpec =
            ParameterSpec.builder(inputMapTypeName, ProcessorUtils.PARAM_NAME).build()

        /**
         * Method: @Override public void loadTask(List<ITaskInfo> taskList);
         */
        val loadTaskMethodBuilder = MethodSpec.methodBuilder(ProcessorUtils.METHOD_NAME)
            .addAnnotation(Override::class.java)
            .addModifiers(Modifier.PUBLIC)
            .addParameter(groupParamSpec)

        for (element in taskElements) {
            val typeMirror = element.asType()
            val task = element.getAnnotation(InitTask::class.java)
            if (typeUtil.isSubtype(typeMirror, taskType.asType())) {
                Log.i("[InitTask] Found task: $typeMirror")

                val taskCn = ClassName.get(element as TypeElement)

                /**
                 * Statement: taskList.add(new TaskInfo(name, onlyMainProcess, background,
                 * priority, depends, task));
                 */
                loadTaskMethodBuilder.addStatement(
                    "\$N.add(new \$T(\$S, \$L, \$L, \$L, \$L, new \$T()))",
                    ProcessorUtils.PARAM_NAME,
                    taskInfoCn,
                    task.name,
                    task.background,
                    task.priority,
                    formatArray(task.process),
                    formatArray(task.depends),
                    taskCn
                )
            }
        }

        /**
         * Write to file
         */
        JavaFile.builder(
            ProcessorUtils.PACKAGE_NAME,
            TypeSpec.classBuilder("TaskLoader\$$moduleName")
                .addJavadoc(ProcessorUtils.JAVADOC)
                .addSuperinterface(ClassName.get(TaskLoader::class.java))
                .addModifiers(Modifier.PUBLIC)
                .addMethod(loadTaskMethodBuilder.build())
                .build()
        )
            .build()
            .writeTo(filer)

        return true
    }

    @ExperimentalStdlibApi
    private fun formatArray(array: Array<String>): String {
        val sb = StringBuilder()
        array.forEach {
            sb.append("\"$it\"").append(",")
        }
        if (sb.isNotEmpty()) {
            sb.deleteAt(sb.length - 1)
        }
        return "new String[]{$sb}"
    }
}