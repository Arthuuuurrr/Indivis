package net.tompsen.nexuscharacters;
import java.util.*;
/** TEST ONLY: selected character, no Nexus persistence substituted or modified in the delivery. */
public final class NexusCharacters {
 public record Character(UUID id){}
 private static final Map<Object,Character> selected=new IdentityHashMap<>();
 public static void select(Object player,UUID id){selected.put(player,new Character(id));}
 public static Character getSelectedCharacter(Object player){return selected.get(player);}
}
