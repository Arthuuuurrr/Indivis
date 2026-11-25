import java.nio.file.*;import java.util.*;import java.util.zip.*;import org.objectweb.asm.*;import org.objectweb.asm.tree.*;
public final class PatchArmorPrepared implements Opcodes{
 static final String P="net/tompsen/nexuscharacters/";
 public static void main(String[] args)throws Exception{
  try(var jar=new ZipFile(args[0])){for(String name:List.of("PosedSurfaceSupport","NativeVoxelSurface")){
   var c=new ClassNode();new ClassReader(jar.getInputStream(jar.getEntry(P+name+".class"))).accept(c,0);
   if(name.equals("PosedSurfaceSupport")){
    var m=c.methods.stream().filter(v->v.name.equals("applyPRE30")).findFirst().orElseThrow();int forward=0,clean=0,output=0;
    for(var n:m.instructions.toArray())if(n instanceof MethodInsnNode call){
     if(call.owner.equals(P+"SurfaceGeometry")&&call.name.equals("transform")){call.owner=P+"ArmorPreparedSurface";forward++;}
     else if(call.owner.equals(P+"PackedSurfaceOutput")&&call.name.equals("write")){call.owner=P+"ArmorPreparedSurface";output++;}
     else if(call.owner.equals(P+"PosedSurfaceSupport")&&call.name.equals("clean")){call.owner=P+"ArmorPreparedSurface";clean++;}
    }
    if(forward!=2||output!=2||clean!=3)throw new AssertionError("Unexpected OPT1 world pipeline "+forward+"/"+output+"/"+clean);
   }else{
    var m=c.methods.stream().filter(v->v.name.equals("clear")).findFirst().orElseThrow();for(var n:m.instructions.toArray())if(n.getOpcode()==RETURN)m.instructions.insertBefore(n,new MethodInsnNode(INVOKESTATIC,P+"ArmorPreparedSurface","clear","()V",false));
   }
   var writer=new ClassWriter(ClassWriter.COMPUTE_MAXS);c.accept(writer);var path=Path.of(args[1]).resolve(c.name+".class");Files.createDirectories(path.getParent());Files.write(path,writer.toByteArray());
  }}System.out.println("ARMOR_PREPARED_PATCH_PASS classes=2");
 }
}
