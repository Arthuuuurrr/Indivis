package net.hautecapitale.quetes.sequence;
import java.lang.reflect.*;
import java.util.*;
import net.hautecapitale.dialogue.client.DialogueClientState;
import net.hautecapitale.dialogue.client.capture.CaptureDialogueScreen;
/** Vrai adaptateur et parseur du JAR ; écran, réseau et voix doublés. */
public final class Pq01ClientBench {
  static int checks;static final int TOKEN=1000000001;
  static void check(boolean ok,String name){checks++;if(!ok)throw new AssertionError(name);}
  public record Text(Style style){public Style method_10866(){return style;}}
  public record Style(String insertion){public String method_10955(){return insertion;}}
  public static final class Network {public final List<String> commands=new ArrayList<>();public void method_45730(String c){commands.add(c);}}
  public static final class Client {public Object field_1755=new CaptureDialogueScreen();public final Network network=new Network();public Network method_1562(){return network;}}
  public static final class Voices {private Object active,pending,fading;}
  static void field(String name,Object value)throws Exception{Field f=Pq01Client.class.getDeclaredField(name);f.setAccessible(true);f.set(null,value);}
  static String marker(String key,String role,int token,int read,int voice){return "indivis:pq01:v1:"+key+":"+role+":"+token+":"+read+":"+voice;}
  static boolean message(String text){return Pq01Client.message(new Text(new Style(text)),false);}
  static void ticks(Client c,int count){for(int i=0;i<count;i++)Pq01Client.tickClient(c);}
  public static void main(String[] args)throws Exception {
    field("voicesChecked",true);
    String frame=marker("c1","altheon",TOKEN,60,0);
    Pq01Pacing.Frame parsed=Pq01Pacing.parse(frame);check(parsed!=null&&parsed.token()==TOKEN,"trame valide");
    for(String bad:Arrays.asList(null,"",frame+":extra",marker("c1","geraud",TOKEN,60,0),marker("missing","altheon",TOKEN,60,0),marker("c1","altheon",1,60,0),marker("c1","altheon",2000000000,60,0),marker("c1","altheon",TOKEN,-1,0),marker("c1","altheon",TOKEN,60,12001)))check(Pq01Pacing.parse(bad)==null,"trame invalide refusée : "+bad);
    check(!Pq01Pacing.ready(parsed,59,80,true,false,false),"délai de lecture respecté");
    check(!Pq01Pacing.ready(parsed,100,39,true,false,false),"dernière page visible assez longtemps");
    check(!Pq01Pacing.ready(parsed,100,80,false,false,false),"aucun saut de page");
    check(!Pq01Pacing.ready(parsed,100,80,true,true,false),"machine à écrire attendue");
    check(!Pq01Pacing.ready(parsed,100,80,true,false,true),"voix active attendue");
    check(Pq01Pacing.ready(parsed,100,80,true,false,false),"page achevée peut avancer");
    Client c=new Client();CaptureDialogueScreen screen=(CaptureDialogueScreen)c.field_1755;
    check(message("bonjour"),"message ordinaire préservé");check(message(null),"style sans insertion préservé");
    check(!message(frame),"trame technique invisible");check(DialogueClientState.name().equals("Althéon Brumeforge"),"nom de capture mis à jour avant les répliques");
    ticks(c,59);check(c.network.commands.isEmpty(),"aucune commande avant lecture");ticks(c,1);
    check(c.network.commands.equals(List.of("trigger QuestChoix set "+TOKEN)),"choix nonce envoyé une seule fois");ticks(c,200);check(c.network.commands.size()==1,"aucun double envoi");
    check(screen.panneau.name.equals("Althéon Brumeforge")&&!screen.panneau.waiting,"en-tête correct et attente vide masquée");
    check(DialogueClientState.deadline()>System.currentTimeMillis(),"fenêtre caméra maintenue pendant la scène");
    message(marker("c_menu","altheon",0,60,0));ticks(c,300);check(c.network.commands.size()==1,"menu de questions jamais validé automatiquement");
    message(frame);screen.panneau.typing=true;ticks(c,100);check(c.network.commands.size()==1,"texte en frappe non sauté");screen.panneau.typing=false;screen.panneau.last=false;ticks(c,100);check(c.network.commands.size()==1,"page intermédiaire non sautée");screen.panneau.last=true;ticks(c,40);check(c.network.commands.size()==2,"validation après dernière page complète");
    Voices voices=new Voices();field("voices",voices);voices.active=new Object();message(frame);ticks(c,100);check(c.network.commands.size()==2,"lecture audio réelle active bloque l’avance");voices.active=null;voices.pending=new Object();ticks(c,100);check(c.network.commands.size()==2,"chargement audio bloque l’avance");voices.pending=null;voices.fading=new Object();ticks(c,100);check(c.network.commands.size()==2,"fin audio attendue");voices.fading=null;ticks(c,1);check(c.network.commands.size()==3,"avance après fin audio");
    message(frame);DialogueClientState.active=false;ticks(c,200);check(c.network.commands.size()==3,"fermeture empêche toute commande tardive");
    System.out.println("PASS "+checks+" assertions ; vrais Pq01Client/Pq01Pacing ; UI, réseau et champs de voix doublés. Aucun rendu client réel.");
  }
}
