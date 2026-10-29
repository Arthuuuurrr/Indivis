package net.hautecapitale.quetes.sequence;
import java.util.*;
import static net.hautecapitale.quetes.sequence.Pq01Bridge.*;
import static net.hautecapitale.quetes.sequence.SequenceBridge.*;
import static net.hautecapitale.quetes.sequence.Pq02Runtime.*;
import static net.hautecapitale.quetes.sequence.Pq02WorldBench.*;

/** Server-side readiness gate exercised through the real player command dispatcher. */
final class Pq02FocusBench {
 static void check(boolean value,String message){ok(value,"focus HF4: "+message);}
 static void verify(){
  autoFocusAck=false;
  try{
   open(a,"leovic");production();View v=v(a);
   check(v.session!=null&&v.pendingNode!=null&&v.baseToken==0,"no page or choice token before client readiness");
   int token=v.focusToken;check(acknowledgeFocus(a,token+1)==0,"wrong readiness token rejected");
   check(acknowledgeFocus(b,token)==0,"another player cannot acknowledge this focus");
   score2(a,"CAP_PROFILE_LOCK",1);check(acknowledgeFocus(a,token)==0,"profile lock rejects readiness");score2(a,"CAP_PROFILE_LOCK",0);
   UUID character=character(a);net.tompsen.nexuscharacters.NexusCharacters.select(a,UUID.randomUUID());check(acknowledgeFocus(a,token)==0,"character switch rejects old readiness");net.tompsen.nexuscharacters.NexusCharacters.select(a,character);
   Object dispatcher=call(call(srv,"method_3734"),"method_9235"),playerSource=call(a,"method_64396");
   call(dispatcher,"execute","indivispq02 ready "+token,playerSource);check(focusReady(v),"non-OP readiness command acknowledges the native session");production();
   check(v.pendingNode==null&&v.baseToken!=0&&v.node.key.equals("a01"),"first page released after acknowledgement");
   check(acknowledgeFocus(a,token)==0,"duplicate acknowledgement after render is harmless");
   closeUi(v);dispose(uuid(a));score2(a,"CAP_PQ2_NODE",0);open(a,"leovic");production();v=v(a);
   check(v.focusToken!=token&&!focusReady(v),"reopened conversation requires its own acknowledgement");
   v.focusSince=clock-201;production();check(!v.ui&&v.session==null,"missing acknowledgement times out without advancing quest");
   check(stage(a)==30&&score(a,"CAP_PQ2_DA")==0&&score(a,"CAP_LEOVIC_GIFT")==0,"timeout preserves narrative decisions and rewards");dispose(uuid(a));score2(a,"CAP_PQ2_NODE",0);
   check(Pq02Focus.parse("indivis:pq02:focus:v1:-1:not-a-uuid:0")==null,"malformed client readiness envelope rejected");
  }finally{autoFocusAck=true;}
 }
}
