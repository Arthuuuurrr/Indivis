import java.util.*;
import java.util.zip.*;
import jdk.internal.org.objectweb.asm.*;
import jdk.internal.org.objectweb.asm.tree.*;
public final class Verify35 {
 static void check(boolean c,String s){if(!c)throw new AssertionError(s);}
 public static void main(String[] args)throws Exception{
  String p="net/tompsen/nexuscharacters/";int preserved=0,delegates=0,references=0;
  try(ZipFile a=new ZipFile(args[0]);ZipFile b=new ZipFile(args[1]);ZipFile q=new ZipFile(args[2])){
   for(String type:List.of("SurfaceGeometry","PosedSurfaceSupport")){
    ClassNode old=Verify.cls(a,p+type+".class"),next=Verify.cls(b,p+type+".class"),qa=Verify.cls(q,p+type+".class");
    check(old.fields.size()==next.fields.size(),"Fields changed");check(old.methods.size()==next.methods.size(),"Method count changed");
    for(int i=0;i<old.fields.size();i++){FieldNode x=old.fields.get(i),y=next.fields.get(i);check(x.name.equals(y.name)&&x.desc.equals(y.desc)&&x.access==y.access,"Field contract");}
    for(MethodNode m:old.methods){
     MethodNode n=next.methods.stream().filter(v->v.name.equals(m.name)&&v.desc.equals(m.desc)).findFirst().orElseThrow();
     check(m.access==n.access&&Objects.equals(m.signature,n.signature)&&m.exceptions.equals(n.exceptions),"Method contract");
     boolean changed=type.equals("SurfaceGeometry")&&m.name.equals("subtract")&&m.desc.equals(Patch35.SUB)||type.equals("PosedSurfaceSupport")&&m.name.equals("clean");
     if(!changed){check(Verify.code(m).equals(Verify.code(n)),"Unrelated method "+type+"."+m.name);preserved++;}
     else{
      String helper=p+(type.equals("SurfaceGeometry")?"ExactSurfaceClipper":"ExactSurfaceCleaner");
      int calls=0;for(AbstractInsnNode ins:n.instructions)if(ins instanceof MethodInsnNode call){check(call.owner.equals(helper)&&call.desc.equals(m.desc),"Delegate target");calls++;}
      check(calls==1,"Delegate count");delegates++;
     }
     String alias=m.name.equals("clean")?"referenceClean":m.name.equals("subtractConnected")?"referenceSubtractConnected":m.name.equals("subtract")?"referenceSubtract":null;
     if(alias!=null){
      MethodNode ref=qa.methods.stream().filter(v->v.name.equals(alias)&&v.desc.equals(m.desc)).findFirst().orElseThrow();
      for(AbstractInsnNode ins:ref.instructions)if(ins instanceof MethodInsnNode call&&call.owner.equals(p+"SurfaceGeometry")){
       if(call.name.equals("referenceSubtract"))call.name="subtract";if(call.name.equals("referenceSubtractConnected"))call.name="subtractConnected";
      }
      check(Verify.code(m).equals(Verify.code(ref)),"QA reference differs from actual SI1: "+m.name);references++;
     }
    }
   }
   check(b.getEntry(p+"SurfaceComparisonOracle.class")==null,"Oracle leaked into production");
  }
  check(delegates==2&&references==4,"Delegate/reference totals");
  System.out.println("BYTECODE35_PASS preserved_methods="+preserved+" exact_delegates="+delegates+" original_QA_bodies="+references);
 }
}
