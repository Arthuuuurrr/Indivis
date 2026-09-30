import java.nio.file.*;
import jdk.internal.org.objectweb.asm.*;
import jdk.internal.org.objectweb.asm.tree.*;

public class PatchZoneMusicPlayer {
  public static void main(String[] args) throws Exception {
    Path in = Path.of(args[0]);
    Path out = Path.of(args[1]);
    ClassNode cn = new ClassNode();
    new ClassReader(Files.readAllBytes(in)).accept(cn, 0);
    boolean patched = false;
    for (MethodNode mn : cn.methods) {
      if (!mn.name.equals("selectBiomeZone")) continue;
      AbstractInsnNode[] arr = mn.instructions.toArray();
      for (int i = 0; i < arr.length; i++) {
        if (arr[i] instanceof FieldInsnNode f && f.getOpcode() == Opcodes.GETSTATIC && f.name.equals("WATER")) {
          JumpInsnNode waterIf = null;
          for (int j = i; j < Math.min(arr.length, i + 12); j++) {
            if (arr[j] instanceof JumpInsnNode ji && ji.getOpcode() == Opcodes.IFEQ) { waterIf = ji; break; }
          }
          if (waterIf == null) throw new IllegalStateException("WATER IFEQ not found");
          LabelNode end = waterIf.label;
          AbstractInsnNode cur = waterIf.getNext();
          while (cur != null && cur != end) {
            AbstractInsnNode next = cur.getNext();
            mn.instructions.remove(cur);
            cur = next;
          }
          InsnList repl = new InsnList();
          repl.add(new VarInsnNode(Opcodes.ALOAD, 6));
          repl.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/util/LinkedHashSet", "clear", "()V", false));
          for (int id : new int[]{38,39,40}) {
            repl.add(new VarInsnNode(Opcodes.ALOAD, 6));
            repl.add(new IntInsnNode(Opcodes.BIPUSH, id));
            repl.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/mmomusiczones/client/ZoneMusicPlayer", "nature", "(I)Ljava/lang/String;", false));
            repl.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/util/LinkedHashSet", "add", "(Ljava/lang/Object;)Z", false));
            repl.add(new InsnNode(Opcodes.POP));
          }
          mn.instructions.insert(waterIf, repl);
          patched = true;
          break;
        }
      }
    }
    if (!patched) throw new IllegalStateException("selectBiomeZone WATER block not patched");
    ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
    cn.accept(cw);
    Files.createDirectories(out.getParent());
    Files.write(out, cw.toByteArray());
  }
}
