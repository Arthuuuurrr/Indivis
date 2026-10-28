import java.nio.file.*;
import java.util.*;
import java.util.zip.*;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;

/** Preserve OPT1's method as an oracle and redirect only the list transform. */
public final class PatchOpt2 implements Opcodes {
    static final String P="net/tompsen/nexuscharacters/";
    public static void main(String[] args)throws Exception {
        Path out=Path.of(args[1]);int changed=0;
        try(ZipFile z=new ZipFile(args[0])) {
            for(String name:List.of("SurfaceGeometry")) {
                ClassNode c=new ClassNode(ASM9);
                new ClassReader(z.getInputStream(z.getEntry(P+name+".class"))).accept(c,0);
                String desc="(Ljava/util/List;Lorg/joml/Matrix4f;)Ljava/util/List;";
                MethodNode original=c.methods.stream().filter(m->m.name.equals("transform")&&m.desc.equals(desc)).findFirst().orElseThrow();
                original.name="transformOPT1";
                MethodNode delegate=new MethodNode(ASM9,original.access,"transform",desc,original.signature,null);
                delegate.instructions.add(new VarInsnNode(ALOAD,0));delegate.instructions.add(new VarInsnNode(ALOAD,1));
                delegate.instructions.add(new MethodInsnNode(INVOKESTATIC,P+"ExactTransformSupport","transform",desc,false));
                delegate.instructions.add(new InsnNode(ARETURN));c.methods.add(delegate);changed++;
                ClassWriter writer=new ClassWriter(ClassWriter.COMPUTE_MAXS);c.accept(writer);
                Path f=out.resolve(P+name+".class");Files.createDirectories(f.getParent());Files.write(f,writer.toByteArray());
            }
        }
        if(changed!=1)throw new AssertionError(changed);
        System.out.println("OPT2_TRANSFORM_PATCH_PASS delegates="+changed);
    }
}

