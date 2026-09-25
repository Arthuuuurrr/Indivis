import java.nio.file.*;
import java.util.*;
import jdk.internal.org.objectweb.asm.*;
import jdk.internal.org.objectweb.asm.tree.*;

public class PatchCreatures123FamilyAndChaosBalance {
  static ClassNode read(byte[] b){ ClassNode cn=new ClassNode(); new ClassReader(b).accept(cn,0); return cn; }
  static byte[] write(ClassNode cn){
    ClassWriter cw=new ClassWriter(ClassWriter.COMPUTE_FRAMES|ClassWriter.COMPUTE_MAXS){
      @Override protected String getCommonSuperClass(String a,String b){ return "java/lang/Object"; }
    };
    cn.accept(cw); return cw.toByteArray();
  }

  static byte[] patchController(byte[] in){
    ClassNode cn=read(in);
    String owner=cn.name;
    MethodNode init=null;
    for(MethodNode m:cn.methods) if(m.name.equals("onInitialize")&&m.desc.equals("()V")) init=m;
    if(init==null) throw new IllegalStateException("onInitialize missing");
    boolean injected=false;
    for(AbstractInsnNode n=init.instructions.getFirst(); n!=null; n=n.getNext()){
      if(n instanceof VarInsnNode v && v.getOpcode()==Opcodes.ISTORE && v.var==26){
        AbstractInsnNode p=n.getPrevious();
        if(p instanceof MethodInsnNode mi && mi.owner.equals("java/lang/Math") && mi.name.equals("max") && mi.desc.equals("(II)I")){
          InsnList add=new InsnList();
          add.add(new VarInsnNode(Opcodes.ALOAD,13));
          add.add(new VarInsnNode(Opcodes.ALOAD,17));
          add.add(new VarInsnNode(Opcodes.ILOAD,26));
          add.add(new MethodInsnNode(Opcodes.INVOKESTATIC,owner,"effectiveWeight","(Ljava/util/Map;Ljava/util/List;I)I",false));
          add.add(new VarInsnNode(Opcodes.ISTORE,26));
          init.instructions.insert(n,add);
          injected=true; break;
        }
      }
    }
    if(!injected) throw new IllegalStateException("weight store hook not found");

    cn.methods.removeIf(m->m.name.equals("effectiveWeight")&&m.desc.equals("(Ljava/util/Map;Ljava/util/List;I)I"));
    MethodNode m=new MethodNode(Opcodes.ACC_PRIVATE|Opcodes.ACC_STATIC,"effectiveWeight","(Ljava/util/Map;Ljava/util/List;I)I",null,null);
    InsnList il=m.instructions;
    LabelNode noFamily=new LabelNode();
    il.add(new VarInsnNode(Opcodes.ALOAD,0));
    il.add(new LdcInsnNode("family_weight"));
    il.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE,"java/util/Map","get","(Ljava/lang/Object;)Ljava/lang/Object;",true));
    il.add(new MethodInsnNode(Opcodes.INVOKESTATIC,owner,"intVal","(Ljava/lang/Object;)Ljava/lang/Integer;",false));
    il.add(new VarInsnNode(Opcodes.ASTORE,3));
    il.add(new VarInsnNode(Opcodes.ALOAD,3));
    il.add(new JumpInsnNode(Opcodes.IFNULL,noFamily));
    il.add(new InsnNode(Opcodes.ICONST_1));
    il.add(new VarInsnNode(Opcodes.ALOAD,3));
    il.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,"java/lang/Integer","intValue","()I",false));
    il.add(new MethodInsnNode(Opcodes.INVOKESTATIC,"java/lang/Math","max","(II)I",false));
    il.add(new IntInsnNode(Opcodes.BIPUSH,12));
    il.add(new InsnNode(Opcodes.IMUL));
    il.add(new VarInsnNode(Opcodes.ISTORE,4));
    il.add(new InsnNode(Opcodes.ICONST_1));
    il.add(new VarInsnNode(Opcodes.ALOAD,1));
    il.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE,"java/util/List","size","()I",true));
    il.add(new MethodInsnNode(Opcodes.INVOKESTATIC,"java/lang/Math","max","(II)I",false));
    il.add(new VarInsnNode(Opcodes.ISTORE,5));
    il.add(new InsnNode(Opcodes.ICONST_1));
    il.add(new VarInsnNode(Opcodes.ILOAD,4));
    il.add(new VarInsnNode(Opcodes.ILOAD,5));
    il.add(new InsnNode(Opcodes.IDIV));
    il.add(new MethodInsnNode(Opcodes.INVOKESTATIC,"java/lang/Math","max","(II)I",false));
    il.add(new InsnNode(Opcodes.IRETURN));
    il.add(noFamily);
    il.add(new InsnNode(Opcodes.ICONST_1));
    il.add(new VarInsnNode(Opcodes.ILOAD,2));
    il.add(new IntInsnNode(Opcodes.BIPUSH,12));
    il.add(new InsnNode(Opcodes.IMUL));
    il.add(new MethodInsnNode(Opcodes.INVOKESTATIC,"java/lang/Math","max","(II)I",false));
    il.add(new InsnNode(Opcodes.IRETURN));
    cn.methods.add(m);

    for(MethodNode mm:cn.methods) for(AbstractInsnNode n=mm.instructions.getFirst();n!=null;n=n.getNext())
      if(n instanceof LdcInsnNode l && l.cst instanceof String s && s.equals("datapack_spawn_v1_2_5")) l.cst="datapack_spawn_v1_2_23_family_weight";
    return write(cn);
  }

  static void setProp(InsnList il, String key, String value){
    il.add(new VarInsnNode(Opcodes.ALOAD,1));
    il.add(new LdcInsnNode(key));
    il.add(new LdcInsnNode(value));
    il.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,"java/util/Properties","setProperty","(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/Object;",false));
    il.add(new InsnNode(Opcodes.POP));
  }

  static byte[] patchChaosConfig(byte[] in){
    ClassNode cn=read(in);
    MethodNode init=null;
    for(MethodNode m:cn.methods) if(m.name.equals("initialize")&&m.desc.equals("(Ljava/util/List;)V")) init=m;
    if(init==null) throw new IllegalStateException("ChaosConfig.initialize missing");
    AbstractInsnNode anchor=null;
    for(AbstractInsnNode n=init.instructions.getFirst();n!=null;n=n.getNext()){
      if(n instanceof LdcInsnNode l && "global.health_multiplier".equals(l.cst)){ anchor=n; break; }
    }
    if(anchor==null) throw new IllegalStateException("global.health_multiplier anchor missing");
    AbstractInsnNode before=anchor.getPrevious();
    InsnList add=new InsnList();
    setProp(add,"mob.maggot.health","6.0");
    setProp(add,"mob.babyspider.health","8.0");
    setProp(add,"mob.corpsefly.health","6.0");
    init.instructions.insertBefore(before,add);
    return write(cn);
  }

  public static void main(String[] args) throws Exception {
    if(args.length!=4) throw new IllegalArgumentException("controllerIn controllerOut chaosConfigIn chaosConfigOut");
    Files.write(Path.of(args[1]), patchController(Files.readAllBytes(Path.of(args[0]))));
    Files.write(Path.of(args[3]), patchChaosConfig(Files.readAllBytes(Path.of(args[2]))));
  }
}
