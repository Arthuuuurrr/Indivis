import java.nio.file.*;
import java.util.zip.*;
import org.objectweb.asm.*;

/** Correct the inherited PRE17 call signature without rebuilding or changing the dwarf model. */
public final class FixFeatureDescriptors {
  public static void main(String[] args) throws Exception {
    try (ZipFile base = new ZipFile(args[0])) {
      String name = "net/tompsen/nexuscharacters/DwarfNoseFeatureRenderer.class";
      byte[] bytes = base.getInputStream(base.getEntry(name)).readAllBytes();
      ClassReader reader = new ClassReader(bytes);
      ClassWriter writer = new ClassWriter(0);
      int[] count = {0};
      reader.accept(
          new ClassVisitor(Opcodes.ASM9, writer) {
            public MethodVisitor visitMethod(
                int access, String n, String d, String signature, String[] exceptions) {
              return new MethodVisitor(
                  Opcodes.ASM9, super.visitMethod(access, n, d, signature, exceptions)) {
                public void visitMethodInsn(
                    int opcode, String owner, String method, String desc, boolean itf) {
                  if (method.equals("method_17165") && desc.equals("()Ljava/lang/Object;")) {
                    desc = "()Lnet/minecraft/class_583;";
                    count[0]++;
                  }
                  super.visitMethodInsn(opcode, owner, method, desc, itf);
                }
              };
            }
          },
          0);
      if (count[0] != 1)
        throw new AssertionError("Unexpected dwarf descriptor patches " + count[0]);
      Path target = Path.of(args[1], name);
      Files.createDirectories(target.getParent());
      Files.write(target, writer.toByteArray());
      System.out.println("DWARF_SIGNATURE_PATCH_PASS calls=" + count[0]);
    }
  }
}

