import java.util.*;
import net.minecraft.*;
import net.minecraft.server.MinecraftServer;
import de.markusbordihn.easynpc.io.DefaultPresetDataFiles;
import de.markusbordihn.easynpc.handler.PresetHandler;
import de.markusbordihn.easynpc.data.preset.PresetType;
import de.markusbordihn.easynpc.entity.easynpc.EasyNPC;
import fr.indivis.nexusnpc.*;

/** Uses EasyNPC's own discovery, loading, security, and entity-import path. */
final class NativePresets {
 static void run(MinecraftServer server) throws Exception {
  Set<class_2960> identifiers=new HashSet<>(DefaultPresetDataFiles.getPresetIdentifiers(server).toList());
  int i=0;
  for(String id:PresetStore.BUILTIN){
   var location=class_2960.method_60655("easy_npc","default_preset/indivis_nexus/"+id+".npc.snbt");
   NpcIntegrationTest.check(identifiers.contains(location),"Preset absent from EasyNPC browser discovery: "+location);
   var preset=PresetHandler.loadPreset(location,PresetType.DEFAULT,server);
   NpcIntegrationTest.check(preset!=null&&preset.hasValidData(),"Native preset invalid "+location);
   UUID target=UUID.randomUUID();
   NpcIntegrationTest.check(PresetHandler.importPreset(server.method_30002(),preset,new class_243(-8+i*2,100,-8),target,null),"Native EasyNPC import failed "+id);
   class_1297 entity=server.method_30002().method_73284(target);
   NpcIntegrationTest.check(entity instanceof EasyNPC<?>,"Imported entity missing "+id);
   var npc=(EasyNPC<?>)entity;
   NpcIntegrationTest.check(NpcAppearance.read(npc.getEasyNPCSkinData().getSkinDataEntry()).preset().equals(PresetStore.load(id)),"Native import lost recipe "+id);
   var scale=npc.getEasyNPCModelData().getModelRootData().scale();
   NpcIntegrationTest.check(Math.abs(scale.y()-PresetStore.load(id).height())<.0001f&&Math.abs(scale.x()-PresetStore.load(id).build())<.0001f,"Native import lost morphology "+id);
   if(entity instanceof class_1308 mob)mob.method_5977(true);
   i++;
  }
  NpcIntegrationTest.results.add("NATIVE_EASYNPC_PASS discovered_loaded_imported="+i);
 }
}


