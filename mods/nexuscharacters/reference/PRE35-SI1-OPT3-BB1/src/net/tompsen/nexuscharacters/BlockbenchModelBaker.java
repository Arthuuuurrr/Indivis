package net.tompsen.nexuscharacters;
import com.google.gson.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import net.minecraft.*;

/** Bedrock box-UV geometry baked once to vanilla ModelParts, without GeckoLib. */
public final class BlockbenchModelBaker {
    private static final Map<String,class_630> CACHE=new HashMap<>();
    public static long bakes,cubes;
    private BlockbenchModelBaker() {}
    public static synchronized void clear(){CACHE.clear();}
    public static synchronized class_630 model(BlockbenchPresetCatalog.Entry entry) {
        return CACHE.computeIfAbsent(entry.kind()+"/"+entry.id(),key->bake(entry));
    }
    private static JsonObject geometry(BlockbenchPresetCatalog.Entry e) {
        try(InputStream in=BlockbenchModelBaker.class.getClassLoader().getResourceAsStream("assets/nexuscharacters/"+e.geometry())) {
            if(in==null)throw new FileNotFoundException(e.geometry());
            return JsonParser.parseReader(new InputStreamReader(in,StandardCharsets.UTF_8)).getAsJsonObject().getAsJsonArray("minecraft:geometry").get(0).getAsJsonObject();
        }catch(IOException failure){throw new IllegalStateException("Blockbench geometry "+e.id(),failure);}
    }
    private static float[] vector(JsonObject object,String field,float[] fallback) {
        if(!object.has(field))return fallback.clone();JsonArray a=object.getAsJsonArray(field);
        return new float[]{a.get(0).getAsFloat(),a.get(1).getAsFloat(),a.get(2).getAsFloat()};
    }
    private static class_630 bake(BlockbenchPresetCatalog.Entry entry) {
        JsonObject geo=geometry(entry);JsonObject description=geo.getAsJsonObject("description");
        int width=description.get("texture_width").getAsInt(),height=description.get("texture_height").getAsInt();
        class_5609 data=new class_5609();Map<String,JsonObject> bones=new LinkedHashMap<>();
        for(JsonElement b:geo.getAsJsonArray("bones")){JsonObject bone=b.getAsJsonObject();bones.put(bone.get("name").getAsString(),bone);}
        Map<String,class_5610> built=new HashMap<>();Set<String> active=new HashSet<>();
        for(String name:bones.keySet())bone(name,bones,built,active,data.method_32111());
        class_630 root=class_5607.method_32110(data,width,height).method_32109();bakes++;
        return root;
    }
    private static class_5610 bone(String name,Map<String,JsonObject> bones,Map<String,class_5610> built,Set<String> active,class_5610 root) {
        class_5610 cached=built.get(name);if(cached!=null)return cached;
        if(!active.add(name))throw new IllegalArgumentException("Cyclic Blockbench bone "+name);
        JsonObject b=bones.get(name);if(b==null)throw new IllegalArgumentException("Missing parent "+name);
        float[] pivot=vector(b,"pivot",new float[]{0,24,0}),parentPivot=new float[]{0,24,0};class_5610 parent=root;
        if(b.has("parent")) {
            String parentName=b.get("parent").getAsString();parent=bone(parentName,bones,built,active,root);
            parentPivot=vector(bones.get(parentName),"pivot",new float[]{0,24,0});
        }
        class_5606 builder=class_5606.method_32108();
        if(b.has("cubes"))for(JsonElement value:b.getAsJsonArray("cubes")) {
            JsonObject c=value.getAsJsonObject();float[] origin=vector(c,"origin",new float[3]),size=vector(c,"size",new float[3]);
            JsonArray uv=c.getAsJsonArray("uv");float inflate=c.has("inflate")?c.get("inflate").getAsFloat():0;
            builder.method_32101(uv.get(0).getAsInt(),uv.get(1).getAsInt());
            builder.method_32098(origin[0]-pivot[0],pivot[1]-origin[1]-size[1],origin[2]-pivot[2],size[0],size[1],size[2],new class_5605(inflate));cubes++;
        }
        float[] rotation=vector(b,"rotation",new float[3]);float radians=(float)(Math.PI/180.0);
        class_5603 transform=class_5603.method_32091(pivot[0]-parentPivot[0],parentPivot[1]-pivot[1],pivot[2]-parentPivot[2],-rotation[0]*radians,rotation[1]*radians,-rotation[2]*radians);
        class_5610 result=parent.method_32117(name,builder,transform);built.put(name,result);active.remove(name);return result;
    }
}
