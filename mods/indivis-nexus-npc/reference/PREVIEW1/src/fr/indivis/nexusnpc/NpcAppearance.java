package fr.indivis.nexusnpc;
import com.google.gson.*;
import java.util.*;
import de.markusbordihn.easynpc.entity.easynpc.EasyNPC;
import de.markusbordihn.easynpc.data.skin.*;
import de.markusbordihn.easynpc.data.model.RootModelData;
import de.markusbordihn.easynpc.data.scale.CustomScale;
import net.minecraft.*;

/** Persisted and synced through EasyNPC's existing SkinData channel, only on edits. */
public final class NpcAppearance {
 public static final String PREFIX="indivis-nexus-npc:v1:";
 public record Saved(NpcPreset preset,String originalSkin,String originalRoot){}
 private NpcAppearance(){}
 public static Saved read(SkinDataEntry entry){
  if(entry==null||entry.content()==null||!entry.content().startsWith(PREFIX))return null;
  if(entry.content().length()>20000)throw new IllegalArgumentException("Données PNJ trop volumineuses");
  Saved saved=NpcPreset.JSON.fromJson(entry.content().substring(PREFIX.length()),Saved.class);
  if(saved==null||saved.preset()==null||saved.originalSkin()==null||saved.originalRoot()==null)throw new IllegalArgumentException("Recette PNJ incomplète");
  saved.preset().validated();return saved;
 }
 public static boolean supported(class_1297 entity){
  if(!(entity instanceof EasyNPC<?> npc))return false;
  return Set.of("easy_npc:humanoid","easy_npc:humanoid_slim").contains(npc.getEntityTypeId());
 }
 public static void apply(class_1297 entity,NpcPreset p)throws Exception{
  p.validated();if(!supported(entity))throw new IllegalArgumentException("Cibler un PNJ EasyNPC humanoid ou humanoid_slim");
  EasyNPC<?> npc=(EasyNPC<?>)entity;
  SkinDataEntry original=npc.getEasyNPCSkinData().getSkinDataEntry();
  Saved before=read(original);
  Saved saved=new Saved(p,before==null?original.createTag().toString():before.originalSkin(),before==null?npc.getEasyNPCModelData().getModelRootData().save().toString():before.originalRoot());
  RootModelData root=RootModelData.load(class_2522.method_67315(saved.originalRoot()));
  CustomScale scale=root.scale();
  RootModelData result=new RootModelData(root.rotation(),new CustomScale(scale.x()*p.build(),scale.y()*p.height(),scale.z()*p.build()));
  String content=PREFIX+NpcPreset.JSON.toJson(saved);
  if(content.length()>20000)throw new IllegalArgumentException("Données PNJ trop volumineuses");
  // Do not modify UUID, name, inventory, quests, AI, dialogs or non-root model data.
  npc.getEasyNPCModelData().setModelRootData(result);
  npc.getEasyNPCSkinData().setSkinDataEntry(new SkinDataEntry("Nexus: "+p.id(),"",new UUID(0,0),SkinType.DEFAULT,false,content,System.currentTimeMillis()));
 }
 public static boolean reset(class_1297 entity)throws Exception{
  if(!supported(entity))throw new IllegalArgumentException("PNJ EasyNPC humanoïde requis");
  EasyNPC<?> npc=(EasyNPC<?>)entity;Saved saved=read(npc.getEasyNPCSkinData().getSkinDataEntry());if(saved==null)return false;
  // Parse both backups before mutating either field.
  RootModelData root=RootModelData.load(class_2522.method_67315(saved.originalRoot()));
  SkinDataEntry skin=new SkinDataEntry(class_2522.method_67315(saved.originalSkin()));
  npc.getEasyNPCModelData().setModelRootData(root);npc.getEasyNPCSkinData().setSkinDataEntry(skin);return true;
 }
}
