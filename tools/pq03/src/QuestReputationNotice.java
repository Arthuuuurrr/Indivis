package net.hautecapitale.quetes.sequence;
import java.util.Set;
import static net.hautecapitale.quetes.sequence.SequenceBridge.*;
import static net.hautecapitale.quetes.sequence.Pq01Bridge.*;
/** Notification privée, séparée des répliques capturées ; le delta reflète la borne réelle. */
final class QuestReputationNotice {
 static final String PREFIX="indivis:reputation:v1:";
 record Notice(String faction,int delta,int total){}
 static Notice parse(String text){
  if(text==null||!text.startsWith(PREFIX))return null;String[] p=text.substring(PREFIX.length()).split(":",-1);
  if(p.length!=3||!Set.of("port","coeur").contains(p[0]))return null;
  try{int delta=Integer.parseInt(p[1]),total=Integer.parseInt(p[2]);return delta==0||delta< -3||delta>3?null:new Notice(p[0],delta,total);}catch(NumberFormatException invalid){return null;}
 }
 static String encode(Notice n){return PREFIX+n.faction()+":"+n.delta()+":"+n.total();}
 static String label(Notice n){return (n.faction().equals("coeur")?"Garde du Cœur":"Garde du Port")+" : "+(n.delta()>0?"+":"")+n.delta()+" en réputation";}
 static void changed(Object player,String faction,int before,int after){
  if(before==after)return;Notice n=new Notice(faction,after-before,after);
  run(player,"tellraw @s {\"text\":\"\",\"insertion\":"+quote(encode(n))+"}");
 }
}
