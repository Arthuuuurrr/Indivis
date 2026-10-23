package net.hautecapitale.quetes.sequence;

import java.lang.reflect.*;
import java.util.*;
import net.hautecapitale.dialogue.config.DialogueConfig;
import net.hautecapitale.dialogue.session.DialogueManager;
import net.tompsen.nexuscharacters.NexusCharacters;

/** Exécute Pq01Bridge/SequenceBridge du JAR livré. Monde, Nexus et caméra doublés. */
public final class Pq01InteractionBench {
  public static final List<String> events=new ArrayList<>();
  static int checks;
  static void check(boolean ok,String message){checks++;if(!ok)throw new AssertionError(message+" "+events);}
  public static final class Store {
    public Object nbt=GuardNbt.compound();
    public Object method_22546(Object id){return nbt;}
  }
  public static final class Board {
    final Map<String,Integer> values=new HashMap<>();
    public String method_1170(String name){return name;}
    public Score method_55430(Object player,String name){return new Score(values.getOrDefault(name,0));}
  }
  public record Score(int value){public int method_55397(){return value;}}
  public static final class Source {
    public Source method_9232(Object p){return this;}
    public Source method_9227(Object w){return this;}
    public Source method_9208(Object p){return this;}
    public Source method_9217(){return this;}
  }
  public static final class Commands {
    public void method_44252(Object source,String command){events.add("command:"+command);}
  }
  public static final class Server {
    final Store store=new Store();final Board board=new Board();
    public Store method_22827(){return store;}
    public Board method_3845(){return board;}
    public Source method_3739(){return new Source();}
    public Commands method_3734(){return new Commands();}
  }
  public static final class Dimension {public String method_29177(){return "minecraft:overworld";}}
  public static final class World {
    final Server server=new Server();
    public Server method_8503(){return server;}
    public Dimension method_27983(){return new Dimension();}
  }
  public static class Entity {
    final UUID id=UUID.randomUUID();World world;double x;final Set<String> tags=new HashSet<>();
    Entity(World w){world=w;}
    public UUID method_5667(){return id;}
    public World method_73183(){return world;}
    public Set<String> method_5752(){return tags;}
    public double method_23317(){return x;}
    public double method_23318(){return 0;}
    public double method_23321(){return 0;}
  }
  public static final class Player extends Entity {
    Player(World w){super(w);}
    public World method_51469(){return world;}
    public Object method_73189(){return this;}
  }
  static World world;static Player player;static Entity npc;
  static void reset(){
    events.clear();DialogueManager.session=null;DialogueManager.blocked=false;DialogueManager.failOpen=false;DialogueManager.opened=0;
    DialogueConfig.enabled=true;NexusCharacters.selected=true;
    world=new World();player=new Player(world);npc=new Entity(world);
    Object record=GuardNbt.compound();GuardNbt.string(record,"uuid",npc.id.toString());GuardNbt.string(record,"dimension","minecraft:overworld");
    GuardNbt.put(GuardNbt.childOrCreate(world.server.store.nbt,"bindings"),"geraud_large",record);
  }
  static boolean quest(){return events.stream().anyMatch(s->s.equals("command:function capitale:npc/geraud_rivet/interact_self"));}
  static void click(){Pq01Bridge.interact(player,npc,"geraud_large","interact");}
  public static void main(String[] args)throws Exception {
    check(Pq01Bridge.class.getProtectionDomain().getCodeSource().getLocation().toString().endsWith("PQ01-R3-HF3.jar"),"pont de production HF3");
    check(SequenceBridge.class.getProtectionDomain().getCodeSource().equals(Pq01Bridge.class.getProtectionDomain().getCodeSource()),"helper historique de production");
    reset();click();check(DialogueManager.opened==1&&quest(),"clic avec joueur non nul ouvre la caméra et appelle la quête");
    check(events.get(0).equals("camera:CAPTURE")&&events.get(1).startsWith("command:function"),"caméra ouverte avant le premier texte");
    click();check(DialogueManager.opened==1,"session existante non ouverte une seconde fois");
    reset();world.server.board.values.put("hcq_pq01",40);click();
    check(DialogueManager.opened==1&&!quest(),"phase quai : caméra conservée, aucune progression de départ");
    check(events.stream().anyMatch(s->s.contains("test_debut")),"phase incompatible expliquée avec relecture explicite");
    check(world.server.board.values.get("hcq_pq01")==40,"aucune réinitialisation automatique");
    reset();world.server.board.values.put("CAP_PROFILE_LOCK",1);click();check(DialogueManager.opened==1&&!quest(),"profil en chargement : caméra sans exécution de quête");
    check(events.stream().anyMatch(s->s.contains("chargement")),"chargement expliqué");
    reset();NexusCharacters.selected=false;click();check(DialogueManager.opened==1&&!quest(),"aucun personnage : caméra sans quête");
    reset();Pq01Bridge.interact(player,npc,"geraud_large","near");check(DialogueManager.opened==0&&events.equals(List.of("command:function capitale:quest/spawn_dirigeable_couronne/geraud_large_start_self")),"near ne force pas la caméra");
    reset();GuardNbt.string(Pq01Bridge.binding(player,"geraud_large"),"uuid",UUID.randomUUID().toString());click();check(events.isEmpty(),"autre PNJ refusé avant caméra");
    reset();npc.x=8.01;click();check(events.isEmpty(),"distance de clic contrôlée avant caméra");
    reset();npc.x=8;click();check(quest(),"limite de distance autorisée");
    reset();npc.world=new World();click();check(events.isEmpty(),"autre monde refusé");
    reset();GuardNbt.string(Pq01Bridge.binding(player,"geraud_large"),"dimension","minecraft:the_nether");click();check(events.isEmpty(),"dimension de liaison vérifiée");
    reset();npc.tags.add(Pq01Actors.TAG);click();check(events.isEmpty(),"acteur personnel hors scène refusé");
    reset();DialogueManager.blocked=true;click();check(DialogueManager.opened==0&&!quest()&&events.equals(List.of("refus:PNJ occupé")),"refus de Dialogue respecté");
    reset();DialogueConfig.enabled=false;click();check(DialogueManager.opened==0&&quest(),"configuration de capture désactivée respectée");
    reset();DialogueManager.failOpen=true;boolean failed=false;try{click();}catch(IllegalStateException e){failed=true;}
    Field suppressed=Pq01Bridge.class.getDeclaredField("SUPPRESS");suppressed.setAccessible(true);
    check(failed&&((Set<?>)suppressed.get(null)).isEmpty()&&!quest(),"échec caméra : aucun verrou oublié ni quête exécutée");
    reset();Pq01Bridge.interact(null,npc,"geraud_large","interact");Pq01Bridge.interact(player,null,"geraud_large","interact");check(events.isEmpty(),"arguments absents ignorés");
    boolean invalid=false;try{Pq01Bridge.interact(player,npc,"geraud_large","op");}catch(IllegalArgumentException e){invalid=true;}check(invalid,"liste fermée des événements conservée");
    System.out.println("PASS "+checks+" assertions ; vrais Pq01Bridge, SequenceBridge et NBT ; monde, personnage Nexus et effets caméra doublés. Aucun serveur, paquet client ou rendu en jeu exécuté.");
  }
}
