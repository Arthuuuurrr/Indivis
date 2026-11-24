package net.tompsen.nexuscharacters;
import java.io.*;
import java.util.*;
import net.minecraft.*;

/** Recolours actual pixels once per choice, leaving original gold/gem pixels intact.
 * Evicted GPU textures survive two client ticks, past deferred submissions.
 */
public final class BlockbenchTextureCache {
    private record Key(String resource,int argb) {}
    private record Retired(class_2960 id,long tick) {}
    private static final LinkedHashMap<Key,class_2960> CACHE=new LinkedHashMap<>(256,.75f,true);
    private static final ArrayDeque<Retired> RETIRED=new ArrayDeque<>();
    private static long tick,sequence;
    public static long creates,hits;
    private BlockbenchTextureCache() {}
    public static class_2960 texture(BlockbenchPresetCatalog.Entry e,int argb) {
        if(argb==0)return class_2960.method_60655("nexuscharacters",e.texture());
        Key key=new Key(e.texture(),argb);class_2960 cached=CACHE.get(key);if(cached!=null){hits++;return cached;}
        try(InputStream in=BlockbenchTextureCache.class.getClassLoader().getResourceAsStream("assets/nexuscharacters/"+e.texture())) {
            if(in==null)throw new FileNotFoundException(e.texture());
            class_1011 image=class_1011.method_4309(in);
            ArrayList<Float> levels=new ArrayList<>();
            for(int y=0;y<image.method_4323();y++)for(int x=0;x<image.method_4307();x++) {
                int color=image.method_61940(x,y);if((color>>>24)!=0&&tintable(e,color))levels.add(luma(color));
            }
            Collections.sort(levels);float reference=levels.isEmpty()?128f:levels.get(levels.size()/2);
            int tr=argb>>>16&255,tg=argb>>>8&255,tb=argb&255;
            for(int y=0;y<image.method_4323();y++)for(int x=0;x<image.method_4307();x++) {
                int color=image.method_61940(x,y);if((color>>>24)==0||!tintable(e,color))continue;
                float ratio=luma(color)/Math.max(1,reference);
                int r=Math.min(255,Math.round(tr*ratio)),g=Math.min(255,Math.round(tg*ratio)),b=Math.min(255,Math.round(tb*ratio));
                image.method_61941(x,y,(color&0xff000000)|(r<<16)|(g<<8)|b);
            }
            class_2960 id=class_2960.method_60655("nexuscharacters","dynamic/blockbench/"+e.kind()+"/"+e.id()+"_"+Integer.toHexString(argb)+"_"+(sequence++));
            class_1043 texture=new class_1043(()->"Nexus Blockbench "+e.id(),image);
            class_310.method_1551().method_1531().method_4616(id,texture);CACHE.put(key,id);creates++;
            while(CACHE.size()>256){var i=CACHE.entrySet().iterator();var old=i.next();RETIRED.addLast(new Retired(old.getValue(),tick+2));i.remove();}
            return id;
        }catch(IOException failure){throw new IllegalStateException("Blockbench texture "+e.id(),failure);}
    }
    private static float luma(int c){return ((c>>>16&255)*.2126f+(c>>>8&255)*.7152f+(c&255)*.0722f);}
    static boolean tintable(BlockbenchPresetCatalog.Entry entry,int color) {
        int r=color>>>16&255,g=color>>>8&255,b=color&255;
        if(entry.preserveColors().contains(color&0xffffff))return false;
        if(entry.kind().equals("oreilles"))return r>g*1.08f&&g>b*1.08f&&r-g<95&&g-b<70;
        return true;
    }
    public static void tick() {
        tick++;
        while(!RETIRED.isEmpty()&&RETIRED.peekFirst().tick()<=tick)
            class_310.method_1551().method_1531().method_4615(RETIRED.removeFirst().id());
    }
    public static void clear() {
        for(class_2960 id:CACHE.values())RETIRED.addLast(new Retired(id,tick+2));CACHE.clear();
    }
}
