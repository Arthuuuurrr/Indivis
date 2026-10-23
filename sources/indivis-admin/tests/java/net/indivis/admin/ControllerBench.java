package net.indivis.admin;
import java.util.*;

public final class ControllerBench {
  static int checks;
  static void check(boolean condition,String name){checks++;if(!condition)throw new AssertionError(name);}
  static AdminController.Person person(String login){return new AdminController.Person(UUID.randomUUID(),login,UUID.randomUUID(),"RP "+login,true,true,42,new Object());}
  static final class Port implements AdminController.Port {
    boolean op=true;AdminController.Person admin=person("Operateur"),target=person("Joueur"),other=person("Autre");
    final Map<UUID,Map<String,Integer>> data=new HashMap<>();final List<String> invoked=new ArrayList<>(),audit=new ArrayList<>(),messages=new ArrayList<>();
    final Set<String> functions=new HashSet<>();Map<String,Object> dialog;int closed;
    Port(){for(Catalog.Action a:Catalog.ACTIONS)functions.add(a.function());for(var p:List.of(admin,target,other))data.put(p.id(),new HashMap<>(Map.of("CAP_RANGSOCIAL",20,"CAP_CRIME",10)));}
    public boolean authorized(){return op;}public AdminController.Person actor(){return admin;}
    public List<AdminController.Person> players(){return List.of(admin,target,other);}
    public AdminController.Person find(UUID id){return players().stream().filter(p->p.id().equals(id)).findFirst().orElse(null);}
    public Map<String,Integer> scores(AdminController.Person p,Collection<String> keys){Map<String,Integer> result=new LinkedHashMap<>();for(String key:keys)result.put(key,data.get(p.id()).get(key));return result;}
    public Set<String> functions(){return functions;}public void show(Map<String,Object> d){dialog=d;}public void close(){closed++;}
    public void notify(String m){messages.add(m);}
    public void invoke(AdminController.Person p,String fn){invoked.add(p.id()+" "+fn);if(fn.startsWith("capitale:rank/set_"))data.get(p.id()).put("CAP_RANGSOCIAL",Integer.parseInt(fn.split("set_")[1].split("_")[0]));}
    public void audit(AdminController.Person p,Catalog.Action a,Map<String,Integer> before,Map<String,Integer> after,String result){audit.add(result);}
  }
  @SuppressWarnings("unchecked") static void buttons(Object object,List<Map<String,Object>> result) {
    if(object instanceof Map<?,?> m){if(m.containsKey("action")&&m.containsKey("label"))result.add((Map<String,Object>)m);for(Object v:m.values())buttons(v,result);}
    if(object instanceof Collection<?> c)for(Object v:c)buttons(v,result);
  }
  @SuppressWarnings("unchecked") static String token(Port p,String label) {
    List<Map<String,Object>> b=new ArrayList<>();buttons(p.dialog,b);
    var button=b.stream().filter(x->x.get("label").toString().startsWith(label)).findFirst().orElseThrow(()->new AssertionError("Missing "+label+" in "+Json.encode(p.dialog)));
    String command=(String)((Map<String,Object>)button.get("action")).get("command");return command.substring(command.lastIndexOf(' ')+1);
  }
  static String preview(AdminController c,Port p) {c.selectByName(p,"Joueur");c.click(p,token(p,"Rang social"));c.click(p,token(p,"30 —"));return token(p,"Appliquer");}
  static AdminController.Person changed(AdminController.Person p,boolean reconnect,boolean ready) {return new AdminController.Person(p.id(),p.login(),reconnect?p.characterId():UUID.randomUUID(),p.rpName(),true,ready,p.epoch(),reconnect?new Object():p.connection());}
  public static void main(String[]args) {
    check(Catalog.ACTIONS.size()==148,"catalogue complet");
    Port p=new Port();AdminController c=new AdminController();c.open(p);check(p.invoked.isEmpty(),"ouvrir ne modifie rien");
    c.click(p,token(p,"Joueur —"));check(Json.encode(p.dialog).contains("RP Joueur"),"personnage affiché");
    String apply=preview(c,p);check(p.invoked.isEmpty(),"confirmation obligatoire");c.click(p,apply);
    check(p.data.get(p.target.id()).get("CAP_RANGSOCIAL")==30,"fonction exécutée sur la cible");
    check(p.data.get(p.admin.id()).get("CAP_RANGSOCIAL")==20&&p.data.get(p.other.id()).get("CAP_RANGSOCIAL")==20,"autres joueurs inchangés");
    check(p.audit.equals(List.of("FUNCTION_COMPLETED")),"trace créée");c.click(p,apply);check(p.invoked.size()==1,"bouton rejoué refusé");
    p=new Port();c=new AdminController();apply=preview(c,p);p.op=false;c.click(p,apply);check(p.invoked.isEmpty(),"retrait OP avant clic");
    p=new Port();c=new AdminController();apply=preview(c,p);p.target=changed(p.target,false,true);c.click(p,apply);check(p.invoked.isEmpty(),"changement personnage cible");
    p=new Port();c=new AdminController();apply=preview(c,p);p.target=changed(p.target,true,true);c.click(p,apply);check(p.invoked.isEmpty(),"reconnexion cible");
    p=new Port();c=new AdminController();apply=preview(c,p);p.admin=changed(p.admin,false,true);c.click(p,apply);check(p.invoked.isEmpty(),"changement personnage admin");
    p=new Port();c=new AdminController();apply=preview(c,p);var t=p.target;p.target=new AdminController.Person(t.id(),t.login(),t.characterId(),t.rpName(),true,false,t.epoch(),t.connection());c.click(p,apply);check(p.invoked.isEmpty(),"profil verrouillé");
    p=new Port();c=new AdminController();apply=preview(c,p);p.functions.clear();c.click(p,apply);check(p.invoked.isEmpty(),"fonction retirée après confirmation");
    p=new Port();c=new AdminController();apply=preview(c,p);p.data.get(p.target.id()).put("CAP_RANGSOCIAL",50);c.click(p,apply);check(p.invoked.isEmpty(),"concurrence sur valeur affichée");c.click(p,token(p,"Appliquer"));check(p.invoked.size()==1,"nouvelle confirmation valide");
    long[] now={0};p=new Port();c=new AdminController(()->now[0]);apply=preview(c,p);now[0]=java.time.Duration.ofMinutes(6).toNanos();c.click(p,apply);check(p.invoked.isEmpty()&&p.closed==1,"expiration");
    p=new Port();Port intruder=new Port();c=new AdminController();apply=preview(c,p);c.click(intruder,apply);check(intruder.invoked.isEmpty()&&p.invoked.isEmpty(),"token isolé par administrateur");
    check(Json.encode(Map.of("text","\"\\\n")).equals("{\"text\":\"\\\"\\\\\\n\"}"),"texte JSON échappé");
    System.out.println("CONTROLLER_BENCH assertions="+checks+" errors=0; API Minecraft/Nexus doublées");
  }
}
