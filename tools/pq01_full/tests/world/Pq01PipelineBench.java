package net.hautecapitale.quetes.sequence;
import java.lang.reflect.*;
import java.util.*;
import static net.hautecapitale.quetes.sequence.SequenceBridge.*;
import static net.hautecapitale.quetes.sequence.Pq01Bridge.*;
/** Vraies fonctions/Brigadier/paquets/capture b10. Caméra, profil Nexus et panneau doublés. */
public final class Pq01PipelineBench {
  static boolean active;static Object player;static Class<?> state;static int checks,frames,commands;static String current="";
  static Object fixture;static final Client client=new Client();static final List<String> evidence=new ArrayList<>();
  public static final class Client {public Object field_1755=new net.hautecapitale.dialogue.client.capture.CaptureDialogueScreen();public Network method_1562(){return network;}final Network network=new Network();}
  public static final class Network {public void method_45730(String text){commands++;run(player,text);run(player,"function capitale:pq01/dispatch_self");}}
  static void check(boolean ok,String label){checks++;if(!ok)throw new AssertionError(label+" ; node="+score(player,"hcm_pq1_node")+" frame="+current);}
  static void set(Class<?> c,String name,Object value)throws Exception{Field f=c.getDeclaredField(name);f.setAccessible(true);f.set(null,value);}
  static void setScore(String name,int value){run(player,"scoreboard players set @s "+name+" "+value);}
  static List<?> choices(){return (List<?>)call(state,"choixCaptures");}
  static void registerOverrides(Object dispatcher){
    Object root=stat("com.mojang.brigadier.builder.LiteralArgumentBuilder","literal","indivispq01");
    List<String> names=new ArrayList<>(List.of("close","approach","return","banter","reconcile"));for(String r:List.of("geraud","altheon","gautier","hugues","anselme","perrin"))names.add("camera_"+r);for(String k:Pq01Ids.keys())names.add("line_"+k);
    for(String name:names){Object node=stat("com.mojang.brigadier.builder.LiteralArgumentBuilder","literal",name);Class<?> contract=type("com.mojang.brigadier.Command");
      Object fn=Proxy.newProxyInstance(contract.getClassLoader(),new Class<?>[]{contract},(p,m,a)->{
        if(m.getDeclaringClass()==Object.class)return objectMethod(p,m,a);
        if(active)return name.equals("approach")||name.equals("return")||name.equals("banter")?0:1;
        return execute(call(call(a[0],"getSource"),"method_9207"),name);
      });call(node,"executes",fn);call(root,"then",node);
    }call(dispatcher,"register",root);
  }
  public static void packet(Object packet){
    if(!active||!packet.getClass().getName().equals("net.minecraft.class_7439"))return;
    Object text=call(packet,"comp_763");boolean overlay=Boolean.TRUE.equals(call(packet,"comp_906"));if(overlay)return;
    String insertion=(String)call(call(text,"method_10866"),"method_10955");
    if(insertion!=null&&insertion.startsWith("indivis:pq01:v1:")){current=insertion.split(":")[3];frames++;}
    if(!Pq01Client.message(text,false))return;
    Object classified=stat("net.hautecapitale.dialogue.client.capture.MessageCapture","classer",text,call(state,"pnjNom"),true,List.of());call(state,"onMessageCapture",classified);
  }
  static void seed(String role){
    for(Object n:(Iterable<?>)call(call(fixture,"get","scores"),"getAsJsonArray"))setScore((String)call(n,"getAsString"),0);
    setScore("hcm_pq1_active",1);setScore("CAP_QDIALOG_OWNER",role.equals("altheon")?92:role.equals("geraud")?91:93);setScore("hcm_pq1_role",score(player,"CAP_QDIALOG_OWNER"));setScore("CAP_QDIALOG_KEEP",1);setScore("hcm_pq1_ttl",12000);setScore("hcf_pq1_ver",1);setScore("hcf_pq1_av",1);setScore("hcq_pq01",10);setScore("hcf_pq1_c",1);
    run(player,"data modify storage hc_quetes:session current set value {owner:\"pipeline\",character:\"character1\",dimension:\"minecraft:overworld\"}");
    run(player,"function capitale:pq01/session/save_macro with storage hc_quetes:session current");
  }
  static void node(String key){run(player,"function capitale:pq01/node/"+key);check(current.equals(key.equals("c0")?"c1":key),"native frame emitted "+key);}
  static void pick(int ordinal){
    Object button=choices().get(ordinal);Object event=call(button,"evenement");String text=null;
    for(Method m:event.getClass().getDeclaredMethods())if(m.getParameterCount()==0&&m.getReturnType()==String.class&&!List.of("toString").contains(m.getName())){text=(String)call(event,m.getName());break;}
    if(text==null||!text.startsWith("/trigger QuestChoix set "))throw new AssertionError("real capture has no QuestChoix command: "+event);
    call(state,"onChoixEnvoye",false,"");client.network.method_45730(text.substring(1));
  }
  static void automatic(){String was=current;int before=commands;for(int i=0;i<12050&&current.equals(was)&&score(player,"hcm_pq1_active")==1;i++)Pq01Client.tickClient(client);check(commands>before,"automatic real frame sends choice "+was);}
  static String string(Object o,String key){return (String)call(call(o,"get",key),"getAsString");}
  public static String runAll(Object p)throws Exception{
    player=p;active=true;
    Object unsafe;Class<?> u=Class.forName("sun.misc.Unsafe");Field uf=u.getDeclaredField("theUnsafe");uf.setAccessible(true);unsafe=uf.get(null);
    Field conn=player.getClass().getField("field_13987");conn.set(player,call(unsafe,"allocateInstance",type("net.hautecapitale.quetes.sequence.Pq01PacketSink")));
    Class<?> settings=type("net.hautecapitale.dialogue.client.DialogueClientSettings");Constructor<?> ctor=settings.getDeclaredConstructor();ctor.setAccessible(true);set(settings,"instance",ctor.newInstance());
    state=type("net.hautecapitale.dialogue.client.DialogueClientState");set(state,"mode",en("net.hautecapitale.dialogue.session.DialogueMode","CAPTURE"));set(state,"etat",en("net.hautecapitale.dialogue.client.DialogueClientState$Etat","DIALOGUE_ACTIVE"));set(state,"pnjNom","Géraud Rivet");set(Pq01Client.class,"voicesChecked",true);
    fixture=stat("com.google.gson.JsonParser","parseString",java.nio.file.Files.readString(java.nio.file.Path.of("pipeline-fixtures.json")));
    for(Object n:(Iterable<?>)call(call(fixture,"get","scores"),"getAsJsonArray")){String name=(String)call(n,"getAsString");run(player,"scoreboard objectives add "+name+(name.equals("QuestChoix")?" trigger":" dummy"));}
    run(player,"function capitale:pq01/load");
    // Chaque arête du graphe traverse le vrai dispatcher et les macros, sans changer son identifiant.
    for(Object row:(Iterable<?>)call(call(fixture,"get","edges"),"getAsJsonArray")){
      String key=string(row,"key"),target=string(row,"target");seed(string(row,"role"));if(key.startsWith("e")||key.startsWith("f_"))setScore("hcq_pq01",40);
      node(key);int expected=((Number)call(call(row,"get","choices"),"getAsInt")).intValue();check(choices().size()==expected,"native visible choices "+key);
      if(Boolean.TRUE.equals(call(call(row,"get","automatic"),"getAsBoolean")))automatic();else pick(((Number)call(call(row,"get","pick"),"getAsInt")).intValue());
      if(target.equals("close"))check(score(player,"hcm_pq1_active")==0,"real close "+key);
      else if(target.equals("dock"))check(score(player,"hcm_pq1_dock")==1&&score(player,"pipeline_dock")==1,"docking handoff only after final line");
      else if(target.equals("complete"))check(score(player,"hcf_pq1_gain")==1&&score(player,"pipeline_reward")==1,"reward handoff only after final line");
      else check(current.equals(target),"real dispatch "+key+" -> "+target);
    }
    // Plusieurs questions successives, sans fermer ni recréer une session entre les réponses.
    for(Object row:(Iterable<?>)call(call(fixture,"get","menus"),"getAsJsonArray")){
      String key=string(row,"key");seed(string(row,"role"));node(key);
      int count=((Number)call(call(row,"get","count"),"getAsInt")).intValue();
      int expected=((Number)call(call(row,"get","choices"),"getAsInt")).intValue();
      for(int i=0;i<count;i++){pick(i);check(score(player,"hcm_pq1_active")==1&&choices().size()==expected,"same session repeated questions "+key+" #"+i);}
      pick(choices().size()-1);check(score(player,"hcm_pq1_active")==0,"close remains usable "+key);
    }
    // Course client/serveur : choix reçu pendant la voix, puis envoyé à nouveau.
    seed("geraud");node("a1");pick(0);automatic();check(current.equals("a3"),"intro enters Gautier scene fallback");
    automatic();automatic();automatic();automatic();check(current.equals("a7"),"intro returns to Géraud after Gautier");pick(0);automatic();check(current.equals("ga_menu")&&score(player,"hcf_pq1_a")==1,"intro reaches captain objective");
    setScore("hcm_pq1_ttl",201);pick(0);check(score(player,"hcm_pq1_ttl")==3600,"a new answer renews conversation timeout");pick(1);pick(2);pick(3);check(score(player,"hcm_pq1_active")==1,"Géraud four questions without reopening");
    pick(choices().size()-1);run(player,"function capitale:pq01/open/geraud_self");check(current.equals("ga_menu")&&score(player,"hcm_pq1_active")==1,"closed completed intro reopens at questions");
    seed("altheon");setScore("hcf_pq1_av",0);node("c1");automatic();pick(0);automatic();pick(0);automatic();automatic();
    check(current.equals("c_menu")&&choices().size()==6,"captain introduction and conditional questions");
    pick(0);pick(0);pick(0);check(current.equals("ca3"),"captain family A returns questions on answer");
    pick(1);pick(0);pick(0);check(current.equals("cb3")&&choices().size()==7,"captain mission unlocks Avaleiv");
    pick(2);pick(0);check(current.equals("cc2"),"captain family C usable in same session");
    pick(3);automatic();pick(0);pick(0);check(current.equals("cd4"),"captain family D usable in same session");
    pick(4);automatic();pick(0);check(current.equals("ce3")&&score(player,"hcm_pq1_active")==1,"captain family E usable before conclusion");
    check(call(choices().get(5),"type").toString().equals("QUETE"),"native green captain conclusion remains explicit");
    pick(5);automatic();automatic();automatic();automatic();check(score(player,"pipeline_dock")==1,"captain full interview ends at legacy docking handoff");
    seed("geraud");node("a8");setScore("hcm_pq1_voice",5);automatic();
    check(current.equals("a8")&&score(player,"hcm_pq1_queued")>0,"early automatic input queued without losing frame");
    for(int i=0;i<5;i++)run(player,"function capitale:pq01/session/tick_self");
    check(current.equals("ga_menu")&&choices().size()==6,"queued automatic resumes to usable questions");int nonce=score(player,"hcm_pq1_tok");
    client.network.method_45730("trigger QuestChoix set "+(nonce-16+1));check(current.equals("ga_menu")&&score(player,"hcm_pq1_active")==1,"stale retry cannot advance next frame");
    // Voix déclarée sans son : aucune attente fantôme pour le client.
    run(player,"data modify storage capitale:pq01_config voices.a8 set value {sound:\"\",ticks:12000}");node("a8");
    Field f=Pq01Client.class.getDeclaredField("frame");f.setAccessible(true);check(((Pq01Pacing.Frame)f.get(null)).voiceTicks()==0,"empty sound ignores stale voice ticks");
    active=false;String result="PASS "+checks+" pipeline checks, "+frames+" actual server frames, "+commands+" native trigger commands. Real Fabric functions, macros, scoreboard, tellraw packets and Dialogue b10 capture. Camera/panel/Nexus doubled; legacy teleport/reward bodies replaced by markers.\n";
    System.out.println(result);return result;
  }
}
