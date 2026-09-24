import java.nio.file.*;
import jdk.internal.org.objectweb.asm.*;
import jdk.internal.org.objectweb.asm.tree.*;

public class PatchCityNoSpawn {
  private static final String OWNER = "fr/hautecapitale/creatures/spawn/CapitaleCreaturesDatapackSpawnController";
  private static final String PRED = "Ljava/util/function/Predicate;";

  public static void main(String[] args) throws Exception {
    if (args.length != 2) throw new IllegalArgumentException("usage input.class output.class");
    byte[] in = Files.readAllBytes(Path.of(args[0]));
    ClassNode cn = new ClassNode();
    new ClassReader(in).accept(cn, 0);
    if (!OWNER.equals(cn.name)) throw new IllegalStateException("Unexpected class " + cn.name);

    boolean patched = false;
    for (MethodNode mn : cn.methods) {
      if (!mn.name.equals("onInitialize") || !mn.desc.equals("()V")) continue;
      for (AbstractInsnNode n = mn.instructions.getFirst(); n != null; n = n.getNext()) {
        if (n instanceof LdcInsnNode ldc && "all_overworld".equals(ldc.cst)) {
          AbstractInsnNode nx = n.getNext();
          if (nx instanceof MethodInsnNode mi
              && mi.getOpcode() == Opcodes.INVOKESTATIC
              && OWNER.equals(mi.owner)
              && "tag".equals(mi.name)
              && ("(Ljava/lang/String;)" + PRED).equals(mi.desc)) {
            mi.name = "clearTargets";
            patched = true;
            break;
          }
        }
      }
    }
    if (!patched) throw new IllegalStateException("clear selector hook not found");

    cn.methods.removeIf(m -> m.name.equals("clearTargets") && m.desc.equals("(Ljava/lang/String;)" + PRED));
    MethodNode helper = new MethodNode(
        Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC,
        "clearTargets",
        "(Ljava/lang/String;)" + PRED,
        null,
        null);
    InsnList il = helper.instructions;
    il.add(new FieldInsnNode(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;"));
    il.add(new LdcInsnNode("[Capitale Creatures] 1.2.18 city no-spawn guard active: #capitale_creatures:city_no_spawn"));
    il.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false));
    il.add(new LdcInsnNode("all_overworld"));
    il.add(new MethodInsnNode(Opcodes.INVOKESTATIC, OWNER, "tag", "(Ljava/lang/String;)" + PRED, false));
    il.add(new LdcInsnNode("city_no_spawn"));
    il.add(new MethodInsnNode(Opcodes.INVOKESTATIC, OWNER, "tag", "(Ljava/lang/String;)" + PRED, false));
    il.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, "java/util/function/Predicate", "or", "(Ljava/util/function/Predicate;)Ljava/util/function/Predicate;", true));
    il.add(new InsnNode(Opcodes.ARETURN));
    helper.maxStack = 2;
    helper.maxLocals = 1;
    cn.methods.add(helper);

    ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
    cn.accept(cw);
    Files.write(Path.of(args[1]), cw.toByteArray());
  }
}
