import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;
import java.util.zip.*;
import jdk.internal.org.objectweb.asm.*;
import jdk.internal.org.objectweb.asm.tree.*;

public final class MMOMusicZonesAvaleivPatcher {
    private static final String TARGET_CLASS = "com/mmomusiczones/client/ZoneMusicPlayer.class";
    private static final String HELPER_CLASS = "com/mmomusiczones/client/AvaleivBiomeMusic.class";
    private static final String NEW_VERSION = "1.2.10-indivis-city-biomes-combat30s-avaleiv";

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.err.println("Usage: java ... -jar MMOMusicZones_Avaleiv_Patcher_1.2.10.jar <MMOMusicZones-1.2.9.jar>");
            System.exit(2);
        }
        Path input = Path.of(args[0]).toAbsolutePath().normalize();
        if (!Files.isRegularFile(input)) throw new FileNotFoundException(input.toString());
        Path output = input.resolveSibling("mmomusiczones-1.2.10-indivis-city-biomes-combat30s-avaleiv-FULL.jar");
        if (input.equals(output)) throw new IllegalArgumentException("Le JAR source doit etre la 1.2.9, pas le fichier de sortie.");

        byte[] zoneClass;
        byte[] fabricMod;
        try (ZipFile zf = new ZipFile(input.toFile())) {
            ZipEntry ce = zf.getEntry(TARGET_CLASS);
            if (ce == null) throw new IllegalStateException("ZoneMusicPlayer.class introuvable : mauvais JAR ?");
            zoneClass = readAll(zf, ce);
            ZipEntry fm = zf.getEntry("fabric.mod.json");
            if (fm == null) throw new IllegalStateException("fabric.mod.json introuvable : mauvais JAR ?");
            fabricMod = readAll(zf, fm);
        }

        String meta = new String(fabricMod, StandardCharsets.UTF_8);
        if (!meta.contains(""id"") || !meta.contains("mmomusiczones"))
            throw new IllegalStateException("Ce fichier ne semble pas etre MMO Music Zones.");

        byte[] patchedClass = patchZoneMusicPlayer(zoneClass);
        byte[] helper = resourceBytes("/" + HELPER_CLASS);
        byte[] patchedMeta = patchVersion(meta).getBytes(StandardCharsets.UTF_8);

        Files.deleteIfExists(output);
        try (ZipFile zf = new ZipFile(input.toFile());
             ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(Files.newOutputStream(output)))) {
            Enumeration<? extends ZipEntry> en = zf.entries();
            while (en.hasMoreElements()) {
                ZipEntry src = en.nextElement();
                String name = src.getName();
                if (name.equals(TARGET_CLASS) || name.equals("fabric.mod.json") || name.equals(HELPER_CLASS)) continue;
                if (src.isDirectory()) {
                    ZipEntry e = new ZipEntry(name);
                    if (src.getTime() >= 0) e.setTime(src.getTime());
                    out.putNextEntry(e); out.closeEntry();
                    continue;
                }
                writeStored(out, name, readAll(zf, src), src.getTime());
            }
            writeStored(out, TARGET_CLASS, patchedClass, System.currentTimeMillis());
            writeStored(out, HELPER_CLASS, helper, System.currentTimeMillis());
            writeStored(out, "fabric.mod.json", patchedMeta, System.currentTimeMillis());
        }

        try (ZipFile zf = new ZipFile(output.toFile())) {
            if (zf.getEntry(TARGET_CLASS) == null || zf.getEntry(HELPER_CLASS) == null || zf.getEntry("fabric.mod.json") == null)
                throw new IllegalStateException("Verification ZIP echouee.");
        }

        System.out.println("Patch termine.");
        System.out.println("Nouveau JAR : " + output.getFileName());
        System.out.println("Avaleiv jour : VILLE 05 + 09");
        System.out.println("Avaleiv nuit : VILLE 13");
    }

    private static byte[] patchZoneMusicPlayer(byte[] input) {
        ClassNode cn = new ClassNode();
        new ClassReader(input).accept(cn, 0);
        boolean injected = false;
        for (MethodNode m : cn.methods) {
            if (!m.name.equals("selectBiomeZone")) continue;
            if (!m.desc.equals("(Lnet/minecraft/class_310;Lnet/minecraft/class_2338;Ljava/lang/String;)Lcom/mmomusiczones/zone/MusicZone;")) continue;

            for (AbstractInsnNode n=m.instructions.getFirst(); n!=null; n=n.getNext()) {
                if (n instanceof MethodInsnNode mi && mi.owner.equals("com/mmomusiczones/client/AvaleivBiomeMusic"))
                    throw new IllegalStateException("Avaleiv semble deja patche dans ce JAR.");
            }

            AbstractInsnNode jungle = null;
            LabelNode normalFlow = null;
            for (AbstractInsnNode n=m.instructions.getFirst(); n!=null; n=n.getNext()) {
                if (n instanceof FieldInsnNode fi && fi.getOpcode()==Opcodes.GETSTATIC &&
                    fi.owner.equals("com/mmomusiczones/client/ZoneMusicPlayer") && fi.name.equals("JUNGLES")) {
                    jungle = n;
                    for (AbstractInsnNode p=n.getPrevious(); p!=null; p=p.getPrevious()) {
                        if (p instanceof LabelNode l) { normalFlow = l; break; }
                        if (!(p instanceof FrameNode) && !(p instanceof LineNumberNode)) break;
                    }
                    if (normalFlow == null) {
                        normalFlow = new LabelNode();
                        m.instructions.insertBefore(n, normalFlow);
                    }
                    break;
                }
            }
            if (jungle == null) throw new IllegalStateException("Point d'injection JUNGLES introuvable.");

            InsnList add = new InsnList();
            add.add(new VarInsnNode(Opcodes.ALOAD, 3));
            add.add(new VarInsnNode(Opcodes.ILOAD, 4));
            add.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
                "com/mmomusiczones/client/AvaleivBiomeMusic", "select",
                "(Ljava/lang/String;Z)Lcom/mmomusiczones/zone/MusicZone;", false));
            add.add(new VarInsnNode(Opcodes.ASTORE, 5));
            add.add(new VarInsnNode(Opcodes.ALOAD, 5));
            add.add(new JumpInsnNode(Opcodes.IFNULL, normalFlow));
            add.add(new VarInsnNode(Opcodes.ALOAD, 5));
            add.add(new InsnNode(Opcodes.ARETURN));
            m.instructions.insertBefore(normalFlow, add);
            injected = true;
        }
        if (!injected) throw new IllegalStateException("selectBiomeZone introuvable.");
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        cn.accept(cw);
        return cw.toByteArray();
    }

    private static String patchVersion(String json) {
        Pattern p = Pattern.compile("(\\"version\\"\\s*:\\s*\\")[^\\"]+(\\")");
        Matcher m = p.matcher(json);
        if (!m.find()) throw new IllegalStateException("Champ version introuvable dans fabric.mod.json");
        return m.replaceFirst(Matcher.quoteReplacement(m.group(1) + NEW_VERSION + m.group(2)));
    }

    private static byte[] resourceBytes(String path) throws IOException {
        try (InputStream in = MMOMusicZonesAvaleivPatcher.class.getResourceAsStream(path)) {
            if (in == null) throw new FileNotFoundException("Ressource interne absente: " + path);
            return in.readAllBytes();
        }
    }

    private static byte[] readAll(ZipFile zf, ZipEntry e) throws IOException {
        try (InputStream in = zf.getInputStream(e)) { return in.readAllBytes(); }
    }

    private static void writeStored(ZipOutputStream out, String name, byte[] data, long time) throws IOException {
        CRC32 crc = new CRC32(); crc.update(data);
        ZipEntry e = new ZipEntry(name);
        e.setMethod(ZipEntry.STORED);
        e.setSize(data.length);
        e.setCompressedSize(data.length);
        e.setCrc(crc.getValue());
        if (time >= 0) e.setTime(time);
        out.putNextEntry(e); out.write(data); out.closeEntry();
    }
}
