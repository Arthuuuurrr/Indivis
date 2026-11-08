package net.hautecapitale.quetes.sequence;
import java.nio.file.*;import java.util.*;import com.google.gson.*;
import net.fabricmc.api.ModInitializer;
import net.hautecapitale.quetes.sequence.Pq01Motion.Point;
import static net.hautecapitale.quetes.sequence.SequenceBridge.*;
import static net.hautecapitale.quetes.sequence.Pq01Bridge.*;
import static net.hautecapitale.quetes.sequence.Pq02WorldBench.*;
/** Deuxième processus Minecraft sur le monde sauvegardé, sans recréer les PNJ/repères. */
public final class Pq03RestartBench implements ModInitializer {
 static int age,count;static Object owner;static JsonObject saved;static boolean ended;
 static void check(boolean value,String text){if(!value)throw new AssertionError(text);count++;System.out.println("PQ03 RESTART "+text);}
 public void onInitialize(){
  event("net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback","EVENT",x->{stub(x[0],"name");stub(x[0],"capidentity");stub(x[0],"mmospawn");return null;});
  Pq02FullSystemBench.endPhase(true,x->{if(srv!=null&&!ended)((List<Object>)call(call(srv,"method_3760"),"method_14571")).addAll(players);return null;});
  event("net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents$ServerStarted","SERVER_STARTED",x->{try{srv=x[0];w=call(srv,"method_30002");saved=JsonParser.parseString(Files.readString(Path.of("pq03-reboot.json"))).getAsJsonObject();System.setProperty("pq03.reboot.owner",saved.get("owner").getAsString());cmd("forceload add -16 -16 48 64");owner=player("PQ02_Observer",UUID.fromString(saved.get("character").getAsString()));place();net.tompsen.nexuscharacters.ProfileStateBridge.loadStrict((net.minecraft.class_3222)owner,Pq02ProfileCheckpointBench.dto(owner));}catch(Throwable e){fail(e);}return null;});
  Pq02FullSystemBench.endPhase(false,x->{try{if(srv==null||ended)return null;age++;t++;if(age>180)throw new AssertionError("restart timeout");if(age==60){
   var v=Pq03Runtime.VIEWS.get(uuid(owner));check(v!=null,"view restored automatically after actual server restart");check(Pq03Runtime.stage(owner)==60&&v.waypoint==13,"canonical depth phase and N waypoint restored");check(!v.walking&&!v.ui,"saved discussion remains paused without a forced camera");Point at=pos(v.actors.get("aurele").entity,w);check(Math.abs(at.x()-saved.get("x").getAsDouble())<0.01&&Math.abs(at.y()-saved.get("y").getAsDouble())<0.01&&Math.abs(at.z()-saved.get("z").getAsDouble())<0.01,"exact stored actor position survived the process boundary");check(score(owner,"CAP_PQ3_DA")==1&&score(owner,"CAP_PQ3_DB")==0&&score(owner,"CAP_PQ3_RA")==1&&score(owner,"CAP_PQ3_IMPR")==1&&score(owner,"REP_GARDECOEUR")==1&&score(owner,"CAP_PQ3_REW")==0,"immediate reputation, impression and paid decision survive restart without replay");
   Pq03WorldBench.interact(owner);v=Pq03Runtime.VIEWS.get(uuid(owner));check(v.ui&&v.node.key.equals("b_rules")&&v.session==Pq03Runtime.current(owner),"native private action reopens the exact remembered dialogue");Pq03WorldBench.close3(owner);check(v.walking&&v.motion!=null&&v.motion.target().distance2(Pq03Runtime.routePoint(owner,"o"))<0.001,"closing restored conversation resumes N-O segment");check(Pq03Runtime.ERRORS.isEmpty(),"no PQ03 runtime exception across restart");
   long actors=0;for(Object e:((net.minecraft.class_3218)w).method_27909())if(tags(e).contains(Pq03Runtime.TAG)&&tags(e).contains("hcm_pq3_aurele"))actors++;check(actors==1,"stale private NPCs from first process did not reappear");
   Files.writeString(Path.of("pq03-restart-result.txt"),"PASS "+count+" actual restart assertions\nProduction JAR unchanged; synthetic non-OP account and real Nexus profile reloaded in a second Fabric process.\n");ended=true;cmd("stop");
  }}catch(Throwable e){try{Files.writeString(Path.of("pq03-restart-result.txt"),"FAIL "+e+"\n");}catch(Exception ignored){}ended=true;fail(e);}finally{if(srv!=null)((List<?>)call(call(srv,"method_3760"),"method_14571")).clear();}return null;});
 }
 static void place(){call(owner,"method_5814",saved.get("x").getAsDouble()+1.5,saved.get("y").getAsDouble(),saved.get("z").getAsDouble());}
}
