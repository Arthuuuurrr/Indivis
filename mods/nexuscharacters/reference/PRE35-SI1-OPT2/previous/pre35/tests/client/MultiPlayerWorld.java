import java.util.*;
import net.minecraft.*;
import com.mojang.authlib.GameProfile;
import net.tompsen.nexuscharacters.*;
/** Isolated client-world entities, never touches an external server or user save. */
public final class MultiPlayerWorld {
 static final List<class_745> players=new ArrayList<>();
 static final Map<Integer,Set<Integer>> rendered=new TreeMap<>();
 public static int audits;
 @SuppressWarnings("unchecked")
 public static void setup(class_310 c,int scene)throws Exception{
  for(class_745 p:players)c.field_1687.method_2945(p.method_5628(),class_1297.class_5529.field_26998);
  players.clear();
  int count=scene==18?1:scene==19?2:scene==20?4:8;
  Map<UUID,CharacterAppearancePayload> appearances=(Map<UUID,CharacterAppearancePayload>)SurfaceGeometry.field(CharacterAppearanceClient.class,"ACTIVE");
  Map<UUID,CharacterCosmeticPayload> cosmetics=(Map<UUID,CharacterCosmeticPayload>)SurfaceGeometry.field(CharacterCosmeticsClient.class,"ACTIVE");
  c.field_1724.method_36456(0);c.field_1724.method_36457(0);
  for(int i=0;i<count;i++){
   UUID id=UUID.nameUUIDFromBytes(("PRE35_Player_"+i).getBytes(java.nio.charset.StandardCharsets.UTF_8));
   int variant=i%2;CharacterDto dto=new CharacterDto(id,"PRE35_Player_"+i,null,null,"__capitale_preset__:"+WorldDiagnostic.markers[variant],0,false,variant==0?"human":"dwarf",variant==0?1:.82f,variant==0?1:1.17f,"none","none");
   appearances.put(id,CharacterAppearancePayload.active(id,dto));cosmetics.put(id,CharacterCosmeticPayload.active(id,dto));
   PreviewDummyPlayerManager.skin(dto);
   class_745 player=new class_745(c.field_1687,new GameProfile(id,"PRE35_Player_"+i));
   player.method_5838(9940+i);player.method_5780("nexuscharacters.skin."+WorldDiagnostic.markers[variant]);player.method_5780("nexuscharacters.build."+(variant==0?100:117));
   player.method_5875(true);c.field_1687.method_53875(player);players.add(player);
  }
  update(c,0);System.out.println("REMOTE35_SCENE "+scene+" players="+count);
 }
 public static void update(class_310 c,int tick){
  for(int i=0;i<players.size();i++){
   class_745 p=players.get(i);double angle=(i-(players.size()-1)/2d)*.18;
   p.method_5814(c.field_1724.method_23317()+java.lang.Math.sin(angle)*5,c.field_1724.method_23318(),c.field_1724.method_23321()+java.lang.Math.cos(angle)*5);
   p.method_36456(180+(float)java.lang.Math.sin(tick*.06+i)*20);p.method_36457((float)java.lang.Math.sin(tick*.04+i)*15);
   if(tick%12==i%12)p.method_6104(class_1268.field_5808);
  }
 }
 public static void audit(Object model,Object renderState){
  if(WorldDiagnostic.scene<18)return;
  try{
   Object entity=SurfaceGeometry.call(renderState,"getTransitionEntity");
   if(!(entity instanceof class_745 player)||!players.contains(player))return;
   Object state=RenderPerf.state(model);WorldDiagnostic.check(state!=null,"Remote surface state absent");
   for(String name:RenderPerf.LOW){
    var part=(dev.tr7zw.skinlayers.accessor.ModelPartInjector)SurfaceGeometry.field(model,name);
    WorldDiagnostic.check(part.getInjectedMesh()!=null&&part.getInjectedMesh().toString().startsWith("NexusPosed23"),"Remote native base mesh absent");
   }
   long count=0;for(Object mesh:(Object[])SurfaceGeometry.field(state,"outer"))count+=((float[])SurfaceGeometry.field(mesh,"polygonData")).length/23;
   WorldDiagnostic.check(count>100,"Remote outer geometry absent: "+count);
   rendered.computeIfAbsent(WorldDiagnostic.scene,k->new TreeSet<>()).add(player.method_5628());audits++;
  }catch(Throwable e){WorldDiagnostic.recordFailure(e);throw new RuntimeException(e);}
 }
 public static void finish(){
  for(int scene=18;scene<24;scene++){
   int expected=scene==18?1:scene==19?2:scene==20?4:8;
   WorldDiagnostic.check(rendered.getOrDefault(scene,Set.of()).size()==expected,"Remote entities not rendered in scene "+scene+": "+rendered);
  }
 }
}

