package fr.indivis.nexusnpc;

import com.google.gson.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import net.tompsen.nexuscharacters.*;

/** An appearance recipe, not a character account or an NPC replacement. */
public record NpcPreset(int version,String id,String name,String race,String skin,float height,float build,
    String ears,String beard,boolean slim) {
 public static final Gson JSON=new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
 public static final int MAX_JSON=4096;
 public NpcPreset validated(){
  if(version!=1)throw new IllegalArgumentException("Version de preset inconnue");
  if(id==null||!id.matches("[a-z0-9][a-z0-9_-]{0,47}"))throw new IllegalArgumentException("ID attendu : a-z, 0-9, _ ou -, 48 caractères maximum");
  if(name==null||name.isBlank()||name.length()>96)throw new IllegalArgumentException("Nom du preset invalide");
  CharacterRace r=Arrays.stream(CharacterRace.values()).filter(x->x.id().equals(race)).findFirst().orElseThrow(()->new IllegalArgumentException("Race Nexus inconnue"));
  if(r==CharacterRace.VILLAGER)throw new IllegalArgumentException("Le prototype est réservé aux modèles humanoïdes Nexus");
  if(!Float.isFinite(height)||!Float.isFinite(build)||height<r.minHeight()-.0001f||height>r.maxHeight()+.0001f||build<r.minBuild()-.0001f||build>r.maxBuild()+.0001f)throw new IllegalArgumentException("Morphologie hors des limites de la race Nexus");
  if(skin==null||!(Appearance69Support.isId(skin)||Appearance68Support.isId(skin)||Appearance65Support.isId(skin)))throw new IllegalArgumentException("Recette Nexus invalide ou version non prise en charge");
  if(ears==null||beard==null||ears.length()>96||beard.length()>128)throw new IllegalArgumentException("Cosmétiques invalides");
  if(!CharacterCosmetics.normalizeEar(r,ears).equals(ears)||!CharacterCosmetics.normalizeBeard(r,beard).equals(beard))throw new IllegalArgumentException("Cosmétiques incompatibles avec la race");
  return this;
 }
 public String json(){return JSON.toJson(validated());}
 public static NpcPreset parse(String text){
  if(text==null||text.length()>MAX_JSON)throw new IllegalArgumentException("Preset trop volumineux");
  NpcPreset p=JSON.fromJson(text,NpcPreset.class);if(p==null)throw new IllegalArgumentException("Preset vide");return p.validated();
 }
 public CharacterDto dto(){return new CharacterDto(UUID.nameUUIDFromBytes(("indivis-npc:"+id+":"+skin).getBytes(StandardCharsets.UTF_8)),name,null,null,"__capitale_preset__:"+skin,0,false,race,height,build,ears,beard);}
 public static NpcPreset fromDto(String id,CharacterDto dto,boolean slim){
  String prefix="__capitale_preset__:";
  if(dto.skinUsername()==null||!dto.skinUsername().startsWith(prefix))throw new IllegalArgumentException("Sélectionner une apparence Nexus dans l’éditeur");
  return new NpcPreset(1,id,dto.name(),dto.race(),dto.skinUsername().substring(prefix.length()),dto.heightScale(),dto.buildScale(),dto.earStyle(),dto.beardStyle(),slim).validated();
 }
}
