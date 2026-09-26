import java.nio.file.*;
import jdk.internal.org.objectweb.asm.*;
import jdk.internal.org.objectweb.asm.tree.*;

public class PatchOrcPatrolSurface138 implements Opcodes {
    public static void main(String[] args) throws Exception {
        if(args.length!=2) throw new IllegalArgumentException("<input class> <output class>");
        byte[] in=Files.readAllBytes(Path.of(args[0]));
        ClassReader cr=new ClassReader(in); ClassNode cn=new ClassNode(); cr.accept(cn,0);
        int patched=0;
        for(MethodNode m:cn.methods){
            if(!m.name.equals("surfaceAt") || !m.desc.startsWith("(Ljava/lang/Object;II)")) continue;
            for(AbstractInsnNode n=m.instructions.getFirst();n!=null;n=n.getNext()){
                if(n instanceof VarInsnNode v && v.getOpcode()==ASTORE && v.var==4){
                    AbstractInsnNode prev=previousReal(n);
                    if(!(prev instanceof MethodInsnNode mi) || !mi.owner.equals("java/lang/reflect/Constructor") || !mi.name.equals("newInstance")) continue;
                    LabelNode ok=new LabelNode();
                    InsnList add=new InsnList();
                    add.add(new VarInsnNode(ALOAD,0));
                    add.add(new VarInsnNode(ALOAD,4));
                    add.add(new MethodInsnNode(INVOKESTATIC,
                        "fr/hautecapitale/creatures/spawn/CapitaleCreaturesSpawnSafety138",
                        "validGroundAt","(Ljava/lang/Object;Ljava/lang/Object;)Z",false));
                    add.add(new JumpInsnNode(IFNE,ok));
                    add.add(new InsnNode(ACONST_NULL));
                    add.add(new InsnNode(ARETURN));
                    add.add(ok);
                    m.instructions.insert(n,add); patched++;
                    break;
                }
            }
        }
        if(patched!=1) throw new IllegalStateException("Expected 1 surfaceAt patch, got "+patched);
        ClassWriter cw=new ClassWriter(ClassWriter.COMPUTE_FRAMES|ClassWriter.COMPUTE_MAXS){
            @Override protected String getCommonSuperClass(String a,String b){return "java/lang/Object";}
        };
        cn.accept(cw); Path out=Path.of(args[1]); Files.createDirectories(out.getParent()); Files.write(out,cw.toByteArray());
    }
    static AbstractInsnNode previousReal(AbstractInsnNode n){
        for(AbstractInsnNode p=n.getPrevious();p!=null;p=p.getPrevious())
            if(!(p instanceof LabelNode)&&!(p instanceof LineNumberNode)&&!(p instanceof FrameNode)) return p;
        return null;
    }
}
