package net.hautecapitale.quetes.sequence;
import java.util.*;
/** Valeurs canoniques V2, distinctes des suffixes vXX. */
public final class Pq03Rules {
 public static final List<String> SCORES=List.of("CAP_PQ3_VER","CAP_PQ3_NODE","CAP_PQ3_DA","CAP_PQ3_DB","CAP_PQ3_IMPR","CAP_PQ3_WP","CAP_PQ3_PAUSE","CAP_PQ3_REW","CAP_PQ3_END","CAP_PQ3_RECALL","CAP_PQ3_RUMBLE","CAP_PQ3_BRANCH");
 public static boolean introduction(int stage){return stage==0||stage==10||stage==20;}
 public static boolean walking(int stage){return stage==30||stage==40||stage==60;}
 public static List<Integer> route(int stage){if(stage==30)return List.of(0,1,2,3,4,5,6,7,8);if(stage==40)return List.of(8,7,6,5,4,3,2,1,0);return List.of(0,9,10,11,12,13,14,15,16,17,18,19,20,21);}
 public static List<Integer> retreat(){return List.of(21,20,19,18,17,16,15,14,13,12,11,10,9,0);}
 public static boolean validNode(String key,int stage){
  if(key.startsWith("post"))return stage>=100;
  if(key.equals("b_walk")||key.equals("c_recall"))return false;
  if(key.startsWith("a_")||key.equals("a01"))return introduction(stage)||key.equals("a_watch")||key.equals("a_service");
  if(key.startsWith("b_market"))return stage==30;
  if(key.startsWith("b_reunion"))return stage==50;
  if(Set.of("c_district","c_different","c_judge","c_approach","c_trouble").contains(key))return stage==60;
  if(key.startsWith("c"))return stage==90;
  return key.startsWith("b_")||key.equals("t_menu");
 }
 public static String initial(int stage,int point){return introduction(stage)?"a01":stage==50?"b_reunion":stage==90?"c03":stage>=100?"post":stage==30&&point==8?"b_market":"t_menu";}
 public static int result(int discipline,int separation){if(discipline<0||discipline>3||separation<1||separation>4)throw new IllegalArgumentException("Décisions PQ03 invalides");return (discipline==1?1:0)+(separation==1?1:separation==4?-1:0);}
 public static String greeting(int impression){return impression>0?"post":impression<0?"post_unfriendly":"post_neutral";}
}
