import java.nio.file.*;
import java.util.zip.*;
import jdk.internal.org.objectweb.asm.*;
import jdk.internal.org.objectweb.asm.tree.*;
import jdk.internal.org.objectweb.asm.tree.analysis.*;
public final class VerifyClasses {
 public static void main(String[] args)throws Exception{
  int classes=0,methods=0;try(ZipFile zip=new ZipFile(args[0])){
   var entries=zip.entries();while(entries.hasMoreElements()){
    var e=entries.nextElement();if(!e.getName().endsWith(".class"))continue;byte[] b=zip.getInputStream(e).readAllBytes();b[6]=0;b[7]=61;ClassNode c=new ClassNode();new ClassReader(b).accept(c,0);
    if(c.name.startsWith("net/hautecapitale/quetes/pq01/mixin/")){
      if(c.invisibleAnnotations==null||c.invisibleAnnotations.stream().noneMatch(a->a.desc.equals("Lorg/spongepowered/asm/mixin/Mixin;")))throw new AssertionError("Missing class-retained @Mixin: "+c.name);
      if(c.visibleAnnotations!=null&&c.visibleAnnotations.stream().anyMatch(a->a.desc.equals("Lorg/spongepowered/asm/mixin/Mixin;")))throw new AssertionError("Wrong @Mixin retention: "+c.name);
      for(MethodNode m:c.methods)if(m.visibleAnnotations!=null)for(AnnotationNode a:m.visibleAnnotations){
        if(a.desc.equals("Lorg/spongepowered/asm/mixin/injection/Inject;")){
          int at=a.values.indexOf("at");
          if(at<0||!(a.values.get(at+1) instanceof java.util.List<?> points)||points.isEmpty()||!(points.get(0) instanceof AnnotationNode))throw new AssertionError("Inject.at must be At[]: "+c.name+"."+m.name);
        }
      }
    }
    for(MethodNode m:c.methods){if(m.instructions.size()==0)continue;new Analyzer<BasicValue>(new BasicVerifier()).analyze(c.name,m);methods++;}classes++;
   }
  }System.out.println("PASS ASM BasicVerifier: "+classes+" classes, "+methods+" methods; no Minecraft API resolution.");
 }
}
