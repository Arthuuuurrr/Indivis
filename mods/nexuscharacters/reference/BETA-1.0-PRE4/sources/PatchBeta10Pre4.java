import java.nio.file.*;
import jdk.internal.org.objectweb.asm.*;
import jdk.internal.org.objectweb.asm.tree.*;

public final class PatchBeta10Pre4 implements Opcodes {
    static final int API = ASM8;

    static void clear(MethodNode m) {
        m.instructions.clear();
        m.tryCatchBlocks.clear();
        if (m.localVariables != null) m.localVariables.clear();
    }

    static byte[] patchCosmeticColor(byte[] in) {
        ClassNode cn = new ClassNode(API); new ClassReader(in).accept(cn,0);
        int base=0, apply=0;
        for (MethodNode m : cn.methods) {
            if (m.name.equals("baseStyle") && m.desc.equals("(Ljava/lang/String;)Ljava/lang/String;")) {
                clear(m);
                m.instructions.add(new VarInsnNode(ALOAD,0));
                m.instructions.add(new MethodInsnNode(INVOKESTATIC,
                        "net/tompsen/nexuscharacters/CulturalBeardSupport","baseStyle",
                        "(Ljava/lang/String;)Ljava/lang/String;",false));
                m.instructions.add(new InsnNode(ARETURN));
                m.maxLocals=1; m.maxStack=1; base++;
            } else if (m.name.equals("applyColorToDto") && m.desc.equals("(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;")) {
                clear(m);
                m.instructions.add(new VarInsnNode(ALOAD,0));
                m.instructions.add(new InsnNode(ARETURN));
                m.maxLocals=2; m.maxStack=1; apply++;
            }
        }
        if(base!=1||apply!=1) throw new IllegalStateException("CosmeticColor patch mismatch base="+base+" apply="+apply);
        ClassWriter cw=new ClassWriter(ClassWriter.COMPUTE_MAXS); cn.accept(cw); return cw.toByteArray();
    }

    static byte[] patchPalette(byte[] in) {
        ClassNode cn=new ClassNode(API); new ClassReader(in).accept(cn,0); int changed=0;
        for(MethodNode m:cn.methods) if(m.name.equals("canonicalId")&&m.desc.equals("(Ljava/lang/String;)Ljava/lang/String;")) {
            clear(m);
            m.instructions.add(new VarInsnNode(ALOAD,0));
            m.instructions.add(new MethodInsnNode(INVOKESTATIC,"net/tompsen/nexuscharacters/CulturalBeardSupport","colorId","(Ljava/lang/String;)Ljava/lang/String;",false));
            m.instructions.add(new InsnNode(ARETURN)); m.maxLocals=1;m.maxStack=1;changed++;
        }
        if(changed!=1) throw new IllegalStateException("Palette canonicalId count="+changed);
        ClassWriter cw=new ClassWriter(ClassWriter.COMPUTE_MAXS);cn.accept(cw);return cw.toByteArray();
    }

    static byte[] patchNordic(byte[] in) {
        ClassNode cn=new ClassNode(API); new ClassReader(in).accept(cn,0); int changed=0;
        for(MethodNode m:cn.methods) if(m.name.equals("normalize")&&m.desc.equals("(Ljava/lang/Object;Ljava/lang/String;)Ljava/lang/String;")) {
            clear(m);
            m.instructions.add(new VarInsnNode(ALOAD,0));
            m.instructions.add(new VarInsnNode(ALOAD,1));
            m.instructions.add(new MethodInsnNode(INVOKESTATIC,"net/tompsen/nexuscharacters/CulturalBeardSupport","normalize","(Ljava/lang/Object;Ljava/lang/String;)Ljava/lang/String;",false));
            m.instructions.add(new InsnNode(ARETURN));m.maxLocals=2;m.maxStack=2;changed++;
        }
        if(changed!=1) throw new IllegalStateException("Nordic normalize count="+changed);
        ClassWriter cw=new ClassWriter(ClassWriter.COMPUTE_MAXS);cn.accept(cw);return cw.toByteArray();
    }

    static byte[] patchWorldless(byte[] in) {
        ClassNode cn=new ClassNode(API); new ClassReader(in).accept(cn,0);
        int base=0,color=0,safe=0;
        for(MethodNode m:cn.methods) {
            if(m.name.equals("baseStyle")&&m.desc.equals("(Ljava/lang/String;)Ljava/lang/String;")) {
                clear(m);m.instructions.add(new VarInsnNode(ALOAD,0));
                m.instructions.add(new MethodInsnNode(INVOKESTATIC,"net/tompsen/nexuscharacters/CulturalBeardSupport","previewModelKey","(Ljava/lang/String;)Ljava/lang/String;",false));
                m.instructions.add(new InsnNode(ARETURN));m.maxLocals=1;m.maxStack=1;base++;
            } else if(m.name.equals("colorId")&&m.desc.equals("(Ljava/lang/String;)Ljava/lang/String;")) {
                clear(m);m.instructions.add(new VarInsnNode(ALOAD,0));
                m.instructions.add(new MethodInsnNode(INVOKESTATIC,"net/tompsen/nexuscharacters/CulturalBeardSupport","previewTextureKey","(Ljava/lang/String;)Ljava/lang/String;",false));
                m.instructions.add(new InsnNode(ARETURN));m.maxLocals=1;m.maxStack=1;color++;
            } else if(m.name.equals("safeColor")&&m.desc.equals("(Ljava/lang/String;)Ljava/lang/String;")) {
                clear(m);m.instructions.add(new VarInsnNode(ALOAD,0));
                m.instructions.add(new MethodInsnNode(INVOKESTATIC,"net/tompsen/nexuscharacters/CulturalBeardSupport","safePreviewTextureKey","(Ljava/lang/String;)Ljava/lang/String;",false));
                m.instructions.add(new InsnNode(ARETURN));m.maxLocals=1;m.maxStack=1;safe++;
            }
        }
        if(base!=1||color!=1||safe!=1) throw new IllegalStateException("Worldless patch mismatch "+base+","+color+","+safe);
        ClassWriter cw=new ClassWriter(ClassWriter.COMPUTE_MAXS);cn.accept(cw);return cw.toByteArray();
    }

    static byte[] patchCreationCosmetic(byte[] in) {
        ClassNode cn=new ClassNode(API);new ClassReader(in).accept(cn,0);int cycle=0,refresh=0;
        for(MethodNode m:cn.methods){
            if(m.name.equals("nexuscharacters$cycleCosmetic")&&m.desc.equals("()V")){
                clear(m);
                m.instructions.add(new VarInsnNode(ALOAD,0));
                m.instructions.add(new MethodInsnNode(INVOKESTATIC,"net/tompsen/nexuscharacters/CulturalBeardUiSupport","cycleCosmetic","(Ljava/lang/Object;)V",false));
                m.instructions.add(new InsnNode(RETURN));m.maxLocals=1;m.maxStack=1;cycle++;
            } else if(m.name.equals("nexuscharacters$refreshCosmeticButton")&&m.desc.equals("()V")){
                clear(m);
                m.instructions.add(new VarInsnNode(ALOAD,0));
                m.instructions.add(new MethodInsnNode(INVOKESTATIC,"net/tompsen/nexuscharacters/CulturalBeardUiSupport","refreshCosmeticButton","(Ljava/lang/Object;)V",false));
                m.instructions.add(new InsnNode(RETURN));m.maxLocals=1;m.maxStack=1;refresh++;
            }
        }
        if(cycle!=1||refresh!=1)throw new IllegalStateException("Creation cosmetic mismatch cycle="+cycle+" refresh="+refresh);
        ClassWriter cw=new ClassWriter(ClassWriter.COMPUTE_MAXS);cn.accept(cw);return cw.toByteArray();
    }

    static byte[] patchCharacterCosmetics(byte[] in) {
        ClassNode cn=new ClassNode(API);new ClassReader(in).accept(cn,0);int safe=0;
        for(MethodNode m:cn.methods) if(m.name.equals("safeId")&&m.desc.equals("(Ljava/lang/String;)Ljava/lang/String;")){
            clear(m);m.instructions.add(new VarInsnNode(ALOAD,0));
            m.instructions.add(new MethodInsnNode(INVOKESTATIC,"net/tompsen/nexuscharacters/CulturalBeardSupport","sanitizeStoredStyle","(Ljava/lang/String;)Ljava/lang/String;",false));
            m.instructions.add(new InsnNode(ARETURN));m.maxLocals=1;m.maxStack=1;safe++;
        }
        if(safe!=1)throw new IllegalStateException("CharacterCosmetics safeId count="+safe);
        ClassWriter cw=new ClassWriter(ClassWriter.COMPUTE_MAXS);cn.accept(cw);return cw.toByteArray();
    }

    static byte[] patchPlayerCosmeticState(byte[] in) {
        ClassNode cn=new ClassNode(API);new ClassReader(in).accept(cn,0);int from=0,id=0;
        for(MethodNode m:cn.methods){
            if(!m.name.equals("nexuscharacters$copyCosmetics")) continue;
            for(AbstractInsnNode n=m.instructions.getFirst();n!=null;){
                AbstractInsnNode next=n.getNext();
                if(n instanceof MethodInsnNode mi && mi.owner.equals("net/tompsen/nexuscharacters/CharacterCosmetics$BeardStyle")){
                    if(mi.name.equals("fromId")&&mi.desc.equals("(Ljava/lang/String;)Lnet/tompsen/nexuscharacters/CharacterCosmetics$BeardStyle;")){
                        m.instructions.remove(n);from++;
                    } else if(mi.name.equals("id")&&mi.desc.equals("()Ljava/lang/String;")){
                        m.instructions.remove(n);id++;
                    }
                }
                n=next;
            }
        }
        if(from!=1||id!=1)throw new IllegalStateException("PlayerCosmeticState fallback mismatch from="+from+" id="+id);
        ClassWriter cw=new ClassWriter(ClassWriter.COMPUTE_MAXS);cn.accept(cw);return cw.toByteArray();
    }

    static byte[] patchFeatureRenderer(byte[] in) {
        ClassNode cn=new ClassNode(API);new ClassReader(in).accept(cn,0);int inserted=0;
        String owner="net/tompsen/nexuscharacters/CharacterCosmeticFeatureRenderer";
        for(MethodNode m:cn.methods){
            if(!m.name.equals("render")) continue;
            for(AbstractInsnNode n=m.instructions.getFirst();n!=null;n=n.getNext()){
                if(n instanceof MethodInsnNode mi && mi.getOpcode()==INVOKESTATIC && mi.owner.equals(owner) && mi.name.equals("submit")){
                    InsnList x=new InsnList();
                    x.add(new VarInsnNode(ALOAD,9));
                    x.add(new VarInsnNode(ALOAD,1));
                    x.add(new VarInsnNode(ALOAD,2));
                    x.add(new VarInsnNode(ILOAD,3));
                    x.add(new VarInsnNode(ALOAD,4));
                    x.add(new MethodInsnNode(INVOKESTATIC,"net/tompsen/nexuscharacters/BeardOrnamentRenderSupport","render","(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;ILjava/lang/Object;)V",false));
                    m.instructions.insert(n,x);inserted++;
                    break;
                }
            }
        }
        if(inserted!=1)throw new IllegalStateException("Feature ornament insert count="+inserted);
        ClassWriter cw=new ClassWriter(ClassWriter.COMPUTE_MAXS);cn.accept(cw);return cw.toByteArray();
    }

    static void patch(Path root,String rel,java.util.function.Function<byte[],byte[]> fn)throws Exception{
        Path p=root.resolve(rel); Files.write(p,fn.apply(Files.readAllBytes(p)));
    }

    public static void main(String[] args)throws Exception{
        Path root=Paths.get(args[0]);
        patch(root,"net/tompsen/nexuscharacters/CosmeticColorSupport.class",PatchBeta10Pre4::patchCosmeticColor);
        patch(root,"net/tompsen/nexuscharacters/BeardPaletteSupport.class",PatchBeta10Pre4::patchPalette);
        patch(root,"net/tompsen/nexuscharacters/NordicBeardSupport.class",PatchBeta10Pre4::patchNordic);
        patch(root,"net/tompsen/nexuscharacters/WorldlessBeardWidgetRenderer.class",PatchBeta10Pre4::patchWorldless);
        patch(root,"net/tompsen/nexuscharacters/mixin/client/CharacterCreationCosmeticMixin.class",PatchBeta10Pre4::patchCreationCosmetic);
        patch(root,"net/tompsen/nexuscharacters/CharacterCosmetics.class",PatchBeta10Pre4::patchCharacterCosmetics);
        patch(root,"net/tompsen/nexuscharacters/mixin/client/PlayerCosmeticStateMixin.class",PatchBeta10Pre4::patchPlayerCosmeticState);
        patch(root,"net/tompsen/nexuscharacters/CharacterCosmeticFeatureRenderer.class",PatchBeta10Pre4::patchFeatureRenderer);
        System.out.println("BETA 1.0 PRE4 bytecode patch applied.");
    }
}
