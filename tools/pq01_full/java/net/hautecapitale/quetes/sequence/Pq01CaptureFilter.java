package net.hautecapitale.quetes.sequence;
import java.util.*;
import static net.hautecapitale.quetes.sequence.SequenceBridge.call;
/** Filtre sémantique des notifications de connexion ; laisse le chat du jeu intact. */
public final class Pq01CaptureFilter {
  private static final Set<String> KEYS=Set.of("multiplayer.player.joined","multiplayer.player.joined.renamed","multiplayer.player.left");
  private Pq01CaptureFilter(){}
  public static boolean ignore(Object text){return ignore(text,0);}
  private static boolean ignore(Object text,int depth){
    if(text==null||depth>32)return false;
    Object content=call(text,"method_10851");
    if(content!=null&&content.getClass().getName().equals("net.minecraft.class_2588")&&KEYS.contains(call(content,"method_11022")))return true;
    for(Object child:(List<?>)call(text,"method_10855"))if(ignore(child,depth+1))return true;
    return false;
  }
}
