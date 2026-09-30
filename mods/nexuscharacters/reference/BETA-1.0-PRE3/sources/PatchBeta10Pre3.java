import java.nio.file.*;
import jdk.internal.org.objectweb.asm.*;
import jdk.internal.org.objectweb.asm.tree.*;

public final class PatchBeta10Pre3 implements Opcodes {
    static final int API=ASM8;
    static final String APPEAR="net/tompsen/nexuscharacters/mixin/client/CharacterCreationAppearance65Mixin";

    static void clear(MethodNode m){m.instructions.clear();m.tryCatchBlocks.clear();if(m.localVariables!=null)m.localVariables.clear();}

    static byte[] patchAppearance(byte[] in){
        ClassNode cn=new ClassNode(API);new ClassReader(in).accept(cn,0);
        int fix=0,cycle=0,color=0;
        for(MethodNode m:cn.methods){
            if(m.name.equals("nexuscharacters$finalResponsiveLayout")){
                for(AbstractInsnNode n=m.instructions.getFirst();n!=null;n=n.getNext()){
                    if(n instanceof MethodInsnNode mi && mi.getOpcode()==INVOKESTATIC
                            && mi.owner.equals("net/tompsen/nexuscharacters/NordicBeardUiSupport") && mi.name.equals("fix")){
                        mi.owner="net/tompsen/nexuscharacters/UnifiedPilositySupport"; mi.name="fix"; mi.itf=false; fix++;
                    }
                }
            } else if(m.name.equals("lambda$nexuscharacters$install$8") && m.desc.equals("(Lnet/minecraft/class_4185;)V")){
                clear(m); InsnList x=m.instructions;
                x.add(new VarInsnNode(ALOAD,0));
                x.add(new MethodInsnNode(INVOKESTATIC,"net/tompsen/nexuscharacters/UnifiedPilositySupport","cycle","(Ljava/lang/Object;)V",false));
                x.add(new VarInsnNode(ALOAD,0));
                x.add(new MethodInsnNode(INVOKEVIRTUAL,APPEAR,"nexuscharacters$refresh","()V",false));
                x.add(new InsnNode(RETURN)); m.maxLocals=2;m.maxStack=1;cycle++;
            } else if(m.name.equals("lambda$nexuscharacters$install$9") && m.desc.equals("(Lnet/minecraft/class_4185;)V")){
                clear(m); InsnList x=m.instructions;
                x.add(new VarInsnNode(ALOAD,0)); x.add(new VarInsnNode(ALOAD,0));
                x.add(new FieldInsnNode(GETFIELD,APPEAR,"nexuscharacters$facialColor","I"));
                x.add(new MethodInsnNode(INVOKESTATIC,"net/tompsen/nexuscharacters/Appearance69Support","nextFacialColor","(I)I",false));
                x.add(new FieldInsnNode(PUTFIELD,APPEAR,"nexuscharacters$facialColor","I"));
                x.add(new VarInsnNode(ALOAD,0));
                x.add(new MethodInsnNode(INVOKESTATIC,"net/tompsen/nexuscharacters/UnifiedPilositySupport","syncColor","(Ljava/lang/Object;)V",false));
                x.add(new VarInsnNode(ALOAD,0));
                x.add(new MethodInsnNode(INVOKEVIRTUAL,APPEAR,"nexuscharacters$refresh","()V",false));
                x.add(new InsnNode(RETURN));m.maxLocals=2;m.maxStack=2;color++;
            }
        }
        if(fix!=1||cycle!=1||color!=1)throw new IllegalStateException("appearance patch mismatch fix="+fix+" cycle="+cycle+" color="+color);
        ClassWriter cw=new ClassWriter(ClassWriter.COMPUTE_MAXS);cn.accept(cw);return cw.toByteArray();
    }

    static byte[] patchCosmeticMixin(byte[] in){
        ClassNode cn=new ClassNode(API);new ClassReader(in).accept(cn,0);
        int defaultNone=0,sync=0;
        for(MethodNode m:cn.methods){
            if(m.name.equals("nexuscharacters$addCosmeticButton")){
                for(AbstractInsnNode n=m.instructions.getFirst();n!=null;n=n.getNext()){
                    if(n instanceof FieldInsnNode fi && fi.getOpcode()==GETSTATIC
                            && fi.owner.equals("net/tompsen/nexuscharacters/CharacterCosmetics$BeardStyle") && fi.name.equals("FULL")){
                        fi.name="NONE";defaultNone++;break;
                    }
                }
            } else if(m.name.equals("nexuscharacters$decorate")){
                InsnList head=new InsnList();
                head.add(new VarInsnNode(ALOAD,0));
                head.add(new MethodInsnNode(INVOKESTATIC,"net/tompsen/nexuscharacters/UnifiedPilositySupport","syncColor","(Ljava/lang/Object;)V",false));
                m.instructions.insert(head);sync++;
            }
        }
        if(defaultNone!=1||sync!=1)throw new IllegalStateException("cosmetic mixin mismatch default="+defaultNone+" sync="+sync);
        ClassWriter cw=new ClassWriter(ClassWriter.COMPUTE_MAXS);cn.accept(cw);return cw.toByteArray();
    }

    static byte[] patchColorSupport(byte[] in){
        ClassNode cn=new ClassNode(API);new ClassReader(in).accept(cn,0);int attach=0,refresh=0;
        for(MethodNode m:cn.methods){
            if(m.name.equals("attachColorButton")&&m.desc.equals("(Ljava/lang/Object;)V")){
                clear(m);m.instructions.add(new InsnNode(RETURN));m.maxLocals=1;m.maxStack=0;attach++;
            } else if(m.name.equals("refreshColorButton")&&m.desc.equals("(Ljava/lang/Object;)V")){
                clear(m);m.instructions.add(new InsnNode(RETURN));m.maxLocals=1;m.maxStack=0;refresh++;
            }
        }
        if(attach!=1||refresh!=1)throw new IllegalStateException("color support mismatch attach="+attach+" refresh="+refresh);
        ClassWriter cw=new ClassWriter(ClassWriter.COMPUTE_MAXS);cn.accept(cw);return cw.toByteArray();
    }

    static void patch(Path root,String rel,java.util.function.Function<byte[],byte[]> fn)throws Exception{
        Path p=root.resolve(rel);Files.write(p,fn.apply(Files.readAllBytes(p)));
    }
    public static void main(String[] a)throws Exception{
        Path root=Paths.get(a[0]);
        patch(root,"net/tompsen/nexuscharacters/mixin/client/CharacterCreationAppearance65Mixin.class",PatchBeta10Pre3::patchAppearance);
        patch(root,"net/tompsen/nexuscharacters/mixin/client/CharacterCreationCosmeticMixin.class",PatchBeta10Pre3::patchCosmeticMixin);
        patch(root,"net/tompsen/nexuscharacters/CosmeticColorSupport.class",PatchBeta10Pre3::patchColorSupport);
    }
}
