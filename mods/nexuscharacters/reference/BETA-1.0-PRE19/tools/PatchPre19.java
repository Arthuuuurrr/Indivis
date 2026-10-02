import java.nio.file.*;
import jdk.internal.org.objectweb.asm.*;
import jdk.internal.org.objectweb.asm.tree.*;

/** Minimal bytecode edits keep the original compositor, tint math and public ABI intact. */
public final class PatchPre19 {
    private static final String OWNER="net/tompsen/nexuscharacters/DynamicAppearanceSupport";
    private static final String HELPER="net/tompsen/nexuscharacters/GenericSkinLayerSupport";
    private static final String CATALOG="net/tompsen/nexuscharacters/DynamicAssetCatalog";
    public static void main(String[] args)throws Exception {
        Path root=Path.of(args[0]);Path file=root.resolve(OWNER+".class");
        ClassNode c=new ClassNode();new ClassReader(Files.readAllBytes(file)).accept(c,0);
        MethodNode overlay=null,load=null;int completed=0;
        for(MethodNode m:c.methods) {
            if(m.name.equals("overlayTinted"))overlay=m;
            if(m.name.equals("load"))load=m;
            if(m.name.equals("textures"))for(AbstractInsnNode insn:m.instructions.toArray()) {
                if(insn instanceof MethodInsnNode call && call.owner.equals(OWNER)
                        && (call.name.equals("compose69")||call.name.equals("composeLegacy"))) {
                    InsnList hook=new InsnList();hook.add(new VarInsnNode(Opcodes.ALOAD,0));
                    hook.add(new MethodInsnNode(Opcodes.INVOKESTATIC,HELPER,"complete","(Ljava/lang/Object;Ljava/lang/String;)Ljava/lang/Object;",false));
                    hook.add(new TypeInsnNode(Opcodes.CHECKCAST,"net/minecraft/class_1011"));
                    m.instructions.insert(call,hook);completed++;
                }
            }
        }
        if(overlay==null||load==null||completed!=2)throw new IllegalStateException("Unexpected PRE17 compositor shape");
        String descriptor=overlay.desc;overlay.name="nexus$overlayOriginal";
        MethodNode dispatch=new MethodNode(Opcodes.ACC_PRIVATE|Opcodes.ACC_STATIC,"overlayTinted",descriptor,null,null);
        for(int i=0;i<6;i++)dispatch.instructions.add(new VarInsnNode(i==2||i>=4?Opcodes.ILOAD:Opcodes.ALOAD,i));
        dispatch.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC,HELPER,"overlay","(Ljava/lang/Object;Ljava/lang/Object;ILjava/lang/Object;II)V",false));
        dispatch.instructions.add(new InsnNode(Opcodes.RETURN));dispatch.maxStack=6;dispatch.maxLocals=6;c.methods.add(dispatch);
        load.instructions.clear();load.tryCatchBlocks.clear();if(load.localVariables!=null)load.localVariables.clear();
        load.instructions.add(new VarInsnNode(Opcodes.ALOAD,0));
        load.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC,HELPER,"load","(Ljava/lang/String;)Ljava/lang/Object;",false));
        load.instructions.add(new TypeInsnNode(Opcodes.CHECKCAST,"net/minecraft/class_1011"));load.instructions.add(new InsnNode(Opcodes.ARETURN));
        load.maxStack=1;load.maxLocals=1;
        ClassWriter w=new ClassWriter(ClassWriter.COMPUTE_MAXS);c.accept(w);Files.write(file,w.toByteArray());
        patchCatalogUsers(root);
        System.out.println("PRE19 compositor hooks=2; dynamic hair/beard/outfit catalog installed");
    }

    private static void patchCatalogUsers(Path root)throws Exception {
        ClassNode appearance=read(root,"net/tompsen/nexuscharacters/Appearance69Support");
        for(MethodNode m:appearance.methods){
            if(m.name.equals("marker"))delegate(m,"marker");
            if(m.name.equals("<clinit>"))for(AbstractInsnNode n:m.instructions.toArray())if(n instanceof LdcInsnNode l
                    && l.cst instanceof String s && s.startsWith("player_v69_b("))
                l.cst="player_v69_b([1-4])_e([1-8])_ec([0-7])_ey([0-4])_h([0-9]{1,4})_hc(0[0-9]|1[0-2])_s(0[1-9]|1[0-6])_o([0-9]{1,4})_fh([0-9]{1,4})_fc(0[0-9]|1[0-2])_mk([0-8])_mc([0-9]|1[0-2])";
        }
        write(root,"net/tompsen/nexuscharacters/Appearance69Support",appearance);

        ClassNode outfits=read(root,"net/tompsen/nexuscharacters/OutfitCatalogSupport");
        for(MethodNode m:outfits.methods)switch(m.name){
            case "count"->delegate(m,"outfitCount");case "isAllowed"->delegate(m,"outfitAllowed");
            case "normalize"->delegate(m,"normalizeOutfit");case "next"->delegate(m,"nextOutfit");
            case "position"->delegate(m,"outfitPosition");case "label"->delegate(m,"outfitLabel");default->{}
        }
        write(root,"net/tompsen/nexuscharacters/OutfitCatalogSupport",outfits);

        ClassNode cosmetics=read(root,"net/tompsen/nexuscharacters/CosmeticsPack1Support");
        for(MethodNode m:cosmetics.methods)switch(m.name){
            case "withOutfit"->delegate(m,"withOutfit");case "facialHairLabel"->delegate(m,"facialHairLabel");
            case "facialHairName"->delegate(m,"facialHairName");default->{}
        }
        write(root,"net/tompsen/nexuscharacters/CosmeticsPack1Support",cosmetics);

        ClassNode facial=read(root,"net/tompsen/nexuscharacters/FacialHairRaceSupport");
        for(MethodNode m:facial.methods)if(m.name.equals("normalize"))delegate(m,"normalizeFacialHair");else if(m.name.equals("next"))delegate(m,"nextFacialHair");
        write(root,"net/tompsen/nexuscharacters/FacialHairRaceSupport",facial);

        ClassNode unified=read(root,"net/tompsen/nexuscharacters/UnifiedPilositySupport");
        int normalizations=0,cycles=0,cycleLimits=0;
        for(MethodNode m:unified.methods){
            if(m.name.equals("currentLabel")){delegate(m,"pilosityLabel");continue;}
            if(!m.name.equals("fix")&&!m.name.equals("cycle")&&!m.name.equals("syncColor"))continue;
            for(AbstractInsnNode n:m.instructions.toArray())if(n instanceof IntInsnNode i && i.getOpcode()==Opcodes.BIPUSH){
                if(i.operand!=6)continue;
                AbstractInsnNode next=nextCode(i),previous=previousCode(i);
                if(next instanceof MethodInsnNode call && call.name.equals("clamp") && previous.getOpcode()==Opcodes.ICONST_0){
                    m.instructions.remove(previous);m.instructions.remove(i);
                    m.instructions.set(call,new MethodInsnNode(Opcodes.INVOKESTATIC,CATALOG,"normalizeFacialIndex","(I)I",false));normalizations++;
                }else if(next.getOpcode()==Opcodes.IF_ICMPGE){
                    m.instructions.set(i,new MethodInsnNode(Opcodes.INVOKESTATIC,CATALOG,"lastFacialHairId","()I",false));cycleLimits++;
                }else throw new IllegalStateException("Unexpected facial-hair limit in "+m.name);
            }
            if(m.name.equals("cycle"))for(AbstractInsnNode n:m.instructions.toArray())if(n instanceof IincInsnNode i && i.var==5 && i.incr==1){
                InsnList cycle=new InsnList();cycle.add(new VarInsnNode(Opcodes.ILOAD,5));
                cycle.add(new MethodInsnNode(Opcodes.INVOKESTATIC,CATALOG,"nextFacialIndex","(I)I",false));
                cycle.add(new VarInsnNode(Opcodes.ISTORE,5));m.instructions.insertBefore(i,cycle);m.instructions.remove(i);cycles++;
            }
        }
        if(normalizations!=3||cycles!=1||cycleLimits!=1)throw new IllegalStateException("Unexpected PRE17 pilosity shape");
        write(root,"net/tompsen/nexuscharacters/UnifiedPilositySupport",unified);

        ClassNode ui=read(root,"net/tompsen/nexuscharacters/mixin/client/CharacterCreationAppearance65Mixin");
        for(MethodNode m:ui.methods){
            if(m.name.equals("lambda$nexuscharacters$install$2")){
                m.instructions.clear();m.tryCatchBlocks.clear();if(m.localVariables!=null)m.localVariables.clear();
                m.instructions.add(new VarInsnNode(Opcodes.ALOAD,0));m.instructions.add(new VarInsnNode(Opcodes.ALOAD,0));
                m.instructions.add(new FieldInsnNode(Opcodes.GETFIELD,ui.name,"nexuscharacters$hair","I"));
                m.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC,CATALOG,"nextHair","(I)I",false));
                m.instructions.add(new FieldInsnNode(Opcodes.PUTFIELD,ui.name,"nexuscharacters$hair","I"));
                m.instructions.add(new VarInsnNode(Opcodes.ALOAD,0));
                m.instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,ui.name,"nexuscharacters$changed","()V",false));
                m.instructions.add(new InsnNode(Opcodes.RETURN));continue;
            }
            for(AbstractInsnNode n:m.instructions.toArray()){
            if(n instanceof LdcInsnNode l && l.cst instanceof String s && s.contains("/13"))l.cst=s.replace("/13","");
            else if(n instanceof InvokeDynamicInsnNode d)for(int x=0;x<d.bsmArgs.length;x++)if(d.bsmArgs[x] instanceof String s && s.contains("/13"))d.bsmArgs[x]=s.replace("/13","");
            }
        }
        write(root,"net/tompsen/nexuscharacters/mixin/client/CharacterCreationAppearance65Mixin",ui);
    }

    private static AbstractInsnNode nextCode(AbstractInsnNode n){do{n=n.getNext();}while(n!=null&&n.getOpcode()<0);return n;}
    private static AbstractInsnNode previousCode(AbstractInsnNode n){do{n=n.getPrevious();}while(n!=null&&n.getOpcode()<0);return n;}
    private static void delegate(MethodNode m,String helper){
        m.instructions.clear();m.tryCatchBlocks.clear();if(m.localVariables!=null)m.localVariables.clear();
        Type[] args=Type.getArgumentTypes(m.desc);int slot=0;
        for(Type arg:args){m.instructions.add(new VarInsnNode(arg.getOpcode(Opcodes.ILOAD),slot));slot+=arg.getSize();}
        m.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC,CATALOG,helper,m.desc.replace("Lnet/tompsen/nexuscharacters/CharacterRace;","Ljava/lang/Object;"),false));
        m.instructions.add(new InsnNode(Type.getReturnType(m.desc).getOpcode(Opcodes.IRETURN)));m.maxLocals=slot;
    }
    private static ClassNode read(Path root,String name)throws Exception{ClassNode c=new ClassNode();new ClassReader(Files.readAllBytes(root.resolve(name+".class"))).accept(c,0);return c;}
    private static void write(Path root,String name,ClassNode c)throws Exception{ClassWriter w=new ClassWriter(ClassWriter.COMPUTE_MAXS);c.accept(w);Files.write(root.resolve(name+".class"),w.toByteArray());}
}
