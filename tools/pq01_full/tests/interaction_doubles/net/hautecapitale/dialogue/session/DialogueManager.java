package net.hautecapitale.dialogue.session;
import java.util.*;
import net.hautecapitale.quetes.sequence.Pq01InteractionBench;
/** Doublure d'effets caméra, uniquement dans le classpath du banc. */
public final class DialogueManager {
  public static Object session;
  public static boolean blocked,failOpen,returnNull;
  public static int opened;
  public static Optional<String> refus(Object player,Object npc){return blocked?Optional.of("PNJ occupé"):Optional.empty();}
  public static boolean enSession(Object player){return session!=null;}
  public static Optional<Object> session(Object player){return Optional.ofNullable(session);}
  public static boolean pnjOccupe(UUID npc,UUID player){return blocked;}
  public static void fermer(Object player,Object reason){Pq01InteractionBench.events.add("camera-close:"+reason);Object old=session;session=null;if(old!=null)closed.accept(player,old);}
  public static java.util.function.BiConsumer<Object,Object> closed=(p,s)->{};
  public static void dire(Object player,Object text){Pq01InteractionBench.events.add("refus:"+text);}
  public static Object ouvrir(Object player,Object npc,Object mode){
    if(failOpen)throw new IllegalStateException("camera failure");
    if(returnNull){session=null;return null;}
    Pq01InteractionBench.events.add("camera:"+mode);opened++;return session=new Object();
  }
}
