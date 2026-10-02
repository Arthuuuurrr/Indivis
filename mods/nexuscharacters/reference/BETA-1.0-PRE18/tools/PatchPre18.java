import java.nio.file.*;
import jdk.internal.org.objectweb.asm.*;
import jdk.internal.org.objectweb.asm.tree.*;

/** Minimal bytecode edits keep the original compositor, tint math and public ABI intact. */
public final class PatchPre18 {
    private static final String OWNER="net/tompsen/nexuscharacters/DynamicAppearanceSupport";
    private static final String HELPER="net/tompsen/nexuscharacters/GenericSkinLayerSupport";
    public static void main(String[] args)throws Exception {
        Path root=Path.of(args[0]);Path file=root.resolve(OWNER+".class");
        ClassNode c=new ClassNode();new ClassReader(Files.readAllBytes(file)).accept(c,0);
        MethodNode overlay=null,load=null;int completed=0;
        for(MethodNode m:c.methods) {
            if(m.name.equals("overlayTinted"))overlay=m;
            if(m.name.equals("load"))load=m;
            if(m.name.equals("textures"))for(AbstractInsnNode insn:m.instructions.toArray()) {
                if(insn instanceof MethodInsnNode call && call.owner.equals(OWNER)
                        && (call.name.equals("compose69")||call.name.equals("composeLegacy"))) {
                    InsnList hook=new InsnList();hook.add(new VarInsnNode(Opcodes.ALOAD,0));
                    hook.add(new MethodInsnNode(Opcodes.INVOKESTATIC,HELPER,"complete","(Ljava/lang/Object;Ljava/lang/String;)Ljava/lang/Object;",false));
                    hook.add(new TypeInsnNode(Opcodes.CHECKCAST,"net/minecraft/class_1011"));
                    m.instructions.insert(call,hook);completed++;
                }
            }
        }
        if(overlay==null||load==null||completed!=2)throw new IllegalStateException("Unexpected PRE17 compositor shape");
        String descriptor=overlay.desc;overlay.name="nexus$overlayOriginal";
        MethodNode dispatch=new MethodNode(Opcodes.ACC_PRIVATE|Opcodes.ACC_STATIC,"overlayTinted",descriptor,null,null);
        for(int i=0;i<6;i++)dispatch.instructions.add(new VarInsnNode(i==2||i>=4?Opcodes.ILOAD:Opcodes.ALOAD,i));
        dispatch.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC,HELPER,"overlay","(Ljava/lang/Object;Ljava/lang/Object;ILjava/lang/Object;II)V",false));
        dispatch.instructions.add(new InsnNode(Opcodes.RETURN));dispatch.maxStack=6;dispatch.maxLocals=6;c.methods.add(dispatch);
        load.instructions.clear();load.tryCatchBlocks.clear();if(load.localVariables!=null)load.localVariables.clear();
        load.instructions.add(new VarInsnNode(Opcodes.ALOAD,0));
        load.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC,HELPER,"load","(Ljava/lang/String;)Ljava/lang/Object;",false));
        load.instructions.add(new TypeInsnNode(Opcodes.CHECKCAST,"net/minecraft/class_1011"));load.instructions.add(new InsnNode(Opcodes.ARETURN));
        load.maxStack=1;load.maxLocals=1;
        ClassWriter w=new ClassWriter(ClassWriter.COMPUTE_MAXS);c.accept(w);Files.write(file,w.toByteArray());
        System.out.println("PRE18 compositor hooks=2; original tint/blend bytecode retained");
    }
}
