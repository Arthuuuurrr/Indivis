import java.util.*;
import java.util.zip.*;
import jdk.internal.org.objectweb.asm.*;
import jdk.internal.org.objectweb.asm.tree.*;

public final class Verify31 implements Opcodes {
    static final String P="net/tompsen/nexuscharacters/";
    static AbstractInsnNode real(AbstractInsnNode n) {
        while(n!=null && n.getOpcode()<0)n=n.getNext();return n;
    }
    public static void main(String[] args)throws Exception {
        int same=0;
        try(ZipFile old=new ZipFile(args[0]);ZipFile fresh=new ZipFile(args[1])) {
            for(String cls:List.of("SurfaceGeometry","PosedSurfaceSupport")) {
                ClassNode a=Verify.cls(old,P+cls+".class"),b=Verify.cls(fresh,P+cls+".class");
                if(a.methods.size()!=b.methods.size() || a.fields.size()!=b.fields.size())throw new AssertionError("Members changed");
                Map<String,MethodNode> methods=new HashMap<>();for(MethodNode m:b.methods)methods.put(m.name+m.desc,m);
                for(MethodNode m:a.methods) {
                    MethodNode n=methods.get(m.name+m.desc);
                    if(n==null)throw new AssertionError("Method removed");
                    if(cls.equals("SurfaceGeometry") && m.name.equals("write")) {
                        List<AbstractInsnNode> ins=new ArrayList<>();for(AbstractInsnNode i:n.instructions)if(i.getOpcode()>=0)ins.add(i);
                        if(ins.size()!=3 || ins.get(0).getOpcode()!=ALOAD || ins.get(2).getOpcode()!=ARETURN
                                || !(ins.get(1) instanceof MethodInsnNode call) || !call.owner.equals(P+"PrimitiveSurfaceWriter") || !call.name.equals("write") || !call.desc.equals(m.desc))throw new AssertionError("Writer bridge differs");
                        continue;
                    }
                    if(cls.equals("PosedSurfaceSupport") && m.name.equals("applyPRE30")) {
                        JumpInsnNode branch=null;
                        for(AbstractInsnNode i:n.instructions)if(i instanceof VarInsnNode v && v.getOpcode()==ILOAD && v.var==4 && real(i.getNext()) instanceof JumpInsnNode j && j.getOpcode()==IFEQ)branch=j;
                        if(branch==null)throw new AssertionError("Branch not found");
                        AbstractInsnNode[] guard=new AbstractInsnNode[6];guard[0]=real(branch.getNext());for(int i=1;i<6;i++)guard[i]=real(guard[i-1].getNext());
                        if(!(guard[0] instanceof VarInsnNode local) || local.getOpcode()!=ILOAD || local.var!=12
                                || !(guard[1] instanceof JumpInsnNode changed) || changed.getOpcode()!=IFNE
                                || !(guard[2] instanceof VarInsnNode state) || state.getOpcode()!=ALOAD || state.var!=5
                                || !(guard[3] instanceof FieldInsnNode first) || first.getOpcode()!=GETFIELD || !first.owner.equals(P+"PosedSurfaceSupport$State") || !first.name.equals("firstPerson")
                                || !(guard[4] instanceof JumpInsnNode perspective) || perspective.getOpcode()!=IFEQ || perspective.label!=changed.label
                                || !(guard[5] instanceof JumpInsnNode done) || done.getOpcode()!=GOTO)throw new AssertionError("Incorrect serialization guard");
                        for(AbstractInsnNode i:guard)n.instructions.remove(i);
                    }
                    if(!Verify.code(m).equals(Verify.code(n)))throw new AssertionError("Instructions changed: "+cls+"."+m.name);
                    same++;
                }
            }
        }
        System.out.println("BYTECODE31_PASS methods="+same+" original_pose_body_preserved_after_removing_six_instruction_guard=true clipping_math_unchanged=true");
    }
}
