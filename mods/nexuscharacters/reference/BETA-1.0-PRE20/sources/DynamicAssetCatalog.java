package net.tompsen.nexuscharacters;

import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Discovers appearance assets from the active resource set, including packs. */
public final class DynamicAssetCatalog {
    private static final List<String> DEFAULT_HAIR=List.of(
            "hair_short_01.png","hair_short_02.png","hair_short_03.png","hair_short_04.png","hair_short_05.png",
            "hair_long_01.png","hair_long_02.png","hair_long_03.png","hair_long_04.png","hair_long_05.png",
            "hair_long_06.png","hair_long_07.png","hair_long_08.png");
    private static final List<String> DEFAULT_FACIAL=List.of("stubble.png","moustache_short.png","moustache_thick.png",
            "moustache_user_01.png","beard_light.png","beard_light_02.png");
    private static final int[] DEFAULT_OUTFITS={1,3,4,5,6,7,10,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38};
    private static final Pattern HAIR_NUMBER=Pattern.compile("hair_([0-9]+)\\.png");
    private static final Pattern FACIAL_NUMBER=Pattern.compile("(?:facial|beard|moustache|stubble)_([0-9]+)\\.png");
    private static final Pattern OUTFIT=Pattern.compile("outfit_([0-9]+)\\.png");
    private static volatile Map<Integer,String> hair,facial;
    private static volatile int[] outfits;

    private DynamicAssetCatalog() {}

    public static int hairCount(){return hairs().size();}
    public static int hairChoiceCount(){return hairCount()+1;}
    public static int nextHair(int index){ArrayList<Integer> ids=new ArrayList<>(hairs().keySet());ids.add(0,0);int at=ids.indexOf(index);return ids.get((at<0?0:at+1)%ids.size());}
    public static int normalizeHair(int index){return index==0||hairs().containsKey(index)?index:0;}
    public static String hairAssetName(int index){return hairs().getOrDefault(index,DEFAULT_HAIR.get(0));}
    public static int facialHairCount(){return facials().size();}
    public static int facialHairChoiceCount(){return facialHairCount()+1;}
    public static int normalizeFacialIndex(int value){return value==0||facials().containsKey(value)?value:0;}
    public static int lastFacialHairId(){return Collections.max(facials().keySet());}
    public static int nextFacialIndex(int value){ArrayList<Integer> ids=new ArrayList<>(facials().keySet());ids.add(0,0);int at=ids.indexOf(value);return ids.get((at<0?0:at+1)%ids.size());}
    public static String facialHairName(int index){return facials().getOrDefault(index,DEFAULT_FACIAL.get(0));}
    public static String facialHairLabel(int index){
        if(index==0)return "Aucune";
        String[] known={"Barbe de trois jours","Moustache courte","Moustache épaisse","Moustache mince","Barbe mince 1","Barbe mince 2"};
        if(index>0&&index<=known.length)return known[index-1];
        return title(facialHairName(index));
    }
    public static String pilosityLabel(int index,String modeledStyle){
        if(index>0)return facialHairLabel(index);
        try{return String.valueOf(callStatic("net.tompsen.nexuscharacters.CulturalBeardSupport","styleLabel",modeledStyle));}
        catch(Exception ignored){return "Aucune";}
    }

    public static int outfitCount(){return outfitIds().length;}
    public static boolean outfitAllowed(int id){for(int n:outfitIds())if(n==id)return true;return false;}
    public static int normalizeOutfit(int id){int[] a=outfitIds();return outfitAllowed(id)?id:a[0];}
    public static int nextOutfit(int id){int[] a=outfitIds();id=normalizeOutfit(id);for(int i=0;i<a.length;i++)if(a[i]==id)return a[(i+1)%a.length];return a[0];}
    public static int outfitPosition(int id){int[] a=outfitIds();id=normalizeOutfit(id);for(int i=0;i<a.length;i++)if(a[i]==id)return i+1;return 1;}
    public static String outfitLabel(int id){return "Tenue "+outfitPosition(id)+"/"+outfitCount();}

    public static int normalizeFacialHair(Object race,int value){
        String name=race instanceof Enum<?> e?e.name():"";
        if(!name.equals("HUMAN")&&!name.equals("NORDIC"))return 0;
        return normalizeFacialIndex(value);
    }
    public static int nextFacialHair(Object race,int value){
        int n=normalizeFacialHair(race,value);return normalizeFacialHair(race,nextFacialIndex(n));
    }

    public static String withOutfit(String marker,int outfit){
        if(marker==null)return null;
        return marker.replaceFirst("_o[0-9]+_",String.format(Locale.ROOT,"_o%02d_",normalizeOutfit(outfit)));
    }

    /** Replacement for Appearance69Support.marker without fixed cosmetic maxima. */
    public static String marker(Object race,int preset,int eyeColor,int hairColor,int eyeDrop,int eye,int hairIndex,
                                int facialIndex,int facialColor,int marking,int markingColor,int tone) {
        int body=integer("net.tompsen.nexuscharacters.ModularSkinSupport","body",preset,1);
        int outfit=normalizeOutfit(integer("net.tompsen.nexuscharacters.ModularSkinSupport","outfit",preset,1));
        return "__capitale_preset__:"+String.format(Locale.ROOT,
                "player_v69_b%d_e%d_ec%d_ey%d_h%02d_hc%02d_s%02d_o%02d_fh%d_fc%02d_mk%d_mc%d",
                clamp(body,1,4),clamp(eye,1,8),clamp(eyeColor,0,7),clamp(eyeDrop,0,4),
                normalizeHair(hairIndex),clamp(hairColor,0,12),clamp(tone,1,16),outfit,
                normalizeFacialHair(race,facialIndex),clamp(facialColor,0,12),clamp(marking,0,8),clamp(markingColor,0,12));
    }

    public static synchronized void clear(){hair=null;facial=null;outfits=null;}

    private static Map<Integer,String> hairs(){
        Map<Integer,String> value=hair;if(value!=null)return value;
        synchronized(DynamicAssetCatalog.class){
            if(hair!=null)return hair;
            Set<String> names=resourceNames("appearance_parts_v064/hair");
            LinkedHashMap<Integer,String> all=new LinkedHashMap<>();for(int i=0;i<DEFAULT_HAIR.size();i++)all.put(i+1,DEFAULT_HAIR.get(i));
            for(String name:names){
                if(DEFAULT_HAIR.contains(name))continue;Matcher numbered=HAIR_NUMBER.matcher(name);
                if(numbered.matches()){int id=Integer.parseInt(numbered.group(1));if(id>=14)all.putIfAbsent(id,name);continue;}
                Matcher longHair=Pattern.compile("hair_long_([0-9]+)\\.png").matcher(name);
                if(longHair.matches()){int n=Integer.parseInt(longHair.group(1));if(n>8)all.putIfAbsent(5+n,name);continue;}
                Matcher shortHair=Pattern.compile("hair_short_([0-9]+)\\.png").matcher(name);
                if(shortHair.matches()){int n=Integer.parseInt(shortHair.group(1));if(n>5)all.putIfAbsent(1000+n,name);}
            }
            hair=Collections.unmodifiableMap(new TreeMap<>(all));return hair;
        }
    }
    private static Map<Integer,String> facials(){
        Map<Integer,String> value=facial;if(value!=null)return value;
        synchronized(DynamicAssetCatalog.class){
            if(facial!=null)return facial;
            Set<String> names=resourceNames("appearance_parts_v068/facial_hair");
            LinkedHashMap<Integer,String> all=new LinkedHashMap<>();for(int i=0;i<DEFAULT_FACIAL.size();i++)all.put(i+1,DEFAULT_FACIAL.get(i));
            ArrayList<String> unnumbered=new ArrayList<>();
            for(String name:names){if(DEFAULT_FACIAL.contains(name)||!name.endsWith(".png"))continue;Matcher m=FACIAL_NUMBER.matcher(name);if(m.matches()&&Integer.parseInt(m.group(1))>=7)all.putIfAbsent(Integer.parseInt(m.group(1)),name);else unnumbered.add(name);}
            Collections.sort(unnumbered);int next=7;for(String name:unnumbered){while(all.containsKey(next))next++;all.put(next++,name);}
            facial=Collections.unmodifiableMap(new TreeMap<>(all));return facial;
        }
    }
    private static int[] outfitIds(){
        int[] value=outfits;if(value!=null)return value;
        synchronized(DynamicAssetCatalog.class){
            if(outfits!=null)return outfits;
            Set<Integer> ids=new TreeSet<>();
            for(String name:resourceNames("appearance_parts_v064/outfits")){Matcher m=OUTFIT.matcher(name);if(m.matches())ids.add(Integer.parseInt(m.group(1)));}
            outfits=ids.isEmpty()?DEFAULT_OUTFITS.clone():ids.stream().mapToInt(Integer::intValue).toArray();return outfits;
        }
    }
    private static Set<String> resourceNames(String prefix){
        TreeSet<String> result=new TreeSet<>();
        try {
            Object client=callStatic("net.minecraft.class_310","method_1551");if(client==null)return result;
            Object manager=call(client,"method_1478");
            @SuppressWarnings("unchecked") Map<Object,Object> found=(Map<Object,Object>)call(manager,"method_14488",prefix,(Predicate<Object>)id->String.valueOf(id).startsWith("nexuscharacters:"));
            for(Object id:found.keySet()){
                String path=String.valueOf(id);int slash=path.lastIndexOf('/');if(slash>=0)result.add(path.substring(slash+1));
            }
        }catch(Throwable ignored){}
        return result;
    }
    private static String title(String file){String s=file.replaceFirst("\\.png$","").replace('_',' ');return Character.toUpperCase(s.charAt(0))+s.substring(1);}
    private static int integer(String type,String method,int value,int fallback){try{return ((Number)callStatic(type,method,value)).intValue();}catch(Exception ignored){return fallback;}}
    private static int clamp(int n,int min,int max){return Math.max(min,Math.min(max,n));}
    private static Object callStatic(String type,String name,Object...args)throws Exception{return method(Class.forName(type),name,args.length).invoke(null,args);}
    private static Object call(Object object,String name,Object...args)throws Exception{return method(object.getClass(),name,args.length).invoke(object,args);}
    private static Method method(Class<?> type,String name,int argc)throws NoSuchMethodException{
        for(Class<?> c=type;c!=null;c=c.getSuperclass())for(Method m:c.getDeclaredMethods())if(m.getName().equals(name)&&m.getParameterCount()==argc){m.setAccessible(true);return m;}
        for(Method m:type.getMethods())if(m.getName().equals(name)&&m.getParameterCount()==argc){m.setAccessible(true);return m;}throw new NoSuchMethodException(type.getName()+"."+name);
    }
}

