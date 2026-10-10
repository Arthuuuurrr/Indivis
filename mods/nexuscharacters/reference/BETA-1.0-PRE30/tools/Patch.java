import java.nio.file.*;
import java.util.*;
import java.util.zip.*;
import jdk.internal.org.objectweb.asm.*;
import jdk.internal.org.objectweb.asm.tree.*;
public final class Patch implements Opcodes {
 static final String P="net/tompsen/nexuscharacters/", S=P+"PosedSurfaceSupport$State", W=P+"PoseScratch$Workspace";
 static MethodNode bridge(int access,String name,String desc,String owner,String target) {
  MethodNode m=new MethodNode(access,name,desc,null,new String[]{"java/lang/Exception"});
  int local=0;
  for(Type t:Type.getArgumentTypes(desc)){m.instructions.add(new VarInsnNode(t.getOpcode(ILOAD),local));local+=t.getSize();}
  m.instructions.add(new MethodInsnNode(INVOKESTATIC,owner,target,desc,false));
  m.instructions.add(new InsnNode(Type.getReturnType(desc).getOpcode(IRETURN)));return m;
 }
 static void body(MethodNode m,String owner,String name,String desc){
  m.instructions=bridge(m.access,m.name,desc,owner,name).instructions;
  m.tryCatchBlocks.clear(); if(m.localVariables!=null)m.localVariables.clear();
 }
 public static void main(String[] args)throws Exception{
  Path out=Path.of(args[1]);
  try(ZipFile z=new ZipFile(args[0])){
   for(String cls:List.of("GenericSkinLayerSupport","SurfaceGeometry","PosedSurfaceSupport","PosedSurfaceSupport$State")){
    String name=P+cls; ClassNode c=new ClassNode();new ClassReader(z.getInputStream(z.getEntry(name+".class"))).accept(c,0);
    List<MethodNode> added=new ArrayList<>();
    for(MethodNode m:c.methods){
     if(cls.equals("GenericSkinLayerSupport") && List.of("player","preview","firstPerson").contains(m.name)) {
      m.instructions.insert(new MethodInsnNode(INVOKESTATIC,P+"Pre30Diagnostics",m.name,"()V",false));
     }
     if(cls.equals("GenericSkinLayerSupport")&&m.name.equals("method")){
      String desc=m.desc;m.name="resolveMethodPRE30";m.access=ACC_STATIC;
      MethodNode cached=bridge(ACC_PRIVATE|ACC_STATIC,"method",desc,P+"ReflectionAccess","method");cached.exceptions=new ArrayList<>(m.exceptions);added.add(cached);
     }else if(cls.equals("GenericSkinLayerSupport")&&m.name.equals("field")){
      body(m,P+"ReflectionAccess","genericField",m.desc);
     }else if(cls.equals("SurfaceGeometry")&&List.of("field","set","call").contains(m.name)){
      body(m,P+"ReflectionAccess",m.name.equals("field")?"surfaceField":m.name.equals("set")?"surfaceSet":"surfaceCall",m.desc);
     }else if(cls.equals("PosedSurfaceSupport")&&m.name.equals("apply")&&Type.getArgumentTypes(m.desc).length==5){
      String desc=m.desc;m.name="applyPRE30";
      added.add(bridge(ACC_STATIC,"apply",desc,P+"PoseScratch","apply"));
      AbstractInsnNode start=null,end=null;
      for(AbstractInsnNode i=m.instructions.getFirst();i!=null;i=i.getNext()){
       if(i instanceof IntInsnNode ii&&ii.operand==12&&i.getNext() instanceof TypeInsnNode ti&&ti.getOpcode()==ANEWARRAY){start=i;break;}
      }
      if(start==null)throw new AssertionError("Missing PRE29 array block");
      for(AbstractInsnNode i=start;i!=null;i=i.getNext()){
       if(i instanceof FieldInsnNode f&&f.owner.equals(S)&&f.name.equals("firstPerson")){end=i.getPrevious();break;}
      }
      if(end==null)throw new AssertionError("Missing PRE29 pose predicate");
      // End is ALOAD state: retain it, delete the allocation/fill block preceding it.
      InsnList code=new InsnList();code.add(new VarInsnNode(ALOAD,5));code.add(new VarInsnNode(ALOAD,0));code.add(new VarInsnNode(ALOAD,1));code.add(new VarInsnNode(ILOAD,2));code.add(new VarInsnNode(ILOAD,3));
      code.add(new MethodInsnNode(INVOKESTATIC,P+"PoseScratch","prepare","(L"+S+";Ljava/lang/Object;[Ljava/lang/Object;ZZ)L"+W+";",false));
      for(String field:List.of("matrices","relatives","templates")){
       if(!field.equals("templates"))code.add(new InsnNode(DUP));
       code.add(new FieldInsnNode(GETFIELD,W,field,field.equals("templates")?"[Ljava/lang/Object;":"[Lorg/joml/Matrix4f;"));
       code.add(new VarInsnNode(ASTORE,field.equals("matrices")?6:field.equals("relatives")?7:8));
      }
      m.instructions.insertBefore(start,code);
      for(AbstractInsnNode i=start;i!=end;){AbstractInsnNode next=i.getNext();m.instructions.remove(i);i=next;}
     }else if(cls.equals("PosedSurfaceSupport")&&m.name.equals("clear")){
      m.name="clearPRE30";m.access=ACC_STATIC;
      added.add(bridge(ACC_PUBLIC|ACC_STATIC,"clear",m.desc,P+"PoseScratch","clear"));
     }
    }
    c.methods.addAll(added);
    if(cls.endsWith("$State"))c.fields.add(new FieldNode(0,"pre30Scratch","L"+W+";",null,null));
    ClassWriter writer=new ClassWriter(ClassWriter.COMPUTE_FRAMES|ClassWriter.COMPUTE_MAXS){
      protected String getCommonSuperClass(String a,String b){
        try{ClassLoader l=Patch.class.getClassLoader();Class<?> x=Class.forName(a.replace('/','.'),false,l),y=Class.forName(b.replace('/','.'),false,l);
          if(x.isAssignableFrom(y))return a;if(y.isAssignableFrom(x))return b;if(x.isInterface()||y.isInterface())return "java/lang/Object";
          do{x=x.getSuperclass();}while(!x.isAssignableFrom(y));return x.getName().replace('.','/');
        }catch(ClassNotFoundException e){throw new RuntimeException(e);}
      }
    };
    c.accept(writer);Path file=out.resolve(name+".class");Files.createDirectories(file.getParent());Files.write(file,writer.toByteArray());
   }
  }
 }
}
