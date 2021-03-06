package com.xiaomu.test

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.utils.FileUtils
import com.xiaomu.test.methodtime.InjectMethod
import com.xiaomu.test.tryexception.TryInject
import org.apache.commons.codec.digest.DigestUtils
import org.gradle.api.Project

class InjectTransform extends Transform {

    private Project mProject

    InjectTransform(Project project) {
        this.mProject = project
    }

    // 自定义的Transform对应的Task名称
    @Override
    String getName() {
        return "InjectTransform"
    }

    // 指定输入的类型,指定我们要处理的文件类型
    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    // 指定Transform的作用范围
    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    //是否支持增量编译
    @Override
    boolean isIncremental() {
        return false
    }

    // 核心方法
    // inputs是传过来的输入流，有两种格式：jar和目录格式
    // outputProvider 获取输出目录，将修改的文件复制到输出目录，必须执行

    /**
     @Override
      void transform(Context context, Collection<TransformInput> inputs, Collection<TransformInput> referencedInputs, TransformOutputProvider outputProvider, boolean isIncremental) throws IOException, TransformException, InterruptedException {super.transform(context, inputs, referencedInputs, outputProvider, isIncremental)
      // Transform的inputs有两种类型，一种是目录，一种是jar包，要分开遍历
      inputs.each {TransformInput input ->
      // 遍历文件夹
      //文件夹里面包含的是我们手写的类以及R.class、BuildConfig.class以及R$XXX.class等
      input.directoryInputs.each {DirectoryInput directoryInput ->
      // 注入代码
      //                        MyInjectByJavassit.injectToast(directoryInput.file.absolutePath, mProject)
      InjectMethod.injectMethod(directoryInput.file.absolutePath, mProject)

      // 获取输出目录
      def dest = outputProvider.getContentLocation(directoryInput.name,
      directoryInput.contentTypes, directoryInput.scopes, Format.DIRECTORY)

      //                        println("directory output dest: $dest.absolutePath")
      // 将input的目录复制到output指定目录
      FileUtils.copyDirectory(directoryInput.file, dest)}//对类型为jar文件的input进行遍历
      input.jarInputs.each {//jar文件一般是第三方依赖库jar文件
      JarInput jarInput ->
      // 重命名输出文件（同目录copyFile会冲突）
      def jarName = jarInput.name
      def md5Name = DigestUtils.md5Hex(jarInput.file.absolutePath)
      if (jarName.endsWith('.jar')) {jarName = jarName.substring(0, jarName.length() - 4)}def dest = outputProvider.getContentLocation(jarName + md5Name, jarInput.contentTypes, jarInput.scopes, Format.JAR)
      FileUtils.copyFile(jarInput.file, dest)}}}*  */

    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        super.transform(transformInvocation)

        transformInvocation.inputs.forEach{
            it.directoryInputs.forEach{
//                InjectMethod.injectMethod(it.file.absolutePath, mProject)
                TryInject.injectMethod(it.file.absolutePath,mProject)
                def dest = transformInvocation.outputProvider.getContentLocation(it.name,
                        it.contentTypes, it.scopes, Format.DIRECTORY)
                FileUtils.copyDirectory(it.file, dest)
            }
            it.jarInputs.forEach{
                def jarName = it.name
                def md5Name = DigestUtils.md5Hex(it.file.absolutePath)
                if (jarName.endsWith('.jar')) {
                    jarName = jarName.substring(0, jarName.length() - 4)
                }
                def dest = transformInvocation.outputProvider.getContentLocation(jarName + md5Name, it.contentTypes, it.scopes, Format.JAR)
                FileUtils.copyFile(it.file, dest)
            }
        }
    }
}