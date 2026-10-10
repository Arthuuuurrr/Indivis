package net.tompsen.nexuscharacters;

import java.lang.reflect.Field;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/** Cache field resolution, including absent optional fields, never instance values. */
public final class LowerJointFieldAccess {
    private static final ClassValue<ConcurrentHashMap<String, Optional<Field>>> FIELDS = new ClassValue<>() {
        protected ConcurrentHashMap<String, Optional<Field>> computeValue(Class<?> type) { return new ConcurrentHashMap<>(); }
    };

    private static Field resolve(Class<?> type, String name) throws NoSuchFieldException {
        var fields = FIELDS.get(type);
        Optional<Field> found = fields.get(name);
        if (found == null) found = fields.computeIfAbsent(name, key -> {
            for (Class<?> c = type; c != null; c = c.getSuperclass()) {
                try {
                    Field field = c.getDeclaredField(key);
                    field.setAccessible(true);
                    return Optional.of(field);
                } catch (NoSuchFieldException absent) { /* Try the next superclass once. */ }
            }
            return Optional.empty();
        });
        if (found.isEmpty()) throw new NoSuchFieldException(name);
        return found.get();
    }

    public static Object field(Object target, String name) throws Exception {
        return resolve(target instanceof Class<?> type ? type : target.getClass(), name)
                .get(target instanceof Class<?> ? null : target);
    }

    public static void set(Object target, String name, Object value) throws Exception {
        resolve(target.getClass(), name).set(target, value);
    }
}
