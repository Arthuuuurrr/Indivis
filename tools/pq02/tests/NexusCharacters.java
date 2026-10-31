package net.tompsen.nexuscharacters;
import java.util.*;
/** TEST ONLY: sélection de profils synthétique, politique de persistance PRE35 réelle ailleurs. */
public final class NexusCharacters {
 public static final org.slf4j.Logger LOGGER=org.slf4j.LoggerFactory.getLogger("NexusFixture");
 public record Character(UUID id,String name,String race){}
 private static final Map<Object,Character> selected=new IdentityHashMap<>();
 public static void select(Object p,UUID id){selected.put(p,new Character(id,"Personnage test","HUMAN"));}
 public static Character getSelectedCharacter(Object p){return selected.get(p);}
 /** TEST ONLY: same INVOKESTATIC site as the real 100-tick Nexus callback. */
 public static void periodicSave(net.minecraft.class_3222 p,CharacterDto character){ProfileStateBridge.save(p,character);}
}
