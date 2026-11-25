import java.nio.file.*;import java.util.*;import java.util.zip.*;import org.objectweb.asm.*;import org.objectweb.asm.tree.*;
public final class PatchSurfaceOpt implements Opcodes{
 static final String P="net/tompsen/nexuscharacters/";
 static MethodNode method(ClassNode c,String n){return c.methods.stream().filter(m->m.name.equals(n)).findFirst().orElseThrow();}
 static AbstractInsnNode next(AbstractInsnNode n){do{n=n.getNext();}while(n!=null&&n.getOpcode()<0);return n;}
 public static void main(String[] a)throws Exception{
  Set<String> disabled=a.length>2&&!a[2].isEmpty()?Set.of(a[2].split(",")):Set.of();
  try(ZipFile jar=new ZipFile(a[0])){for(String name:List.of("PosedSurfaceSupport","CompactSurfaceClipper","NativeVoxelSurface","SpatialSurfaceCleaner","SpatialSurfaceCleaner$Piece")){
   ClassNode c=new ClassNode();new ClassReader(jar.getInputStream(jar.getEntry(P+name+".class"))).accept(c,0);
   if(name.equals("PosedSurfaceSupport")){
    MethodNode m=method(c,"applyPRE30");int packed=0,memo=0;
    for(AbstractInsnNode n:m.instructions.toArray())if(n instanceof MethodInsnNode call&&call.owner.equals(P+"SurfaceGeometry")&&call.name.equals("transform")){
     AbstractInsnNode after=next(n);
     if(after instanceof MethodInsnNode write&&write.owner.equals(P+"SurfaceGeometry")&&write.name.equals("write")){
      if(!disabled.contains("writer")){call.owner=P+"PackedSurfaceOutput";call.name="write";call.desc="(Ljava/util/List;Lorg/joml/Matrix4f;)[F";m.instructions.remove(write);}packed++;
     }else{if(!disabled.contains("memo"))call.owner=P+"MemoSurfaceTransform";memo++;}
    }
    if(packed!=2||memo!=2)throw new AssertionError("Unexpected transform layout "+packed+"/"+memo);
    int local=0;
    for(var n:m.instructions.toArray())if(n instanceof MethodInsnNode call&&call.owner.equals(P+"PreparedSurfaceCache")&&(call.name.equals("lookup")||call.name.equals("store"))){call.owner=P+"GeometryLocalCache";local++;}
    if(local!=2)throw new AssertionError("Unexpected local preparation layout "+local);
    MethodNode clear=method(c,"clearPRE30");InsnList head=new InsnList();head.add(new VarInsnNode(ALOAD,0));head.add(new MethodInsnNode(INVOKESTATIC,P+"GeometryLocalCache","clear","(Ljava/lang/Object;)V",false));clear.instructions.insert(head);
   }else if(name.equals("CompactSurfaceClipper")){
    MethodNode old=method(c,"intersecting");old.name="intersectingBB1";
    MethodNode wrapper=new MethodNode(ASM9,old.access,"intersecting",old.desc,old.signature,old.exceptions.toArray(String[]::new));
    wrapper.instructions.add(new VarInsnNode(ALOAD,0));wrapper.instructions.add(new VarInsnNode(ALOAD,1));wrapper.instructions.add(new VarInsnNode(ILOAD,2));wrapper.instructions.add(new MethodInsnNode(INVOKESTATIC,P+(disabled.contains("clip")?"CompactSurfaceClipper":"PackedSurfaceClipper"),disabled.contains("clip")?"intersectingBB1":"intersecting",old.desc,false));wrapper.instructions.add(new InsnNode(ARETURN));c.methods.add(wrapper);
   }else if(name.equals("SpatialSurfaceCleaner")){
    MethodNode old=method(c,"cleanIndexed");old.name="cleanIndexedBB1";
    MethodNode wrapper=new MethodNode(ASM9,old.access,"cleanIndexed",old.desc,old.signature,old.exceptions.toArray(String[]::new));
    for(int i=0;i<7;i++)wrapper.instructions.add(new VarInsnNode(i<3?ALOAD:ILOAD,i));
    wrapper.instructions.add(new MethodInsnNode(INVOKESTATIC,P+(disabled.contains("scratch")?"SpatialSurfaceCleaner":"ScratchSurfaceCleaner"),disabled.contains("scratch")?"cleanIndexedBB1":"clean",old.desc,false));wrapper.instructions.add(new InsnNode(ARETURN));c.methods.add(wrapper);
   }else if(name.equals("SpatialSurfaceCleaner$Piece")){
    for(var f:c.fields)f.access&=~ACC_FINAL;
    MethodNode ctor=method(c,"<init>");MethodNode reset=new MethodNode(ASM9,0,"reset",ctor.desc,ctor.signature,null);ctor.accept(reset);reset.name="reset";reset.access=0;
    boolean removed=false;for(var n:reset.instructions.toArray())if(n instanceof MethodInsnNode call&&call.owner.equals("java/lang/Object")&&call.name.equals("<init>")){var prev=n.getPrevious();if(prev.getOpcode()!=ALOAD)throw new AssertionError("Piece super layout");reset.instructions.remove(prev);reset.instructions.remove(n);removed=true;break;}
    if(!removed)throw new AssertionError("Piece constructor not found");c.methods.add(reset);
   }else{
    MethodNode m=method(c,"clear");for(var n:m.instructions.toArray())if(n.getOpcode()==RETURN)for(String helper:List.of("MemoSurfaceTransform","NativeGeometryKeys","GeometryLocalCache","ScratchSurfaceCleaner"))m.instructions.insertBefore(n,new MethodInsnNode(INVOKESTATIC,P+helper,"clear","()V",false));
   }
   ClassWriter writer=new ClassWriter(ClassWriter.COMPUTE_MAXS);c.accept(writer);Path dest=Path.of(a[1]).resolve(c.name+".class");Files.createDirectories(dest.getParent());Files.write(dest,writer.toByteArray());
  }}System.out.println("SURFACE_OPT_PATCH_PASS classes=5");
 }
}
