package com.example.annotest.anno;


import static com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl.PACKAGE_NAME;

import com.google.auto.service.AutoService;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

//设置当前类要处理的注解类型
@SupportedAnnotationTypes({"com.example.annotest.anno.People"})
//设置当前类支持的Java版本
@SupportedSourceVersion(SourceVersion.RELEASE_8)
//声明当前类是一个注解处理器
@AutoService(Processor.class)
public class MyClass extends AbstractProcessor {
    Messager messager;
    Filer filer;
    Elements elementUtils ;
    final int TypePeople = 1;
    final int TypeTr069Get = 2;
    //被注解处理工具调用，并传入 ProcessingEnvironment 参数。ProcessingEnvironment提供很多有用的工具类，比如Elements、Types、Filer和Messager等
    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        //System.out.println(new RuntimeException().getStackTrace()[0].getMethodName());
        elementUtils = processingEnvironment.getElementUtils();
        System.out.println(elementUtils.toString());
        filer = processingEnvironment.getFiler();
        System.out.println(filer.toString());
        messager = processingEnvironment.getMessager();
        messager.printMessage(Diagnostic.Kind.NOTE, "init");
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        buildTr069ClassFactory(roundEnvironment, TypePeople, "PeopleAnno","PeopleMap");
        messager.printMessage(Diagnostic.Kind.NOTE, "process");
        return true;
    }

    private void buildTr069ClassFactory(RoundEnvironment roundEnvironment, int type, String className,String varName) {
        HashMap<String, MethodProperty> actionsMap = getStringMethodPropertyHashMap(roundEnvironment, type);
        messager.printMessage(Diagnostic.Kind.NOTE, actionsMap.size()+"");
        if (actionsMap.isEmpty()) {
            messager.printMessage(Diagnostic.Kind.NOTE, "FactoryMap isEmpty");
            return;
        }
        MethodFactoryCreater.create(varName,PACKAGE_NAME, className, actionsMap, filer);
        messager.printMessage(Diagnostic.Kind.NOTE, "MethodFactoryCreater.create");
    }

    private HashMap<String, MethodProperty> getStringMethodPropertyHashMap(RoundEnvironment roundEnvironment, int type) {
        HashMap<String, MethodProperty> actionsMap = new HashMap<>();
        Class<? extends Annotation> aClass = null;
        if (type == TypePeople) {
            aClass = People.class;
        }

        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(aClass);
        if (elements == null || elements.size() < 1) {
            return actionsMap;
        }

        for (Element typeElement : roundEnvironment.getElementsAnnotatedWith(aClass)) {
            String name = null;
            int age = 0;
            if (type == TypePeople) {
                People ann = (People) typeElement.getAnnotation(aClass);
                name = ann.name();
                age = ann.age();
                messager.printMessage(Diagnostic.Kind.NOTE, "name:"+name+",age:"+age);
            }
            if (name == null || name.length() < 1) {
                messager.printMessage(Diagnostic.Kind.NOTE, "name is null");
                return actionsMap;
            }
            String packageName = elementUtils.getPackageOf(typeElement).getQualifiedName().toString();
            String clazzName = typeElement.getEnclosingElement().getSimpleName().toString();
            String methodName = typeElement.getSimpleName().toString();
            messager.printMessage(Diagnostic.Kind.NOTE, "packageName:" + packageName + ",clazzName:" + clazzName + ",methodName=" + methodName);
            actionsMap.put(name, createProperty(packageName, clazzName, methodName));
            messager.printMessage(Diagnostic.Kind.NOTE, "actionsMap.put");
        }
        return actionsMap;
    }

    private MethodProperty createProperty(String packageName, String className, String methodName) {
        return new MethodProperty(packageName, className, methodName);
    }
}