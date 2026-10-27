import java.nio.file.*;
import java.util.zip.*;
import org.objectweb.asm.*;
/** Retire uniquement l'annotation Fabric CLIENT du banc serveur, jamais du livrable. */
public final class StripClientEnvironment {
  public static void main(String[] args)throws Exception {
    try(ZipFile zip=new ZipFile(args[0])){
      for(ZipEntry e:java.util.Collections.list(zip.entries())){
        if(!e.getName().startsWith("net/hautecapitale/dialogue/")||!e.getName().endsWith(".class"))continue;
        Path dest=Path.of(args[1],e.getName());if(Files.exists(dest))continue;
        byte[] bytes=zip.getInputStream(e).readAllBytes();ClassWriter w=new ClassWriter(0);
        new ClassReader(bytes).accept(new ClassVisitor(Opcodes.ASM9,w){
          public AnnotationVisitor visitAnnotation(String d,boolean v){return d.equals("Lnet/fabricmc/api/Environment;")?null:super.visitAnnotation(d,v);}
        },0);
        Files.createDirectories(dest.getParent());Files.write(dest,w.toByteArray());
      }
    }
  }
}

