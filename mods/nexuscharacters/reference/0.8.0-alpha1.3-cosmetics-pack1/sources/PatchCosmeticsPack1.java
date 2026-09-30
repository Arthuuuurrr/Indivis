import java.nio.file.*;
import jdk.internal.org.objectweb.asm.*;
import jdk.internal.org.objectweb.asm.tree.*;

public final class PatchCosmeticsPack1 implements Opcodes {
    private static final int ASM_API = ASM8;

    private static AbstractInsnNode prevReal(AbstractInsnNode n) {
        for (AbstractInsnNode p=n.getPrevious(); p!=null; p=p.getPrevious()) if (p.getOpcode() >= 0) return p;
        return null;
    }
    private static AbstractInsnNode nextReal(AbstractInsnNode n) {
        for (AbstractInsnNode p=n.getNext(); p!=null; p=p.getNext()) if (p.getOpcode() >= 0) return p;
        return null;
    }

    private static byte[] patchAppearance69(byte[] input) {
        ClassNode cn = new ClassNode(ASM_API);
        new ClassReader(input).accept(cn, 0);
        int fields=0, regex=0, hairClamp=0, labels=0;

        for (FieldNode f : cn.fields) {
            if (f.name.equals("HAIR_COUNT") && f.desc.equals("I") && Integer.valueOf(10).equals(f.value)) {
                f.value = 12;
                fields++;
            }
        }

        final String oldRegex = "player_v69_b([1-4])_e([1-8])_ec([0-7])_ey([0-4])_h(0[0-9]|10)_hc(0[0-9]|1[0-2])_s(0[1-9]|1[0-6])_o(0[1-9]|1[0-9]|20)_fh([0-3])_fc(0[0-9]|1[0-2])_mk([0-8])_mc([0-9]|1[0-2])";
        final String newRegex = "player_v69_b([1-4])_e([1-8])_ec([0-7])_ey([0-4])_h(0[0-9]|1[0-2])_hc(0[0-9]|1[0-2])_s(0[1-9]|1[0-6])_o(0[1-9]|[12][0-9]|3[0-5])_fh([0-3])_fc(0[0-9]|1[0-2])_mk([0-8])_mc([0-9]|1[0-2])";

        for (MethodNode m : cn.methods) {
            for (AbstractInsnNode n=m.instructions.getFirst(); n!=null; n=n.getNext()) {
                if (n instanceof LdcInsnNode ldc && ldc.cst instanceof String s) {
                    if (s.equals(oldRegex)) { ldc.cst = newRegex; regex++; }
                    else if (s.equals("Moustache épaisse")) { ldc.cst = "Barbe légère"; labels++; }
                    else if (s.equals("Moustache courte")) { ldc.cst = "Moustache"; labels++; }
                }
            }
            if (m.name.equals("marker")) {
                for (AbstractInsnNode n=m.instructions.getFirst(); n!=null; n=n.getNext()) {
                    if (n instanceof IntInsnNode ii && ii.getOpcode()==BIPUSH && ii.operand==10) {
                        AbstractInsnNode p1=prevReal(n), p2=p1==null?null:prevReal(p1), nx=nextReal(n);
                        if (p1!=null && p1.getOpcode()==ICONST_0 && p2 instanceof VarInsnNode vi && vi.getOpcode()==ILOAD && vi.var==6
                                && nx instanceof MethodInsnNode mi && mi.name.equals("clamp")) {
                            ii.operand=12;
                            hairClamp++;
                        }
                    }
                }
            }
        }
        if (fields!=1 || regex!=1 || hairClamp!=1 || labels!=2) {
            throw new IllegalStateException("Appearance69 patch mismatch fields="+fields+" regex="+regex+" hairClamp="+hairClamp+" labels="+labels);
        }
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        cn.accept(cw);
        return cw.toByteArray();
    }

    private static String patchUiText(String s) {
        return s.replace("/10", "/12").replace("/20", "/35");
    }

    private static byte[] patchCreationMixin(byte[] input) {
        ClassNode cn = new ClassNode(ASM_API);
        new ClassReader(input).accept(cn, 0);
        int hairCycle=0, outfitCycle=0, markerHook=0, strings=0;
        final String owner = "net/tompsen/nexuscharacters/mixin/client/CharacterCreationAppearance65Mixin";
        final String appearance = "net/tompsen/nexuscharacters/Appearance69Support";
        final String helper = "net/tompsen/nexuscharacters/CosmeticsPack1Support";

        for (MethodNode m : cn.methods) {
            for (AbstractInsnNode n=m.instructions.getFirst(); n!=null; n=n.getNext()) {
                if (n instanceof LdcInsnNode ldc && ldc.cst instanceof String s) {
                    String p=patchUiText(s);
                    if (!p.equals(s)) { ldc.cst=p; strings++; }
                } else if (n instanceof InvokeDynamicInsnNode indy) {
                    Object[] args=indy.bsmArgs;
                    for (int i=0;i<args.length;i++) {
                        if (args[i] instanceof String s) {
                            String p=patchUiText(s);
                            if (!p.equals(s)) { args[i]=p; strings++; }
                        }
                    }
                }
            }

            if (m.name.equals("lambda$nexuscharacters$install$2")) {
                for (AbstractInsnNode n=m.instructions.getFirst(); n!=null; n=n.getNext()) {
                    if (n instanceof IntInsnNode ii && ii.getOpcode()==BIPUSH && ii.operand==11) {
                        ii.operand=13; hairCycle++;
                    }
                }
            }
            if (m.name.equals("lambda$nexuscharacters$install$4")) {
                for (AbstractInsnNode n=m.instructions.getFirst(); n!=null; n=n.getNext()) {
                    if (n instanceof IntInsnNode ii && ii.getOpcode()==BIPUSH && ii.operand==20) {
                        ii.operand=35; outfitCycle++;
                    }
                }
            }
            if (m.name.equals("nexuscharacters$v69Marker")) {
                for (AbstractInsnNode n=m.instructions.getFirst(); n!=null; ) {
                    AbstractInsnNode next=n.getNext();
                    if (n instanceof MethodInsnNode mi && mi.getOpcode()==INVOKESTATIC && mi.owner.equals(appearance) && mi.name.equals("marker")) {
                        InsnList add = new InsnList();
                        add.add(new VarInsnNode(ALOAD, 0));
                        add.add(new FieldInsnNode(GETFIELD, owner, "nexuscharacters$outfit", "I"));
                        add.add(new MethodInsnNode(INVOKESTATIC, helper, "withOutfit", "(Ljava/lang/String;I)Ljava/lang/String;", false));
                        m.instructions.insert(n, add);
                        markerHook++;
                    }
                    n=next;
                }
            }
        }
        if (hairCycle!=1 || outfitCycle!=1 || markerHook!=1 || strings < 4) {
            throw new IllegalStateException("Creation mixin patch mismatch hairCycle="+hairCycle+" outfitCycle="+outfitCycle+" markerHook="+markerHook+" strings="+strings);
        }
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        cn.accept(cw);
        return cw.toByteArray();
    }

    public static void main(String[] args) throws Exception {
        if (args.length!=1) throw new IllegalArgumentException("workdir");
        Path root=Paths.get(args[0]);
        Path a=root.resolve("net/tompsen/nexuscharacters/Appearance69Support.class");
        Path c=root.resolve("net/tompsen/nexuscharacters/mixin/client/CharacterCreationAppearance65Mixin.class");
        Files.write(a, patchAppearance69(Files.readAllBytes(a)));
        Files.write(c, patchCreationMixin(Files.readAllBytes(c)));
        System.out.println("Cosmetics Pack 1 bytecode patch applied successfully.");
    }
}
