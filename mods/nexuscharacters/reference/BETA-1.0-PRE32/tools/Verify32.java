import java.util.*;
import java.util.zip.*;
import jdk.internal.org.objectweb.asm.*;
import jdk.internal.org.objectweb.asm.tree.*;

public final class Verify32 implements Opcodes {
    static final String P="net/tompsen/nexuscharacters/";
    public static void main(String[] args)throws Exception {
        int same=0,hooks=0;
        try(ZipFile a=new ZipFile(args[0]);ZipFile b=new ZipFile(args[1])) {
            for(String cls:List.of("GenericSkinLayerSupport","PosedSurfaceSupport")) {
                ClassNode old=Verify.cls(a,P+cls+".class"), fresh=Verify.cls(b,P+cls+".class");
                if(old.methods.size()!=fresh.methods.size()||old.fields.size()!=fresh.fields.size())throw new AssertionError("Members changed");
                Map<String,MethodNode> methods=new HashMap<>();for(MethodNode m:fresh.methods)methods.put(m.name+m.desc,m);
                for(MethodNode original:old.methods) {
                    MethodNode patched=methods.get(original.name+original.desc);
                    if(patched==null)throw new AssertionError("Removed method");
                    for(AbstractInsnNode n:patched.instructions.toArray()) {
                        if(n instanceof MethodInsnNode call&&call.owner.equals(P+"FirstPersonSurfaceSupport")) {
                            if(call.name.equals("firstPerson")) {
                                if(!original.name.equals("firstPerson")||!cls.equals("GenericSkinLayerSupport"))throw new AssertionError("Unexpected hand hook");
                                AbstractInsnNode arm=call.getPrevious();
                                if(!(arm instanceof VarInsnNode v)||v.getOpcode()!=ALOAD||v.var!=2)throw new AssertionError("Wrong arm parameter");
                                patched.instructions.remove(arm);call.owner=P+"PosedSurfaceSupport";call.desc="(Ljava/lang/Object;[Ljava/lang/Object;Z)V";
                            } else if(call.name.equals("clearCaches")) {
                                if(!original.name.equals("clearCaches"))throw new AssertionError("Unexpected resource hook");
                                patched.instructions.remove(call);
                            } else if(call.name.equals("detach")) {
                                if(!original.name.equals("clear")||!cls.equals("PosedSurfaceSupport"))throw new AssertionError("Unexpected clear hook");
                                AbstractInsnNode model=call.getPrevious();
                                if(!(model instanceof VarInsnNode v)||v.getOpcode()!=ALOAD||v.var!=0)throw new AssertionError("Wrong clear model");
                                patched.instructions.remove(model);patched.instructions.remove(call);
                            } else throw new AssertionError("Unexpected hook "+call.name);
                            hooks++;
                        }
                    }
                    if(!Verify.code(original).equals(Verify.code(patched)))throw new AssertionError("Unrelated code changed: "+cls+"."+original.name);
                    same++;
                }
            }
        }
        if(hooks!=3)throw new AssertionError("Wrong hook count "+hooks);
        System.out.println("BYTECODE32_PASS preserved_methods="+same+" hooks="+hooks+" world_clipping_and_old_first_person_oracle_unchanged=true");
    }
}
