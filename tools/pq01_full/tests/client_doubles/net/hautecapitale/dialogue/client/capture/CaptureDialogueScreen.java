package net.hautecapitale.dialogue.client.capture;
public final class CaptureDialogueScreen extends net.minecraft.class_437 {
  public final Panel panneau=new Panel();
  public static final class Panel {
    public String name="";public boolean waiting=true,last=true,typing=false;public int pagesAdvanced;
    public void nom(String name){this.name=name;}
    public void attente(boolean value){waiting=value;}
    public boolean dernierePage(){return last;}
    public boolean typewriterActif(){return typing;}
    public boolean avancer(){pagesAdvanced++;last=true;return true;}
  }
}
