package perfqa;
import java.lang.management.*;
import java.lang.reflect.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import com.google.gson.*;
import com.mojang.authlib.GameProfile;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.*;
import net.minecraft.server.MinecraftServer;
import net.tompsen.nexuscharacters.*;
import fr.indivis.nexusnpc.*;
import de.markusbordihn.easynpc.entity.easynpc.EasyNPC;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

/** Profiling fixtures only. Never modifies the tested mod or existing worlds. */
public final class PerfMain implements ClientModInitializer {
 static final Path OUT=Path.of(System.getProperty("perfqa.out"));
 static final boolean BB=Boolean.getBoolean("perfqa.bb");
 static final Gson JSON=new GsonBuilder().setPrettyPrinting().create();
 static final com.sun.management.ThreadMXBean BEAN=(com.sun.management.ThreadMXBean)ManagementFactory.getThreadMXBean();
 static final List<GarbageCollectorMXBean> GC=ManagementFactory.getGarbageCollectorMXBeans();
 static final List<String> PROFILES=List.of("haut_elfe_ivoire","elfe_des_bois","nain_bronze","nordique_roux","naine_forge");
 static final List<Scene> SCENES=new ArrayList<>();
 static final List<String> CSV=new ArrayList<>(List.of("variant,kind,pose,count,frame,render_cpu_ns,render_wall_ns,interval_ns,allocated_bytes,native_ns,native_alloc_bytes,native_calls,cosmetic_ns,cosmetic_alloc_bytes,cosmetic_calls,recomputes"));
 static final List<Map<String,Object>> SUMMARIES=new ArrayList<>();
 static final Map<UUID,Integer> INDEX=new ConcurrentHashMap<>();
 static final List<class_1297> SERVER_NPCS=new ArrayList<>();
 static final List<class_745> PLAYERS=new ArrayList<>();
 static int boot,tick,scene=-1,position,measured;static volatile int serverGeneration;
 static boolean waiting,active,recording,failed,finished;static class_745 camera;
 static long cpuStart,wallStart,byteStart,lastStart,interval,nativeStart,nativeByte,cosmeticStart,cosmeticByte;
 static long nativeNs,nativeBytes,nativeCalls,cosmeticNs,cosmeticBytes,cosmeticCalls,recomputeStart;
 static long[] cpu,wall,intervals,allocated,natives,nativeAllocs,nativeCounts,cosmetics,cosmeticAllocs,cosmeticCounts,recomputes;
 static Map<String,Long> cacheStart;static long gcCountStart,gcMsStart,heapStart;
 static JsonObject HEAVY;static final Map<String,Object> ENV=new LinkedHashMap<>();
 record Scene(String variant,String kind,String pose,int count,int warm,int samples){}
 static void require(boolean value,String message){if(!value)throw new AssertionError(message);}
 public void onInitializeClient(){
  BEAN.setThreadAllocatedMemoryEnabled(true);BEAN.setThreadCpuTimeEnabled(true);
  try{HEAVY=JsonParser.parseString(Files.readString(Path.of(System.getProperty("perfqa.recipes")))).getAsJsonObject();Files.createDirectories(OUT);}catch(Exception e){throw new RuntimeException(e);}
  for(String variant:BB?List.of("legacy","authored"):List.of("legacy"))
   for(String kind:List.of("npc","player"))for(int count:kind.equals("npc")?new int[]{0,3,8,24}:new int[]{3,8})for(String pose:List.of("idle","animated_orbit"))
    SCENES.add(new Scene(variant,kind,pose,count,count==24?20:36,count==24?40:96));
  if(Boolean.getBoolean("perfqa.animatedOnly"))SCENES.removeIf(s->!s.pose.equals("animated_orbit"));
  if(Boolean.getBoolean("perfqa.pilot"))SCENES.removeIf(s->!s.kind.equals("npc")||s.count!=3);
  ClientTickEvents.END_CLIENT_TICK.register(c->{try{tick(c);}catch(Throwable e){fail(e);}});
 }
 static void tick(class_310 c)throws Exception{
  if(failed||finished)return;
  if(boot==0){
   if(c.field_1755==null||c.method_18506()!=null||++tick<70)return;
   NexusCharacters.selectedCharacter=new CharacterDto(UUID.fromString("00000000-0000-0000-0000-000000000001"),"Perf QA",null,null,"__capitale_preset__:player_v69_b1_e1_ec0_ey2_h01_hc02_s04_o01_fh0_fc00_mk0_mc0",0,false,"human",1,1,"none","none");
   c.method_1507(null);var config=class_7712.field_40260;var settings=new class_1940("Nexus isolated performance",class_1934.field_9220,false,class_1267.field_5801,true,new class_1928(config.comp_1011()),config);
   c.method_41735().method_41895("perf-isolated",settings,new class_5285(1,false,false),class_5317::method_64225,null);boot=1;tick=0;return;
  }
  if(c.field_1724==null||c.field_1687==null||c.method_18506()!=null)return;
  if(boot==1){
   if(++tick<30)return;
   c.field_1690.field_1842=true;c.field_1724.method_5648(true);
   camera=new class_745(c.field_1687,new GameProfile(UUID.fromString("00000000-0000-0000-0000-000000000099"),"PerfCamera"));c.method_1504(camera);
   GLFW.glfwFocusWindow(c.method_22683().method_4490());
   ENV.put("renderer",GL11.glGetString(GL11.GL_RENDERER));ENV.put("vendor",GL11.glGetString(GL11.GL_VENDOR));ENV.put("gl_version",GL11.glGetString(GL11.GL_VERSION));ENV.put("java",System.getProperty("java.runtime.version"));ENV.put("processors",Runtime.getRuntime().availableProcessors());ENV.put("focused",GLFW.glfwGetWindowAttrib(c.method_22683().method_4490(),GLFW.GLFW_FOCUSED));
   var server=c.method_1576();server.execute(()->{try{for(String cmd:List.of("fill -25 99 -25 25 99 25 minecraft:stone","fill -20 100 -20 20 105 22 minecraft:air","tp @a 0 100 -7 0 0","time set noon","weather clear","gamerule minecraft:advance_time false","gamerule minecraft:advance_weather false"))server.method_3734().method_44252(server.method_3739(),cmd);}catch(Throwable e){fail(e);}});
   boot=2;tick=0;return;
  }
  if(boot==2){if(++tick<35)return;boot=3;next(c);return;}
  if(waiting&&serverGeneration==scene+1){
   int found=0;if(current().kind.equals("npc")){for(var e:c.field_1687.method_18112())if(e instanceof EasyNPC<?> npc){var saved=NpcAppearance.read(npc.getEasyNPCSkinData().getSkinDataEntry());if(saved!=null&&saved.preset().id().startsWith("perf_"+scene+"_"))found++;}}
   else found=PLAYERS.size();
   if(found>=current().count){waiting=false;active=true;position=0;measured=0;lastStart=0;allocBuffers();System.out.println("PERF_SCENE_START "+scene+" "+current());}
  }
 }
 static Scene current(){return SCENES.get(scene);}
 static void next(class_310 c)throws Exception{
  active=false;recording=false;
  for(var p:PLAYERS)c.field_1687.method_2945(p.method_5628(),class_1297.class_5529.field_26998);PLAYERS.clear();INDEX.clear();
  if(++scene>=SCENES.size()){finish(c);return;}
  waiting=true;Scene s=current();List<NpcPreset> recipes=new ArrayList<>();
  for(int i=0;i<s.count;i++)recipes.add(recipe(s.variant,i));
  if(s.kind.equals("player"))createPlayers(c,recipes);
  final int generation=scene+1;
  c.method_1576().execute(()->{try{
   for(var npc:SERVER_NPCS)npc.method_31472();SERVER_NPCS.clear();
   if(s.kind.equals("npc")){int i=0;for(var p:recipes){int index=i++;var e=NexusNpc.spawn(c.method_1576().method_3739().method_9208(new class_243(x(index,s.count),100,z(index))),p);e.method_36456(180);if(e instanceof class_1309 living){living.method_5847(180);living.method_5636(180);}if(e instanceof class_1308 mob)mob.method_5977(true);SERVER_NPCS.add(e);INDEX.put(e.method_5667(),index);}}
   serverGeneration=generation;
  }catch(Throwable e){fail(e);}});
 }
 static double x(int i,int n){return (i%6-(Math.min(n,6)-1)/2d)*1.7;}
 static double z(int i){return 5+(i/6)*2.3;}
 static NpcPreset recipe(String variant,int i)throws Exception{
  NpcPreset p=PresetStore.load(PROFILES.get(i%5));String skin=p.skin(),ears=p.ears(),beard=p.beard();
  if(variant.equals("authored")){
   JsonObject h=HEAVY.getAsJsonObject(p.race());skin=skin.replaceFirst("_h[0-9]+_","_h"+h.get("hair_id").getAsInt()+"_").replaceFirst("_hc[0-9]+_",String.format("_hc%02d_",1+i%12));
   ears=h.get("ears").getAsString();beard=h.get("beard").getAsString();if(!beard.equals("none"))beard+="#"+List.of("black","brown","chestnut","blond","light_blond","dark_blond","red","bright_red","auburn","gray","white","silver").get(i%12);
  }
  return new NpcPreset(1,"perf_"+scene+"_"+i,"Perf "+i,p.race(),skin,p.height(),p.build(),ears,beard,p.slim()).validated();
 }
 @SuppressWarnings("unchecked") static void createPlayers(class_310 c,List<NpcPreset> recipes)throws Exception{
  Field af=CharacterAppearanceClient.class.getDeclaredField("ACTIVE"),cf=CharacterCosmeticsClient.class.getDeclaredField("ACTIVE");af.setAccessible(true);cf.setAccessible(true);
  var appearances=(Map<UUID,CharacterAppearancePayload>)af.get(null);var cosmetics=(Map<UUID,CharacterCosmeticPayload>)cf.get(null);
  for(int i=0;i<recipes.size();i++){
   NpcPreset preset=recipes.get(i);UUID id=UUID.nameUUIDFromBytes(("perf_player_"+scene+"_"+i).getBytes(java.nio.charset.StandardCharsets.UTF_8));var dto=new CharacterDto(id,"PerfPlayer"+i,null,null,"__capitale_preset__:"+preset.skin(),0,false,preset.race(),preset.height(),preset.build(),preset.ears(),preset.beard());
   appearances.put(id,CharacterAppearancePayload.active(id,dto));cosmetics.put(id,CharacterCosmeticPayload.active(id,dto));PreviewDummyPlayerManager.skin(dto);
   var player=new class_745(c.field_1687,new GameProfile(id,"PerfPlayer"+i));player.method_5838(19000+i);player.method_5780("nexuscharacters.skin."+preset.skin());player.method_5780("nexuscharacters.build."+Math.round(preset.build()*100));player.method_5808(x(i,recipes.size()),100,z(i),180,0);player.field_6241=player.field_6259=180;c.field_1687.method_53875(player);PLAYERS.add(player);INDEX.put(id,i);
  }
 }
 static void allocBuffers(){int n=current().samples;cpu=new long[n];wall=new long[n];intervals=new long[n];allocated=new long[n];natives=new long[n];nativeAllocs=new long[n];nativeCounts=new long[n];cosmetics=new long[n];cosmeticAllocs=new long[n];cosmeticCounts=new long[n];recomputes=new long[n];}
 public static void pose(Object entity,class_10055 state){
  if(!active||!(entity instanceof class_1297 e))return;Integer index=INDEX.get(e.method_5667());if(index==null)return;
  state.field_53446=180;
  if(current().pose.equals("animated_orbit")){state.field_53328=0;state.field_53447=(float)Math.sin(position*.071+index)*55;state.field_53448=(float)Math.cos(position*.043+index)*16;state.field_53450=position*.17f+index*.31f;state.field_53451=.6f;}
 }
 public static void frameStart(){try{
  if(!active||failed||finished)return;
  Scene s=current();double angle=s.pose.equals("animated_orbit")?2*Math.PI*position/(s.warm+s.samples):0;
  double cx=Math.sin(angle)*14,cz=8-Math.cos(angle)*14;float yaw=(float)Math.toDegrees(Math.atan2(cx,8-cz));camera.method_5808(cx,101.7,cz,yaw,3);camera.field_6241=camera.field_6259=yaw;camera.field_5982=yaw;
  if(position==s.warm){cacheStart=caches();gcCountStart=gcCount();gcMsStart=gcMs();heapStart=usedHeap();}
  nativeNs=nativeBytes=nativeCalls=cosmeticNs=cosmeticBytes=cosmeticCalls=0;recomputeStart=PosedSurfaceSupport.recomputeCount;
  byteStart=BEAN.getThreadAllocatedBytes(Thread.currentThread().threadId());cpuStart=BEAN.getCurrentThreadCpuTime();wallStart=System.nanoTime();interval=lastStart==0?0:wallStart-lastStart;lastStart=wallStart;recording=true;
 }catch(Throwable e){fail(e);}}
 public static void frameEnd(){try{
  if(!recording||failed)return;recording=false;
  long wallNs=System.nanoTime()-wallStart,cpuNs=BEAN.getCurrentThreadCpuTime()-cpuStart,bytes=BEAN.getThreadAllocatedBytes(Thread.currentThread().threadId())-byteStart;
  if(position>=current().warm){int i=measured++;cpu[i]=cpuNs;wall[i]=wallNs;intervals[i]=interval;allocated[i]=bytes;natives[i]=nativeNs;nativeAllocs[i]=nativeBytes;nativeCounts[i]=nativeCalls;cosmetics[i]=cosmeticNs;cosmeticAllocs[i]=cosmeticBytes;cosmeticCounts[i]=cosmeticCalls;recomputes[i]=PosedSurfaceSupport.recomputeCount-recomputeStart;}
  position++;
  if(measured==current().samples){saveScene();active=false;next(class_310.method_1551());}
 }catch(Throwable e){fail(e);}}
 public static void nativeStart(){if(recording){nativeStart=System.nanoTime();nativeByte=BEAN.getThreadAllocatedBytes(Thread.currentThread().threadId());}}
 public static void nativeEnd(){if(recording){nativeNs+=System.nanoTime()-nativeStart;nativeBytes+=BEAN.getThreadAllocatedBytes(Thread.currentThread().threadId())-nativeByte;nativeCalls++;}}
 public static void cosmeticStart(){if(recording){cosmeticStart=System.nanoTime();cosmeticByte=BEAN.getThreadAllocatedBytes(Thread.currentThread().threadId());}}
 public static void cosmeticEnd(){if(recording){cosmeticNs+=System.nanoTime()-cosmeticStart;cosmeticBytes+=BEAN.getThreadAllocatedBytes(Thread.currentThread().threadId())-cosmeticByte;cosmeticCalls++;}}
 static long value(String name,String field){try{return Class.forName("net.tompsen.nexuscharacters."+name).getField(field).getLong(null);}catch(Exception e){return -1;}}
 static long mapSize(String name,String field){try{Field f=Class.forName("net.tompsen.nexuscharacters."+name).getDeclaredField(field);f.setAccessible(true);return ((Map<?,?>)f.get(null)).size();}catch(Exception e){return -1;}}
 static Map<String,Long> caches(){Map<String,Long> m=new LinkedHashMap<>();for(String[] p:new String[][]{{"BlockbenchModelBaker","bakes"},{"BlockbenchTextureCache","creates"},{"BlockbenchTextureCache","hits"},{"BlockbenchRenderSupport","submissions"}})m.put(p[0]+"."+p[1],value(p[0],p[1]));for(String f:List.of("bakes","cacheHits","fullyRigidParts","rigidPolygons","contactPolygons","fallbacks","evictions","peakBytes"))m.put("ArmorPreparedSurface."+f,value("ArmorPreparedSurface",f));for(String f:List.of("bakes","hits","evictions","peakBytes"))m.put("ArmorContactVolumes."+f,value("ArmorContactVolumes",f));m.put("baked_models",mapSize("BlockbenchModelBaker","CACHE"));m.put("tinted_textures",mapSize("BlockbenchTextureCache","CACHE"));m.put("cached_recipes",mapSize("BlockbenchPresetCatalog","PARAMS"));m.put("native_recomputes",PosedSurfaceSupport.recomputeCount);return m;}
 static long gcCount(){return GC.stream().mapToLong(GarbageCollectorMXBean::getCollectionCount).sum();}
 static long gcMs(){return GC.stream().mapToLong(GarbageCollectorMXBean::getCollectionTime).sum();}
 static long usedHeap(){Runtime r=Runtime.getRuntime();return r.totalMemory()-r.freeMemory();}
 static void saveScene()throws Exception{
  Scene s=current();Map<String,Long> end=caches();Map<String,Object> row=new LinkedHashMap<>();row.put("scene",s);row.put("samples",measured);row.put("cache_start",cacheStart);row.put("cache_end",end);row.put("gc_count",gcCount()-gcCountStart);row.put("gc_ms",gcMs()-gcMsStart);row.put("heap_start",heapStart);row.put("heap_end",usedHeap());SUMMARIES.add(row);
  require(s.count==0||Arrays.stream(nativeCounts).sum()>s.samples,"No genuine entity preparation");require(s.count==0||Arrays.stream(cosmeticCounts).sum()>0,"No genuine cosmetic feature render");
  require(s.count==0||Arrays.stream(cosmeticCounts).min().orElse(0)>=s.count,"Some target entities left the camera frustum; discard this measurement");
  if(s.variant.equals("authored")&&s.count>0)require(end.get("BlockbenchRenderSupport.submissions")>cacheStart.get("BlockbenchRenderSupport.submissions"),"Authored models did not reach renderer");
  for(int i=0;i<measured;i++)CSV.add(s.variant+","+s.kind+","+s.pose+","+s.count+","+i+","+cpu[i]+","+wall[i]+","+intervals[i]+","+allocated[i]+","+natives[i]+","+nativeAllocs[i]+","+nativeCounts[i]+","+cosmetics[i]+","+cosmeticAllocs[i]+","+cosmeticCounts[i]+","+recomputes[i]);
  Files.write(OUT.resolve("frames.csv"),CSV);Files.writeString(OUT.resolve("stages.json"),JSON.toJson(SUMMARIES));
  long[] sorted=cpu.clone();Arrays.sort(sorted);System.out.println("PERF_SCENE_PASS "+scene+" "+s+" cpu_median_ms="+sorted[sorted.length/2]/1e6+" cache="+end);
 }
 static void finish(class_310 c)throws Exception{finished=true;Files.writeString(OUT.resolve("environment.json"),JSON.toJson(ENV));Files.writeString(OUT.resolve("pass.txt"),"PERF_PASS scenes="+SUMMARIES.size()+" frames="+(CSV.size()-1)+" diagnostics=false\n");c.method_1490();}
 static void fail(Throwable failure){if(failed)return;failed=true;failure.printStackTrace();try{Files.createDirectories(OUT);var out=new java.io.StringWriter();failure.printStackTrace(new java.io.PrintWriter(out));Files.writeString(OUT.resolve("failure.txt"),out.toString());}catch(Exception ignored){}class_310.method_1551().method_1490();}
}
