package net.tompsen.nexuscharacters;

public final class FacialHairRaceSupport {
    private FacialHairRaceSupport() {}

    public static boolean allows(CharacterRace race) {
        return race == CharacterRace.HUMAN || race == CharacterRace.NORDIC;
    }

    public static int normalize(CharacterRace race, int value) {
        return allows(race) ? Math.max(0, Math.min(6, value)) : 0;
    }

    public static int next(CharacterRace race, int value) {
        return allows(race) ? (normalize(race, value) + 1) % 7 : 0;
    }
}
