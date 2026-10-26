import net.tompsen.nexuscharacters.*;
import fr.indivis.nexusnpc.*;
import java.nio.file.*;
public class Probe {
 public static void main(String[] args)throws Exception{
  for(Path path:Files.list(Path.of(args[0])).toList()){
   NpcPreset p=NpcPreset.JSON.fromJson(Files.readString(path),NpcPreset.class);CharacterRace r=CharacterRace.fromId(p.race());
   System.out.println(p.id()+" ears="+p.ears()+" -> "+CharacterCosmetics.normalizeEar(r,p.ears())+" beard="+p.beard()+" -> "+CharacterCosmetics.normalizeBeard(r,p.beard()));
  }
 }
}
