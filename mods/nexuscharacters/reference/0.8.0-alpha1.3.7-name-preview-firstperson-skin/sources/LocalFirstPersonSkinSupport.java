package net.tompsen.nexuscharacters;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.UUID;

public final class LocalFirstPersonSkinSupport {
    private static volatile Method RESOLVE_DTO;
    private static volatile Method RESOLVE_PAYLOAD;

    private LocalFirstPersonSkinSupport() {}

    public static Object override(Object player, Object vanillaSkin) {
        if (player == null) return vanillaSkin;
        try {
            Object client = minecraftClient();
            if (client == null) return vanillaSkin;
            Object localPlayer = readField(client, "field_1724");
            if (localPlayer != player) return vanillaSkin;

            UUID playerId = entityUuid(player);
            if (playerId == null) return vanillaSkin;

            Object payload = appearancePayload(playerId);
            Object fromPayload = resolvePayload(payload);
            if (fromPayload != null) return fromPayload;

            Object selected = selectedCharacter();
            if (selected != null) {
                Object fromDto = resolveDto(selected, playerId);
                if (fromDto != null) return fromDto;
            }
        } catch (Throwable ignored) {}
        return vanillaSkin;
    }

    private static Object minecraftClient() throws Exception {
        Class<?> c = Class.forName("net.minecraft.class_310");
        Method m = c.getDeclaredMethod("method_1551");
        m.setAccessible(true);
        return m.invoke(null);
    }

    private static Object selectedCharacter() throws Exception {
        Class<?> c = Class.forName("net.tompsen.nexuscharacters.NexusCharacters");
        Field f = c.getDeclaredField("selectedCharacter");
        f.setAccessible(true);
        return f.get(null);
    }

    private static Object appearancePayload(UUID uuid) throws Exception {
        Class<?> c = Class.forName("net.tompsen.nexuscharacters.CharacterAppearanceClient");
        Method m = c.getMethod("get", UUID.class);
        return m.invoke(null, uuid);
    }

    private static Object readField(Object instance, String name) throws Exception {
        Class<?> c = instance.getClass();
        while (c != null) {
            try {
                Field f = c.getDeclaredField(name);
                f.setAccessible(true);
                return f.get(instance);
            } catch (NoSuchFieldException ignored) {
                c = c.getSuperclass();
            }
        }
        return null;
    }

    private static UUID entityUuid(Object player) throws Exception {
        Class<?> c = player.getClass();
        while (c != null) {
            try {
                Method m = c.getDeclaredMethod("method_5667");
                m.setAccessible(true);
                Object v = m.invoke(player);
                return v instanceof UUID u ? u : null;
            } catch (NoSuchMethodException ignored) {
                c = c.getSuperclass();
            }
        }
        return null;
    }

    private static Object resolveDto(Object dto, UUID uuid) throws Exception {
        Method m = RESOLVE_DTO;
        if (m == null) {
            Class<?> remote = Class.forName("net.tompsen.nexuscharacters.RemoteAppearanceSupport");
            Class<?> dtoClass = Class.forName("net.tompsen.nexuscharacters.CharacterDto");
            m = remote.getDeclaredMethod("resolveDtoSkin", dtoClass, UUID.class);
            m.setAccessible(true);
            RESOLVE_DTO = m;
        }
        return m.invoke(null, dto, uuid);
    }

    private static Object resolvePayload(Object payload) throws Exception {
        if (payload == null) return null;
        Method removed = payload.getClass().getMethod("removed");
        if (Boolean.TRUE.equals(removed.invoke(payload))) return null;
        Method m = RESOLVE_PAYLOAD;
        if (m == null) {
            Class<?> remote = Class.forName("net.tompsen.nexuscharacters.RemoteAppearanceSupport");
            Class<?> payloadClass = Class.forName("net.tompsen/nexuscharacters/CharacterAppearancePayload".replace('/', '.'));
            m = remote.getDeclaredMethod("resolvePayloadSkin", payloadClass);
            m.setAccessible(true);
            RESOLVE_PAYLOAD = m;
        }
        return m.invoke(null, payload);
    }
}
