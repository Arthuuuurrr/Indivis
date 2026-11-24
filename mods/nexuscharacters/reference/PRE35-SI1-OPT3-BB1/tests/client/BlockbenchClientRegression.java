import java.nio.file.*;
import java.lang.reflect.*;
import java.util.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.*;
import net.tompsen.nexuscharacters.*;

/** Real menu controls and real GUI entity submissions; no external world/save. */
public final class BlockbenchClientRegression implements ClientModInitializer {
    static final Path OUT=Path.of(System.getProperty("npcqa.out"));
    static int tick,scene,stage,checks,captures;static boolean ready,failed,captured;
    static long before,frames,stageStart;static List<BlockbenchPresetCatalog.Entry> entries;
    static void check(boolean ok,String text){checks++;if(!ok)throw new AssertionError(text);}
    static class_339 widget(Object screen,String name){return (class_339)IndivisMenus.get(screen,name);}
    static class_11909 mouse(double x,double y){return new class_11909(x,y,new class_11910(0,0));}
    static void click(class_437 screen,class_339 button){screen.method_25402(mouse(button.method_46426()+button.method_25368()/2d,button.method_46427()+button.method_25364()/2d),false);}
    static void setRace(class_437 screen,CharacterRace race) {
        for(int i=0;i<6&&IndivisMenus.get(screen,"race")!=race;i++) {
            IndivisMenus.layoutCreation(screen);class_339 button=null;
            for(class_364 child:screen.method_25396())if(child instanceof class_339 w&&w.method_25369().getString().startsWith("Race")){button=w;break;}
            check(button!=null,"Race control absent");click(screen,button);
        }
        check(IndivisMenus.get(screen,"race")==race,"Race control did not switch "+race);
    }
    static int cubeCount(class_630 root)throws Exception {
        int count=((List<?>)SurfaceGeometry.field(root,"field_3663")).size();
        for(Object child:((Map<?,?>)SurfaceGeometry.field(root,"field_3661")).values())count+=cubeCount((class_630)child);
        return count;
    }
    static void palettes()throws Exception {
        String[] kinds={"coiffures","barbes","barbes","barbes","barbes","oreilles"};String[] ids={"bb_chignon","bb_barbe_runique","bb_barbe_royale_naine","bb_barbe_tressee_clan","bb_barbe_seigneur_profondeurs","bb_bijou_dore"};
        int variants=0;
        for(int k=0;k<kinds.length;k++) {
            var entry=BlockbenchPresetCatalog.entry(kinds[k],ids[k]);
            try(var input=BlockbenchClientRegression.class.getClassLoader().getResourceAsStream("assets/nexuscharacters/"+entry.texture());var original=class_1011.method_4309(input)) {
                int size=kinds[k].equals("oreilles")?16:12;
                for(int choice=1;choice<=size;choice++) {
                    int argb=kinds[k].equals("oreilles")?Appearance69Support.skinToneArgb(choice):Appearance69Support.hairColorArgb(choice);
                    var id=BlockbenchTextureCache.texture(entry,argb);check(id.equals(BlockbenchTextureCache.texture(entry,argb)),"Tint texture not cached");
                    var tinted=((class_1043)class_310.method_1551().method_1531().method_4619(id)).method_4525();int changed=0;
                    for(int y=0;y<original.method_4323();y++)for(int x=0;x<original.method_4307();x++) {
                        int a=original.method_61940(x,y),b=tinted.method_61940(x,y);check((a>>>24)==(b>>>24),"Tint modified alpha");if(a!=b)changed++;
                        if(entry.preserveColors().contains(a&0xffffff))check(a==b,"Authored ornament recoloured");
                        if(!kinds[k].equals("oreilles")&&(a>>>24)!=0&&!entry.preserveColors().contains(a&0xffffff)&&choice==2)check(a!=b,"Hair material failed to follow brown palette: "+entry.id());
                    }
                    check(changed>0,"Palette not applied "+entry.id()+" colour="+choice);variants++;
                }
            }
        }
        Files.writeString(OUT.resolve("palettes-pass.txt"),"BB_PALETTES_PASS variants="+variants+" alpha_and_authored_ornaments_preserved=true\n");
    }
    static void metadata()throws Exception {
        entries=BlockbenchPresetCatalog.entries();check(entries.size()==91,"Archive model count");int cubes=0;
        for(var e:entries) {
            class_630 model=BlockbenchModelBaker.model(e);check(model==BlockbenchModelBaker.model(e),"Model not cached "+e.id());cubes+=cubeCount(model);
            for(CharacterRace race:RacialAppearance69Support.playableRaces()) {
                boolean allowed=e.races().contains(race.id());
                if(e.kind().equals("barbes")&&!e.aliasNone()) {
                    String normalized=CharacterCosmetics.normalizeBeard(race,e.id()+"#red");
                    check(allowed?normalized.equals(e.id()+"#red"):normalized.equals("none"),"Beard race/colour "+race+" "+e.id()+" got "+normalized);
                }
                if(e.kind().equals("oreilles"))check(CharacterCosmetics.normalizeEar(race,e.id()).equals(allowed?e.id():CharacterCosmetics.defaultEar(race)),"Ear race "+race+" "+e.id());
                if(e.kind().equals("coiffures"))check(BlockbenchPresetCatalog.normalizeHair(race,e.hairId())==(allowed?e.hairId():1),"Hair race "+race+" "+e.id());
            }
        }
        check(cubes==2968,"Native cube count differs: "+cubes);
        String recipe="__capitale_preset__:player_v69_b1_e1_ec0_ey2_h500_hc02_s04_o01_fh0_fc00_mk0_mc0";
        check(BlockbenchPresetCatalog.params(recipe)==BlockbenchPresetCatalog.params(recipe),"Appearance recipe parsed again instead of reused");
        for(CharacterRace race:RacialAppearance69Support.playableRaces()) {
            int[] hairs=BlockbenchPresetCatalog.hairChoices(race);Set<Integer> actual=new LinkedHashSet<>();for(int h:hairs)check(actual.add(h),"Duplicate hair choice");
            for(int old=0;old<=13;old++)check(actual.contains(old),"Legacy hair removed "+old);
            for(var e:entries)if(e.kind().equals("coiffures"))check(actual.contains(e.hairId())==e.races().contains(race.id()),"Hair selector distribution mismatch "+race+" "+e.id());
            int h=0;for(int i=0;i<hairs.length;i++)h=BlockbenchPresetCatalog.nextHair(race,h);check(h==0,"Hair selector cycle incomplete");
        }
        Files.writeString(OUT.resolve("catalogue-pass.txt"),"BB_CATALOGUE_PASS checks="+checks+" models="+entries.size()+" cubes="+cubes+"\n");
    }
    static void choose(class_310 client,BlockbenchPresetCatalog.Entry e) {
        class_437 screen=new CharacterCreationScreen(null,()->{});client.method_1507(screen);
        CharacterRace race=CharacterRace.fromId(e.races().iterator().next());setRace(screen,race);
        IndivisMenus.set(screen,"nexuscharacters$hair",0);IndivisMenus.set(screen,"nexuscharacters$facialHair",0);
        IndivisMenus.set(screen,"nexuscharacters$earStyle",CharacterCosmetics.defaultEar(race));
        IndivisMenus.set(screen,"nexuscharacters$beardStyle",CharacterCosmetics.defaultBeard(race));
        if(e.kind().equals("coiffures"))IndivisMenus.set(screen,"nexuscharacters$hair",e.hairId());
        if(e.kind().equals("oreilles"))IndivisMenus.set(screen,"nexuscharacters$earStyle",e.id());
        if(e.kind().equals("barbes"))IndivisMenus.set(screen,"nexuscharacters$beardStyle",e.aliasNone()?"none":e.id()+"#brown");
        IndivisMenus.set(screen,"heightScale",race.defaultBuild()>1?race.minHeight():race.maxHeight());
        IndivisMenus.set(screen,"buildScale",race.defaultBuild());
        IndivisMenus.invoke(screen,"nexuscharacters$changed");IndivisMenus.layoutCreation(screen);IndivisMenus.yaw(0);
        before=BlockbenchRenderSupport.renderCount(e.id());frames=BlockbenchRenderSupport.previewRenders;
    }
    static void capture(class_310 c,String name) {
        captured=false;
        class_318.method_1663(c.method_1522(),im->{try{im.method_4314(OUT.resolve(name));captures++;captured=true;}catch(Exception ex){throw new RuntimeException(ex);}finally{im.close();}});
    }
    public void onInitializeClient(){ClientTickEvents.END_CLIENT_TICK.register(c->{try{
        if(failed)return;
        if(!ready) {
            if(c.field_1755==null||c.method_18506()!=null||++tick<60)return;
            ready=true;tick=0;Files.createDirectories(OUT);metadata();palettes();NexusCharacters.DATA_FILE_MANAGER.characterList.clear();
        }
        if(scene>=entries.size()) {
            Files.writeString(OUT.resolve("pass.txt"),"BB_CLIENT_PASS checks="+checks+" models="+entries.size()+" captures="+captures+" feature_submissions="+BlockbenchRenderSupport.submissions+" model_bakes="+BlockbenchModelBaker.bakes+" texture_creates="+BlockbenchTextureCache.creates+"\n");c.method_1490();return;
        }
        var e=entries.get(scene);
        if(stage==0){choose(c,e);stage=1;stageStart=System.nanoTime();return;}
        check(System.nanoTime()-stageStart<20_000_000_000L,"Preview timeout scene="+scene+" stage="+stage+" "+e.id());
        if(stage==1&&BlockbenchRenderSupport.previewRenders>=frames+2) {
            if(!e.aliasNone())check(BlockbenchRenderSupport.renderCount(e.id())>before,"No actual feature submissions for "+e.kind()+"/"+e.id());
            check(BlockbenchModelBaker.bakes==91,"Per-frame model rebuild");
            capture(c,String.format("preset-%02d-%s-%s.png",scene,e.kind(),e.id()));stage=2;
        }
        else if(stage==2&&captured){IndivisMenus.yaw(180);frames=BlockbenchRenderSupport.previewRenders;stage=3;stageStart=System.nanoTime();}
        else if(stage==3&&BlockbenchRenderSupport.previewRenders>=frames+2){capture(c,String.format("preset-%02d-back.png",scene));stage=4;}
        else if(stage==4&&captured){System.out.println("BB_SCENE_PASS "+scene+" "+e.kind()+" "+e.id()+" submissions="+(BlockbenchRenderSupport.renderCount(e.id())-before));scene++;stage=0;}
        tick++;
    }catch(Throwable failure){failed=true;failure.printStackTrace();try{Files.createDirectories(OUT);var s=new java.io.StringWriter();failure.printStackTrace(new java.io.PrintWriter(s));Files.writeString(OUT.resolve("failure.txt"),s.toString());}catch(Exception ignored){}c.method_1490();}});}
}
