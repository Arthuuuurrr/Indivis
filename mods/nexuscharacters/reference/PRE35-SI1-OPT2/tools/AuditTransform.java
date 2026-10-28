import java.io.*;
import java.util.*;
import java.util.zip.*;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;
import org.objectweb.asm.util.*;

/** Check all original SurfaceGeometry methods, including retained oracle. */
public final class AuditTransform {
    static ClassNode read(String jar)throws Exception {
        try(ZipFile z=new ZipFile(jar)) {
            ClassNode c=new ClassNode();new ClassReader(z.getInputStream(z.getEntry("net/tompsen/nexuscharacters/SurfaceGeometry.class"))).accept(c,0);return c;
        }
    }
    static String instructions(MethodNode m) {
        Textifier printer=new Textifier();m.accept(new TraceMethodVisitor(printer));StringWriter out=new StringWriter();printer.print(new PrintWriter(out));return out.toString();
    }
    public static void main(String[] args)throws Exception {
        ClassNode old=read(args[0]),next=read(args[1]);int checks=0;
        if(old.fields.size()!=next.fields.size()||!old.interfaces.equals(next.interfaces)||!Objects.equals(old.nestMembers,next.nestMembers))throw new AssertionError("Surface class structure changed");
        for(MethodNode m:old.methods) {
            String name=m.name.equals("transform")?"transformOPT1":m.name;
            MethodNode n=next.methods.stream().filter(x->x.name.equals(name)&&x.desc.equals(m.desc)).findFirst().orElseThrow();
            if(m.access!=n.access||!Objects.equals(m.signature,n.signature)||!Objects.equals(m.exceptions,n.exceptions)||!instructions(m).equals(instructions(n)))throw new AssertionError("Changed original method "+m.name+m.desc);
            checks++;
        }
        if(next.methods.size()!=old.methods.size()+1)throw new AssertionError("Unexpected method additions");
        System.out.println("OPT2_BYTECODE_ORACLE_PASS original_methods="+checks+" unchanged_except_transform_name; original class fields/interfaces/nest unchanged");
    }
}
