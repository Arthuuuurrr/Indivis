import net.tompsen.nexuscharacters.V2QuestPolicy;
public final class CheckProfilePolicy {
  public static void main(String[] args){
    int checks=0;for(String score:new String[]{"hcm_adm_savelock","hcm_adm_haslock"}){
      if(score.length()>16||!V2QuestPolicy.transientState(score)||V2QuestPolicy.extra(score))throw new AssertionError(score);checks+=3;
    }
    System.out.println("PROFILE_POLICY PASS: "+checks+" assertions on actual Nexus V2QuestPolicy; two transient objectives.");
  }
}
