package fr.indivis.nexusnpc;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import net.fabricmc.loader.api.FabricLoader;

public final class PresetStore {
 private PresetStore(){}
 public static final List<String> BUILTIN=List.of("haut_elfe_ivoire","naine_forge","nain_bronze","nordique_roux","elfe_des_bois");
 public static Path folder(){return FabricLoader.getInstance().getConfigDir().resolve("indivis-nexus-npc/presets");}
 public static Path localFolder(){return FabricLoader.getInstance().getConfigDir().resolve("indivis-nexus-npc/local");}
 public static Path path(String id){if(id==null||!id.matches("[a-z0-9][a-z0-9_-]{0,47}"))throw new IllegalArgumentException("ID de preset invalide");return folder().resolve(id+".json");}
 public static NpcPreset load(String id)throws IOException{
  Path p=path(id);
  if(Files.exists(p)){if(Files.size(p)>NpcPreset.MAX_JSON)throw new IOException("Preset trop volumineux");return NpcPreset.parse(Files.readString(p));}
  try(InputStream in=PresetStore.class.getResourceAsStream("/presets/"+id+".json")){
   if(in==null)throw new FileNotFoundException("Preset inconnu : "+id);
   return NpcPreset.parse(new String(in.readNBytes(NpcPreset.MAX_JSON+1),StandardCharsets.UTF_8));
  }
 }
 public static void saveNew(NpcPreset preset)throws IOException{
  preset.validated();Files.createDirectories(folder());
  // Never replace an existing user preset silently.
  Files.writeString(path(preset.id()),preset.json()+"\n",StandardOpenOption.CREATE_NEW,StandardOpenOption.WRITE);
 }
 public static NpcPreset loadLocal(String id)throws IOException{path(id);Path p=localFolder().resolve(id+".json");if(Files.size(p)>NpcPreset.MAX_JSON)throw new IOException("Preset trop volumineux");return NpcPreset.parse(Files.readString(p));}
 public static void saveLocalNew(NpcPreset preset)throws IOException{preset.validated();Files.createDirectories(localFolder());Files.writeString(localFolder().resolve(preset.id()+".json"),preset.json()+"\n",StandardOpenOption.CREATE_NEW,StandardOpenOption.WRITE);}
 public static List<String> list()throws IOException{
  Set<String> all=new TreeSet<>(BUILTIN);
  if(Files.isDirectory(folder()))try(var files=Files.list(folder())){files.filter(p->p.getFileName().toString().matches("[a-z0-9][a-z0-9_-]{0,47}\\.json")).forEach(p->all.add(p.getFileName().toString().replaceFirst("\\.json$","")));}
  return List.copyOf(all);
 }
}
