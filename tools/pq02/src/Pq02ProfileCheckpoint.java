package net.hautecapitale.quetes.sequence;

import static net.hautecapitale.quetes.sequence.Pq01Bridge.*;
import static net.hautecapitale.quetes.sequence.SequenceBridge.*;
import static net.hautecapitale.quetes.sequence.Pq02Runtime.*;

/** A periodic Nexus snapshot is not a character departure. The original writer still runs. */
public final class Pq02ProfileCheckpoint {
 private static final String BRIDGE="net.tompsen.nexuscharacters.ProfileStateBridge";
 private record Scope(Object player,java.util.UUID character,Object world){}
 private static final ThreadLocal<Scope> CHECKPOINT=new ThreadLocal<>();
 private Pq02ProfileCheckpoint(){}
 static boolean preserving(Object player){Scope scope=CHECKPOINT.get();return scope!=null&&scope.player==player&&scope.character.equals(character(player))&&scope.world==world(player);}
 public static void save(Object player,Object characterDto){
  View view=VIEWS.get(uuid(player));Pq02Entries.Pending pending=Pq02Entries.PENDING.get(uuid(player));
  java.util.UUID selected=character(player);
  boolean live=view!=null&&same(view),queued=view==null&&pending!=null&&available(player)&&pending.character().equals(selected)&&pending.world()==world(player)&&pending.stage()==stage(player);
  if((!live&&!queued)||!java.util.Objects.equals(selected,call(characterDto,"id"))){stat(BRIDGE,"save",player,characterDto);return;}
  Scope previous=CHECKPOINT.get(),scope=new Scope(player,selected,world(player));int previousLock=score(player,"CAP_PROFILE_LOCK");
  CHECKPOINT.set(scope);
  try{
   if(view!=null)savePosition(view);
   stat(BRIDGE,"save",player,characterDto);
  }finally{
   if(previous==null)CHECKPOINT.remove();else CHECKPOINT.set(previous);
   // before_save_self temporarily locks the profile. A checkpoint keeps the same live character.
   if(scope.character.equals(character(player))&&scope.world==world(player)&&score(player,"CAP_PROFILE_LOCK")!=previousLock)score2(player,"CAP_PROFILE_LOCK",previousLock);
  }
 }
}
