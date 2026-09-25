import java.nio.file.*;
import java.util.*;
import java.util.zip.*;
import jdk.internal.org.objectweb.asm.*;
import jdk.internal.org.objectweb.asm.tree.*;

public class PatchGlobalLivingDespawn120 {
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

  static void patchRuntime(ClassNode cn) {
    if (!OWNER.equals(cn.name)) throw new IllegalStateException("Unexpected class " + cn.name);

    MethodNode hard = null;
    for (MethodNode m : cn.methods) {
      if (m.name.equals("hardDespawn") && m.desc.equals("(Lnet/minecraft/class_3218;)V")) hard = m;
    }
    if (hard == null) throw new IllegalStateException("hardDespawn missing");

    boolean replaced = false;
    for (AbstractInsnNode n = hard.instructions.getFirst(); n != null; n = n.getNext()) {
      if (n instanceof MethodInsnNode mi
          && mi.getOpcode() == Opcodes.INVOKESTATIC
          && OWNER.equals(mi.owner)
          && mi.name.equals("isManagedPersistentHostile")
          && mi.desc.equals("(Ljava/lang/String;)Z")) {
        InsnList before = new InsnList();
        before.add(new VarInsnNode(Opcodes.ASTORE, 4));
        before.add(new VarInsnNode(Opcodes.ALOAD, 3));
        before.add(new VarInsnNode(Opcodes.ALOAD, 4));
        hard.instructions.insertBefore(n, before);
        mi.name = "shouldHardDespawnLiving";
        mi.desc = "(Lnet/minecraft/class_1297;Ljava/lang/String;)Z";
        replaced = true;
        break;
      }
    }
    if (!replaced) throw new IllegalStateException("managed-hostile gate not found");

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
    LabelNode checkId = new LabelNode();
    LabelNode checkTags = new LabelNode();
    LabelNode checkDino = new LabelNode();
    LabelNode checkAutonomousHorses = new LabelNode();
    LabelNode checkVanillaProtected = new LabelNode();

    il.add(new VarInsnNode(Opcodes.ALOAD,0));
    il.add(new JumpInsnNode(Opcodes.IFNULL,no));

    il.add(new VarInsnNode(Opcodes.ALOAD,0));
    il.add(new TypeInsnNode(Opcodes.INSTANCEOF,LIVING));
    il.add(new JumpInsnNode(Opcodes.IFEQ,no));

    il.add(new VarInsnNode(Opcodes.ALOAD,0));
    il.add(new TypeInsnNode(Opcodes.INSTANCEOF,PLAYER));
    il.add(new JumpInsnNode(Opcodes.IFNE,no));

    il.add(new VarInsnNode(Opcodes.ALOAD,1));
    il.add(new JumpInsnNode(Opcodes.IFNONNULL,checkId));
    il.add(new JumpInsnNode(Opcodes.GOTO,no));

    il.add(checkId);

    il.add(new LdcInsnNode("minecraft:armor_stand"));
    il.add(new VarInsnNode(Opcodes.ALOAD,1));
    il.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,"java/lang/String","equals","(Ljava/lang/Object;)Z",false));
    il.add(new JumpInsnNode(Opcodes.IFNE,no));

    il.add(new VarInsnNode(Opcodes.ALOAD,1));
    il.add(new LdcInsnNode("easy_npc:"));
    il.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,"java/lang/String","startsWith","(Ljava/lang/String;)Z",false));
    il.add(new JumpInsnNode(Opcodes.IFNE,no));

    // Preserve named LivingEntity: typical boss / quest / decorative actor.
    il.add(new VarInsnNode(Opcodes.ALOAD,0));
    il.add(new TypeInsnNode(Opcodes.CHECKCAST,"net/minecraft/class_1275"));
    il.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE,"net/minecraft/class_1275","method_16914","()Z",true));
    il.add(new JumpInsnNode(Opcodes.IFNE,no));

    // Preserve only Tameable entities that actually have an owner.
    LabelNode notTameable = new LabelNode();
    il.add(new VarInsnNode(Opcodes.ALOAD,0));
    il.add(new TypeInsnNode(Opcodes.INSTANCEOF,"net/minecraft/class_6025"));
    il.add(new JumpInsnNode(Opcodes.IFEQ,notTameable));
    il.add(new VarInsnNode(Opcodes.ALOAD,0));
    il.add(new TypeInsnNode(Opcodes.CHECKCAST,"net/minecraft/class_6025"));
    il.add(new MethodInsnNode(
        Opcodes.INVOKEINTERFACE,
        "net/minecraft/class_6025",
        "method_66287",
        "()Lnet/minecraft/class_10583;",
        true));
    il.add(new JumpInsnNode(Opcodes.IFNONNULL,no));
    il.add(notTameable);

    il.add(checkTags);
    il.add(new VarInsnNode(Opcodes.ALOAD,0));
    il.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,ENTITY,"method_5752","()Ljava/util/Set;",false));
    il.add(new LdcInsnNode("indivis_no_hard_despawn"));
    il.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE,"java/util/Set","contains","(Ljava/lang/Object;)Z",true));
    il.add(new JumpInsnNode(Opcodes.IFNE,no));

    // Non-hostile Dino Mounts are acquisition/tameable variants, not wild population.
    il.add(checkDino);
    il.add(new VarInsnNode(Opcodes.ALOAD,1));
    il.add(new LdcInsnNode("dino_mounts_ai:"));
    il.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,"java/lang/String","startsWith","(Ljava/lang/String;)Z",false));
    LabelNode notDino = new LabelNode();
    il.add(new JumpInsnNode(Opcodes.IFEQ,notDino));
    il.add(new VarInsnNode(Opcodes.ALOAD,1));
    il.add(new LdcInsnNode("_hostile"));
    il.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,"java/lang/String","endsWith","(Ljava/lang/String;)Z",false));
    il.add(new JumpInsnNode(Opcodes.IFEQ,no));
    il.add(notDino);

    // Colored horses may be player mounts; their ownership API is mod-specific.
    il.add(checkAutonomousHorses);
    il.add(new VarInsnNode(Opcodes.ALOAD,1));
    il.add(new LdcInsnNode("autonomous_colored_horses:"));
    il.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,"java/lang/String","startsWith","(Ljava/lang/String;)Z",false));
    il.add(new JumpInsnNode(Opcodes.IFNE,no));

    // Persistent town/constructed companions that should not be culled globally.
    il.add(checkVanillaProtected);
    String[] protectedIds = {
      "minecraft:villager",
      "minecraft:wandering_trader",
      "minecraft:iron_golem",
      "minecraft:snow_golem",
      "minecraft:allay",
      "minecraft:happy_ghast"
    };
    for (String id : protectedIds) {
      il.add(new LdcInsnNode(id));
      il.add(new VarInsnNode(Opcodes.ALOAD,1));
      il.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,"java/lang/String","equals","(Ljava/lang/Object;)Z",false));
      il.add(new JumpInsnNode(Opcodes.IFNE,no));
    }

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
        if (n instanceof LdcInsnNode ldc && ldc.cst instanceof String s && s.contains("1.2.4 runtime registered")) {
          ldc.cst = "[Capitale Creatures] 1.2.20 runtime registered: global LivingEntity hard >128 despawn with protected NPC/mount exceptions; independent land/marine spawning.";
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
          s = s.replace("\"version\": \"1.2.19\"", "\"version\": \"1.2.20\"");
          s = s.replace(
              "1.2.19 exact RegistryKey city no-spawn + patrol containment",
              "1.2.20 global living hard-despawn + exact city safety + patrol containment");
          data = s.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        }

        ZipEntry ne = new ZipEntry(name);
        ne.setTime(e.getTime());
        zos.putNextEntry(ne);
        zos.write(data);
        zos.closeEntry();
      }

      ZipEntry note = new ZipEntry("META-INF/INDIVIS_GLOBAL_LIVING_DESPAWN_1_2_20.txt");
      zos.putNextEntry(note);
      String txt =
          "Capitale Creatures 1.2.20\n" +
          "Global hard despawn >128 blocks for all LivingEntity except protected categories.\n" +
          "Exceptions: server players, armor stands, easy_npc namespace, custom-named living entities, actually-owned Tameable entities, indivis_no_hard_despawn command tag, non-hostile Dino Mounts variants, autonomous colored horses, villagers/traders/golems, allays and happy ghasts.\n" +
          "PersistenceRequired is deliberately NOT a blanket exemption.\n";
      zos.write(txt.getBytes(java.nio.charset.StandardCharsets.UTF_8));
      zos.closeEntry();
    }
  }
}
