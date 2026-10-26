package net.hautecapitale.quetes.sequence;
import net.fabricmc.api.ModInitializer;
import java.util.*;
import java.nio.file.*;
import static net.hautecapitale.quetes.sequence.SequenceBridge.*;
import static net.hautecapitale.quetes.sequence.Pq01Bridge.*;

/** Exécute les vraies fonctions sur les anciennes actions de la capture utilisateur. */
public final class Pq01QuaiCommandsBench implements ModInitializer {
 static Object server,world,player;static int ticks,checks;static boolean done;
 static final List<String> evidence=new ArrayList<>();
 static final String[] ROLES={"geraud","altheon","gautier","hugues","anselme","perrin"};
 static final Map<String,Object> NPCS=new LinkedHashMap<>();
 public void onInitialize(){
  Pq01Bridge.init();
  event("net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback","EVENT",a->{Pq01WorldBench.stub(a[0],"hcd_interpellation");Pq01WorldBench.stub(a[0],"indivissequence");return null;});
  event("net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents$ServerStarted","SERVER_STARTED",a->{try{start(a[0]);}catch(Throwable t){fail(t);}return null;});
  event("net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents$EndTick","END_SERVER_TICK",a->{try{tick();}catch(Throwable t){fail(t);}return null;});
 }
 static void check(boolean b,String text){checks++;if(!b)throw new AssertionError(text);evidence.add(text);}
 static Object row(String kind,String cmd){Object n=GuardNbt.compound();GuardNbt.string(n,"Type",kind);GuardNbt.string(n,"Cmd",cmd);return n;}
 static Object rows(Object a,Object b){return GuardNbt.list(List.of(a,b));}
 static void start(Object s)throws Exception{
  server=s;CoreQuestMarkersBench.start(s);world=CoreQuestMarkersBench.world;player=CoreQuestMarkersBench.player;
  for(int i=0;i<ROLES.length;i++)NPCS.put(ROLES[i],Pq01WorldBench.npc(0.5+3*i,8.5));
 }
 static void setup(){run(player,"function capitale:pq01/load");CoreQuestMarkersBench.setScores(player,CoreQuestMarkersBench.base);run(player,"data modify storage capitale:pq01_setup ambient_enabled set value 0");}
 static String legacy(String role,String phase){
  String base="function capitale:quest/spawn_dirigeable_couronne/";
  if(role.equals("geraud"))return phase.equals("large")?base+"geraud_large_start_self":base+"geraud_quai_interact_self";
  if(role.equals("altheon"))return base+"capitaine_"+phase+"/interact_self";
  int n=List.of("gautier","hugues","anselme","perrin").indexOf(role)+1;
  // Les quatre copies au quai peuvent avoir gardé la commande de l'exemplaire au large.
  return base+"matelots/matelot_0"+n+"_self";
 }
 static Object actual(Object npc){return child(eventSet(GuardNbt.entity(npc)),"ON_INTERACTION");}
 static void bindings(String round){
  for(String phase:List.of("large","quai"))for(String role:ROLES){
   Object npc=NPCS.get(role);Pq01Motion.Point at=pos(npc,world);String key=role+"_"+phase;
   call(player,"method_5814",at.x()+0.3,at.y(),at.z()+0.8);
   Object original=rows(row("COMMAND",legacy(role,phase)),row("COMMAND","particle minecraft:happy_villager ~ ~1 ~ 0 0 0 0 1"));
   Pq01Actions.mutate(npc,"ON_INTERACTION",original);original=GuardNbt.copy(actual(npc));
   run(player,"function capitale:pq01/liaison/"+key);
   Object b=binding(player,key);check(b!=null&&uuid(npc).toString().equals(str(b,"uuid","")),round+" legacy function bound "+key);
   Object expected=Pq01Actions.plan(original,key,"interact");
   check(Pq01Actions.equivalent(actual(npc),expected),round+" migrated only role action, kept unrelated action "+key);
   run(player,"function capitale:pq01/pose/placer_"+key);
   check(pos(npc,world).distance2(pos(player,world))<0.001,round+" function placed "+key);
   run(player,"function capitale:pq01/liaison/liberer_"+key);check(binding(player,key)==null,round+" function unbound "+key);
   check(Pq01Actions.equivalent(actual(npc),original),round+" exact original actions restored "+key);
   run(player,"indivispq01 bind_"+key);check(binding(player,key)!=null,round+" native bind "+key);
   run(player,"indivispq01 unbind_"+key);check(binding(player,key)==null,round+" native unbind "+key);
  }
 }
 static void protections(){
  Object npc=NPCS.get("anselme");Pq01Motion.Point at=pos(npc,world);call(player,"method_5814",at.x()+0.2,at.y(),at.z()+0.4);
  Object wrong=GuardNbt.list(List.of(row("COMMAND","function capitale:quest/spawn_dirigeable_couronne/matelots/matelot_03_quai_self")));
  Pq01Actions.mutate(npc,"ON_INTERACTION",wrong);Object before=GuardNbt.entity(npc);
  run(player,"indivispq01 bind_altheon_quai");
  check(binding(player,"altheon_quai")==null&&Objects.equals(before,GuardNbt.entity(npc)),"wrong role from screenshot stays rejected with no write");
  run(player,"indivispq01 bind_anselme_quai");check(binding(player,"anselme_quai")!=null,"same NPC accepted with correct Anselme role");run(player,"indivispq01 unbind_anselme_quai");
  Object two=rows(row("COMMAND",legacy("anselme","large")),row("COMMAND","function capitale:quest/spawn_dirigeable_couronne/matelots/matelot_03_quai_self"));
  Pq01Actions.mutate(npc,"ON_INTERACTION",two);before=GuardNbt.entity(npc);run(player,"indivispq01 bind_anselme_quai");
  check(binding(player,"anselme_quai")==null&&Objects.equals(before,GuardNbt.entity(npc)),"two legacy triggers stay rejected with no write");
  Pq01Actions.mutate(npc,"ON_INTERACTION",GuardNbt.list(List.of(row("CUSTOM",Pq01Actions.command("anselme_large","interact")))));
  run(player,"indivispq01 bind_anselme_quai");check(binding(player,"anselme_quai")!=null,"copied custom same-role action migrates to quay");run(player,"indivispq01 unbind_anselme_quai");
 }
 static void tick()throws Exception{
  if(done||server==null)return;ticks++;
  if(ticks==20){setup();bindings("startup");protections();Pq01WorldBench.command("reload");}
  if(ticks==100){bindings("after reload");protections();done=true;Files.writeString(Path.of("world-test-result.txt"),"PASS "+checks+" quay migration checks\n"+String.join("\n",evidence)+"\nActual production initializer/commands/functions at function-permission-level=2, EasyNPC action APIs, legacy captain-quay and sailor non-quay commands. Unrelated actions preserved; wrong roles and duplicate triggers protected. Nexus profiles/connections synthetic. No client rendering tested.\n");Pq01WorldBench.command("stop");}
 }
 static void fail(Throwable t){if(done)return;done=true;t.printStackTrace();try{java.io.StringWriter s=new java.io.StringWriter();t.printStackTrace(new java.io.PrintWriter(s));Files.writeString(Path.of("world-test-result.txt"),"FAIL "+s+"\n"+String.join("\n",evidence));if(server!=null)Pq01WorldBench.command("stop");}catch(Throwable ignored){}}
}
