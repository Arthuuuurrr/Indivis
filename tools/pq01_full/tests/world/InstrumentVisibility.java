import java.nio.file.*;
import java.util.zip.*;
import jdk.internal.org.objectweb.asm.*;
/** Compteur de banc : le corps réel de QuestVisibility et son mixin restent exécutés. */
public final class InstrumentVisibility implements Opcodes {
 public static void main(String[] args)throws Exception {
  String name="net/hautecapitale/quetes/visibility/QuestVisibility.class";byte[] bytes;
  try(ZipFile jar=new ZipFile(args[0])){bytes=jar.getInputStream(jar.getEntry(name)).readAllBytes();}
  int major=((bytes[6]&255)<<8)|(bytes[7]&255);bytes[6]=0;bytes[7]=61;
  ClassReader r=new ClassReader(bytes);ClassWriter w=new ClassWriter(ClassWriter.COMPUTE_MAXS);int[] count={0};
  r.accept(new ClassVisitor(ASM8,w){
   public void visit(int v,int a,String n,String s,String p,String[] i){super.visit(major,a,n,s,p,i);}
   public MethodVisitor visitMethod(int a,String n,String d,String s,String[] e){
    MethodVisitor m=super.visitMethod(a,n,d,s,e);
    if(!n.equals("refresh")||!d.equals("(Lnet/minecraft/class_3222;)V"))return m;
    count[0]++;return new MethodVisitor(ASM8,m){public void visitCode(){super.visitCode();visitVarInsn(ALOAD,0);visitMethodInsn(INVOKESTATIC,"net/hautecapitale/quetes/sequence/Pq01MultiWorldBench","visibilityPass","(Ljava/lang/Object;)V",false);}};
   }
  },0);
  if(count[0]!=1)throw new AssertionError("refresh instrument count "+count[0]);
  Path dest=Path.of(args[1],name);Files.createDirectories(dest.getParent());Files.write(dest,w.toByteArray());
 }
}
