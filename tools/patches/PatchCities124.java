import java.nio.file.*;
import jdk.internal.org.objectweb.asm.*;
import jdk.internal.org.objectweb.asm.tree.*;

public class PatchCities124 {
  static final String SPAWN="fr/hautecapitale/creatures/spawn/CapitaleCreaturesDatapackSpawnController";
  static final String PATROL="fr/hautecapitale/creatures/spawn/CapitaleCreaturesOrcPatrol1212";
  static final String[] CITIES={
    "capitale:capitale",
    "indivis:lion_port","indivis:clairval","indivis:haute_rive","indivis:ilystara",
    "indivis:sylvarhen","indivis:avaleiv","indivis:skarnfjord","indivis:durak_vor",
    "indivis:blanche_fleche","indivis:port_levant","indivis:sillons_d_or",
    "indivis:sombrefleche","indivis:clos_des_ormes","indivis:aubecourt"
  };
  static final String[] HARD_EXCLUDED={
    "capitale:capitale","capitale:donjon","indivis:corruption",
    "indivis:lion_port","indivis:clairval","indivis:haute_rive","indivis:ilystara",
    "indivis:sylvarhen","indivis:avaleiv","indivis:skarnfjord","indivis:durak_vor",
    "indivis:blanche_fleche","indivis:port_levant","indivis:sillons_d_or",
    "indivis:sombrefleche","indivis:clos_des_ormes","indivis:aubecourt",
    "minecraft:river","minecraft:frozen_river",
    "minecraft:ocean","minecraft:cold_ocean","minecraft:lukewarm_ocean","minecraft:warm_ocean",
    "minecraft:frozen_ocean","minecraft:deep_ocean","minecraft:deep_cold_ocean",
    "minecraft:deep_lukewarm_ocean","minecraft:deep_frozen_ocean"
  };

  static ClassNode read(Path p) throws Exception {
    ClassNode cn=new ClassNode(); new ClassReader(Files.readAllBytes(p)).accept(cn,0); return cn;
  }
  static void write(ClassNode cn, Path p) throws Exception {
    ClassWriter cw=new ClassWriter(ClassWriter.COMPUTE_FRAMES|ClassWriter.COMPUTE_MAXS){
      @Override protected String getCommonSuperClass(String a,String b){return "java/lang/Object";}
    };
    cn.accept(cw); Files.createDirectories(p.getParent()); Files.write(p,cw.toByteArray());
  }
  static MethodNode find(ClassNode cn,String name,String desc){
    for(MethodNode m:cn.methods) if(m.name.equals(name)&&m.desc.equals(desc)) return m;
    throw new IllegalStateException(cn.name+" missing "+name+desc);
  }

  static void patchSpawn(Path in,Path out)throws Exception{
    ClassNode cn=read(in);
    if(!SPAWN.equals(cn.name)) throw new IllegalStateException(cn.name);
    MethodNode m=find(cn,"clearTargets","(Ljava/lang/String;)Ljava/util/function/Predicate;");
    m.instructions.clear(); m.tryCatchBlocks.clear(); m.localVariables=null;
    InsnList il=m.instructions;
    il.add(new FieldInsnNode(Opcodes.GETSTATIC,"java/lang/System","out","Ljava/io/PrintStream;"));
    il.add(new LdcInsnNode("[Capitale Creatures] 1.2.24 exact city RegistryKey no-spawn guard active (15 city biomes)"));
    il.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,"java/io/PrintStream","println","(Ljava/lang/String;)V",false));
    il.add(new LdcInsnNode("all_overworld"));
    il.add(new MethodInsnNode(Opcodes.INVOKESTATIC,SPAWN,"tag","(Ljava/lang/String;)Ljava/util/function/Predicate;",false));
    il.add(new VarInsnNode(Opcodes.ASTORE,1));
    il.add(new TypeInsnNode(Opcodes.NEW,"java/util/ArrayList")); il.add(new InsnNode(Opcodes.DUP));
    il.add(new MethodInsnNode(Opcodes.INVOKESPECIAL,"java/util/ArrayList","<init>","()V",false));
    il.add(new VarInsnNode(Opcodes.ASTORE,2));
    for(String s:CITIES){
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
    m.maxLocals=4; m.maxStack=6;
    write(cn,out);
  }

  static void patchPatrol(Path in,Path out)throws Exception{
    ClassNode cn=read(in);
    if(!PATROL.equals(cn.name)) throw new IllegalStateException(cn.name);
    MethodNode m=find(cn,"hardNormalizeBiomeId","(Ljava/lang/Object;)Ljava/lang/String;");
    m.instructions.clear(); m.tryCatchBlocks.clear(); m.localVariables=null;
    InsnList il=m.instructions;
    il.add(new VarInsnNode(Opcodes.ALOAD,0));
    il.add(new MethodInsnNode(Opcodes.INVOKESTATIC,"java/lang/String","valueOf","(Ljava/lang/Object;)Ljava/lang/String;",false));
    il.add(new VarInsnNode(Opcodes.ASTORE,1));
    LabelNode excluded=new LabelNode();
    for(String id:HARD_EXCLUDED){
      il.add(new LdcInsnNode(id));
      il.add(new VarInsnNode(Opcodes.ALOAD,1));
      il.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,"java/lang/String","equals","(Ljava/lang/Object;)Z",false));
      il.add(new JumpInsnNode(Opcodes.IFNE,excluded));
    }
    il.add(new VarInsnNode(Opcodes.ALOAD,1));
    il.add(new InsnNode(Opcodes.ARETURN));
    il.add(excluded);
    il.add(new LdcInsnNode("capitale:donjon"));
    il.add(new InsnNode(Opcodes.ARETURN));
    m.maxLocals=2; m.maxStack=2;
    write(cn,out);
  }

  public static void main(String[] args)throws Exception{
    if(args.length!=4) throw new IllegalArgumentException("spawnIn spawnOut patrolIn patrolOut");
    patchSpawn(Path.of(args[0]),Path.of(args[1]));
    patchPatrol(Path.of(args[2]),Path.of(args[3]));
  }
}
