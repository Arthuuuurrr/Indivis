package net.tompsen.nexuscharacters;
import java.util.concurrent.atomic.AtomicLong;
import net.minecraft.class_4587;
import net.minecraft.class_630;
import org.joml.Matrix4f;
/** Only replaces PRE29's temporary pose extraction, never its geometry algorithm. */
final class PoseScratch {
 static final boolean DIAGNOSTICS=ReflectionAccess.DIAGNOSTICS;
 static final AtomicLong applyCount=new AtomicLong(),applyNanos=new AtomicLong(),applyMaxNanos=new AtomicLong();
 static final ThreadLocal<Workspace> ACTIVE=new ThreadLocal<>();
 static final class Workspace {
  Workspace next;
  boolean busy;
  final Matrix4f[][] matrixBanks={matrices(12),matrices(12)},relativeBanks={matrices(6),matrices(6)};
  final Object[][] templateBanks={new Object[6],new Object[6]};
  final class_4587 stack=new class_4587(),relative=new class_4587();
  Matrix4f[] matrices,relatives;
  Object[] templates;
  static Matrix4f[] matrices(int count){Matrix4f[] result=new Matrix4f[count];for(int i=0;i<count;i++)result[i]=new Matrix4f();return result;}
 }
 static Workspace prepare(PosedSurfaceSupport.State state,Object model,Object[] cooked,boolean slim,boolean hideHead)throws Exception{
  Workspace w=state.pre30Scratch;
  if(w==null)state.pre30Scratch=w=new Workspace();
  while(w.busy){if(w.next==null)w.next=new Workspace();w=w.next;}
  w.busy=true;ACTIVE.set(w);
  w.matrices=state.matrices==w.matrixBanks[0]?w.matrixBanks[1]:w.matrixBanks[0];
  w.relatives=state.relatives==w.relativeBanks[0]?w.relativeBanks[1]:w.relativeBanks[0];
  w.templates=state.template==w.templateBanks[0]?w.templateBanks[1]:w.templateBanks[0];
  for(int p=0;p<6;p++){
   class_630 low=(class_630)SurfaceGeometry.field(model,PosedSurfaceSupport.LOWER[p]);
   class_630 up=(class_630)SurfaceGeometry.field(model,PosedSurfaceSupport.UPPER[p]);
   up.field_37938=up.field_37939=up.field_37940=1;
   w.stack.method_34426();
   low.method_22703(w.stack);
   w.matrices[p].set(w.stack.method_23760().method_23761());
   up.method_22703(w.stack);
   w.matrices[p+6].set(w.stack.method_23760().method_23761());
   w.relative.method_34426();
   up.method_22703(w.relative);
   w.relatives[p].set(w.relative.method_23760().method_23761());
   w.templates[p]=p==0&&hideHead?null:cooked[slim&&p>=4?p+2:p];
   up.field_3665=!(p==0&&hideHead);
  }
  return w;
 }
 static void apply(Object model,Object[] cooked,boolean slim,boolean hideHead,boolean firstPerson)throws Exception{
  long start=DIAGNOSTICS?System.nanoTime():0;
  // The model is not retained in any extra map; its existing State owns its scratch objects.
  synchronized(model){
   Workspace previous=ACTIVE.get();
   try{PosedSurfaceSupport.applyPRE30(model,cooked,slim,hideHead,firstPerson);}
   finally{Workspace used=ACTIVE.get();if(used!=previous&&used!=null)used.busy=false;ACTIVE.set(previous);}
  }
  if(DIAGNOSTICS){long duration=System.nanoTime()-start;applyCount.incrementAndGet();applyNanos.addAndGet(duration);applyMaxNanos.accumulateAndGet(duration,Math::max);}
 }
 static void clear(Object model)throws Exception{synchronized(model){PosedSurfaceSupport.clearPRE30(model);}}
}
