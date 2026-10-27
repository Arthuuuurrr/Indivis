package net.tompsen.nexuscharacters;

import java.util.concurrent.ConcurrentHashMap;
import java.lang.invoke.MethodHandles;

/** Only the three literal class lookups on hot client paths are redirected.
 * Resolve/initialise using the same Nexus loader; never cache lookup failures,
 * config values, model objects or resource-dependent appearance information.
 */
public final class RenderClassLookup {
    private static final ConcurrentHashMap<String,Class<?>> CLASSES=new ConcurrentHashMap<>(4);
    private static final MethodHandles.Lookup LOOKUP=MethodHandles.lookup();
    private RenderClassLookup() {}
    public static Class<?> find(String name) throws ClassNotFoundException {
        Class<?> found=CLASSES.get(name);
        if(found!=null) {
            // Preserve Class.forName's initialization barrier even if a class
            // initializer recursively resolved itself before completion.
            try {LOOKUP.ensureInitialized(found);return found;}
            catch(IllegalAccessException inaccessible) {
                return Class.forName(name,true,RenderClassLookup.class.getClassLoader());
            }
        }
        Class<?> resolved=Class.forName(name,true,RenderClassLookup.class.getClassLoader());
        Class<?> existing=CLASSES.putIfAbsent(name,resolved);
        return existing==null?resolved:existing;
    }
}
