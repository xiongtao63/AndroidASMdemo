import com.android.build.api.transform.DirectoryInput;
import com.android.build.api.transform.Format;
import com.android.build.api.transform.JarInput;
import com.android.build.api.transform.QualifiedContent;
import com.android.build.api.transform.Transform;
import com.android.build.api.transform.TransformException;
import com.android.build.api.transform.TransformInput;
import com.android.build.api.transform.TransformInvocation;
import com.android.build.api.transform.TransformOutputProvider;
import com.android.build.gradle.internal.pipeline.TransformManager;
import com.android.utils.FileUtils;
import com.google.common.collect.FluentIterable;

import org.gradle.internal.impldep.org.apache.commons.codec.binary.Hex;
//import org.gradle.internal.impldep.org.apache.commons.codec.digest.DigestUtils;
import org.gradle.internal.impldep.org.apache.commons.codec.digest.DigestUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Set;

public class ASMTransform extends Transform {
    @Override
    public String getName() {
        return "asm";
    }
    // 处理所有class
    @Override
    public Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS;
    }
    //范围仅仅是主项目所有的类
    @Override
    public Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.PROJECT_ONLY;
    }
    //不使用增量
    @Override
    public boolean isIncremental() {
        return false;
    }
    //android插件将所有的class通过这个方法告诉给我们
    @Override
    public void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        super.transform(transformInvocation);
        TransformOutputProvider outputProvider = transformInvocation.getOutputProvider();
        outputProvider.deleteAll();//清理上次缓存的文件信息
        //得到所有的输入
        Collection<TransformInput> inputs = transformInvocation.getInputs();
        for (TransformInput input : inputs) {
            // 处理class目录
            for (DirectoryInput directoryInput : input.getDirectoryInputs()) {
                // 直接复制输出到对应的目录
                String dirName  = directoryInput.getName();
                File src = directoryInput.getFile();
                System.out.println("输出class文件：" + src);
//                String md5Name = new String(Hex.encodeHex(DigestUtils.md5(src.getAbsolutePath())));
//                String md5Name = DigestUtils.md5Hex(src.getAbsolutePath());//签名
                //得到输出class文件的目录
                File dest = outputProvider.getContentLocation(dirName + "md5Name",
                        directoryInput.getContentTypes(),
                        directoryInput.getScopes(),
                        Format.DIRECTORY
                );
                //执行插桩操作

                processInject(src, dest);
            }
            // 处理jar（依赖）的class
            for (JarInput jarInput : input.getJarInputs()) {
                String  jarName  = jarInput.getName();
                File src = jarInput.getFile();
                System.out.println("输出jar包：" + src);
//                String md5Name = DigestUtils.md5Hex(src.getAbsolutePath());
//                String md5Name = new String(Hex.encodeHex(DigestUtils.md5(src.getAbsolutePath())));
                if(jarName.endsWith(".jar")){
                    jarName = jarName.substring(0,jarName.length()-4);
                }

                File dest = outputProvider.getContentLocation(jarName + "md5Name", jarInput.getContentTypes(), jarInput.getScopes(), Format.JAR);
                FileUtils.copyFile(src,dest);

            }
        }


    }

    private void processInject(File src, File dest) throws IOException{
        String dir = src.getAbsolutePath();
        System.out.println("=======dir=="+dir);
        FluentIterable<File> allFiles = FileUtils.getAllFiles(src);
        for (File file : allFiles) {
            //得到文件输入流
            FileInputStream fis = new FileInputStream(file);
            //得到字节码Reader
            ClassReader cr = new ClassReader(fis);
            //得到写出器
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
            //将注入的时间信息，写入
            cr.accept(new ClassInjectTimeVisitor(cw,file.getName()),ClassReader.EXPAND_FRAMES);
            byte[] newClassBytes  = cw.toByteArray();
            String absolutePath = file.getAbsolutePath();
            String fullClassPath = absolutePath.replace(dir, "");
            System.out.println("=======fullClassPath=="+fullClassPath);

            //将得到的字节码信息 写如输出目录
            File outFile = new File(dest,fullClassPath);
            FileUtils.mkdirs(outFile.getParentFile());
            FileOutputStream fos = new FileOutputStream(outFile);
            fos.write(newClassBytes);
            fos.close();
        }
    }
}
