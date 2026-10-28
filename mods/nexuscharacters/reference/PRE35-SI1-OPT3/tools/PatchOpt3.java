import java.nio.file.*;
import java.util.*;
import java.util.zip.*;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;

/** Replace only world-grid construction. Keep the complete OPT2 method. */
public final class PatchOpt3 implements Opcodes {
    static final String P="net/tompsen/nexuscharacters/";
    public static void main(String[] args)throws Exception {
        Path out=Path.of(args[1]);
        try(ZipFile z=new ZipFile(args[0])) {
            List<String> names=new ArrayList<>();
            if(Boolean.getBoolean("opt3.volumes"))names.addAll(List.of("PosedSurfaceSupport","PosedSurfaceSupport$Grid","PoseScratch$Workspace"));
            if(Boolean.getBoolean("opt3.compact"))names.add("ExactSurfaceClipper");
            for(String name:names) {
                ClassNode c=new ClassNode(ASM9);
                new ClassReader(z.getInputStream(z.getEntry(P+name+".class"))).accept(c,0);
                if(name.equals("PosedSurfaceSupport")) {
                    MethodNode m=c.methods.stream().filter(x->x.name.equals("applyPRE30")).findFirst().orElseThrow();
                    MethodNode original=new MethodNode(ASM9,m.access,"applyOPT2",m.desc,m.signature,m.exceptions.toArray(String[]::new));
                    m.accept(original);original.name="applyOPT2";c.methods.add(original);
                    List<TypeInsnNode> starts=new ArrayList<>();
                    for(AbstractInsnNode n:m.instructions)if(n instanceof TypeInsnNode t&&t.getOpcode()==NEW&&t.desc.equals(P+"PosedSurfaceSupport$Grid"))starts.add(t);
                    if(starts.size()!=2)throw new AssertionError("Expected exactly two grid constructions");
                    AbstractInsnNode start=starts.get(1),end=null;
                    // First part loop has the ILOAD14, BIPUSH6, IF_ICMPGE exit.
                    for(AbstractInsnNode n=start;n!=null;n=n.getNext()) {
                        if(n instanceof JumpInsnNode j&&j.getOpcode()==IF_ICMPGE){end=j.label;break;}
                    }
                    if(end==null)throw new AssertionError("No world-grid loop exit");
                    InsnList replacement=new InsnList();
                    replacement.add(new VarInsnNode(ALOAD,5));replacement.add(new VarInsnNode(ALOAD,6));replacement.add(new VarInsnNode(ALOAD,8));
                    replacement.add(new MethodInsnNode(INVOKESTATIC,P+"PartVolumeCache","world","(L"+P+"PosedSurfaceSupport$State;[Lorg/joml/Matrix4f;[Ljava/lang/Object;)L"+P+"PosedSurfaceSupport$Grid;",false));
                    replacement.add(new VarInsnNode(ASTORE,13));m.instructions.insertBefore(start,replacement);
                    for(AbstractInsnNode n=start;n!=end;){AbstractInsnNode next=n.getNext();m.instructions.remove(n);n=next;}
                    // Expanded original frames reference removed loop locals.
                    // Recompute for this class using the original frame types.
                    for(MethodNode method:c.methods)for(AbstractInsnNode n:method.instructions.toArray())if(n instanceof FrameNode)method.instructions.remove(n);
                }else if(name.equals("PosedSurfaceSupport$Grid")) {
                    c.fields.add(new FieldNode(ACC_PUBLIC,"opt3Boxes","[L"+P+"SurfaceGeometry$Box;",null,null));
                    // Any later add invalidates the fast enumeration as well.
                    MethodNode add=c.methods.stream().filter(x->x.name.equals("add")).findFirst().orElseThrow();
                    InsnList invalidate=new InsnList();invalidate.add(new VarInsnNode(ALOAD,0));invalidate.add(new InsnNode(ACONST_NULL));
                    invalidate.add(new FieldInsnNode(PUTFIELD,c.name,"opt3Boxes","[L"+P+"SurfaceGeometry$Box;"));add.instructions.insert(invalidate);
                }else if(name.equals("PoseScratch$Workspace"))c.fields.add(new FieldNode(ACC_PUBLIC,"opt3Volumes","L"+P+"PartVolumeCache$Pool;",null,null));
                else {
                    MethodNode m=c.methods.stream().filter(x->x.name.equals("intersecting")).findFirst().orElseThrow();m.name="intersectingOPT2";
                    MethodNode delegate=new MethodNode(ASM9,m.access,"intersecting",m.desc,m.signature,null);
                    delegate.instructions.add(new VarInsnNode(ALOAD,0));delegate.instructions.add(new VarInsnNode(ALOAD,1));delegate.instructions.add(new VarInsnNode(ILOAD,2));
                    delegate.instructions.add(new MethodInsnNode(INVOKESTATIC,P+"CompactSurfaceClipper","intersecting",m.desc,false));delegate.instructions.add(new InsnNode(ARETURN));c.methods.add(delegate);
                }
                ClassWriter writer=new ClassWriter(ClassWriter.COMPUTE_FRAMES|ClassWriter.COMPUTE_MAXS){
                    protected String getCommonSuperClass(String a,String b){return "java/lang/Object";}
                };
                c.accept(writer);Path f=out.resolve(P+name+".class");Files.createDirectories(f.getParent());Files.write(f,writer.toByteArray());
            }
        }
        System.out.println("OPT3_TARGETED_PATCH_PASS");
    }
}
