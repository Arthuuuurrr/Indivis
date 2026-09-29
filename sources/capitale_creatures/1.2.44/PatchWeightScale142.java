import java.nio.file.*;
import jdk.internal.org.objectweb.asm.*;
import jdk.internal.org.objectweb.asm.tree.*;

public class PatchWeightScale142 implements Opcodes {
    static final String HELPER="fr/hautecapitale/creatures/spawn/CapitaleCreaturesWeightScale142";
    public static void main(String[] args) throws Exception {
        if(args.length!=2) throw new IllegalArgumentException("<input> <output>");
        ClassReader cr=new ClassReader(Files.readAllBytes(Path.of(args[0])));
        ClassNode cn=new ClassNode(); cr.accept(cn,0);
        int patched=0;
        for(MethodNode m:cn.methods){
            if(!m.name.equals("effectiveWeight") || !m.desc.equals("(Ljava/util/Map;Ljava/util/List;I)I")) continue;
            for(AbstractInsnNode n=m.instructions.getFirst();n!=null;){
                AbstractInsnNode next=n.getNext();
                if(n.getOpcode()==IRETURN){
                    InsnList a=new InsnList();
                    a.add(new VarInsnNode(ALOAD,0));
                    a.add(new MethodInsnNode(INVOKESTATIC,HELPER,"scale","(ILjava/util/Map;)I",false));
                    m.instructions.insertBefore(n,a); patched++;
                }
                n=next;
            }
        }
        if(patched!=2) throw new IllegalStateException("expected 2 returns, patched="+patched);
        ClassWriter cw=new ClassWriter(ClassWriter.COMPUTE_FRAMES|ClassWriter.COMPUTE_MAXS){
            @Override protected String getCommonSuperClass(String a,String b){return "java/lang/Object";}
        };
        cn.accept(cw); Path out=Path.of(args[1]); Files.createDirectories(out.getParent()); Files.write(out,cw.toByteArray());
    }
}
