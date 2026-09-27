import java.nio.file.*;
import jdk.internal.org.objectweb.asm.*;
import jdk.internal.org.objectweb.asm.tree.*;

public final class PatchFacialHairAdditive implements Opcodes {
    static final int API = ASM8;

    static byte[] patchA69(byte[] in) {
        ClassNode cn = new ClassNode(API);
        new ClassReader(in).accept(cn, 0);
        int field=0, regex=0, clamp=0, delegate=0, restore=0;

        for (FieldNode f : cn.fields) {
            if (f.name.equals("FACIAL_HAIR_COUNT") && f.desc.equals("I") && Integer.valueOf(4).equals(f.value)) {
                f.value = 6;
                field++;
            }
        }

        for (MethodNode m : cn.methods) {
            for (AbstractInsnNode n=m.instructions.getFirst(); n!=null; n=n.getNext()) {
                if (n instanceof LdcInsnNode l && l.cst instanceof String s) {
                    if (s.contains("_fh([0-3])_")) {
                        l.cst=s.replace("_fh([0-3])_","_fh([0-5])_");
                        regex++;
                    }
                    if (m.name.equals("<clinit>") && s.equals("Moustache")) {
                        l.cst="Moustache courte";
                        restore++;
                    }
                    if (m.name.equals("<clinit>") && s.equals("Barbe légère")) {
                        l.cst="Moustache épaisse";
                        restore++;
                    }
                }
            }

            if (m.name.equals("marker")) {
                for (AbstractInsnNode n=m.instructions.getFirst(); n!=null; n=n.getNext()) {
                    if (n.getOpcode()==ICONST_3) {
                        AbstractInsnNode p=n.getPrevious();
                        while (p!=null && p.getOpcode()<0) p=p.getPrevious();
                        AbstractInsnNode p2=p==null?null:p.getPrevious();
                        while (p2!=null && p2.getOpcode()<0) p2=p2.getPrevious();
                        AbstractInsnNode nx=n.getNext();
                        while (nx!=null && nx.getOpcode()<0) nx=nx.getNext();

                        if (p!=null && p.getOpcode()==ICONST_0
                                && p2 instanceof VarInsnNode v && v.getOpcode()==ILOAD && v.var==7
                                && nx instanceof MethodInsnNode mi && mi.name.equals("clamp")) {
                            m.instructions.set(n,new InsnNode(ICONST_5));
                            clamp++;
                        }
                    }
                }
            }

            if (m.name.equals("facialHairLabel") && m.desc.equals("(I)Ljava/lang/String;")) {
                m.instructions.clear();
                m.tryCatchBlocks.clear();
                m.localVariables=null;
                InsnList x=new InsnList();
                x.add(new VarInsnNode(ILOAD,0));
                x.add(new MethodInsnNode(INVOKESTATIC,
                        "net/tompsen/nexuscharacters/CosmeticsPack1Support",
                        "facialHairLabel",
                        "(I)Ljava/lang/String;",
                        false));
                x.add(new InsnNode(ARETURN));
                m.instructions.add(x);
                delegate++;
            }
        }

        if (field!=1 || regex!=1 || clamp!=1 || delegate!=1 || restore!=2) {
            throw new IllegalStateException("A69 mismatch field="+field+" regex="+regex+" clamp="+clamp+" delegate="+delegate+" restore="+restore);
        }

        ClassWriter cw=new ClassWriter(ClassWriter.COMPUTE_MAXS);
        cn.accept(cw);
        return cw.toByteArray();
    }

    static byte[] patchRacial(byte[] in) {
        ClassNode cn=new ClassNode(API);
        new ClassReader(in).accept(cn,0);
        int norm=0, nextMax=0, nextMod=0;

        for (MethodNode m : cn.methods) {
            if (m.name.equals("normalizeFacialHair")) {
                for (AbstractInsnNode n=m.instructions.getFirst(); n!=null; n=n.getNext()) {
                    if (n.getOpcode()==ICONST_3) {
                        m.instructions.set(n,new InsnNode(ICONST_5));
                        norm++;
                    }
                }
            }

            if (m.name.equals("nextFacialHair")) {
                for (AbstractInsnNode n=m.instructions.getFirst(); n!=null;) {
                    AbstractInsnNode nx=n.getNext();
                    if (n.getOpcode()==ICONST_3) {
                        m.instructions.set(n,new InsnNode(ICONST_5));
                        nextMax++;
                    } else if (n.getOpcode()==ICONST_4) {
                        m.instructions.set(n,new IntInsnNode(BIPUSH,6));
                        nextMod++;
                    }
                    n=nx;
                }
            }
        }

        if (norm!=1 || nextMax!=1 || nextMod!=1) {
            throw new IllegalStateException("Racial mismatch norm="+norm+" nextMax="+nextMax+" nextMod="+nextMod);
        }

        ClassWriter cw=new ClassWriter(ClassWriter.COMPUTE_MAXS);
        cn.accept(cw);
        return cw.toByteArray();
    }

    static byte[] patchDynamic(byte[] in) {
        ClassNode cn=new ClassNode(API);
        new ClassReader(in).accept(cn,0);
        int delegate=0;

        for (MethodNode m : cn.methods) {
            if (m.name.equals("facialHairName") && m.desc.equals("(I)Ljava/lang/String;")) {
                m.instructions.clear();
                m.tryCatchBlocks.clear();
                m.localVariables=null;
                InsnList x=new InsnList();
                x.add(new VarInsnNode(ILOAD,0));
                x.add(new MethodInsnNode(INVOKESTATIC,
                        "net/tompsen/nexuscharacters/CosmeticsPack1Support",
                        "facialHairName",
                        "(I)Ljava/lang/String;",
                        false));
                x.add(new InsnNode(ARETURN));
                m.instructions.add(x);
                delegate++;
            }
        }

        if (delegate!=1) {
            throw new IllegalStateException("Dynamic mismatch delegate="+delegate);
        }

        ClassWriter cw=new ClassWriter(ClassWriter.COMPUTE_MAXS);
        cn.accept(cw);
        return cw.toByteArray();
    }

    public static void main(String[] a) throws Exception {
        Path root=Paths.get(a[0]);

        Path p=root.resolve("net/tompsen/nexuscharacters/Appearance69Support.class");
        Files.write(p,patchA69(Files.readAllBytes(p)));

        p=root.resolve("net/tompsen/nexuscharacters/RacialAppearance69Support.class");
        Files.write(p,patchRacial(Files.readAllBytes(p)));

        p=root.resolve("net/tompsen/nexuscharacters/DynamicAppearanceSupport.class");
        Files.write(p,patchDynamic(Files.readAllBytes(p)));

        System.out.println("Additive facial-hair patch applied.");
    }
}
