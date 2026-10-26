package net.hautecapitale.quetes.sequence;

import net.fabricmc.api.ModInitializer;
import java.util.*;
import java.lang.reflect.*;
import java.nio.file.*;
import static net.hautecapitale.quetes.sequence.SequenceBridge.*;
import static net.hautecapitale.quetes.sequence.Pq01Bridge.*;

/** Vraies fonctions, prédicats, EasyNPC et suivi réseau. Identité Nexus/connexions synthétiques. */
public final class CoreQuestMarkersBench implements ModInitializer {
 static Object server,world,player,npc;static int ticks,checks;static boolean finished;
 static final List<String> evidence=new ArrayList<>();
 static final List<Object> players=Pq01MultiWorldBench.players;
 static final Set<Integer> markerIds=new HashSet<>();
 static List<Map<String,Object>> cases,givers;static Map<String,Object> base;
 public void onInitialize(){
  event("net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback","EVENT",a->{register(a[0]);Pq01WorldBench.stub(a[0],"hcd_interpellation");Pq01WorldBench.stub(a[0],"indivissequence");return null;});
  event("net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents$ServerStarted","SERVER_STARTED",a->{try{start(a[0]);}catch(Throwable t){fail(t);}return null;});
  event("net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents$EndTick","END_SERVER_TICK",a->{try{tick();}catch(Throwable t){fail(t);}return null;});
 }
 static void check(boolean b,String s){checks++;if(!b)throw new AssertionError(s);evidence.add(s);}
 @SuppressWarnings("unchecked") static void start(Object s)throws Exception{
  server=s;Pq01MultiWorldBench.start(s);world=Pq01MultiWorldBench.world;player=players.get(0);npc=Pq01WorldBench.source;
  Map<String,Object> root=(Map<String,Object>)type("com.google.gson.Gson").getConstructor().newInstance().getClass().getMethod("fromJson",String.class,Class.class).invoke(type("com.google.gson.Gson").getConstructor().newInstance(),Files.readString(Path.of("qmark-fixtures.json")),Map.class);
  cases=(List<Map<String,Object>>)root.get("cases");givers=(List<Map<String,Object>>)root.get("givers");base=(Map<String,Object>)root.get("base");
  for(Object key:(List<?>)root.get("objectives"))run(player,"scoreboard objectives add "+key+" dummy");
  for(Object p:players)call(p,"method_5814",0.5d,64d,2.5d);
 }
 static void setScores(Object p,Map<?,?> values){for(var e:values.entrySet())run(p,"scoreboard players set @s "+e.getKey()+" "+((Number)e.getValue()).intValue());}
 static int probe(Object p,String qid){run(p,"execute store result score @s qmark_probe run function capitale:quest/markers/available/"+qid);return score(p,"qmark_probe");}
 static void context(Object p){Object n=GuardNbt.compound(),current=GuardNbt.childOrCreate(n,"current");GuardNbt.string(current,"owner",uuid(p).toString());GuardNbt.string(current,"character",character(p).toString());GuardNbt.save(server,"hc_quetes:session",n);}
 static void update(Object p){context(p);run(p,"function hc_quetes:runtime/second");}
 static List<Object> markers(Object p){List<Object> out=new ArrayList<>();for(Object e:(Iterable<?>)call(world,"method_27909"))if(tags(e).contains("cap_qmark")&&(p==null||tags(e).contains("hcm_owner_"+uuid(p))))out.add(e);return out;}
 static int id(Object e){return ((Number)call(e,"method_5628")).intValue();}
 static void trackers()throws Exception{
  Object manager=Pq01MultiWorldBench.get(call(world,"method_14178"),"field_17254");
  for(Object p:players)call(Pq01MultiWorldBench.get(Pq01MultiWorldBench.get(p,"field_13987"),"field_45026"),"method_52387",p,call(npc,"method_31476"));
  for(Object tracker:((Map<?,?>)Pq01MultiWorldBench.get(manager,"field_18242")).values())for(Object p:players)call(tracker,"method_18736",p);
  for(Object marker:markers(null))markerIds.add(id(marker));
 }
 static void privateMatrix()throws Exception{
  trackers();for(Object p:players)for(Object marker:markers(null)){
   boolean own=tags(marker).contains("hcm_owner_"+uuid(p))&&tags(marker).contains("hcm_character_"+character(p));
   check(Pq01MultiWorldBench.live.get(p).contains(id(marker))==own,"private marker tracked correctly by observer "+players.indexOf(p));
  }
 }
 static void cleanup(List<Object> online)throws Exception{Method m=SequenceBridge.class.getDeclaredMethod("cleanupPrivate",Object.class,List.class);m.setAccessible(true);m.invoke(null,server,online);}
 static void name(String s){call(npc,"method_5665",stat("net.minecraft.class_2561","method_43470",s));}
 static void clear(){run(player,"kill @e[type=minecraft:text_display,tag=cap_qmark]");}
 @SuppressWarnings("unchecked") static void runRules()throws Exception{
  for(Map<String,Object> c:cases){setScores(player,(Map<?,?>)c.get("scores"));check(probe(player,(String)c.get("qid"))==((Number)c.get("expected")).intValue(),(String)c.get("label"));}
  for(Map<String,Object> giver:givers){
   clear();name((String)giver.get("name"));setScores(player,(Map<?,?>)giver.get("scores"));Object before=GuardNbt.entity(npc);update(player);
   check(Objects.equals(before,GuardNbt.entity(npc)),"NPC NBT unchanged by marker: "+giver.get("giver"));
   check(markers(player).size()==1,"name discovery and native runtime hook: "+giver.get("giver"));
   Object marker=markers(player).get(0);check(tags(marker).contains("cap_qgiver_"+giver.get("giver")),"correct giver marker: "+giver.get("giver"));
   check(Math.abs(num(call(marker,"method_23318"))-num(call(npc,"method_23318"))-2.6)<0.001,"marker above NPC: "+giver.get("giver"));
  }
  setScores(player,base);run(player,"scoreboard players reset @s hcm_pq1_active");
  check(probe(player,"pq01")==1,"first prologue offer tolerates missing transient active score");
  run(player,"scoreboard players reset @s hcd_dqhc03");check(probe(player,"dqhc03")==1,"daily offer tolerates missing zero cooldown as actual core interaction does");
  clear();name("Mira");for(Object p:players)setScores(p,base);
  run(players.get(2),"scoreboard players set @s hcq_sqbaob01 101");
  update(players.get(0));update(players.get(1));update(players.get(2));
  check(markers(players.get(0)).size()==1&&markers(players.get(1)).size()==1&&markers(players.get(2)).isEmpty(),"two owners offered, completed observer has no !");
 }
 static Object firstA,firstB;static UUID stableA;static int initialSpawn;static Pq01Motion.Point npcInitial;
 static void tick()throws Exception{
  if(finished||server==null)return;ticks++;
  if(ticks==20){runRules();npcInitial=pos(npc,world);firstA=markers(players.get(0)).get(0);firstB=markers(players.get(1)).get(0);stableA=uuid(firstA);}
  if(ticks==23){privateMatrix();initialSpawn=Pq01MultiWorldBench.spawnCounts.get(players.get(0)).getOrDefault(id(firstA),0);check(initialSpawn==1,"owner received marker spawn exactly once");}
  if(ticks==25){
   Map<String,Integer> state=new HashMap<>();for(String k:base.keySet())if(k.startsWith("hcq_")||k.startsWith("hcf_"))state.put(k,score(players.get(0),k));
   for(int i=0;i<20;i++){update(players.get(0));update(players.get(1));}
   check(state.entrySet().stream().allMatch(e->score(players.get(0),e.getKey())==e.getValue()),"all quest states and outcomes remain unchanged after 20 updates");
   check(markers(players.get(0)).size()==1&&uuid(markers(players.get(0)).get(0)).equals(stableA),"20 updates reuse the same marker UUID");
   check(Pq01MultiWorldBench.spawnCounts.get(players.get(0)).get(id(firstA))==initialSpawn,"stationary updates cause no spawn churn");
   check(score(players.get(0),"hcq_sqbaob01")==0,"marker does not accept or advance the quest");
   call(npc,"method_5814",2.5d,64d,2.5d);update(players.get(0));
   check(pos(firstA,world).distance2(new Pq01Motion.Point(dimension(world),2.5,66.6,2.5))<0.001,"marker follows moved NPC");
   name("Renamed NPC");call(npc,"method_5780","cap_qgiver_mira");update(players.get(0));check(uuid(markers(players.get(0)).get(0)).equals(stableA),"explicit binding supports renamed NPC and reuses marker");
   run(players.get(0),"scoreboard players set @s CAP_PROFILE_LOCK 1");update(players.get(0));check(markers(players.get(0)).isEmpty(),"profile lock removes marker");
   run(players.get(0),"scoreboard players set @s CAP_PROFILE_LOCK 0");update(players.get(0));
  }
  if(ticks==30){
   privateMatrix();Object p=players.get(0),old=markers(p).get(0);net.tompsen.nexuscharacters.NexusCharacters.select(p,UUID.randomUUID());trackers();
   check(!Pq01MultiWorldBench.live.get(p).contains(id(old)),"character switch immediately rejects old marker in native tracker");
   cleanup(players);check(Boolean.TRUE.equals(call(old,"method_31481")),"legacy cleanup removes previous character marker");update(p);
   check(markers(p).size()==1&&tags(markers(p).get(0)).contains("hcm_character_"+character(p)),"new character has independent marker");
   run(p,"scoreboard players set @s hcq_sqbaob01 101");update(p);check(markers(p).isEmpty()&&markers(players.get(1)).size()==1,"quest completion removes A marker without touching B");
   run(p,"scoreboard players set @s hcq_sqbaob01 0");update(p);check(markers(p).size()==1,"abandoned/reset quest can be offered again");
   call(p,"method_5814",80.5d,64d,2.5d);update(p);check(markers(p).isEmpty(),"markers outside 24 blocks are removed");call(p,"method_5814",0.5d,64d,2.5d);update(p);
  }
  if(ticks==35){
   privateMatrix();Object p=players.get(0),old=markers(p).get(0);cleanup(List.of(players.get(1),players.get(2)));
   check(Boolean.TRUE.equals(call(old,"method_31481"))&&markers(players.get(1)).size()==1,"disconnect cleanup removes only disconnected owner marker");
   net.tompsen.nexuscharacters.NexusCharacters.selected=false;trackers();
   check(!Pq01MultiWorldBench.live.get(players.get(1)).contains(id(firstB)),"no selected character cannot see private marker");
   net.tompsen.nexuscharacters.NexusCharacters.selected=true;
   cleanup(List.of());check(markers(null).isEmpty(),"orphan cleanup leaves no markers");
   check(pos(npc,world).distance2(new Pq01Motion.Point(dimension(world),2.5,64,2.5))<0.001,"marker commands never move the NPC");
   for(Object observer:players)for(int marker:markerIds)if(Pq01MultiWorldBench.spawns.get(observer).contains(marker)){
    // Tout marqueur reçu est contrôlé dans la matrice native ; les identifiants du
    // cycle initial restent attribués sans ambiguïté au joueur correspondant.
    if(marker==id(firstA))check(observer==players.get(0),"no foreign initial A spawn packet");
    if(marker==id(firstB))check(observer==players.get(1),"no foreign initial B spawn packet");
   }
   finished=true;Files.writeString(Path.of("world-test-result.txt"),"PASS "+checks+" QMARK1 checks\n"+String.join("\n",evidence)+"\nNative Fabric 1.21.11 functions/predicates, EasyNPC 7.16.0 name selectors, real QuestVisibility and original EntityTrackerMixin, native spawn/destroy tracking. Nexus identities, chunk receipt and connections synthetic; no graphical rendering or FPS measurement.\n");Pq01WorldBench.command("stop");
  }
 }
 static void fail(Throwable t){if(finished)return;finished=true;t.printStackTrace();try{java.io.StringWriter s=new java.io.StringWriter();t.printStackTrace(new java.io.PrintWriter(s));Files.writeString(Path.of("world-test-result.txt"),"FAIL "+s+"\n"+String.join("\n",evidence));if(server!=null)Pq01WorldBench.command("stop");}catch(Throwable ignored){}}
}
