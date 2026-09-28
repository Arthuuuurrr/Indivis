package net.tompsen.nexuscharacters;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Reflection-only Minecraft ModelPart builder so the compatibility source stays mapping-agnostic. */
public final class BeardVoxelModelFactory {
    public record Cuboid(int u, int v, float x, float y, float z, float w, float h, float d) {}

    private BeardVoxelModelFactory() {}

    public static Cuboid c(float x,float y,float z,float w,float h,float d) {
        return new Cuboid(8,8,x,y,z,w,h,d);
    }
    public static Cuboid jewel(float x,float y,float z,float w,float h,float d) {
        return new Cuboid(40,40,x,y,z,w,h,d);
    }

    public static Object bake(Cuboid... cuboids) {
        return bake(List.of(cuboids));
    }

    public static Object bake(List<Cuboid> cuboids) {
        try {
            Class<?> builderClass = Class.forName("net.minecraft.class_5606");
            Object builder = builderClass.getMethod("method_32108").invoke(null);
            Class<?> dilationClass = Class.forName("net.minecraft.class_5605");
            Field noneField = dilationClass.getField("field_27715");
            Object dilation = noneField.get(null);
            Method uv = builderClass.getMethod("method_32101", int.class, int.class);
            Method add = null;
            for (Method m : builderClass.getMethods()) {
                if (m.getName().equals("method_32098") && m.getParameterCount() == 7) { add = m; break; }
            }
            if (add == null) throw new NoSuchMethodException("class_5606.method_32098");

            for (Cuboid q : cuboids) {
                Object textured = uv.invoke(builder, q.u(), q.v());
                add.invoke(textured, q.x(), q.y(), q.z(), q.w(), q.h(), q.d(), dilation);
            }

            Class<?> modelDataClass = Class.forName("net.minecraft.class_5609");
            Object modelData = modelDataClass.getConstructor().newInstance();
            Object root = modelDataClass.getMethod("method_32111").invoke(modelData);
            Class<?> rootClass = Class.forName("net.minecraft.class_5610");
            Class<?> transformClass = Class.forName("net.minecraft.class_5603");
            Object transform = transformClass.getField("field_27701").get(null);
            Method addChild = null;
            for (Method m : rootClass.getMethods()) {
                if (m.getName().equals("method_32117") && m.getParameterCount() == 3) { addChild = m; break; }
            }
            if (addChild == null) throw new NoSuchMethodException("class_5610.method_32117");
            addChild.invoke(root, "cosmetic", builder, transform);

            Class<?> textureDataClass = Class.forName("net.minecraft.class_5607");
            Method of = null;
            for (Method m : textureDataClass.getMethods()) {
                if (m.getName().equals("method_32110") && m.getParameterCount() == 3) { of = m; break; }
            }
            if (of == null) throw new NoSuchMethodException("class_5607.method_32110");
            Object textured = of.invoke(null, modelData, 64, 64);
            Object rootPart = textureDataClass.getMethod("method_32109").invoke(textured);
            Method child = rootPart.getClass().getMethod("method_32086", String.class);
            return child.invoke(rootPart, "cosmetic");
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Unable to bake beard voxel model", e);
        }
    }

    /** Attach an already-baked ModelPart as a child without depending on mapped ModelPart types. */
    public static Object attach(Object parent, String childName, Object child) {
        if (parent == null || child == null) return parent;
        try {
            Field children = findField(parent.getClass(), "field_3661");
            children.setAccessible(true);
            @SuppressWarnings("unchecked")
            Map<String,Object> existing = (Map<String,Object>) children.get(parent);
            LinkedHashMap<String,Object> copy = new LinkedHashMap<>();
            if (existing != null) copy.putAll(existing);
            copy.put(childName, child);
            children.set(parent, copy);
            return parent;
        } catch (Throwable t) {
            return parent;
        }
    }

    private static Field findField(Class<?> c, String name) throws NoSuchFieldException {
        for (Class<?> k = c; k != null; k = k.getSuperclass()) {
            try { return k.getDeclaredField(name); }
            catch (NoSuchFieldException ignored) {}
        }
        throw new NoSuchFieldException(name);
    }
}
