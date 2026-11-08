package net.hautecapitale.quetes.sequence;

import java.lang.reflect.*;
import static net.hautecapitale.quetes.sequence.Pq01Bridge.*;
import static net.hautecapitale.quetes.sequence.SequenceBridge.*;
import static net.hautecapitale.quetes.sequence.Pq02WorldBench.*;

/** Test-only event phases; production quest and dialogue JARs are copied without modification. */
final class Pq02FullSystemBench {
 static final boolean ACTIVE=Boolean.getBoolean("pq02.full.system");
 static int coreTicks,framePulses,nativeClicks;
 static void network(Object player,Object handler)throws Exception{if(!ACTIVE)return;
  Object connection=make("net.minecraft.class_2535",en("net.minecraft.class_2598","field_11941"));set(handler,"field_45013",connection);
  Object addon=make("net.fabricmc.fabric.impl.networking.server.ServerPlayNetworkAddon",handler,connection,srv);
  boolean attached=false;for(Class<?> c=handler.getClass();c!=null;c=c.getSuperclass())for(Field f:c.getDeclaredFields())if(f.getType().getName().equals("net.fabricmc.fabric.impl.networking.server.ServerPlayNetworkAddon")){f.setAccessible(true);f.set(handler,addon);attached=true;}
  if(!attached)throw new AssertionError("Synthetic handler lacks Fabric addon field");
  @SuppressWarnings("unchecked") java.util.Set<Object> channels=(java.util.Set<Object>)get(addon,"sendableChannels");
  for(String name:java.util.List.of("journal_snapshot","quest_update","notification","tracked_update"))channels.add(stat("net.minecraft.class_2960","method_60654","haute_capitale_quetes:"+name));
 }
 static void interact(Object player,Object npc){
  Object hand=en("net.minecraft.class_1268","field_5808"),event=en("net.fabricmc.fabric.api.event.player.UseEntityCallback","EVENT");
  Object result=call(call(event,"invoker"),"interact",player,w,hand,npc,null);
  ok(result.equals(en("net.minecraft.class_1269","field_5811")),"full system: original use-entity listeners allow NPC interaction");
  Object action=call(npc,"method_5688",player,hand);nativeClicks++;
  if(!Pq02Runtime.VIEWS.containsKey(uuid(player))&&!Pq02Entries.PENDING.containsKey(uuid(player))){
   Object raw=call(npc,"method_5992",player,hand);Object visibility=stat("de.markusbordihn.easynpc.entity.easynpc.handlers.VisibilityHandler","handleIsInvisibleToPlayer",npc,player,false);
   throw new AssertionError("NPC action did not open or queue PQ02: result="+action+", raw="+raw+", afterRawQueued="+Pq02Entries.PENDING.containsKey(uuid(player))+", invisible="+visibility+", permissions="+call(call(npc,"getEasyNPCActionEventData"),"getActionPermissionLevel")+", npc="+npc.getClass().getName()+", actions="+Pq02Runtime.actionText(npc)+", alive="+call(npc,"method_5805")+", emptyHand="+call(call(player,"method_6047"),"method_7960")+", nativeSession="+Pq02Runtime.current(player));
  }
 }
 static void endPhase(boolean first,EventHandler handler){
  if(!ACTIVE){event("net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents$EndTick","END_SERVER_TICK",handler);return;}
  Class<?> contract=type("net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents$EndTick");
  Object ev=en("net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents","END_SERVER_TICK"),phase=stat("net.minecraft.class_2960","method_60654",first?"pq02_fixture:players":"pq02_fixture:assertions"),normal=en("net.fabricmc.fabric.api.event.Event","DEFAULT_PHASE");
  call(ev,"addPhaseOrdering",first?phase:normal,first?normal:phase);
  Object listener=Proxy.newProxyInstance(contract.getClassLoader(),new Class<?>[]{contract},(p,m,a)->{if(m.getDeclaringClass()==Object.class)return objectMethod(p,m,a);try{return handler.run(a);}catch(Throwable e){fail(e);return null;}});
  call(ev,"register",phase,listener);
 }
 static void coreTick(){if(!ACTIVE||srv==null||finished||t<20)return;cmd("function capitale:core/tick");coreTicks++;}
 static void framePulse(){if(!ACTIVE)return;coreTick();framePulses++;}
 static void started(){if(!ACTIVE)return;
  ok(((java.util.Optional<?>)stat("net.hautecapitale.quetes.engine.QuestEngine","optional")).isPresent(),"full system: actual QuestEngine initialized and running");
  ok(type("net.hautecapitale.dialogue.capture.CaptureOpener")!=null,"full system: original Dialogue classes available");
  ok(CoreQuestMarkers.functionPresent(a,"capitale:core/tick"),"full system: real complete core tick and transitive functions loaded");
 }
 static void finished(){if(!ACTIVE)return;ok(coreTicks>300&&framePulses>100,"full system: core tick ran during escort, long pause and every rendered narrative step");ok(nativeClicks>50,"full system: public and private NPCs used actual Fabric listeners and EasyNPC interact");
  ok(((java.util.Optional<?>)stat("net.hautecapitale.quetes.engine.QuestEngine","optional")).isPresent(),"full system: original QuestEngine still active at completion");evidence.add("Full integration core ticks="+coreTicks+", narrative interleavings="+framePulses);
 }
}
