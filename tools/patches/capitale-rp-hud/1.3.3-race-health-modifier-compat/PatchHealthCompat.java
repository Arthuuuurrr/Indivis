import java.nio.file.*;
import jdk.internal.org.objectweb.asm.*;
import jdk.internal.org.objectweb.asm.tree.*;

public final class PatchHealthCompat {
  static final String STRICT="fr/arthur/capitale/rphud/CapitaleStrictRaceHealthFinalizer";
  static final String REBUILD="fr/arthur/capitale/rphud/CapitaleRebuildMorphServer";
  static final String HELPER="fr/arthur/capitale/rphud/RaceHealthModifierCompat";
  public static void main(String[] a)throws Exception{
    patchStrict(Path.of(a[0]),Path.of(a[1])); patchRebuild(Path.of(a[2]),Path.of(a[3])); patchAutoSync(Path.of(a[4]),Path.of(a[5])); patchNexus(Path.of(a[6]),Path.of(a[7]));
  }
  static ClassNode read(Path p)throws Exception{ClassNode n=new ClassNode(Opcodes.ASM8); new ClassReader(Files.readAllBytes(p)).accept(n,0); return n;}
  static void write(ClassNode n,Path p)throws Exception{ClassWriter w=new ClassWriter(ClassWriter.COMPUTE_MAXS); n.accept(w); Files.write(p,w.toByteArray());}
  static void patchStrict(Path in,Path out)throws Exception{
    ClassNode n=read(in); int base=0,norm=0;
    for(MethodNode m:n.methods) for(AbstractInsnNode x=m.instructions.getFirst();x!=null;x=x.getNext()) if(x instanceof MethodInsnNode mi){
      if(mi.getOpcode()==Opcodes.INVOKESTATIC && mi.owner.equals(STRICT) && mi.name.equals("getMaxHealth") && mi.desc.equals("(Ljava/lang/Object;)Ljava/lang/Float;")){mi.owner=HELPER;mi.name="getMaxHealthBase";base++;}
      else if(mi.getOpcode()==Opcodes.INVOKESTATIC && mi.owner.equals(STRICT) && mi.name.equals("normalizeCurrentHealth") && mi.desc.equals("(Ljava/lang/Object;II)V")){mi.owner=HELPER;norm++;}
    }
    if(base<4 || norm!=1) throw new IllegalStateException("unexpected strict replacements base="+base+" norm="+norm);
    write(n,out);
  }
  static void patchRebuild(Path in,Path out)throws Exception{
    ClassNode n=read(in); boolean found=false;
    for(MethodNode m:n.methods) if(m.name.equals("applyRaceHealth")&&m.desc.equals("(Ljava/lang/Object;Ljava/lang/Object;I)Z")){
      found=true; m.instructions.clear(); m.tryCatchBlocks.clear(); if(m.localVariables!=null)m.localVariables.clear();
      m.instructions.add(new VarInsnNode(Opcodes.ALOAD,1)); m.instructions.add(new VarInsnNode(Opcodes.ILOAD,2));
      m.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC,HELPER,"applyRaceBase","(Ljava/lang/Object;I)Z",false));
      m.instructions.add(new InsnNode(Opcodes.IRETURN)); m.maxStack=2; m.maxLocals=3;
    }
    if(!found)throw new IllegalStateException("applyRaceHealth not found"); write(n,out);
  }
  static void patchAutoSync(Path in,Path out)throws Exception{
    ClassNode n=read(in); int baseReads=0, totalRepl=0;
    for(MethodNode m:n.methods){
      if(m.name.equals("forceExactTotalMaxHealth")&&m.desc.equals("(Ljava/lang/Object;Ljava/lang/Object;I)Lfr/arthur/capitale/rphud/CapitaleExactPersonnagesAutoSync$ExactHealthResult;")){
        for(AbstractInsnNode x=m.instructions.getFirst();x!=null;x=x.getNext()) if(x instanceof MethodInsnNode mi && mi.getOpcode()==Opcodes.INVOKESTATIC && mi.owner.equals("fr/arthur/capitale/rphud/CapitaleExactPersonnagesAutoSync") && mi.name.equals("getMaxHealth") && mi.desc.equals("(Ljava/lang/Object;)Ljava/lang/Float;")){ mi.owner=HELPER; mi.name="getMaxHealthBase"; baseReads++; }
      }
      if(m.name.equals("normalizeCurrentHealth")&&m.desc.equals("(Ljava/lang/Object;I)Lfr/arthur/capitale/rphud/CapitaleExactPersonnagesAutoSync$HealthResult;")){
        int seen=0;
        for(AbstractInsnNode x=m.instructions.getFirst();x!=null;){ AbstractInsnNode next=x.getNext();
          if(x instanceof VarInsnNode vi && vi.getOpcode()==Opcodes.FLOAD && vi.var==4){ seen++; if(seen<=2){
            InsnList r=new InsnList(); r.add(new VarInsnNode(Opcodes.ALOAD,0)); r.add(new VarInsnNode(Opcodes.FLOAD,4)); r.add(new MethodInsnNode(Opcodes.INVOKESTATIC,HELPER,"getMaxHealthTotalOr","(Ljava/lang/Object;F)F",false));
            m.instructions.insertBefore(x,r); m.instructions.remove(x); totalRepl++;
          }} x=next;
        }
      }
    }
    if(baseReads!=3 || totalRepl!=2) throw new IllegalStateException("AutoSync replacements baseReads="+baseReads+" total="+totalRepl);
    write(n,out);
  }
  static void patchNexus(Path in,Path out)throws Exception{
    ClassNode n=read(in); int changed=0;
    for(MethodNode m:n.methods) if(m.name.equals("applyAttributes")&&m.desc.equals("(Lnet/minecraft/class_3222;FF)V")){
      int fload2=0;
      for(AbstractInsnNode x=m.instructions.getFirst();x!=null;){AbstractInsnNode next=x.getNext();
        if(x instanceof VarInsnNode vi && vi.getOpcode()==Opcodes.FLOAD && vi.var==2){fload2++; if(fload2>=3){
          InsnList repl=new InsnList(); repl.add(new VarInsnNode(Opcodes.ALOAD,0)); repl.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,"net/minecraft/class_3222","method_6063","()F",false));
          m.instructions.insertBefore(x,repl); m.instructions.remove(x); changed++; }} x=next; }
    }
    if(changed!=2)throw new IllegalStateException("expected two Nexus clamp replacements, got "+changed); write(n,out);
  }
}
