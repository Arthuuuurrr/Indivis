package fr.hautecapitale.creatures.spawn;

import net.fabricmc.api.ModInitializer;
import java.lang.reflect.*;
import java.util.*;

public final class CapitaleCreaturesSpawnSafety138 implements ModInitializer {
    private static volatile boolean attempted=false, reflectionReady=false;
    private static Method down, isWater, getBlockState, isLeaves, isSolidBlock;
    private static Object leavesTag;

    @Override public void onInitialize(){
        try {
            registerOneShotEndWorldTick();
            System.out.println("[Capitale Creatures] 1.2.38 spawn safety armed: terrestrial spawns require dry solid non-leaf support; patrol helper enabled.");
        } catch(Throwable t){System.err.println("[Capitale Creatures] 1.2.38 spawn safety registration failure: "+root(t));}
    }

    private static void registerOneShotEndWorldTick() throws Exception {
        Class<?> cb=Class.forName("net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents$EndWorldTick");
        Object event=Class.forName("net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents").getField("END_WORLD_TICK").get(null);
        Object proxy=Proxy.newProxyInstance(cb.getClassLoader(),new Class<?>[]{cb},(p,m,a)->{
            if(m.getDeclaringClass()==Object.class)return objectMethod(p,m,a);
            applyOnce(); return null;
        });
        Class.forName("net.fabricmc.fabric.api.event.Event").getMethod("register",Object.class).invoke(event,proxy);
    }
    private static Object objectMethod(Object p,Method m,Object[] a){return switch(m.getName()){case"toString"->"CapitaleSpawnSafety138";case"hashCode"->System.identityHashCode(p);case"equals"->p==(a==null?null:a[0]);default->null;};}

    private static synchronized void initPhysical() throws Exception {
        if(reflectionReady)return;
        Class<?> pos=Class.forName("net.minecraft.class_2338");
        Class<?> world=Class.forName("net.minecraft.class_3218");
        Class<?> blockView=Class.forName("net.minecraft.class_1922");
        Class<?> state=Class.forName("net.minecraft.class_2680");
        Class<?> tagKey=Class.forName("net.minecraft.class_6862");
        down=pos.getMethod("method_10074");
        isWater=world.getMethod("method_22351",pos);
        getBlockState=blockView.getMethod("method_8320",pos);
        leavesTag=Class.forName("net.minecraft.class_3481").getField("field_15503").get(null);
        isLeaves=state.getMethod("method_26164",tagKey);
        isSolidBlock=state.getMethod("method_26212",blockView,pos);
        reflectionReady=true;
    }

    public static boolean validGroundAt(Object world,Object spawnPos){
        try{
            initPhysical();
            Object below=down.invoke(spawnPos);
            if(Boolean.TRUE.equals(isWater.invoke(world,spawnPos)))return false;
            if(Boolean.TRUE.equals(isWater.invoke(world,below)))return false;
            Object state=getBlockState.invoke(world,below);
            if(state==null)return false;
            if(Boolean.TRUE.equals(isLeaves.invoke(state,leavesTag)))return false;
            return Boolean.TRUE.equals(isSolidBlock.invoke(state,world,below));
        }catch(Throwable t){
            System.err.println("[Capitale Creatures] 1.2.38 ground validation failure: "+root(t));
            return false;
        }
    }

    private static synchronized void applyOnce(){
        if(attempted)return; attempted=true;
        try{
            int n=applyWrappers();
            System.out.println("[Capitale Creatures] 1.2.38 spawn safety active: wrapped="+n+" terrestrial entity types.");
        }catch(Throwable t){
            System.err.println("[Capitale Creatures] 1.2.38 spawn safety apply failure: "+root(t));
            t.printStackTrace();
        }
    }

    @SuppressWarnings({"unchecked","rawtypes"})
    private static int applyWrappers() throws Exception {
        Class<?> sr=Class.forName("net.minecraft.class_1317");
        Class<?> entityType=Class.forName("net.minecraft.class_1299");
        Class<?> entry=Class.forName("net.minecraft.class_1317$class_1318");
        Class<?> predicate=Class.forName("net.minecraft.class_1317$class_4306");
        Class<?> loc=Class.forName("net.minecraft.class_2902$class_2903");
        Class<?> height=Class.forName("net.minecraft.class_9168");

        Field mapField;
        try{mapField=sr.getDeclaredField("field_6313");}
        catch(NoSuchFieldException x){mapField=Arrays.stream(sr.getDeclaredFields()).filter(f->Map.class.isAssignableFrom(f.getType())).findFirst().orElseThrow();}
        mapField.setAccessible(true);
        Map map=(Map)mapField.get(null);
        Method lookup=entityType.getMethod("method_5898",String.class);
        Method getLoc=findNoArg(entry,loc,"comp_2253");
        Method getHeight=findNoArg(entry,height,"comp_2254");
        Method getPred=findNoArg(entry,predicate,"comp_2255");
        Constructor<?> ctor=Arrays.stream(entry.getDeclaredConstructors()).filter(c->{Class<?>[] p=c.getParameterTypes();return p.length==3&&p[0]==loc&&p[1]==height&&p[2]==predicate;}).findFirst().orElseThrow();
        ctor.setAccessible(true);

        LinkedHashSet<String> ids=new LinkedHashSet<>();
        addPrivateArray(ids,"fr.hautecapitale.creatures.spawn.CapitaleCreaturesSpawnSurfaceGuard","GROUND");
        addPrivateArray(ids,"fr.hautecapitale.creatures.spawn.CapitaleCreaturesExtraSpawnRestrictions","GROUND");
        addPrivateArray(ids,"fr.hautecapitale.creatures.spawn.CapitaleCreaturesExtraSpawnRestrictions","ORC_GROUND");

        int wrapped=0;
        for(String id:ids){
            Object got=lookup.invoke(null,id);
            if(!(got instanceof Optional<?> opt)||opt.isEmpty())continue;
            Object type=opt.get(), old=map.get(type);
            if(old==null)continue;
            Object location=getLoc.invoke(old), hm=getHeight.invoke(old), oldPred=getPred.invoke(old);
            Object proxy=Proxy.newProxyInstance(predicate.getClassLoader(),new Class<?>[]{predicate},(p,m,a)->{
                if(m.getDeclaringClass()==Object.class)return objectMethod(p,m,a);
                if(m.getReturnType()==boolean.class && a!=null && a.length==5){
                    Object original=m.invoke(oldPred,a);
                    if(!Boolean.TRUE.equals(original))return false;
                    return validGroundAt(a[1],a[3]);
                }
                return m.invoke(oldPred,a);
            });
            map.put(type,ctor.newInstance(location,hm,proxy)); wrapped++;
        }
        return wrapped;
    }

    private static Method findNoArg(Class<?> owner,Class<?> ret,String preferred)throws Exception{
        try{Method m=owner.getDeclaredMethod(preferred);m.setAccessible(true);return m;}catch(NoSuchMethodException ignored){}
        for(Method m:owner.getDeclaredMethods())if(m.getParameterCount()==0&&m.getReturnType()==ret){m.setAccessible(true);return m;}
        throw new NoSuchMethodException(owner+" -> "+ret);
    }
    private static void addPrivateArray(Set<String> out,String cls,String field)throws Exception{
        Field f=Class.forName(cls).getDeclaredField(field);f.setAccessible(true);String[] a=(String[])f.get(null);Collections.addAll(out,a);
    }
    private static Throwable root(Throwable t){while(t instanceof InvocationTargetException ite&&ite.getCause()!=null)t=ite.getCause();return t;}
}
