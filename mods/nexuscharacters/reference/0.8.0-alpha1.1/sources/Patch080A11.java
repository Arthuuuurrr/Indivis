import jdk.internal.org.objectweb.asm.*;
import java.nio.file.*;

public class Patch080A11 {
    static final int API = Opcodes.ASM9;
    static ClassWriter writer(ClassReader cr) {
        return new ClassWriter(cr, ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS) {
            @Override protected String getCommonSuperClass(String a, String b) { return "java/lang/Object"; }
        };
    }

    static byte[] patchNetwork(byte[] in) {
        ClassReader cr = new ClassReader(in); ClassWriter cw = writer(cr);
        final int[] reasonPatch={0}, versions={0};
        ClassVisitor cv = new ClassVisitor(API, cw) {
            @Override public MethodVisitor visitMethod(int access, String name, String desc, String sig, String[] ex) {
                MethodVisitor base = super.visitMethod(access,name,desc,sig,ex);
                return new MethodVisitor(API,base) {
                    @Override public void visitLdcInsn(Object v) {
                        if (v instanceof String s) {
                            if (name.equals("lambda$register$1") &&
                                s.equals("NexusCharacters: personnage refuse par l'autorite serveur.")) {
                                reasonPatch[0]++;
                                super.visitMethodInsn(Opcodes.INVOKESTATIC,
                                    "net/tompsen/nexuscharacters/ServerAuthorityV080",
                                    "consumeRejectionDisconnectMessage",
                                    "()Ljava/lang/String;", false);
                                return;
                            }
                            if (s.contains("0.8.0-alpha1")) {
                                versions[0]++;
                                s=s.replace("0.8.0-alpha1","0.8.0-alpha1.1");
                            }
                            super.visitLdcInsn(s); return;
                        }
                        super.visitLdcInsn(v);
                    }
                };
            }
            @Override public void visitEnd() {
                if (reasonPatch[0] != 1) throw new IllegalStateException("rejection message patch count="+reasonPatch[0]);
                super.visitEnd();
            }
        };
        cr.accept(cv,0);
        System.out.println("PATCH network rejection reason="+reasonPatch[0]+" versionStrings="+versions[0]);
        return cw.toByteArray();
    }

    static byte[] patchLoading(byte[] in) {
        ClassReader cr=new ClassReader(in); ClassWriter cw=writer(cr);
        final int[] target={0};
        ClassVisitor cv=new ClassVisitor(API,cw){
            @Override public MethodVisitor visitMethod(int access,String name,String desc,String sig,String[] ex){
                MethodVisitor base=super.visitMethod(access,name,desc,sig,ex);
                if(!name.equals("method_25393") || !desc.equals("()V")) return base;
                target[0]++;
                return new MethodVisitor(API,base){
                    @Override public void visitCode(){
                        super.visitCode();
                        Label noWorld=new Label(), noTimeout=new Label();

                        super.visitVarInsn(Opcodes.ALOAD,0);
                        super.visitFieldInsn(Opcodes.GETFIELD,"net/tompsen/nexuscharacters/CharacterConnectionLoadingScreen","field_22787","Lnet/minecraft/class_310;");
                        super.visitJumpInsn(Opcodes.IFNULL,noWorld);
                        super.visitVarInsn(Opcodes.ALOAD,0);
                        super.visitFieldInsn(Opcodes.GETFIELD,"net/tompsen/nexuscharacters/CharacterConnectionLoadingScreen","field_22787","Lnet/minecraft/class_310;");
                        super.visitFieldInsn(Opcodes.GETFIELD,"net/minecraft/class_310","field_1687","Lnet/minecraft/class_638;");
                        super.visitJumpInsn(Opcodes.IFNULL,noWorld);
                        super.visitVarInsn(Opcodes.ALOAD,0);
                        super.visitFieldInsn(Opcodes.GETFIELD,"net/tompsen/nexuscharacters/CharacterConnectionLoadingScreen","field_22787","Lnet/minecraft/class_310;");
                        super.visitInsn(Opcodes.ACONST_NULL);
                        super.visitMethodInsn(Opcodes.INVOKEVIRTUAL,"net/minecraft/class_310","method_1507","(Lnet/minecraft/class_437;)V",false);
                        super.visitInsn(Opcodes.RETURN);

                        super.visitLabel(noWorld);
                        super.visitVarInsn(Opcodes.ALOAD,0);
                        super.visitFieldInsn(Opcodes.GETFIELD,"net/tompsen/nexuscharacters/CharacterConnectionLoadingScreen","error","Z");
                        super.visitJumpInsn(Opcodes.IFNE,noTimeout);
                        super.visitMethodInsn(Opcodes.INVOKESTATIC,"java/lang/System","currentTimeMillis","()J",false);
                        super.visitVarInsn(Opcodes.ALOAD,0);
                        super.visitFieldInsn(Opcodes.GETFIELD,"net/tompsen/nexuscharacters/CharacterConnectionLoadingScreen","openedAt","J");
                        super.visitInsn(Opcodes.LSUB);
                        super.visitLdcInsn(20000L);
                        super.visitInsn(Opcodes.LCMP);
                        super.visitJumpInsn(Opcodes.IFLE,noTimeout);
                        super.visitVarInsn(Opcodes.ALOAD,0);
                        super.visitFieldInsn(Opcodes.GETFIELD,"net/tompsen/nexuscharacters/CharacterConnectionLoadingScreen","field_22787","Lnet/minecraft/class_310;");
                        super.visitJumpInsn(Opcodes.IFNULL,noTimeout);
                        super.visitVarInsn(Opcodes.ALOAD,0);
                        super.visitFieldInsn(Opcodes.GETFIELD,"net/tompsen/nexuscharacters/CharacterConnectionLoadingScreen","field_22787","Lnet/minecraft/class_310;");
                        super.visitTypeInsn(Opcodes.NEW,"net/tompsen/nexuscharacters/CharacterConnectionLoadingScreen");
                        super.visitInsn(Opcodes.DUP);
                        super.visitInsn(Opcodes.ICONST_1);
                        super.visitLdcInsn("La synchronisation n’a pas abouti sous 20 s. Le serveur a peut-être refusé le personnage ou interrompu la connexion.");
                        super.visitMethodInsn(Opcodes.INVOKESPECIAL,"net/tompsen/nexuscharacters/CharacterConnectionLoadingScreen","<init>","(ZLjava/lang/String;)V",false);
                        super.visitMethodInsn(Opcodes.INVOKEVIRTUAL,"net/minecraft/class_310","method_1507","(Lnet/minecraft/class_437;)V",false);
                        super.visitInsn(Opcodes.RETURN);
                        super.visitLabel(noTimeout);
                    }
                };
            }
            @Override public void visitEnd(){ if(target[0]!=1) throw new IllegalStateException("loading tick target count="+target[0]); super.visitEnd(); }
        };
        cr.accept(cv,0);
        System.out.println("PATCH loading timeout target="+target[0]);
        return cw.toByteArray();
    }

    static byte[] patchVersionStrings(byte[] in) {
        ClassReader cr=new ClassReader(in); ClassWriter cw=writer(cr);
        final int[] changed={0};
        ClassVisitor cv=new ClassVisitor(API,cw){
            @Override public MethodVisitor visitMethod(int access,String name,String desc,String sig,String[] ex){
                MethodVisitor base=super.visitMethod(access,name,desc,sig,ex);
                return new MethodVisitor(API,base){
                    @Override public void visitLdcInsn(Object v){
                        if(v instanceof String s && s.contains("0.8.0-alpha1")){
                            changed[0]++; super.visitLdcInsn(s.replace("0.8.0-alpha1","0.8.0-alpha1.1"));
                        } else super.visitLdcInsn(v);
                    }
                };
            }
        };
        cr.accept(cv,0);
        System.out.println("PATCH version strings="+changed[0]);
        return cw.toByteArray();
    }

    public static void main(String[] args)throws Exception{
        if(args.length!=2) throw new IllegalArgumentException("inDir outDir");
        Path in=Paths.get(args[0]), out=Paths.get(args[1]); Files.createDirectories(out);
        String n="net/tompsen/nexuscharacters/NexusCharactersNetwork.class";
        Path p=out.resolve(n); Files.createDirectories(p.getParent()); Files.write(p,patchNetwork(Files.readAllBytes(in.resolve(n))));
        n="net/tompsen/nexuscharacters/CharacterConnectionLoadingScreen.class";
        p=out.resolve(n); Files.createDirectories(p.getParent()); Files.write(p,patchLoading(Files.readAllBytes(in.resolve(n))));
        n="net/tompsen/nexuscharacters/NexusCharacters.class";
        p=out.resolve(n); Files.createDirectories(p.getParent()); Files.write(p,patchVersionStrings(Files.readAllBytes(in.resolve(n))));
    }
}
