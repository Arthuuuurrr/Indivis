import java.nio.file.*;
import java.util.*;
import jdk.internal.org.objectweb.asm.*;

public class PatchSpellIcons {
  static final int ASM = Opcodes.ASM8;

  static class Entry {
    final String spell, namespace, path;
    Entry(String spell, String texture) {
      this.spell = spell;
      int i = texture.indexOf(':');
      this.namespace = texture.substring(0, i);
      this.path = texture.substring(i + 1);
    }
  }

  public static void main(String[] args) throws Exception {
    Path spellRenderIn = Path.of(args[0]);
    Path mapTsv = Path.of(args[1]);
    Path spellRenderOut = Path.of(args[2]);
    Path helperOut = Path.of(args[3]);

    List<Entry> entries = new ArrayList<>();
    for (String line : Files.readAllLines(mapTsv)) {
      if (line.isBlank()) continue;
      String[] p = line.split("\\t", 2);
      entries.add(new Entry(p[0], p[1]));
    }

    byte[] bytes = Files.readAllBytes(spellRenderIn);
    ClassReader cr = new ClassReader(bytes);
    ClassWriter cw = new ClassWriter(0);
    ClassVisitor cv = new ClassVisitor(ASM, cw) {
      @Override public MethodVisitor visitMethod(int access, String name, String desc, String sig, String[] ex) {
        if (name.equals("iconTexture") && desc.equals("(Lnet/minecraft/class_2960;)Lnet/minecraft/class_2960;")) {
          MethodVisitor mv = super.visitMethod(access, name, desc, sig, ex);
          mv.visitCode();
          mv.visitVarInsn(Opcodes.ALOAD, 0);
          mv.visitMethodInsn(Opcodes.INVOKESTATIC,
              "net/spell_engine/client/util/HcSpellIconFallback", "resolve",
              "(Lnet/minecraft/class_2960;)Lnet/minecraft/class_2960;", false);
          mv.visitInsn(Opcodes.ARETURN);
          mv.visitMaxs(1,1);
          mv.visitEnd();
          return null;
        }
        return super.visitMethod(access, name, desc, sig, ex);
      }
    };
    cr.accept(cv, 0);
    Files.write(spellRenderOut, cw.toByteArray());

    ClassWriter h = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
    h.visit(Opcodes.V21, Opcodes.ACC_PUBLIC | Opcodes.ACC_FINAL | Opcodes.ACC_SUPER,
        "net/spell_engine/client/util/HcSpellIconFallback", null, "java/lang/Object", null);

    MethodVisitor init = h.visitMethod(Opcodes.ACC_PRIVATE, "<init>", "()V", null, null);
    init.visitCode();
    init.visitVarInsn(Opcodes.ALOAD,0);
    init.visitMethodInsn(Opcodes.INVOKESPECIAL,"java/lang/Object","<init>","()V",false);
    init.visitInsn(Opcodes.RETURN);
    init.visitMaxs(0,0); init.visitEnd();

    MethodVisitor mv = h.visitMethod(Opcodes.ACC_PUBLIC|Opcodes.ACC_STATIC, "resolve",
        "(Lnet/minecraft/class_2960;)Lnet/minecraft/class_2960;", null, null);
    mv.visitCode();
    Label nonNull = new Label();
    mv.visitVarInsn(Opcodes.ALOAD,0);
    mv.visitJumpInsn(Opcodes.IFNONNULL, nonNull);
    mv.visitInsn(Opcodes.ACONST_NULL);
    mv.visitInsn(Opcodes.ARETURN);
    mv.visitLabel(nonNull);

    mv.visitVarInsn(Opcodes.ALOAD,0);
    mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL,"java/lang/Object","toString","()Ljava/lang/String;",false);
    mv.visitVarInsn(Opcodes.ASTORE,1);

    for (Entry e : entries) {
      Label next = new Label();
      mv.visitVarInsn(Opcodes.ALOAD,1);
      mv.visitLdcInsn(e.spell);
      mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL,"java/lang/String","equals","(Ljava/lang/Object;)Z",false);
      mv.visitJumpInsn(Opcodes.IFEQ,next);
      mv.visitLdcInsn(e.namespace);
      mv.visitLdcInsn(e.path);
      mv.visitMethodInsn(Opcodes.INVOKESTATIC,"net/minecraft/class_2960","method_60655",
          "(Ljava/lang/String;Ljava/lang/String;)Lnet/minecraft/class_2960;",false);
      mv.visitInsn(Opcodes.ARETURN);
      mv.visitLabel(next);
    }

    mv.visitVarInsn(Opcodes.ALOAD,0);
    mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL,"net/minecraft/class_2960","method_12836","()Ljava/lang/String;",false);
    mv.visitTypeInsn(Opcodes.NEW,"java/lang/StringBuilder");
    mv.visitInsn(Opcodes.DUP);
    mv.visitLdcInsn("textures/spell/");
    mv.visitMethodInsn(Opcodes.INVOKESPECIAL,"java/lang/StringBuilder","<init>","(Ljava/lang/String;)V",false);
    mv.visitVarInsn(Opcodes.ALOAD,0);
    mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL,"net/minecraft/class_2960","method_12832","()Ljava/lang/String;",false);
    mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL,"java/lang/StringBuilder","append","(Ljava/lang/String;)Ljava/lang/StringBuilder;",false);
    mv.visitLdcInsn(".png");
    mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL,"java/lang/StringBuilder","append","(Ljava/lang/String;)Ljava/lang/StringBuilder;",false);
    mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL,"java/lang/StringBuilder","toString","()Ljava/lang/String;",false);
    mv.visitMethodInsn(Opcodes.INVOKESTATIC,"net/minecraft/class_2960","method_60655",
        "(Ljava/lang/String;Ljava/lang/String;)Lnet/minecraft/class_2960;",false);
    mv.visitInsn(Opcodes.ARETURN);
    mv.visitMaxs(0,0); mv.visitEnd();
    h.visitEnd();
    Files.write(helperOut,h.toByteArray());
  }
}
