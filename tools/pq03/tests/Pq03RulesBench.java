package net.hautecapitale.quetes.sequence;
import java.util.*;
/** Contrats de route, décisions, cadrage et vraie politique Nexus OPT3. */
public final class Pq03RulesBench {
 static int checks;
 static void ok(boolean b,String s){checks++;if(!b)throw new AssertionError(s);}
 public static void main(String[] args){
  int min=100,max=-100,count=0;
  for(int a=0;a<=3;a++)for(int b=1;b<=4;b++){
   int r=Pq03Rules.result(a,b);ok((a==0?0:Pq03Rules.decisionDelta(false,a))+Pq03Rules.decisionDelta(true,b)==r,"somme des effets immédiats identique au barème total");min=Math.min(min,r);max=Math.max(max,r);count++;
   ok(r>=-1&&r<=2,"barème borné pour chaque combinaison");
   ok(Pq03Rules.greeting(r).equals(r>0?"post":r<0?"post_unfriendly":"post_neutral"),"souvenir cohérent avec l'impression");
  }
  ok(count==16&&min==-1&&max==2,"16 combinaisons, extrema atteints");
  ok(Pq03Rules.result(0,2)==0&&Pq03Rules.result(3,3)==0,"route et critique respectueuse sans bonus automatique");
  for(int[] bad:new int[][]{{-1,1},{4,1},{1,0},{1,5}}){boolean refused=false;try{Pq03Rules.result(bad[0],bad[1]);}catch(IllegalArgumentException e){refused=true;}ok(refused,"choix invalide rejeté");}
  ok(Pq03Rules.route(30).equals(List.of(0,1,2,3,4,5,6,7,8)),"route commerces A-I");
  ok(Pq03Rules.route(40).equals(List.of(8,7,6,5,4,3,2,1,0)),"retour physique I-A");
  ok(Pq03Rules.route(60).equals(List.of(0,9,10,11,12,13,14,15,16,17,18,19,20,21)),"route directe A-J-V");
  var reversed=new ArrayList<>(Pq03Rules.route(60));Collections.reverse(reversed);ok(Pq03Rules.retreat().equals(reversed),"départ final V-J-A");
  for(String score:Pq03Rules.SCORES)ok(score.length()<=16&&net.tompsen.nexuscharacters.HCProfilePolicyV0621.shouldPersist(score),"Nexus OPT3 conserve "+score);
  for(String score:List.of("hcm_pq3_ui","Pq03Choix","hcm_pq3_check","CAP_QDIALOG_OWNER"))ok(!net.tompsen.nexuscharacters.HCProfilePolicyV0621.shouldPersist(score),"Nexus exclut le jeton temporaire "+score);
  for(var notice:List.of(new QuestReputationNotice.Notice("coeur",1,12),new QuestReputationNotice.Notice("port",-1,-7))){ok(QuestReputationNotice.parse(QuestReputationNotice.encode(notice)).equals(notice),"codec de notification positif/négatif et total exact");}
  for(String text:List.of("indivis:reputation:v1:coeur:0:10","indivis:reputation:v1:inconnu:1:1","indivis:reputation:v1:port:999:1","indivis:reputation:v1:port:1:texte"))ok(QuestReputationNotice.parse(text)==null,"notification invalide ignorée");
  var frame=Pq03Pacing.parse("indivis:pq03:v1:c03:aurele:1000000014:0:80");ok(frame!=null,"trame valide");
  ok(!Pq03Pacing.ready(frame,100,2,true,false,true,false),"attente de la voix");
  ok(!Pq03Pacing.ready(frame,100,2,true,true,false,false),"attente du texte");
  ok(!Pq03Pacing.ready(frame,100,2,false,false,false,false),"attente de la dernière page");
  ok(!Pq03Pacing.ready(frame,79,2,true,false,false,false),"durée vocale respectée");
  ok(Pq03Pacing.ready(frame,80,2,true,false,false,false),"avance explicite après lecture");
  for(String value:List.of("indivis:pq02:v1:c03:aurele:1000000014:0:0","indivis:pq03:v1:c03:leovic:1000000014:0:0","indivis:pq03:v1:c03:aurele:2147483647:0:0","indivis:pq03:v1:c03:aurele:1000000014:0:-1"))ok(Pq03Pacing.parse(value)==null,"trame étrangère ou corrompue rejetée");
  ok(!Pq03Rules.validNode("c03",60)&&Pq03Rules.validNode("c03",90),"scène finale seulement à V");
  ok(!Pq03Rules.validNode("b_market",60)&&Pq03Rules.validNode("b_market",30),"commerces absents de la route directe");
  ok(!Pq03Rules.validNode("b_reunion",40)&&Pq03Rules.validNode("b_reunion",50),"retrouvailles après arrivée physique à A");
  ok(!Pq03Rules.validNode("post",90)&&Pq03Rules.validNode("post",100),"souvenir après fin seulement");
  ok(Pq03Transitions.agreedExit("event:return",30).equals("return")&&Pq03Transitions.agreedExit("event:return",60).isEmpty(),"accord de départ limité à son état");
  ok(Pq03Transitions.playerClosed("ESC")&&!Pq03Transitions.playerClosed("REMPLACEE")&&!Pq03Transitions.playerClosed("DECONNEXION"),"fermeture humaine distincte du remplacement ou de la déconnexion");
  System.out.println("PASS "+checks+" assertions; 16 decision combinations, exact routes, actual Nexus OPT3 policy, pacing and stage gates.");
 }
}
