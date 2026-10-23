package net.hautecapitale.dialogue.client;
public final class DialogueClientState {
  private static String pnjNom="Géraud Rivet";
  private static long fenetreJusquaMs;
  public static boolean active=true;public static int cleared;
  public static boolean captureActive(){return active;}
  public static void onChoixEnvoye(boolean close,String answer){if(close||!answer.isEmpty())throw new AssertionError("réponse fictive");cleared++;}
  public static String name(){return pnjNom;}
  public static long deadline(){return fenetreJusquaMs;}
}
