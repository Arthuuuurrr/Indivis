package net.tompsen.nexuscharacters;

import com.google.gson.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/** Data-driven additions. Existing IDs and existing racial rules are retained. */
public final class BlockbenchPresetCatalog {
    public record Entry(String kind,String id,int hairId,String label,String geometry,String texture,Set<String> races,boolean aliasNone,Set<Integer> preserveColors) {}
    private static final List<Entry> ENTRIES=load();
    private static final Map<String,Map<String,Entry>> BY_KIND=new HashMap<>();
    private static final Map<String,Optional<Appearance69Support.Params>> PARAMS=new ConcurrentHashMap<>();
    private static final Map<Integer,Entry> HAIR=new HashMap<>();
    private static final Map<String,int[]> HAIR_CHOICES=new HashMap<>();
    static {for(Entry e:ENTRIES){BY_KIND.computeIfAbsent(e.kind(),k->new ConcurrentHashMap<>()).put(e.id(),e);if(e.kind().equals("coiffures"))HAIR.put(e.hairId(),e);}}
    private BlockbenchPresetCatalog() {}
    private static List<Entry> load() {
        String path="assets/nexuscharacters/blockbench/catalog.json";
        try(InputStream in=BlockbenchPresetCatalog.class.getClassLoader().getResourceAsStream(path)) {
            if(in==null)throw new FileNotFoundException(path);
            JsonObject root=JsonParser.parseReader(new InputStreamReader(in,StandardCharsets.UTF_8)).getAsJsonObject();
            ArrayList<Entry> result=new ArrayList<>();
            for(JsonElement value:root.getAsJsonArray("entries")) {
                JsonObject e=value.getAsJsonObject();LinkedHashSet<String> races=new LinkedHashSet<>();
                for(JsonElement race:e.getAsJsonArray("races"))races.add(race.getAsString());
                Set<Integer> preserve=new HashSet<>();if(e.has("preserve_colors"))for(JsonElement color:e.getAsJsonArray("preserve_colors"))preserve.add(color.getAsInt());
                result.add(new Entry(e.get("kind").getAsString(),e.get("id").getAsString(),e.get("hair_id").getAsInt(),e.get("label").getAsString(),e.get("geometry").getAsString(),e.get("texture").getAsString(),Collections.unmodifiableSet(races),e.get("alias_none").getAsBoolean(),Set.copyOf(preserve)));
            }
            return List.copyOf(result);
        }catch(IOException|RuntimeException failure){throw new ExceptionInInitializerError(failure);}
    }
    public static List<Entry> entries(){return ENTRIES;}
    public static String base(String style) {
        if(style==null)return "none";String id=style.trim().toLowerCase(Locale.ROOT);
        int at=id.indexOf('#');if(at>=0)id=id.substring(0,at);at=id.indexOf('~');if(at>=0)id=id.substring(0,at);return id;
    }
    public static Entry entry(String kind,String style){
        if(style==null)return null;
        String key=style.startsWith("bb_")?style:base(style);if(!key.startsWith("bb_"))return null;
        Map<String,Entry> entries=BY_KIND.get(kind);if(entries==null)return null;
        Entry e=entries.get(key);if(e!=null)return e;
        e=entries.get(base(key));if(e!=null&&entries.size()<2048)entries.putIfAbsent(key,e);return e;
    }
    public static Entry hair(int id){return HAIR.get(id);}
    public static boolean custom(String kind,String id){return entry(kind,id)!=null;}
    public static String raceId(Object race) {
        if(race==null)return "";
        return race instanceof Enum<?> e?e.name().toLowerCase(Locale.ROOT):race.toString().trim().toLowerCase(Locale.ROOT);
    }
    public static boolean allowed(Entry entry,Object race){return entry!=null&&entry.races().contains(raceId(race));}
    public static Appearance69Support.Params params(String recipe) {
        if(recipe==null)return null;Optional<Appearance69Support.Params> cached=PARAMS.get(recipe);if(cached!=null)return cached.orElse(null);
        int start=recipe.indexOf("player_v69_");Appearance69Support.Params value=start<0?null:Appearance69Support.parse(recipe.substring(start));
        if(PARAMS.size()>=512)PARAMS.clear();PARAMS.put(recipe,Optional.ofNullable(value));return value;
    }
    public static boolean hasCustom(CharacterDto dto) {
        if(dto==null)return false;
        Appearance69Support.Params p=params(dto.skinUsername());
        return p!=null&&hair(p.hair())!=null||custom("barbes",dto.beardStyle())||custom("oreilles",dto.earStyle());
    }
    public static String normalizeEar(Object race,String style) {
        Entry e=entry("oreilles",style);
        if(allowed(e,race))return e.id();
        String r=raceId(race);return r.equals("wood_elf")||r.equals("high_elf")?"long":"none";
    }
    public static Set<String> extendBeardSet(Set<String> old) {
        LinkedHashSet<String> result=new LinkedHashSet<>(old);
        for(Entry e:ENTRIES)if(e.kind().equals("barbes")&&!e.aliasNone())result.add(e.id());
        return Collections.unmodifiableSet(result);
    }
    public static String[] extendBeardChoices(String[] old,String race) {
        ArrayList<String> result=new ArrayList<>(Arrays.asList(old));
        for(Entry e:ENTRIES)if(e.kind().equals("barbes")&&!e.aliasNone()&&allowed(e,race))result.add(e.id());
        return result.toArray(String[]::new);
    }
    public static boolean beardAllowed(Object race,String style){return allowed(entry("barbes",style),race);}
    public static String label(String kind,String style){Entry e=entry(kind,style);return e==null?style:e.label();}
    public static synchronized void clearHairChoices(){HAIR_CHOICES.clear();PARAMS.clear();}
    public static synchronized int[] hairChoices(Object race) {
        String r=raceId(race);int[] cached=HAIR_CHOICES.get(r);if(cached!=null)return cached.clone();
        ArrayList<Integer> choices=new ArrayList<>();choices.add(0);int id=0;
        for(int guard=0;guard<10000;guard++) {
            id=DynamicAssetCatalog.nextHair(id);if(id==0)break;
            Entry e=hair(id);if(e==null||allowed(e,race))choices.add(id);
        }
        int[] result=choices.stream().mapToInt(Integer::intValue).toArray();HAIR_CHOICES.put(r,result);return result.clone();
    }
    public static int nextHair(Object race,int current) {
        int[] choices=hairChoices(race);for(int i=0;i<choices.length;i++)if(choices[i]==current)return choices[(i+1)%choices.length];return choices[0];
    }
    public static int normalizeHair(Object race,int current) {
        Entry e=hair(current);return e==null||allowed(e,race)?current:1;
    }
    public static String nextEar(Object race,String current) {
        ArrayList<String> choices=new ArrayList<>(List.of("discreet","long","upswept"));
        for(Entry e:ENTRIES)if(e.kind().equals("oreilles")&&allowed(e,race))choices.add(e.id());
        int i=choices.indexOf(base(current));return choices.get((i+1)%choices.size());
    }
    public static void refreshHairLabel(Object screen) {
        Object race=UnifiedPilositySupport.get(screen,"race");int hair=UnifiedPilositySupport.intValue(screen,"nexuscharacters$hair",1);
        int[] choices=hairChoices(race);int position=1;
        for(int i=0;i<choices.length;i++)if(choices[i]==hair){position=i+1;break;}
        Entry e=hair(hair);String name=e!=null?e.label():hair==0?"Chauve":"Coiffure "+hair;
        UnifiedPilositySupport.setText(UnifiedPilositySupport.get(screen,"nexuscharacters$hairButton"),name+" ("+position+"/"+choices.length+")");
    }
    public static boolean cycleEar(Object screen) {
        Object race=UnifiedPilositySupport.get(screen,"race");String id=raceId(race);
        if(!id.equals("wood_elf")&&!id.equals("high_elf"))return false;
        String old=UnifiedPilositySupport.stringValue(screen,"nexuscharacters$earStyle","long");
        UnifiedPilositySupport.setString(screen,"nexuscharacters$earStyle",nextEar(race,old));
        CulturalBeardUiSupport.refreshCosmeticButton(screen);return true;
    }
    public static boolean refreshEar(Object screen) {
        Object race=UnifiedPilositySupport.get(screen,"race");String style=UnifiedPilositySupport.stringValue(screen,"nexuscharacters$earStyle","long");
        Entry e=entry("oreilles",style);if(e==null)return false;
        style=normalizeEar(race,style);UnifiedPilositySupport.setString(screen,"nexuscharacters$earStyle",style);
        Object button=UnifiedPilositySupport.get(screen,"nexuscharacters$cosmeticButton");
        UnifiedPilositySupport.setVisibleActive(button,true,true);UnifiedPilositySupport.setText(button,"Oreilles : "+label("oreilles",style));return true;
    }
}
