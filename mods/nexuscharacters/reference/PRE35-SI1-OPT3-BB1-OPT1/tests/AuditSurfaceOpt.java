import java.nio.file.*;import java.io.*;import java.util.*;import java.util.zip.*;
import org.objectweb.asm.*;import org.objectweb.asm.tree.*;import org.objectweb.asm.util.*;
/** Reject any accidental alteration outside the explicit optimization surface. */
public final class AuditSurfaceOpt{
 static String body(MethodNode m){var p=new Textifier();m.accept(new TraceMethodVisitor(p));var out=new StringWriter();p.print(new PrintWriter(out));return out.toString();}
 static ClassNode cls(ZipFile z,String n)throws Exception{var c=new ClassNode();new ClassReader(z.getInputStream(z.getEntry(n))).accept(c,ClassReader.SKIP_DEBUG);return c;}
 public static void main(String[]a)throws Exception{
  int preserved=0,unchanged=0,targeted=0,assets=0;
  try(var old=new ZipFile(a[0]);var now=new ZipFile(a[1])){
   for(var e:Collections.list(old.entries())){String n=e.getName();byte[]before=old.getInputStream(e).readAllBytes(),after=now.getInputStream(now.getEntry(n)).readAllBytes();
    if(n.startsWith("assets/")){if(!Arrays.equals(before,after))throw new AssertionError("Asset changed "+n);assets++;}
    if(!n.endsWith(".class"))continue;if(Arrays.equals(before,after)){unchanged++;continue;}
    var x=cls(old,n);var y=cls(now,n);
    if(x.name.contains("/PartSurfaceReuse")){targeted++;continue;}
    for(var m:x.methods){
     if(x.name.endsWith("/PosedSurfaceSupport")&&Set.of("applyPRE30","clearPRE30").contains(m.name)||x.name.endsWith("/NativeVoxelSurface")&&m.name.equals("clear")){targeted++;continue;}
     String name=m.name;if(x.name.endsWith("/CompactSurfaceClipper")&&name.equals("intersecting")||x.name.endsWith("/SpatialSurfaceCleaner")&&name.equals("cleanIndexed"))name+="BB1";
     String expected=name;var match=y.methods.stream().filter(v->v.name.equals(expected)&&v.desc.equals(m.desc)).findFirst().orElseThrow();
     if(!body(m).equals(body(match)))throw new AssertionError("Unexpected method alteration "+x.name+"."+m.name+m.desc);preserved++;
    }
   }
   for(var e:Collections.list(now.entries()))if(e.getName().contains("freezeqa")||e.getName().contains("perfqa")||e.getName().contains("npctest"))throw new AssertionError("QA harness leaked "+e.getName());
  }
  String result="SURFACE_BYTECODE_AUDIT_PASS unchanged_classes="+unchanged+" preserved_method_bodies="+preserved+" targeted="+targeted+" identical_assets="+assets+" no_test_harness=true\n";
  Files.writeString(Path.of(a[2]),result);System.out.print(result);
 }
}
