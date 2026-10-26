package net.hautecapitale.quetes.sequence;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.*;
import java.util.function.Predicate;
import java.util.regex.*;
import static net.hautecapitale.quetes.sequence.SequenceBridge.*;
import static net.hautecapitale.quetes.sequence.Pq01Bridge.*;

/** Repères du core : un passage local par seconde, sans dépendance au tick MQ. */
final class CoreQuestMarkers {
  static final String RESOURCE="/indivis_core_quest_givers.tsv";
  private static final Map<String,String> NAMES=new HashMap<>(),TAGS=new HashMap<>(),COMMANDS=new HashMap<>();
  private static final Set<String> ROLES=new LinkedHashSet<>(),ERRORS=new HashSet<>();
  private static final Map<Object,Detected> CACHE=new WeakHashMap<>();
  private static final Pattern FUNCTION=Pattern.compile("(?:^|\\s)function\\s+(capitale:[a-z0-9_./-]+)(?:$|\\s)");
  private static long seconds,passes,inspections;
  private record Detected(String name,String role,long until){}
  static {
    try(InputStream stream=CoreQuestMarkers.class.getResourceAsStream(RESOURCE)){
      if(stream==null)throw new IllegalStateException("Catalogue de donneurs absent");
      try(BufferedReader r=new BufferedReader(new InputStreamReader(stream,StandardCharsets.UTF_8))){
        for(String line;(line=r.readLine())!=null;){String[] p=line.split("\t",3);if(p.length!=3)continue;ROLES.add(p[1]);
          switch(p[0]){case "name"->NAMES.put(normalize(p[2]),p[1]);case "tag"->TAGS.put(p[2],p[1]);case "function"->COMMANDS.put(p[2],p[1]);default->throw new IllegalStateException(p[0]);}}
      }
    }catch(IOException failure){throw new ExceptionInInitializerError(failure);}
  }
  private CoreQuestMarkers(){}
  static String normalize(String text){
    return Normalizer.normalize(text,Normalizer.Form.NFC).replaceAll("(?i)[§&][0-9a-fk-orx]","")
      .replace('\u00a0',' ').strip().replaceAll("\\s+"," ").toLowerCase(Locale.ROOT);
  }
  static boolean functionPresent(Object p,String id){
    return ((Optional<?>)call(call(server(p),"method_3740"),"method_12905",stat("net.minecraft.class_2960","method_60654",id))).isPresent();
  }
  static boolean enabled(Object p){return functionPresent(p,"capitale:quest/markers/version");}
  static List<Object> nearby(Object p){
    Object box=call(call(p,"method_5829"),"method_1014",24d);
    @SuppressWarnings("unchecked") List<Object> list=(List<Object>)call(world(p),"method_8333",p,box,(Predicate<Object>)e->
      type("de.markusbordihn.easynpc.entity.easynpc.EasyNPC").isInstance(e)&&!tags(e).contains("hcm_private")&&!actor(e));
    return list.stream().filter(e->pos(e,world(p)).distance2(pos(p,world(p)))<=24*24).toList();
  }
  static String detect(Object npc){
    Set<String> tags=tags(npc);
    for(String role:ROLES)if(tags.contains("cap_qgiver_"+role)&&!tags.contains("hcm_qmark_auto_"+role))return role;
    String raw=(String)call(call(npc,"method_5477"),"getString");Detected cached=CACHE.get(npc);
    if(cached!=null&&cached.name.equals(raw)&&seconds<cached.until)return cached.role;
    inspections++;Set<String> actions=new LinkedHashSet<>();
    Object eventSet=call(call(npc,"getEasyNPCActionEventData"),"getActionEventSet");
    Object rows=eventSet==null?null:child(child(call(eventSet,"createTag"),"ActionEventSet"),"ON_INTERACTION");
    if(rows instanceof List<?> list)for(Object row:list){
      String kind=str(row,"Type",""),command=str(row,"Cmd","");
      if(kind.equals("COMMAND")){
        Matcher matcher=FUNCTION.matcher(Pq01Actions.normalizedCommand(command));
        while(matcher.find()){String role=COMMANDS.get(matcher.group(1));if(role!=null)actions.add(role);}
      }else if(kind.equals("CUSTOM")&&command.strip().startsWith(Pq01Actions.ID+" geraud_"))actions.add("geraud");
    }
    String role=actions.size()==1?actions.iterator().next():null;
    if(actions.isEmpty()){
      for(var e:TAGS.entrySet())if(tags.contains(e.getKey())){role=e.getValue();break;}
      if(role==null)role=NAMES.get(normalize(raw));
    }
    CACHE.put(npc,new Detected(raw,role,seconds+10));return role;
  }
  static void identify(Object npc){
    String role=detect(npc);Set<String> tags=tags(npc);
    for(String old:ROLES)if(tags.contains("hcm_qmark_auto_"+old)&&!old.equals(role)){
      call(npc,"method_5738","hcm_qmark_auto_"+old);call(npc,"method_5738","cap_qgiver_"+old);
    }
    if(role!=null&&!tags.contains("cap_qgiver_"+role)){
      call(npc,"method_5780","cap_qgiver_"+role);call(npc,"method_5780","hcm_qmark_auto_"+role);
    }
  }
  static void update(Object p){
    if(!enabled(p))return;
    UUID character=character(p);
    if(character==null||score(p,"CAP_PROFILE_LOCK")!=0){
      run(p,"kill @e[type=minecraft:text_display,tag=cap_qmark,tag=hcm_owner_"+uuid(p)+"]");return;
    }
    for(Object npc:nearby(p))identify(npc);
    // Arguments propres au joueur ; aucune écriture dans hc_quetes:session.
    run(p,"function capitale:quest/markers/tick_self {owner:"+quote(uuid(p).toString())+",character:"+quote(character.toString())+"}");passes++;
  }
  static void tick(List<?> players){seconds++;for(Object p:players)try{update(p);}catch(Throwable failure){reportOnce(failure);}}
  static void reportOnce(Throwable t){if(ERRORS.add(t.toString())){System.err.println("[Indivis REPERES HF12] "+t);t.printStackTrace(System.err);}}
  static void diagnostic(Object p){
    UUID ch=character(p);boolean loaded=enabled(p);List<Object> near=nearby(p);
    say(p,"Repères HF12 : fonctions QMARK2="+loaded+", personnage="+(ch==null?"aucun":ch)+", verrou="+score(p,"CAP_PROFILE_LOCK")+", passages="+passes+", inspections NPC="+inspections+".");
    if(loaded){run(p,"execute store result score @s hcm_qmark_probe run function capitale:quest/markers/guide_free");say(p,"Escorte libre="+score(p,"hcm_qmark_probe")+" ; quête active="+score(p,"CAP_QUETEACTIVE")+" ; Greffier : état="+score(p,"hcq_dqhc05")+", délai="+score(p,"hcd_dqhc05")+".");}
    near.stream().sorted(Comparator.comparingDouble(e->pos(e,world(p)).distance2(pos(p,world(p))))).limit(3).forEach(e->{
      String role=detect(e);say(p,"PNJ "+Pq01EntityFactory.registryId(e)+" : "+call(call(e,"method_5477"),"getString")+" ; rôle="+(role==null?"non reconnu":role)+" ; distance="+String.format(Locale.ROOT,"%.1f",Math.sqrt(pos(e,world(p)).distance2(pos(p,world(p)))))+".");
      if(loaded&&role!=null){run(p,"execute store result score @s hcm_qmark_probe run function capitale:quest/markers/giver_available/"+role);say(p,"Offre disponible "+role+"="+score(p,"hcm_qmark_probe")+".");}
    });
    System.out.println("[Indivis REPERES HF12] diagnostic compte="+uuid(p)+" personnage="+ch+" QMARK2="+loaded+" NPC proches="+near.size()+" étatGreffier="+score(p,"hcq_dqhc05")+" délaiGreffier="+score(p,"hcd_dqhc05"));
  }
  static void reset(){CACHE.clear();ERRORS.clear();seconds=passes=inspections=0;}
}
