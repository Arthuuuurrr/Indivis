import java.lang.annotation.*;
import java.util.*;
import java.util.zip.*;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.util.Annotations;

/** Utilise le vrai lecteur d'annotations Mixin 0.8.7 du serveur, sans démarrer le jeu. */
public final class VerifyMixinAnnotations {
  public static void main(String[] args)throws Exception{
    if(Mixin.class.getAnnotation(Retention.class).value()!=RetentionPolicy.CLASS)throw new AssertionError("Unexpected Mixin API retention");
    for(int i=0;i<args.length;i+=2){
      int expected=Integer.parseInt(args[i]),missing=0,headers=0,injections=0,badAt=0;
      try(ZipFile zip=new ZipFile(args[i+1])){
        var entries=zip.entries();while(entries.hasMoreElements()){
          var entry=entries.nextElement();if(!entry.getName().endsWith(".class"))continue;
          ClassNode node=new ClassNode();new ClassReader(zip.getInputStream(entry).readAllBytes()).accept(node,0);
          AnnotationNode invisible=Annotations.getInvisible(node,Mixin.class),visible=Annotations.getVisible(node,Mixin.class);
          boolean ours=node.name.startsWith("net/hautecapitale/quetes/pq01/mixin/")||node.name.equals("net/indivis/admin/mixin/NexusProfileSaveMixin");
          if(invisible!=null||visible!=null||ours){
            headers++;
            // Même appel que MixinInfo.readPriority, à l'origine du crash transmis.
            if(invisible==null){missing++;System.out.println("REPRO missing @Mixin: "+node.name+" (visible="+(visible!=null)+")");}
          }
          if(ours)for(MethodNode method:node.methods){
            AnnotationNode inject=Annotations.getVisible(method,Inject.class),modify=Annotations.getVisible(method,ModifyVariable.class);
            if(inject!=null||modify!=null)injections++;
            if(inject!=null||modify!=null){
              Object at=Annotations.getValue(inject!=null?inject:modify,"at");
              if(!(at instanceof List<?> list)||list.size()!=1||!(list.get(0) instanceof AnnotationNode point)||!"HEAD".equals(Annotations.getValue(point,"value"))){
                badAt++;System.out.println("REPRO malformed injection at: "+node.name+"."+method.name);
              }
            }
          }
        }
      }
      if(missing!=expected)throw new AssertionError(args[i+1]+" missing="+missing+" expected="+expected);
      if(badAt!=(expected==2?5:expected==1?1:0))throw new AssertionError(args[i+1]+" malformed injection at="+badAt);
      System.out.println("PASS "+args[i+1]+": headers="+headers+", missing="+missing+", injectionHandlers="+injections+", malformedInjectionAt="+badAt+", real Mixin Annotations.getInvisible");
    }
    System.out.println("No Minecraft/Fabric launch or full mixin application performed by this reader test.");
  }
}
