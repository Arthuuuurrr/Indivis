import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.zip.*;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;
import org.objectweb.asm.util.*;

/** Instruction audit, ignoring debug locations and recomputed stack frames. */
public final class AuditOpt3 {
    static final String P="net/tompsen/nexuscharacters/";
    static ClassNode read(ZipFile z,String name)throws Exception {
        ClassNode c=new ClassNode();new ClassReader(z.getInputStream(z.getEntry(P+name+".class"))).accept(c,ClassReader.SKIP_DEBUG|ClassReader.SKIP_FRAMES);return c;
    }
    static String code(MethodNode m) {
        m.maxStack=0;m.maxLocals=0;Textifier p=new Textifier();m.accept(new TraceMethodVisitor(p));StringWriter out=new StringWriter();p.print(new PrintWriter(out));return out.toString();
    }
    static MethodNode method(ClassNode c,String name,String desc) {
        return c.methods.stream().filter(m->m.name.equals(name)&&m.desc.equals(desc)).findFirst().orElseThrow();
    }
    public static void main(String[] args)throws Exception {
        int checks=0;
        try(ZipFile old=new ZipFile(args[0]);ZipFile next=new ZipFile(args[1])) {
            for(String name:List.of("PosedSurfaceSupport","PosedSurfaceSupport$Grid","PoseScratch$Workspace","ExactSurfaceClipper","SpatialSurfaceCleaner$Index")) {
                ClassNode a=read(old,name),b=read(next,name);
                for(MethodNode m:a.methods) {
                    if(name.equals("SpatialSurfaceCleaner$Index")&&m.name.equals("<init>"))continue;
                    boolean volume=b.methods.stream().anyMatch(x->x.name.equals("applyOPT2"));
                    boolean fastGrid=b.fields.stream().anyMatch(x->x.name.equals("opt3Boxes"));
                    String target=name.equals("PosedSurfaceSupport")&&volume&&m.name.equals("applyPRE30")?"applyOPT2":name.equals("ExactSurfaceClipper")&&m.name.equals("intersecting")?"intersectingOPT2":m.name;
                    MethodNode n=method(b,target,m.desc);
                    if(name.equals("PosedSurfaceSupport$Grid")&&fastGrid&&m.name.equals("add")) {
                        AbstractInsnNode first=n.instructions.getFirst();
                        if(!(first instanceof VarInsnNode v&&v.getOpcode()==Opcodes.ALOAD&&v.var==0))throw new AssertionError("Grid prefix");
                        AbstractInsnNode second=first.getNext(),third=second.getNext();
                        if(second.getOpcode()!=Opcodes.ACONST_NULL||!(third instanceof FieldInsnNode f&&f.name.equals("opt3Boxes")))throw new AssertionError("Grid invalidation");
                        n.instructions.remove(first);n.instructions.remove(second);n.instructions.remove(third);
                    }
                    String ac=code(m),bc=code(n);
                    // Textifier includes the method's name in record bootstrap
                    // descriptors only, not its own method declaration.
                    if(!ac.equals(bc))throw new AssertionError("Original method changed "+name+"."+m.name);
                    checks++;
                }
            }
        }
        System.out.println("OPT3_BYTECODE_AUDIT_PASS unchanged_original_methods="+checks+" retained_OPT2_apply_and_clipper; only Grid.add fast-enumeration invalidation removed for comparison; only Index constructor excluded");
    }
}
