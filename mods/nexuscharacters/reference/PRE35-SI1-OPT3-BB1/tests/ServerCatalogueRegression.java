import java.net.*;
import java.io.*;
import java.nio.file.*;
import java.lang.reflect.*;
import java.util.*;
/** Common API smoke test with client classes explicitly unavailable. */
public final class ServerCatalogueRegression {
 static int checks;
 static void check(boolean ok,String text){checks++;if(!ok)throw new AssertionError(text);}
 public static void main(String[] args)throws Exception{
  List<URL> urls=new ArrayList<>();for(String p:args[1].split(File.pathSeparator))urls.add(Path.of(p).toUri().toURL());
  Set<String> blocked=Set.of("net.minecraft.class_310","net.minecraft.class_1043","net.minecraft.class_10055","net.tompsen.nexuscharacters.BlockbenchClient","net.tompsen.nexuscharacters.BlockbenchRenderSupport","net.tompsen.nexuscharacters.BlockbenchTextureCache","net.tompsen.nexuscharacters.BlockbenchModelBaker");
  try(URLClassLoader loader=new URLClassLoader(urls.toArray(URL[]::new),ClassLoader.getPlatformClassLoader()){
   protected Class<?> loadClass(String n,boolean resolve)throws ClassNotFoundException{if(blocked.contains(n))throw new ClassNotFoundException("Client class prohibited in common API: "+n);return super.loadClass(n,resolve);}
  }){
   Class<?> race=loader.loadClass("net.tompsen.nexuscharacters.CharacterRace"),cosmetics=loader.loadClass("net.tompsen.nexuscharacters.CharacterCosmetics"),catalog=loader.loadClass("net.tompsen.nexuscharacters.BlockbenchPresetCatalog"),dynamic=loader.loadClass("net.tompsen.nexuscharacters.DynamicAssetCatalog");
   Method fromId=race.getMethod("fromId",String.class),ear=cosmetics.getMethod("normalizeEar",race,String.class),beard=cosmetics.getMethod("normalizeBeard",race,String.class);
   List<?> entries=(List<?>)catalog.getMethod("entries").invoke(null);check(entries.size()==91,"Catalogue missing on server");
   for(Object e:entries){Class<?> type=e.getClass();String kind=(String)type.getMethod("kind").invoke(e),id=(String)type.getMethod("id").invoke(e);Set<?> races=(Set<?>)type.getMethod("races").invoke(e);boolean alias=(boolean)type.getMethod("aliasNone").invoke(e);
    if(kind.equals("coiffures")){int h=(int)type.getMethod("hairId").invoke(e);check(dynamic.getMethod("normalizeHair",int.class).invoke(null,h).equals(h),"Server rejected known hairstyle");continue;}
    for(String r:List.of("human","nordic","dwarf","wood_elf","high_elf")){Object value=fromId.invoke(null,r);boolean allowed=races.contains(r);String actual=(String)(kind.equals("oreilles")?ear:beard).invoke(null,value,id+(kind.equals("barbes")?"#brown":""));check(allowed&&!alias?actual.equals(id+(kind.equals("barbes")?"#brown":"")):!actual.startsWith("bb_"),"Common normalization differs: "+r+"/"+id+" -> "+actual);}
   }
  }
  String out="BB_SERVER_LINKAGE_PASS checks="+checks+" client_classes_prohibited=true\n";Files.writeString(Path.of(args[0]),out);System.out.print(out);
 }
}
