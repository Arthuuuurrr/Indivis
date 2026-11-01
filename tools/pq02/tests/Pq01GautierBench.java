package net.hautecapitale.quetes.sequence;

import java.util.*;
import java.lang.reflect.*;
import net.hautecapitale.quetes.sequence.Pq01Motion.Point;
import static net.hautecapitale.quetes.sequence.SequenceBridge.*;
import static net.hautecapitale.quetes.sequence.Pq01Bridge.*;
import static net.hautecapitale.quetes.sequence.Pq02Runtime.score2;
import static net.hautecapitale.quetes.sequence.Pq02WorldBench.get;
import static net.hautecapitale.quetes.sequence.Pq02WorldBench.set;

/** Marche EasyNPC réelle et fonctions PQ01 réelles, sans rendu graphique. */
public final class Pq01GautierBench {
 static Object player,other,view,otherView,gautier,geraud,otherActor,srcGautier,srcGeraud,savedConfig;
 static Point post,approach,srcPost,srcGeraudPost,playerPost,otherPost;
 static int phase,ticks;static boolean movingChecked;
 static Map<String,Integer> saved=new LinkedHashMap<>(),otherScores=new LinkedHashMap<>();
 static Object bridge(String name,Object...args){
  for(Method m:Pq01Bridge.class.getDeclaredMethods())if(m.getName().equals(name)&&m.getParameterCount()==args.length){
   try{m.setAccessible(true);return m.invoke(null,args);}catch(IllegalArgumentException ignored){}catch(InvocationTargetException e){throw new RuntimeException(e.getCause());}catch(ReflectiveOperationException e){throw new RuntimeException(e);}
  }throw new IllegalStateException(name);
 }
 static void check(boolean value,String message){Pq02WorldBench.ok(value,"Gautier HF3: "+message);}
 static Object entity(Object actor)throws Exception{return get(actor,"entity");}
 static void seed(Object p,Map<String,Integer> scores){
  for(String key:List.of("hcq_pq01","hcm_pq1_active","hcm_pq1_node","hch_pq1node","hcf_pq1_a","CAP_QDIALOG_OWNER","CAP_PROFILE_LOCK","hcm_pq1_wait","hcm_pq1_voice"))scores.put(key,score(p,key));
  score2(p,"hcq_pq01",10);score2(p,"hcm_pq1_active",1);score2(p,"hcm_pq1_node",2);score2(p,"hch_pq1node",2);score2(p,"hcf_pq1_a",0);score2(p,"CAP_QDIALOG_OWNER",91);score2(p,"CAP_PROFILE_LOCK",0);score2(p,"hcm_pq1_wait",0);score2(p,"hcm_pq1_voice",0);
 }
 static Object binding(Object npc,Point point){Object n=GuardNbt.compound();GuardNbt.string(n,"uuid",uuid(npc).toString());GuardNbt.string(n,"dimension",point.dimension());GuardNbt.string(n,"type",Pq01EntityFactory.registryId(npc));GuardNbt.put(n,"post",pointNbt(point));return n;}
 static void start(Object p,Object spectator)throws Exception{
  player=p;other=spectator;phase=0;ticks=0;movingChecked=false;Object world=world(p);
  playerPost=pos(p,world);otherPost=pos(other,world);savedConfig=Pq01Nbt.copy(setup(p));
  srcGautier=Pq02WorldBench.npc(13.5,12.5,"fixture_gautier");srcGeraud=Pq02WorldBench.npc(1.5,12.5,"fixture_geraud");
  srcPost=post=pos(srcGautier,world);srcGeraudPost=pos(srcGeraud,world);approach=new Point(post.dimension(),3.5,64,12.5);
  Object config=Pq01Nbt.copy(savedConfig),bindings=GuardNbt.childOrCreate(config,"bindings"),points=GuardNbt.childOrCreate(config,"points");
  GuardNbt.put(bindings,"gautier_large",binding(srcGautier,post));GuardNbt.put(bindings,"geraud_large",binding(srcGeraud,srcGeraudPost));
  GuardNbt.put(points,"gautier_poste",pointNbt(post));GuardNbt.put(points,"gautier_approche",pointNbt(approach));GuardNbt.integer(config,"ambient_enabled",0);save(p,config);
  seed(p,saved);seed(other,otherScores);call(p,"method_5814",2.5d,64d,15.5d);call(other,"method_5814",2.5d,64d,17.5d);
  view=bridge("view",p);set(view,"scene",true);set(view,"approach",approach);
  geraud=bridge("spawn",view,"geraud",null);gautier=bridge("spawn",view,"gautier",post);
  otherView=bridge("view",other);set(otherView,"scene",true);set(otherView,"approach",approach);otherActor=bridge("spawn",otherView,"gautier",post);
  check(!uuid(entity(gautier)).equals(uuid(entity(otherActor))),"two players retain distinct actors");
  check(!Boolean.TRUE.equals(stat("net.hautecapitale.quetes.visibility.QuestVisibility","isVisibleTo",entity(gautier),other)),"Gautier is invisible to the other player");
  set(view,"cameraRole","geraud");check(Boolean.TRUE.equals(bridge("openCamera",view,entity(geraud))),"Geraud capture opens before the interruption");
  bridge("startApproach",p);run(p,"function capitale:pq01/node/a3");
 }
 static boolean tick()throws Exception{
  Object world=world(player);Object actor=entity(gautier);Point here=pos(actor,world);ticks++;
  if(phase==0){
   Pq01Motion motion=(Pq01Motion)get(gautier,"motion");
   if(ticks>=4&&here.distance2(post)>0.01&&!movingChecked){
    check(here.distance2(post)>0.01,"Gautier actually walks from his post");
    check(Math.abs(((Number)get(call(actor,"getGroundPathNavigation"),"field_6668")).doubleValue()-1.25)<0.001,"navigation uses the requested 25 percent speed increase");
    check(score(player,"hcm_pq1_node")==3&&score(player,"hcm_pq1_wait")==0,"Gautier speaks during his approach without a waiting page");movingChecked=true;
   }
   if(ticks>180)throw new AssertionError("Gautier approach timeout: "+here+" / "+get(gautier,"failure"));
   if(motion!=null&&motion.done()){
    check(movingChecked&&here.distance2(approach)<=2.25,"Gautier reaches the existing approach point after a measured walk");
    check(pos(srcGautier,world).equals(srcPost)&&pos(srcGeraud,world).equals(srcGeraudPost),"public NPC positions remain untouched");
    run(player,"function capitale:pq01/node/a6");phase=1;ticks=0;
   }return false;
  }
  if(phase==1){
   if(ticks<3)return false;check(here.distance2(approach)<=2.25,"Gautier stays at the dialogue point for his reply");
   run(player,"indivispq01 return");
   check(score(player,"hcm_pq1_node")==7&&score(player,"hcm_pq1_wait")==0,"Geraud resumes immediately while Gautier returns");
   check(!((Pq01Motion)get(gautier,"motion")).done()&&pos(actor,world).distance2(post)>2.25,"return is real movement and has not teleported to the post");
   check(get(view,"camera")==entity(geraud),"camera returns to Geraud while Gautier walks away");
   phase=2;ticks=0;return false;
  }
  Pq01Motion motion=(Pq01Motion)get(gautier,"motion");
  if(ticks>180)throw new AssertionError("Gautier return timeout: "+here+" / "+get(gautier,"failure"));
  if(motion!=null&&motion.done()){
   check(here.distance2(post)<=2.25,"Gautier reaches his post by navigation");
   check(score(player,"hcm_pq1_node")==7,"arrival does not replay or advance Geraud's dialogue");
   bridge("close",player,true);
   check(!Boolean.TRUE.equals(call(entity(otherActor),"method_31481")),"closing one scene keeps the other player's actor");bridge("close",other,true);
   save(player,savedConfig);for(var e:saved.entrySet())score2(player,e.getKey(),e.getValue());for(var e:otherScores.entrySet())score2(other,e.getKey(),e.getValue());
   call(player,"method_5814",playerPost.x(),playerPost.y(),playerPost.z());call(other,"method_5814",otherPost.x(),otherPost.y(),otherPost.z());
   call(srcGautier,"method_31472");call(srcGeraud,"method_31472");return true;
  }return false;
 }
}
