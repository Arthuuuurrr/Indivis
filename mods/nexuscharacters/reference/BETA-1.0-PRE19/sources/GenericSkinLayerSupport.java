package net.tompsen.nexuscharacters;

import java.io.InputStream;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/** PRE19: preserve base/outer UVs and render separate pixel volumes with joint limits. */
public final class GenericSkinLayerSupport {
    private static final String[] PART_FIELDS={"field_3394","field_3483","field_3482","field_3479","field_3484","field_3486"};
    private static final String[] FLAGS={"enableHat","enableJacket","enableLeftPants","enableRightPants","enableLeftSleeve","enableRightSleeve"};
    private static final int[][] SPECS={{8,8,8,32,0,0},{8,12,4,16,32,1},{4,12,4,0,48,1},{4,12,4,0,32,1},
            {4,12,4,48,48,1},{4,12,4,40,32,1},{3,12,4,48,48,1},{3,12,4,40,32,1}};
    private static final int[][] BASE_UV={{0,0},{16,16},{16,48},{0,16},{32,48},{40,16},{32,48},{40,16}};
    public static long drawnBase, drawnOuter;
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
        // The inherited compositor uses 64x64 UVs. Normalize HD resource-pack
        // cosmetic overrides before it reads them, rather than reading a corner.
        if(((Number)call(image,"method_4307")).intValue()!=64) {
            Object normalized=newImage(readPixels(image));close(image);image=normalized;
        }
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
            // Keep the original compositor and both UV layers byte-for-byte in
            // the texture. A mesh sampling base UVs must never sample hat UVs.
            original(output,source,color,mode,0,0);
            Pending pending=PENDING.computeIfAbsent(output,k->new Pending());
            int owner=hair?2:beard?3:1;
            for(int y=0;y<64;y++)for(int x=0;x<64;x++) {
                int i=y*64+x;
                if(GenericLayerPixels.isBase(x,y) && GenericLayerPixels.alpha(raw[i])>0)
                    pending.baseOwner[i]=(byte)owner;
            }
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
            if(Class.forName("net.minecraft.class_9950").isInstance(model))return;
            try{if(Boolean.TRUE.equals(field(model,"ignored")))return;}catch(NoSuchFieldException ignored){}
            String key=textureKey(field(state,"field_53520"));
            Plan plan=PLANS.get(key);
            if(plan==null)return;
            boolean slim=(Boolean)field(model,"field_3480");
            Object entity=call(state,"getTransitionEntity");
            boolean dummy=entity!=null && Class.forName("net.tompsen.nexuscharacters.NexusDummyEntity").isInstance(entity);
            if(dummy){
                // GUI preview entities live at (0,0,0), not at the camera. They
                // also have no network-populated skin-part visibility flags.
                for(String name:PART_FIELDS)field(model,name).getClass().getField("field_3665").setBoolean(field(model,name),true);
            }
            apply(model,plan,slim,!dummy);
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

    public static void firstPerson(Object renderer,Object texture,Object arm) {
        try {
            Plan plan=PLANS.get(String.valueOf(texture));if(plan==null)return;
            Object model=call(renderer,"method_4038");
            boolean left=arm==field(model,"field_27433"),slim=(Boolean)field(model,"field_3480");
            int part=left?4:5,index=slim?part+2:part;
            if(!enabled(FLAGS[part]))return;
            Object sleeve=field(model,PART_FIELDS[part]);
            method(Class.forName("dev.tr7zw.skinlayers.accessor.ModelPartInjector"),"setInjectedMesh",2)
                .invoke(sleeve,plan.cooked()[index],noOffset());
            ready("hand|"+texture,"first-person",plan);
        }catch(Throwable e){fail("FIRST_PERSON",e);}
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
                return objectMethod(p,m,a,"NexusLayer19IdentityOffset");
            });
        }
        return noOffset;
    }

    private static Object composite(int index,Object base,Object outer)throws Exception {
        if(base==null && outer==null)return null;
        Class<?> iface=Class.forName("dev.tr7zw.skinlayers.api.Mesh");
        boolean[] visible={true};
        return Proxy.newProxyInstance(iface.getClassLoader(),new Class<?>[]{iface},(proxy,m,args)->{
            String name=m.getName();
            if(name.equals("render") && args.length==6) {
                if(!visible[0])return null;
                renderMesh(index,base,args,0);
                renderMesh(index,outer,args,1);
                return null;
            }
            if(m.isDefault())return InvocationHandler.invokeDefault(proxy,m,args);
            if(name.equals("isVisible"))return visible[0];
            if(name.equals("setVisible")){visible[0]=(Boolean)args[0];return null;}
            if(name.equals("reset")){visible[0]=true;return null;}
            if(name.equals("setPosition")||name.equals("setRotation")||name.equals("loadPose")||name.equals("copyFrom"))return null;
            return objectMethod(proxy,m,args,"NexusLayer19Composite["+index+"]");
        });
    }

    private static void renderMesh(int index,Object mesh,Object[] args,int category)throws Exception {
        if(mesh==null)return;
        if(category==0)drawnBase++;else drawnOuter++;
        Object matrices=args[1];
        call(matrices,"method_22903");
        try {
            // Geometry contains the extrusion already. Scaling an entire limb
            // also widens its inner edge into its neighbour and causes flicker.
            float x=index>=6?(index==6?0.5f:-0.5f):index>=4?(index==4?1f:-1f):0f;
            method(Class.forName("dev.tr7zw.skinlayers.api.Mesh"),"setPosition",3).invoke(mesh,x,0f,0f);
            method(Class.forName("dev.tr7zw.skinlayers.api.Mesh"),"render",6).invoke(mesh,args);
        } finally {call(matrices,"method_22909");}
    }

    private static Object[] meshSet(int[] pixels,boolean base)throws Exception {
        Mask key=new Mask(pixels,base);
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
                        int[] s=SPECS[i].clone();
                        if(base){s[3]=BASE_UV[i][0];s[4]=BASE_UV[i][1];}
                        if(surfacePixels(pixels,s)==0)continue;
                        parts[i]=create.invoke(helper,image,s[0],s[1],s[2],s[3],s[4],s[5]!=0,i==0?0.6f:(i>=4?-2f:0f));
                        extrude(parts[i],i,base?0.30f:0.70f);
                    }
                }finally{try{if(fast!=null)fast.setBoolean(config,previous);}finally{close(image);}}
            }
            MESHES.put(key,parts);return parts;
        }
    }

    /** Extrude pixel faces, keeping the body and joint seams in their own bounds. */
    private static void extrude(Object mesh,int part,float depth)throws Exception {
        float[] data=(float[])field(mesh,"polygonData");
        for(int q=0;q<data.length;q+=23)for(int v=0;v<4;v++)for(int axis=0;axis<3;axis++) {
            int at=q+3+v*5+axis;
            data[at]=extrudeCoordinate(data[at]*16f,part,axis,depth)/16f;
        }
        // Iris compatibility uses Minecraft cubes instead of polygonData.
        // Keep that representation and its UVs; extrude its immutable vertices.
        Constructor<?> vertex=Class.forName("net.minecraft.class_630$class_618")
            .getConstructor(float.class,float.class,float.class,float.class,float.class);
        for(Object cube:(List<?>)field(mesh,"cubes"))for(Object polygon:(Object[])field(cube,"field_3649")) {
            Object[] vertices=(Object[])call(polygon,"comp_3184");
            for(int i=0;i<vertices.length;i++) {
                Object old=vertices[i];
                vertices[i]=vertex.newInstance(
                    extrudeCoordinate(((Number)call(old,"comp_4804")).floatValue(),part,0,depth),
                    extrudeCoordinate(((Number)call(old,"comp_4805")).floatValue(),part,1,depth),
                    extrudeCoordinate(((Number)call(old,"comp_4806")).floatValue(),part,2,depth),
                    ((Number)call(old,"comp_3187")).floatValue(),((Number)call(old,"comp_3188")).floatValue());
            }
        }
    }

    private static float extrudeCoordinate(float p,int part,int axis,float depth) {
        int[] s=SPECS[part];
        float[] lo={-s[0]/2f,part==0?-8f:part>=4?-2f:0f,-s[2]/2f};
        float[] hi={s[0]/2f,lo[1]+s[1],s[2]/2f};
            if(part==0 && axis==1)p-=0.6f;
            boolean low=true,high=true;
            if(axis==0){
                if(part==2 || part==4 || part==6)low=false;
                else if(part==3 || part==5 || part==7)high=false;
            }
            if(axis==1){if(part==1)high=false;else if(part==2||part==3)low=false;}
            if(low && p<lo[axis]+1f)p-=depth*Math.max(0f,lo[axis]+1f-p);
            if(high && p>hi[axis]-1f)p+=depth*Math.max(0f,p-(hi[axis]-1f));
            // A capped face must also avoid the flat base face. The two
            // sleeves begin at the jacket's raised side, not inside it.
            if(axis==0 && !low)p=Math.max(p,lo[axis]+(part>=4?depth:0.015f));
            if(axis==0 && !high)p=Math.min(p,hi[axis]-(part>=4?depth:0.015f));
            if(axis==1 && !low)p=Math.max(p,lo[axis]+0.015f);
            if(axis==1 && !high)p=Math.min(p,hi[axis]-0.015f);
            return p;
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
        if(w<64||h!=w||w%64!=0)throw new IllegalArgumentException("Unsupported skin size "+w+"x"+h);
        int[] result=new int[4096];
        int scale=w/64;
        for(int y=0;y<64;y++)for(int x=0;x<64;x++)
            result[y*64+x]=((Number)pixelGet.invoke(image,x*scale,y*scale)).intValue();
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
    private static void ready(String key,String path,Plan p){if(LOGGED.putIfAbsent(key,true)==null)System.out.println("[NexusCharacters][GenericLayers19] READY path="+path+" base="+GenericLayerPixels.count(p.base)+" outer="+GenericLayerPixels.count(p.outer)+" hair="+p.hairCount+" classicBeard="+p.beardCount);}
    private static void fail(String stage,Throwable e){String key=stage+"|"+e;if(LOGGED.putIfAbsent(key,true)==null){System.err.println("[NexusCharacters][GenericLayers19] FAIL stage="+stage+" "+e);e.printStackTrace();}}

    private static final class Pending {final byte[] baseOwner=new byte[4096];}
    private static final class Plan {
        final int[] base,outer;final int hairCount,beardCount;private Object[] cooked;private int mode;
        Plan(int[] pixels,Pending pending) {
            base=new int[4096];outer=new int[4096];int hc=0,bc=0;
            for(int y=0;y<64;y++)for(int x=0;x<64;x++) {
                int i=y*64+x;
                if(GenericLayerPixels.isOuter(x,y))outer[i]=pixels[i];
                else if(pending!=null && pending.baseOwner[i]!=0){
                    base[i]=pixels[i];if(pending.baseOwner[i]==2)hc++;if(pending.baseOwner[i]==3)bc++;
                }
            }
            hairCount=hc;beardCount=bc;
        }
        synchronized Object[] cooked()throws Exception {
            int currentMode=geometryMode();
            if(cooked!=null && mode==currentMode)return cooked;
            Object[] b=meshSet(base,true),o=meshSet(outer,false),out=new Object[8];
            for(int i=0;i<8;i++)out[i]=composite(i,b[i],o[i]);
            cooked=out;mode=currentMode;return out;
        }
    }
    private static final class Mask {
        final byte[] alpha=new byte[4096];final int hash;final int mode=geometryMode();final boolean base;
        Mask(int[] pixels,boolean base){this.base=base;for(int i=0;i<4096;i++)alpha[i]=(byte)(pixels[i]>>>24);hash=31*Arrays.hashCode(alpha)+mode+(base?127:0);}
        public int hashCode(){return hash;}
        public boolean equals(Object other){return other instanceof Mask m && base==m.base && mode==m.mode && Arrays.equals(alpha,m.alpha);}
    }
}
