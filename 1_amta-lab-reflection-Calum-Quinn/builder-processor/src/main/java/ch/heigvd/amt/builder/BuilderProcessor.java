package ch.heigvd.amt.builder;

import com.squareup.javapoet.*;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Set;

/**
 * This class is an annotation processor that generates a builder class for each class annotated with @GenerateBuilder.
 */
@SupportedAnnotationTypes("ch.heigvd.amt.builder.GenerateBuilder")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class BuilderProcessor extends AbstractProcessor {

    private Elements elementUtils;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        elementUtils = processingEnv.getElementUtils();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getElementsAnnotatedWith(GenerateBuilder.class)) {
            TypeElement typeElement = (TypeElement) element;
            String className = typeElement.getSimpleName() + "Builder";
            String packageName = processingEnv.getElementUtils().getPackageOf(typeElement).getQualifiedName().toString();

            // Start building the builder class
            TypeSpec.Builder builderClass = TypeSpec.classBuilder(className)
                    .addModifiers(Modifier.PUBLIC)
                    .addJavadoc("Generated Builder for $L", typeElement.getSimpleName());

            // Add fields and setter methods for each field in the original class
            for (VariableElement field : ElementFilter.fieldsIn(typeElement.getEnclosedElements())) {
                String fieldName = field.getSimpleName().toString();
                TypeName fieldType = TypeName.get(field.asType());

                // Add private field
                builderClass.addField(fieldType, fieldName, Modifier.PRIVATE);

                // Create setter method
                MethodSpec setter = MethodSpec.methodBuilder("set" + capitalize(fieldName))
                        .addModifiers(Modifier.PUBLIC)
                        .returns(ClassName.bestGuess(className))
                        .addParameter(fieldType, fieldName)
                        .addStatement("this.$L = $L", fieldName, fieldName)
                        .addStatement("return this") // return this for chaining
                        .build(); // Build the MethodSpec here

                builderClass.addMethod(setter); // Add the method to the builder class
            }

            // Add the build method
            MethodSpec.Builder buildMethodBuilder = MethodSpec.methodBuilder("build")
                    .addModifiers(Modifier.PUBLIC)
                    .returns(ClassName.bestGuess(typeElement.getQualifiedName().toString()))
                    .addStatement("$T result = new $T()", typeElement.asType(), typeElement.asType())
                    .addCode("try {\n");

            for (VariableElement field : ElementFilter.fieldsIn(typeElement.getEnclosedElements())) {
                String fieldName = field.getSimpleName().toString();
                buildMethodBuilder.addCode("$T $LField = result.getClass().getDeclaredField($S);\n", Field.class, fieldName, fieldName);
                buildMethodBuilder.addCode("$LField.setAccessible(true);\n", fieldName);
                buildMethodBuilder.addCode("$LField.set(result, this.$L);\n", fieldName, fieldName);
            }

            buildMethodBuilder.addCode("} catch (Exception e) {\n");
            buildMethodBuilder.addCode("e.printStackTrace();\n");
            buildMethodBuilder.addCode("}\n");
            buildMethodBuilder.addStatement("return result");

            // Build the complete build method
            builderClass.addMethod(buildMethodBuilder.build());

            // Create the Java file
            JavaFile javaFile = JavaFile.builder(packageName, builderClass.build()).build();

            // Write the generated Java file to the specified location
            try {
                javaFile.writeTo(processingEnv.getFiler());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }


    private String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

}
