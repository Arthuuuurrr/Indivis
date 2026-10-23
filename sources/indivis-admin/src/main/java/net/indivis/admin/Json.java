package net.indivis.admin;
import java.util.*;

/** Sérialisation des dialogues ; aucun fragment de commande ne provient d'un nom de joueur. */
public final class Json {
  private Json() {}
  public static String encode(Object value) {
    if(value==null)return "null";
    if(value instanceof String s)return quote(s);
    if(value instanceof Number||value instanceof Boolean)return value.toString();
    if(value instanceof Map<?,?> map) {
      List<String> fields=new ArrayList<>();
      for(var e:map.entrySet())fields.add(quote(String.valueOf(e.getKey()))+":"+encode(e.getValue()));
      return "{"+String.join(",",fields)+"}";
    }
    if(value instanceof Collection<?> list)return "["+String.join(",",list.stream().map(Json::encode).toList())+"]";
    throw new IllegalArgumentException("Type JSON : "+value.getClass());
  }
  public static String quote(String value) {
    StringBuilder s=new StringBuilder("\"");
    for(char c:value.toCharArray())switch(c) {
      case '"'->s.append("\\\"");case '\\'->s.append("\\\\");case '\n'->s.append("\\n");
      case '\r'->s.append("\\r");case '\t'->s.append("\\t");
      default->{if(c<32)s.append(String.format("\\u%04x",(int)c));else s.append(c);}
    }
    return s.append('"').toString();
  }
}
