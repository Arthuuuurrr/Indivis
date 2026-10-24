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
    field("voicesChecked",true);String frame=marker("c1","altheon",TOKEN,600,0);
    Pq01Pacing.Frame parsed=Pq01Pacing.parse(frame);check(parsed!=null&&parsed.token()==TOKEN,"trame valide");
    for(String bad:Arrays.asList(null,"",frame+":extra",marker("c1","geraud",TOKEN,60,0),marker("missing","altheon",TOKEN,60,0),marker("c1","altheon",1,60,0),marker("c1","altheon",2000000000,60,0),marker("c1","altheon",TOKEN,-1,0),marker("c1","altheon",TOKEN,60,12001)))check(Pq01Pacing.parse(bad)==null,"trame invalide refusée : "+bad);
    check(!Pq01Pacing.ready(parsed,1,1,true,false,false,true),"pas d'avance dans le premier tick de réception");
    check(Pq01Pacing.ready(parsed,2,1,true,false,false,true),"texte révélé sans second délai de lecture");
    check(!Pq01Pacing.ready(parsed,599,80,true,false,false,false),"lecture conservée si texte instantané");
    check(Pq01Pacing.ready(parsed,600,80,true,false,false,false),"lecture instantanée avance après son budget");
    check(!Pq01Pacing.ready(parsed,1000,80,false,false,false,true),"aucun saut de page");
    check(!Pq01Pacing.ready(parsed,1000,80,true,true,false,true),"frappe attendue");
    check(!Pq01Pacing.ready(parsed,1000,80,true,false,true,true),"voix réelle attendue");
    var voiced=Pq01Pacing.parse(marker("c1","altheon",TOKEN,600,80));
    check(!Pq01Pacing.ready(voiced,79,1,true,false,false,true),"durée audio déclarée respectée");
    check(Pq01Pacing.ready(voiced,80,1,true,false,false,true),"aucun silence supplémentaire après durée audio");
    Client c=new Client();CaptureDialogueScreen screen=(CaptureDialogueScreen)c.field_1755;
    check(message("bonjour")&&message(null),"messages ordinaires préservés");check(!message(frame),"trame invisible");
    check(DialogueClientState.name().equals("Althéon Brumeforge"),"nom mis à jour avant texte");
    screen.versionVue=0;ticks(c,100);check(c.network.commands.isEmpty(),"ancienne page ne valide jamais la nouvelle trame");
    screen.versionVue=1;screen.panneau.typing=true;ticks(c,100);check(c.network.commands.isEmpty(),"frappe réelle jamais coupée");
    screen.panneau.typing=false;ticks(c,1);check(c.network.commands.size()==1,"fin de frappe enchaîne au tick suivant");
    int cleared=DialogueClientState.cleared;ticks(c,19);check(c.network.commands.size()==1,"pas de spam entre tentatives");ticks(c,1);check(c.network.commands.size()==2,"réessai même nonce si aucun acquittement");check(DialogueClientState.cleared==cleared,"capture conservée pendant réessai");
    check(screen.panneau.name.equals("Althéon Brumeforge")&&!screen.panneau.waiting,"en-tête et panneau corrects");check(DialogueClientState.deadline()>System.currentTimeMillis(),"caméra maintenue");
    message(marker("c_menu","altheon",0,60,0));ticks(c,300);check(c.network.commands.size()==2,"menu acquitte sans progression automatique");
    message(frame);screen.panneau.last=false;ticks(c,1);check(screen.panneau.pagesAdvanced==1&&c.network.commands.size()==2,"page intermédiaire lue avance directement");ticks(c,1);check(c.network.commands.size()==3,"dernière page achevée valide sans pause");
    message(marker("c1","altheon",TOKEN,60,0));screen.panneau.typewriter=false;ticks(c,59);check(c.network.commands.size()==3,"frappe désactivée laisse le temps de lire");ticks(c,1);check(c.network.commands.size()==4,"frappe désactivée garde lecture bornée");screen.panneau.typewriter=true;
    Voices voices=new Voices();field("voices",voices);voices.active=new Object();message(frame);ticks(c,100);check(c.network.commands.size()==4,"voix active bloque");voices.active=null;voices.pending=new Object();ticks(c,100);check(c.network.commands.size()==4,"chargement audio bloque");voices.pending=null;voices.fading=new Object();ticks(c,100);check(c.network.commands.size()==4,"fin audio bloque");voices.fading=null;ticks(c,1);check(c.network.commands.size()==5,"fin audio enchaîne immédiatement");
    message(frame);DialogueClientState.active=false;ticks(c,200);check(c.network.commands.size()==5,"fermeture annule avance tardive");
    DialogueClientState.active=true;message(frame);ticks(c,2000);check(c.network.commands.size()==65,"réessais bornés à soixante");
    System.out.println("PASS "+checks+" assertions ; vrais Pq01Client/Pq01Pacing ; frappe, version de capture, voix et budget sans frappe ; UI et réseau doublés.");
  }
}
