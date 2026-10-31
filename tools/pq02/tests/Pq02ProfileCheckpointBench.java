package net.hautecapitale.quetes.sequence;

import java.nio.file.*;
import java.util.*;
import net.tompsen.nexuscharacters.*;
import net.minecraft.class_3222;
import static net.hautecapitale.quetes.sequence.Pq01Bridge.*;
import static net.hautecapitale.quetes.sequence.SequenceBridge.*;
import static net.hautecapitale.quetes.sequence.Pq02Runtime.*;
import static net.hautecapitale.quetes.sequence.Pq02WorldBench.*;

/** Real OPT3 ProfileStateBridge.save + real supplied before_save_self + native b10 sessions. */
final class Pq02ProfileCheckpointBench {
 static CharacterDto dto(Object p){return new CharacterDto(character(p),"PQ02 snapshot","","","",2,false,"human",1f,1f,"none","none");}
 static void snapshot(Object p){NexusCharacters.periodicSave((class_3222)p,dto(p));}
 static void check(boolean value,String label){ok(value,"Nexus checkpoint: "+label);}
 static void runAll(boolean legacy)throws Exception{
  seed(a,30);seed(b,30);run(a,"function capitale:pq02/load");
  Pq02SetupBench.run();open(a,"leovic");settle(a);View v=v(a);Object session=v.session,entity=v.actors.get("leovic").entity;UUID actor=v.actors.get("leovic").id;
  // Arthur is OP. Nexus invokes the function with the player's actual command permissions.
  call(call(srv,"method_3760"),"method_14582",make("net.minecraft.class_11560",call(a,"method_7334")));
  check(v.ui&&v.node.key.equals("a01")&&current(a)==session,"first page a01 active before real Nexus save");
  snapshot(a);
  Path snapshot=Path.of("world/nexuscharacters/profile_state/"+character(a)+".bin");
  check(Files.exists(snapshot)&&Files.size(snapshot)>0,"actual Nexus profile snapshot written to disk");
  if(legacy){
   check(v(a)==null&&current(a)==null,"HF5 periodic save destroys the private actor and closes the dialogue");
   check(Boolean.TRUE.equals(call(entity,"method_31481")),"HF5 actor removed by before_save suspension");
   return;
  }
  check(v(a)==v&&v.ui&&v.session==session&&current(a)==session,"periodic save retains identical view and native session");
  check(score(a,"CAP_PROFILE_LOCK")==0,"periodic checkpoint restores the previous transient profile lock");
  check(v.actors.get("leovic").id.equals(actor),"private actor UUID retained");
  for(int i=0;i<5;i++){snapshot(a);production();check(v(a)==v&&v.session==session&&v.ui,"repeated periodic save "+i+" retains dialogue");}
  check(stage(a)==30&&score(a,"CAP_PQ2_DA")==0&&score(a,"CAP_LEOVIC_GIFT")==0,"checkpoint never advances decisions, quest or money");
  open(b,"leovic");settle(b);View other=v(b);Object otherSession=other.session;snapshot(a);
  check(v(b)==other&&other.session==otherSession&&current(b)==otherSession,"another player's scene remains independent");
  for(Pq02Data.Node node:Pq02Data.NODES.values()){
   Pq02CaptureBench.ready(a,node);View page=v(a);Object pageSession=page.session;UUID pageActor=page.actors.get("leovic").id;int choiceToken=page.baseToken;
   snapshot(a);production();
   check(v(a)==page&&page.ui&&current(a)==pageSession&&page.session==pageSession&&page.baseToken==choiceToken&&page.actors.get("leovic").id.equals(pageActor),"real Nexus snapshot preserves session, camera actor and choices at "+node.key);
  }
  Pq02CaptureBench.ready(a,Pq02Data.get("t_menu"));v=v(a);closeUi(v);resume(v);Object motion=v.motion;int waypoint=v.waypoint;
  snapshot(a);
  check(v(a)==v&&v.walking&&!v.ui&&v.motion==motion&&v.waypoint==waypoint&&score(a,"CAP_PQ2_PAUSE")==0,"periodic save keeps the escort walking without restarting navigation");
  pause(v);open(a,"leovic");settle(a);
  // Direct final save must keep its original destructive transition behavior.
  ProfileStateBridge.save((class_3222)a,dto(a));
  check(v(a)==null&&current(a)==null,"real final profile save still suspends and closes PQ02");score2(a,"CAP_PROFILE_LOCK",0);
  check(v(b)==other&&current(b)==otherSession,"final save only closes its owner");
  seed(a,30);score2(a,"CAP_PQ2_NODE",0);Pq02EntryBench.click(a,leo);check(Pq02Entries.PENDING.containsKey(uuid(a)),"first click queued before periodic save");
  snapshot(a);check(Pq02Entries.PENDING.containsKey(uuid(a))&&score(a,"CAP_PROFILE_LOCK")==0,"periodic save retains a first click queued before actor creation");production();settle(a);
  check(v(a)!=null&&v(a).ui&&v(a).node.key.equals("a01"),"queued first click still opens the first page after the checkpoint");
  suspend(a);
  closeUi(other);dispose(uuid(b));score2(b,"CAP_PROFILE_LOCK",0);
 }
}
