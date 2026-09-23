package net.hautecapitale.fusils.skills;

import net.minecraft.class_1309;
import net.minecraft.class_1657;
import net.minecraft.class_1937;

/** Server authority for firearm access granted by CapSkills. */
public final class CapSkillsFusilAuthority {
    public static final String USE_TAG = "capskills.fusils.use";
    public static final String SKILL_PREFIX = "capskills.fusils.";

    private CapSkillsFusilAuthority() {}

    public static boolean canFire(class_1309 entity) {
        if (entity == null) return false;
        class_1937 world = entity.method_73183();
        if (world != null && world.method_8608()) return true;
        if (!(entity instanceof class_1657)) return true;
        return entity.method_5752().contains(USE_TAG);
    }

    public static boolean canUseSkill(class_1309 entity, String skillId) {
        if (!canFire(entity)) return false;
        if (!(entity instanceof class_1657)) return true;
        return skillId != null && entity.method_5752().contains(SKILL_PREFIX + skillId);
    }
}
