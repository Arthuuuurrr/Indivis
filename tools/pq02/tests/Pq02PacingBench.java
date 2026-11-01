package net.hautecapitale.quetes.sequence;

import java.util.*;
import net.hautecapitale.quetes.sequence.Pq01Motion.Point;
import net.hautecapitale.dialogue.config.DialogueConfig;
import net.hautecapitale.dialogue.session.DialogueSession;
import static net.hautecapitale.quetes.sequence.SequenceBridge.*;
import static net.hautecapitale.quetes.sequence.Pq01Bridge.*;
import static net.hautecapitale.quetes.sequence.Pq02Runtime.*;
import static net.hautecapitale.quetes.sequence.Pq02WorldBench.*;

/** HF9 : vraies pauses D/H, reprise disque en attente, profils b10 et cadence. */
final class Pq02PacingBench {
 static boolean behind,pushed,pendingReloaded,pendingChecked;static int reloadAt,joins;
 static Point pendingPosition;static final Map<String,Hold> holds=new LinkedHashMap<>();
 static final Map<Object,Integer> lastWaypoint=new IdentityHashMap<>();
 record Hold(int at,Point position,Object actor,Object session){}
 static void check(boolean value,String label){ok(value,"HF9 pacing: "+label);}
 static void rules(){
  class Port implements Pq02Motion.Port {
   Point at=new Point("minecraft:overworld",0,64,0);boolean running,arrived,failed,stopOnArrival;int moves,stops;
   public boolean valid(){return true;}public Point position(){return at;}public boolean navigate(Point p){moves++;return false;}
   public boolean navigating(){return running;}public void stop(){stops++;}public void arrived(){arrived=true;}public void failed(String reason){failed=true;}
   public boolean stopOnArrival(){return stopOnArrival;}
  }
  Port port=new Port();Pq02Motion motion=new Pq02Motion(port,new Point("minecraft:overworld",5,64,0),200);motion.start();
  for(int i=0;i<3;i++)motion.tick();check(port.moves==1,"no pathfinding request every tick");motion.tick();check(port.moves==2,"inactive path retries in four ticks instead of twenty");
  for(int i=0;i<16;i++)motion.tick();check(port.moves==6,"retries stay limited to five per second");
  port.running=true;for(int i=0;i<20;i++)motion.tick();check(port.moves==6,"active path is never recalculated periodically");
  port.at=new Point("minecraft:overworld",5,64,0);motion.tick();check(port.arrived&&port.stops==0,"intermediate arrival preserves navigation and momentum");
  port=new Port();port.stopOnArrival=true;motion=new Pq02Motion(port,new Point("minecraft:overworld",5,64,0),200);motion.start();port.at=new Point("minecraft:overworld",5,64,0);motion.tick();check(port.arrived&&port.stops==1,"lore and endpoint arrivals stop the actor");
  port=new Port();motion=new Pq02Motion(port,new Point("minecraft:overworld",5,64,0),200);motion.start();motion.pause();for(int i=0;i<300;i++)motion.tick();check(port.stops==1&&port.moves==1&&!port.arrived&&!port.failed,"pause cancels repathing and timeout");
 }
 static void camera(Object p,boolean group){
  View v=v(p);DialogueSession session=(DialogueSession)v.session;var profile=session.profil;
  check(Math.abs(profile.visee(3))<0.001,"interview camera aims at its target without lateral yaw bias");
  check(profile.transitionMs()==450,"interview camera retains a gradual native transition");
  if(group){var a=pos(v.actors.get("leovic").entity,w);var b=pos(v.actors.get("magistrat").entity,w);var focus=pos(v.anchor,w);
   check(Math.abs(focus.x()-(a.x()+b.x())/2)<0.001&&Math.abs(focus.z()-(a.z()+b.z())/2)<0.001&&Math.abs(focus.y()-(a.y()+b.y())/2-1.55)<0.001,"group focus centers both NPCs at face height");
   check(profile.hauteurCible()==0&&profile.decalage(3)<0.2&&profile.recul(3)>=1.8,"native group profile uses an exact centered wider framing");
  }else check(Math.abs(profile.decalage(3)-0.55)<0.001&&profile.hauteurCible()>0.8,"magistrate shot retains face framing with a smaller shoulder offset");
  check(!DialogueConfig.get().pnj.containsKey(uuid(v.focus).toString()),"temporary camera profile leaves no persistent UUID override");
  if(group){Point at=pos(p,w),left=pos(v.actors.get("leovic").entity,w),right=pos(v.actors.get("magistrat").entity,w);double span=Math.sqrt(left.distance2(right));
   call(p,"method_5814",left.x()+(left.x()-right.x())/span*(cameraRange()-0.4),left.y(),left.z()+(left.z()-right.z())/span*(cameraRange()-0.4));
   check(!inCameraRange(v,v.anchor)&&inCameraRange(v,v.actors.get("leovic").entity),"fixture places group anchor outside range while guide remains reachable");
   Object fallback=focus(v,v.node);check(fallback==v.actors.get("leovic").entity&&inCameraRange(v,fallback),"group camera falls back to a reachable private speaker");
   call(p,"method_5814",at.x(),at.y(),at.z());focus(v,v.node);
  }
 }
 static void tick()throws Exception{
  if(!pushed&&v(b)!=null&&stage(b)==40&&v(b).waypoint==2){pushed=true;behind=true;Point at=pos(v(b).actors.get("leovic").entity,w);call(b,"method_5814",at.x()+8,at.y(),at.z()+2);}
  if(behind&&v(b)!=null&&v(b).waypoint==3&&!pendingReloaded){View v=v(b);pendingPosition=pos(v.actors.get("leovic").entity,w);
   check(!v.walking&&!v.ui&&v.lorePending==3,"guide waits at D without opening a distant camera");
   check(integer(characterScene(b,character(b)),"pending_lore",-1)==3,"unopened lore pause is stored with the character scene");
   // Like A, this synthetic connection needs server permission for the supplied
   // Nexus before_save_self hook; no permission substitutes enter the release.
   call(call(srv,"method_3760"),"method_14582",make("net.minecraft.class_11560",call(b,"method_7334")));
   var dto=Pq02ProfileCheckpointBench.dto(b);net.tompsen.nexuscharacters.ProfileStateBridge.save((net.minecraft.class_3222)b,dto);
   check(v(b)==null,"real final Nexus save retires the pending scene");net.tompsen.nexuscharacters.ProfileStateBridge.loadStrict((net.minecraft.class_3222)b,dto);pendingReloaded=true;reloadAt=t;
  }
  if(behind&&pendingReloaded&&t-reloadAt>=40){View v=v(b);check(v!=null&&v.lorePending==3&&v.waypoint==3&&!v.walking&&!v.ui,"actual Nexus reload restores the pending D pause without starting it from a distance");
   check(pos(v.actors.get("leovic").entity,w).distance2(pendingPosition)<0.01,"pending pause reload restores the actor position");
   Point at=pos(v.actors.get("leovic").entity,w);call(b,"method_5814",at.x(),at.y(),at.z()+2);behind=false;pendingChecked=true;
  }
  for(Object p:List.of(a,b)){View v=v(p);if(v==null||stage(p)!=40)continue;
   Integer previous=lastWaypoint.put(p,v.waypoint);if(previous!=null&&previous!=v.waypoint&&v.waypoint!=3&&v.waypoint!=8&&v.walking){check(v.motion!=null,"next real leg starts in the arrival tick at "+ROUTE.get(v.waypoint));if(joins==0)check(Math.abs(((Number)get(call(v.actors.get("leovic").entity,"getGroundPathNavigation"),"field_6668")).doubleValue()-1.2)<0.001,"real native navigation uses speed 1.2 instead of 0.8");joins++;}
   int point=v.waypoint;if((point!=3&&point!=8)||!v.ui||v.node==null||!v.node.key.equals(point==3?"t01":"t04"))continue;
   String key=uuid(p)+":"+point;Hold hold=holds.get(key);
   if(hold==null){settle(p);check(!v.walking&&v.motion==null&&score(p,"CAP_PQ2_PAUSE")==1,"automatic lore dialog stops actual navigation at "+ROUTE.get(point));
    check(v.choices.stream().anyMatch(c->c.target.equals("t_resume")),"lore pause offers an explicit route-resume answer");
    check(v.lorePending==-1&&integer(characterScene(p,character(p)),"pending_lore",-2)==-1,"shown pause is acknowledged and cannot repeat on reload");
    holds.put(key,new Hold(t,pos(v.actors.get("leovic").entity,w),v.actors.get("leovic").entity,v.session));continue;
   }
   if(hold.at()<0||t-hold.at()<120)continue;
   check(pos(hold.actor(),w).distance2(hold.position())<0.001&&v.session==hold.session(),"lore dialog remains readable and stationary for six real seconds at "+ROUTE.get(point));
   int rep=score(p,"REP_GARDEPORT"),gift=score(p,"CAP_LEOVIC_GIFT");Pq02ProfileCheckpointBench.snapshot(p);check(v(p)==v&&v.ui&&v.session==hold.session(),"periodic Nexus save preserves the lore pause");
   if(p==a)Pq02HandoffBench.nativeClose(p,"ESC");else {Pq02EndToEndBench.go(p,"t_resume");pick(p,14);}
   check(v.walking&&!v.ui&&v.waypoint>=point,"Escape or explicit reply resumes beyond its existing lore point");
   check(score(p,"REP_GARDEPORT")==rep&&score(p,"CAP_LEOVIC_GIFT")==gift,"lore pause itself gives no reward or reputation");
   holds.put(key,new Hold(-1,hold.position(),hold.actor(),hold.session()));
  }
 }
 static void finished(){check(holds.size()==4&&holds.values().stream().allMatch(h->h.at()<0),"both players passed both automatic lore pauses exactly once");check(pendingChecked,"pending pause exercised a real Nexus disk reload");check(joins>=8,"ordinary pathfinding points were chained without waiting an extra tick");}
}
