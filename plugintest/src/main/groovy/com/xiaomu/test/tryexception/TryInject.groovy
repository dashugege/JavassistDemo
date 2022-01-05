package com.xiaomu.test.tryexception

import javassist.ClassPool
import javassist.CtClass
import javassist.CtMethod
import javassist.LoaderClassPath
import javassist.bytecode.AnnotationsAttribute
import javassist.bytecode.MethodInfo
import javassist.bytecode.annotation.ArrayMemberValue
import javassist.bytecode.annotation.ClassMemberValue
import org.gradle.api.Project

import java.lang.annotation.Annotation

//https://github.com/feelschaotic/AopAutoTryCatch/blob/master/buildsrc/src/main/groovy/com/feelschaotic/plugin/TryCatchInject.groovy
class TryInject {

    public static final ClassPool classPool = ClassPool.getDefault()

    static void injectMethod(String path, Project project) {
        def packagename =  project.configExtension.packageName
        classPool.appendClassPath(path)
        classPool.appendClassPath(project.android.bootClasspath[0].toString())
        classPool.importPackage('android.os.Bundle')

        File dir = new File(path)

        if (dir.isDirectory()) {
            println("dir "+ dir)
            dir.eachFileRecurse {
                def filePath = it.getAbsolutePath()
                def fileName = it.name
                if (!filePath.endsWith(".class")) {
                    return
                }
                if (filePath.endsWith('R$') || filePath.endsWith('R.class') || filePath.endsWith('BuildConfig.class')) {
                    return
                }
//                println("filePath "+ filePath)
                def className = filePath.replace('.class', '').replace("\\", ".")
                        .replace("/", ".")
//                println("className "+ className)
//                println("packagename "+ packagename)
//                println("contains "+ className.contains(packagename))

                if (className.contains(packagename)) {
                    def packageName = className.substring(className.indexOf(packagename), className.length())
                    CtClass ctClass = classPool.getCtClass(packageName)
                    if (ctClass.isFrozen()) {
                        ctClass.defrost()
                    }
                    CtMethod[] ctMethods = ctClass.getDeclaredMethods()
                    for (CtMethod method : ctMethods) {
                        Annotation[] annotations = method.getAvailableAnnotations()
                        for (Annotation annotation : annotations) {
                            String name = annotation.annotationType().canonicalName
                            if ("com.xiaomu.test.JTryCatch".equals(name)) {
                                MethodInfo methodInfo = method.getMethodInfo()
                                println("methodInfo " + methodInfo)
                                AnnotationsAttribute attribute = methodInfo.getAttribute(AnnotationsAttribute.visibleTag)
                                println("attribute " + attribute)
                                javassist.bytecode.annotation.Annotation annotation1 = attribute.getAnnotation(name)
                                println("annotation1 " + annotation1)
                                Set<String> names = annotation1.getMemberNames()
                                println("names " + names.toString())
                                if (names == null || names.isEmpty()) {
                                    CtClass exceptionType = classPool.get("java.lang.Exception")
                                    method.addCatch('{com.xiaomu.test.PrintException.printException($e); return;}',exceptionType)
                                    ctClass.writeFile(path)
                                    println("methond 1 " + method)
                                    return
                                }

                                names.forEach{
                                    ArrayMemberValue arrayMemberValues = (ArrayMemberValue) annotation1.getMemberValue(it)
                                    if(arrayMemberValues != null){
                                        (ClassMemberValue)arrayMemberValues.getValue().each {
                                            CtClass exceptionType =  classPool.get(((ClassMemberValue)it).value)
                                            method.addCatch('{com.xiaomu.test.PrintException.printException($e);return;}',exceptionType)
                                            println("methond 2 " + method)

                                        }
                                    }
                                }

                            }
                        }
                    }

                }


            }

        }


    }


}