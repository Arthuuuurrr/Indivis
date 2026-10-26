import java.util.*;
import net.tompsen.nexuscharacters.*;
import fr.indivis.nexusnpc.*;

/** Content-only collection tested against the unchanged PREVIEW1 module. */
final class CollectionV2 {
 static final List<String> IDS=List.of("haut_elfe_sentinelle_v2","elfe_sylvestre_v2","nain_artisan_v2","naine_artisane_v2","nordique_eclaireur_v2");
 static void validate()throws Exception{
  NpcIntegrationTest.check(PresetStore.list().containsAll(IDS)&&PresetStore.list().containsAll(PresetStore.BUILTIN),"Old/new preset coexistence failed");
  for(String id:PresetStore.BUILTIN)NpcIntegrationTest.check(PresetStore.load(id).id().equals(id),"Original preset missing "+id);
  for(String id:IDS){
   var p=PresetStore.load(id);var r=CharacterRace.fromId(p.race());
   NpcIntegrationTest.check(Math.abs(p.height()-(r.minHeight()+r.maxHeight())/2)<.00001f,"Height slider not centered "+id);
   NpcIntegrationTest.check(Math.abs(p.build()-r.defaultBuild())<.00001f,"Build slider not centered "+id);
   var skin=Appearance69Support.parse(p.skin());
   NpcIntegrationTest.check(skin!=null,"Invalid recipe "+id);
   NpcIntegrationTest.check(RacialAppearance69Support.normalizeTone(r,skin.tone())==skin.tone(),"Nonracial skin tone "+id);
  }
  var male=Appearance69Support.parse(PresetStore.load(IDS.get(0)).skin());
  var female=Appearance69Support.parse(PresetStore.load(IDS.get(1)).skin());
  NpcIntegrationTest.check(male.body()==1&&!PresetStore.load(IDS.get(0)).slim(),"High elf must have standard masculine base");
  NpcIntegrationTest.check(female.body()==4&&PresetStore.load(IDS.get(1)).slim(),"Wood elf must have feminine base and slim arms");
  NpcIntegrationTest.results.add("COLLECTION_V2_PASS exact race slider midpoints; masculine high elf; feminine wood elf; 5 original + 5 new IDs coexist");
 }
}
