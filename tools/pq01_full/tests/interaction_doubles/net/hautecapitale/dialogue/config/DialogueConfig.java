package net.hautecapitale.dialogue.config;
/** Doublure de configuration, uniquement dans le classpath du banc. */
public final class DialogueConfig {
  public static boolean enabled=true;
  private static final DialogueConfig INSTANCE=new DialogueConfig();
  public static DialogueConfig get(){return INSTANCE;}
  public boolean capture(Object npc){return enabled;}
}
