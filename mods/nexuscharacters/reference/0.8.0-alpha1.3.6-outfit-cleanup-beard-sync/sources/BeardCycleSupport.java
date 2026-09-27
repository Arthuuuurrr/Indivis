package net.tompsen.nexuscharacters;

public final class BeardCycleSupport {
    private BeardCycleSupport() {}
    public static CharacterCosmetics.BeardStyle nextForRace(CharacterRace race, CharacterCosmetics.BeardStyle current) {
        CharacterCosmetics.BeardStyle NONE = CharacterCosmetics.BeardStyle.NONE;
        CharacterCosmetics.BeardStyle SHORT = CharacterCosmetics.BeardStyle.SHORT;
        CharacterCosmetics.BeardStyle FULL = CharacterCosmetics.BeardStyle.FULL;
        CharacterCosmetics.BeardStyle FORKED = CharacterCosmetics.BeardStyle.FORKED;
        CharacterCosmetics.BeardStyle BRAIDED = CharacterCosmetics.BeardStyle.BRAIDED;
        CharacterCosmetics.BeardStyle LONG = CharacterCosmetics.BeardStyle.valueOf("LONG");
        if (NordicBeardSupport.isHuman(race)) return current == LONG ? NONE : LONG;
        if (NordicBeardSupport.isNordic(race)) {
            if (current == NONE) return SHORT;
            if (current == SHORT) return FULL;
            if (current == FULL) return LONG;
            return NONE;
        }
        if (NordicBeardSupport.isDwarf(race)) {
            if (current == NONE) return SHORT;
            if (current == SHORT) return FULL;
            if (current == FULL) return LONG;
            if (current == LONG) return FORKED;
            if (current == FORKED) return BRAIDED;
            return SHORT;
        }
        return NONE;
    }
}
