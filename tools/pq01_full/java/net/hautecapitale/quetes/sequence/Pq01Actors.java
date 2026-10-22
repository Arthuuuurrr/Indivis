package net.hautecapitale.quetes.sequence;
import java.util.*;
import net.minecraft.class_1297;

/** Contrôles en chemin chaud : aucune réflexion, aucun scan de monde. */
public final class Pq01Actors {
  public static final String TAG="hcm_pq1_actor";
  private static final Set<Object> SELECTORS=Collections.newSetFromMap(new IdentityHashMap<>());
  private Pq01Actors(){}
  public static boolean actor(class_1297 entity){return entity!=null&&entity.method_5752().contains(TAG);}
  public static boolean protectedSelector(Object selector){return SELECTORS.contains(selector);}
  static void protect(Object selector){SELECTORS.add(selector);}
  static void forget(Object selector){SELECTORS.remove(selector);}
  static void reset(){SELECTORS.clear();}
}
