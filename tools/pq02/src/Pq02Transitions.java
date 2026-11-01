package net.hautecapitale.quetes.sequence;

/** Interface actions do not add NPC lines or protagonist replies to the validated script. */
public final class Pq02Transitions {
 private Pq02Transitions(){}
 public static String label(String next){
  if(!next.startsWith("event:"))return "";
  return switch(next.substring(6)){
   case "start_walk"->"Suivre Léovic";
   case "resume_walk"->"Reprendre la route";
   case "elevator"->"Rejoindre l'ascenseur avec Léovic";
   case "rest"->"Prendre un moment de repos";
   case "register"->"Confirmer l'inscription";
   case "practical_menu"->"Revenir aux questions";
   case "gift"->"Accepter la bourse";
   case "complete"->"Terminer l'entretien";
   default->throw new IllegalArgumentException("Action PQ02 sans libellé : "+next);
  };
 }
 public static boolean playerClosed(String reason){return reason.equals("ESC")||reason.equals("ADIEU")||reason.equals("ECRAN_FERME");}
 static boolean movementChoice(String target){Pq02Data.Node node=Pq02Data.NODES.get(target);return node!=null&&(node.next.equals("event:start_walk")||node.next.equals("event:resume_walk")||node.next.equals("event:elevator"));}
 /** Departure was already agreed to in the preceding protagonist choice. */
 public static String agreedExit(String next,int stage){
  return switch(next){
   case "event:start_walk"->Pq02Rules.introduction(stage)?"start_walk":"";
   case "event:resume_walk"->Pq02Rules.walking(stage)?"resume_walk":"";
   case "event:elevator"->stage==60?"elevator":"";
   case "event:rest"->Pq02Rules.introduction(stage)?"rest":"";
   default->"";
  };
 }
}
