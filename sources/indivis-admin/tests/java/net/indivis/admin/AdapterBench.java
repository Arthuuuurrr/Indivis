package net.indivis.admin;
import java.util.*;
import net.minecraft.class_2960;
import net.tompsen.nexuscharacters.NexusCharacters.Character;

/** Exécute le véritable adaptateur de production avec API Minecraft/Nexus doublées. */
public final class AdapterBench {
  static int assertions;static void check(boolean v,String s){assertions++;if(!v)throw new AssertionError(s);}
  public record Text(String getString) {public String getString(){return getString;}}
  public static final class Source {
    public Player player;public Object world,position;
    public Source method_9232(Player p){player=p;return this;}
    public Source method_9227(Object w){world=w;return this;}
    public Source method_9208(Object p){position=p;return this;}
  }
  public static final class Player {
    public boolean op;public final UUID id=UUID.randomUUID();public final String login;public Character character=new Character(UUID.randomUUID(),"Personnage");
    public final Object world=new Object(),position=new Object();public final Map<String,Integer> scores=new HashMap<>();
    Player(String name){login=name;scores.put("CAP_RANGSOCIAL",20);scores.put("CAP_PROFILE_LOCK",0);}
    public UUID method_5667(){return id;}public Text method_5477(){return new Text(login);}
    public Object method_51469(){return world;}public Object method_73189(){return position;}
    public Source method_64396(){Source s=new Source();s.player=this;return s;}
  }
  public static final class Players {
    final Map<UUID,Player> list=new LinkedHashMap<>();
    public Player method_14602(UUID id){return list.get(id);}public List<Player> method_14571(){return new ArrayList<>(list.values());}
  }
  public record ReadScore(int value){public int method_55397(){return value;}}
  public static final class Board {
    public String method_1170(String key){return key.startsWith("CAP_")?key:null;}
    public ReadScore method_55430(Player p,String key){return p.scores.containsKey(key)?new ReadScore(p.scores.get(key)):null;}
  }
  public static final class Functions {
    public String called;public Source source;
    public Iterable<class_2960> method_29463(){return ()->List.of(class_2960.method_60654("capitale:rank/set_30_self")).iterator();}
    public Optional<String> method_12905(class_2960 id){return id.toString().equals("capitale:rank/set_30_self")?Optional.of(id.toString()):Optional.empty();}
    public void method_12904(String f,Source s){called=f;source=s;s.player.scores.put("CAP_RANGSOCIAL",30);}
  }
  public static final class Commands {
    public final List<String> commands=new ArrayList<>();public final List<Source> sources=new ArrayList<>();
    public void method_44252(Source source,String command){
      if(command.startsWith("dialog ")||command.startsWith("tellraw ")){
        String selector=command.split(" ")[command.startsWith("dialog ")?2:1];
        if(!selector.equals("@s"))throw new IllegalArgumentException("Only players may be affected by this command, but the provided selector includes entities");
        if(source.player==null)throw new IllegalArgumentException("Player context missing");
      }
      commands.add(command);sources.add(source);
    }
  }
  public static final class Server {
    public final Players players=new Players();public final Board board=new Board();public final Functions functions=new Functions();public final Commands commands=new Commands();
    public Players method_3760(){return players;}public Board method_3845(){return board;}
    public Functions method_3740(){return functions;}public Commands method_3734(){return commands;}
    public Source method_3739(){return new Source();}
  }
  public static void main(String[]args) {
    Server server=new Server();Player admin=new Player("Admin"),target=new Player("Joueur");admin.op=true;
    server.players.list.put(admin.id,admin);server.players.list.put(target.id,target);MinecraftPort p=new MinecraftPort(server,admin);
    check(p.authorized(),"permission opérateur fraîche");var t=p.find(target.id);check(t.characterId().equals(target.character.id())&&t.ready(),"personnage Nexus actif");
    check(p.functions().equals(Set.of("capitale:rank/set_30_self")),"catalogue natif Iterable sans Collection");
    int count=target.scores.size();var read=p.scores(t,List.of("CAP_RANGSOCIAL","CAP_INEXISTANT","REP_COURONNE"));
    check(read.get("CAP_RANGSOCIAL")==20&&read.get("CAP_INEXISTANT")==null,"scores existants/absents");check(count==target.scores.size(),"lecture sans création de scores");
    p.invoke(t,"capitale:rank/set_30_self");check(server.functions.source.player==target,"source @s cible");
    check(server.functions.source.world==target.world&&server.functions.source.position==target.position,"dimension et position de la cible");
    check(p.scores(t,List.of("CAP_RANGSOCIAL")).get("CAP_RANGSOCIAL")==30,"relecture après fonction native");
    check(admin.scores.get("CAP_RANGSOCIAL")==20,"administrateur inchangé");
    boolean denied=false;try{p.invoke(t,"capitale:evil_self");}catch(IllegalArgumentException e){denied=true;}check(denied,"fonction arbitraire refusée");
    admin.op=false;check(!p.authorized(),"deop relu");denied=false;try{p.invoke(t,"capitale:rank/set_30_self");}catch(IllegalStateException e){denied=true;}check(denied,"deop bloque invocation");admin.op=true;
    target.scores.put("CAP_PROFILE_LOCK",1);check(!p.find(target.id).ready(),"verrou Nexus lu");target.scores.put("CAP_PROFILE_LOCK",0);
    target.character=null;check(!p.find(target.id).ready()&&p.find(target.id).nexus(),"aucun personnage ne bascule pas sur le compte");
    p.show(Map.of("type","minecraft:multi_action","title","Test \" échappé"));check(server.commands.commands.get(0).startsWith("dialog show @s {"),"sélecteur joueur pour le dialogue");
    check(server.commands.sources.get(0).player==admin,"@s est l’administrateur pour le menu");
    p.close();check(server.commands.commands.get(1).equals("dialog clear @s"),"fermeture ciblée");
    check(server.commands.sources.get(1).player==admin,"@s est l’administrateur pour la fermeture");
    p.notify("Bouton expiré");check(server.commands.commands.get(2).startsWith("tellraw @s "),"sélecteur joueur pour les messages");
    check(server.commands.sources.get(2).player==admin,"notification à l’administrateur uniquement");
    boolean selectorRejected=false;try{server.commands.method_44252(server.method_3739(),"dialog show "+admin.id+" {}");}catch(IllegalArgumentException e){selectorRejected=true;}
    check(selectorRejected,"ancienne commande reproduit le refus du log (sélecteur modélisé)");
    System.out.println("ADAPTER_BENCH assertions="+assertions+" errors=0; API Minecraft/Nexus doublées, serveur non testé");
  }
}
