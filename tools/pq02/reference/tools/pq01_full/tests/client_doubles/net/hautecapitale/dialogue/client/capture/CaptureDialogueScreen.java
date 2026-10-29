package net.hautecapitale.dialogue.client.capture;
public final class CaptureDialogueScreen extends net.minecraft.class_437 {
  public final Panel panneau=new Panel();public int versionVue=1;
  public boolean fermetureNotifiee;
  public CaptureDialogueScreen(){}
  public CaptureDialogueScreen(String name){panneau.name=name;}
  @Override public void method_25419(){fermetureNotifiee=true;net.hautecapitale.dialogue.client.DialogueClientState.demanderFin(net.hautecapitale.dialogue.session.CloseReason.ESC);super.method_25419();}
  public void fermerSansPrevenir(){fermetureNotifiee=true;super.method_25419();}
  public static final class Font {public int method_1727(String text){return text.length()*6;}}
  public static final class Panel {
    public int choiceAt=-1;public boolean history;public final Font textRenderer=new Font();public boolean typewriter=true;public String name="";public boolean waiting=true,last=true,typing=false;public int pagesAdvanced;
    public void nom(String name){this.name=name;}
    public void attente(boolean value){waiting=value;}
    public boolean historiqueVisible(){return history;}public int choixSous(double x,double y){return choiceAt;}public boolean dernierePage(){return last;}
    public boolean typewriterActif(){return typing;}
    public boolean avancer(){pagesAdvanced++;last=true;return true;}
  }
}

