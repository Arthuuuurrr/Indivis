import java.nio.file.*;import org.objectweb.asm.*;
public final class MakeSink implements Opcodes {
 public static void main(String[] a)throws Exception{ClassWriter w=new ClassWriter(0);String n="net/hautecapitale/quetes/sequence/Pq02PacketSink";w.visit(V21,ACC_PUBLIC,n,null,"net/minecraft/class_3244",null);
  for(String[] m:new String[][]{{"method_14364","(Lnet/minecraft/class_2596;)V"},{"method_52391","(Lnet/minecraft/class_2596;Lio/netty/channel/ChannelFutureListener;)V"}}){var v=w.visitMethod(ACC_PUBLIC,m[0],m[1],null,null);v.visitCode();v.visitVarInsn(ALOAD,0);v.visitVarInsn(ALOAD,1);v.visitMethodInsn(INVOKESTATIC,"net/hautecapitale/quetes/sequence/Pq02WorldBench","packet","(Ljava/lang/Object;Ljava/lang/Object;)V",false);v.visitInsn(RETURN);v.visitMaxs(2,m[0].equals("method_14364")?2:3);v.visitEnd();}
  w.visitEnd();Path p=Path.of(a[0],n+".class");Files.createDirectories(p.getParent());Files.write(p,w.toByteArray());
 }
}
