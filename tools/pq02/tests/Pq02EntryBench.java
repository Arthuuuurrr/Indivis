package net.hautecapitale.quetes.sequence;

import java.util.*;import java.nio.file.*;
import net.hautecapitale.quetes.sequence.Pq01Motion.Point;
import static net.hautecapitale.quetes.sequence.SequenceBridge.*;
import static net.hautecapitale.quetes.sequence.Pq01Bridge.*;
import static net.hautecapitale.quetes.sequence.Pq02Runtime.*;
import static net.hautecapitale.quetes.sequence.Pq02WorldBench.*;

/** Entre par le gestionnaire de commandes EasyNPC et les fonctions Minecraft imbriquées. */
final class Pq02EntryBench {
 static void click(Object player,Object npc){
  stat(DM,"ouvrir",player,npc,en("net.hautecapitale.dialogue.session.DialogueMode","CAPTURE"));
  command(player,npc);
 }
 static void command(Object player,Object npc){
  String function=npc==mag?"capitale:quest/le_registre_du_port/magistrat/register_self":"capitale:quest/le_registre_du_port/leovic/interact_self";
  stat("de.markusbordihn.easynpc.entity.easynpc.handlers.action.executor.CommandExecutor","executePlayerCommand","function "+function,player,2,false);
 }
 static void reproduce()throws Exception{
  click(a,leo);SequenceBridge.run(a,"function capitale:quest/le_registre_du_port/leovic/interact_self");
  boolean found=ERRORS.stream().anyMatch(s->s.contains("Copie EasyNPC non créée : leovic"));
  if(!found)throw new AssertionError("Expected real EasyNPC function-entry failure was not reproduced; errors="+ERRORS+", view="+v(a));
  finished=true;Files.writeString(Path.of("world-test-result.txt"),"PASS REPRODUCED_HF1_FUNCTION_ENTRY_FAILURE\nNative function entry: capitale:quest/le_registre_du_port/leovic/interact_self -> indivispq02 leovic -> nested summon -> Copie EasyNPC non créée : leovic\nNative Fabric 1.21.11, EasyNPC 7.16.0; synthetic connections/Nexus. No client rendered.\n");cmd("stop");
 }
 static void verify(){
  click(a,leo);click(a,leo);production();settle(a);
  ok(v(a)!=null&&v(a).actors.containsKey("leovic")&&v(a).node!=null&&v(a).node.key.equals("a01"),"entry HF2: native EasyNPC command executor and function open first PQ02 dialogue");
  ok(ERRORS.isEmpty(),"entry HF2: no actor creation exception from nested function entry");
  UUID actor=v(a).actors.get("leovic").id;command(a,leo);command(a,leo);ok(Pq02Entries.PENDING.size()==1,"entry HF2: repeated function requests are coalesced per account");production();settle(a);
  ok(v(a).actors.size()==1&&v(a).actors.get("leovic").id.equals(actor),"entry HF2: repeated function requests retain exactly one private actor");
  closeUi(v(a));dispose(uuid(a));score2(a,"CAP_PQ2_NODE",0);
  SequenceBridge.run(b,"function capitale:pq02/open_leovic");production();settle(b);
  ok(v(b)!=null&&v(b).node.key.equals("a01")&&v(b).actors.containsKey("leovic"),"entry HF2: public datapack function also opens PQ02");
  ok(!v(b).actors.get("leovic").id.equals(actor),"entry HF2: function route uses a separate actor for another character");
  closeUi(v(b));dispose(uuid(b));score2(b,"CAP_PQ2_NODE",0);
  // Une requête ne doit survivre ni à un changement de personnage, ni à un verrou/progression.
  UUID originalCharacter=character(a);click(a,leo);net.tompsen.nexuscharacters.NexusCharacters.select(a,UUID.randomUUID());production();
  ok(v(a)==null&&Pq02Entries.PENDING.isEmpty(),"entry HF2: character switch cancels pending interaction");net.tompsen.nexuscharacters.NexusCharacters.select(a,originalCharacter);
  click(a,leo);score2(a,"CAP_PROFILE_LOCK",1);production();ok(v(a)==null&&Pq02Entries.PENDING.isEmpty(),"entry HF2: profile lock cancels pending interaction");score2(a,"CAP_PROFILE_LOCK",0);
  click(a,leo);score2(a,"hcq_pq02",40);production();ok(v(a)==null&&Pq02Entries.PENDING.isEmpty(),"entry HF2: stage change cancels stale interaction before spawning");score2(a,"hcq_pq02",30);
  click(a,leo);suspend(a);production();ok(v(a)==null&&Pq02Entries.PENDING.isEmpty(),"entry HF2: profile save suspends pending interaction");
  click(a,leo);Pq02Runtime.dispose(uuid(a));production();ok(v(a)==null&&Pq02Entries.PENDING.isEmpty(),"entry HF2: restore/cleanup cancels pending interaction without an actor");
  click(a,leo);players.remove(a);@SuppressWarnings("unchecked") List<Object> online=(List<Object>)call(call(srv,"method_3760"),"method_14571");online.remove(a);production();ok(v(a)==null&&Pq02Entries.PENDING.isEmpty(),"entry HF2: disconnect cancels pending interaction");players.add(a);online.add(a);
  click(a,leo);Object session=current(a);stat(DM,"fermer",a,en("net.hautecapitale.dialogue.session.CloseReason","FERMEE"));production();ok(v(a)==null&&Pq02Entries.PENDING.isEmpty(),"entry HF2: closed source camera does not reopen on deferred execution");
  Point before=pos(a,w),m=routePoint(a,"m");call(a,"method_5814",m.x(),m.y(),m.z());score2(a,"hcq_pq02",50);score2(a,"CAP_PQ2_WP",14);click(a,mag);production();settle(a);
  ok(v(a)!=null&&v(a).node.key.equals("t07")&&v(a).actors.keySet().containsAll(Set.of("leovic","magistrat")),"entry HF2: native magistrate function creates both private actors and three-person focus");
  closeUi(v(a));dispose(uuid(a));score2(a,"hcq_pq02",30);score2(a,"CAP_PQ2_WP",0);score2(a,"CAP_PQ2_NODE",0);call(a,"method_5814",before.x(),before.y(),before.z());
  ok(ERRORS.isEmpty()&&VIEWS.isEmpty()&&Pq02Entries.PENDING.isEmpty(),"entry HF2: entry tests finish without orphan views, requests or runtime errors");flush();
 }
}
