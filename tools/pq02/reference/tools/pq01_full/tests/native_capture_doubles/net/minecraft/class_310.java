package net.minecraft;
/** Client sans rendu : seul le cycle de l'écran et la sortie réseau sont simulés. */
public class class_310 {
 public static final class_310 INSTANCE=new class_310();
 public class_437 field_1755;
 public static class_310 method_1551(){return INSTANCE;}
 public class_634 method_1562(){return new class_634();}
 public void method_1507(class_437 screen){
  class_437 old=field_1755;field_1755=screen;
  if(old!=null)net.hautecapitale.dialogue.client.DialogueClientState.onEcranFerme(old);
  if(screen instanceof net.hautecapitale.dialogue.client.capture.CaptureDialogueScreen)net.hautecapitale.dialogue.client.DialogueClientState.onEcranOuvert(screen);
 }
}
