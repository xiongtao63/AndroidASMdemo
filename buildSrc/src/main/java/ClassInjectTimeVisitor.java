import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class ClassInjectTimeVisitor extends ClassVisitor {
    //得到类名
    private String mClassName;
    public ClassInjectTimeVisitor(ClassVisitor cv,String fileName) {
        super(Opcodes.ASM5, cv);
        mClassName = fileName.substring(0,fileName.lastIndexOf("."));

    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
        return new MethodAdapterVisitor(mv,access, name, descriptor,mClassName);
    }
}
