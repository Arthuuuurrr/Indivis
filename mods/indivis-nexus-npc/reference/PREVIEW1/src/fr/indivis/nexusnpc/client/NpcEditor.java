package fr.indivis.nexusnpc.client;
import fr.indivis.nexusnpc.*;
import net.minecraft.*;
import net.tompsen.nexuscharacters.*;
import java.util.*;

public final class NpcEditor {
 private static final Map<Object,String> EDITORS=new WeakHashMap<>();
 public static long exports;
 private NpcEditor(){}
 public static boolean active(Object screen){return EDITORS.containsKey(screen);}
 public static void open(String id){
  PresetStore.path(id);var client=class_310.method_1551();
  var screen=new CharacterCreationScreen(client.field_1755,()->{});EDITORS.put(screen,id);
  client.method_1507(screen);
  IndivisMenus.set(screen,"status","Mode PNJ — Créer = exporter le preset.");
 }
 public static boolean confirm(Object screen){
  String id=EDITORS.get(screen);if(id==null)return false;
  try{
   CharacterDto dto=(CharacterDto)IndivisMenus.invoke(screen,"previewDto");
   // The base-2 choice produces the narrow preset type. Assets are still UV-adapted for both NPC models.
   boolean slim=dto.skinUsername()!=null&&dto.skinUsername().contains("_b2_");
   NpcPreset preset=NpcPreset.fromDto(id,dto,slim);PresetStore.saveLocalNew(preset);exports++;
   IndivisMenus.set(screen,"status","Preset PNJ enregistré.");
   var player=class_310.method_1551().field_1724;if(player!=null)player.method_7353(class_2561.method_43470("Preset PNJ enregistré : "+id+". Publier avec /nexusnpc_local push "+id),false);
  }catch(Exception e){IndivisMenus.set(screen,"status","Export PNJ refusé : "+e.getMessage());}
  return true;
 }
}
