import java.nio.file.*;
import jdk.internal.org.objectweb.asm.*;
import jdk.internal.org.objectweb.asm.tree.*;
import jdk.internal.org.objectweb.asm.tree.analysis.*;

public final class VerifyClasses {
  public static void main(String[] args)throws Exception {
    int classes=0,methods=0;
    for(String dir:args)try(var paths=Files.walk(Path.of(dir))) {
      for(Path path:paths.filter(p->p.toString().endsWith(".class")).toList()) {
        byte[] bytes=Files.readAllBytes(path);bytes[6]=0;bytes[7]=61;
        ClassNode node=new ClassNode();new ClassReader(bytes).accept(node,0);
        for(MethodNode method:node.methods)if((method.access&(Opcodes.ACC_ABSTRACT|Opcodes.ACC_NATIVE))==0) {
          new Analyzer<BasicValue>(new BasicVerifier()).analyze(node.name,method);methods++;
        }
        classes++;
      }
    }
    System.out.println("ASM_BASIC_VERIFIER classes="+classes+" methods="+methods+" errors=0 (sans résolution des API Minecraft)");
  }
}
