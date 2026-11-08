package net.hautecapitale.quetes.sequence;

import java.util.*;
import static net.hautecapitale.quetes.sequence.SequenceBridge.*;
import static net.hautecapitale.quetes.sequence.Pq01Bridge.*;
import static net.hautecapitale.quetes.sequence.Pq03Runtime.*;

/** Les fonctions Minecraft imbriquées mettent leurs commandes en file : ouvrir hors de ce contexte. */
final class Pq03Entries {
 record Pending(Object player,UUID character,Object world,int stage,String action,Object session,int after) {}
 static final Map<UUID,Pending> PENDING=new LinkedHashMap<>();
 static boolean handles(String action){return Set.of("aurele","near","dispatch").contains(action);}
 static int submit(Object player,String action){
  if(!ready(player))return 0;UUID account=uuid(player);Pending old=PENDING.get(account);
  if(action.equals("aurele")||action.equals("near")){
   Object current=current(player);View view=VIEWS.get(account);
   if(current!=null&&(view==null||current!=view.session)){
    Pq03Runtime.source(player,"aurele");Object record=roleRecord(player,"aurele");String actor=String.valueOf(Pq01Client.field(current,"pnj"));
    if(record==null||!actor.equals(str(record,"uuid","")))return 0;
   }
  }
  // Une interpellation ne remplace pas un clic explicite déjà en attente.
  if(action.equals("near")&&old!=null&&!old.action.equals("near")&&old.character.equals(character(player)))return 1;
  PENDING.put(account,new Pending(player,character(player),world(player),stage(player),action,current(player),clock+1));return 1;
 }
 static void cancel(UUID account){PENDING.remove(account);}
 static void clear(){PENDING.clear();}
 static void tick(List<Object> online){
  if(PENDING.isEmpty())return;Map<UUID,Object> present=new HashMap<>();for(Object player:online)present.put(uuid(player),player);
  for(var item:new ArrayList<>(PENDING.entrySet())){UUID account=item.getKey();Pending entry=item.getValue();if(clock<entry.after)continue;
   if(!PENDING.remove(account,entry))continue;Object player=entry.player;
   if(present.get(account)!=player||world(player)!=entry.world||!entry.character.equals(character(player))||!ready(player)||stage(player)!=entry.stage||!Boolean.TRUE.equals(call(player,"method_5805"))||Boolean.TRUE.equals(call(player,"method_31481")))continue;
   if(entry.session!=null&&current(player)!=entry.session)continue;
   try{
    ensure(player);
    if(entry.action.equals("near"))near(player);
    else if(entry.action.equals("dispatch")){View v=VIEWS.get(account);if(v!=null)dispatch(v);}
    else open(player,entry.action);
   }catch(Throwable failure){
    report2(failure);View v=VIEWS.get(account);if(v!=null){closeUi(v);dispose(account);}
   }
  }
 }
}
