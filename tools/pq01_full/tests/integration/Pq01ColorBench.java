package net.hautecapitale.quetes.sequence;
import java.lang.reflect.*;
/** Classification native de la réponse verte dans le vrai Dialogue b10. */
public final class Pq01ColorBench {
  public static void main(String[] args)throws Exception {
    Class.forName("net.minecraft.class_155").getMethod("method_36208").invoke(null);
    Class.forName("net.minecraft.class_2966").getMethod("method_12851").invoke(null);
    Class<?> style=Class.forName("net.minecraft.class_2583"),format=Class.forName("net.minecraft.class_124");
    Object empty=style.getField("field_24360").get(null);
    Object green=format.getField("field_1060").get(null);
    Object value=style.getMethod("method_10977",format).invoke(empty,green);
    Method method=Class.forName("net.hautecapitale.dialogue.client.capture.MessageCapture").getDeclaredMethod("typeDe",style,String.class);method.setAccessible(true);
    Object kind=method.invoke(null,value,"Terminer l’entretien et lancer l’amarrage.");
    if(!kind.toString().equals("QUETE"))throw new AssertionError("expected native green QUETE, got "+kind);
    System.out.println("PASS native Dialogue b10 choice color: Minecraft GREEN -> QUETE");
  }
}
