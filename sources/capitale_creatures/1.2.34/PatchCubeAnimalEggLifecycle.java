import java.nio.file.*;
import jdk.internal.org.objectweb.asm.*;
import jdk.internal.org.objectweb.asm.tree.*;

public class PatchCubeAnimalEggLifecycle implements Opcodes {
    static final String BLOCK_STATE="net/minecraft/class_2680";
    static final String BOOL_PROP="net/minecraft/class_2746";
    static final String PROP="net/minecraft/class_2769";
    static final String BUILDER="net/minecraft/class_2689$class_2690";
    static final String SERVER_WORLD="net/minecraft/class_3218";
    static final String BLOCK_POS="net/minecraft/class_2338";
    static final String BLOCK="net/minecraft/class_2248";
    static final String BLOCKS="net/minecraft/class_2246";
    static final String MOD_BLOCKS="net/suprk/ufauna/block/ModBlocks";\n    static final String SETTINGS="net/minecraft/class_4970$class_2251";

    static AbstractInsnNode nextReal(AbstractInsnNode n){
        for(AbstractInsnNode x=n.getNext();x!=null;x=x.getNext())
            if(!(x instanceof LabelNode)&&!(x instanceof LineNumberNode)&&!(x instanceof FrameNode)) return x;
        return null;
    }
    static AbstractInsnNode prevReal(AbstractInsnNode n){
        for(AbstractInsnNode x=n.getPrevious();x!=null;x=x.getPrevious())
            if(!(x instanceof LabelNode)&&!(x instanceof LineNumberNode)&&!(x instanceof FrameNode)) return x;
        return null;
    }

    static void addPlayerProperty(ClassNode cn){
        String owner=cn.name;
        cn.fields.add(new FieldNode(ACC_PUBLIC|ACC_STATIC|ACC_FINAL,"PLACED_BY_PLAYER","L"+BOOL_PROP+";",null,null));
        MethodNode clinit=null;
        for(MethodNode m:cn.methods) if(m.name.equals("<clinit>")) clinit=m;
        if(clinit==null) throw new IllegalStateException(owner+": no <clinit>");
        InsnList si=new InsnList();
        si.add(new LdcInsnNode("placed_by_player"));
        si.add(new MethodInsnNode(INVOKESTATIC,BOOL_PROP,"method_11825","(Ljava/lang/String;)L"+BOOL_PROP+";",false));
        si.add(new FieldInsnNode(PUTSTATIC,owner,"PLACED_BY_PLAYER","L"+BOOL_PROP+";"));
        clinit.instructions.insert(si);

        int ctor=0, props=0, placement=0, tick=0;
        for(MethodNode m:cn.methods){
            if(m.name.equals("<init>")){
                for(AbstractInsnNode n=m.instructions.getFirst();n!=null;n=n.getNext()){
                    if(n instanceof MethodInsnNode mi && mi.getOpcode()==INVOKEVIRTUAL && mi.owner.equals(owner) && mi.name.equals("method_9590")){
                        InsnList add=new InsnList();
                        add.add(new FieldInsnNode(GETSTATIC,owner,"PLACED_BY_PLAYER","L"+BOOL_PROP+";"));
                        add.add(new InsnNode(ICONST_0));
                        add.add(new MethodInsnNode(INVOKESTATIC,"java/lang/Boolean","valueOf","(Z)Ljava/lang/Boolean;",false));
                        add.add(new MethodInsnNode(INVOKEVIRTUAL,BLOCK_STATE,"method_11657","(L"+PROP+";Ljava/lang/Comparable;)Ljava/lang/Object;",false));
                        add.add(new TypeInsnNode(CHECKCAST,BLOCK_STATE));
                        m.instructions.insertBefore(n,add);
                        ctor++;
                        break;
                    }
                }
            }
            if(m.name.equals("method_9515")){
                for(AbstractInsnNode n=m.instructions.getFirst();n!=null;n=n.getNext()){
                    if(n.getOpcode()==RETURN){
                        InsnList add=new InsnList();
                        add.add(new VarInsnNode(ALOAD,1));
                        add.add(new InsnNode(ICONST_1));
                        add.add(new TypeInsnNode(ANEWARRAY,PROP));
                        add.add(new InsnNode(DUP));
                        add.add(new InsnNode(ICONST_0));
                        add.add(new FieldInsnNode(GETSTATIC,owner,"PLACED_BY_PLAYER","L"+BOOL_PROP+";"));
                        add.add(new InsnNode(AASTORE));
                        add.add(new MethodInsnNode(INVOKEVIRTUAL,BUILDER,"method_11667","([L"+PROP+";)L"+BUILDER+";",false));
                        add.add(new InsnNode(POP));
                        m.instructions.insertBefore(n,add);
                        props++;
                        break;
                    }
                }
            }
            if(m.name.equals("method_9605")){
                for(AbstractInsnNode n=m.instructions.getFirst();n!=null;n=n.getNext()){
                    if(n.getOpcode()==ARETURN){
                        AbstractInsnNode p=prevReal(n);
                        if(p!=null && p.getOpcode()==ACONST_NULL) continue;
                        InsnList add=new InsnList();
                        add.add(new FieldInsnNode(GETSTATIC,owner,"PLACED_BY_PLAYER","L"+BOOL_PROP+";"));
                        add.add(new InsnNode(ICONST_1));
                        add.add(new MethodInsnNode(INVOKESTATIC,"java/lang/Boolean","valueOf","(Z)Ljava/lang/Boolean;",false));
                        add.add(new MethodInsnNode(INVOKEVIRTUAL,BLOCK_STATE,"method_11657","(L"+PROP+";Ljava/lang/Comparable;)Ljava/lang/Object;",false));
                        add.add(new TypeInsnNode(CHECKCAST,BLOCK_STATE));
                        m.instructions.insertBefore(n,add);
                        placement++;
                    }
                }
            }
            if(m.name.equals("method_9588") && m.desc.equals("(Lnet/minecraft/class_2680;Lnet/minecraft/class_3218;Lnet/minecraft/class_2338;Lnet/minecraft/class_5819;)V")){
                LabelNode keep=new LabelNode();
                InsnList pre=new InsnList();
                pre.add(new VarInsnNode(ALOAD,1));
                pre.add(new FieldInsnNode(GETSTATIC,owner,"PLACED_BY_PLAYER","L"+BOOL_PROP+";"));
                pre.add(new MethodInsnNode(INVOKEVIRTUAL,BLOCK_STATE,"method_11654","(L"+PROP+";)Ljava/lang/Comparable;",false));
                pre.add(new TypeInsnNode(CHECKCAST,"java/lang/Boolean"));
                pre.add(new MethodInsnNode(INVOKEVIRTUAL,"java/lang/Boolean","booleanValue","()Z",false));
                pre.add(new JumpInsnNode(IFNE,keep));
                pre.add(new VarInsnNode(ALOAD,2));
                pre.add(new VarInsnNode(ALOAD,3));
                pre.add(new FieldInsnNode(GETSTATIC,BLOCKS,"field_10124","L"+BLOCK+";"));
                pre.add(new MethodInsnNode(INVOKEVIRTUAL,BLOCK,"method_9564","()L"+BLOCK_STATE+";",false));
                pre.add(new InsnNode(ICONST_3));
                pre.add(new MethodInsnNode(INVOKEVIRTUAL,SERVER_WORLD,"method_8652","(L"+BLOCK_POS+";L"+BLOCK_STATE+";I)Z",false));
                pre.add(new InsnNode(POP));
                pre.add(new InsnNode(RETURN));
                pre.add(keep);
                pre.add(new FrameNode(F_SAME,0,null,0,null));
                m.instructions.insert(pre);
                tick++;
            }
        }
        // Migration path for legacy eggs: random ticks only schedule cleanup for natural/legacy states.
        MethodNode randomTick = new MethodNode(ACC_PROTECTED, "method_9514",
                "(Lnet/minecraft/class_2680;Lnet/minecraft/class_3218;Lnet/minecraft/class_2338;Lnet/minecraft/class_5819;)V", null, null);
        LabelNode ret = new LabelNode();
        randomTick.instructions.add(new VarInsnNode(ALOAD,1));
        randomTick.instructions.add(new FieldInsnNode(GETSTATIC,owner,"PLACED_BY_PLAYER","L"+BOOL_PROP+";"));
        randomTick.instructions.add(new MethodInsnNode(INVOKEVIRTUAL,BLOCK_STATE,"method_11654","(L"+PROP+";)Ljava/lang/Comparable;",false));
        randomTick.instructions.add(new TypeInsnNode(CHECKCAST,"java/lang/Boolean"));
        randomTick.instructions.add(new MethodInsnNode(INVOKEVIRTUAL,"java/lang/Boolean","booleanValue","()Z",false));
        randomTick.instructions.add(new JumpInsnNode(IFNE,ret));
        randomTick.instructions.add(new VarInsnNode(ALOAD,2));
        randomTick.instructions.add(new VarInsnNode(ALOAD,3));
        randomTick.instructions.add(new VarInsnNode(ALOAD,0));
        randomTick.instructions.add(new IntInsnNode(SIPUSH,6000));
        randomTick.instructions.add(new MethodInsnNode(INVOKEVIRTUAL,SERVER_WORLD,"method_64310","(L"+BLOCK_POS+";L"+BLOCK+";I)V",false));
        randomTick.instructions.add(ret);
        randomTick.instructions.add(new FrameNode(F_SAME,0,null,0,null));
        randomTick.instructions.add(new InsnNode(RETURN));
        cn.methods.add(randomTick);

        if(ctor!=1||props!=1||placement<1||tick!=1)
            throw new IllegalStateException(owner+" patch counts ctor="+ctor+" props="+props+" placement="+placement+" tick="+tick);
    }

    static void scheduleNaturalEgg(ClassNode cn,String eggField){
        int schedules=0;
        for(MethodNode m:cn.methods){
            if(!m.name.equals("placeEgg") || !m.desc.equals("(Lnet/minecraft/class_3218;)Lnet/minecraft/class_243;")) continue;
            for(AbstractInsnNode n=m.instructions.getFirst();n!=null;n=n.getNext()){
                if(n instanceof MethodInsnNode mi && mi.getOpcode()==INVOKEVIRTUAL && mi.owner.equals(SERVER_WORLD)
                        && mi.name.equals("method_8652") && mi.desc.equals("(Lnet/minecraft/class_2338;Lnet/minecraft/class_2680;I)Z")){
                    AbstractInsnNode pop=nextReal(n);
                    if(pop==null||pop.getOpcode()!=POP) continue;
                    InsnList add=new InsnList();
                    add.add(new VarInsnNode(ALOAD,1));
                    add.add(new VarInsnNode(ALOAD,8));
                    add.add(new FieldInsnNode(GETSTATIC,MOD_BLOCKS,eggField,"L"+BLOCK+";"));
                    add.add(new IntInsnNode(SIPUSH,6000));
                    add.add(new MethodInsnNode(INVOKEVIRTUAL,SERVER_WORLD,"method_64310","(L"+BLOCK_POS+";L"+BLOCK+";I)V",false));
                    m.instructions.insert(pop,add);
                    schedules++;
                    break;
                }
            }
        }
        if(schedules!=1) throw new IllegalStateException(cn.name+" schedule patches="+schedules);
    }

    static void enableEggRandomTicks(ClassNode cn){
        int patched=0;
        for(MethodNode m:cn.methods){
            if(!m.name.equals("<clinit>")) continue;
            for(AbstractInsnNode n=m.instructions.getFirst();n!=null;n=n.getNext()){
                if(n instanceof MethodInsnNode mi && mi.getOpcode()==INVOKESPECIAL
                        && (mi.owner.equals("net/suprk/ufauna/block/custom/CrocodileEgg") || mi.owner.equals("net/suprk/ufauna/block/custom/KomodoDragonEgg"))
                        && mi.name.equals("<init>")) {
                    m.instructions.insertBefore(n,new MethodInsnNode(INVOKEVIRTUAL,SETTINGS,"method_9640","()L"+SETTINGS+";",false));
                    patched++;
                }
            }
        }
        if(patched!=2) throw new IllegalStateException("ModBlocks random tick settings patches="+patched);
    }

    static byte[] patch(byte[] input, boolean blockClass, String eggField){
        ClassReader cr=new ClassReader(input);
        ClassNode cn=new ClassNode();
        cr.accept(cn,0);
        if(blockClass) addPlayerProperty(cn); else scheduleNaturalEgg(cn,eggField);
        ClassWriter cw=new ClassWriter(cr,ClassWriter.COMPUTE_MAXS);
        cn.accept(cw);
        return cw.toByteArray();
    }

    static byte[] patchModBlocks(byte[] input){
        ClassReader cr=new ClassReader(input);
        ClassNode cn=new ClassNode();
        cr.accept(cn,0);
        enableEggRandomTicks(cn);
        ClassWriter cw=new ClassWriter(cr,ClassWriter.COMPUTE_MAXS);
        cn.accept(cw);
        return cw.toByteArray();
    }

    public static void main(String[] args)throws Exception{
        if(args.length!=2) throw new IllegalArgumentException("usage: <class-root> <output-root>");
        Path in=Path.of(args[0]), out=Path.of(args[1]);
        String[][] targets={
            {"net/suprk/ufauna/block/custom/CrocodileEgg","block","CROCODILE_EGG"},
            {"net/suprk/ufauna/block/custom/KomodoDragonEgg","block","KOMODODRAGON_EGG"},
            {"net/suprk/ufauna/entity/custom/CrocodileEntity","entity","CROCODILE_EGG"},
            {"net/suprk/ufauna/entity/custom/KomodoDragonEntity","entity","KOMODODRAGON_EGG"}
        };
        for(String[] t:targets){
            Path src=in.resolve(t[0]+".class"), dst=out.resolve(t[0]+".class");
            Files.createDirectories(dst.getParent());
            Files.write(dst,patch(Files.readAllBytes(src),t[1].equals("block"),t[2]));
            System.out.println("patched "+t[0]);
        }
        Path mbSrc=in.resolve("net/suprk/ufauna/block/ModBlocks.class");
        Path mbDst=out.resolve("net/suprk/ufauna/block/ModBlocks.class");
        Files.createDirectories(mbDst.getParent());
        Files.write(mbDst,patchModBlocks(Files.readAllBytes(mbSrc)));
        System.out.println("patched net/suprk/ufauna/block/ModBlocks");
    }
}
