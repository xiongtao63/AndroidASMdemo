import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AdviceAdapter;

public class MethodAdapterVisitor extends AdviceAdapter {
    private String mClassName;

    private String mMethodName;

    private boolean mInject;

    private int mStart, mEnd;
    public MethodAdapterVisitor(MethodVisitor mv, int access, String name, String descriptor, String className) {
        super(Opcodes.ASM5,mv, access, name, descriptor);
        mMethodName = name;
        mClassName = className;
    }

    @Override
    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        if ("Lcom/xiongtao/asmdemo/MSTimeAnalysis;".equals(descriptor)) {

            mInject = true;

        }
        return super.visitAnnotation(descriptor, visible);
    }

    @Override
    protected void onMethodEnter() {
        super.onMethodEnter();
        if (mInject) {

            //执行方法currentTimeMillis 得到startTime

            mv.visitMethodInsn(INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J", false);

            mStart = newLocal(Type.LONG_TYPE);

            mv.visitVarInsn(LSTORE, mStart);

        }
    }

    @Override
    protected void onMethodExit(int opcode) {
        super.onMethodExit(opcode);
        if (mInject) {

            //执行 currentTimeMillis 得到end time

            mv.visitMethodInsn(INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J", false);

            mEnd =newLocal(Type.LONG_TYPE);

            mv.visitVarInsn(LSTORE, mEnd);

            //得到静态成员 out

            mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");

            //new //class java/lang/StringBuilder

            mv.visitTypeInsn(NEW, "java/lang/StringBuilder");

            //引入类型 分配内存 并dup压入栈顶让下面的INVOKESPECIAL 知道执行谁的构造方法

            mv.visitInsn(DUP);

            //执行init方法 （构造方法）

            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false);

            //把常量压入栈顶

            mv.visitLdcInsn("execute "+ mMethodName +" :");

            //执行append方法，使用栈顶的值作为参数

            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);

            // 获得存储的本地变量

            mv.visitVarInsn(LLOAD, mEnd);

            mv.visitVarInsn(LLOAD, mStart);

            // lsub 减法指令

            mv.visitInsn(LSUB);

            //把减法结果append

            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(J)Ljava/lang/StringBuilder;", false);

            //拼接常量

            mv.visitLdcInsn(" ms.");

            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);

            //执行StringBuilder 的toString方法

            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);

            //执行println方法

            mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);

        }
    }
}
