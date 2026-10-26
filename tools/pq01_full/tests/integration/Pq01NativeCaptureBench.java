package net.hautecapitale.quetes.sequence;
import java.lang.reflect.*;
import java.util.*;
import static net.hautecapitale.quetes.sequence.SequenceBridge.call;
/** Vrais State et MessageCapture b10 ; seuls les types d'écran absents sont doublés. */
public final class Pq01NativeCaptureBench {
  static Class<?> state;static int checks;
  static void set(Class<?> c,String name,Object value)throws Exception{Field f=c.getDeclaredField(name);f.setAccessible(true);f.set(null,value);}
  static Object stat(String cls,String method,Object...args)throws Exception{return call(Class.forName(cls),method,args);}
  static void check(boolean ok,String label){checks++;if(!ok)throw new AssertionError(label);}
  static Object text(String json)throws Exception{
    Object codec=Pq01Bridge.en("net.minecraft.class_8824","field_46597");Object ops=Pq01Bridge.en("com.mojang.serialization.JsonOps","INSTANCE");
    return call(call(codec,"parse",ops,stat("com.google.gson.JsonParser","parseString",json)),"getOrThrow");
  }
  public static void main(String[] args)throws Exception {
    Class.forName("net.minecraft.class_155").getMethod("method_36208").invoke(null);Class.forName("net.minecraft.class_2966").getMethod("method_12851").invoke(null);
    Class<?> settings=Class.forName("net.hautecapitale.dialogue.client.DialogueClientSettings");Constructor<?> ctor=settings.getDeclaredConstructor();ctor.setAccessible(true);set(settings,"instance",ctor.newInstance());
    state=Class.forName("net.hautecapitale.dialogue.client.DialogueClientState");
    set(state,"mode",Pq01Bridge.en("net.hautecapitale.dialogue.session.DialogueMode","CAPTURE"));set(state,"etat",Pq01Bridge.en("net.hautecapitale.dialogue.client.DialogueClientState$Etat","DIALOGUE_ACTIVE"));set(state,"pnjNom","Géraud Rivet");
    check(Boolean.TRUE.equals(call(state,"captureActive")),"capture native active");
    call(state,"onChoixEnvoye",false,"");Object txt=stat("net.minecraft.class_2561","method_43470","[Géraud Rivet] Réponse du matelot.");
    Object classified=stat("net.hautecapitale.dialogue.client.capture.MessageCapture","classer",txt,"Géraud Rivet",true,List.of());call(state,"onMessageCapture",classified);
    check(((List<?>)call(state,"repliquesCapturees")).size()==1,"réplique classée par b10");
    Object button=text("{\"text\":\"[Autre question]\",\"color\":\"yellow\",\"click_event\":{\"action\":\"run_command\",\"command\":\"/trigger QuestChoix set 1000000001\"}}");
    call(state,"onMessageCapture",stat("net.hautecapitale.dialogue.client.capture.MessageCapture","classer",button,"Géraud Rivet",true,List.of()));
    check(((List<?>)call(state,"choixCaptures")).size()==1,"bouton réel Minecraft classé par b10");
    if(args.length>0){
      Object rows=stat("com.google.gson.JsonParser","parseString",java.nio.file.Files.readString(java.nio.file.Path.of(args[0])));
      for(Object row:(Iterable<?>)call(rows,"getAsJsonArray")){
        String key=(String)call(call(row,"get","key"),"getAsString");
        for(Object line:(Iterable<?>)call(call(row,"get","messages"),"getAsJsonArray")){
          Object message=text((String)call(line,"getAsString"));
          if(!Pq01Client.message(message,false))continue;
          Object capture=stat("net.hautecapitale.dialogue.client.capture.MessageCapture","classer",message,call(state,"pnjNom"),true,List.of());call(state,"onMessageCapture",capture);
        }
        int expected=((Number)call(call(row,"get","choices"),"getAsInt")).intValue();
        List<?> captured=(List<?>)call(state,"choixCaptures");check(captured.size()==expected,"questions natives pour "+key+" : "+captured.size()+"/"+expected);
        check(call(state,"pnjNom").equals(call(call(row,"get","speaker"),"getAsString")),"interlocuteur natif pour "+key);
      }
    }
    System.out.println("PASS "+checks+" native b10 capture assertions; no graphical client.");
  }
}
