package me.wcy.arch.compiler

import com.squareup.javapoet.*
import me.wcy.arch.annotation.AbsModule
import me.wcy.arch.annotation.Module
import me.wcy.arch.annotation.ModuleLoader
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
class ModuleProcessor : AbstractProcessor() {
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
                "[Arch] Can not find apt argument 'moduleName', check if has add the code like this in module's build.gradle:\n" +
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

        Log.w("[Arch] Start to deal module ${this.moduleName}")
    }

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        val supportAnnotationTypes = mutableSetOf<String>()
        supportAnnotationTypes.add(Module::class.java.canonicalName)
        return supportAnnotationTypes
    }

    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.latestSupported()
    }

    override fun process(
        annotations: MutableSet<out TypeElement>?,
        roundEnv: RoundEnvironment
    ): Boolean {
        val moduleElements = roundEnv.getElementsAnnotatedWith(Module::class.java)
        if (moduleElements == null || moduleElements.size == 0) {
            return false
        }

        Log.w("[Arch] Found modules, size is ${moduleElements.size}")

        val moduleType = elementUtil.getTypeElement(AbsModule::class.java.name)

        /**
         * Param type: List<AbsModule>
         */
        val inputMapTypeName = ParameterizedTypeName.get(
            ClassName.get(List::class.java),
            ClassName.get(AbsModule::class.java)
        )

        /**
         * Param name: moduleList
         */
        val groupParamSpec =
            ParameterSpec.builder(inputMapTypeName, ProcessorUtils.PARAM_NAME).build()

        /**
         * Method: @Override public void loadModule(List<AbsModule> moduleList)
         */
        val loadModuleMethodBuilder = MethodSpec.methodBuilder(ProcessorUtils.METHOD_NAME)
            .addAnnotation(Override::class.java)
            .addModifiers(Modifier.PUBLIC)
            .addParameter(groupParamSpec)

        for (element in moduleElements) {
            val typeMirror = element.asType()
            if (typeUtil.isSubtype(typeMirror, moduleType.asType())) {
                Log.w("[Arch] Found module: $typeMirror")

                val activityCn = ClassName.get(element as TypeElement)
                /**
                 * Statement: moduleList.add(new AbsModule());
                 */
                loadModuleMethodBuilder.addStatement(
                    "\$N.add(new \$T())", ProcessorUtils.PARAM_NAME, activityCn
                )
            }
        }

        /**
         * Write to file
         */
        JavaFile.builder(
            ProcessorUtils.PACKAGE_NAME,
            TypeSpec.classBuilder("ModuleLoader\$$moduleName")
                .addJavadoc(ProcessorUtils.JAVADOC)
                .addSuperinterface(ClassName.get(ModuleLoader::class.java))
                .addModifiers(Modifier.PUBLIC)
                .addMethod(loadModuleMethodBuilder.build())
                .build()
        )
            .build()
            .writeTo(filer)

        return true
    }
}