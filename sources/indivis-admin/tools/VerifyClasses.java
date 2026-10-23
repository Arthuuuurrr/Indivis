import java.nio.file.*;
import jdk.internal.org.objectweb.asm.*;
import jdk.internal.org.objectweb.asm.tree.*;
import jdk.internal.org.objectweb.asm.tree.analysis.*;

public final class VerifyClasses {
  public static void main(String[] args)throws Exception {
    int classes=0,methods=0;
    for(String dir:args)try(var paths=Files.walk(Path.of(dir))) {
      for(Path path:paths.filter(p->p.toString().endsWith(".class")).toList()) {
        byte[] bytes=Files.readAllBytes(path);bytes[6]=0;bytes[7]=61;
        ClassNode node=new ClassNode();new ClassReader(bytes).accept(node,0);
        if(node.name.equals("net/indivis/admin/mixin/NexusProfileSaveMixin")){
          if(node.invisibleAnnotations==null||node.invisibleAnnotations.stream().noneMatch(a->a.desc.equals("Lorg/spongepowered/asm/mixin/Mixin;")))throw new AssertionError("Missing class-retained @Mixin: "+node.name);
          if(node.visibleAnnotations!=null&&node.visibleAnnotations.stream().anyMatch(a->a.desc.equals("Lorg/spongepowered/asm/mixin/Mixin;")))throw new AssertionError("Wrong @Mixin retention: "+node.name);
          for(MethodNode m:node.methods)if(m.visibleAnnotations!=null)for(AnnotationNode a:m.visibleAnnotations){
            if(a.desc.equals("Lorg/spongepowered/asm/mixin/injection/ModifyVariable;")){
              int at=a.values.indexOf("at");
              if(at<0||!(a.values.get(at+1) instanceof java.util.List<?> points)||points.isEmpty()||!(points.get(0) instanceof AnnotationNode))throw new AssertionError("ModifyVariable.at must be At[]: "+node.name+"."+m.name);
            }
          }
        }
        for(MethodNode method:node.methods)if((method.access&(Opcodes.ACC_ABSTRACT|Opcodes.ACC_NATIVE))==0) {
          new Analyzer<BasicValue>(new BasicVerifier()).analyze(node.name,method);methods++;
        }
        classes++;
      }
    }
    System.out.println("ASM_BASIC_VERIFIER classes="+classes+" methods="+methods+" errors=0 (sans résolution des API Minecraft)");
  }
}
