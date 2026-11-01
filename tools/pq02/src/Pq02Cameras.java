package net.hautecapitale.quetes.sequence;

import java.lang.reflect.Field;
import net.hautecapitale.dialogue.config.DialogueConfig;
import static net.hautecapitale.quetes.sequence.SequenceBridge.*;
import static net.hautecapitale.quetes.sequence.Pq01Bridge.*;

/** Profils b10 limités à l'ouverture de la scène privée ; aucune config n'est écrite. */
final class Pq02Cameras {
 static Object open(Object player,Object focus,boolean group,double span){
  DialogueConfig config=DialogueConfig.get();String id=uuid(focus).toString();
  DialogueConfig.Pnj previous=config.pnj.get(id),profile=new DialogueConfig.Pnj();
  if(previous!=null)try{for(Field f:DialogueConfig.Pnj.class.getFields())f.set(profile,f.get(previous));}
  catch(ReflectiveOperationException e){throw new IllegalStateException("Profil caméra PQ02",e);}
  profile.recul=group?Math.max(1.8,Math.min(4.5,1.1+span*0.35)):1.65;
  profile.decalage=group?0.18:0.55;profile.angle_visee=0.0;
  profile.hauteur_cible=group?0.0:0.85;profile.decalage_cible_y=0.0;
  profile.elevation=group?0.2:0.12;profile.transition_ms=450;
  config.pnj.put(id,profile);
  try{return stat(Pq02Runtime.DM,"ouvrir",player,focus,en("net.hautecapitale.dialogue.session.DialogueMode","CAPTURE"));}
  finally{if(previous==null)config.pnj.remove(id);else config.pnj.put(id,previous);}
 }
}
