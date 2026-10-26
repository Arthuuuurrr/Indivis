import java.util.*;
import net.minecraft.*;
import de.markusbordihn.easynpc.entity.easynpc.EasyNPC;
import de.markusbordihn.easynpc.data.dialog.DialogDataEntry;
import de.markusbordihn.easynpc.data.model.RootModelData;
import de.markusbordihn.easynpc.data.rotation.CustomRotation;
import de.markusbordihn.easynpc.data.scale.CustomScale;
import fr.indivis.nexusnpc.*;

final class NpcDataRegression {
 static void dirty(class_2487 nbt){nbt.method_10562("Status").ifPresent(t->t.method_10551("npc_data_last_update"));}
 static void appearance(class_2487 nbt){dirty(nbt);nbt.method_10551("SkinData");nbt.method_10562("ModelData").ifPresent(t->t.method_10551("Root"));}
 static void run(class_3218 world)throws Exception{
  class_1299<?> type=null;for(var t:class_7923.field_41177)if(class_1299.method_5890(t).toString().equals("easy_npc:humanoid")){type=t;break;}
  var entity=type.method_5883(world,class_3730.field_16460);var npc=(EasyNPC<?>)entity;
  entity.method_5665(class_2561.method_43470("Dialogue et quête à conserver"));entity.method_5780("quest_regression_sentinel");
  var dialog=new DialogDataEntry("quest_sentinel","Texte de quête inchangé","test_quest");npc.getEasyNPCDialogData().setDialog(dialog.getId(),dialog);
  var root=new RootModelData(new CustomRotation(0,17,0),new CustomScale(.95f,1.03f,.93f));npc.getEasyNPCModelData().setModelRootData(root);
  class_2487 original=NpcIntegrationTest.snapshot(entity);
  for(String id:NpcIntegrationTest.presets()){
   var p=PresetStore.load(id);NpcAppearance.apply(entity,p);
   class_2487 a=NpcIntegrationTest.snapshot(entity),b=NpcIntegrationTest.snapshot(entity); // full NBT captured independently below
   b=class_2522.method_67315(original.toString());appearance(a);appearance(b);
   NpcIntegrationTest.check(a.equals(b),"NPC data outside appearance changed on first apply: "+id);
   var s=npc.getEasyNPCModelData().getModelRootData().scale();
   NpcIntegrationTest.check(Math.abs(s.x()-.95f*p.build())<.0001f&&Math.abs(s.y()-1.03f*p.height())<.0001f&&Math.abs(s.z()-.93f*p.build())<.0001f,"Root scale accumulated "+id);
   var saved=NpcIntegrationTest.snapshot(entity);var loaded=type.method_5883(world,class_3730.field_16460);
   loaded.method_5651(class_11352.method_71417(class_8942.field_60348,world.method_30349(),saved));
   var loadedNpc=(EasyNPC<?>)loaded;
   NpcIntegrationTest.check(NpcAppearance.read(loadedNpc.getEasyNPCSkinData().getSkinDataEntry()).preset().equals(p),"Entity NBT reload lost recipe "+id);
   NpcIntegrationTest.check(loaded.method_5667().equals(entity.method_5667())&&loadedNpc.getEasyNPCDialogData().hasDialog(dialog.getId()),"Entity NBT reload lost identity/dialogue "+id);
  }
  NpcIntegrationTest.check(NpcAppearance.reset(entity),"Final reset failed");
  class_2487 restored=NpcIntegrationTest.snapshot(entity);dirty(original);dirty(restored);
  NpcIntegrationTest.check(restored.equals(original),"Reset did not restore original full NPC NBT");
  NpcIntegrationTest.results.add("NPC_DATA_PASS 5 applications; original UUID/name/dialogue/tags/model rotation preserved; entity NBT reload; full reset");
 }
}
