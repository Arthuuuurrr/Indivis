package net.hautecapitale.quetes.sequence;

import java.nio.file.*;
import java.util.*;
import java.lang.reflect.*;
import net.fabricmc.api.ModInitializer;
import static net.hautecapitale.quetes.sequence.Pq01Bridge.*;
import static net.hautecapitale.quetes.sequence.SequenceBridge.*;
import static net.hautecapitale.quetes.sequence.Pq45Runtime.*;

/** TEST ONLY: actual Fabric/EasyNPC world, synthetic player connection and selected profile. */
public final class ColinBindingNative implements ModInitializer {
 static Object server,level,player,colin,upper,other;static int checks,legacyCalls;
 static final List<String> evidence=new ArrayList<>();
 public void onInitialize(){event("net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents$ServerStarted","SERVER_STARTED",a->{
  server=a[0];try{test();Files.writeString(Path.of("colin-tests.txt"),"PASS "+checks+" native checks\n"+String.join("\n",evidence)+"\n");}
  catch(Throwable failure){failure.printStackTrace();Files.writeString(Path.of("colin-tests.txt"),"FAIL "+failure+"\n"+String.join("\n",evidence));}
  finally{cmd("stop");}return null;
 });}
 static void check(boolean condition,String label){if(!condition)throw new AssertionError(label);checks++;evidence.add(label);System.out.println("COLIN CHECK "+label);}
 static void cmd(String text){call(call(server,"method_3734"),"method_44252",call(server,"method_3739"),text);}
 static void set(Object object,String name,Object value)throws Exception{for(Class<?> c=object.getClass();c!=null;c=c.getSuperclass())try{Field f=c.getDeclaredField(name);f.setAccessible(true);f.set(object,value);return;}catch(NoSuchFieldException ignored){}throw new NoSuchFieldException(name);}
 static Object npc(String name,double x,double y,double z,String tag){UUID id=UUID.randomUUID();cmd("summon easy_npc:humanoid "+x+" "+y+" "+z+" {UUID:"+uuidArray(id)+",CustomName:"+quote(name)+",Tags:["+quote(tag)+"]}");Object e=call(level,"method_66347",id);if(e==null)throw new AssertionError("NPC spawn failed");return e;}
 static Object click(Object npc,Object hand){Object callback=call(en("net.fabricmc.fabric.api.event.player.UseEntityCallback","EVENT"),"invoker");return call(callback,"interact",player,level,hand,npc,null);}
 static boolean pending(){return Pq45Entries.PENDING.containsKey(uuid(player));}
 static void noPending(){Pq45Entries.clear();}
 static void unsetColin(){Object root=Pq01Nbt.copy(config(player)),roles=child(root,"roles");if(roles!=null)call(roles,"method_10551","colin");GuardNbt.save(server,SETUP,root);}
 static void test()throws Exception{
  level=call(server,"method_30002");cmd("forceload add -16 -16 16 16");
  player=make("net.minecraft.class_3222",server,level,make("com.mojang.authlib.GameProfile",UUID.randomUUID(),"ColinFixTest"),stat("net.minecraft.class_8791","method_53821"));
  call(player,"method_5814",0.5d,4d,1.5d);Field uf=Class.forName("sun.misc.Unsafe").getDeclaredField("theUnsafe");uf.setAccessible(true);Object sink=call(uf.get(null),"allocateInstance",type("net.hautecapitale.quetes.sequence.ColinPacketSink"));set(sink,"field_14140",player);set(sink,"field_45026",type("net.minecraft.class_8608").getConstructor(boolean.class).newInstance(false));set(player,"field_13987",sink);
  net.tompsen.nexuscharacters.NexusCharacters.select(player,UUID.randomUUID());
  for(String score:List.of("CAP_PROFILE_LOCK","CAP_QUETEACTIVE","hcq_pq03","hcq_pq04","hcq_pq05"))cmd("scoreboard objectives add "+score+" dummy");
  score2(player,"hcq_pq03",100);score2(player,"hcq_pq04",50);
  colin=npc("Colin Férand",0.5,4,0.5,"test_colin");upper=npc("Roch Vallet",0.5,24,0.5,"test_upper");
  check(local(player,pos(player,level),4,Pq45Runtime::publicNpc).size()==2,"R1 search box reproduces the NPC on the upper floor");
  Object actions=actionText(colin);Pq45Runtime.bind(player,"colin");
  check(str(roleRecord(player,"colin"),"uuid","").equals(uuid(colin).toString()),"binding excludes the upper floor and selects Colin");
  check(actions.equals(actionText(colin)),"binding preserves public interaction actions");
  check(pos(colin,level).y()==4&&pos(upper,level).y()==24,"binding moves neither public NPC");
  other=npc("Garde des Profondeurs",1.5,4,0.5,"test_other");Pq45Runtime.bind(player,"colin");
  check(str(roleRecord(player,"colin"),"uuid","").equals(uuid(colin).toString()),"Colin identity wins over a nearby unrelated guard");
  unsetColin();check(Pq45Runtime.source(player,"colin")==colin,"named Colin auto-binds without legacy commands or quest tags");
  check(normalizedName("§aColin  Férand").equals("colin ferand"),"identity accepts colour, accents and extra spaces");
  View prior=Pq45Runtime.view(player);prior.actors.put("roch",new Actor("roch",upper,uuid(upper),null,null));Pq45Runtime.bind(player,"colin");
  check(str(roleRecord(player,"colin"),"uuid","").equals(uuid(colin).toString()),"a Roch scene does not prevent binding Colin");VIEWS.clear();HIDDEN.clear();
  Object duplicate=npc("Colin Férand",2.5,4,0.5,"test_duplicate");boolean ambiguous=false;try{Pq45Runtime.bind(player,"colin");}catch(IllegalStateException expected){ambiguous=expected.getMessage().contains("UUID");}
  check(ambiguous,"two named Colins fail with an explicit UUID remedy");
  Pq45Runtime.bind(player,"colin",uuid(colin));check(str(roleRecord(player,"colin"),"uuid","").equals(uuid(colin).toString()),"explicit UUID resolves two named Colins");
  Pq45Runtime.register(call(call(server,"method_3734"),"method_9235"));unsetColin();
  run(player,"indivispq45 bind_colin "+uuid(colin));check(str(roleRecord(player,"colin"),"uuid","").equals(uuid(colin).toString()),"documented bind_colin UUID command executes through Minecraft's real dispatcher");
  call(duplicate,"method_5780","hcm_private");boolean privateRejected=false;try{Pq45Runtime.bind(player,"colin",uuid(duplicate));}catch(IllegalStateException expected){privateRejected=true;}
  check(privateRejected,"explicit binding cannot select a private actor");call(duplicate,"method_31472");
  // Register legacy listener first: the new priority phase must still consume Colin before it.
  event("net.fabricmc.fabric.api.event.player.UseEntityCallback","EVENT",a->{legacyCalls++;return en("net.minecraft.class_1269","field_5811");});
  registerPublicClicks();Object main=en("net.minecraft.class_1268","field_5808"),off=en("net.minecraft.class_1268","field_5810"),pass=en("net.minecraft.class_1269","field_5811"),success=en("net.minecraft.class_1269","field_21466");
  unsetColin();check(click(colin,main).equals(success)&&pending(),"actual Fabric click consumes Colin interaction and queues PQ04 instead of legacy dialog");
  check(legacyCalls==0&&Boolean.TRUE.equals(call(success,"method_23665")),"Fabric reports accepted interaction and never invokes earlier legacy default listener");
  check(Pq45Entries.PENDING.get(uuid(player)).action().equals("colin")&&Pq45Entries.PENDING.get(uuid(player)).character().equals(character(player)),"queued Colin entry retains the selected character");
  check(str(roleRecord(player,"colin"),"uuid","").equals(uuid(colin).toString()),"first real click records the clicked source UUID");noPending();
  check(click(other,main).equals(pass)&&!pending(),"unrelated guard keeps its original interaction");
  check(legacyCalls==1&&!Boolean.TRUE.equals(call(pass,"method_23665")),"unrelated NPC reaches the legacy listener with PASS");
  check(click(colin,off).equals(pass)&&!pending(),"offhand cannot queue a duplicate entry");
  score2(player,"hcq_pq04",40);check(click(colin,main).equals(pass)&&!pending(),"Colin cannot start before the elevator handoff");score2(player,"hcq_pq04",50);
  score2(player,"CAP_QUETEACTIVE",42);check(click(colin,main).equals(pass)&&!pending(),"another active quest retains control of the interaction");score2(player,"CAP_QUETEACTIVE",0);
  call(colin,"method_5780","hcm_private");check(click(colin,main).equals(pass)&&!pending(),"private actor retains its owner-checked custom action");call(colin,"method_5738","hcm_private");
  call(player,"method_5660",true);check(click(colin,main).equals(pass)&&!pending(),"sneaking retains NPC editor access");call(player,"method_5660",false);
  saveBinding(player,"colin",other);check(click(colin,main).equals(pass)&&!pending(),"an explicit different source is never overwritten by a matching name");Pq45Runtime.bind(player,"colin",uuid(colin));
  call(player,"method_5814",0.5d,24d,1.5d);check(click(colin,main).equals(pass)&&!pending(),"a click on another floor cannot open Colin remotely");
  call(player,"method_5814",0.5d,4d,1.5d);score2(player,"hcq_pq04",100);check(click(colin,main).equals(success)&&pending(),"post-quest crate delivery still enters Colin's existing logic");noPending();
  check(actions.equals(actionText(colin)),"all routing checks preserve the original NPC actions");
 }
}
