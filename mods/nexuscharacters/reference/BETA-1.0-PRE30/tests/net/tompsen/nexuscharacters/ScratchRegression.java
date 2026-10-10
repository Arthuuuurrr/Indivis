package net.tompsen.nexuscharacters;
import net.minecraft.*;
import org.joml.*;
import java.util.*;
import java.lang.ref.*;
public final class ScratchRegression {
 static int checks;
 static void check(boolean c,String t){checks++;if(!c)throw new AssertionError(t);}
 static final class Model {
  public class_630 field_3398=new class_630(List.of(),Map.of()),field_3391=new class_630(List.of(),Map.of()),field_3397=new class_630(List.of(),Map.of()),field_3392=new class_630(List.of(),Map.of()),field_27433=new class_630(List.of(),Map.of()),field_3401=new class_630(List.of(),Map.of());
  public class_630 field_3394=new class_630(List.of(),Map.of()),field_3483=new class_630(List.of(),Map.of()),field_3482=new class_630(List.of(),Map.of()),field_3479=new class_630(List.of(),Map.of()),field_3484=new class_630(List.of(),Map.of()),field_3486=new class_630(List.of(),Map.of());
 }
 static void release(PoseScratch.Workspace w){w.busy=false;PoseScratch.ACTIVE.set(null);}
 static List<WeakReference<?>> gcFixture()throws Exception{
  Model m=new Model();var state=new PosedSurfaceSupport.State();PosedSurfaceSupport.STATES.put(m,state);var w=PoseScratch.prepare(state,m,new Object[8],false,false);release(w);
  return List.of(new WeakReference<>(m),new WeakReference<>(state),new WeakReference<>(w));
 }
 public static void main(String[] args)throws Exception{
  Model model=new Model();var state=new PosedSurfaceSupport.State();Object[] templates={0,1,2,3,4,5,6,7};java.util.Random random=new java.util.Random(1930);
  for(int frame=0;frame<300;frame++){
   Matrix4f[] baseline=new Matrix4f[12],relatives=new Matrix4f[6];
   for(int p=0;p<6;p++){
    class_630 low=(class_630)SurfaceGeometry.field(model,PosedSurfaceSupport.LOWER[p]),up=(class_630)SurfaceGeometry.field(model,PosedSurfaceSupport.UPPER[p]);
    low.field_3654=random.nextFloat()*2-1;low.field_3675=random.nextFloat()*2-1;low.field_3674=random.nextFloat()*2-1;
    low.field_3657=random.nextFloat()*12-6;low.field_3656=random.nextFloat()*12-6;low.field_3655=random.nextFloat()*12-6;
    low.field_37938=random.nextFloat()+.5f;low.field_37939=random.nextFloat()+.5f;low.field_37940=random.nextFloat()+.5f;
    up.field_3654=random.nextFloat()*.2f;up.field_3657=random.nextFloat();up.field_37938=up.field_37939=up.field_37940=1;
    class_4587 stack=new class_4587();low.method_22703(stack);baseline[p]=new Matrix4f(stack.method_23760().method_23761());up.method_22703(stack);baseline[p+6]=new Matrix4f(stack.method_23760().method_23761());
    class_4587 relative=new class_4587();up.method_22703(relative);relatives[p]=new Matrix4f(relative.method_23760().method_23761());
   }
   boolean slim=frame%2==1,hide=frame%3==0;var w=PoseScratch.prepare(state,model,templates,slim,hide);
   check(Arrays.equals(baseline,w.matrices),"Actual Minecraft matrix differs frame="+frame);check(Arrays.equals(relatives,w.relatives),"Relative matrix differs frame="+frame);
   for(int p=0;p<6;p++)check(Objects.equals(w.templates[p],p==0&&hide?null:templates[slim&&p>=4?p+2:p]),"Slim selection differs");
   state.matrices=w.matrices;state.relatives=w.relatives;state.template=w.templates;Matrix4f[] snapshot=Arrays.stream(w.matrices).map(Matrix4f::new).toArray(Matrix4f[]::new);release(w);
   var next=PoseScratch.prepare(state,model,templates,slim,hide);check(next.matrices!=state.matrices&&Arrays.equals(state.matrices,snapshot),"Stable previous snapshot mutated");
   var nested=PoseScratch.prepare(state,model,templates,!slim,!hide);check(nested!=next&&nested.stack!=next.stack&&nested.matrices!=next.matrices,"Reentrant temporary buffers aliased");release(nested);release(next);
  }
  var refs=gcFixture();for(int i=0;i<40&&refs.stream().anyMatch(r->r.get()!=null);i++){System.gc();Thread.sleep(20);PosedSurfaceSupport.STATES.size();}
  check(refs.stream().noneMatch(r->r.get()!=null),"Scratch retains model/state");
  System.out.println("SCRATCH_PASS checks="+checks+" realMinecraftTransforms=300 nonuniformScale+rotation+translation+slim+hideHead+snapshotIsolation+reentrancy+weakStateGC");
 }
}
