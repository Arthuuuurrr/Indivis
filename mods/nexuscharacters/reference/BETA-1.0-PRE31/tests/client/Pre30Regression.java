import java.nio.file.*;
import java.lang.reflect.*;
import java.util.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.*;
import net.tompsen.nexuscharacters.*;

public final class Pre30Regression implements ClientModInitializer {
    static int tick, scene = -1, captures, checks;
    static boolean ready;
    static final int SINGLE = Integer.getInteger("pre30.scene", -1);
    static final CharacterRace[] RACES = {CharacterRace.HUMAN, CharacterRace.DWARF, CharacterRace.WOOD_ELF, CharacterRace.HIGH_ELF, CharacterRace.NORDIC};
    static final boolean WORLD = Boolean.getBoolean("pre28.world");
    static final boolean ROTATION = Boolean.getBoolean("pre28.rotation");
    static final Path OUT = Path.of(SINGLE >= 0 ? "pre31/qa/recapture" : WORLD ? "pre31/qa/world-evidence" : ROTATION ? "pre31/qa/rotation-evidence" : Boolean.getBoolean("pre30.baseline") ? "pre31/qa/baseline-evidence" : "pre31/qa/evidence");
    static void check(boolean condition, String message) { checks++; if (!condition) throw new AssertionError(message); }
    static class_11909 mouse(double x, double y) { return new class_11909(x, y, new class_11910(0, 0)); }
    static void click(class_437 s, class_339 w) {
        check(w != null, "Missing control");
        s.method_25402(mouse(w.method_46426() + w.method_25368() / 2d, w.method_46427() + w.method_25364() / 2d), false);
    }
    static class_339 raceButton(class_437 s) {
        for (class_364 child : s.method_25396()) if (child instanceof class_339 w && w.method_25369().getString().startsWith("Race")) return w;
        throw new AssertionError("Race button absent");
    }
    static void setRace(class_437 s, CharacterRace race) {
        for (int i = 0; i < 6 && IndivisMenus.get(s, "race") != race; i++) { IndivisMenus.layoutCreation(s); click(s, raceButton(s)); }
        check(IndivisMenus.get(s, "race") == race, "Race click failed: " + race);
        check(raceButton(s).method_25369().getString().contains(race.label()), "Race label stale");
    }
    static CharacterDto dto(CharacterRace race) {
        String name = race == CharacterRace.HIGH_ELF ? "§6Ferendil §6§lIlwëedur" : race == CharacterRace.WOOD_ELF ? "§2Elendil §aAraenoth" : race == CharacterRace.NORDIC ? "§9Balin §9§lKuld-Dûr" : "§0Arthur §fPendragon";
        return new CharacterDto(UUID.nameUUIDFromBytes(race.id().getBytes()), name, null, null,
            "__capitale_preset__:player_v69_b1_e1_ec0_ey2_h01_hc02_s04_o01_fh0_fc00_mk0_mc0", 0, false,
            race.id(), race.maxHeight(), race.maxBuild(), "none", "none");
    }
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(c -> { try {
            if (!ready) {
                if (c.field_1755 == null || c.method_18506() != null) return;
                if (++tick < 80) return;
                ready = true; tick = 0;
                Files.createDirectories(OUT);
                NexusCharacters.DATA_FILE_MANAGER.characterList.clear();
                for (CharacterRace r : RACES) NexusCharacters.DATA_FILE_MANAGER.characterList.add(dto(r));
                System.out.println("PRE30_MENU_TEST_STARTED");
                if (WORLD) {
                    NexusCharacters.selectedCharacter = dto(RACES[0]);
                    NexusCharacters.DATA_FILE_MANAGER.save();
                    c.method_41735().method_57784("pre28-menu-test", () -> {});
                    return;
                }
            }
            if (WORLD && c.field_1724 == null) return;
            if (WORLD) c.method_1566().method_2000();
            int nextScene = tick / 24 + (SINGLE >= 0 ? SINGLE : ROTATION ? 20 : 0), phase = tick % 24;
            if (nextScene >= (SINGLE >= 0 ? SINGLE + 1 : 23)) {
                if (SINGLE < 0) RenderPerf.run();
                if (Boolean.getBoolean("pre31.steady")) SteadyPerf.run();
                System.out.println("PRE30_MENU_PASS captures=" + captures + " checks=" + checks);
                Files.writeString(OUT.resolve("result.txt"), "PRE30_MENU_PASS captures=" + captures + " checks=" + checks + "\n");
                c.method_1490(); return;
            }
            if (phase == 0) {
                scene = nextScene;
                int scale = scene < 15 ? 2 + scene / 5 : 2;
                c.field_1690.method_42474().method_41748(scale); c.method_15993();
                if (scene < 15 || scene >= 20) {
                    class_437 s = new CharacterCreationScreen(null, () -> {}); c.method_1507(s);
                    setRace(s, RACES[scene < 15 ? scene % 5 : 1]);
                    ((class_342)IndivisMenus.get(s, "name")).method_1852("Arthur");
                    ((class_342)IndivisMenus.get(s, "nexuscharacters$lastName")).method_1852("Pendragon");
                    IndivisMenus.set(s, "heightScale", RACES[scene < 15 ? scene % 5 : 1].maxHeight());
                    IndivisMenus.set(s, "buildScale", RACES[scene < 15 ? scene % 5 : 1].maxBuild());
                    IndivisMenus.invoke(IndivisMenus.get(s, "heightSlider"), "sync");
                    IndivisMenus.invoke(IndivisMenus.get(s, "buildSlider"), "sync");
                    for (String prefix : new String[]{"first", "last"}) {
                        int desired = prefix.equals("first") ? 0 : 15;
                        for (int i = 0; i < 16 && ((Number)IndivisMenus.get(s, "nexuscharacters$" + prefix + "Color")).intValue() != desired; i++) {
                            IndivisMenus.layoutCreation(s); click(s, (class_339)IndivisMenus.get(s, "nexuscharacters$" + prefix + "ColorButton"));
                        }
                    }
                    IndivisMenus.invoke(s, "nexuscharacters$changed");
                    IndivisMenus.layoutCreation(s);
                    IndivisMenus.yaw(scene >= 20 ? (scene - 20) * 90f : 0f);
                } else {
                    class_437 s = new CharacterSelectionScreen(null, () -> {}); c.method_1507(s);
                    IndivisMenus.set(s, "previewCharacter", dto(RACES[scene - 15]));
                    IndivisMenus.yaw(90f);
                }
            }
            if (phase == 4 && scene >= 20) IndivisMenus.yaw((scene - 20) * 90f);
            if (phase == 12) {
                class_437 s = c.field_1755;
                IndivisMenus.State state = IndivisMenus.state(s);
                Field f = CharacterPreviewRenderer.class.getDeclaredField("skinWidget"); f.setAccessible(true);
                class_339 widget = (class_339)f.get(null);
                if (!WORLD) {
                    check(widget != null, "No rendered avatar");
                    check(widget.method_25368() == state.pw, "Avatar width clipped: " + widget.method_25368() + " expected=" + state.pw);
                    check(widget.method_46426() == state.px - state.pw / 2, "Avatar center changed");
                    check(widget.method_25364() <= state.pb - state.pt, "Avatar too tall");
                    check(widget.method_46427() >= state.pt, "Avatar exceeds preview top");
                    check(Math.abs(((Number)IndivisMenus.get(widget, "field_46006")).floatValue() - (30 + PreviewDragInput.getYawDegrees())) < .01, "Initial body/cosmetic yaw mismatch before any drag");
                    if (ROTATION) check(Math.abs(((Number)IndivisMenus.get(widget, "field_46006")).floatValue() - (30 + (scene - 20) * 90)) < .01, "Rendered widget yaw was not updated");
                } else check(c.field_1724 != null && c.field_1687 != null, "World disappeared");
                CharacterRace r = RACES[scene < 15 ? scene % 5 : scene < 20 ? scene - 15 : 1];
                check(IndivisReadability.currentRace() == r, "Banner race mismatch");
                check(c.method_1478().method_14486(IndivisReadability.banner(r)).isPresent(), "Banner missing " + r);
                System.out.println("PRE28_SCENE " + scene + " race=" + r + " viewport=" + state.pw + " world=" + WORLD + " banner=" + IndivisReadability.banner(r));
                final int index = scene;
                class_318.method_1663(c.method_1522(), im -> { try {
                    Path target=OUT.resolve(String.format("menu-%02d.png", index)), temporary=OUT.resolve(String.format("menu-%02d-writing.png",index));
                    im.method_4314(temporary);Files.move(temporary,target,StandardCopyOption.REPLACE_EXISTING,StandardCopyOption.ATOMIC_MOVE);captures++;
                } catch (Exception e) { throw new RuntimeException(e); } finally { im.close(); }});
            }
            if (phase == 16 && scene < 15) {
                class_437 s = c.field_1755;
                IndivisMenus.layoutCreation(s);
                for (String name : new String[]{"heightSlider", "buildSlider", "nexuscharacters$eyeHeight"}) {
                    class_339 w = (class_339)IndivisMenus.get(s, name);
                    if (w.method_46427() < 0) {
                        IndivisMenus.State state = IndivisMenus.state(s);
                        state.scroll = name.equals("nexuscharacters$eyeHeight") ? Math.min(state.maxScroll, 200) : state.maxScroll;
                        IndivisMenus.layoutCreation(s);
                    }
                    double fraction = ((Number)IndivisMenus.get(w, "field_22753")).doubleValue();
                    double target = fraction > .5 ? .25 : .75;
                    check(w.field_22764 && w.method_46427() >= 0, "Slider hidden " + name);
                    s.method_25402(mouse(w.method_46426() + 4 + target * (w.method_25368() - 8), w.method_46427() + 15), false);
                    s.method_25406(mouse(w.method_46426() + 4 + target * (w.method_25368() - 8), w.method_46427() + 15));
                    double after = ((Number)IndivisMenus.get(w, "field_22753")).doubleValue();
                    check(Math.abs(after - fraction) > .1, "Slider click did not change value " + name);
                }
            }
            tick++;
        } catch (Throwable e) { System.err.println("PRE30_MENU_FAILURE " + e); e.printStackTrace(); c.method_1490(); }});
    }
}

