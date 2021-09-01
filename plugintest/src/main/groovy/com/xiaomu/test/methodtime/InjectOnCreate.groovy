package com.xiaomu.test.methodtime


import javassist.ClassPool
import javassist.CtClass
import javassist.CtMethod
import org.gradle.api.Project


/**
 * 在统计activity 中所有方法耗时 耗时
 */
class InjectOnCreate {

    public static final ClassPool classPool = ClassPool.getDefault()


    static void injectOnCreateOnResume(String path, Project project) {
        classPool.appendClassPath(path)
        classPool.appendClassPath(project.android.bootClasspath[0].toString())
        classPool.importPackage('android.os.Bundle')
        File dir = new File(path)
        if (dir.isDirectory()) {
            dir.eachFileRecurse {
                def filePath = it.getAbsolutePath()
                def fileName = it.name
                if (!filePath.endsWith(".class")) {
                    return
                }
                if (filePath.endsWith('R$') || filePath.endsWith('R.class') || filePath.endsWith('BuildConfig.class')) {
                    return
                }
                def className = filePath.replace('.class','').replace("\\", ".")
                        .replace("/", ".")
                if (className.contains('com.xiaomu')) {
                    def packageName = className.substring(className.indexOf('com.xiaomu'), className.length())
                    CtClass ctClass =  classPool.getCtClass(packageName)
                    CtMethod [] ctMethods =  ctClass.getDeclaredMethods()
                    for(CtMethod method : ctMethods){
                        calculationMethodTime(ctClass,method,path)
                    }
                }

            }


        }
    }

    /**
     * 统计方法耗时
     * @param ctClass
     * @param method
     * @param path
     */
    static void calculationMethodTime(CtClass ctClass,CtMethod method,String path){
        if (ctClass.isFrozen()) {
            ctClass.defrost()
        }
        println('----------------insert---------------------------'+method.name)
        method.addLocalVariable("start",CtClass.longType)
        method.insertBefore("start = System.currentTimeMillis();")
        method.insertAfter("System.out.println(\""  + ctClass.name + "  " + method.name+ " exec time is :\"  + (System.currentTimeMillis() - start) + \"ms\");");
        ctClass.writeFile(path)
    }

}