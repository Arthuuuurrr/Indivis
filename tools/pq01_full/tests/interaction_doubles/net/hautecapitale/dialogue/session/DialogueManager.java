package net.hautecapitale.dialogue.session;
import java.util.*;
import net.hautecapitale.quetes.sequence.Pq01InteractionBench;
/** Doublure d'effets caméra, uniquement dans le classpath du banc. */
public final class DialogueManager {
  public static Object session;
  public static boolean blocked,failOpen;
  public static int opened;
  public static Optional<String> refus(Object player,Object npc){return blocked?Optional.of("PNJ occupé"):Optional.empty();}
  public static boolean enSession(Object player){return session!=null;}
  public static void dire(Object player,Object text){Pq01InteractionBench.events.add("refus:"+text);}
  public static Object ouvrir(Object player,Object npc,Object mode){
    if(failOpen)throw new IllegalStateException("camera failure");
    Pq01InteractionBench.events.add("camera:"+mode);opened++;return session=new Object();
  }
}
