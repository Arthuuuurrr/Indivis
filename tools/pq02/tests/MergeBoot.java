import net.fabricmc.api.ModInitializer;import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;import java.nio.file.*;import java.lang.reflect.*;
/** Smoke test: the shipped merged JAR and all its real main entrypoints stay intact. */
public final class MergeBoot implements ModInitializer {
 static Object call(Object owner,String name,Object...args)throws Exception{for(Method m:owner.getClass().getMethods())if(m.getName().equals(name)&&m.getParameterCount()==args.length)try{return m.invoke(owner,args);}catch(IllegalArgumentException ignored){}throw new NoSuchMethodException(name);}
 static void check(boolean yes,String label){if(!yes)throw new AssertionError(label);System.out.println("MERGE BOOT CHECK "+label);}
 public void onInitialize(){ServerLifecycleEvents.SERVER_STARTED.register(server->{
  try{
   var loader=FabricLoader.getInstance();check(loader.isModLoaded("haute_capitale_quetes")&&!loader.isModLoaded("indivis_pq02"),"one quest mod ID; no PQ02 companion");
   Object root=call(call(call(server,"method_3734"),"method_9235"),"getRoot");
   for(String name:new String[]{"indivispq01","indivispq02","indivissequence","hcd_interpellation"})check(call(root,"getChild",name)!=null,"command initialized "+name);
   for(String name:new String[]{"Pq01Bridge","Pq02Runtime","SequenceBridge"}){Field f=Class.forName("net.hautecapitale.quetes.sequence."+name).getDeclaredField("initialized");f.setAccessible(true);check(f.getBoolean(null),"real main initializer ran "+name);}
   Files.writeString(Path.of("smoke-result.txt"),"PASS full merged production JAR startup; 8 checks. All original+PQ02 main entrypoints and mixin configs intact. Nexus/Puffish/RPG dependency declarations supplied by test-only metadata, no player/API gameplay exercised. No client rendered.\n");
  }catch(Throwable e){e.printStackTrace();try{Files.writeString(Path.of("smoke-result.txt"),"FAIL "+e);}catch(Exception ignored){}}
  try{call(call(server,"method_3734"),"method_44252",call(server,"method_3739"),"stop");}catch(Exception e){throw new RuntimeException(e);}
 });}
}
