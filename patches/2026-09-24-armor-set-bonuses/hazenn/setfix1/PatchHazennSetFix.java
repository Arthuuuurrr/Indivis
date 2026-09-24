import jdk.internal.org.objectweb.asm.*;
import jdk.internal.org.objectweb.asm.tree.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.jar.*;

public class PatchHazennSetFix implements Opcodes {
    static final String GEO = "net/hazen/hazennstuff/item/HnSGeoArmorItem";
    static final String ITEMS = "net/hazen/hazennstuff/registry/HnSItems";
    static final String EFFECTS = "net/hazen/hazennstuff/registry/HnSEffects";
    static final String HELPER = "net/hazen/hazennstuff/hc/HcSetRules";

    static final Map<String,String> TYRANT_REMAP = Map.ofEntries(
        Map.entry("cryogenic_ruler_", "MAGE_SET_BONUS"),
        Map.entry("dead_king_", "SUMMONER_SET_BONUS"),
        Map.entry("garments_of_the_first_flamebearer_", "FIREBLOSSOM_RULER_EFFECT"),
        Map.entry("legionnaire_commander_", "SWORDMASTER_SET_BONUS"),
        Map.entry("legionnaire_ruler_", "SWORDMASTER_SET_BONUS"),
        Map.entry("legionnaire_", "SWORDMASTER_SET_BONUS"),
        Map.entry("soul_legionnaire_ruler_", "SWORDMASTER_SET_BONUS"),
        Map.entry("pyrium_battlemage_", "MAGE_SET_BONUS"),
        Map.entry("pyrium_", "SWORDMASTER_SET_BONUS")
    );

    public static void main(String[] args) throws Exception {
        if (args.length != 4) throw new IllegalArgumentException("in.jar out.jar helper.class version");
        Path in = Path.of(args[0]), out = Path.of(args[1]), helper = Path.of(args[2]);
        String version = args[3];
        try (JarFile jar = new JarFile(in.toFile()); JarOutputStream jos = new JarOutputStream(Files.newOutputStream(out))) {
            Enumeration<JarEntry> en = jar.entries();
            Set<String> written = new HashSet<>();
            while (en.hasMoreElements()) {
                JarEntry e = en.nextElement();
                if (e.isDirectory()) { if (written.add(e.getName())) jos.putNextEntry(new JarEntry(e.getName())); jos.closeEntry(); continue; }
                byte[] data = jar.getInputStream(e).readAllBytes();
                String name = e.getName();
                if (name.equals(GEO + ".class")) data = patchGeo(data);
                else if (name.equals(ITEMS + ".class")) data = patchItems(data);
                else if (name.equals(EFFECTS + ".class")) data = patchEffects(data);
                else if (name.equals("fabric.mod.json")) data = patchManifest(data, version);
                if (written.add(name)) { jos.putNextEntry(new JarEntry(name)); jos.write(data); jos.closeEntry(); }
            }
            String helperName = HELPER + ".class";
            if (written.add(helperName)) {
                jos.putNextEntry(new JarEntry(helperName));
                jos.write(Files.readAllBytes(helper));
                jos.closeEntry();
            }
            Path nested = helper.resolveSibling("HcSetRules$Access.class");
            String nestedName = HELPER + "$Access.class";
            if (Files.exists(nested) && written.add(nestedName)) {
                jos.putNextEntry(new JarEntry(nestedName)); jos.write(Files.readAllBytes(nested)); jos.closeEntry();
            }
        }
    }

    static byte[] patchGeo(byte[] input) {
        ClassNode cn = read(input);
        boolean done=false;
        for (MethodNode m: cn.methods) {
            if (m.name.equals("isWearingFullSet") && m.desc.equals("(Lnet/minecraft/class_1657;)Z")) {
                m.instructions.clear(); m.tryCatchBlocks.clear();
                if (m.localVariables != null) m.localVariables.clear();
                m.instructions.add(new VarInsnNode(ALOAD,0));
                m.instructions.add(new VarInsnNode(ALOAD,1));
                m.instructions.add(new MethodInsnNode(INVOKESTATIC, HELPER, "isWearingExpectedSet", "(Ljava/lang/Object;Ljava/lang/Object;)Z", false));
                m.instructions.add(new InsnNode(IRETURN));
                m.maxStack=2; m.maxLocals=2; done=true;
            }
        }
        if(!done) throw new IllegalStateException("isWearingFullSet target not found");
        return write(cn);
    }

    static byte[] patchEffects(byte[] input) {
        ClassNode cn = read(input); int changed=0;
        for (MethodNode m: cn.methods) {
            if (!m.name.equals("lambda$static$6")) continue;
            for (AbstractInsnNode n=m.instructions.getFirst(); n!=null; n=n.getNext()) {
                if (n instanceof FieldInsnNode f && f.getOpcode()==GETSTATIC &&
                    f.owner.equals("net/minecraft/class_1322$class_1323") && f.name.equals("field_6330")) {
                    f.name="field_6328"; changed++;
                }
            }
        }
        if(changed!=2) throw new IllegalStateException("Expected 2 fireblossom operations, changed="+changed);
        return write(cn);
    }

    static byte[] patchItems(byte[] input) {
        ClassNode cn=read(input); int changed=0;
        MethodNode clinit=null;
        for(MethodNode m:cn.methods) if(m.name.equals("<clinit>")) clinit=m;
        if(clinit==null) throw new IllegalStateException("HnSItems <clinit> missing");
        String currentItem=null;
        for(AbstractInsnNode n=clinit.instructions.getFirst();n!=null;n=n.getNext()) {
            if(n instanceof LdcInsnNode ldc && ldc.cst instanceof String s) {
                if(s.matches("[a-z0-9_]+_(helmet|chestplate|leggings|boots)")) currentItem=s;
            }
            if(n instanceof FieldInsnNode f && f.getOpcode()==GETSTATIC && f.owner.equals(EFFECTS) && f.name.equals("TYRANTS_GRACE_EFFECT") && currentItem!=null) {
                String replacement = replacement(currentItem);
                if(replacement!=null) { f.name=replacement; changed++; }
            }
            if(n instanceof MethodInsnNode mi && mi.owner.equals(ITEMS) && mi.name.equals("armour")) currentItem=null;
        }
        if(changed!=36) throw new IllegalStateException("Expected 36 Tyrant piece remaps, changed="+changed);
        return write(cn);
    }

    static String replacement(String item) {
        if(item.startsWith("soul_legionnaire_ruler_")) return "SWORDMASTER_SET_BONUS";
        if(item.startsWith("legionnaire_commander_")) return "SWORDMASTER_SET_BONUS";
        if(item.startsWith("legionnaire_ruler_")) return "SWORDMASTER_SET_BONUS";
        if(item.startsWith("pyrium_battlemage_")) return "MAGE_SET_BONUS";
        for(var e:TYRANT_REMAP.entrySet()) if(item.startsWith(e.getKey())) return e.getValue();
        return null;
    }

    static byte[] patchManifest(byte[] data, String version) {
        String s=new String(data, java.nio.charset.StandardCharsets.UTF_8);
        s=s.replaceAll("\\\"version\\\"\\s*:\\s*\\\"[^\\\"]+\\\"", "\\\"version\\\": \\\""+version+"\\\"");
        s=s.replaceFirst("(\"description\"\\s*:\s*\")([^\"]*)(\")",
                "$1$2 Haute Capitale SETFIX1: fixes partial/variant armor-set detection, replaces the ineffective Fireblossom Warrior armor-base multiplier with fixed armor/toughness values, and specializes former Tyrant's Grace assignments.$3");
        return s.getBytes(java.nio.charset.StandardCharsets.UTF_8);
    }
    static ClassNode read(byte[] b){ ClassNode n=new ClassNode(); new ClassReader(b).accept(n,0); return n; }
    static byte[] write(ClassNode n){ ClassWriter w=new ClassWriter(0); n.accept(w); return w.toByteArray(); }
}
