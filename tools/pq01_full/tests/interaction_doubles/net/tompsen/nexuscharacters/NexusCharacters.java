package net.tompsen.nexuscharacters;
import java.util.UUID;
/** Doublure de contexte Nexus, uniquement dans le classpath du banc. */
public final class NexusCharacters {
  public static boolean selected=true;
  public record Character(UUID id){}
  private static final Character CHARACTER=new Character(UUID.randomUUID());
  public static Character getSelectedCharacter(Object player){return selected?CHARACTER:null;}
}
