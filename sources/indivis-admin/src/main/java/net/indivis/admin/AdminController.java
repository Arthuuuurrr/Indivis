package net.indivis.admin;
import java.time.*;
import java.util.*;
import java.util.function.LongSupplier;

/** Ciblage et confirmations contrôlés côté serveur, indépendants du rendu Minecraft. */
public final class AdminController {
  public record Person(UUID id,String login,UUID characterId,String rpName,boolean nexus,boolean ready,Integer epoch,Object connection) {
    public String label(){return login+(nexus?" — "+(rpName==null?"aucun personnage actif":rpName):"");}
    public boolean same(Person p){return p!=null&&id.equals(p.id)&&Objects.equals(characterId,p.characterId)&&nexus==p.nexus&&Objects.equals(epoch,p.epoch)&&connection==p.connection;}
  }
  public interface Port {
    boolean authorized();Person actor();List<Person> players();Person find(UUID id);
    Map<String,Integer> scores(Person target,Collection<String> keys);
    Set<String> functions();void show(Map<String,Object> dialog);void close();void notify(String message);
    void invoke(Person target,String function);
    void audit(Person target,Catalog.Action action,Map<String,Integer> before,Map<String,Integer> after,String result);
  }
  private enum Kind {PLAYERS,SELECT,MAIN,CATEGORY,PREVIEW,APPLY,CLOSE}
  private record Event(Kind kind,Person target,String category,int page,Catalog.Action action,Map<String,Integer> expected) {}
  private static final class View {
    final Person actor,target;final long expiry;final Map<String,Event> buttons=new HashMap<>();
    View(Person a,Person t,long e){actor=a;target=t;expiry=e;}
  }
  private final Map<UUID,View> views=new HashMap<>();
  private final LongSupplier clock;
  public AdminController(){this(System::nanoTime);}
  public AdminController(LongSupplier clock){this.clock=clock;}
  public void clear(){views.clear();}
  private boolean authorize(Port p) {
    if(p.authorized())return true;
    views.remove(p.actor().id);p.notify("Administration réservée aux opérateurs de niveau 2 ou supérieur.");return false;
  }
  private View start(Port p,Person target) {
    long now=clock.getAsLong();views.entrySet().removeIf(e->e.getValue().expiry<now);
    View v=new View(p.actor(),target,now+Duration.ofMinutes(5).toNanos());views.put(v.actor.id,v);return v;
  }
  public void open(Port p){if(authorize(p))players(p,0);}
  public void selectByName(Port p,String name) {
    if(!authorize(p))return;
    Person target=p.players().stream().filter(t->t.login.equalsIgnoreCase(name)).findFirst().orElse(null);
    if(target==null){p.notify("Ce joueur n’est pas connecté.");players(p,0);}else main(p,target,"Joueur sélectionné.");
  }
  public void click(Port p,String token) {
    if(!authorize(p))return;
    View v=views.get(p.actor().id);
    if(v==null||v.expiry<clock.getAsLong()||!v.actor.same(p.actor())) {
      views.remove(p.actor().id);p.notify("Cette session a expiré ou le personnage administrateur a changé. Rouvrez /indivisadmin.");p.close();return;
    }
    Event event=v.buttons.remove(token);
    if(event==null){p.notify("Bouton déjà utilisé ou issu d’une ancienne fenêtre. Aucune action exécutée.");return;}
    // Les autres boutons du même écran ne peuvent pas rejouer une action après la première.
    v.buttons.clear();
    if(v.target!=null&&!v.target.same(p.find(v.target.id))) {
      p.notify("Le joueur ciblé s’est reconnecté ou a changé de personnage. Sélectionnez-le de nouveau.");players(p,0);return;
    }
    switch(event.kind) {
      case CLOSE->{views.remove(p.actor().id);p.close();}
      case PLAYERS->players(p,event.page);
      case SELECT->{Person current=p.find(event.target.id);if(!event.target.same(current)){p.notify("La cible a changé. Sélectionnez-la de nouveau.");players(p,0);}else main(p,current,"");}
      case MAIN->main(p,event.target,"");
      case CATEGORY->category(p,event.target,event.category);
      case PREVIEW->preview(p,event.target,event.action);
      case APPLY->apply(p,event.target,event.action,event.expected);
    }
  }
  private Map<String,Object> button(View v,String label,Event event) {return button(v,label,"",event);}
  private Map<String,Object> button(View v,String label,String detail,Event event) {
    String token=UUID.randomUUID().toString().replace("-","");v.buttons.put(token,event);
    Map<String,Object> b=new LinkedHashMap<>();b.put("label",label);b.put("width",200);
    if(!detail.isBlank())b.put("tooltip",detail);
    b.put("action",Map.of("type","run_command","command","/indivisadmin ui "+token));return b;
  }
  private Event event(Kind k,Person t){return new Event(k,t,"",0,null,null);}
  private Event cat(Person t,String category){return new Event(Kind.CATEGORY,t,category,0,null,null);}
  private Map<String,Object> dialog(String title,List<String> messages,List<Map<String,Object>> buttons,Map<String,Object> exit,int columns) {
    List<Map<String,Object>> body=new ArrayList<>();
    for(String text:messages)if(!text.isBlank())body.add(Map.of("type","minecraft:plain_message","contents",text,"width",420));
    Map<String,Object> d=new LinkedHashMap<>();d.put("type","minecraft:multi_action");d.put("title",title);
    d.put("pause",false);d.put("can_close_with_escape",true);d.put("after_action","wait_for_response");
    d.put("body",body);d.put("actions",buttons);d.put("columns",columns);d.put("exit_action",exit);return d;
  }
  private void players(Port p,int page) {
    View v=start(p,null);List<Person> all=new ArrayList<>(p.players());all.sort(Comparator.comparing(Person::login,String.CASE_INSENSITIVE_ORDER));
    int pages=Math.max(1,(all.size()+9)/10);page=Math.max(0,Math.min(page,pages-1));List<Map<String,Object>> b=new ArrayList<>();
    for(Person t:all.subList(page*10,Math.min(all.size(),page*10+10)))b.add(button(v,t.label(),t.ready?"Gérer ce joueur et son personnage actif.":"Profil en chargement ou aucun personnage actif : lecture seule.",event(Kind.SELECT,t)));
    if(page>0)b.add(button(v,"Page précédente",new Event(Kind.PLAYERS,null,"",page-1,null,null)));
    if(page+1<pages)b.add(button(v,"Page suivante",new Event(Kind.PLAYERS,null,"",page+1,null,null)));
    b.add(button(v,"Actualiser les joueurs",event(Kind.PLAYERS,null)));
    p.show(dialog("Indivis — Administration",List.of("Sélectionnez un joueur connecté.","Page "+(page+1)+" / "+pages+" — "+all.size()+" joueur(s)"),b,button(v,"Fermer",event(Kind.CLOSE,null)),1));
  }
  private List<String> header(Port p,Person target) {
    Map<String,Integer> s=p.scores(target,Catalog.SCORES);List<String> h=new ArrayList<>();
    h.add("Compte : "+target.login+"\nPersonnage : "+(target.nexus?(target.rpName==null?"aucun personnage actif":target.rpName):"profil du compte"));
    h.add("Rang : "+Catalog.rank(s.get("CAP_RANGSOCIAL"))+"\nCriminalité : "+display(s.get("CAP_CRIME"))+" / 100");
    if(!target.ready)h.add("Profil indisponible ou en cours de chargement. Modifications désactivées.");
    return h;
  }
  private static String display(Integer n){return n==null?"—":n.toString();}
  private List<Catalog.Action> available(Port p,String category) {
    Set<String> present=p.functions();return Catalog.ACTIONS.stream().filter(a->a.category().equals(category)&&present.contains(a.function())).toList();
  }
  private void main(Port p,Person target,String message) {
    Person current=p.find(target.id);if(!target.same(current)){players(p,0);return;}target=current;
    View v=start(p,target);List<String> h=header(p,target);if(!message.isBlank())h.add(message);
    Map<String,Integer> s=p.scores(target,Catalog.SCORES);
    h.add("Amende : "+display(s.get("CAP_AMENDE"))+" | État judiciaire : "+display(s.get("CAP_ETAT"))+" | Comparution : "+display(s.get("CAP_COMPARUTION")));
    List<Map<String,Object>> b=new ArrayList<>();
    if(target.ready)for(String group:List.of("rank","crime","reputations","access","justice","currency","tools")) {
      boolean present=group.equals("reputations")?Catalog.CATEGORIES.keySet().stream().anyMatch(k->k.startsWith("rep/")&&!available(p,k).isEmpty()):!available(p,group).isEmpty();
      if(present)b.add(button(v,Catalog.CATEGORIES.get(group),cat(target,group)));
    }
    long count=Catalog.ACTIONS.stream().filter(a->p.functions().contains(a.function())).count();
    h.add(count+" / "+Catalog.ACTIONS.size()+" fonctions d’administration disponibles dans le core chargé.");
    h.add("Les modifications concernent uniquement la cible affichée ; une confirmation précède leur application.");
    b.add(button(v,"Actualiser ce dossier",event(Kind.MAIN,target)));b.add(button(v,"Changer de joueur",event(Kind.PLAYERS,null)));
    p.show(dialog("Dossier administratif",h,b,button(v,"Fermer",event(Kind.CLOSE,target)),2));
  }
  private void category(Port p,Person target,String category) {
    Person current=p.find(target.id);if(!target.same(current)){players(p,0);return;}target=current;
    View v=start(p,target);List<String> h=header(p,target);List<Map<String,Object>> b=new ArrayList<>();
    if(category.equals("reputations")) {
      for(String group:Catalog.CATEGORIES.keySet())if(group.startsWith("rep/")&&!available(p,group).isEmpty())b.add(button(v,Catalog.CATEGORIES.get(group),cat(target,group)));
    }else {
      if(category.equals("access"))h.add("Valeurs d’accès : 0 refusé, 1 individuel, 2 lié au rang, 3 temporaire. Les autres sources du core peuvent maintenir un accès.");
      Set<String> keys=new LinkedHashSet<>();for(Catalog.Action a:available(p,category))keys.addAll(a.watch());
      if(!keys.isEmpty())h.add(p.scores(target,keys).entrySet().stream().map(e->human(e.getKey())+" : "+display(e.getValue())).reduce((a,c)->a+"\n"+c).orElse(""));
      if(target.ready)for(Catalog.Action a:available(p,category))b.add(button(v,a.label(),a.detail(),new Event(Kind.PREVIEW,target,category,0,a,null)));
    }
    if(b.isEmpty())h.add("Aucune fonction disponible pour cette rubrique dans le core actuellement chargé.");
    b.add(button(v,"Retour au dossier",event(Kind.MAIN,target)));
    p.show(dialog(Catalog.CATEGORIES.getOrDefault(category,"Administration"),h,b,button(v,"Changer de joueur",event(Kind.PLAYERS,null)),category.equals("rank")?1:2));
  }
  private boolean writable(Port p,Person target,Catalog.Action action) {
    Person current=p.find(target.id);
    if(!target.same(current)||!current.ready){p.notify("Profil ciblé indisponible ou modifié. Aucune modification effectuée.");players(p,0);return false;}
    if(!p.functions().contains(action.function())){p.notify("Cette fonction du core n’est plus disponible. Aucune modification effectuée.");main(p,current,"");return false;}
    return true;
  }
  private void preview(Port p,Person target,Catalog.Action action) {
    if(!writable(p,target,action))return;
    View v=start(p,target);Map<String,Integer> expected=p.scores(target,action.watch());List<String> h=header(p,target);
    h.add("Action : "+action.label());h.add(action.detail());
    if(!expected.isEmpty())h.add("Valeurs actuelles :\n"+expected.entrySet().stream().map(e->human(e.getKey())+" : "+display(e.getValue())).reduce((a,b)->a+"\n"+b).orElse(""));
    Map<String,Object> d=new LinkedHashMap<>();d.put("type","minecraft:confirmation");d.put("title","Confirmer sur "+target.login);
    d.put("pause",false);d.put("can_close_with_escape",true);d.put("after_action","wait_for_response");
    d.put("body",h.stream().map(s->Map.of("type","minecraft:plain_message","contents",s,"width",420)).toList());
    d.put("yes",button(v,"Appliquer à ce personnage",new Event(Kind.APPLY,target,action.category(),0,action,Collections.unmodifiableMap(new LinkedHashMap<>(expected)))));
    d.put("no",button(v,"Annuler",cat(target,action.category())));p.show(d);
  }
  private void apply(Port p,Person target,Catalog.Action action,Map<String,Integer> expected) {
    if(!writable(p,target,action))return;
    if(!expected.equals(p.scores(target,action.watch()))) {
      p.notify("Les valeurs de la cible ont changé depuis la confirmation. Vérifiez de nouveau l’action.");preview(p,target,action);return;
    }
    Map<String,Integer> before=p.scores(target,Catalog.SCORES);
    try {
      p.invoke(target,action.function());
    }catch(RuntimeException e){p.audit(target,action,before,p.scores(target,Catalog.SCORES),"ERROR: "+e.getMessage());p.notify("Erreur pendant l’appel du core. Consultez le journal serveur ; aucune nouvelle tentative automatique.");main(p,target,"");return;}
    Map<String,Integer> after=p.scores(target,Catalog.SCORES);p.audit(target,action,before,after,"FUNCTION_COMPLETED");
    main(p,target,"Fonction du core exécutée : "+action.label()+". Le dossier est actualisé.");
  }
  public static String human(String objective) {
    return switch(objective) {
      case "CAP_RANGSOCIAL"->"Rang social";case "CAP_CRIME"->"Criminalité";case "CAP_ETAT"->"État judiciaire";
      case "CAP_AMENDE"->"Amende";case "CAP_COMPARUTION"->"Comparution";case "CAP_ARREST_PENDING"->"Arrestation en attente";
      case "CAP_QUESTLOCK"->"Verrou de quête";case "CAP_SANCTIONLOCK"->"Verrou de sanction";case "CAP_CUR_BANK"->"Émission comptabilisée";
      case "CAP_DEBUG"->"Diagnostic numérique";case "REP_COURONNE"->"Couronne";case "REP_GARDECOEUR"->"Garde du Cœur";
      case "REP_GARDEPORT"->"Garde du Port";case "REP_GARDEPROFONDEURS"->"Garde des Profondeurs";case "REP_PROFONDEURS"->"Profondeurs";
      case "ACCESS_CAPITALE"->"Capitale";case "ACCESS_QH_NORD"->"Quartier haut nord";case "ACCESS_QH_SUD"->"Quartier haut sud";
      case "ACCESS_PALAIS"->"Palais";case "ACCESS_ARCHIVES"->"Archives";case "ACCESS_BANQUE"->"Banque";
      case "ACCESS_TRIBUNAL"->"Tribunal";case "ACCESS_COEUR"->"Cœur";case "ACCESS_SEUILS"->"Seuils scellés";
      default->objective;
    };
  }
}
