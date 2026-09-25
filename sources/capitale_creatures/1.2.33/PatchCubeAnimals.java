import java.nio.file.*;
import jdk.internal.org.objectweb.asm.*;
import jdk.internal.org.objectweb.asm.tree.*;

public class PatchCubeAnimals implements Opcodes {
    static final String EAGLE_NEST = "net/suprk/ufauna/block/custom/EagleNest";
    static final String EAGLE_ENTITY = "net/suprk/ufauna/entity/custom/EagleEntity";
    static final String BLOCK_STATE = "net/minecraft/class_2680";
    static final String SERVER_WORLD = "net/minecraft/class_3218";
    static final String BLOCK_POS = "net/minecraft/class_2338";
    static final String BLOCK = "net/minecraft/class_2248";
    static final String BLOCKS = "net/minecraft/class_2246";
    static final String MOD_BLOCKS = "net/suprk/ufauna/block/ModBlocks";

    static AbstractInsnNode nextReal(AbstractInsnNode n) {
        for (AbstractInsnNode x=n.getNext(); x!=null; x=x.getNext())
            if (!(x instanceof LabelNode) && !(x instanceof LineNumberNode) && !(x instanceof FrameNode)) return x;
        return null;
    }
    static AbstractInsnNode prevReal(AbstractInsnNode n) {
        for (AbstractInsnNode x=n.getPrevious(); x!=null; x=x.getPrevious())
            if (!(x instanceof LabelNode) && !(x instanceof LineNumberNode) && !(x instanceof FrameNode)) return x;
        return null;
    }

    static void patchNest(ClassNode cn) {
        int patches=0;
        for (MethodNode m: cn.methods) {
            if (m.name.equals("method_9588") && m.desc.equals("(Lnet/minecraft/class_2680;Lnet/minecraft/class_3218;Lnet/minecraft/class_2338;Lnet/minecraft/class_5819;)V")) {
                LabelNode keep = new LabelNode();
                InsnList pre = new InsnList();
                pre.add(new VarInsnNode(ALOAD, 1));
                pre.add(new FieldInsnNode(GETSTATIC, EAGLE_NEST, "HAS_EGG", "Lnet/minecraft/class_2746;"));
                pre.add(new MethodInsnNode(INVOKEVIRTUAL, BLOCK_STATE, "method_11654", "(Lnet/minecraft/class_2769;)Ljava/lang/Comparable;", false));
                pre.add(new TypeInsnNode(CHECKCAST, "java/lang/Boolean"));
                pre.add(new MethodInsnNode(INVOKEVIRTUAL, "java/lang/Boolean", "booleanValue", "()Z", false));
                pre.add(new JumpInsnNode(IFNE, keep));
                pre.add(new VarInsnNode(ALOAD, 1));
                pre.add(new FieldInsnNode(GETSTATIC, EAGLE_NEST, "PLACED_BY_PLAYER", "Lnet/minecraft/class_2746;"));
                pre.add(new MethodInsnNode(INVOKEVIRTUAL, BLOCK_STATE, "method_11654", "(Lnet/minecraft/class_2769;)Ljava/lang/Comparable;", false));
                pre.add(new TypeInsnNode(CHECKCAST, "java/lang/Boolean"));
                pre.add(new MethodInsnNode(INVOKEVIRTUAL, "java/lang/Boolean", "booleanValue", "()Z", false));
                pre.add(new JumpInsnNode(IFNE, keep));
                pre.add(new VarInsnNode(ALOAD, 2));
                pre.add(new VarInsnNode(ALOAD, 3));
                pre.add(new FieldInsnNode(GETSTATIC, BLOCKS, "field_10124", "Lnet/minecraft/class_2248;"));
                pre.add(new MethodInsnNode(INVOKEVIRTUAL, BLOCK, "method_9564", "()Lnet/minecraft/class_2680;", false));
                pre.add(new InsnNode(ICONST_3));
                pre.add(new MethodInsnNode(INVOKEVIRTUAL, SERVER_WORLD, "method_8652", "(Lnet/minecraft/class_2338;Lnet/minecraft/class_2680;I)Z", false));
                pre.add(new InsnNode(POP));
                pre.add(new InsnNode(RETURN));
                pre.add(keep);
                pre.add(new FrameNode(F_SAME, 0, null, 0, null));
                m.instructions.insert(pre);
                patches++;

                for (AbstractInsnNode n=m.instructions.getFirst(); n!=null; n=n.getNext()) {
                    if (n instanceof MethodInsnNode mi && mi.getOpcode()==INVOKEVIRTUAL && mi.owner.equals(EAGLE_NEST) && mi.name.equals("hatchBaby")) {
                        InsnList post = new InsnList();
                        post.add(new VarInsnNode(ALOAD, 2));
                        post.add(new VarInsnNode(ALOAD, 3));
                        post.add(new VarInsnNode(ALOAD, 0));
                        post.add(new IntInsnNode(SIPUSH, 6000));
                        post.add(new MethodInsnNode(INVOKEVIRTUAL, SERVER_WORLD, "method_64310", "(Lnet/minecraft/class_2338;Lnet/minecraft/class_2248;I)V", false));
                        m.instructions.insert(n, post);
                        patches++;
                        break;
                    }
                }
            }

            if (m.name.equals("method_9605") && m.desc.equals("(Lnet/minecraft/class_1750;)Lnet/minecraft/class_2680;")) {
                for (AbstractInsnNode n=m.instructions.getFirst(); n!=null; n=n.getNext()) {
                    if (n.getOpcode()==ARETURN) {
                        InsnList add = new InsnList();
                        add.add(new FieldInsnNode(GETSTATIC, EAGLE_NEST, "PLACED_BY_PLAYER", "Lnet/minecraft/class_2746;"));
                        add.add(new InsnNode(ICONST_1));
                        add.add(new MethodInsnNode(INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;", false));
                        add.add(new MethodInsnNode(INVOKEVIRTUAL, BLOCK_STATE, "method_11657", "(Lnet/minecraft/class_2769;Ljava/lang/Comparable;)Ljava/lang/Object;", false));
                        add.add(new TypeInsnNode(CHECKCAST, BLOCK_STATE));
                        m.instructions.insertBefore(n, add);
                        patches++;
                        break;
                    }
                }
            }

            if (m.name.equals("method_55766")) {
                for (AbstractInsnNode n=m.instructions.getFirst(); n!=null; n=n.getNext()) {
                    if (n instanceof FieldInsnNode fi && fi.getOpcode()==GETSTATIC && fi.owner.equals(EAGLE_NEST) && fi.name.equals("PLACED_BY_PLAYER")) {
                        AbstractInsnNode x = nextReal(n);
                        if (x != null && x.getOpcode()==ICONST_0) {
                            m.instructions.set(x, new InsnNode(ICONST_1));
                            patches++;
                        }
                    }
                }
            }
        }
        if (patches != 4) throw new IllegalStateException("Expected 4 EagleNest patches, got " + patches);
    }

    static void patchEagleEntity(ClassNode cn) {
        int schedules=0;
        for (MethodNode m: cn.methods) {
            if (!m.name.equals("placeNest") || !m.desc.equals("(Lnet/minecraft/class_3218;)Lnet/minecraft/class_243;")) continue;
            for (AbstractInsnNode n=m.instructions.getFirst(); n!=null; ) {
                AbstractInsnNode next=n.getNext();
                if (n instanceof MethodInsnNode mi && mi.getOpcode()==INVOKEVIRTUAL && mi.owner.equals(SERVER_WORLD)
                    && mi.name.equals("method_8652") && mi.desc.equals("(Lnet/minecraft/class_2338;Lnet/minecraft/class_2680;I)Z")) {
                    AbstractInsnNode pop = nextReal(n);
                    if (pop != null && pop.getOpcode()==POP) {
                        int posLocal=-1;
                        for (AbstractInsnNode b=prevReal(n); b!=null; b=prevReal(b)) {
                            if (b instanceof FieldInsnNode fi && fi.getOpcode()==GETSTATIC && fi.owner.equals(MOD_BLOCKS) && fi.name.equals("EAGLE_NEST")) {
                                AbstractInsnNode a=prevReal(b);
                                if (a instanceof VarInsnNode vi && vi.getOpcode()==ALOAD) posLocal=vi.var;
                                break;
                            }
                            if (b.getOpcode()==GOTO || b.getOpcode()==ARETURN) break;
                        }
                        if (posLocal<0) throw new IllegalStateException("Could not infer nest pos local");
                        InsnList add=new InsnList();
                        add.add(new VarInsnNode(ALOAD,1));
                        add.add(new VarInsnNode(ALOAD,posLocal));
                        add.add(new FieldInsnNode(GETSTATIC,MOD_BLOCKS,"EAGLE_NEST","Lnet/minecraft/class_2248;"));
                        add.add(new IntInsnNode(SIPUSH,6000));
                        add.add(new MethodInsnNode(INVOKEVIRTUAL,SERVER_WORLD,"method_64310","(Lnet/minecraft/class_2338;Lnet/minecraft/class_2248;I)V",false));
                        m.instructions.insert(pop,add);
                        schedules++;
                    }
                }
                n=next;
            }
        }
        if (schedules!=2) throw new IllegalStateException("Expected 2 natural nest schedule injections, got "+schedules);
    }

    static byte[] transform(byte[] input, boolean nest) {
        ClassReader cr=new ClassReader(input);
        ClassNode cn=new ClassNode();
        cr.accept(cn,0);
        if (nest) patchNest(cn); else patchEagleEntity(cn);
        ClassWriter cw=new ClassWriter(cr,ClassWriter.COMPUTE_MAXS);
        cn.accept(cw);
        return cw.toByteArray();
    }

    public static void main(String[] args) throws Exception {
        if (args.length!=2) throw new IllegalArgumentException("usage: <class-root> <output-root>");
        Path in=Path.of(args[0]), out=Path.of(args[1]);
        Path nest=in.resolve(EAGLE_NEST+".class"), eagle=in.resolve(EAGLE_ENTITY+".class");
        Path outNest=out.resolve(EAGLE_NEST+".class"), outEagle=out.resolve(EAGLE_ENTITY+".class");
        Files.createDirectories(outNest.getParent());
        Files.createDirectories(outEagle.getParent());
        Files.write(outNest,transform(Files.readAllBytes(nest),true));
        Files.write(outEagle,transform(Files.readAllBytes(eagle),false));
    }
}
