package net.indivis.admin;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;
import java.util.function.Predicate;

/** Adaptateur serveur 1.21.11 ; aucune classe Minecraft ou Nexus copiée dans ce mod. */
final class MinecraftPort implements AdminController.Port {
  private final Object server,administrator;
  private Set<String> available;
  MinecraftPort(Object server,Object administrator){this.server=server;this.administrator=administrator;}
  @Override @SuppressWarnings("unchecked") public boolean authorized() {
    Predicate<Object> permission=(Predicate<Object>)Reflect.stat("net.minecraft.class_2170","method_71774",Reflect.field("net.minecraft.class_2170","field_31839"));
    return permission.test(Reflect.call(administrator,"method_64396"));
  }
  private UUID id(Object p){return (UUID)Reflect.call(p,"method_5667");}
  private Object manager(){return Reflect.call(server,"method_3760");}
  private Object player(UUID id){return Reflect.call(manager(),"method_14602",id);}
  private Integer score(Object p,String key) {
    Object board=Reflect.call(server,"method_3845"),objective=Reflect.call(board,"method_1170",key);
    if(objective==null)return null;Object readable=Reflect.call(board,"method_55430",p,objective);
    return readable==null?null:((Number)Reflect.call(readable,"method_55397")).intValue();
  }
  private AdminController.Person person(Object p) {
    String name=(String)Reflect.call(Reflect.call(p,"method_5477"),"getString");
    Integer lock=score(p,"CAP_PROFILE_LOCK"),epoch=score(p,"hcm_nonce");
    boolean nexus=false,ready=lock==null||lock<=0;UUID character=null;String rp=null;
    try {
      Class<?> api=Class.forName("net.tompsen.nexuscharacters.NexusCharacters");nexus=true;
      Object profile=Reflect.call(api,"getSelectedCharacter",p);
      if(profile==null)ready=false;
      else {character=(UUID)Reflect.call(profile,"id");rp=(String)Reflect.call(profile,"name");}
    }catch(ClassNotFoundException absent){/* Serveur sans Nexus : profil de compte. */}
    catch(RuntimeException problem){nexus=true;ready=false;rp="profil indisponible";}
    return new AdminController.Person(id(p),name,character,rp,nexus,ready,epoch,p);
  }
  @Override public AdminController.Person actor(){return person(administrator);}
  @Override @SuppressWarnings("unchecked") public List<AdminController.Person> players() {
    List<AdminController.Person> result=new ArrayList<>();for(Object p:(List<Object>)Reflect.call(manager(),"method_14571"))result.add(person(p));return result;
  }
  @Override public AdminController.Person find(UUID id){Object p=player(id);return p==null?null:person(p);}
  @Override public Map<String,Integer> scores(AdminController.Person target,Collection<String> keys) {
    Object p=player(target.id());Map<String,Integer> result=new LinkedHashMap<>();
    for(String key:keys)result.put(key,p==null?null:score(p,key));return result;
  }
  @Override public Set<String> functions() {
    if(available==null) {
      Object manager=Reflect.call(server,"method_3740");
      @SuppressWarnings("unchecked") Iterable<Object> ids=(Iterable<Object>)Reflect.call(manager,"method_29463");
      Set<String> result=new HashSet<>();for(Object id:ids)result.add(id.toString());available=Collections.unmodifiableSet(result);
    }
    return available;
  }
  private void command(String command){
    Object source=Reflect.call(Reflect.call(server,"method_3739"),"method_9232",administrator);
    Reflect.call(Reflect.call(server,"method_3734"),"method_44252",source,command);
  }
  @Override public void show(Map<String,Object> dialog){command("dialog show @s "+Json.encode(dialog));}
  @Override public void close(){command("dialog clear @s");}
  @Override public void notify(String message){command("tellraw @s "+Json.encode(Map.of("text","[Indivis Admin] "+message,"color","yellow")));}
  @Override public void invoke(AdminController.Person target,String function) {
    if(!authorized())throw new IllegalStateException("Droits opérateur retirés");
    Object p=player(target.id());if(p==null||!target.same(person(p))||!person(p).ready())throw new IllegalStateException("Personnage cible modifié");
    if(Catalog.ACTIONS.stream().noneMatch(a->a.function().equals(function)))throw new IllegalArgumentException("Fonction hors catalogue");
    Object fm=Reflect.call(server,"method_3740"),key=Reflect.stat("net.minecraft.class_2960","method_60654",function);
    Optional<?> loaded=(Optional<?>)Reflect.call(fm,"method_12905",key);
    if(loaded.isEmpty())throw new IllegalStateException("Fonction du core absente");
    Object source=Reflect.call(server,"method_3739");source=Reflect.call(source,"method_9232",p);
    source=Reflect.call(source,"method_9227",Reflect.call(p,"method_51469"));source=Reflect.call(source,"method_9208",Reflect.call(p,"method_73189"));
    // Appelé hors du contexte Brigadier, dans END_SERVER_TICK : la fonction termine avant la relecture.
    Reflect.call(fm,"method_12904",loaded.get(),source);
  }
  @Override public void audit(AdminController.Person target,Catalog.Action action,Map<String,Integer> before,Map<String,Integer> after,String result) {
    Map<String,Object> line=new LinkedHashMap<>();line.put("timestamp",Instant.now().toString());
    line.put("administrator",actor().id().toString());line.put("target",target.id().toString());
    line.put("character",target.characterId()==null?null:target.characterId().toString());line.put("function",action.function());
    line.put("before",before);line.put("after",after);line.put("result",result);String json=Json.encode(line);
    System.out.println("[Indivis Admin] "+json);
    try {
      Path folder=Path.of("logs","indivis-admin");Files.createDirectories(folder);
      Files.writeString(folder.resolve("actions.jsonl"),json+"\n",StandardCharsets.UTF_8,StandardOpenOption.CREATE,StandardOpenOption.APPEND);
    }catch(java.io.IOException e){System.err.println("[Indivis Admin] Écriture du journal d’administration impossible : "+e.getMessage());}
  }
}
