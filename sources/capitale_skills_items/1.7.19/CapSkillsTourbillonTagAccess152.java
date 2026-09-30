package fr.hautecapitale.skillsitems;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Reflection bridge for Minecraft command tags.
 *
 * 1.7.19 performance change: reflection lookup results are cached per runtime
 * class. The fallback names, invocation order and return semantics are kept
 * identical to 1.7.18; only repeated Class.get* scanning is removed.
 */
public final class CapSkillsTourbillonTagAccess152 {
    private static volatile boolean accessFailureLogged;

    /*
     * Weak outer keys avoid pinning a runtime class loader. Inner maps are
     * concurrent because the same bridge can be reached by more than one
     * callback/thread. Optional.empty() caches misses as well as hits: a failed
     * Yarn/intermediary candidate must not be rescanned every tick.
     */
    private static final Map<Class<?>, Map<String, Optional<Method>>> METHOD_CACHE =
            Collections.synchronizedMap(new WeakHashMap<>());
    private static final Map<Class<?>, Map<String, Optional<Field>>> FIELD_CACHE =
            Collections.synchronizedMap(new WeakHashMap<>());

    private CapSkillsTourbillonTagAccess152() {}

    public static boolean hasTag(Object player, String tag) {
        return tags(player).contains(tag);
    }

    public static Set<String> snapshot(Object player) {
        Collection<?> raw = rawTags(player);
        if (raw == null) return Collections.emptySet();
        Set<String> out = new LinkedHashSet<>();
        for (Object value : raw) if (value != null) out.add(String.valueOf(value));
        return out;
    }

    public static boolean removeTag(Object player, String tag) {
        if (player == null || tag == null) return false;
        for (String name : new String[]{"removeCommandTag", "method_5738"}) {
            Method method = findOneArg(player.getClass(), name, String.class);
            if (method == null) continue;
            try {
                method.setAccessible(true);
                Object result = method.invoke(player, tag);
                return !(result instanceof Boolean b) || b;
            } catch (Throwable ignored) {
                // Preserve 1.7.18 fallback behavior: try the next mapping name.
            }
        }
        Collection<?> raw = rawTags(player);
        if (raw != null) {
            try {
                @SuppressWarnings("unchecked")
                Collection<Object> mutable = (Collection<Object>) raw;
                return mutable.remove(tag);
            } catch (Throwable ignored) {
                // Preserve 1.7.18 behavior.
            }
        }
        return false;
    }

    private static Set<String> tags(Object player) {
        Collection<?> raw = rawTags(player);
        if (raw == null || raw.isEmpty()) return Collections.emptySet();
        Set<String> out = new LinkedHashSet<>();
        for (Object value : raw) if (value != null) out.add(String.valueOf(value));
        return out;
    }

    private static Collection<?> rawTags(Object player) {
        if (player == null) return null;
        for (String name : new String[]{"getCommandTags", "method_5752"}) {
            Method method = findNoArg(player.getClass(), name);
            if (method == null) continue;
            try {
                method.setAccessible(true);
                Object result = method.invoke(player);
                if (result instanceof Collection<?> collection) return collection;
            } catch (Throwable ignored) {}
        }
        for (String name : new String[]{"commandTags", "field_6029"}) {
            Field field = findField(player.getClass(), name);
            if (field == null) continue;
            try {
                field.setAccessible(true);
                Object result = field.get(player);
                if (result instanceof Collection<?> collection) return collection;
            } catch (Throwable ignored) {}
        }
        if (!accessFailureLogged) {
            accessFailureLogged = true;
            System.err.println("[CapSkills][Tourbillon-BC] SERVER cannot access command tags on "
                    + player.getClass().getName() + "; checked method_5752 and field_6029.");
        }
        return null;
    }

    private static Method findNoArg(Class<?> type, String name) {
        String key = "0:" + name;
        Map<String, Optional<Method>> cache = methodCache(type);
        Optional<Method> cached = cache.get(key);
        if (cached != null) return cached.orElse(null);

        Method found = null;
        Class<?> cursor = type;
        while (cursor != null) {
            try {
                Method m = cursor.getDeclaredMethod(name);
                if (m.getParameterCount() == 0) { found = m; break; }
            } catch (Throwable ignored) {}
            try {
                Method m = cursor.getMethod(name);
                if (m.getParameterCount() == 0) { found = m; break; }
            } catch (Throwable ignored) {}
            cursor = cursor.getSuperclass();
        }
        cache.putIfAbsent(key, Optional.ofNullable(found));
        return found;
    }

    private static Method findOneArg(Class<?> type, String name, Class<?> argumentType) {
        String key = "1:" + name + ':' + argumentType.getName();
        Map<String, Optional<Method>> cache = methodCache(type);
        Optional<Method> cached = cache.get(key);
        if (cached != null) return cached.orElse(null);

        Method found = null;
        Class<?> cursor = type;
        while (cursor != null && found == null) {
            for (Method method : cursor.getDeclaredMethods()) {
                if (!method.getName().equals(name) || method.getParameterCount() != 1) continue;
                Class<?> parameter = method.getParameterTypes()[0];
                if (parameter.isAssignableFrom(argumentType) || argumentType.isAssignableFrom(parameter)) {
                    found = method; break;
                }
            }
            if (found == null) {
                for (Method method : cursor.getMethods()) {
                    if (!Modifier.isPublic(method.getModifiers())
                            || !method.getName().equals(name)
                            || method.getParameterCount() != 1) continue;
                    Class<?> parameter = method.getParameterTypes()[0];
                    if (parameter.isAssignableFrom(argumentType) || argumentType.isAssignableFrom(parameter)) {
                        found = method; break;
                    }
                }
            }
            cursor = cursor.getSuperclass();
        }
        cache.putIfAbsent(key, Optional.ofNullable(found));
        return found;
    }

    private static Field findField(Class<?> type, String name) {
        Map<String, Optional<Field>> cache = fieldCache(type);
        Optional<Field> cached = cache.get(name);
        if (cached != null) return cached.orElse(null);

        Field found = null;
        Class<?> cursor = type;
        while (cursor != null) {
            try { found = cursor.getDeclaredField(name); break; }
            catch (Throwable ignored) { cursor = cursor.getSuperclass(); }
        }
        cache.putIfAbsent(name, Optional.ofNullable(found));
        return found;
    }

    private static Map<String, Optional<Method>> methodCache(Class<?> type) {
        synchronized (METHOD_CACHE) {
            return METHOD_CACHE.computeIfAbsent(type, ignored -> new ConcurrentHashMap<>());
        }
    }

    private static Map<String, Optional<Field>> fieldCache(Class<?> type) {
        synchronized (FIELD_CACHE) {
            return FIELD_CACHE.computeIfAbsent(type, ignored -> new ConcurrentHashMap<>());
        }
    }
}
