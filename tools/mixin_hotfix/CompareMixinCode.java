import java.util.*;
import java.util.zip.*;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;

/** Compare le code et les sélecteurs en normalisant les deux défauts d'annotations. */
public final class CompareMixinCode {
  static byte[] normalized(byte[] bytes) {
    ClassNode n=new ClassNode();new ClassReader(bytes).accept(n,0);
    if(n.visibleAnnotations!=null)n.visibleAnnotations.removeIf(a->a.desc.equals("Lorg/spongepowered/asm/mixin/Mixin;"));
    if(n.invisibleAnnotations!=null)n.invisibleAnnotations.removeIf(a->a.desc.equals("Lorg/spongepowered/asm/mixin/Mixin;"));
    for(MethodNode m:n.methods)if(m.visibleAnnotations!=null)for(AnnotationNode a:m.visibleAnnotations){
      if((a.desc.equals("Lorg/spongepowered/asm/mixin/injection/Inject;")||a.desc.equals("Lorg/spongepowered/asm/mixin/injection/ModifyVariable;"))&&a.values!=null){
        for(int i=0;i<a.values.size();i+=2)if(a.values.get(i).equals("at")&&a.values.get(i+1) instanceof AnnotationNode){
          // L'ancien contrat déclarait At au lieu de At[] : même point HEAD,
          // représentation désormais conforme à la véritable API Mixin.
          a.values.set(i+1,new ArrayList<>(List.of(a.values.get(i+1))));
        }
      }
    }
    ClassWriter w=new ClassWriter(0);n.accept(w);return w.toByteArray();
  }
  public static void main(String[] args)throws Exception {
    try(ZipFile before=new ZipFile(args[0]);ZipFile after=new ZipFile(args[1])){
      for(int i=2;i<args.length;i++){
        String path=args[i];
        byte[] a=before.getInputStream(before.getEntry(path)).readAllBytes(),b=after.getInputStream(after.getEntry(path)).readAllBytes();
        if(!Arrays.equals(normalized(a),normalized(b)))throw new AssertionError("Other mixin content changed: "+path);
        System.out.println("PASS identical mixin code and selectors after normalizing @Mixin retention and injection at array: "+path);
      }
    }
  }
}
