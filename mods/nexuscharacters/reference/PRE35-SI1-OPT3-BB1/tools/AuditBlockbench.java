import java.nio.file.*;
import java.io.*;
import java.util.*;
import java.util.zip.*;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;
import org.objectweb.asm.util.*;
/** Symbolic instruction audit: no constant-pool or ZIP-order false positives. */
public final class AuditBlockbench {
 static String body(MethodNode m){Textifier p=new Textifier();m.accept(new TraceMethodVisitor(p));var out=new StringWriter();p.print(new PrintWriter(out));return out.toString();}
 static ClassNode cls(ZipFile z,String n)throws Exception{ClassNode c=new ClassNode();new ClassReader(z.getInputStream(z.getEntry(n))).accept(c,ClassReader.SKIP_DEBUG);return c;}
 static boolean expected(String c,MethodNode m){
  if(c.endsWith("/CulturalBeardSupport")&&m.name.equals("<clinit>"))return true;
  if(c.endsWith("/UnifiedPilositySupport")&&m.name.equals("<clinit>"))return true;
  if(c.endsWith("/DynamicAssetCatalog")&&m.name.equals("clear"))return true;
  if(c.endsWith("/CharacterCosmeticFeatureRenderer")&&m.name.equals("render"))return true;
  if(c.endsWith("/CharacterPreviewRenderer")&&m.name.equals("drawWorldless"))return true;
  if(c.endsWith("/CharacterCreationAppearance65Mixin")){
   if(m.name.equals("nexuscharacters$normalizeForRace")||m.name.equals("nexuscharacters$refresh"))return true;
   for(var insn:m.instructions)if(insn instanceof MethodInsnNode call&&call.owner.endsWith("/DynamicAssetCatalog")&&call.name.equals("nextHair"))return true;
  }
  return false;
 }
 public static void main(String[] a)throws Exception{
  int classes=0,unchanged=0,retained=0,targeted=0;
  try(ZipFile old=new ZipFile(a[0]);ZipFile now=new ZipFile(a[1])){
   for(var entry:Collections.list(old.entries())){
    String n=entry.getName();if(!n.endsWith(".class"))continue;
    byte[] x=old.getInputStream(entry).readAllBytes(),y=now.getInputStream(now.getEntry(n)).readAllBytes();
    if(Arrays.equals(x,y)){unchanged++;continue;}classes++;
    ClassNode before=cls(old,n),after=cls(now,n);
    for(MethodNode m:before.methods){
     if(expected(before.name,m)){targeted++;continue;}
     MethodNode match=after.methods.stream().filter(t->t.desc.equals(m.desc)&&t.name.equals(m.name+"OPT3")).findFirst().orElseGet(()->after.methods.stream().filter(t->t.desc.equals(m.desc)&&t.name.equals(m.name)).findFirst().orElseThrow());
     if(!body(m).equals(body(match)))throw new AssertionError("Unexpected original method change: "+before.name+"."+m.name+m.desc);
     retained++;
    }
   }
  }
  String json="{\"unchanged_classes\":"+unchanged+",\"patched_classes\":"+classes+",\"preserved_method_bodies\":"+retained+",\"targeted_additions\":"+targeted+"}\n";
  Files.writeString(Path.of(a[2]),json);System.out.print("BB_BYTECODE_AUDIT_PASS "+json);
 }
}
