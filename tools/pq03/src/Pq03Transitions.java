package net.hautecapitale.quetes.sequence;
import java.util.Set;
public final class Pq03Transitions {
 public static boolean playerClosed(String reason){return Pq02Transitions.playerClosed(reason);}
 public static String label(String next){if(!next.startsWith("event:"))return "";return switch(next.substring(6)){case "market"->"Suivre Aurèle vers les commerces";case "depth"->"Suivre Aurèle vers les Profondeurs";case "return"->"Laisser Aurèle rejoindre son poste";case "complete"->"Terminer l'entretien";case "resume"->"Reprendre la route";case "questions"->"Revenir aux questions";case "context"->"Revenir au trajet";case "later"->"Prendre son temps";default->throw new IllegalArgumentException(next);};}
 static boolean movementChoice(String target){Pq03Data.Node n=Pq03Data.NODES.get(target);return n!=null&&Set.of("event:market","event:depth","event:return").contains(n.next);}
 static String agreedExit(String next,int stage){return switch(next){case "event:market"->Pq03Rules.introduction(stage)?"market":"";case "event:depth"->Pq03Rules.introduction(stage)||stage==50?"depth":"";case "event:return"->stage==30?"return":"";case "event:complete"->stage==90?"complete":"";default->"";};}
}
