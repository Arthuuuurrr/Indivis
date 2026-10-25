import java.nio.file.*;
import java.util.*;
import java.util.zip.*;
import jdk.internal.org.objectweb.asm.*;
import jdk.internal.org.objectweb.asm.tree.*;

public final class Patch35 implements Opcodes {
  static final String P="net/tompsen/nexuscharacters/";
  static final String SUB="(L"+P+"SurfaceGeometry$Polygon;L"+P+"SurfaceGeometry$Box;Z)Ljava/util/List;";
  static final String CLEAN="(Ljava/util/List;L"+P+"PosedSurfaceSupport$Grid;IZZZ)Ljava/util/List;";
  public static void main(String[] args)throws Exception {
    boolean oracle=args.length>2; int changed=0,aliases=0;
    Path out=Path.of(args[1]);
    try(ZipFile z=new ZipFile(args[0])) {
      for(String type:List.of("SurfaceGeometry","PosedSurfaceSupport")) {
        ClassNode c=new ClassNode(ASM8);
        new ClassReader(z.getInputStream(z.getEntry(P+type+".class"))).accept(c,0);
        if(oracle) {
          List<MethodNode> originals=new ArrayList<>(c.methods);
          for(MethodNode m:originals) if(type.equals("SurfaceGeometry")&&(m.name.equals("subtract")||m.name.equals("subtractConnected")) || type.equals("PosedSurfaceSupport")&&m.name.equals("clean")) {
            MethodNode copy=new MethodNode(ASM8,ACC_PUBLIC|ACC_STATIC,m.name.equals("clean")?"referenceClean":m.name.equals("subtractConnected")?"referenceSubtractConnected":"referenceSubtract",m.desc,m.signature,m.exceptions.toArray(String[]::new));
            m.accept(copy);
            for(AbstractInsnNode i:copy.instructions) if(i instanceof MethodInsnNode n&&n.owner.equals(P+"SurfaceGeometry")) {
              if(n.name.equals("subtract"))n.name="referenceSubtract";
              if(n.name.equals("subtractConnected"))n.name="referenceSubtractConnected";
            }
            c.methods.add(copy); aliases++;
          }
        }
        for(MethodNode m:c.methods) {
          boolean sub=type.equals("SurfaceGeometry")&&m.name.equals("subtract")&&m.desc.equals(SUB);
          boolean clean=type.equals("PosedSurfaceSupport")&&m.name.equals("clean")&&m.desc.equals(CLEAN);
          if(!sub&&!clean)continue;
          m.instructions.clear();m.tryCatchBlocks.clear();
          if(m.localVariables!=null)m.localVariables.clear();
          m.visibleLocalVariableAnnotations=null;m.invisibleLocalVariableAnnotations=null;
          m.instructions.add(new VarInsnNode(ALOAD,0));m.instructions.add(new VarInsnNode(ALOAD,1));
          for(int at=2;at<(sub?3:6);at++)m.instructions.add(new VarInsnNode(ILOAD,at));
          m.instructions.add(new MethodInsnNode(INVOKESTATIC,P+(sub?"ExactSurfaceClipper":oracle?"SurfaceComparisonOracle":"ExactSurfaceCleaner"),sub?"subtract":"clean",m.desc,false));
          m.instructions.add(new InsnNode(ARETURN));changed++;
        }
        ClassWriter w=new ClassWriter(ClassWriter.COMPUTE_MAXS);c.accept(w);
        Path dest=out.resolve(P+type+".class");Files.createDirectories(dest.getParent());Files.write(dest,w.toByteArray());
      }
    }
    if(changed!=2||aliases!=(oracle?4:0))throw new AssertionError(changed+"/"+aliases);
    System.out.println("PATCH35_PASS delegates="+changed+" QAaliases="+aliases);
  }
}
