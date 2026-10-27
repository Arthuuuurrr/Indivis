package net.hautecapitale.quetes.sequence;
import java.util.*;
import static net.hautecapitale.quetes.sequence.SequenceBridge.*;
import static net.hautecapitale.quetes.sequence.Pq02Runtime.score2;
public final class HuguesBench {
 static void runAll(Object p){run(p,"data modify storage fixture:buttons current set value {b1:101,b2:102,b3:103,b4:104,b5:105,b6:106,b7:107,b15:115}");
  for(String key:List.of("hugues_menu","hugues_36","hugues_38","hugues_40","hugues_42","hugues_44")){
   int start=Pq02WorldBench.messages.get(p).size();run(p,"function capitale:pq01/menu/"+key+" with storage fixture:buttons current");List<String> out=Pq02WorldBench.messages.get(p).subList(start,Pq02WorldBench.messages.get(p).size());
   Pq02WorldBench.ok(out.size()==7&&out.stream().noneMatch(s->s.contains("Écouter leur échange")),"Hugues native menu "+key+": 5 questions, farewell and close; no option6");
   score2(p,"hcm_pq1_pick",6);score2(p,"hcm_pq1_node",9876);int count=Pq02WorldBench.messages.get(p).size();run(p,"function capitale:pq01/choice/"+key);Pq02WorldBench.ok(score(p,"hcm_pq1_node")==9876&&count==Pq02WorldBench.messages.get(p).size(),"Hugues residual choice6 neutral in "+key);
  }
 }
}
