import java.nio.file.*;
import java.util.*;
import java.util.zip.*;
import jdk.internal.org.objectweb.asm.*;
import jdk.internal.org.objectweb.asm.tree.*;

public class PatchBossPoi121 {
  static final String OWNER = "fr/hautecapitale/creatures/spawn/CapitaleCreaturesRuntime124";
  static final String ENTITY = "net/minecraft/class_1297";
  static final String LIVING = "net/minecraft/class_1309";
  static final String PLAYER = "net/minecraft/class_3222";

  static ClassNode read(byte[] in) {
    ClassNode cn = new ClassNode();
    new ClassReader(in).accept(cn, 0);
    return cn;
  }

  static byte[] write(ClassNode cn) {
    ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS) {
      @Override protected String getCommonSuperClass(String a, String b) { return "java/lang/Object"; }
    };
    cn.accept(cw);
    return cw.toByteArray();
  }

  static void emitEquals(InsnList il, String value, int stringLocal, LabelNode target) {
    il.add(new LdcInsnNode(value));
    il.add(new VarInsnNode(Opcodes.ALOAD, stringLocal));
    il.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/String", "equals", "(Ljava/lang/Object;)Z", false));
    il.add(new JumpInsnNode(Opcodes.IFNE, target));
  }

  static void emitStartsWith(InsnList il, int stringLocal, String prefix, LabelNode target) {
    il.add(new VarInsnNode(Opcodes.ALOAD, stringLocal));
    il.add(new LdcInsnNode(prefix));
    il.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/String", "startsWith", "(Ljava/lang/String;)Z", false));
    il.add(new JumpInsnNode(Opcodes.IFNE, target));
  }

  static void patchRuntime(ClassNode cn) {
    if (!OWNER.equals(cn.name)) throw new IllegalStateException("Unexpected class " + cn.name);

    cn.methods.removeIf(m -> m.name.equals("shouldHardDespawnLiving")
        && m.desc.equals("(Lnet/minecraft/class_1297;Ljava/lang/String;)Z"));

    MethodNode h = new MethodNode(
        Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC,
        "shouldHardDespawnLiving",
        "(Lnet/minecraft/class_1297;Ljava/lang/String;)Z",
        null,
        null);

    InsnList il = h.instructions;
    LabelNode no = new LabelNode();
    LabelNode yes = new LabelNode();
    LabelNode notTameable = new LabelNode();
    LabelNode notDinoPrefix = new LabelNode();
    LabelNode tagLoop = new LabelNode();
    LabelNode afterTags = new LabelNode();

    il.add(new VarInsnNode(Opcodes.ALOAD,0));
    il.add(new JumpInsnNode(Opcodes.IFNULL,no));
    il.add(new VarInsnNode(Opcodes.ALOAD,0));
    il.add(new TypeInsnNode(Opcodes.INSTANCEOF,LIVING));
    il.add(new JumpInsnNode(Opcodes.IFEQ,no));
    il.add(new VarInsnNode(Opcodes.ALOAD,0));
    il.add(new TypeInsnNode(Opcodes.INSTANCEOF,PLAYER));
    il.add(new JumpInsnNode(Opcodes.IFNE,no));
    il.add(new VarInsnNode(Opcodes.ALOAD,1));
    il.add(new JumpInsnNode(Opcodes.IFNULL,no));

    emitEquals(il, "minecraft:armor_stand", 1, no);
    emitStartsWith(il, 1, "easy_npc:", no);

    il.add(new VarInsnNode(Opcodes.ALOAD,0));
    il.add(new TypeInsnNode(Opcodes.CHECKCAST,"net/minecraft/class_1275"));
    il.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE,"net/minecraft/class_1275","method_16914","()Z",true));
    il.add(new JumpInsnNode(Opcodes.IFNE,no));

    il.add(new VarInsnNode(Opcodes.ALOAD,0));
    il.add(new TypeInsnNode(Opcodes.INSTANCEOF,"net/minecraft/class_6025"));
    il.add(new JumpInsnNode(Opcodes.IFEQ,notTameable));
    il.add(new VarInsnNode(Opcodes.ALOAD,0));
    il.add(new TypeInsnNode(Opcodes.CHECKCAST,"net/minecraft/class_6025"));
    il.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE,"net/minecraft/class_6025","method_66287","()Lnet/minecraft/class_10583;",true));
    il.add(new JumpInsnNode(Opcodes.IFNONNULL,no));
    il.add(notTameable);

    il.add(new VarInsnNode(Opcodes.ALOAD,0));
    il.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,ENTITY,"method_5752","()Ljava/util/Set;",false));
    il.add(new VarInsnNode(Opcodes.ASTORE,2));

    String[] exactTags = {
      "indivis_no_hard_despawn",
      "hcspawn",
      "indivis_boss",
      "hc_boss",
      "boss",
      "miniboss"
    };
    for (String tag : exactTags) {
      il.add(new VarInsnNode(Opcodes.ALOAD,2));
      il.add(new LdcInsnNode(tag));
      il.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE,"java/util/Set","contains","(Ljava/lang/Object;)Z",true));
      il.add(new JumpInsnNode(Opcodes.IFNE,no));
    }

    il.add(new VarInsnNode(Opcodes.ALOAD,2));
    il.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE,"java/util/Set","iterator","()Ljava/util/Iterator;",true));
    il.add(new VarInsnNode(Opcodes.ASTORE,3));
    il.add(tagLoop);
    il.add(new VarInsnNode(Opcodes.ALOAD,3));
    il.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE,"java/util/Iterator","hasNext","()Z",true));
    il.add(new JumpInsnNode(Opcodes.IFEQ,afterTags));
    il.add(new VarInsnNode(Opcodes.ALOAD,3));
    il.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE,"java/util/Iterator","next","()Ljava/lang/Object;",true));
    il.add(new TypeInsnNode(Opcodes.CHECKCAST,"java/lang/String"));
    il.add(new FieldInsnNode(Opcodes.GETSTATIC,"java/util/Locale","ROOT","Ljava/util/Locale;"));
    il.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,"java/lang/String","toLowerCase","(Ljava/util/Locale;)Ljava/lang/String;",false));
    il.add(new VarInsnNode(Opcodes.ASTORE,4));

    String[] bossPrefixes = {
      "hcspawn.rank.boss",
      "hcspawn.rank.miniboss",
      "indivis_boss",
      "indivis.boss",
      "hc_boss",
      "hc.boss",
      "boss:",
      "boss.",
      "miniboss:",
      "miniboss."
    };
    for (String p : bossPrefixes) emitStartsWith(il,4,p,no);
    il.add(new JumpInsnNode(Opcodes.GOTO,tagLoop));
    il.add(afterTags);

    String[] bossNamespaces = {
      "rpg-minibosses:",
      "block_factorys_bosses:",
      "hc_necromancer:"
    };
    for (String ns : bossNamespaces) emitStartsWith(il,1,ns,no);

    String[] vanillaBosses = {
      "minecraft:ender_dragon",
      "minecraft:wither",
      "minecraft:warden"
    };
    for (String id : vanillaBosses) emitEquals(il,id,1,no);

    il.add(new VarInsnNode(Opcodes.ALOAD,1));
    il.add(new LdcInsnNode("dino_mounts_ai:"));
    il.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,"java/lang/String","startsWith","(Ljava/lang/String;)Z",false));
    il.add(new JumpInsnNode(Opcodes.IFEQ,notDinoPrefix));
    il.add(new VarInsnNode(Opcodes.ALOAD,1));
    il.add(new LdcInsnNode("_hostile"));
    il.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,"java/lang/String","endsWith","(Ljava/lang/String;)Z",false));
    il.add(new JumpInsnNode(Opcodes.IFEQ,no));
    il.add(notDinoPrefix);

    emitStartsWith(il,1,"autonomous_colored_horses:",no);

    String[] protectedIds = {
      "minecraft:villager",
      "minecraft:wandering_trader",
      "minecraft:iron_golem",
      "minecraft:snow_golem",
      "minecraft:allay",
      "minecraft:happy_ghast"
    };
    for (String id : protectedIds) emitEquals(il,id,1,no);

    il.add(new JumpInsnNode(Opcodes.GOTO,yes));
    il.add(yes);
    il.add(new InsnNode(Opcodes.ICONST_1));
    il.add(new InsnNode(Opcodes.IRETURN));
    il.add(no);
    il.add(new InsnNode(Opcodes.ICONST_0));
    il.add(new InsnNode(Opcodes.IRETURN));

    cn.methods.add(h);

    for (MethodNode m : cn.methods) {
      for (AbstractInsnNode n=m.instructions.getFirst(); n!=null; n=n.getNext()) {
        if (n instanceof LdcInsnNode ldc && ldc.cst instanceof String s && s.contains("1.2.20 runtime registered")) {
          ldc.cst = "[Capitale Creatures] 1.2.21 runtime registered: global LivingEntity hard >128 despawn; hcspawn/POI + boss catalogs protected.";
        }
      }
    }
  }

  public static void main(String[] args) throws Exception {
    if (args.length != 2) throw new IllegalArgumentException("input.jar output.jar");
    Path in = Path.of(args[0]);
    Path out = Path.of(args[1]);

    try (ZipFile zf = new ZipFile(in.toFile());
         ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(out))) {
      Enumeration<? extends ZipEntry> en = zf.entries();
      while (en.hasMoreElements()) {
        ZipEntry e = en.nextElement();
        byte[] data = zf.getInputStream(e).readAllBytes();
        String name = e.getName();

        if (name.equals(OWNER + ".class")) {
          ClassNode cn = read(data);
          patchRuntime(cn);
          data = write(cn);
        } else if (name.equals("fabric.mod.json")) {
          String s = new String(data, java.nio.charset.StandardCharsets.UTF_8);
          s = s.replace("\"version\": \"1.2.20\"", "\"version\": \"1.2.21\"");
          s = s.replace(
              "1.2.20 global living hard-despawn + exact city safety + patrol containment",
              "1.2.21 global living despawn + protected POI/boss catalogs + exact city safety");
          data = s.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        }

        ZipEntry ne = new ZipEntry(name);
        ne.setTime(e.getTime());
        zos.putNextEntry(ne);
        zos.write(data);
        zos.closeEntry();
      }

      ZipEntry note = new ZipEntry("META-INF/INDIVIS_GLOBAL_LIVING_DESPAWN_1_2_21_POI_BOSS_SAFE.txt");
      zos.putNextEntry(note);
      String txt =
          "Capitale Creatures 1.2.21\n" +
          "Global hard despawn >128 for ordinary LivingEntity.\n" +
          "Protected: hcspawn controlled POI mobs, boss/miniboss catalog tags, dedicated boss namespaces, vanilla bosses, plus all 1.2.20 protected categories.\n" +
          "PersistenceRequired remains intentionally ignored as a blanket exemption.\n";
      zos.write(txt.getBytes(java.nio.charset.StandardCharsets.UTF_8));
      zos.closeEntry();
    }
  }
}
