package net.hautecapitale.quetes.sequence;

import java.util.*;
import net.hautecapitale.quetes.sequence.Pq01Motion.Point;
import static net.hautecapitale.quetes.sequence.SequenceBridge.*;
import static net.hautecapitale.quetes.sequence.Pq01Bridge.*;
import static net.hautecapitale.quetes.sequence.Pq02Runtime.*;
import static net.hautecapitale.quetes.sequence.Pq02WorldBench.*;

/** Commandes administratives exercées sur les vraies entités et le dispatcher natif. */
final class Pq02SetupBench {
 static void cmd(String command){call(call(call(srv,"method_3734"),"method_9235"),"execute",command,call(srv,"method_3739"));}
 static void check(boolean value,String description){ok(value,"setup HF1: "+description);}
 static void forget(String key){Object root=Pq01Nbt.copy(config(a));call(child(root,"points"),"method_10551",key);GuardNbt.save(srv,SETUP,root);}
 static void function(String name){SequenceBridge.run(a,"function capitale:pq02/admin/"+name);}
 static long debugCount(){long count=0;for(Object e:((net.minecraft.class_3218)w).method_27909())if(tags(e).contains("debug_leovic_registre_port"))count++;return count;}
 static void run(){
  Point playerBefore=pos(a,w),publicBefore=pos(leo,w);Object pointA=marker(a,"a"),pointD=marker(a,"d"),pointHBefore=marker(a,"h_before"),pointHAfter=marker(a,"h_after");Point originalA=pos(pointA,w),originalD=pos(pointD,w);
  cmd("summon minecraft:armor_stand 13 64 4 {Tags:[\"guide_leovic_registre_port\",\"escort_active\",\"escort_moving\"],NoGravity:1b,Invisible:1b,Marker:1b}");
  Object guide=Pq02Setup.scan(a).guides().get(0);
  cmd("summon minecraft:armor_stand 8 64 9 {Tags:[\"wp_leovic_registre_port_a\",\"fixture_fake_marker\"],NoGravity:1b,Marker:1b}");
  Object root=Pq01Nbt.copy(config(a));GuardNbt.string(root,"fixture_preserved","yes");GuardNbt.save(srv,SETUP,root);
  // Outside both previous bounds: 256 horizontal blocks and 32 vertical blocks.
  call(a,"method_5814",800.5d,140d,800.5d);
  check(marker(a,"a")==null,"fixture is outside former local marker lookup");
  check(execute2(a,"prepare_route")==1,"preparation succeeds far from loaded A and at a different altitude");
  check(pos(guide,w).equals(originalA),"legacy guide parks on existing A, never on administrator position");
  check(!tags(guide).contains("escort_active")&&!tags(guide).contains("escort_moving"),"legacy motion states removed");
  check(pos(pointA,w).equals(originalA)&&pos(pointD,w).equals(originalD),"existing markers remain in place");
  check(pos(leo,w).equals(publicBefore),"public Leovic position unchanged by setup");
  check(Pq02Setup.scan(a).markers().get("a").size()==1,"armor stand carrying A tag is ignored as a route marker");
  check(str(config(a),"fixture_preserved","").equals("yes"),"unrelated setup data retained");
  check(Pq02Setup.cached(a,"d").equals(originalD),"loaded coordinates copied exactly to cache");
  int count=messages.get(a).size();execute2(a,"points");
  List<String> feedback=messages.get(a).subList(count,messages.get(a).size());
  check(feedback.stream().anyMatch(s->s.contains("D — pause lore 1")&&s.contains("chargé"))&&feedback.stream().anyMatch(s->s.contains("M — magistrat"))&&feedback.stream().anyMatch(s->s.contains("P — ascenseur")),"point diagnostics expose roles, coordinates and loaded status");
  Object dispatcher=call(call(call(srv,"method_3734"),"method_9235"),"getRoot"),command=call(dispatcher,"getChild","indivispq02");
  for(String action:List.of("prepare_route","prepare_legacy","points","status")){
   Object node=call(command,"getChild",action);check(node!=null&&Boolean.FALSE.equals(call(node,"canUse",call(a,"method_64396")))&&Boolean.TRUE.equals(call(node,"canUse",call(srv,"method_3739"))),"operator requirement enforced for "+action);
  }
  // Loaded positions win over cached values, without being moved by preparation.
  Point moved=new Point(originalD.dimension(),originalD.x()+0.4,originalD.y(),originalD.z()+0.25);call(pointD,"method_5814",moved.x(),moved.y(),moved.z());
  check(execute2(a,"prepare_route")==1&&Pq02Setup.cached(a,"d").equals(moved)&&pos(pointD,w).equals(moved),"preparation adapts to changed existing marker coordinates");
  call(pointD,"method_5738","wp_leovic_registre_port_d");
  check(execute2(a,"prepare_route")==1&&Pq02Setup.cached(a,"d").equals(moved),"cached point remains usable when its marker is not loaded/recognized");
  count=messages.get(a).size();execute2(a,"points");check(messages.get(a).subList(count,messages.get(a).size()).stream().anyMatch(s->s.contains("D — pause lore 1")&&s.contains("mémorisé, marker non chargé")),"diagnostic distinguishes remembered from loaded points");
  forget("d");call(guide,"method_5814",30d,64d,4d);Point guideBefore=pos(guide,w);
  check(execute2(a,"prepare_route")==0&&pos(guide,w).equals(guideBefore),"missing required point refuses completion and leaves guide untouched");
  check(Pq02Setup.cached(a,"d")==null,"missing point never gets invented fallback coordinates");
  root=Pq01Nbt.copy(config(a));GuardNbt.put(GuardNbt.childOrCreate(root,"points"),"d",pointNbt(new Point("minecraft:the_nether",originalD.x(),originalD.y(),originalD.z())));GuardNbt.save(srv,SETUP,root);
  check(execute2(a,"prepare_route")==0&&Pq02Setup.cached(a,"d")==null,"cache from another dimension is rejected");
  call(pointD,"method_5780","wp_leovic_registre_port_d");call(pointD,"method_5814",originalD.x(),originalD.y(),originalD.z());check(execute2(a,"prepare_route")==1,"restored marker repairs preparation without changing progression");
  // Real duplicate markers must never pick an arbitrary destination or overwrite cache.
  cmd("summon minecraft:marker 10 64 12 {Tags:[\"wp_leovic_registre_port_d\",\"fixture_duplicate\"]}");
  Point cachedBefore=Pq02Setup.cached(a,"d");call(guide,"method_5814",30d,64d,4d);guideBefore=pos(guide,w);
  check(execute2(a,"prepare_route")==0&&pos(guide,w).equals(guideBefore)&&Pq02Setup.cached(a,"d").equals(cachedBefore),"duplicate marker refuses both guide move and cache mutation");
  cmd("kill @e[type=minecraft:marker,tag=fixture_duplicate]");
  cmd("summon minecraft:armor_stand 9 64 12 {Tags:[\"guide_leovic_registre_port\",\"fixture_duplicate_guide\"],NoGravity:1b,Marker:1b}");
  check(execute2(a,"prepare_route")==0&&pos(guide,w).equals(guideBefore),"duplicate legacy guide is not chosen arbitrarily");cmd("kill @e[type=minecraft:armor_stand,tag=fixture_duplicate_guide]");
  call(pointHBefore,"method_5738","wp_leovic_registre_port_h_before");call(pointHAfter,"method_5738","wp_leovic_registre_port_h_after");forget("h_before");forget("h_after");
  check(execute2(a,"prepare_route")==1,"optional H approach markers do not prevent preparation");
  call(pointHBefore,"method_5780","wp_leovic_registre_port_h_before");call(pointHAfter,"method_5780","wp_leovic_registre_port_h_after");
  call(pointA,"method_5738","wp_leovic_registre_port_a");call(guide,"method_5814",30d,64d,4d);guideBefore=pos(guide,w);
  check(execute2(a,"prepare_route")==0&&pos(guide,w).equals(guideBefore),"cached but unloaded A defers legacy parking without teleport to unloaded marker");call(pointA,"method_5780","wp_leovic_registre_port_a");
  VIEWS.put(uuid(a),new View(a));try{check(execute2(a,"prepare_route")==0&&pos(guide,w).equals(guideBefore),"active scene prevents legacy guide relocation");}finally{dispose(uuid(a));}
  check(execute2(a,"prepare_legacy")==1&&pos(guide,w).equals(originalA),"old direct command remains compatible");
  for(String name:List.of("preparer_parcours","preparer_guide_historique","diagnostic","diagnostic_points","afficher_points","masquer_points"))check(CoreQuestMarkers.functionPresent(a,"capitale:pq02/admin/"+name),"datapack function exists: "+name);
  function("preparer_guide_historique");check(pos(guide,w).equals(originalA),"old function forwards through actual function dispatcher");
  function("diagnostic");function("diagnostic_points");function("afficher_points");check(debugCount()==ROUTE.size(),"visualization uses existing historical waypoint function");
  function("masquer_points");check(debugCount()==0&&Pq02Setup.scan(a).markers().values().stream().allMatch(list->list.size()==1),"hiding visualization preserves all real route markers");
  check(stage(a)==30&&score(a,"REP_GARDEPORT")==0&&score(a,"CAP_LEOVIC_GIFT")==0&&score(a,"CAP_RANGSOCIAL")==0&&VIEWS.isEmpty(),"admin commands do not start quest, alter reputation, give money or change character state");
  cmd("kill @e[type=minecraft:armor_stand,tag=fixture_fake_marker]");call(guide,"method_31472");call(a,"method_5814",playerBefore.x(),playerBefore.y(),playerBefore.z());
 }
}
