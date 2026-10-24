package net.hautecapitale.quetes.sequence;
import java.lang.reflect.*;
import static net.hautecapitale.quetes.sequence.SequenceBridge.*;
import static net.hautecapitale.quetes.sequence.Pq01Bridge.*;
/** Vrai contrôleur de scène + vraies fonctions + marche EasyNPC ; caméra/panneau doublés. */
public final class Pq01SceneWorldBench {
  static Object player,world,view,actor,record,lookSession;static int phase,ticks,checks;static Pq01Motion.Point initial,at,previous;static double maxStep;
  static void set(String n,Object value)throws Exception{Field f=view.getClass().getDeclaredField(n);f.setAccessible(true);f.set(view,value);}
  static void check(boolean ok,String label){checks++;if(!ok)throw new AssertionError("scene: "+label);System.out.println("SCENE CHECK "+label);}
  static Pq01Motion motion()throws Exception{return (Pq01Motion)Pq01WorldBench.reflect(record,"motion");}
  static void sameActor(){check(!Boolean.TRUE.equals(call(actor,"method_31481"))&&!visibleOriginal(Pq01WorldBench.source,player),"same Gautier actor retained and original hidden");}
  static void look(boolean open)throws Exception{
    Class<?> session=type("net.hautecapitale.dialogue.session.DialogueSession");
    if(open)lookSession=session.getConstructors()[0].newInstance(700,uuid(player),uuid(actor),call(actor,"method_5628"),"Gautier",call(world,"method_27983"),en("net.hautecapitale.dialogue.session.DialogueMode","CAPTURE"),null,0,0L,call(player,"method_73189"));
    Method m=type("net.hautecapitale.dialogue.compat.easynpc.EasyNpcLookAt").getDeclaredMethod(open?"poser":"retirer",type("net.minecraft.class_3222"),session);m.setAccessible(true);m.invoke(null,player,lookSession);
    if(open){Object objectives=call(actor,"getEasyNPCObjectiveData");boolean present=Boolean.TRUE.equals(call(objectives,"hasObjective",en("de.markusbordihn.easynpc.data.objective.ObjectiveType","LOOK_AT_ENTITY_BY_UUID")));check(present,"native Dialogue look objective present while walking (created="+session.getField("regardPose").get(lookSession)+", worldResolved="+(call(world,"method_66347",uuid(actor))==actor)+")");}
  }
  static void start(Object p)throws Exception{
    player=p;world=Pq01WorldBench.world;Pq01PipelineBench.active=true;Pq01PipelineBench.realScene=true;Pq01PipelineBench.seed("geraud");
    view=Pq01WorldBench.invoke("view",new Class<?>[]{Object.class},player);Pq01WorldBench.view=view;
    set("scene",true);set("approach",new Pq01Motion.Point(dimension(world),9.5,64.5,0.5));
    record=Pq01WorldBench.spawn("gautier");actor=Pq01WorldBench.reflect(record,"entity");Object geraud=Pq01WorldBench.spawn("geraud");
    set("cameraRole","geraud");set("camera",Pq01WorldBench.reflect(geraud,"entity"));
    Object setup=GuardNbt.copy(storage(player,"capitale:pq01_setup"));GuardNbt.integer(setup,"move_timeout_ticks",300);GuardNbt.save(server(player),"capitale:pq01_setup",setup);
    phase=-1;ticks=0;initial=pos(actor,world);previous=initial;
  }
  static String tick()throws Exception{
    Pq01Motion.Point now=pos(actor,world);maxStep=Math.max(maxStep,Math.sqrt(now.distance2(previous)));previous=now;
    ticks++;
    if(phase==-1){if(ticks<20)return null;Pq01PipelineBench.node("a2");check(score(player,"hcm_pq1_node")==2&&motion()!=null&&!motion().done(),"Gautier starts while Géraud a2 is displayed");phase=0;ticks=0;return null;}
    Pq01Motion m=motion();if(m!=null&&!m.done())m.tick();
    if(phase==0&&ticks>=8){
      check(pos(actor,world).distance2(initial)>0.01&&!motion().done(),"Gautier physically walking before interruption");
      look(true);
      Pq01PipelineBench.automatic();check(Pq01PipelineBench.current.equals("a3")&&!motion().done()&&score(player,"hcm_pq1_wait")==0,"Gautier speaks during approach without waiting for arrival");
      check(call(Pq01PipelineBench.state,"pnjNom").equals("Gautier"),"native capture speaker is only Gautier");phase=1;ticks=0;
    }else if(phase==1&&motion().done()){
      sameActor();look(false);at=pos(actor,world);Pq01PipelineBench.automatic();Pq01PipelineBench.automatic();check(Pq01PipelineBench.current.equals("a5"),"Géraud admonishment while same actor stays");phase=2;ticks=0;
    }else if(phase==2&&ticks==20){
      at=pos(actor,world);check(at.distance2(initial)>25,"Gautier remains near dialogue point after physical settling");
    }else if(phase==2&&ticks>=40){
      sameActor();check(pos(actor,world).distance2(at)<0.001,"Gautier stays at reached point during conversation");
      Pq01PipelineBench.automatic();check(Pq01PipelineBench.current.equals("a6"),"Gautier final reply before return");Pq01PipelineBench.automatic();
      check(score(player,"hcm_pq1_node")==6&&!motion().done()&&score(player,"hcm_pq1_wait")==1,"return begins after Gautier final reply");phase=3;ticks=0;
    }else if(phase==3&&motion().done()){
      sameActor();check(Pq01PipelineBench.current.equals("a7")&&pos(actor,world).distance2(initial)<=2.25,"same actor walks back before Géraud resumes");
      set("approachStarted",false);set("approach",new Pq01Motion.Point(dimension(world),8.5,64,4.5));
      Object setup=GuardNbt.copy(storage(player,"capitale:pq01_setup"));GuardNbt.integer(setup,"move_timeout_ticks",80);GuardNbt.save(server(player),"capitale:pq01_setup",setup);
      Pq01PipelineBench.node("a2");Pq01PipelineBench.automatic();phase=4;ticks=0;
    }else if(phase==4&&motion().done()){
      sameActor();check(Pq01PipelineBench.current.equals("a3")&&score(player,"hcm_pq1_active")==1,"blocked approach keeps actor and conversation instead of respawning original");
      Object setup=GuardNbt.copy(storage(player,"capitale:pq01_setup"));GuardNbt.put(GuardNbt.childOrCreate(setup,"points"),"gautier_poste",pointNbt(new Pq01Motion.Point(dimension(world),8.5,64,4.5)));GuardNbt.save(server(player),"capitale:pq01_setup",setup);
      Pq01PipelineBench.node("a6");Pq01PipelineBench.automatic();phase=5;ticks=0;
    }else if(phase==5&&motion().done()){
      sameActor();check(Pq01PipelineBench.current.equals("a7"),"blocked return keeps actor and resumes Géraud");check(maxStep<1.3,"no instantaneous actor position jump during entire scene");
      Pq01PipelineBench.pick(Pq01PipelineBench.choices().size()-1);check(Boolean.TRUE.equals(call(actor,"method_31481"))&&visibleOriginal(Pq01WorldBench.source,player),"actor cleaned only when conversation closes");
      Pq01PipelineBench.active=false;Pq01PipelineBench.realScene=false;
      return "PASS "+checks+" coupled scene checks: production Pq01Bridge, actual PQ01 functions and EasyNPC path navigation; camera and panel doubled. Approach during a2, speech during walking, stationary conversation, walking return, blocked approach/return retain actor, no teleport.\n";
    }else if(ticks>400)throw new AssertionError("coupled scene timeout phase "+phase);
    return null;
  }
}
