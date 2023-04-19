package com.example.annotest.anno;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.HashMap;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;

import sun.rmi.runtime.Log;

public class MethodFactoryCreater {
    public static void create(String mapName,String packageName, String simpleName, HashMap<String, MethodProperty> actionsMap, Filer mFiler) {
        ClassName contextClazz = ClassName.get("android.content", "Context");
        ClassName cArrayMap = ClassName.get("android.util", "ArrayMap");
        ClassName cMethodProperty = ClassName.get("com.example.annotest.anno", "MethodProperty");
        ClassName cLog = ClassName.get("com.skyworthdigital.stb.log", "LogUtils");
        ClassName cMethod = ClassName.get("java.lang.reflect", "Method");

        TypeName mapFieldType = ParameterizedTypeName.get(cArrayMap, TypeName.get(String.class), cMethodProperty);
        FieldSpec mapField = FieldSpec.builder(mapFieldType, mapName)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .build();

        MethodSpec.Builder constructorBuilder =
                MethodSpec.constructorBuilder()
                        .addModifiers(Modifier.PUBLIC)
                        .addStatement("this.$N = new $T<$T,$T>()", mapName, cArrayMap, String.class, cMethodProperty);

        for (String key : actionsMap.keySet()) {
            MethodProperty property = actionsMap.get(key);
            ClassName className = ClassName.get(property.mPackageName, property.mClassName);
            constructorBuilder.addStatement("$N.put($S,new $T($S,$S,$S))", mapName, key, cMethodProperty, property.mPackageName, property.mClassName, property.mMethodName);
        }
        MethodSpec constructor = constructorBuilder.build();

//        ClassName cTextUtils = ClassName.get("android.text", "TextUtils");
//        MethodSpec getString = MethodSpec.methodBuilder("getString")
//                .addModifiers(Modifier.PUBLIC)
//                .returns(String.class)
//                .addParameter(String.class, "path")
//                .addStatement("if ($T.isEmpty(path)) return null", cTextUtils)
//                .addStatement("MethodProperty property=mTr069MethodMap.get(path)")
//                .addStatement("if (property==null)return null")
//                .beginControlFlow("try")
//                .addStatement("Class clz = Class.forName(property.mPackageName+\".\"+property.mClassName)")
//                .addStatement("Object obj = clz.newInstance()")
//                .addStatement("$T m = obj.getClass().getDeclaredMethod(property.mMethodName)", cMethod)
//                .addStatement("return (String) m.invoke(obj)")
//                .nextControlFlow("catch ($T e)", Exception.class)
//                .addStatement("e.printStackTrace()")
//                .endControlFlow()
//                .addStatement("return null")
//                .build();

        TypeSpec mainClass = TypeSpec.classBuilder(simpleName)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addJavadoc("Tis file is automatically generated with comments,not be modified\n")
                .addField(mapField)
                .addMethod(constructor)
                .build();
        JavaFile javaFile = JavaFile.builder(packageName, mainClass).build();

        try {
            javaFile.writeTo(mFiler);
        } catch (IOException e) {
            System.out.println(e.toString());
        }
    }
}
