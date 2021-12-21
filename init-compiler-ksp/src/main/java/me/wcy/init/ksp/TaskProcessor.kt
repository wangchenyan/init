package me.wcy.init.ksp

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getAllSuperTypes
import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import me.wcy.init.annotation.InitTask
import me.wcy.init.annotation.ModuleTaskRegister
import me.wcy.init.annotation.TaskInfo

/**
 * Created by wangchenyan.top on 2021/12/21.
 */
@DelicateKotlinPoetApi("")
@KspExperimental
class TaskProcessor : SymbolProcessor, SymbolProcessorProvider {
    private lateinit var logger: KSPLogger
    private lateinit var codeGenerator: CodeGenerator
    private lateinit var moduleName: String

    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        logger = environment.logger
        codeGenerator = environment.codeGenerator

        val moduleName = environment.options["moduleName"]
        if (moduleName.isNullOrEmpty()) {
            throw IllegalArgumentException(
                "[InitTask] Can not find ksp arg 'moduleName', check if has add the code like this in module's build.gradle.kts:\n" +
                        "\n" +
                        "    ksp {\n" +
                        "        arg(\"moduleName\", project.name)\n" +
                        "    }" +
                        "\n"
            )
        }
        this.moduleName = ProcessorUtils.formatModuleName(moduleName)

        logger.warn("[InitTask] Start to process module ${this.moduleName}")
        return this
    }

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val taskList = resolver.getSymbolsWithAnnotation(InitTask::class.java.name).toList()
        if (taskList.isNullOrEmpty()) {
            return emptyList()
        }

        logger.warn("[InitTask] Found tasks, size is ${taskList.size}")

        /**
         * Param type: MutableList<TaskInfo>
         *
         * There's no such type as MutableList at runtime so the library only sees the runtime type.
         * If you need MutableList then you'll need to use a ClassName to create it.
         * [https://github.com/square/kotlinpoet/issues/482]
         */
        val listTypeName =
            ClassName(
                "kotlin.collections",
                "MutableList"
            ).parameterizedBy(TaskInfo::class.asTypeName())

        /**
         * Param name: taskList: MutableList<TaskInfo>
         */
        val groupParamSpec =
            ParameterSpec.builder(ProcessorUtils.PARAM_NAME, listTypeName).build()

        /**
         * Method: override fun register(taskList: MutableList<TaskInfo>)
         */
        val loadTaskMethodBuilder = FunSpec.builder(ProcessorUtils.METHOD_NAME)
            .addModifiers(KModifier.OVERRIDE)
            .addParameter(groupParamSpec)

        taskList.forEach {
            checkDeclaration(it)
            val declaration = it as KSDeclaration
            val taskCn = declaration.toClassName()

            logger.warn("[InitTask] Found task: ${taskCn.canonicalName}")

            val task =
                declaration.getAnnotationsByType(InitTask::class).firstOrNull() ?: return@forEach

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

        /**
         * Write to file
         */
        val fileSpec = FileSpec.builder(ProcessorUtils.PACKAGE_NAME, "TaskRegister\$$moduleName")
            .addType(
                TypeSpec.classBuilder("TaskRegister\$$moduleName")
                    .addKdoc(ProcessorUtils.JAVADOC)
                    .addSuperinterface(ModuleTaskRegister::class.java)
                    .addFunction(loadTaskMethodBuilder.build())
                    .build()
            )
            .build()

        val file =
            codeGenerator.createNewFile(Dependencies.ALL_FILES, fileSpec.packageName, fileSpec.name)
        file.write(fileSpec.toString().toByteArray())

        return emptyList()
    }

    /**
     * 检查注解是否合法
     * 1. 注解类为 Class 类型
     * 2. 注解类实现 IInitTask 接口
     */
    private fun checkDeclaration(annotated: KSAnnotated) {
        check(annotated is KSClassDeclaration) {
            "Type [${annotated}] with annotation [${InitTask::class.java.name}] should be a class"
        }
        checkNotNull(annotated.getAllSuperTypes().find {
            it.declaration.toClassName().canonicalName == INIT_TASK_CLASS_NAME
        }) {
            "Type [${annotated.toClassName().canonicalName}] with annotation [${InitTask::class.java.name}] should extends [$INIT_TASK_CLASS_NAME]"
        }
    }

    companion object {
        private const val INIT_TASK_CLASS_NAME = "me.wcy.init.api.IInitTask"
    }
}