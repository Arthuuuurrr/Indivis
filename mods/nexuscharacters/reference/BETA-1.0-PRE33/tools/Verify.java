import java.util.*;
import java.util.zip.*;
import jdk.internal.org.objectweb.asm.*;
import jdk.internal.org.objectweb.asm.tree.*;
public final class Verify {
 static ClassNode cls(ZipFile z,String name)throws Exception{ClassNode c=new ClassNode();new ClassReader(z.getInputStream(z.getEntry(name))).accept(c,0);return c;}
 static String code(MethodNode m){
  Map<LabelNode,Integer> labels=new IdentityHashMap<>();int pc=0;
  for(AbstractInsnNode i:m.instructions){if(i instanceof LabelNode l)labels.put(l,pc);if(i.getOpcode()>=0)pc++;}
  StringBuilder b=new StringBuilder();for(AbstractInsnNode i:m.instructions){int op=i.getOpcode();if(op<0)continue;b.append(op).append(':');
   if(i instanceof VarInsnNode n)b.append(n.var);
   else if(i instanceof FieldInsnNode n)b.append(n.owner).append(n.name).append(n.desc);
   else if(i instanceof MethodInsnNode n)b.append(n.owner).append(n.name).append(n.desc).append(n.itf);
   else if(i instanceof TypeInsnNode n)b.append(n.desc);
   else if(i instanceof IntInsnNode n)b.append(n.operand);
   else if(i instanceof LdcInsnNode n)b.append(n.cst);
   else if(i instanceof JumpInsnNode n)b.append(labels.get(n.label));
   else if(i instanceof IincInsnNode n)b.append(n.var).append('/').append(n.incr);
   else if(i instanceof InvokeDynamicInsnNode n)b.append(n.name).append(n.desc).append(n.bsm).append(Arrays.deepToString(n.bsmArgs));
   else if(i instanceof TableSwitchInsnNode n)b.append(n.min).append(n.max).append(labels.get(n.dflt)).append(n.labels.stream().map(labels::get).toList());
   else if(i instanceof LookupSwitchInsnNode n)b.append(n.keys).append(labels.get(n.dflt)).append(n.labels.stream().map(labels::get).toList());
   else if(i instanceof MultiANewArrayInsnNode n)b.append(n.desc).append(n.dims);
   b.append(';');
  }return b.toString();
 }
 public static void main(String[] args)throws Exception {
  String prefix="net/tompsen/nexuscharacters/";int methods=0;
  Map<String,Set<String>> changed=Map.of("GenericSkinLayerSupport",Set.of("method","field","player","preview","firstPerson"),"SurfaceGeometry",Set.of("field","set","call"),"PosedSurfaceSupport",Set.of("apply","clear"),"PosedSurfaceSupport$State",Set.of());
  try(ZipFile base=new ZipFile(args[0]);ZipFile next=new ZipFile(args[1])){
   for(var entry:changed.entrySet()){
    String name=prefix+entry.getKey()+".class";ClassNode a=cls(base,name),b=cls(next,name);
    Map<String,MethodNode> map=new HashMap<>();for(MethodNode m:b.methods)map.put(m.name+m.desc,m);
    for(MethodNode m:a.methods){if(entry.getValue().contains(m.name))continue;MethodNode n=map.get(m.name+m.desc);if(n==null||!code(m).equals(code(n)))throw new AssertionError("Unrelated method changed "+entry.getKey()+"."+m.name+m.desc);methods++;}
    if(entry.getKey().equals("GenericSkinLayerSupport")){
     MethodNode original=a.methods.stream().filter(m->m.name.equals("method")).findFirst().orElseThrow(),resolver=b.methods.stream().filter(m->m.name.equals("resolveMethodPRE30")).findFirst().orElseThrow();
     if(!code(original).equals(code(resolver)))throw new AssertionError("Legacy resolver changed");methods++;
    }
    if(entry.getKey().equals("PosedSurfaceSupport")){
     MethodNode original=a.methods.stream().filter(m->m.name.equals("clear")).findFirst().orElseThrow(),resolver=b.methods.stream().filter(m->m.name.equals("clearPRE30")).findFirst().orElseThrow();
     if(!code(original).equals(code(resolver)))throw new AssertionError("Mesh clear changed");methods++;
    }
   }
  }
  System.out.println("BYTECODE_PASS unchangedMethods="+methods+" including SurfaceGeometry math, PRE29 legacy lookup and mesh clear");
 }
}

