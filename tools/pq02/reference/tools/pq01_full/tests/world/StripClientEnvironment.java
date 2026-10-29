import java.nio.file.*;
import java.util.zip.*;
import org.objectweb.asm.*;
/** Banc uniquement : annotations CLIENT retirées et sortie C2S enregistrée, jamais livrés. */
public final class StripClientEnvironment {
  public static void main(String[] args)throws Exception {
    try(ZipFile zip=new ZipFile(args[0])){
      for(ZipEntry e:java.util.Collections.list(zip.entries())){
        if(!e.getName().startsWith("net/hautecapitale/dialogue/")||!e.getName().endsWith(".class"))continue;
        Path dest=Path.of(args[1],e.getName());if(Files.exists(dest))continue;
        byte[] bytes=zip.getInputStream(e).readAllBytes();ClassWriter w=new ClassWriter(0);
        new ClassReader(bytes).accept(new ClassVisitor(Opcodes.ASM9,w){
          public AnnotationVisitor visitAnnotation(String d,boolean v){return d.equals("Lnet/fabricmc/api/Environment;")?null:super.visitAnnotation(d,v);}
          public MethodVisitor visitMethod(int access,String name,String desc,String sig,String[] exceptions){
            return new MethodVisitor(Opcodes.ASM9,super.visitMethod(access,name,desc,sig,exceptions)){
              public void visitMethodInsn(int op,String owner,String method,String descriptor,boolean itf){
                if(owner.equals("net/fabricmc/fabric/api/client/networking/v1/ClientPlayNetworking")&&method.equals("send"))owner="net/hautecapitale/quetes/sequence/Pq02ScreenNetwork";
                super.visitMethodInsn(op,owner,method,descriptor,itf);
              }
            };
          }
        },0);
        Files.createDirectories(dest.getParent());Files.write(dest,w.toByteArray());
      }
    }
  }
}

