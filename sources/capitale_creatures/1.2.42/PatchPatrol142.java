import java.nio.file.*;
import jdk.internal.org.objectweb.asm.*;
import jdk.internal.org.objectweb.asm.tree.*;

public class PatchPatrol142 implements Opcodes {
    static final String OWNER="fr/hautecapitale/creatures/spawn/CapitaleCreaturesOrcPatrol1212";
    static final String GUARD="fr/hautecapitale/creatures/spawn/CapitaleCreaturesPatrolCityGuard142";

    public static void main(String[] args) throws Exception {
        if(args.length!=2) throw new IllegalArgumentException("<input> <output>");
        ClassReader cr=new ClassReader(Files.readAllBytes(Path.of(args[0])));
        ClassNode cn=new ClassNode(); cr.accept(cn,0);
        int surface=0,tick=0;
        for(MethodNode m:cn.methods){
            if(m.name.equals("surfaceAt") && m.desc.equals("(Ljava/lang/Object;II)Lfr/hautecapitale/creatures/spawn/CapitaleCreaturesOrcPatrol1212$Surface;")){
                for(AbstractInsnNode n=m.instructions.getFirst();n!=null;n=n.getNext()){
                    if(n instanceof VarInsnNode v && v.getOpcode()==ASTORE && v.var==5){
                        AbstractInsnNode p=prevReal(n);
                        if(p instanceof MethodInsnNode mi && mi.owner.equals(OWNER) && mi.name.equals("biomeId")){
                            LabelNode ok=new LabelNode(); InsnList a=new InsnList();
                            a.add(new VarInsnNode(ALOAD,0));
                            a.add(new VarInsnNode(ILOAD,1));
                            a.add(new VarInsnNode(ILOAD,3));
                            a.add(new VarInsnNode(ILOAD,2));
                            a.add(new VarInsnNode(ALOAD,5));
                            a.add(new MethodInsnNode(INVOKESTATIC,GUARD,"isForbidden","(Ljava/lang/Object;IIILjava/lang/String;)Z",false));
                            a.add(new JumpInsnNode(IFEQ,ok));
                            a.add(new InsnNode(ACONST_NULL)); a.add(new InsnNode(ARETURN)); a.add(ok);
                            m.instructions.insert(n,a); surface++; break;
                        }
                    }
                }
            }
            if(m.name.equals("tickWorld") && m.desc.equals("(Ljava/lang/Object;)V")){
                for(AbstractInsnNode n=m.instructions.getFirst();n!=null;n=n.getNext()){
                    if(n instanceof MethodInsnNode mi && mi.owner.equals(OWNER) && mi.name.equals("purgePatrolsInExcludedBiomes")){
                        InsnList a=new InsnList();
                        a.add(new VarInsnNode(ALOAD,0));
                        a.add(new MethodInsnNode(INVOKESTATIC,GUARD,"purgeNearCities","(Ljava/lang/Object;)V",false));
                        m.instructions.insert(n,a); tick++; break;
                    }
                }
            }
        }
        if(surface!=1||tick!=1) throw new IllegalStateException("patch counts surface="+surface+" tick="+tick);
        ClassWriter cw=new ClassWriter(ClassWriter.COMPUTE_FRAMES|ClassWriter.COMPUTE_MAXS){
            @Override protected String getCommonSuperClass(String a,String b){return "java/lang/Object";}
        };
        cn.accept(cw); Path out=Path.of(args[1]); Files.createDirectories(out.getParent()); Files.write(out,cw.toByteArray());
    }
    static AbstractInsnNode prevReal(AbstractInsnNode n){for(AbstractInsnNode p=n.getPrevious();p!=null;p=p.getPrevious()) if(!(p instanceof LabelNode)&&!(p instanceof LineNumberNode)&&!(p instanceof FrameNode)) return p; return null;}
}
