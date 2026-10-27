package net.tompsen.nexuscharacters;
import java.util.UUID;
import java.util.IdentityHashMap;
import java.util.Map;
/** Doublure de contexte Nexus, uniquement dans le classpath du banc. */
public final class NexusCharacters {
  public static boolean selected=true;
  public record Character(UUID id){}
  private static final Character CHARACTER=new Character(UUID.randomUUID());
  private static final Map<Object,Character> CHARACTERS=new IdentityHashMap<>();
  public static void select(Object player,UUID id){CHARACTERS.put(player,new Character(id));}
  public static Character getSelectedCharacter(Object player){return selected?CHARACTERS.getOrDefault(player,CHARACTER):null;}
}

