package net.hautecapitale.quetes.sequence;
import java.util.*;
/** Règles pures ; valeurs canoniques hcq_pq02, décisions et sécurité des étapes. */
public final class Pq02Rules {
 public static final Map<String,String> DECISIONS=Map.of("arrival","CAP_PQ2_DA","guard","CAP_PQ2_DG","magistrate","CAP_PQ2_DM","gift","CAP_PQ2_DD");
 public record Delta(int reputation,int impression){}
 public static Delta delta(String decision,int value){
  int max=decision.equals("gift")?4:3;
  if(!DECISIONS.containsKey(decision)||value<1||value>max)throw new IllegalArgumentException("Décision PQ02 invalide");
  if(decision.equals("gift"))return value==3?new Delta(1,1):value==4?new Delta(-1,-1):new Delta(0,0);
  return value==1?new Delta(decision.equals("magistrate")?0:1,1):new Delta(0,0);
 }
 public static String greeting(int total){return total>=3?"post_very_friendly":total>=1?"post_friendly":total<0?"post_unfriendly":"post_neutral";}
 public static boolean introduction(int stage){return stage>=0&&stage<=30;}
 public static boolean walking(int stage){return stage==40||stage==70;}
 public static boolean validNode(String key,int stage){
  if(key.startsWith("post_"))return stage>=100;
  if(key.startsWith("a"))return introduction(stage);
  if(key.startsWith("t07"))return stage==50;
  if(key.startsWith("t"))return stage==40||stage==70;
  if(key.startsWith("m06_registered")||key.matches("m06[abc]")||key.startsWith("m07"))return stage==60;
  if(key.startsWith("m"))return stage==50;
  if(key.startsWith("f"))return stage==90||stage>=100&&Set.of("f02","f03","f03_prefer","f04","f04_return","f05","f05_short").contains(key);
  return false;
 }
 public static String initial(int stage){return introduction(stage)?"a01":stage==40||stage==70?"t_menu":stage==50?"t07":stage==60?"m07":stage==90?"f_menu":"post_neutral";}
 public static int legacyToCanonical(int legacy){return switch(legacy){case 0->0;case 1->10;case 2->20;case 10->30;case 20->40;case 30->50;case 40->60;case 50->70;case 60->90;case 100->100;default->throw new IllegalArgumentException();};}
}
