import java.nio.file.*;
import java.util.*;
import java.util.zip.*;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;

/** Stack-identical redirects only; all geometry, flags and field reads survive. */
public final class PatchOpt2 implements Opcodes {
    static final String P="net/tompsen/nexuscharacters/";
    public static void main(String[] args)throws Exception {
        Path out=Path.of(args[1]);Map<String,Integer> seen=new TreeMap<>();
        Map<String,Set<String>> targets=Map.of(
            "GenericSkinLayerSupport",Set.of("net.minecraft.class_9950","net.tompsen.nexuscharacters.NexusDummyEntity"),
            "LowerJointSupport",Set.of("dev.tr7zw.skinlayers.SkinLayersModBase"));
        try(ZipFile z=new ZipFile(args[0])) {
            for(var entry:targets.entrySet()) {
                String name=entry.getKey();ClassNode c=new ClassNode(ASM9);
                new ClassReader(z.getInputStream(z.getEntry(P+name+".class"))).accept(c,0);
                String methodName=name.equals("GenericSkinLayerSupport")?"player":"apply";
                for(MethodNode method:c.methods)if(method.name.equals(methodName))
                    for(AbstractInsnNode i:method.instructions.toArray())if(i instanceof MethodInsnNode call
                        &&call.getOpcode()==INVOKESTATIC&&call.owner.equals("java/lang/Class")
                        &&call.name.equals("forName")&&call.desc.equals("(Ljava/lang/String;)Ljava/lang/Class;")) {
                        AbstractInsnNode previous=i.getPrevious();
                        while(previous!=null&&previous.getOpcode()<0)previous=previous.getPrevious();
                        if(previous instanceof LdcInsnNode literal&&literal.cst instanceof String key&&entry.getValue().contains(key)) {
                            call.owner=P+"RenderClassLookup";call.name="find";seen.merge(key,1,Integer::sum);
                        }
                    }
                ClassWriter writer=new ClassWriter(0);c.accept(writer);
                Path f=out.resolve(P+name+".class");Files.createDirectories(f.getParent());Files.write(f,writer.toByteArray());
            }
        }
        Set<String> expected=new HashSet<>();for(var values:targets.values())expected.addAll(values);
        if(!seen.keySet().equals(expected)||seen.values().stream().anyMatch(v->v!=1))throw new AssertionError(seen);
        System.out.println("OPT2_REFLECTION_PATCH_PASS "+seen);
    }
}
