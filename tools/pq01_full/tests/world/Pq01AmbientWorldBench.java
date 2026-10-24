package net.hautecapitale.quetes.sequence;
import java.util.*;
import java.lang.reflect.*;
import static net.hautecapitale.quetes.sequence.SequenceBridge.*;
import static net.hautecapitale.quetes.sequence.Pq01Bridge.*;
/** Déclenchement d'ambiance par le tick de production, sans appel direct à travel. */
public final class Pq01AmbientWorldBench {
  static Object player,world,view,record,entity;static int ticks,phase,checks,pauseAt;static Pq01Motion.Point start,paused;
  static void check(boolean value,String label){checks++;if(!value)throw new AssertionError("ambient: "+label);System.out.println("AMBIENT CHECK "+label);}
  static void set(Object o,String key,Object value)throws Exception{Field f=o.getClass().getDeclaredField(key);f.setAccessible(true);f.set(o,value);}
  static Object currentView()throws Exception{Field f=Pq01Bridge.class.getDeclaredField("VIEWS");f.setAccessible(true);return ((Map<?,?>)f.get(null)).get(uuid(player));}
  static Object perrinRecord()throws Exception{Object v=currentView();return v==null?null:((Map<?,?>)Pq01WorldBench.reflect(v,"actors")).get("perrin");}
  static void score(String key,int n){run(player,"scoreboard players set @s "+key+" "+n);}
  static void start(Object p)throws Exception{
    player=p;world=Pq01WorldBench.world;ticks=0;phase=0;
    Pq01WorldBench.command("fill 7 64 3 10 67 6 minecraft:air");
    Object setup=GuardNbt.copy(storage(p,"capitale:pq01_setup")),points=GuardNbt.childOrCreate(setup,"points");
    GuardNbt.put(points,"perrin_a",pointNbt(new Pq01Motion.Point(dimension(world),0.5,64,4.5)));
    GuardNbt.put(points,"perrin_b",pointNbt(new Pq01Motion.Point(dimension(world),1.5,64,4.5)));
    GuardNbt.put(points,"perrin_a_quai",pointNbt(new Pq01Motion.Point(dimension(world),0.5,64,4.5)));
    GuardNbt.put(points,"perrin_b_quai",pointNbt(new Pq01Motion.Point(dimension(world),4.5,64,4.5)));
    GuardNbt.put(GuardNbt.childOrCreate(setup,"bindings"),"perrin_quai",Pq01WorldBench.bindingFor(Pq01WorldBench.perrinSource));
    GuardNbt.integer(setup,"move_timeout_ticks",300);GuardNbt.integer(setup,"ambient_enabled",1);GuardNbt.save(server(p),"capitale:pq01_setup",setup);
    for(String s:List.of("hcm_pq1_active","CAP_QDIALOG_OWNER","CAP_PROFILE_LOCK","CAP_QSEQ"))score(s,0);
    score("hcq_pq01",0);score("hcf_pq1_a",0);start=pos(Pq01WorldBench.perrinSource,world);
  }
  @SuppressWarnings("unchecked") static void productionTick(){
    List<Object> players=(List<Object>)call(call(server(player),"method_3760"),"method_14571");
    players.add(player);try{Pq01Bridge.tick(server(player));}finally{players.remove(player);}
  }
  static boolean leg()throws Exception{return Boolean.TRUE.equals(Pq01WorldBench.reflect(record,"leg"));}
  static Pq01Motion motion()throws Exception{return (Pq01Motion)Pq01WorldBench.reflect(record,"motion");}
  static void ambient(boolean on){Object setup=GuardNbt.copy(storage(player,"capitale:pq01_setup"));GuardNbt.integer(setup,"ambient_enabled",on?1:0);GuardNbt.save(server(player),"capitale:pq01_setup",setup);}
  static void close()throws Exception{Pq01WorldBench.invoke("close",new Class<?>[]{Object.class,boolean.class},player,false);}
  static String tick()throws Exception{
    ticks++;productionTick();
    if(phase==0){
      record=perrinRecord();
      if(record!=null){entity=Pq01WorldBench.reflect(record,"entity");Pq01Motion m=motion();
        if(m!=null&&!m.done()&&pos(entity,world).distance2(start)>0.04){
          check(SequenceBridge.score(player,"hcq_pq01")==0,"Perrin starts before first quest progress");
          check(pos(entity,world).distance2(start)>0.04,"Perrin physically walks on one-block route");
          view=currentView();score("hcm_pq1_active",1);score("CAP_QDIALOG_OWNER",96);
          check(Pq01WorldBench.invoke("camera",new Class<?>[]{Object.class,String.class},player,"perrin").equals(1),"production camera prepares Perrin conversation");
          set(view,"pendingCamera",null);check(motion()==null,"conversation cancels Perrin active motion");
          phase=1;pauseAt=ticks;
        }
      }
      if(ticks>140)throw new AssertionError("Perrin never starts before first quest progress");
    }else if(phase==1){
      if(ticks==pauseAt+10)paused=pos(entity,world);
      if(ticks>=pauseAt+40){
        check(pos(entity,world).distance2(paused)<0.001,"Perrin remains still while his conversation is active");
        score("hcm_pq1_active",0);score("CAP_QDIALOG_OWNER",0);set(view,"cameraRole",null);phase=2;
      }
    }else if(phase==2&&leg()){
      check(pos(entity,world).distance2(new Pq01Motion.Point(dimension(world),1.5,64,4.5))<=0.16,"occupation resumes and reaches nearby B");phase=3;
    }else if(phase==3&&!leg()){
      check(pos(entity,world).distance2(start)<=0.16,"occupation walks back to nearby A");
      check(pos(Pq01WorldBench.perrinSource,world).distance2(start)<0.001,"Perrin original remains at post");
      score("hcq_pq01",40);phase=4;pauseAt=ticks;
    }else if(phase==4){
      Object next=perrinRecord();
      if(next!=null&&next!=record){check(Boolean.TRUE.equals(call(entity,"method_31481")),"phase change removes previous private actor");record=next;entity=Pq01WorldBench.reflect(record,"entity");phase=5;}
      if(ticks-pauseAt>120)throw new AssertionError("quai actor never starts");
    }else if(phase==5&&leg()){
      check(pos(entity,world).distance2(new Pq01Motion.Point(dimension(world),4.5,64,4.5))<=0.4225,"quai route uses its own B point");phase=6;
    }else if(phase==6&&!leg()){
      check(pos(entity,world).distance2(start)<=0.4225,"quai occupation returns to its A point");
      close();ambient(false);phase=7;pauseAt=ticks;
    }else if(phase==7&&ticks>=pauseAt+80){
      check(perrinRecord()==null,"explicitly disabled ambiance does not respawn Perrin");
      ambient(true);phase=8;pauseAt=ticks;
    }else if(phase==8&&ticks>=pauseAt+80){
      record=perrinRecord();check(record!=null,"reactivated ambiance resumes Perrin without rebinding");entity=Pq01WorldBench.reflect(record,"entity");
      net.tompsen.nexuscharacters.NexusCharacters.selected=false;productionTick();
      check(currentView()==null&&Boolean.TRUE.equals(call(entity,"method_31481")),"character exit cleans personal actor");
      net.tompsen.nexuscharacters.NexusCharacters.selected=true;
      check(pos(Pq01WorldBench.perrinSource,world).distance2(start)<0.001,"original unchanged after full occupation lifecycle");
      return "PASS "+checks+" ambient occupation checks: production tick, pre-quest activation, one-block A/B route, conversation pause/resume, quai points, disable/reactivate, character cleanup; real EasyNPC navigation and camera preparation, graphical capture doubled. No direct travel invocation.\n";
    }
    if(ticks>1200)throw new AssertionError("automatic Perrin timeout phase "+phase);
    return null;
  }
}
