package net.hautecapitale.quetes.sequence;

import java.util.*;
import net.hautecapitale.quetes.sequence.Pq01Motion.Point;
import static net.hautecapitale.quetes.sequence.SequenceBridge.*;
import static net.hautecapitale.quetes.sequence.Pq01Bridge.*;
import static net.hautecapitale.quetes.sequence.Pq02Runtime.*;

/** Administration ponctuelle des repères existants ; aucun appel depuis le tick de jeu. */
final class Pq02Setup {
 static final String WP="wp_leovic_registre_port_";
 record Scan(Map<String,List<Object>> markers,List<Object> guides) {}
 static Scan scan(Object p){
  Map<String,List<Object>> found=new LinkedHashMap<>();for(String key:ROUTE)found.put(key,new ArrayList<>());
  List<Object> guides=new ArrayList<>();
  // Itération des seules entités chargées : aucune recherche de chunks ni chargement du monde.
  for(Object e:((net.minecraft.class_3218)world(p)).method_27909()){
   Set<String> all=tags(e);boolean guide=all.contains("guide_leovic_registre_port");
   String key=null;for(String tag:all)if(tag.startsWith(WP)&&found.containsKey(tag.substring(WP.length()))){key=tag.substring(WP.length());break;}
   if(key==null&&!guide)continue;String kind=Pq01EntityFactory.registryId(e);
   if(kind.equals("minecraft:marker"))for(String tag:all)if(tag.startsWith(WP)&&found.containsKey(tag.substring(WP.length())))found.get(tag.substring(WP.length())).add(e);
   if(guide&&kind.equals("minecraft:armor_stand"))guides.add(e);
  }
  return new Scan(found,guides);
 }
 static Point cached(Object p,String key){Object n=child(child(config(p),"points"),key);if(n==null)return null;Point at=point(n);return at.dimension().equals(dimension(world(p)))&&Double.isFinite(at.x())&&Double.isFinite(at.y())&&Double.isFinite(at.z())?at:null;}
 static Point resolved(Object p,Scan scan,String key){List<Object> found=scan.markers.get(key);return found.size()==1?pos(found.get(0),world(p)):found.isEmpty()?cached(p,key):null;}
 static List<String> ambiguous(Scan scan){List<String> keys=new ArrayList<>();for(String key:ROUTE)if(scan.markers.get(key).size()>1)keys.add(label(key));return keys;}
 static List<String> missing(Object p,Scan scan){List<String> keys=new ArrayList<>();for(String key:ROUTE)if(!OPTIONAL.contains(key)&&scan.markers.get(key).isEmpty()&&cached(p,key)==null)keys.add(label(key));return keys;}
 static String label(String key){return switch(key){case "a"->"A — départ Léovic";case "d"->"D — pause lore 1";case "h_before"->"H− — approche";case "h"->"H — pause lore 2";case "h_after"->"H+ — sortie";case "m"->"M — magistrat";case "p"->"P — ascenseur";default->key.toUpperCase(Locale.ROOT);};}
 static String coordinates(Point at){return String.format(Locale.ROOT,"%.2f %.2f %.2f",at.x(),at.y(),at.z());}
 static int prepare(Object p){
  Scan scan=scan(p);List<String> duplicate=ambiguous(scan);
  if(!duplicate.isEmpty()||scan.guides.size()>1){say2(p,"Préparation refusée : repères en doublon="+duplicate+", anciens guides chargés="+scan.guides.size()+". Aucun déplacement ni mise en cache effectué.");return 0;}
  Object root=Pq01Nbt.copy(config(p));int loaded=0,remembered=0;
  for(String key:ROUTE){List<Object> found=scan.markers.get(key);if(found.size()==1){GuardNbt.put(GuardNbt.childOrCreate(root,"points"),key,pointNbt(pos(found.get(0),world(p))));loaded++;}else if(cached(p,key)!=null)remembered++;}
  if(loaded>0)GuardNbt.save(server(p),SETUP,root);
  List<String> absent=missing(p,scan);
  if(!absent.isEmpty()){say2(p,"Préparation incomplète : "+loaded+" repères chargés mémorisés ; repères requis absents="+absent+". Charger les portions concernées et relancer depuis n'importe quel point. Aucun repère créé ni ancien guide déplacé.");return 0;}
  say2(p,"Parcours existant prêt : "+loaded+" repères chargés, "+remembered+" mémorisés. Dimension : "+dimension(world(p))+". Aucun waypoint déplacé.");
  for(String key:List.of("a","m","p"))say2(p,label(key)+" : "+coordinates(resolved(p,scan,key)));
  if(scan.guides.isEmpty())say2(p,"Aucun ancien guide chargé : aucune création nécessaire pour l'escorte personnelle.");
  else if(scan.markers.get("a").isEmpty()){say2(p,"Ancien guide conservé : A est mémorisé mais non chargé. Charger la zone de A puis relancer pour le stationner.");return 0;}
  else if(!VIEWS.isEmpty()){say2(p,"Ancien guide conservé pendant une scène PQ02 active ; relancer hors scène pour son stationnement.");return 0;}
  else{Object guide=scan.guides.get(0);Point at=resolved(p,scan,"a");for(String tag:List.of("escort_active","escort_returning","escort_moving"))call(guide,"method_5738",tag);call(guide,"method_5814",at.x(),at.y(),at.z());say2(p,"Ancien guide invisible stationné sur A. Les armures de visualisation sont conservées.");}
  say2(p,"Préparation uniquement : l'escorte démarre dans le dialogue de Léovic ; aucune progression ni récompense attribuée.");return 1;
 }
 static void summary(Object p){Scan scan=scan(p);say2(p,"Repères requis absents/non mémorisés : "+missing(p,scan)+" ; doublons : "+ambiguous(scan)+" ; anciens guides chargés : "+scan.guides.size()+".");}
 static void describe(Object p){Scan scan=scan(p);say2(p,"Repères PQ02 dans "+dimension(world(p))+" : lecture seule des markers, sans sélection des armures de visualisation.");
  for(String key:ROUTE){List<Object> found=scan.markers.get(key);String suffix=OPTIONAL.contains(key)?" (facultatif)":"";
   if(found.size()>1){say2(p,label(key)+suffix+" : AMBIGU — "+found.size()+" markers chargés.");for(Object e:found)say2(p,"  "+uuid(e)+" : "+coordinates(pos(e,world(p))));}
   else{Point at=resolved(p,scan,key);say2(p,label(key)+suffix+" : "+(at==null?"ABSENT — non chargé et non mémorisé":coordinates(at)+(found.isEmpty()?" — mémorisé, marker non chargé":" — chargé")));}
  }
  say2(p,"Anciens guides chargés : "+scan.guides.size()+". Préparer : /function capitale:pq02/admin/preparer_parcours");
 }
}
