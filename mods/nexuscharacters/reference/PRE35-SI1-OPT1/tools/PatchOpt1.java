import java.nio.file.*;
import java.util.*;
import java.util.zip.*;
import jdk.internal.org.objectweb.asm.*;
import jdk.internal.org.objectweb.asm.tree.*;

/** Two strictly-scoped changes to the verified PRE35 base. */
public final class PatchOpt1 implements Opcodes {
 static final String P="net/tompsen/nexuscharacters/";
 public static void main(String[] args)throws Exception {
  Path out=Path.of(args[1]);int delegates=0,invalidations=0;
  try(ZipFile z=new ZipFile(args[0])) {
   for(String name:List.of("ExactSurfaceCleaner","PosedSurfaceSupport$Grid")) {
    ClassNode c=new ClassNode(ASM8);new ClassReader(z.getInputStream(z.getEntry(P+name+".class"))).accept(c,0);
    if(name.equals("ExactSurfaceCleaner")) {
     MethodNode original=c.methods.stream().filter(m->m.name.equals("clean")).findFirst().orElseThrow();
     original.name="cleanPRE35";
     MethodNode delegate=new MethodNode(ASM8,ACC_PUBLIC|ACC_STATIC,"clean",original.desc,null,null);
     delegate.instructions.add(new VarInsnNode(ALOAD,0));delegate.instructions.add(new VarInsnNode(ALOAD,1));
     for(int i=2;i<6;i++)delegate.instructions.add(new VarInsnNode(ILOAD,i));
     delegate.instructions.add(new MethodInsnNode(INVOKESTATIC,P+"SpatialSurfaceCleaner","clean",original.desc,false));
     delegate.instructions.add(new InsnNode(ARETURN));c.methods.add(delegate);delegates++;
    } else {
     c.fields.add(new FieldNode(ACC_PUBLIC,"opt1Index","L"+P+"SpatialSurfaceCleaner$Index;",null,null));
     for(MethodNode m:c.methods)if(m.name.equals("add")) {
      InsnList clear=new InsnList();clear.add(new VarInsnNode(ALOAD,0));clear.add(new InsnNode(ACONST_NULL));
      clear.add(new FieldInsnNode(PUTFIELD,P+name,"opt1Index","L"+P+"SpatialSurfaceCleaner$Index;"));
      m.instructions.insert(clear);invalidations++;
     }
    }
    ClassWriter w=new ClassWriter(ClassWriter.COMPUTE_MAXS);c.accept(w);
    Path f=out.resolve(P+name+".class");Files.createDirectories(f.getParent());Files.write(f,w.toByteArray());
   }
  }
  if(delegates!=1||invalidations!=1)throw new AssertionError(delegates+"/"+invalidations);
  System.out.println("PATCH_OPT1_PASS delegates="+delegates+" invalidations="+invalidations);
 }
}
