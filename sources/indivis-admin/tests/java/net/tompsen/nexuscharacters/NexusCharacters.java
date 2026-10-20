package net.tompsen.nexuscharacters;
import java.util.UUID;
import net.indivis.admin.AdapterBench.Player;
public final class NexusCharacters {
  public record Character(UUID id,String name) {}
  public static Character getSelectedCharacter(Player player){return player.character;}
}
