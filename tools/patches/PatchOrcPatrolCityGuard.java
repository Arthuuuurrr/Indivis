import java.nio.file.*;
import jdk.internal.org.objectweb.asm.*;
import jdk.internal.org.objectweb.asm.tree.*;

public class PatchOrcPatrolCityGuard {
  private static final String CLASS =
      "fr/hautecapitale/creatures/spawn/CapitaleCreaturesOrcPatrol1212";
  private static final String BIOME_ID_DESC =
      "(Ljava/lang/Object;III)Ljava/lang/String;";

  private static final String[] BLOCKED = {
      "capitale:capitale",
      "capitale:donjon",
      "indivis:corruption",
      "indivis:lion_port",
      "indivis:clairval",
      "indivis:haute_rive",
      "indivis:ilystara",
      "indivis:sylvarhen",
      "indivis:avaleiv",
      "indivis:skarnfjord",
      "indivis:durak_vor",
      "minecraft:river",
      "minecraft:frozen_river",
      "minecraft:ocean",
      "minecraft:cold_ocean",
      "minecraft:lukewarm_ocean",
      "minecraft:warm_ocean",
      "minecraft:frozen_ocean",
      "minecraft:deep_ocean",
      "minecraft:deep_cold_ocean",
      "minecraft:deep_lukewarm_ocean",
      "minecraft:deep_frozen_ocean"
  };

  private static void pushInt(InsnList il, int n) {
    if (n >= 0 && n <= 5) il.add(new InsnNode(Opcodes.ICONST_0 + n));
    else if (n <= Byte.MAX_VALUE) il.add(new IntInsnNode(Opcodes.BIPUSH, n));
    else il.add(new IntInsnNode(Opcodes.SIPUSH, n));
  }

  public static void main(String[] args) throws Exception {
    if (args.length != 2) {
      throw new IllegalArgumentException("usage: input.class output.class");
    }

    ClassNode cn = new ClassNode();
    new ClassReader(Files.readAllBytes(Path.of(args[0]))).accept(cn, 0);

    if (!CLASS.equals(cn.name)) {
      throw new IllegalStateException("Unexpected class: " + cn.name);
    }

    boolean replaced = false;
    for (MethodNode mn : cn.methods) {
      if (!mn.name.equals("biomeId") || !mn.desc.equals(BIOME_ID_DESC)) continue;

      for (AbstractInsnNode insn = mn.instructions.getFirst();
           insn != null;
           insn = insn.getNext()) {
        if (insn instanceof MethodInsnNode mi
            && mi.getOpcode() == Opcodes.INVOKESTATIC
            && mi.owner.equals("java/lang/String")
            && mi.name.equals("valueOf")
            && mi.desc.equals("(Ljava/lang/Object;)Ljava/lang/String;")) {
          mi.owner = CLASS;
          mi.name = "hardNormalizeBiomeId";
          mi.desc = "(Ljava/lang/Object;)Ljava/lang/String;";
          mi.itf = false;
          replaced = true;
        }
      }
    }

    if (!replaced) {
      throw new IllegalStateException("biomeId normalization hook not found");
    }

    cn.methods.removeIf(m ->
        m.name.equals("hardNormalizeBiomeId")
            && m.desc.equals("(Ljava/lang/Object;)Ljava/lang/String;"));

    MethodNode helper = new MethodNode(
        Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC,
        "hardNormalizeBiomeId",
        "(Ljava/lang/Object;)Ljava/lang/String;",
        null,
        null);

    InsnList il = helper.instructions;
    il.add(new VarInsnNode(Opcodes.ALOAD, 0));
    il.add(new MethodInsnNode(
        Opcodes.INVOKESTATIC,
        "java/lang/String",
        "valueOf",
        "(Ljava/lang/Object;)Ljava/lang/String;",
        false));
    il.add(new VarInsnNode(Opcodes.ASTORE, 1));

    pushInt(il, BLOCKED.length);
    il.add(new TypeInsnNode(Opcodes.ANEWARRAY, "java/util/Map$Entry"));

    for (int i = 0; i < BLOCKED.length; i++) {
      il.add(new InsnNode(Opcodes.DUP));
      pushInt(il, i);
      il.add(new LdcInsnNode(BLOCKED[i]));
      il.add(new LdcInsnNode("capitale:donjon"));
      il.add(new MethodInsnNode(
          Opcodes.INVOKESTATIC,
          "java/util/Map",
          "entry",
          "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/util/Map$Entry;",
          true));
      il.add(new InsnNode(Opcodes.AASTORE));
    }

    il.add(new MethodInsnNode(
        Opcodes.INVOKESTATIC,
        "java/util/Map",
        "ofEntries",
        "([Ljava/util/Map$Entry;)Ljava/util/Map;",
        true));
    il.add(new VarInsnNode(Opcodes.ALOAD, 1));
    il.add(new VarInsnNode(Opcodes.ALOAD, 1));
    il.add(new MethodInsnNode(
        Opcodes.INVOKEINTERFACE,
        "java/util/Map",
        "getOrDefault",
        "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;",
        true));
    il.add(new TypeInsnNode(Opcodes.CHECKCAST, "java/lang/String"));
    il.add(new InsnNode(Opcodes.ARETURN));

    helper.maxLocals = 2;
    helper.maxStack = 5;
    cn.methods.add(helper);

    ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
    cn.accept(cw);
    Files.write(Path.of(args[1]), cw.toByteArray());
  }
}
