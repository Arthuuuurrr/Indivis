import java.nio.file.*;
import jdk.internal.org.objectweb.asm.*;
import jdk.internal.org.objectweb.asm.tree.*;

public final class Patch136 implements Opcodes {
    static final int API = ASM8;
    static final String MIXIN = "net/tompsen/nexuscharacters/mixin/client/CharacterCreationAppearance65Mixin";

    static AbstractInsnNode prevReal(AbstractInsnNode n) {
        for (AbstractInsnNode p=n.getPrevious(); p!=null; p=p.getPrevious()) if (p.getOpcode()>=0) return p;
        return null;
    }
    static AbstractInsnNode nextReal(AbstractInsnNode n) {
        for (AbstractInsnNode p=n.getNext(); p!=null; p=p.getNext()) if (p.getOpcode()>=0) return p;
        return null;
    }
    static void clear(MethodNode m) {
        m.instructions.clear(); m.tryCatchBlocks.clear();
        if (m.localVariables != null) m.localVariables.clear();
    }
    static void delegateStatic(MethodNode m, String owner, String name, String desc, int ret) {
        clear(m);
        Type[] args=Type.getArgumentTypes(m.desc); int var=0;
        for(Type t:args){ m.instructions.add(new VarInsnNode(t.getOpcode(ILOAD),var)); var+=t.getSize(); }
        m.instructions.add(new MethodInsnNode(INVOKESTATIC,owner,name,desc,false));
        m.instructions.add(new InsnNode(ret)); m.maxLocals=var; m.maxStack=Math.max(2,var);
    }

    static byte[] patchCreation(byte[] in) {
        ClassNode cn=new ClassNode(API); new ClassReader(in).accept(cn,0);
        int initString=0, lambda=0, normalize=0, labels=0;
        for(MethodNode m:cn.methods){
            for(AbstractInsnNode n=m.instructions.getFirst(); n!=null; n=n.getNext()) {
                if(n instanceof LdcInsnNode l && "Tenue 1/38".equals(l.cst)){ l.cst="Tenue 1/33"; initString++; }
            }
            if(m.name.equals("lambda$nexuscharacters$install$4") && m.desc.equals("(Lnet/minecraft/class_4185;)V")) {
                clear(m);
                InsnList x=m.instructions;
                x.add(new VarInsnNode(ALOAD,0));
                x.add(new VarInsnNode(ALOAD,0));
                x.add(new FieldInsnNode(GETFIELD,MIXIN,"nexuscharacters$outfit","I"));
                x.add(new MethodInsnNode(INVOKESTATIC,"net/tompsen/nexuscharacters/OutfitCatalogSupport","next","(I)I",false));
                x.add(new FieldInsnNode(PUTFIELD,MIXIN,"nexuscharacters$outfit","I"));
                x.add(new VarInsnNode(ALOAD,0));
                x.add(new MethodInsnNode(INVOKEVIRTUAL,MIXIN,"nexuscharacters$changed","()V",false));
                x.add(new InsnNode(RETURN));
                m.maxLocals=2; m.maxStack=2; lambda++;
            }
            if(m.name.equals("nexuscharacters$refresh") && m.desc.equals("()V")) {
                InsnList head=new InsnList();
                head.add(new VarInsnNode(ALOAD,0));
                head.add(new VarInsnNode(ALOAD,0));
                head.add(new FieldInsnNode(GETFIELD,MIXIN,"nexuscharacters$outfit","I"));
                head.add(new MethodInsnNode(INVOKESTATIC,"net/tompsen/nexuscharacters/OutfitCatalogSupport","normalize","(I)I",false));
                head.add(new FieldInsnNode(PUTFIELD,MIXIN,"nexuscharacters$outfit","I"));
                m.instructions.insert(head); normalize++;
            }
            if(m.name.equals("nexuscharacters$setLabels") && m.desc.equals("(Z)V")) {
                for(AbstractInsnNode n=m.instructions.getFirst(); n!=null;) {
                    AbstractInsnNode nx=n.getNext();
                    if(n instanceof InvokeDynamicInsnNode indy && indy.desc.equals("(I)Ljava/lang/String;")) {
                        AbstractInsnNode p=prevReal(n);
                        if(p instanceof FieldInsnNode fi && fi.getOpcode()==GETFIELD && fi.owner.equals(MIXIN) && fi.name.equals("nexuscharacters$outfit")) {
                            m.instructions.set(n,new MethodInsnNode(INVOKESTATIC,"net/tompsen/nexuscharacters/OutfitCatalogSupport","label","(I)Ljava/lang/String;",false));
                            labels++;
                        }
                    }
                    n=nx;
                }
            }
        }
        if(initString!=1 || lambda!=1 || normalize!=1 || labels<1)
            throw new IllegalStateException("creation mismatch init="+initString+" lambda="+lambda+" normalize="+normalize+" labels="+labels);
        ClassWriter cw=new ClassWriter(ClassWriter.COMPUTE_MAXS); cn.accept(cw); return cw.toByteArray();
    }

    static byte[] patchDynamic(byte[] in) {
        ClassNode cn=new ClassNode(API); new ClassReader(in).accept(cn,0); int changed=0;
        for(MethodNode m:cn.methods) {
            if(!(m.name.equals("compose69")||m.name.equals("composeLegacy"))) continue;
            for(AbstractInsnNode n=m.instructions.getFirst();n!=null;n=n.getNext()) {
                if(n instanceof MethodInsnNode mi && mi.getOpcode()==INVOKEVIRTUAL && mi.name.equals("outfit") && mi.desc.equals("()I")) {
                    m.instructions.insert(n,new MethodInsnNode(INVOKESTATIC,"net/tompsen/nexuscharacters/OutfitCatalogSupport","normalize","(I)I",false));
                    changed++;
                }
            }
        }
        if(changed!=2) throw new IllegalStateException("dynamic outfit normalize count="+changed);
        ClassWriter cw=new ClassWriter(ClassWriter.COMPUTE_MAXS); cn.accept(cw); return cw.toByteArray();
    }

    static byte[] patchColors(byte[] in) {
        ClassNode cn=new ClassNode(API); new ClassReader(in).accept(cn,0);
        int id=0,argb=0,valid=0,label=0,cycle=0;
        for(MethodNode m:cn.methods) {
            if(m.name.equals("colorId") && m.desc.equals("(Ljava/lang/String;)Ljava/lang/String;")) {
                delegateStatic(m,"net/tompsen/nexuscharacters/BeardPaletteSupport","canonicalId",m.desc,ARETURN); id++;
            } else if(m.name.equals("colorArgb") && m.desc.equals("(Ljava/lang/String;)I")) {
                delegateStatic(m,"net/tompsen/nexuscharacters/BeardPaletteSupport","argb",m.desc,IRETURN); argb++;
            } else if(m.name.equals("isColorId") && m.desc.equals("(Ljava/lang/String;)Z")) {
                delegateStatic(m,"net/tompsen/nexuscharacters/BeardPaletteSupport","isColorId",m.desc,IRETURN); valid++;
            } else if(m.name.equals("colorLabel") && m.desc.equals("(Ljava/lang/String;)Ljava/lang/String;")) {
                delegateStatic(m,"net/tompsen/nexuscharacters/BeardPaletteSupport","label",m.desc,ARETURN); label++;
            } else if(m.name.equals("cycleScreenColor") && m.desc.equals("(Ljava/lang/Object;)V")) {
                clear(m); InsnList x=m.instructions;
                x.add(new FieldInsnNode(GETSTATIC,"net/tompsen/nexuscharacters/CosmeticColorSupport","SCREEN_COLORS","Ljava/util/Map;"));
                x.add(new VarInsnNode(ALOAD,0)); x.add(new LdcInsnNode("brown"));
                x.add(new MethodInsnNode(INVOKEINTERFACE,"java/util/Map","getOrDefault","(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;",true));
                x.add(new TypeInsnNode(CHECKCAST,"java/lang/String")); x.add(new VarInsnNode(ASTORE,1));
                x.add(new FieldInsnNode(GETSTATIC,"net/tompsen/nexuscharacters/CosmeticColorSupport","SCREEN_COLORS","Ljava/util/Map;"));
                x.add(new VarInsnNode(ALOAD,0)); x.add(new VarInsnNode(ALOAD,1));
                x.add(new MethodInsnNode(INVOKESTATIC,"net/tompsen/nexuscharacters/BeardPaletteSupport","nextId","(Ljava/lang/String;)Ljava/lang/String;",false));
                x.add(new MethodInsnNode(INVOKEINTERFACE,"java/util/Map","put","(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;",true));
                x.add(new InsnNode(POP)); x.add(new InsnNode(RETURN)); m.maxLocals=2; m.maxStack=3; cycle++;
            }
        }
        if(id!=1||argb!=1||valid!=1||label!=1||cycle!=1)
            throw new IllegalStateException("color mismatch "+id+","+argb+","+valid+","+label+","+cycle);
        ClassWriter cw=new ClassWriter(ClassWriter.COMPUTE_MAXS);cn.accept(cw);return cw.toByteArray();
    }

    static byte[] patchWorldless(byte[] in) {
        ClassNode cn=new ClassNode(API); new ClassReader(in).accept(cn,0); int safe=0,yaw=0;
        for(MethodNode m:cn.methods) {
            if(m.name.equals("safeColor") && m.desc.equals("(Ljava/lang/String;)Ljava/lang/String;")) {
                delegateStatic(m,"net/tompsen/nexuscharacters/BeardPaletteSupport","canonicalId",m.desc,ARETURN); safe++;
            }
            if(m.name.equals("render")) {
                for(AbstractInsnNode n=m.instructions.getFirst(); n!=null;) {
                    AbstractInsnNode nx=n.getNext();
                    if(n instanceof MethodInsnNode mi && mi.getOpcode()==INVOKEVIRTUAL && mi.owner.equals("java/lang/reflect/Field") && mi.name.equals("getFloat")) {
                        AbstractInsnNode a=prevReal(n);
                        AbstractInsnNode b=a==null?null:prevReal(a);
                        AbstractInsnNode next=nextReal(n);
                        if(a instanceof VarInsnNode vi && vi.getOpcode()==ALOAD && vi.var==15
                                && b instanceof FieldInsnNode fi && fi.getOpcode()==GETSTATIC && fi.owner.equals("net/tompsen/nexuscharacters/WorldlessBeardWidgetRenderer") && fi.name.equals("Y_ROT")
                                && next instanceof MethodInsnNode sm && sm.owner.equals("java/lang/reflect/Field") && sm.name.equals("setFloat")) {
                            m.instructions.remove(b); m.instructions.remove(a);
                            InsnList repl=new InsnList();
                            repl.add(new LdcInsnNode(30.0f));
                            repl.add(new MethodInsnNode(INVOKESTATIC,"net/tompsen/nexuscharacters/PreviewDragInput","getYawDegrees","()F",false));
                            repl.add(new InsnNode(FADD));
                            m.instructions.insertBefore(n,repl);
                            m.instructions.remove(n); yaw++;
                        }
                    }
                    n=nx;
                }
            }
        }
        if(safe!=1||yaw!=1) throw new IllegalStateException("worldless mismatch safe="+safe+" yaw="+yaw);
        ClassWriter cw=new ClassWriter(ClassWriter.COMPUTE_MAXS);cn.accept(cw);return cw.toByteArray();
    }

    static byte[] patchBeardCycle(byte[] in) {
        ClassNode cn=new ClassNode(API); new ClassReader(in).accept(cn,0); int changed=0;
        for(MethodNode m:cn.methods) if(m.name.equals("nextForRace") && m.desc.equals("(Lnet/tompsen/nexuscharacters/CharacterRace;Lnet/tompsen/nexuscharacters/CharacterCosmetics$BeardStyle;)Lnet/tompsen/nexuscharacters/CharacterCosmetics$BeardStyle;")) {
            delegateStatic(m,"net/tompsen/nexuscharacters/BeardCycleSupport","nextForRace",m.desc,ARETURN); changed++;
        }
        if(changed!=1) throw new IllegalStateException("beard cycle count="+changed);
        ClassWriter cw=new ClassWriter(ClassWriter.COMPUTE_MAXS);cn.accept(cw);return cw.toByteArray();
    }

    static void patch(Path root,String rel, java.util.function.Function<byte[],byte[]> f)throws Exception{
        Path p=root.resolve(rel); Files.write(p,f.apply(Files.readAllBytes(p)));
    }
    public static void main(String[] args)throws Exception{
        Path root=Paths.get(args[0]);
        patch(root,"net/tompsen/nexuscharacters/mixin/client/CharacterCreationAppearance65Mixin.class",Patch136::patchCreation);
        patch(root,"net/tompsen/nexuscharacters/DynamicAppearanceSupport.class",Patch136::patchDynamic);
        patch(root,"net/tompsen/nexuscharacters/CosmeticColorSupport.class",Patch136::patchColors);
        patch(root,"net/tompsen/nexuscharacters/WorldlessBeardWidgetRenderer.class",Patch136::patchWorldless);
        patch(root,"net/tompsen/nexuscharacters/NordicBeardSupport.class",Patch136::patchBeardCycle);
    }
}
