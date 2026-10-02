package net.tompsen.nexuscharacters;

import java.io.InputStream;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/** PRE18: one textured model, with data-driven Skin Layers meshes for all cosmetics. */
public final class GenericSkinLayerSupport {
    private static final String[] PART_FIELDS={"field_3394","field_3483","field_3482","field_3479","field_3484","field_3486"};
    private static final String[] OFFSETS={"HEAD","BODY","LEFT_LEG","RIGHT_LEG","LEFT_ARM","RIGHT_ARM","LEFT_ARM_SLIM","RIGHT_ARM_SLIM"};
    private static final String[] FLAGS={"enableHat","enableJacket","enableLeftPants","enableRightPants","enableLeftSleeve","enableRightSleeve"};
    private static final int[][] SPECS={{8,8,8,32,0,0},{8,12,4,16,32,1},{4,12,4,0,48,1},{4,12,4,0,32,1},
            {4,12,4,48,48,1},{4,12,4,40,32,1},{3,12,4,48,48,1},{3,12,4,40,32,1}};
    private static final Map<Object,String> ASSETS=Collections.synchronizedMap(new WeakHashMap<>());
    private static final Map<Object,Pending> PENDING=Collections.synchronizedMap(new WeakHashMap<>());
    private static final Map<String,Plan> PLANS=new ConcurrentHashMap<>();
    private static final Map<Mask,Object[]> MESHES=Collections.synchronizedMap(new LinkedHashMap<>(32,0.75f,true){
        protected boolean removeEldestEntry(Map.Entry<Mask,Object[]> e){return size()>128;}
    });
    private static final Map<String,Boolean> LOGGED=new ConcurrentHashMap<>();
    private static Method originalOverlay, pixelGet, pixelSet;
    private static Constructor<?> imageCtor;
    private static Object noOffset;

    private GenericSkinLayerSupport() {}

    /** Called by the patched loader; the ResourceManager honors resource pack overrides. */
    public static Object load(String path) throws Exception {
        InputStream in=null;
        try {
            int cut=path.indexOf("/assets/");
            if(cut>=0) {
                String rest=path.substring(cut+8); int slash=rest.indexOf('/');
                Object id=identifier(rest.substring(0,slash),rest.substring(slash+1));
                Object client=client();
                Object rm=client==null ? null : call(client,"method_1478");
                Object opt=rm==null ? Optional.empty() : call(rm,"method_14486",id);
                if(opt instanceof Optional<?> optional && optional.isPresent()) in=(InputStream)call(optional.get(),"method_14482");
            }
        } catch(ReflectiveOperationException ignored) {}
        if(in==null) in=GenericSkinLayerSupport.class.getResourceAsStream(path);
        if(in==null) throw new IllegalStateException("Missing appearance asset "+path);
        Object image;
        try(InputStream stream=in){image=Class.forName("net.minecraft.class_1011").getMethod("method_4309",InputStream.class).invoke(null,stream);}
        ASSETS.put(image,path);
        return image;
    }

    /** Replaces only the compositor's overlay dispatch; unrelated tint modes use its original code. */
    public static void overlay(Object output,Object source,int color,Object mode,int dx,int dy) {
        try {
            String path=ASSETS.get(source);
            boolean hair=path!=null && path.contains("/hair/");
            boolean beard=path!=null && path.contains("/facial_hair/");
            boolean outfit=path!=null && path.contains("/outfits/");
            if(!hair && !beard && !outfit) {original(output,source,color,mode,dx,dy);return;}
            if(dx!=0 || dy!=0) throw new IllegalArgumentException("Cosmetic UV offsets must be zero");
            int[] raw=readPixels(source);
            // Outfits retain their ordinary base paint and gain depth on torso,
            // sleeves and trousers. Hair/beards are separated so skin remains
            // visible underneath their promoted voxels.
            int[] outer=GenericLayerPixels.exterior(raw,!outfit);
            int[] skin;
            if(outfit) {
                skin=raw.clone();
                for(int i=0;i<outer.length;i++)if(GenericLayerPixels.alpha(outer[i])>0)skin[i]=outer[i];
            } else skin=GenericLayerPixels.cosmeticForSkin(raw,outer,hair);
            Object adjusted=newImage(skin);
            try {original(output,adjusted,color,mode,0,0);} finally {close(adjusted);}
            if(outfit)return;
            Pending pending=PENDING.computeIfAbsent(output,k->new Pending());
            int[] coverage=hair ? pending.hair : pending.beard;
            for(int i=0;i<outer.length;i++) if(GenericLayerPixels.alpha(outer[i])>0) coverage[i]=outer[i];
        } catch(Throwable e) {
            // Original source is still alive and provides a complete flat fallback.
            fail("COMPOSE",e);
            try {original(output,source,color,mode,dx,dy);} catch(Exception nested){throw new IllegalStateException(nested);}
        }
    }

    private static void original(Object out,Object src,int color,Object mode,int dx,int dy)throws Exception {
        if(originalOverlay==null) {
            originalOverlay=method(Class.forName("net.tompsen.nexuscharacters.DynamicAppearanceSupport"),"nexus$overlayOriginal",6);
        }
        originalOverlay.invoke(null,out,src,color,mode,dx,dy);
    }

    /** Called once after composition, before the image is handed to TextureManager. */
    public static Object complete(Object image,String marker) {
        try {
            Pending pending=PENDING.remove(image);
            int[] pixels=readPixels(image);
            PLANS.put("nexuscharacters:dynamic/appearance/"+marker,new Plan(pixels,pending));
        }catch(Throwable e){fail("SNAPSHOT",e);}
        return image;
    }

    public static void preview(Object widget,Object dto) {
        if(widget==null||dto==null)return;
        try {
            Object skin=callStatic("net.tompsen.nexuscharacters.PreviewDummyPlayerManager","skin",dto);
            String key=textureKey(skin);
            clearModel(field(widget,"field_59834"));
            clearModel(field(widget,"field_59835"));
            Plan plan=plan(key);
            if(plan==null)return;
            apply(field(widget,"field_59834"),plan,false,false);
            apply(field(widget,"field_59835"),plan,true,false);
            ready("preview|"+key,"preview",plan);
        }catch(Throwable e){fail("PREVIEW",e);}
    }

    /** PlayerModel setupAnim tail, after Skin Layers has applied its own LOD/config checks. */
    public static void player(Object model,Object state) {
        if(model==null||state==null)return;
        try {
            String key=textureKey(field(state,"field_53520"));
            Plan plan=PLANS.get(key);
            if(plan==null)return;
            boolean slim=(Boolean)field(model,"field_3480");
            apply(model,plan,slim,true);
            ready("world|"+key,"world",plan);
        }catch(Throwable e){fail("PLAYER",e);}
    }

    private static Plan plan(String key)throws Exception {
        if(key==null)return null;
        Plan plan=PLANS.get(key);
        if(plan!=null)return plan;
        // Account/preset skins also get ordinary outer layer depth, without moving base-face paint.
        Object id=parseIdentifier(key);
        Object image=callStatic("dev.tr7zw.skinlayers.SkinUtil","getTexture",id,null);
        if(image==null)return null;
        // SkinUtil may return a borrowed TextureManager image; never close it here.
        plan=new Plan(readPixels(image),null);
        PLANS.put(key,plan);
        return plan;
    }

    private static void apply(Object model,Plan plan,boolean slim,boolean world)throws Exception {
        Object[] cooked=plan.cooked();
        Class<?> injector=Class.forName("dev.tr7zw.skinlayers.accessor.ModelPartInjector");
        Method set=method(injector,"setInjectedMesh",2);
        Method get=method(injector,"getInjectedMesh",0);
        for(int p=0;p<6;p++) {
            Object part=field(model,PART_FIELDS[p]);
            if(!injector.isInstance(part))throw new IllegalStateException("Skin Layers ModelPart mixin is absent");
            // Respect world LOD, helmet suppression, and disabled parts from native Skin Layers.
            if(world && get.invoke(part)==null)continue;
            if(!enabled(FLAGS[p])) {if(!world)set.invoke(part,null,null);continue;}
            int index=p;
            if(slim && p>=4)index=p+2;
            // Real nonempty vanilla parts are used; no empty wrapper/extra widget is involved.
            set.invoke(part,cooked[index],noOffset());
        }
    }

    private static void clearModel(Object model)throws Exception {
        Method set=method(Class.forName("dev.tr7zw.skinlayers.accessor.ModelPartInjector"),"setInjectedMesh",2);
        for(String name:PART_FIELDS)set.invoke(field(model,name),null,null);
    }

    private static boolean enabled(String flag) {
        try{return (Boolean)field(field(Class.forName("dev.tr7zw.skinlayers.SkinLayersModBase"),"config"),flag);}
        catch(Exception ignored){return true;}
    }

    private static int geometryMode() {
        try {
            Object config=field(Class.forName("dev.tr7zw.skinlayers.SkinLayersModBase"),"config");
            return ((Boolean)field(config,"irisCompatibilityMode")?1:0) | ((Boolean)field(config,"applySodiumWorkaround")?2:0);
        }catch(Exception ignored){return 0;}
    }

    private static Object noOffset()throws Exception {
        if(noOffset==null) {
            Class<?> iface=Class.forName("dev.tr7zw.skinlayers.api.OffsetProvider");
            noOffset=Proxy.newProxyInstance(iface.getClassLoader(),new Class<?>[]{iface},(p,m,a)->{
                if(m.getName().equals("applyOffset"))return null;
                return objectMethod(p,m,a,"NexusLayer18IdentityOffset");
            });
        }
        return noOffset;
    }

    private static Object composite(int index,Object normal,Object hair,Object beard)throws Exception {
        if(normal==null && hair==null && beard==null)return null;
        Class<?> iface=Class.forName("dev.tr7zw.skinlayers.api.Mesh");
        boolean[] visible={true};
        return Proxy.newProxyInstance(iface.getClassLoader(),new Class<?>[]{iface},(proxy,m,args)->{
            String name=m.getName();
            if(name.equals("render") && args.length==6) {
                if(!visible[0])return null;
                renderMesh(index,normal,args,0);
                renderMesh(index,hair,args,1);
                renderMesh(index,beard,args,2);
                return null;
            }
            if(m.isDefault())return InvocationHandler.invokeDefault(proxy,m,args);
            if(name.equals("isVisible"))return visible[0];
            if(name.equals("setVisible")){visible[0]=(Boolean)args[0];return null;}
            if(name.equals("reset")){visible[0]=true;return null;}
            if(name.equals("setPosition")||name.equals("setRotation")||name.equals("loadPose")||name.equals("copyFrom"))return null;
            return objectMethod(proxy,m,args,"NexusLayer18Composite["+index+"]");
        });
    }

    private static void renderMesh(int index,Object mesh,Object[] args,int category)throws Exception {
        if(mesh==null)return;
        Object matrices=args[1];
        call(matrices,"method_22903");
        try {
            Class<?> offsetType=Class.forName("dev.tr7zw.skinlayers.api.OffsetProvider");
            Object offset=offsetType.getField(OFFSETS[index]).get(null);
            method(offsetType,"applyOffset",2).invoke(offset,matrices,mesh);
            if(category==1) {
                if(index==1)call(matrices,"method_22905",1.16f,1.02f,1.45f);
                else if(index==0)call(matrices,"method_22905",1.035f,1.035f,1.035f);
                else call(matrices,"method_22905",1.025f,1.025f,1.025f);
            } else if(category==2)call(matrices,"method_22905",1.008f,1.008f,1.008f);
            method(Class.forName("dev.tr7zw.skinlayers.api.Mesh"),"render",6).invoke(mesh,args);
        } finally {call(matrices,"method_22909");}
    }

    private static Object[] meshSet(int[] pixels)throws Exception {
        Mask key=new Mask(pixels);
        synchronized(MESHES) {
            Object[] cached=MESHES.get(key);if(cached!=null)return cached;
            Object[] parts=new Object[8];
            if(GenericLayerPixels.count(pixels)>0) {
                Object image=newImage(pixels);
                Object config=null; Field fast=null; boolean previous=false;
                try {
                    // A full-box fastRender fallback samples every final texture pixel,
                    // including the other cosmetic layers. Build complete, masked voxel
                    // surfaces instead; restore the user's setting immediately afterwards.
                    config=field(Class.forName("dev.tr7zw.skinlayers.SkinLayersModBase"),"config");
                    fast=config.getClass().getField("fastRender");previous=fast.getBoolean(config);
                    fast.setBoolean(config,false);
                    Object helper=callStatic("dev.tr7zw.skinlayers.api.SkinLayersAPI","getMeshHelper");
                    Method create=method(Class.forName("dev.tr7zw.skinlayers.api.MeshHelper"),"create3DMesh",8);
                    for(int i=0;i<8;i++) {
                        int[] s=SPECS[i];
                        if(surfacePixels(pixels,s)==0)continue;
                        parts[i]=create.invoke(helper,image,s[0],s[1],s[2],s[3],s[4],s[5]!=0,i==0?0.6f:(i>=4?-2f:0f));
                    }
                }finally{try{if(fast!=null)fast.setBoolean(config,previous);}finally{close(image);}}
            }
            MESHES.put(key,parts);return parts;
        }
    }

    private static int surfacePixels(int[] pixels,int[] s) {
        int w=s[0],h=s[1],d=s[2],u=s[3],v=s[4],count=0;
        for(int y=v;y<v+d+h;y++)for(int x=u;x<u+2*d+2*w;x++) {
            if(y<v+d && (x<u+d || x>=u+d+2*w))continue;
            if(GenericLayerPixels.alpha(pixels[y*64+x])>0)count++;
        }
        return count;
    }

    public static void clearCaches() {
        PLANS.clear();MESHES.clear();ASSETS.clear();PENDING.clear();LOGGED.clear();
        DynamicAssetCatalog.clear();
        try {
            Class<?> c=Class.forName("net.tompsen.nexuscharacters.DynamicAppearanceSupport");
            for(String f:new String[]{"SKINS","IDS"})((Map<?,?>)field(c,f)).clear();
        }catch(Exception e){fail("RELOAD",e);}
    }

    private static String textureKey(Object skin)throws Exception {
        if(skin==null)return null;
        Object ref=call(skin,"comp_1626");
        return String.valueOf(ref.getClass().getName().equals("net.minecraft.class_2960")?ref:call(ref,"comp_3627"));
    }

    private static int[] readPixels(Object image)throws Exception {
        initPixels();
        int w=((Number)call(image,"method_4307")).intValue(),h=((Number)call(image,"method_4323")).intValue();
        if(w!=64||h!=64)throw new IllegalArgumentException("Skin Layers requires 64x64, received "+w+"x"+h);
        int[] result=new int[4096];
        for(int y=0;y<64;y++)for(int x=0;x<64;x++)result[y*64+x]=((Number)pixelGet.invoke(image,x,y)).intValue();
        return result;
    }

    private static Object newImage(int[] pixels)throws Exception {
        initPixels();Object image=imageCtor.newInstance(64,64,false);
        for(int y=0;y<64;y++)for(int x=0;x<64;x++)pixelSet.invoke(image,x,y,pixels[y*64+x]);
        return image;
    }

    private static synchronized void initPixels()throws Exception {
        if(imageCtor!=null)return;
        Class<?> c=Class.forName("net.minecraft.class_1011");
        pixelGet=c.getMethod("method_61940",int.class,int.class);
        pixelSet=c.getMethod("method_61941",int.class,int.class,int.class);
        imageCtor=c.getConstructor(int.class,int.class,boolean.class);
    }

    private static Object identifier(String ns,String path)throws Exception {
        return Class.forName("net.minecraft.class_2960").getMethod("method_60655",String.class,String.class).invoke(null,ns,path);
    }
    private static Object parseIdentifier(String value)throws Exception {int i=value.indexOf(':');return identifier(value.substring(0,i),value.substring(i+1));}
    private static Object client()throws Exception {return callStatic("net.minecraft.class_310","method_1551");}
    private static void close(Object object){try{call(object,"close");}catch(Exception ignored){}}
    private static Object callStatic(String type,String name,Object...args)throws Exception{return method(Class.forName(type),name,args.length).invoke(null,args);}
    private static Object call(Object object,String name,Object...args)throws Exception{return method(object.getClass(),name,args.length).invoke(object,args);}
    private static Method method(Class<?> type,String name,int argc)throws NoSuchMethodException {
        for(Class<?> c=type;c!=null;c=c.getSuperclass())for(Method m:c.getDeclaredMethods())if(m.getName().equals(name)&&m.getParameterCount()==argc){m.setAccessible(true);return m;}
        for(Method m:type.getMethods())if(m.getName().equals(name)&&m.getParameterCount()==argc){m.setAccessible(true);return m;}
        throw new NoSuchMethodException(type.getName()+"."+name+"/"+argc);
    }
    private static Object field(Object object,String name)throws Exception {
        Class<?> type=object instanceof Class<?> c?c:object.getClass();
        for(Class<?> c=type;c!=null;c=c.getSuperclass())try{Field f=c.getDeclaredField(name);f.setAccessible(true);return f.get(object instanceof Class<?>?null:object);}catch(NoSuchFieldException ignored){}
        throw new NoSuchFieldException(type.getName()+"."+name);
    }
    private static Object objectMethod(Object p,Method m,Object[] a,String label) {
        return switch(m.getName()){case "toString"->label;case "hashCode"->System.identityHashCode(p);case "equals"->p==(a==null?null:a[0]);default->null;};
    }
    private static void ready(String key,String path,Plan p){if(LOGGED.putIfAbsent(key,true)==null)System.out.println("[NexusCharacters][GenericLayers18] READY path="+path+" hair="+GenericLayerPixels.count(p.hair)+" classicBeard="+GenericLayerPixels.count(p.beard)+" single-model=true");}
    private static void fail(String stage,Throwable e){String key=stage+"|"+e;if(LOGGED.putIfAbsent(key,true)==null){System.err.println("[NexusCharacters][GenericLayers18] FAIL stage="+stage+" "+e);e.printStackTrace();}}

    private static final class Pending {final int[] hair=new int[4096],beard=new int[4096];}
    private static final class Plan {
        final int[] normal,hair,beard;private Object[] cooked;private int mode;
        Plan(int[] pixels,Pending pending) {
            normal=new int[4096];hair=new int[4096];beard=new int[4096];
            for(int y=0;y<64;y++)for(int x=0;x<64;x++) {
                int i=y*64+x;if(!GenericLayerPixels.isOuter(x,y))continue;
                if(pending!=null && GenericLayerPixels.alpha(pending.beard[i])>0)beard[i]=pixels[i];
                else if(pending!=null && GenericLayerPixels.alpha(pending.hair[i])>0)hair[i]=pixels[i];
                else normal[i]=pixels[i];
            }
        }
        synchronized Object[] cooked()throws Exception {
            int currentMode=geometryMode();
            if(cooked!=null && mode==currentMode)return cooked;
            Object[] n=meshSet(normal),h=meshSet(hair),b=meshSet(beard),out=new Object[8];
            for(int i=0;i<8;i++)out[i]=composite(i,n[i],h[i],b[i]);
            cooked=out;mode=currentMode;return out;
        }
    }
    private static final class Mask {
        final byte[] alpha=new byte[4096];final int hash;final int mode=geometryMode();
        Mask(int[] pixels){for(int i=0;i<4096;i++)alpha[i]=(byte)(pixels[i]>>>24);hash=31*Arrays.hashCode(alpha)+mode;}
        public int hashCode(){return hash;}
        public boolean equals(Object other){return other instanceof Mask m && mode==m.mode && Arrays.equals(alpha,m.alpha);}
    }
}
