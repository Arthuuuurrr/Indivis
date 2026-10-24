import java.nio.file.*;
import jdk.internal.org.objectweb.asm.*;
/** Connexion sans socket : conserve les vrais paquets tellraw de Minecraft. */
public final class MakePacketSink implements Opcodes {
  public static void main(String[] args)throws Exception {
    ClassWriter c=new ClassWriter(0);String name="net/hautecapitale/quetes/sequence/Pq01PacketSink";
    c.visit(V17,ACC_PUBLIC,name,null,"net/minecraft/class_3244",null);
    for(String[] m:new String[][]{{"method_14364","(Lnet/minecraft/class_2596;)V"},{"method_52391","(Lnet/minecraft/class_2596;Lio/netty/channel/ChannelFutureListener;)V"}}){
      MethodVisitor v=c.visitMethod(ACC_PUBLIC,m[0],m[1],null,null);v.visitCode();v.visitVarInsn(ALOAD,0);v.visitVarInsn(ALOAD,1);
      v.visitMethodInsn(INVOKESTATIC,"net/hautecapitale/quetes/sequence/Pq01MultiWorldBench","packet","(Ljava/lang/Object;Ljava/lang/Object;)V",false);v.visitVarInsn(ALOAD,1);
      v.visitMethodInsn(INVOKESTATIC,"net/hautecapitale/quetes/sequence/Pq01PipelineBench","packet","(Ljava/lang/Object;)V",false);
      v.visitInsn(RETURN);v.visitMaxs(2,m[0].equals("method_14364")?2:3);v.visitEnd();
    }
    c.visitEnd();Path p=Path.of(args[0],name+".class");Files.createDirectories(p.getParent());Files.write(p,c.toByteArray());
  }
}
