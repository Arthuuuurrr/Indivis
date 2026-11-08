package net.hautecapitale.quetes.sequence;
import static net.hautecapitale.quetes.sequence.SequenceBridge.call;
import static net.hautecapitale.quetes.sequence.Pq01Bridge.*;
/** Bandeau court au-dessus de la scène, sans ajout à l'historique du PNJ. */
final class QuestReputationNoticeClient {
 static QuestReputationNotice.Notice notice;static long until;
 static boolean receive(String insertion){
  if(insertion==null||!insertion.startsWith(QuestReputationNotice.PREFIX))return false;
  QuestReputationNotice.Notice n=QuestReputationNotice.parse(insertion);
  if(n!=null&&active()){notice=n;until=System.currentTimeMillis()+4000L;}return true;
 }
 static boolean active(){return Boolean.TRUE.equals(stat("net.hautecapitale.dialogue.client.DialogueClientState","captureActive"));}
 static void clear(){notice=null;until=0;}
 static void tick(){if(!active()||until<=System.currentTimeMillis())clear();}
 static void draw(Object screen,Object draw){
  if(notice==null||!active()||until<=System.currentTimeMillis()||!Pq03Client.captureScreen(screen))return;
  Object font=Pq03Client.field(Pq03Client.field(screen,"panneau"),"textRenderer");String text=QuestReputationNotice.label(notice);
  int width=((Number)call(font,"method_1727",text)).intValue(),screenWidth=((Number)Pq03Client.field(screen,"field_22789")).intValue();
  int x=Math.max(8,(screenWidth-width)/2),y=18;
  call(draw,"method_25294",x-5,y-4,x+width+5,y+13,0xC0181818);
  call(draw,"method_51433",font,text,x,y,notice.delta()>0?0xFF80E59A:0xFFFF8585,false);
 }
}
