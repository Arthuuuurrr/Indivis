import java.nio.file.*;
import java.util.*;
import java.util.zip.*;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;

public final class PatchBlockbench implements Opcodes {
    static final String P="net/tompsen/nexuscharacters/",C=P+"BlockbenchPresetCatalog",H=P+"BlockbenchHooks";
    static MethodNode named(ClassNode c,String name){return c.methods.stream().filter(m->m.name.equals(name)).findFirst().orElseThrow(()->new IllegalStateException(c.name+"."+name));}
    static void redirect(ClassNode c,String name,String helper,boolean expose) {
        MethodNode old=named(c,name);String desc=old.desc;int access=old.access;old.name=name+"OPT3";
        if(expose)old.access=(old.access&~(ACC_PRIVATE|ACC_PROTECTED))|ACC_PUBLIC;
        MethodNode wrapper=new MethodNode(ASM9,access,name,desc,old.signature,old.exceptions.toArray(String[]::new));
        int slot=0;for(Type type:Type.getArgumentTypes(desc)){wrapper.instructions.add(new VarInsnNode(type.getOpcode(ILOAD),slot));slot+=type.getSize();}
        wrapper.instructions.add(new MethodInsnNode(INVOKESTATIC,H,helper,desc,false));wrapper.instructions.add(new InsnNode(Type.getReturnType(desc).getOpcode(IRETURN)));c.methods.add(wrapper);
    }
    public static void main(String[] args)throws Exception {
        Path out=Path.of(args[1]);int changes=0;
        try(ZipFile z=new ZipFile(args[0])) {
            for(String name:List.of("CharacterCosmetics","CulturalBeardSupport","EarPreviewSelectionBridge","CulturalBeardUiSupport","DynamicAssetCatalog","UnifiedPilositySupport","mixin/client/CharacterCreationAppearance65Mixin","CharacterCosmeticFeatureRenderer","CharacterPreviewRenderer","WorldlessEarWidgetRenderer","BeardPreviewVisibilitySupport","GenericSkinLayerSupport")) {
                ClassNode c=new ClassNode(ASM9);new ClassReader(z.getInputStream(z.getEntry(P+name+".class"))).accept(c,0);
                switch(name) {
                    case "CharacterCosmetics"->redirect(c,"normalizeEar","normalizeEar",false);
                    case "CulturalBeardSupport"->{
                        redirect(c,"styleAllowed","styleAllowed",false);redirect(c,"styleLabel","styleLabel",false);redirect(c,"ornamentAllowed","ornamentAllowed",false);
                        int count=0;for(AbstractInsnNode n:named(c,"<clinit>").instructions.toArray())if(n instanceof FieldInsnNode f&&f.getOpcode()==PUTSTATIC&&f.name.equals("ALL_STYLES")) {
                            named(c,"<clinit>").instructions.insertBefore(n,new MethodInsnNode(INVOKESTATIC,C,"extendBeardSet","(Ljava/util/Set;)Ljava/util/Set;",false));count++;
                        }if(count!=1)throw new AssertionError("beard whitelist hook "+count);
                    }
                    case "EarPreviewSelectionBridge"->redirect(c,"normalize","normalizeEarPreview",true);
                    case "CulturalBeardUiSupport"->{redirect(c,"cycleCosmetic","cycleCosmetic",false);redirect(c,"refreshCosmeticButton","refreshCosmetic",false);}
                    case "DynamicAssetCatalog"->{
                        redirect(c,"normalizeHair","normalizeHair",false);redirect(c,"hairAssetName","hairAssetName",false);
                        MethodNode clear=named(c,"clear");for(AbstractInsnNode n:clear.instructions.toArray())if(n.getOpcode()==RETURN)clear.instructions.insertBefore(n,new MethodInsnNode(INVOKESTATIC,H,"clear","()V",false));
                    }
                    case "UnifiedPilositySupport"->{
                        MethodNode init=named(c,"<clinit>");int count=0;
                        for(AbstractInsnNode n:init.instructions.toArray())if(n instanceof FieldInsnNode f&&f.getOpcode()==PUTSTATIC&&Set.of("HUMAN_3D","NORDIC_3D","DWARF_3D").contains(f.name)) {
                            InsnList hook=new InsnList();hook.add(new LdcInsnNode(f.name.substring(0,f.name.indexOf('_')).toLowerCase(Locale.ROOT)));
                            hook.add(new MethodInsnNode(INVOKESTATIC,C,"extendBeardChoices","([Ljava/lang/String;Ljava/lang/String;)[Ljava/lang/String;",false));init.instructions.insertBefore(n,hook);count++;
                        }if(count!=3)throw new AssertionError("beard choices "+count);
                    }
                    case "mixin/client/CharacterCreationAppearance65Mixin"->{
                        int count=0;
                        for(MethodNode m:c.methods)for(AbstractInsnNode n:m.instructions.toArray())if(n instanceof MethodInsnNode call&&call.owner.equals(P+"DynamicAssetCatalog")&&call.name.equals("nextHair")&&call.desc.equals("(I)I")) {
                            InsnList hook=new InsnList();hook.add(new VarInsnNode(ALOAD,0));hook.add(new FieldInsnNode(GETFIELD,c.name,"race","L"+P+"CharacterRace;"));hook.add(new InsnNode(SWAP));
                            m.instructions.insertBefore(n,hook);call.owner=C;call.desc="(Ljava/lang/Object;I)I";count++;
                        }if(count!=1)throw new AssertionError("hair cycle "+count);
                        MethodNode norm=named(c,"nexuscharacters$normalizeForRace");
                        for(AbstractInsnNode n:norm.instructions.toArray())if(n.getOpcode()==RETURN) {
                            InsnList hook=new InsnList();hook.add(new VarInsnNode(ALOAD,0));hook.add(new VarInsnNode(ALOAD,0));hook.add(new FieldInsnNode(GETFIELD,c.name,"race","L"+P+"CharacterRace;"));hook.add(new VarInsnNode(ALOAD,0));hook.add(new FieldInsnNode(GETFIELD,c.name,"nexuscharacters$hair","I"));
                            hook.add(new MethodInsnNode(INVOKESTATIC,C,"normalizeHair","(Ljava/lang/Object;I)I",false));hook.add(new FieldInsnNode(PUTFIELD,c.name,"nexuscharacters$hair","I"));norm.instructions.insertBefore(n,hook);
                        }
                        MethodNode refresh=named(c,"nexuscharacters$refresh");for(AbstractInsnNode n:refresh.instructions.toArray())if(n.getOpcode()==RETURN) {
                            InsnList hook=new InsnList();hook.add(new VarInsnNode(ALOAD,0));hook.add(new MethodInsnNode(INVOKESTATIC,C,"refreshHairLabel","(Ljava/lang/Object;)V",false));refresh.instructions.insertBefore(n,hook);
                        }
                    }
                    case "CharacterCosmeticFeatureRenderer"->{
                        MethodNode render=named(c,"render");MethodInsnNode model=null;
                        for(AbstractInsnNode n:render.instructions)if(n instanceof MethodInsnNode call&&call.name.equals("method_17165")){model=call;break;}
                        if(model==null)throw new AssertionError("Feature model getter absent");
                        InsnList hook=new InsnList();hook.add(new VarInsnNode(ALOAD,0));hook.add(new MethodInsnNode(model.getOpcode(),model.owner,model.name,model.desc,model.itf));hook.add(new TypeInsnNode(CHECKCAST,"net/minecraft/class_591"));
                        hook.add(new VarInsnNode(ALOAD,1));hook.add(new VarInsnNode(ALOAD,2));hook.add(new VarInsnNode(ILOAD,3));hook.add(new VarInsnNode(ALOAD,4));
                        hook.add(new MethodInsnNode(INVOKESTATIC,P+"BlockbenchRenderSupport","render","(Lnet/minecraft/class_591;Lnet/minecraft/class_4587;Lnet/minecraft/class_11659;ILnet/minecraft/class_10055;)V",false));render.instructions.insert(hook);
                    }
                    case "CharacterPreviewRenderer"->{
                        MethodNode render=named(c,"drawWorldless");int count=0;
                        for(AbstractInsnNode n:render.instructions.toArray())if(n instanceof MethodInsnNode call&&call.owner.equals("net/minecraft/class_8765")&&call.name.equals("method_25394")) {
                            render.instructions.insertBefore(n,new VarInsnNode(ALOAD,1));call.setOpcode(INVOKESTATIC);call.owner=P+"BlockbenchRenderSupport";call.name="renderWidget";call.desc="(Lnet/minecraft/class_8765;Lnet/minecraft/class_332;IIFL"+P+"CharacterDto;)V";count++;
                        }if(count!=1)throw new AssertionError("GUI skin submission "+count);
                    }
                    case "WorldlessEarWidgetRenderer"->redirect(c,"renderPhase","previewEars",true);
                    case "BeardPreviewVisibilitySupport"->redirect(c,"render","previewBeard",false);
                    case "GenericSkinLayerSupport"->{
                        MethodNode original=named(c,"hiddenHead");original.name="hiddenHeadOPT3";
                        MethodNode wrapper=new MethodNode(ASM9,original.access,"hiddenHead",original.desc,original.signature,original.exceptions.toArray(String[]::new));
                        LabelNode nonnull=new LabelNode();wrapper.instructions.add(new VarInsnNode(ALOAD,0));wrapper.instructions.add(new JumpInsnNode(IFNONNULL,nonnull));wrapper.instructions.add(new InsnNode(ICONST_0));wrapper.instructions.add(new InsnNode(IRETURN));wrapper.instructions.add(nonnull);wrapper.instructions.add(new FrameNode(F_SAME,0,null,0,null));
                        wrapper.instructions.add(new VarInsnNode(ALOAD,0));wrapper.instructions.add(new MethodInsnNode(INVOKESTATIC,c.name,original.name,original.desc,false));wrapper.instructions.add(new InsnNode(IRETURN));c.methods.add(wrapper);
                    }
                }
                ClassWriter writer=new ClassWriter(ClassWriter.COMPUTE_MAXS);c.accept(writer);Path f=out.resolve(c.name+".class");Files.createDirectories(f.getParent());Files.write(f,writer.toByteArray());changes++;
            }
        }
        System.out.println("BLOCKBENCH_HOOKS_PASS classes="+changes);
    }
}
