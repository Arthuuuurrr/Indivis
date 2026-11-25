import java.nio.file.*;import java.io.*;import java.util.*;import java.util.zip.*;
import org.objectweb.asm.*;import org.objectweb.asm.tree.*;import org.objectweb.asm.util.*;
public final class AuditArmorPrepared{
 static String body(MethodNode m){var p=new Textifier();m.accept(new TraceMethodVisitor(p));var s=new StringWriter();p.print(new PrintWriter(s));return s.toString();}
 static ClassNode cls(ZipFile z,String n)throws Exception{var c=new ClassNode();new ClassReader(z.getInputStream(z.getEntry(n))).accept(c,ClassReader.SKIP_DEBUG);return c;}
 public static void main(String[]a)throws Exception{
  int unchanged=0,preserved=0,targeted=0,assets=0;
  try(var old=new ZipFile(a[0]);var now=new ZipFile(a[1])){
   for(var e:Collections.list(old.entries())){String n=e.getName();byte[]x=old.getInputStream(e).readAllBytes(),y=now.getInputStream(now.getEntry(n)).readAllBytes();
    if(n.startsWith("assets/")){if(!Arrays.equals(x,y))throw new AssertionError("Asset changed "+n);assets++;}
    if(!n.endsWith(".class"))continue;if(Arrays.equals(x,y)){unchanged++;continue;}
    var before=cls(old,n);var after=cls(now,n);
    if(!before.name.endsWith("/PosedSurfaceSupport")&&!before.name.endsWith("/NativeVoxelSurface"))throw new AssertionError("Unexpected patched class "+n);
    for(var m:before.methods){
     if(before.name.endsWith("/PosedSurfaceSupport")&&m.name.equals("applyPRE30")||before.name.endsWith("/NativeVoxelSurface")&&m.name.equals("clear")){targeted++;continue;}
     var match=after.methods.stream().filter(v->v.name.equals(m.name)&&v.desc.equals(m.desc)).findFirst().orElseThrow();if(!body(m).equals(body(match)))throw new AssertionError("Unexpected method change "+n+"/"+m.name);preserved++;
    }
   }
   if(targeted!=2)throw new AssertionError("Unexpected targeted method count "+targeted);
   for(var e:Collections.list(now.entries()))if(e.getName().contains("perfqa/")||e.getName().contains("freezeqa/")||e.getName().contains("npctest/"))throw new AssertionError("Test harness in mod "+e.getName());
  }
  String text="ARMOR_BYTECODE_AUDIT_PASS unchanged_classes="+unchanged+" preserved_method_bodies="+preserved+" targeted_methods="+targeted+" identical_assets="+assets+" no_test_harness=true\n";Files.writeString(Path.of(a[2]),text);System.out.print(text);
 }
}
