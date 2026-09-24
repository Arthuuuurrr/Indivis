import java.nio.file.*;
import java.util.*;
import jdk.internal.org.objectweb.asm.*;
import jdk.internal.org.objectweb.asm.tree.*;

public class PatchCreatures119 {
  static final String SPAWN = "fr/hautecapitale/creatures/spawn/CapitaleCreaturesDatapackSpawnController";
  static final String PATROL = "fr/hautecapitale/creatures/spawn/CapitaleCreaturesOrcPatrol1212";
  static final String[] CITIES = {
    "capitale:capitale",
    "indivis:lion_port","indivis:clairval","indivis:haute_rive","indivis:ilystara",
    "indivis:sylvarhen","indivis:avaleiv","indivis:skarnfjord","indivis:durak_vor"
  };

  static ClassNode read(Path p) throws Exception {
    ClassNode cn = new ClassNode();
    new ClassReader(Files.readAllBytes(p)).accept(cn, 0);
    return cn;
  }
  static void write(ClassNode cn, Path p) throws Exception {
    ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS) {
      @Override protected String getCommonSuperClass(String a, String b) { return "java/lang/Object"; }
    };
    cn.accept(cw);
    Files.write(p, cw.toByteArray());
  }
  static void pushInt(InsnList il, int v) {
    if (v >= -1 && v <= 5) il.add(new InsnNode(v == -1 ? Opcodes.ICONST_M1 : Opcodes.ICONST_0 + v));
    else if (v >= Byte.MIN_VALUE && v <= Byte.MAX_VALUE) il.add(new IntInsnNode(Opcodes.BIPUSH, v));
    else il.add(new IntInsnNode(Opcodes.SIPUSH, v));
  }

  static void patchSpawnController(Path in, Path out) throws Exception {
    ClassNode cn = read(in);
    if (!SPAWN.equals(cn.name)) throw new IllegalStateException(cn.name);
    MethodNode target = null;
    for (MethodNode m: cn.methods) if (m.name.equals("clearTargets") && m.desc.equals("(Ljava/lang/String;)Ljava/util/function/Predicate;")) target=m;
    if (target == null) throw new IllegalStateException("clearTargets missing");

    target.instructions.clear(); target.tryCatchBlocks.clear(); target.localVariables = null;
    InsnList il = target.instructions;
    il.add(new FieldInsnNode(Opcodes.GETSTATIC,"java/lang/System","out","Ljava/io/PrintStream;"));
    il.add(new LdcInsnNode("[Capitale Creatures] 1.2.19 exact city RegistryKey no-spawn guard active (9 city biomes)"));
    il.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,"java/io/PrintStream","println","(Ljava/lang/String;)V",false));
    il.add(new LdcInsnNode("all_overworld"));
    il.add(new MethodInsnNode(Opcodes.INVOKESTATIC,SPAWN,"tag","(Ljava/lang/String;)Ljava/util/function/Predicate;",false));
    il.add(new VarInsnNode(Opcodes.ASTORE,1));
    il.add(new TypeInsnNode(Opcodes.NEW,"java/util/ArrayList")); il.add(new InsnNode(Opcodes.DUP));
    il.add(new MethodInsnNode(Opcodes.INVOKESPECIAL,"java/util/ArrayList","<init>","()V",false));
    il.add(new VarInsnNode(Opcodes.ASTORE,2));
    for (String s: CITIES) {
      String[] a=s.split(":",2);
      il.add(new VarInsnNode(Opcodes.ALOAD,2));
      il.add(new FieldInsnNode(Opcodes.GETSTATIC,"net/minecraft/class_7924","field_41236","Lnet/minecraft/class_5321;"));
      il.add(new LdcInsnNode(a[0])); il.add(new LdcInsnNode(a[1]));
      il.add(new MethodInsnNode(Opcodes.INVOKESTATIC,SPAWN,"id","(Ljava/lang/String;Ljava/lang/String;)Lnet/minecraft/class_2960;",false));
      il.add(new MethodInsnNode(Opcodes.INVOKESTATIC,"net/minecraft/class_5321","method_29179","(Lnet/minecraft/class_5321;Lnet/minecraft/class_2960;)Lnet/minecraft/class_5321;",false));
      il.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,"java/util/ArrayList","add","(Ljava/lang/Object;)Z",false));
      il.add(new InsnNode(Opcodes.POP));
    }
    il.add(new VarInsnNode(Opcodes.ALOAD,2));
    il.add(new MethodInsnNode(Opcodes.INVOKESTATIC,"net/fabricmc/fabric/api/biome/v1/BiomeSelectors","includeByKey","(Ljava/util/Collection;)Ljava/util/function/Predicate;",false));
    il.add(new VarInsnNode(Opcodes.ASTORE,3));
    il.add(new VarInsnNode(Opcodes.ALOAD,1)); il.add(new VarInsnNode(Opcodes.ALOAD,3));
    il.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE,"java/util/function/Predicate","or","(Ljava/util/function/Predicate;)Ljava/util/function/Predicate;",true));
    il.add(new InsnNode(Opcodes.ARETURN));
    target.maxLocals=4; target.maxStack=6;
    write(cn,out);
  }

  static void patchPatrol(Path in, Path out) throws Exception {
    ClassNode cn = read(in);
    if (!PATROL.equals(cn.name)) throw new IllegalStateException(cn.name);
    MethodNode spawn = null, tick = null;
    for(MethodNode m:cn.methods){
      if(m.name.equals("spawnPatrol")) spawn=m;
      if(m.name.equals("tickWorld") && m.desc.equals("(Ljava/lang/Object;)V")) tick=m;
    }
    if(spawn==null||tick==null) throw new IllegalStateException("methods missing");
    boolean marked=false;
    for(AbstractInsnNode n=spawn.instructions.getFirst();n!=null;n=n.getNext()){
      if(n instanceof IincInsnNode ii && ii.var==3 && ii.incr==1){
        InsnList add=new InsnList();
        add.add(new VarInsnNode(Opcodes.ALOAD,8));
        add.add(new MethodInsnNode(Opcodes.INVOKESTATIC,PATROL,"markPatrolEntity","(Ljava/lang/Object;)V",false));
        spawn.instructions.insertBefore(n,add); marked=true; break;
      }
    }
    if(!marked) throw new IllegalStateException("spawn success hook not found");

    boolean injected=false;
    for(AbstractInsnNode n=tick.instructions.getFirst();n!=null;n=n.getNext()){
      if(n instanceof FieldInsnNode fi && fi.getOpcode()==Opcodes.GETSTATIC && fi.owner.equals(PATROL) && fi.name.equals("overworldTicks")){
        InsnList add=new InsnList();
        add.add(new VarInsnNode(Opcodes.ALOAD,0));
        add.add(new MethodInsnNode(Opcodes.INVOKESTATIC,PATROL,"purgePatrolsInExcludedBiomes","(Ljava/lang/Object;)V",false));
        tick.instructions.insertBefore(n,add); injected=true; break;
      }
    }
    if(!injected) throw new IllegalStateException("tick hook not found");

    cn.methods.removeIf(m -> (m.name.equals("markPatrolEntity") && m.desc.equals("(Ljava/lang/Object;)V")) ||
                             (m.name.equals("purgePatrolsInExcludedBiomes") && m.desc.equals("(Ljava/lang/Object;)V")));

    MethodNode mark=new MethodNode(Opcodes.ACC_PRIVATE|Opcodes.ACC_STATIC,"markPatrolEntity","(Ljava/lang/Object;)V",null,null);
    InsnList mi=mark.instructions;
    LabelNode done=new LabelNode();
    mi.add(new VarInsnNode(Opcodes.ALOAD,0)); mi.add(new JumpInsnNode(Opcodes.IFNULL,done));
    mi.add(new VarInsnNode(Opcodes.ALOAD,0)); mi.add(new TypeInsnNode(Opcodes.CHECKCAST,"net/minecraft/class_1297"));
    mi.add(new LdcInsnNode("indivis_orc_patrol"));
    mi.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,"net/minecraft/class_1297","method_5780","(Ljava/lang/String;)Z",false));
    mi.add(new InsnNode(Opcodes.POP)); mi.add(done); mi.add(new InsnNode(Opcodes.RETURN));
    cn.methods.add(mark);

    MethodNode purge=new MethodNode(Opcodes.ACC_PRIVATE|Opcodes.ACC_STATIC,"purgePatrolsInExcludedBiomes","(Ljava/lang/Object;)V",null,new String[]{"java/lang/Exception"});
    InsnList pi=purge.instructions;
    LabelNode ret=new LabelNode(), loop=new LabelNode(), next=new LabelNode(), afterLoop=new LabelNode();
    pi.add(new FieldInsnNode(Opcodes.GETSTATIC,PATROL,"worldEntities","Ljava/lang/reflect/Method;"));
    pi.add(new VarInsnNode(Opcodes.ALOAD,0)); pi.add(new InsnNode(Opcodes.ICONST_0)); pi.add(new TypeInsnNode(Opcodes.ANEWARRAY,"java/lang/Object"));
    pi.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,"java/lang/reflect/Method","invoke","(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;",false));
    pi.add(new VarInsnNode(Opcodes.ASTORE,1));
    pi.add(new VarInsnNode(Opcodes.ALOAD,1)); pi.add(new TypeInsnNode(Opcodes.INSTANCEOF,"java/lang/Iterable")); pi.add(new JumpInsnNode(Opcodes.IFEQ,ret));
    pi.add(new VarInsnNode(Opcodes.ALOAD,1)); pi.add(new TypeInsnNode(Opcodes.CHECKCAST,"java/lang/Iterable"));
    pi.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE,"java/lang/Iterable","iterator","()Ljava/util/Iterator;",true)); pi.add(new VarInsnNode(Opcodes.ASTORE,2));
    pi.add(loop);
    pi.add(new VarInsnNode(Opcodes.ALOAD,2)); pi.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE,"java/util/Iterator","hasNext","()Z",true)); pi.add(new JumpInsnNode(Opcodes.IFEQ,afterLoop));
    pi.add(new VarInsnNode(Opcodes.ALOAD,2)); pi.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE,"java/util/Iterator","next","()Ljava/lang/Object;",true)); pi.add(new VarInsnNode(Opcodes.ASTORE,3));
    pi.add(new VarInsnNode(Opcodes.ALOAD,3)); pi.add(new TypeInsnNode(Opcodes.INSTANCEOF,"net/minecraft/class_1297")); pi.add(new JumpInsnNode(Opcodes.IFEQ,next));
    pi.add(new VarInsnNode(Opcodes.ALOAD,3)); pi.add(new TypeInsnNode(Opcodes.CHECKCAST,"net/minecraft/class_1297"));
    pi.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,"net/minecraft/class_1297","method_5752","()Ljava/util/Set;",false));
    pi.add(new LdcInsnNode("indivis_orc_patrol")); pi.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE,"java/util/Set","contains","(Ljava/lang/Object;)Z",true)); pi.add(new JumpInsnNode(Opcodes.IFEQ,next));
    String[] fields={"entityX","entityY","entityZ"}; int[] vars={4,5,6};
    for(int k=0;k<3;k++){
      pi.add(new FieldInsnNode(Opcodes.GETSTATIC,PATROL,fields[k],"Ljava/lang/reflect/Method;")); pi.add(new VarInsnNode(Opcodes.ALOAD,3));
      pi.add(new InsnNode(Opcodes.ICONST_0)); pi.add(new TypeInsnNode(Opcodes.ANEWARRAY,"java/lang/Object"));
      pi.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,"java/lang/reflect/Method","invoke","(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;",false));
      pi.add(new TypeInsnNode(Opcodes.CHECKCAST,"java/lang/Number")); pi.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,"java/lang/Number","doubleValue","()D",false));
      pi.add(new MethodInsnNode(Opcodes.INVOKESTATIC,"java/lang/Math","floor","(D)D",false)); pi.add(new InsnNode(Opcodes.D2I)); pi.add(new VarInsnNode(Opcodes.ISTORE,vars[k]));
    }
    int[] offs={0,2,6,-2};
    LabelNode discard=new LabelNode();
    for(int off:offs){
      pi.add(new FieldInsnNode(Opcodes.GETSTATIC,PATROL,"config","Lfr/hautecapitale/creatures/spawn/CapitaleCreaturesOrcPatrol1212$Config;"));
      pi.add(new FieldInsnNode(Opcodes.GETFIELD,"fr/hautecapitale/creatures/spawn/CapitaleCreaturesOrcPatrol1212$Config","excludeBiomes","Ljava/util/Set;"));
      pi.add(new VarInsnNode(Opcodes.ALOAD,0)); pi.add(new VarInsnNode(Opcodes.ILOAD,4)); pi.add(new VarInsnNode(Opcodes.ILOAD,5));
      if(off>0){ pushInt(pi,off); pi.add(new InsnNode(Opcodes.IADD)); } else if(off<0){ pushInt(pi,-off); pi.add(new InsnNode(Opcodes.ISUB)); }
      pi.add(new VarInsnNode(Opcodes.ILOAD,6));
      pi.add(new MethodInsnNode(Opcodes.INVOKESTATIC,PATROL,"biomeId","(Ljava/lang/Object;III)Ljava/lang/String;",false));
      pi.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE,"java/util/Set","contains","(Ljava/lang/Object;)Z",true));
      pi.add(new JumpInsnNode(Opcodes.IFNE,discard));
    }
    pi.add(new JumpInsnNode(Opcodes.GOTO,next));
    pi.add(discard);
    pi.add(new VarInsnNode(Opcodes.ALOAD,3)); pi.add(new TypeInsnNode(Opcodes.CHECKCAST,"net/minecraft/class_1297"));
    pi.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,"net/minecraft/class_1297","method_31472","()V",false));
    pi.add(next); pi.add(new JumpInsnNode(Opcodes.GOTO,loop));
    pi.add(afterLoop); pi.add(ret); pi.add(new InsnNode(Opcodes.RETURN));
    cn.methods.add(purge);

    for(MethodNode m:cn.methods) for(AbstractInsnNode n=m.instructions.getFirst();n!=null;n=n.getNext())
      if(n instanceof LdcInsnNode l && l.cst instanceof String s && s.contains("1.2.18 city no-spawn"))
        l.cst=s.replace("1.2.18","1.2.19");

    write(cn,out);
  }

  public static void main(String[] args) throws Exception {
    if(args.length!=4) throw new IllegalArgumentException("spawnIn spawnOut patrolIn patrolOut");
    patchSpawnController(Path.of(args[0]),Path.of(args[1]));
    patchPatrol(Path.of(args[2]),Path.of(args[3]));
  }
}
