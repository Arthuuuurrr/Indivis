package net.hautecapitale.quetes.sequence;
import java.lang.reflect.*;
import java.util.*;
import net.hautecapitale.dialogue.client.DialogueClientState;
import net.hautecapitale.dialogue.client.capture.CaptureDialogueScreen;
/** Vrai adaptateur ; réseau, panneau, entrées et rendu doublés. */
public final class Pq01ClientBench {
  static int checks;static final int TOKEN=1000000001;
  static void check(boolean ok,String name){checks++;if(!ok)throw new AssertionError(name);}
  public record Text(Style style){public Style method_10866(){return style;}}
  public record Style(String insertion){public String method_10955(){return insertion;}}
  public record Key(int code,boolean control){public int method_74228(){return code;}public boolean method_74231(){return control;}}
  public record Mouse(int button,double x,double y){public int method_74245(){return button;}public double comp_4798(){return x;}public double comp_4799(){return y;}}
  public static final class Draw {public String shown="";public int boxes;public void method_25294(int a,int b,int c,int d,int color){boxes++;}public void method_51433(CaptureDialogueScreen.Font f,String s,int x,int y,int color,boolean shadow){shown=s;}}
  public static final class Network {public final List<String> commands=new ArrayList<>();public void method_45730(String c){commands.add(c);}}
  public static final class Client {public Object field_1755=new CaptureDialogueScreen();public final Network network=new Network();public Network method_1562(){return network;}}
  public static final class Voices {private Object active,pending,fading;}
  static void field(String name,Object value)throws Exception{Field f=Pq01Client.class.getDeclaredField(name);f.setAccessible(true);f.set(null,value);}
  static String marker(String key,String role,int token,int voice){return "indivis:pq01:v1:"+key+":"+role+":"+token+":0:"+voice;}
  static boolean message(String text){return Pq01Client.message(new Text(new Style(text)),false);}
  static void ticks(Client c,int count){for(int i=0;i<count;i++)Pq01Client.tickClient(c);}
  public static void main(String[] args)throws Exception {
    field("voicesChecked",true);String frame=marker("a4","geraud",TOKEN,0);
    var parsed=Pq01Pacing.parse(frame);check(parsed!=null&&parsed.token()==TOKEN,"trame valide");
    for(String bad:Arrays.asList(null,"",frame+":extra",marker("a4","gautier",TOKEN,0),marker("missing","geraud",TOKEN,0),marker("a4","geraud",1,0),marker("a4","geraud",2000000000,0),marker("a4","geraud",TOKEN,12001)))check(Pq01Pacing.parse(bad)==null,"trame invalide refusée");
    Client c=new Client();CaptureDialogueScreen screen=(CaptureDialogueScreen)c.field_1755;
    check(message("bonjour")&&message(null),"messages ordinaires préservés");check(!message(frame),"trame invisible");
    ticks(c,2000);check(c.network.commands.isEmpty(),"Matelot et remontrance restent lisibles sans action même après 100 secondes");
    screen.versionVue=0;check(!Pq01Client.requestAdvance(screen),"ancienne capture ne peut pas avancer nouvelle page");screen.versionVue=1;
    screen.panneau.typing=true;check(!Pq01Client.key(screen,new Key(32,false)),"premier Espace laissé au moteur natif pour révéler texte");ticks(c,100);check(c.network.commands.isEmpty(),"frappe jamais validée automatiquement");
    screen.panneau.typing=false;screen.panneau.last=false;check(!Pq01Client.key(screen,new Key(32,false)),"pagination laissée au moteur natif");ticks(c,100);check(screen.panneau.pagesAdvanced==0,"aucune page tournée par minuteur");screen.panneau.last=true;
    screen.panneau.history=true;check(!Pq01Client.requestAdvance(screen),"historique ne fait pas progresser quête");screen.panneau.history=false;
    check(!Pq01Client.key(screen,new Key(72,false))&&!Pq01Client.key(screen,new Key(32,true)),"raccourcis étrangers et modifiés conservés");
    check(Pq01Client.key(screen,new Key(32,false)),"Espace demande explicitement la suite");ticks(c,1);check(c.network.commands.size()==1,"suite envoyée après action utilisateur");
    int cleared=DialogueClientState.cleared;ticks(c,19);check(c.network.commands.size()==1,"réessai borné sans spam");ticks(c,1);check(c.network.commands.size()==2&&DialogueClientState.cleared==cleared,"réessai conserve capture");
    Draw draw=new Draw();Pq01Client.hint(screen,draw);check(draw.shown.equals("Espace ou clic : suite")&&draw.boxes==1,"indication française et hauteur héritée valides");
    message(marker("c1","altheon",0,0));ticks(c,2000);check(c.network.commands.size()==2&&!Pq01Client.requestAdvance(screen),"vraies questions jamais validées par Espace");
    draw=new Draw();Pq01Client.hint(screen,draw);check(draw.shown.isEmpty(),"aucune fausse indication de suite sur un menu de réponses");
    message(frame);screen.panneau.choiceAt=0;check(!Pq01Client.mouse(screen,new Mouse(0,4,4)),"clic sur bouton fermer préservé");screen.panneau.choiceAt=-1;
    check(!Pq01Client.mouse(screen,new Mouse(1,4,4)),"clic droit non détourné");check(Pq01Client.mouse(screen,new Mouse(0,4,4)),"clic gauche sur texte demande suite");ticks(c,2);check(c.network.commands.size()==3,"clic hors boutons avance le bloc");
    message(frame);screen.panneau.typewriter=false;ticks(c,5000);check(c.network.commands.size()==3,"frappe désactivée : texte reste jusqu'à action");check(Pq01Client.key(screen,new Key(257,false)),"Entrée avance aussi sans frappe");ticks(c,1);check(c.network.commands.size()==4,"validation manuelle sans budget imposé");screen.panneau.typewriter=true;
    Voices voices=new Voices();field("voices",voices);voices.active=new Object();message(frame);check(Pq01Client.requestAdvance(screen),"demande de suite mémorisée pendant voix");ticks(c,100);check(c.network.commands.size()==4,"voix active ne peut pas être coupée");voices.active=null;voices.pending=new Object();ticks(c,100);check(c.network.commands.size()==4,"chargement audio attendu");voices.pending=null;voices.fading=new Object();ticks(c,100);check(c.network.commands.size()==4,"fin de voix attendue");voices.fading=null;ticks(c,1);check(c.network.commands.size()==5,"demande déjà faite exécutée à la fin audio");
    message(marker("a4","geraud",TOKEN,80));Pq01Client.requestAdvance(screen);ticks(c,79);check(c.network.commands.size()==5,"durée de voix déclarée respectée");ticks(c,1);check(c.network.commands.size()==6,"suite possible après durée audio");
    message(frame);Pq01Client.requestAdvance(screen);DialogueClientState.active=false;ticks(c,200);check(c.network.commands.size()==6&&!Pq01Client.requestAdvance(screen),"fermeture annule demandes tardives");
    DialogueClientState.active=true;message(frame);Pq01Client.requestAdvance(screen);ticks(c,2000);check(c.network.commands.size()==66,"soixante tentatives maximum par demande explicite");
    message(frame);Pq01Client.requestAdvance(screen);DialogueClientState.sessionId++;Pq01Client.state("pnjNom","Autre PNJ");ticks(c,100);
    check(c.network.commands.size()==66&&!Pq01Client.requestAdvance(screen)&&DialogueClientState.name().equals("Autre PNJ"),"autre session : aucun choix tardif ni nom PQ01 imposé");
    draw=new Draw();Pq01Client.hint(screen,draw);check(draw.shown.isEmpty(),"aucune indication PQ01 sur une autre quête");
    System.out.println("PASS "+checks+" assertions ; vrais Pq01Client/Pq01Pacing, lecture manuelle, clavier/souris, pagination, capture, voix et hint ; affichage/réseau doublés.");
  }
}
