package fr.arthur.capitale.rphud;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/** Preserve the HUD's declared-then-public lookup; only cache resolution, never scores. */
public final class NoArgMethodCache {
    private static final ClassValue<ConcurrentHashMap<String, Optional<Method>>> METHODS = new ClassValue<>() {
        protected ConcurrentHashMap<String, Optional<Method>> computeValue(Class<?> type) { return new ConcurrentHashMap<>(); }
    };

    public static Method find(Class<?> type, String name) {
        if (type == null || name == null) return null;
        var methods = METHODS.get(type);
        Optional<Method> found = methods.get(name);
        if (found == null) found = methods.computeIfAbsent(name, key -> {
            for (Class<?> c = type; c != null; c = c.getSuperclass()) {
                try {
                    Method method = c.getDeclaredMethod(key);
                    if (method.getParameterCount() == 0) return Optional.of(method);
                } catch (Throwable absent) { /* Match the existing optional lookup. */ }
                try {
                    Method method = c.getMethod(key);
                    if (method.getParameterCount() == 0) return Optional.of(method);
                } catch (Throwable absent) { /* Match the existing optional lookup. */ }
            }
            return Optional.empty();
        });
        return found.orElse(null);
    }
}
