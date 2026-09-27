package net.tompsen.nexuscharacters;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public final class LongBeardModelSupport {
    private LongBeardModelSupport() {}

    public static boolean isLong(String style) {
        if (style == null) return false;
        int sep = style.indexOf('#');
        if (sep >= 0) style = style.substring(0, sep);
        return "long".equals(style.trim().toLowerCase(Locale.ROOT));
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static Map extendBeardMap(Map original) {
        LinkedHashMap copy = new LinkedHashMap(original);
        copy.put("long", create());
        return java.util.Collections.unmodifiableMap(copy);
    }

    public static Object createForStyle(String style) {
        try {
            String base = style == null ? "" : style;
            int sep = base.indexOf('#');
            if (sep >= 0) base = base.substring(0, sep);
            base = base.trim().toLowerCase(Locale.ROOT);
            if ("long".equals(base)) return create();
            Class<?> shape = Class.forName("net.tompsen.nexuscharacters.CharacterCosmeticFeatureRenderer$BeardShape");
            @SuppressWarnings({"rawtypes", "unchecked"})
            Object enumValue = Enum.valueOf((Class<? extends Enum>) shape.asSubclass(Enum.class), base.toUpperCase(Locale.ROOT));
            Class<?> renderer = Class.forName("net.tompsen.nexuscharacters.CharacterCosmeticFeatureRenderer");
            Method method = renderer.getDeclaredMethod("createBeardPart", shape);
            method.setAccessible(true);
            return method.invoke(null, enumValue);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Unable to create beard model for style " + style, e);
        }
    }

    public static Object create() {
        try {
            Class<?> builderClass = Class.forName("net.minecraft.class_5606");
            Object builder = builderClass.getMethod("method_32108").invoke(null);
            Class<?> dilationClass = Class.forName("net.minecraft.class_5605");
            Field noneField = dilationClass.getField("field_27715");
            Object dilation = noneField.get(null);
            Method uv = builderClass.getMethod("method_32101", int.class, int.class);
            Method cuboid = null;
            for (Method m : builderClass.getMethods()) {
                if (m.getName().equals("method_32098") && m.getParameterCount() == 7) { cuboid = m; break; }
            }
            if (cuboid == null) throw new NoSuchMethodException("class_5606.method_32098");

            add(builder,uv,cuboid,dilation,8,8,-3.25f,-2.15f,-4.76f,6.50f,2.55f,0.90f);
            add(builder,uv,cuboid,dilation,8,8, 2.40f,-1.55f,-4.72f,1.08f,3.35f,0.82f);
            add(builder,uv,cuboid,dilation,8,8,-3.48f,-1.55f,-4.72f,1.08f,3.35f,0.82f);
            add(builder,uv,cuboid,dilation,8,8,-3.18f,-0.05f,-4.72f,6.36f,4.55f,0.88f);
            add(builder,uv,cuboid,dilation,8,8,-2.95f, 3.70f,-4.69f,5.90f,4.05f,0.84f);
            add(builder,uv,cuboid,dilation,8,8,-2.62f, 7.00f,-4.66f,5.24f,4.05f,0.80f);
            add(builder,uv,cuboid,dilation,8,8,-2.18f,10.25f,-4.63f,4.36f,3.75f,0.74f);
            add(builder,uv,cuboid,dilation,8,8,-1.74f,13.20f,-4.61f,3.48f,3.45f,0.68f);
            add(builder,uv,cuboid,dilation,8,8,-1.28f,15.95f,-4.59f,2.56f,2.85f,0.63f);
            add(builder,uv,cuboid,dilation,8,8,-0.82f,18.20f,-4.57f,1.64f,2.25f,0.57f);

            Class<?> modelDataClass=Class.forName("net.minecraft.class_5609");
            Object modelData=modelDataClass.getConstructor().newInstance();
            Object root=modelDataClass.getMethod("method_32111").invoke(modelData);
            Class<?> rootClass=Class.forName("net.minecraft.class_5610");
            Class<?> transformClass=Class.forName("net.minecraft.class_5603");
            Object transform=transformClass.getField("field_27701").get(null);
            Method addChild=null;
            for(Method m:rootClass.getMethods()) if(m.getName().equals("method_32117")&&m.getParameterCount()==3){addChild=m;break;}
            if(addChild==null) throw new NoSuchMethodException("class_5610.method_32117");
            addChild.invoke(root,"cosmetic",builder,transform);

            Class<?> textureDataClass=Class.forName("net.minecraft.class_5607");
            Method of=null;
            for(Method m:textureDataClass.getMethods()) if(m.getName().equals("method_32110")&&m.getParameterCount()==3){of=m;break;}
            if(of==null) throw new NoSuchMethodException("class_5607.method_32110");
            Object textured=of.invoke(null,modelData,64,64);
            Object rootPart=textureDataClass.getMethod("method_32109").invoke(textured);
            Method child=rootPart.getClass().getMethod("method_32086",String.class);
            return child.invoke(rootPart,"cosmetic");
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Unable to create long beard model",e);
        }
    }

    private static void add(Object builder, Method uv, Method cuboid, Object dilation,
                            int u,int v,float x,float y,float z,float w,float h,float d)
            throws ReflectiveOperationException {
        Object textured=uv.invoke(builder,u,v);
        cuboid.invoke(textured,x,y,z,w,h,d,dilation);
    }
}
