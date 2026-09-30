import java.nio.file.*;
import jdk.internal.org.objectweb.asm.*;
import jdk.internal.org.objectweb.asm.tree.*;

public final class PatchBeta10Pre2 implements Opcodes {
    static final int API=ASM8;
    static byte[] patchPreview(byte[] in){
        ClassNode cn=new ClassNode(API); new ClassReader(in).accept(cn,0); int changed=0;
        for(MethodNode m:cn.methods){
            if(!m.name.equals("drawWorldless")) continue;
            for(AbstractInsnNode n=m.instructions.getFirst();n!=null;n=n.getNext()){
                if(n instanceof MethodInsnNode mi && mi.getOpcode()==INVOKESTATIC
                    && mi.owner.equals("net/tompsen/nexuscharacters/WorldlessBeardWidgetRenderer")
                    && mi.name.equals("render")
                    && mi.desc.equals("(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;IIIIIIF)V")){
                    mi.owner="net/tompsen/nexuscharacters/BeardPreviewVisibilitySupport";
                    mi.name="render"; mi.itf=false; changed++;
                }
            }
        }
        if(changed!=1) throw new IllegalStateException("beard preview call patch count="+changed);
        ClassWriter cw=new ClassWriter(ClassWriter.COMPUTE_MAXS); cn.accept(cw); return cw.toByteArray();
    }
    public static void main(String[] args)throws Exception{
        Path root=Paths.get(args[0]);
        Path p=root.resolve("net/tompsen/nexuscharacters/CharacterPreviewRenderer.class");
        Files.write(p,patchPreview(Files.readAllBytes(p)));
    }
}
