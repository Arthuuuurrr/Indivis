package net.indivis.admin;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public final class Catalog {
  public record Action(String id,String category,String label,String function,List<String> watch,String detail) {}
  public static final List<Action> ACTIONS;
  public static final Map<String,String> CATEGORIES;
  public static final List<String> SCORES=List.of("CAP_RANGSOCIAL","CAP_CRIME","CAP_ETAT","CAP_AMENDE","CAP_COMPARUTION",
      "CAP_FUITE_JUSTICE","CAP_RETENTION_TRIBUNAL","CAP_QUESTLOCK","CAP_SANCTIONLOCK","CAP_ARREST_PENDING",
      "CAP_JUSTICE_LEVEL","CAP_ROLE","CAP_DEBUG","CAP_PROFILE_LOCK","CAP_CUR_BANK","REP_COURONNE","REP_GARDECOEUR",
      "REP_GARDEPORT","REP_GARDEPROFONDEURS","REP_PROFONDEURS","ACCESS_CAPITALE","ACCESS_QH_NORD","ACCESS_QH_SUD",
      "ACCESS_PALAIS","ACCESS_ARCHIVES","ACCESS_BANQUE","ACCESS_TRIBUNAL","ACCESS_COEUR","ACCESS_SEUILS");
  static {
    LinkedHashMap<String,String> c=new LinkedHashMap<>();
    c.put("rank","Rang social");c.put("crime","Criminalité");c.put("reputations","Réputations");c.put("access","Autorisations d’accès");
    c.put("justice","Justice / assistance");c.put("currency","Monnaie impériale");c.put("tools","Diagnostic");
    c.put("rep/couronne","Couronne");c.put("rep/gardecoeur","Garde du Cœur");c.put("rep/gardeport","Garde du Port");
    c.put("rep/gardeprofondeurs","Garde des Profondeurs");c.put("rep/profondeurs","Profondeurs");
    CATEGORIES=Collections.unmodifiableMap(c);
    try(InputStream in=Catalog.class.getResourceAsStream("/admin_actions.tsv")) {
      if(in==null)throw new IllegalStateException("Catalogue d'administration absent");
      List<Action> list=new ArrayList<>();Set<String> ids=new HashSet<>(),functions=new HashSet<>();
      for(String line:new String(in.readAllBytes(),StandardCharsets.UTF_8).split("\n")) {
        if(line.isBlank())continue;String[] p=line.split("\t",-1);
        if(p.length!=6||!p[0].matches("a[0-9]{3}")||!p[3].matches("capitale:[a-z0-9_/]+_self")||!ids.add(p[0])||!functions.add(p[3])||!c.containsKey(p[1]))throw new IllegalStateException("Catalogue invalide : "+line);
        list.add(new Action(p[0],p[1],p[2],p[3],p[4].isEmpty()?List.of():List.of(p[4].split(",")),p[5]));
      }
      ACTIONS=List.copyOf(list);
    }catch(IOException e){throw new ExceptionInInitializerError(e);}
  }
  public static String rank(Integer value) {
    if(value==null)return "non initialisé";
    for(Action a:ACTIONS)if(a.category.equals("rank")&&a.function.equals("capitale:rank/set_"+value+"_self"))return a.label;
    return value.toString();
  }
  private Catalog() {}
}
