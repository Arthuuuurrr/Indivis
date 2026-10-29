package net.hautecapitale.quetes.sequence;
import java.util.UUID;

/** Contrat borné de disponibilité de la capture, commun au serveur et au client. */
final class Pq02Focus {
 static final String PREFIX="indivis:pq02:focus:v1:";
 record Request(int session,UUID actor,int token){}
 static Request parse(String text){
  if(text==null||!text.startsWith(PREFIX))return null;String[] p=text.substring(PREFIX.length()).split(":",-1);
  if(p.length!=3)return null;
  try{int id=Integer.parseInt(p[0]),token=Integer.parseInt(p[2]);if(id<0||token<=0)return null;return new Request(id,UUID.fromString(p[1]),token);}catch(IllegalArgumentException e){return null;}
 }
 static String encode(Request r){return PREFIX+r.session()+":"+r.actor()+":"+r.token();}
}
