import java.nio.file.*;
import org.objectweb.asm.*;
public final class MakeColinSink implements Opcodes {
 public static void main(String[] args)throws Exception{
  ClassWriter w=new ClassWriter(0);String name="net/hautecapitale/quetes/sequence/ColinPacketSink";
  w.visit(V21,ACC_PUBLIC,name,null,"net/minecraft/class_3244",null);
  for(String[] m:new String[][]{{"method_14364","(Lnet/minecraft/class_2596;)V"},{"method_52391","(Lnet/minecraft/class_2596;Lio/netty/channel/ChannelFutureListener;)V"}}){MethodVisitor v=w.visitMethod(ACC_PUBLIC,m[0],m[1],null,null);v.visitCode();v.visitInsn(RETURN);v.visitMaxs(0,m[0].equals("method_14364")?2:3);v.visitEnd();}
  w.visitEnd();Path p=Path.of(args[0],name+".class");Files.createDirectories(p.getParent());Files.write(p,w.toByteArray());
 }
}
