package net.tompsen.nexuscharacters;

import java.io.InputStream;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/** Native outer layers built from the skin submitted by Nexus, independently of account skin meshes. */
public final class GenericSkinLayerSupport {
    private static final String[] PART_FIELDS={"field_3394","field_3483","field_3482","field_3479","field_3484","field_3486"};
    private static final String[] FLAGS={"enableHat","enableJacket","enableLeftPants","enableRightPants","enableLeftSleeve","enableRightSleeve"};
    private static final String[] OFFSETS={"HEAD","BODY","LEFT_LEG","RIGHT_LEG","LEFT_ARM","RIGHT_ARM","LEFT_ARM_SLIM","RIGHT_ARM_SLIM"};
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
            // Preserve base/outer details. A cosmetic with no authored outer
            // pixels on a part gets an automatic outer footprint on that part.
            int[] adjusted=GenericLayerPixels.prepareCosmetic(raw,hair,beard);
            Object image=newImage(adjusted);
            try { original(output,image,color,mode,0,0); } finally { close(image); }
            Pending pending=PENDING.computeIfAbsent(output,k->new Pending());
            int owner=hair?2:beard?3:1;
            for(int y=0;y<64;y++)for(int x=0;x<64;x++) {
                int i=y*64+x;
                if(GenericLayerPixels.alpha(adjusted[i])>0)
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
            if(plan==null){LowerJointSupport.apply(model,false);return;}
            boolean slim=(Boolean)field(model,"field_3480");
            Object entity=call(state,"getTransitionEntity");
            boolean dummy=entity!=null && Class.forName("net.tompsen.nexuscharacters.NexusDummyEntity").isInstance(entity);
            if(dummy){
                // GUI preview entities live at (0,0,0), not at the camera. They
                // also have no network-populated skin-part visibility flags.
                for(String name:PART_FIELDS)field(model,name).getClass().getField("field_3665").setBoolean(field(model,name),true);
            }
            if(!dummy && !inWorldRange(entity)){LowerJointSupport.apply(model,false);return;}
            apply(model,plan,slim,!dummy && hiddenHead(entity));
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
                .invoke(sleeve,plan.cooked()[index],nativeOffset(index,true));
            ready("hand|"+texture,"first-person",plan);
        }catch(Throwable e){fail("FIRST_PERSON",e);}
    }

    private static boolean inWorldRange(Object entity)throws Exception {
        Object c=client();
        if(entity==null || field(c,"field_1724")==null)return false;
        Object camera=call(field(c,"field_1773"),"method_19418");
        Object position=call(camera,"method_71156");
        double distance=((Number)call(entity,"method_5707",position)).doubleValue();
        int lod=((Number)field(field(Class.forName("dev.tr7zw.skinlayers.SkinLayersModBase"),"config"),"renderDistanceLOD")).intValue();
        return distance<=(double)lod*lod;
    }

    private static boolean hiddenHead(Object entity)throws Exception {
        Object helmet=call(entity,"method_6118",field(Class.forName("net.minecraft.class_1304"),"field_6169"));
        return helmet!=null && ((Set<?>)field(Class.forName("dev.tr7zw.skinlayers.SkinLayersModBase"),"hideHeadLayers")).contains(call(helmet,"method_7909"));
    }

    private static void apply(Object model,Plan plan,boolean slim,boolean skipHead)throws Exception {
        LowerJointSupport.apply(model,true);
        Object[] cooked=plan.cooked();
        Class<?> injector=Class.forName("dev.tr7zw.skinlayers.accessor.ModelPartInjector");
        Method set=method(injector,"setInjectedMesh",2);
        for(int p=0;p<6;p++) {
            Object part=field(model,PART_FIELDS[p]);
            if(!injector.isInstance(part))throw new IllegalStateException("Skin Layers ModelPart mixin is absent");
            // A null native mesh is not a LOD/config decision: it can mean that
            // the account skin is blank, unavailable, or cached before Nexus
            // substituted the submitted skin. Check policy explicitly instead.
            if(!enabled(FLAGS[p]) || (p==0 && skipHead)) {set.invoke(part,null,null);continue;}
            int index=p;
            if(slim && p>=4)index=p+2;
            // Real nonempty vanilla parts are used; no empty wrapper/extra widget is involved.
            set.invoke(part,cooked[index],nativeOffset(index,false));
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
            return Objects.hash(field(config,"irisCompatibilityMode"),field(config,"applySodiumWorkaround"),
                field(config,"baseVoxelSize"),field(config,"bodyVoxelWidthSize"),field(config,"headVoxelSize"));
        }catch(Exception ignored){return 0;}
    }

    private static Object nativeOffset(int index,boolean firstPerson)throws Exception {
        String name=OFFSETS[index];
        if(firstPerson)name="FIRSTPERSON_"+name;
        return Class.forName("dev.tr7zw.skinlayers.api.OffsetProvider").getField(name).get(null);
    }

    private static Object composite(int index,Object[] layers)throws Exception {
        Object[] meshes=Arrays.stream(layers).filter(Objects::nonNull).toArray();
        if(meshes.length==0)return null;
        Class<?> iface=Class.forName("dev.tr7zw.skinlayers.api.Mesh");
        return Proxy.newProxyInstance(iface.getClassLoader(),new Class<?>[]{iface},(proxy,m,args)->{
            if(m.getDeclaringClass()==Object.class)return objectMethod(proxy,m,args,"NexusNative22["+index+"]");
            if(m.getName().equals("render") && args.length==6)drawnOuter++;
            // Every category shares the submitted part pose and native offset.
            // Only the alpha masks and a small geometric separation differ.
            Object result=null;
            for(Object mesh:meshes)try{Object value=m.invoke(mesh,args);if(value!=null)result=value;}catch(InvocationTargetException e){throw e.getCause();}
            return result;
        });
    }

    private static Object[] meshSet(int[] pixels,boolean base)throws Exception {
        return meshSet(pixels,base,0);
    }
    private static Object[] meshSet(int[] pixels,boolean base,int category)throws Exception {
        Mask key=new Mask(pixels,base,category);
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
                        if(category>0)separateCategory(parts[i],i,category*.06f);
                        limitJoints(parts[i],i,category);
                    }
                }finally{try{if(fast!=null)fast.setBoolean(config,previous);}finally{close(image);}}
            }
            MESHES.put(key,parts);return parts;
        }
    }

    private static float categoryCoordinate(float p,int part,int axis,float depth){
        int[] spec=SPECS[part];float size=spec[axis];
        float center=axis==1?(part==0?-3.4f:part>=4?4f:6f):0f;
        return center+(p-center)*(1f+2f*depth/size)+depth/50f;
    }
    private static void separateCategory(Object mesh,int part,float depth)throws Exception{
        float[] data=(float[])field(mesh,"polygonData");
        for(int q=0;q<data.length;q+=23)for(int v=0;v<4;v++)for(int a=0;a<3;a++){
            int at=q+3+v*5+a;data[at]=categoryCoordinate(data[at]*16f,part,a,depth)/16f;
        }
        Constructor<?> vertex=Class.forName("net.minecraft.class_630$class_618").getConstructor(float.class,float.class,float.class,float.class,float.class);
        for(Object cube:(List<?>)field(mesh,"cubes"))for(Object polygon:(Object[])field(cube,"field_3649")){
            Object[] vertices=(Object[])call(polygon,"comp_3184");
            for(int v=0;v<vertices.length;v++){
                Object old=vertices[v];vertices[v]=vertex.newInstance(
                    categoryCoordinate(((Number)call(old,"comp_4804")).floatValue(),part,0,depth),
                    categoryCoordinate(((Number)call(old,"comp_4805")).floatValue(),part,1,depth),
                    categoryCoordinate(((Number)call(old,"comp_4806")).floatValue(),part,2,depth),
                    ((Number)call(old,"comp_3187")).floatValue(),((Number)call(old,"comp_3188")).floatValue());
            }
        }
    }

    /** Clip only joint-facing boundaries, in the native provider's coordinates.
     * The other pixel faces and corner triangles remain exactly native. */
    private static void limitJoints(Object mesh,int part,int category)throws Exception {
        float[] data=(float[])field(mesh,"polygonData");
        for(int q=0;q<data.length;q+=23)for(int v=0;v<4;v++)for(int axis=0;axis<3;axis++) {
            int at=q+3+v*5+axis;
            data[at]=jointCoordinate(data[at]*16f,part,axis,category)/16f;
        }
        Constructor<?> vertex=Class.forName("net.minecraft.class_630$class_618")
            .getConstructor(float.class,float.class,float.class,float.class,float.class);
        for(Object cube:(List<?>)field(mesh,"cubes"))for(Object polygon:(Object[])field(cube,"field_3649")) {
            Object[] vertices=(Object[])call(polygon,"comp_3184");
            for(int i=0;i<vertices.length;i++) {
                Object old=vertices[i];
                vertices[i]=vertex.newInstance(
                    jointCoordinate(((Number)call(old,"comp_4804")).floatValue(),part,0,category),
                    jointCoordinate(((Number)call(old,"comp_4805")).floatValue(),part,1,category),
                    jointCoordinate(((Number)call(old,"comp_4806")).floatValue(),part,2,category),
                    ((Number)call(old,"comp_3187")).floatValue(),((Number)call(old,"comp_3188")).floatValue());
            }
        }
        removeDuplicateFaces(mesh);
        CoplanarSurfaceSupport.clean(mesh);
    }

    /** Neighboring edge voxels can contain coincident internal walls. Remove
     * those pairs, plus faces collapsed by clipping, instead of depth-biasing
     * two copies of the same surface. Applies to both native representations. */
    private static void removeDuplicateFaces(Object mesh)throws Exception {
        float[] data=(float[])field(mesh,"polygonData");
        Map<String,Integer> seen=new HashMap<>();boolean[] drop=new boolean[data.length/23];
        for(int q=0;q<data.length;q+=23) {
            float[][] points=new float[4][3];for(int v=0;v<4;v++)for(int a=0;a<3;a++)points[v][a]=data[q+3+v*5+a];
            String key=faceKey(points);int index=q/23;
            if(key==null){drop[index]=true;continue;}
            Integer previous=seen.putIfAbsent(key,index);
            if(previous!=null) {
                drop[index]=true;
                float dot=0;for(int a=0;a<3;a++)dot+=data[previous*23+a]*data[q+a];
                if(dot<0)drop[previous]=true;
            }
        }
        int kept=0;for(boolean d:drop)if(!d)kept++;
        float[] clean=new float[kept*23];int at=0;
        for(int i=0;i<drop.length;i++)if(!drop[i]){System.arraycopy(data,i*23,clean,at,23);at+=23;}
        setField(mesh,"polygonData",clean);
        List<?> cubes=(List<?>)field(mesh,"cubes");
        Map<String,Object[]> cubeSeen=new HashMap<>();Set<Object> removed=Collections.newSetFromMap(new IdentityHashMap<>());
        for(Object cube:cubes)for(Object polygon:(Object[])field(cube,"field_3649")) {
            Object[] vs=(Object[])call(polygon,"comp_3184");float[][] points=new float[4][3];
            for(int v=0;v<4;v++)for(int a=0;a<3;a++)points[v][a]=((Number)call(vs[v],new String[]{"comp_4804","comp_4805","comp_4806"}[a])).floatValue()/16f;
            String key=faceKey(points);if(key==null){removed.add(polygon);continue;}
            Object normal=call(polygon,"comp_3185");Object[] previous=cubeSeen.putIfAbsent(key,new Object[]{polygon,normal});
            if(previous!=null){removed.add(polygon);float dot=0;for(String axis:new String[]{"x","y","z"})dot+=((Number)call(normal,axis)).floatValue()*((Number)call(previous[1],axis)).floatValue();if(dot<0)removed.add(previous[0]);}
        }
        for(Object cube:cubes){
            Object[] polygons=(Object[])field(cube,"field_3649");List<Object> keep=new ArrayList<>();for(Object p:polygons)if(!removed.contains(p))keep.add(p);
            Object[] replacement=(Object[])Array.newInstance(polygons.getClass().getComponentType(),keep.size());keep.toArray(replacement);
            setField(cube,"field_3649",replacement);
            LowerJointSupport.syncSodium(cube);
        }
    }

    private static String faceKey(float[][] p) {
        double area=0;
        for(int v=1;v<3;v++) {
            double[] a=new double[3],b=new double[3];for(int i=0;i<3;i++){a[i]=p[v][i]-p[0][i];b[i]=p[v+1][i]-p[0][i];}
            for(int i=0;i<3;i++){double cross=a[(i+1)%3]*b[(i+2)%3]-a[(i+2)%3]*b[(i+1)%3];area+=cross*cross;}
        }
        if(area<1e-16)return null;
        String[] keys=new String[4];for(int v=0;v<4;v++)keys[v]=Math.round(p[v][0]*1000000)+","+Math.round(p[v][1]*1000000)+","+Math.round(p[v][2]*1000000);
        Arrays.sort(keys);return Arrays.toString(keys);
    }

    private static void setField(Object value,String name,Object replacement)throws Exception {
        for(Class<?> c=value.getClass();c!=null;c=c.getSuperclass())try{Field f=c.getDeclaredField(name);f.setAccessible(true);f.set(value,replacement);return;}catch(NoSuchFieldException ignored){}
        throw new NoSuchFieldException(name);
    }

    private static float configFloat(String name,float fallback) {
        try{return ((Number)field(field(Class.forName("dev.tr7zw.skinlayers.SkinLayersModBase"),"config"),name)).floatValue();}
        catch(Exception e){return fallback;}
    }

    private static float jointCoordinate(float p,int part,int axis,int category) {
        float width=configFloat("baseVoxelSize",1.15f);
        float bodyWidth=configFloat("bodyVoxelWidthSize",1.05f);
        float gap=0.025f+category*.003f;
        if(axis==0) {
            if(part==2)p=Math.max(p,(-1.9f+gap)/width);
            if(part==3)p=Math.min(p,(1.9f-gap)/width);
            if(part>=4) {
                float pivot=5f;
                float offset=part>=6?0.499f:0.998f;
                float inner=(4f*bodyWidth+gap-pivot)/width-offset;
                if(part==4||part==6)p=Math.max(p,inner);
                else p=Math.min(p,-inner);
            }
        }
        if(axis==1) {
            if(part==0) {
                float scale=configFloat("headVoxelSize",1.18f);
                p=Math.min(p,(4f-gap)/scale-3.36f);
            }
            if(part==1) {
                p=Math.max(p,0.2f+gap/1.035f);
                p=Math.min(p,0.2f+(12f-gap)/1.035f);
            }
            if(part==2||part==3)p=Math.max(p,0.2f+gap/1.035f);
        }
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
    private static void ready(String key,String path,Plan p){if(LOGGED.putIfAbsent(key,true)==null)System.out.println("[NexusCharacters][NativeLayers22] PREPARED path="+path+" basePaint="+GenericLayerPixels.count(p.base)+" outerVoxels="+GenericLayerPixels.count(p.outer)+" hair="+p.hairCount+" classicBeard="+p.beardCount);}
    private static void fail(String stage,Throwable e){String key=stage+"|"+e;if(LOGGED.putIfAbsent(key,true)==null){System.err.println("[NexusCharacters][NativeLayers22] FAIL stage="+stage+" "+e);e.printStackTrace();}}

    private static final class Pending {final byte[] baseOwner=new byte[4096];}
    private static final class Plan {
        final int[] base,outer;final int[][] layers=new int[3][4096];final int hairCount,beardCount;private Object[] cooked;private int mode;
        Plan(int[] pixels,Pending pending) {
            base=new int[4096];outer=new int[4096];int hc=0,bc=0;
            for(int y=0;y<64;y++)for(int x=0;x<64;x++) {
                int i=y*64+x;
                if(GenericLayerPixels.isOuter(x,y)){
                    outer[i]=pixels[i];int owner=pending==null?1:pending.baseOwner[i];
                    layers[Math.max(0,owner-1)][i]=pixels[i];
                }
                else if(pending!=null && pending.baseOwner[i]!=0){
                    base[i]=pixels[i];if(pending.baseOwner[i]==2)hc++;if(pending.baseOwner[i]==3)bc++;
                }
            }
            hairCount=hc;beardCount=bc;
        }
        synchronized Object[] cooked()throws Exception {
            int currentMode=geometryMode();
            if(cooked!=null && mode==currentMode)return cooked;
            Object[][] o=new Object[3][];for(int c=0;c<3;c++)o[c]=meshSet(layers[c],false,c);
            Object[] out=new Object[8];
            for(int i=0;i<8;i++)out[i]=composite(i,new Object[]{o[0][i],o[1][i],o[2][i]});
            cooked=out;mode=currentMode;return out;
        }
    }
    private static final class Mask {
        final byte[] alpha=new byte[4096];final int hash;final int mode=geometryMode();final boolean base;final int category;
        Mask(int[] pixels,boolean base,int category){this.base=base;this.category=category;for(int i=0;i<4096;i++)alpha[i]=(byte)(pixels[i]>>>24);hash=31*Arrays.hashCode(alpha)+mode+(base?127:0)+category*131;}
        public int hashCode(){return hash;}
        public boolean equals(Object other){return other instanceof Mask m && base==m.base && mode==m.mode && category==m.category && Arrays.equals(alpha,m.alpha);}
    }
}
