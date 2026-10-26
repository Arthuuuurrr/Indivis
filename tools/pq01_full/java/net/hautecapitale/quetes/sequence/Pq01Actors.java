package net.hautecapitale.quetes.sequence;
import java.util.*;
import net.minecraft.class_1297;
import net.minecraft.class_3222;

/** Contrôles en chemin chaud : aucune réflexion, aucun scan de monde. */
public final class Pq01Actors {
  public static final String TAG="hcm_pq1_actor";
  private static final Set<Object> SELECTORS=Collections.newSetFromMap(new IdentityHashMap<>());
  private static final Map<UUID,UUID> OWNERS=new HashMap<>();
  private static final Map<UUID,Set<UUID>> HIDDEN=new HashMap<>();
  private Pq01Actors(){}
  public static boolean actor(class_1297 entity){return entity!=null&&entity.method_5752().contains(TAG);}
  public static boolean protectedSelector(Object selector){return SELECTORS.contains(selector);}
  static void protect(Object selector){SELECTORS.add(selector);}
  static void forget(Object selector){SELECTORS.remove(selector);}
  static void owner(UUID actor,UUID player){OWNERS.put(actor,player);}
  static void remove(UUID actor){OWNERS.remove(actor);}
  static void view(UUID player,Set<UUID> hidden){HIDDEN.put(player,hidden);}
  static void close(UUID player){HIDDEN.remove(player);}
  /** Refus précoce ; les règles historiques de personnage/quête restent ensuite exécutées. */
  public static boolean visible(class_1297 entity,class_3222 player){
    UUID id=entity.method_5667(),account=player.method_5667(),owner=OWNERS.get(id);
    if(owner!=null)return owner.equals(account);
    if(actor(entity))return false; // Copie orpheline : aucun observateur autorisé.
    Set<UUID> hidden=HIDDEN.get(account);
    return hidden==null||!hidden.contains(id)||!Pq01Bridge.ownerActive(player);
  }
  static void reset(){SELECTORS.clear();OWNERS.clear();HIDDEN.clear();}
}
