import java.nio.file.*;
import java.util.zip.*;
import jdk.internal.org.objectweb.asm.*;
import jdk.internal.org.objectweb.asm.tree.*;

/** Vérifie le point d'injection dans le vrai JAR reçu, sans lancer Minecraft. */
public final class CheckNexusTarget {
  public static void main(String[] args)throws Exception{
    ClassNode target=new ClassNode();
    try(ZipFile zip=new ZipFile(args[0])){
      byte[] bytes=zip.getInputStream(zip.getEntry("net/tompsen/nexuscharacters/ProfileStateBridge.class")).readAllBytes();bytes[6]=0;bytes[7]=61;
      new ClassReader(bytes).accept(target,0);
    }
    String descriptor="(Lnet/minecraft/class_3222;Lnet/minecraft/class_269;Ljava/lang/String;)V";
    long count=target.methods.stream().filter(m->m.name.equals("runHookIfCorePresent")&&m.desc.equals(descriptor)&&(m.access&Opcodes.ACC_STATIC)!=0).count();
    if(count!=1)throw new AssertionError("Nexus injection target count="+count);
    long strings=java.util.Arrays.stream(Type.getArgumentTypes(descriptor)).filter(t->t.getDescriptor().equals("Ljava/lang/String;")).count();if(strings!=1)throw new AssertionError("String ordinal ambiguous");
    Class<?> mixin=Class.forName("net.indivis.admin.mixin.NexusProfileSaveMixin");var method=mixin.getDeclaredMethod("indivisBeforeSaveHook",String.class);method.setAccessible(true);
    if(!method.invoke(null,"function capitale:profiles/before_save_self").equals("function indivis_admin:profile/before_save_self"))throw new AssertionError("before save mapping");
    if(!method.invoke(null,"function capitale:profiles/after_load_self").equals("function capitale:profiles/after_load_self"))throw new AssertionError("after load altered");
    if(!method.invoke(null,"function autre:fonction").equals("function autre:fonction"))throw new AssertionError("other hook altered");
    System.out.println("NEXUS_TARGET PASS: real PRE34 target unique, unique String argument, compiled handler rewrites only before_save. No Fabric mixin application test.");
  }
}
