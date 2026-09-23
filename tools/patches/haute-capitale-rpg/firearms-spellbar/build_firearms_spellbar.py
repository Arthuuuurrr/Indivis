#!/usr/bin/env python3
from __future__ import annotations

import argparse
import hashlib
import json
import shutil
import subprocess
import tempfile
import zipfile
from pathlib import Path

EXPECTED = {
    "rpg": "65824c4c54c96825a3ffdc2e8c441c690d9f4a466622efd62b33ff8c6d7548e4",
    "gun": "0979a7c94e1e959f12b2f8474aee86fc760d3b19a38507c4169b92a79d36e800",
    "capskills": "109ec0428909c009d6599637cc0b23a90c78bd84c9d5772c6447ecf01a3d34f4",
}
EXPECTED_OUTPUTS = {
    "rpg": "08f03d4db0c41286851cbd35318b91afcb0e59483a5ce2d7164aa59ebf047e40",
    "capskills": "c9b330330bd3cf3f5df5c8c6749acba0d02d18902fbc4e20524b0f9caa6f6ebc",
}
RPG_OUT = "haute-capitale-rpg-0.3.0+1.21.11.b2.HC.TEST3.RC3-FIREARMS-SPELLBAR.jar"
DP_OUT = "capitale_skills_BETA_0_10_20_FIREARMS_SPELLBAR_BRIDGE.zip"

SKILLS = {
    "repli": ("firearms_skirmish", ["active", "mobility", "ranged"], 222, "pistolet_silex"),
    "tir_incapacitant": ("firearms_skirmish", ["active", "ranged", "status_effect"], 223, "balle"),
    "marque": ("firearms_mark", ["active", "ranged", "targeted", "status_effect"], 224, "longue_vue"),
    "tir_perforant": ("firearms_piercing", ["active", "ranged", "projectile"], 225, "balle"),
    "tir_explosif": ("firearms_explosive", ["active", "ranged", "aoe", "projectile"], 226, "balle"),
    "rafale": ("firearms_repeater", ["active", "ranged", "projectile"], 227, "fusil_rouages"),
}
STATUS = "§eStatut :§r intégré à la barre Spell Engine via le bridge FusilAPI."

LANG_FR = {
    "repli": ("Repli", "Projette le tireur en arrière et recharge instantanément 1 munition."),
    "tir_incapacitant": ("Tir incapacitant", "Charge une balle de choc qui augmente fortement le recul et étourdit brièvement la cible."),
    "marque": ("Marque du chasseur", "Marque la cible dans la ligne de mire afin d’augmenter les dégâts de vos tirs contre elle."),
    "tir_perforant": ("Tir perforant", "Charge une balle perforante qui ignore une partie de l’armure et traverse une cible supplémentaire."),
    "tir_explosif": ("Tir explosif", "Charge une balle explosive qui détonne à l’impact."),
    "rafale": ("Rafale", "Déclenche une rafale de trois tirs avec le fusil à rouages."),
}
LANG_EN = {
    "repli": ("Retreat", "Pushes the shooter backward and instantly reloads one round."),
    "tir_incapacitant": ("Stunning Shot", "Loads a shock round with heavy knockback and a brief stun."),
    "marque": ("Hunter's Mark", "Marks the target in your line of sight, increasing your firearm damage against it."),
    "tir_perforant": ("Piercing Shot", "Loads an armor-piercing round that can penetrate an additional target."),
    "tir_explosif": ("Explosive Shot", "Loads an explosive round that detonates on impact."),
    "rafale": ("Burst Fire", "Fires a three-shot burst with the clockwork rifle."),
}


def sha256(path: Path) -> str:
    h = hashlib.sha256()
    with path.open("rb") as f:
        for chunk in iter(lambda: f.read(1024 * 1024), b""):
            h.update(chunk)
    return h.hexdigest()


def require_hash(path: Path, key: str) -> None:
    actual = sha256(path)
    if actual != EXPECTED[key]:
        raise SystemExit(f"{key} SHA-256 mismatch: {actual}")


def json_bytes(value) -> bytes:
    return (json.dumps(value, ensure_ascii=False, indent=2) + "\n").encode("utf-8")


def added_info(name: str, timestamp: tuple[int, int, int, int, int, int]) -> zipfile.ZipInfo:
    info = zipfile.ZipInfo(name, timestamp)
    info.compress_type = zipfile.ZIP_DEFLATED
    info.create_system = 3
    info.external_attr = 0o600 << 16
    return info


def compile_bridge(source: Path, work: Path) -> Path:
    stubs = {
        "net/fabricmc/api/ModInitializer.java":
            "package net.fabricmc.api; public interface ModInitializer { void onInitialize(); }",
        "net/minecraft/class_243.java":
            "package net.minecraft; public class class_243 {}",
        "net/minecraft/class_1309.java":
            "package net.minecraft; public class class_1309 {}",
        "net/minecraft/class_1937.java":
            "package net.minecraft; public class class_1937 {}",
        "net/minecraft/class_6880.java":
            "package net.minecraft; public interface class_6880<T> {}",
        "net/spell_engine/api/spell/Spell.java":
            "package net.spell_engine.api.spell; public class Spell {}",
        "net/spell_engine/internals/SpellExecution.java":
            "package net.spell_engine.internals; public class SpellExecution { public static class DeliveryTarget {} public static class ImpactContext {} }",
        "net/spell_engine/api/spell/event/SpellHandlers.java":
            "package net.spell_engine.api.spell.event; import java.util.*; import net.minecraft.*; import net.spell_engine.api.spell.Spell; import net.spell_engine.internals.SpellExecution; public class SpellHandlers { public static final Map<String, CustomDelivery> customDelivery=new HashMap<>(); public interface CustomDelivery { boolean onSpellDelivery(class_1937 world,class_6880<Spell> spellEntry,class_1309 caster,List<SpellExecution.DeliveryTarget> targets,SpellExecution.ImpactContext context,class_243 targetLocation); } }",
        "net/hautecapitale/fusils/api/FusilAPI.java":
            "package net.hautecapitale.fusils.api; import java.util.Optional; import net.minecraft.class_1309; public final class FusilAPI { public static Optional<String> useSkill(class_1309 entity,String skillId,int level){return Optional.empty();} }",
    }
    src = work / "src"
    classes = work / "classes"
    classes.mkdir(parents=True)
    for relative, content in stubs.items():
        target = src / relative
        target.parent.mkdir(parents=True, exist_ok=True)
        target.write_text(content, encoding="utf-8")

    bridge = src / "net/hautecapitale/rpg/ability/FirearmSpellBridge.java"
    bridge.parent.mkdir(parents=True, exist_ok=True)
    shutil.copy2(source, bridge)

    java_files = [str(path) for path in src.rglob("*.java")]
    subprocess.run(
        ["javac", "--release", "21", "-encoding", "UTF-8", "-d", str(classes), *java_files],
        check=True,
    )
    return classes


def build_rpg(base_rpg: Path, gun: Path, source: Path, output: Path) -> None:
    with tempfile.TemporaryDirectory() as temp:
        classes = compile_bridge(source, Path(temp))
        with zipfile.ZipFile(base_rpg) as zin, zipfile.ZipFile(gun) as guns, zipfile.ZipFile(output, "w") as zout:
            for info in zin.infolist():
                data = zin.read(info.filename)
                if info.filename == "fabric.mod.json":
                    manifest = json.loads(data)
                    manifest["version"] = "0.3.0+1.21.11.b2.HC.TEST3.RC3-FIREARMS-SPELLBAR"
                    manifest["description"] = "Noyau RPG Haute Capitale TEST3 RC3 : RC2 + bridge Spell Engine vers les compétences natives du mod Fusils."
                    entrypoint = "net.hautecapitale.rpg.ability.FirearmSpellBridge"
                    manifest.setdefault("entrypoints", {}).setdefault("main", [])
                    if entrypoint not in manifest["entrypoints"]["main"]:
                        manifest["entrypoints"]["main"].append(entrypoint)
                    data = json_bytes(manifest)
                elif info.filename in (
                    "assets/haute_capitale_rpg/lang/fr_fr.json",
                    "assets/haute_capitale_rpg/lang/en_us.json",
                ):
                    language = json.loads(data)
                    additions = LANG_FR if info.filename.endswith("fr_fr.json") else LANG_EN
                    for skill, (name, description) in additions.items():
                        language[f"spell.haute_capitale_rpg.firearm_{skill}.name"] = name
                        language[f"spell.haute_capitale_rpg.firearm_{skill}.description"] = description
                    data = json_bytes(language)
                zout.writestr(info, data)

            timestamp = (2026, 9, 23, 13, 8, 46)
            for class_name in ("FirearmSpellBridge$Handler.class", "FirearmSpellBridge.class"):
                data = (classes / "net/hautecapitale/rpg/ability" / class_name).read_bytes()
                zout.writestr(
                    added_info("net/hautecapitale/rpg/ability/" + class_name, timestamp),
                    data,
                )

            for skill, (_, _, _, icon) in SKILLS.items():
                icon_data = guns.read(f"assets/haute_capitale_fusils/textures/item/{icon}.png")
                zout.writestr(
                    added_info(f"assets/haute_capitale_rpg/textures/spell/firearm_{skill}.png", timestamp),
                    icon_data,
                )


def wrapper_spell(skill: str) -> dict:
    return {
        "school": "spell_power:physical_ranged",
        "secondary_archetype": "ANY",
        "range": 0.0,
        "tier": 1,
        "learn": {},
        "active": {},
        "target": {"type": "NONE"},
        "deliver": {
            "type": "CUSTOM",
            "custom": {"handler": f"haute_capitale_rpg:firearm_{skill}"},
        },
        "cost": {"durability": 0},
    }


def ability_definition(skill: str) -> dict:
    weapon_tag, roles, order, _ = SKILLS[skill]
    categories = [
        "capitale:branch/trait",
        "capitale:class/fusilier",
        "capitale:source/haute_capitale_fusils",
        "capitale:type/active",
        "capitale:school/physical_ranged",
        *[f"capitale:role/{role}" for role in roles],
        f"capitale:order/{order:06d}",
    ]
    return {
        "schema_version": 1,
        "spell": f"haute_capitale_rpg:firearm_{skill}",
        "ability_categories": categories,
        "weapon_requirements": {
            "allowed_tags": [f"capitale:weapon/{weapon_tag}"],
            "allowed_items": [],
            "hands": "main_hand",
        },
        "requires_skill": True,
    }


def integrated_description(description: str) -> str:
    return "\n".join(
        STATUS if line.startswith("§eStatut :§r") else line
        for line in description.split("\n")
    )


def build_capskills(base: Path, output: Path) -> None:
    definitions_path = "data/capskills/puffish_skills/categories/doctrine/definitions.json"
    plan_path = "data/capitale/integration/fusils_capskills_plan.json"

    with zipfile.ZipFile(base) as zin, zipfile.ZipFile(output, "w") as zout:
        for info in zin.infolist():
            data = zin.read(info.filename)
            if info.filename == "pack.mcmeta":
                data = json_bytes({
                    "pack": {
                        "min_format": 94,
                        "max_format": 94,
                        "description": "Haute Capitale CapSkills 0.10.20 — RC2F tree + firearm Spell Engine bridge",
                    }
                })
            elif info.filename == definitions_path:
                definitions = json.loads(data)
                for skill in SKILLS:
                    node = f"fusils_{skill}"
                    definitions[node]["description"] = integrated_description(definitions[node]["description"])
                    reward = {
                        "type": "haute_capitale_rpg:abilities",
                        "data": {
                            "abilities": [f"capitale:fusilier/haute_capitale_fusils/{skill}"]
                        },
                    }
                    if reward not in definitions[node].get("rewards", []):
                        definitions[node].setdefault("rewards", []).append(reward)
                data = json_bytes(definitions)
            elif info.filename == plan_path:
                plan = json.loads(data)
                plan["status"] = "integrated_spellengine_hotbar_bridge"
                plan["notes"] = [
                    "Firearm skills are displayed/cast through Spell Engine but native gunplay remains owned by FusilAPI.",
                    "Ability weapon_requirements validate the equipped firearm family before custom delivery.",
                    "CapSkills player tags remain the persistent unlock markers; haute_capitale_rpg ability rewards mirror them into the Spell Engine container.",
                    "No gameplay cooldown values are invented in 0.10.20; wrapper spells intentionally define no Spell Engine cooldown yet.",
                    "Mark targeting is still native HunterSkills targeting (48 blocks in the audited gun build).",
                ]
                plan["bridge"] = {
                    "owner": "haute_capitale_rpg",
                    "version": "TEST3 RC3 FIREARMS SPELLBAR",
                    "delivery": "SpellHandlers.customDelivery",
                    "spell_namespace": "haute_capitale_rpg",
                    "delegate": "FusilAPI.useSkill(entity, skillId, 0)",
                    "projectile_casting": False,
                }
                data = json_bytes(plan)
            zout.writestr(info, data)

        timestamp = (2026, 9, 23, 13, 9, 26)
        for skill in SKILLS:
            zout.writestr(
                added_info(f"data/haute_capitale_rpg/spell/firearm_{skill}.json", timestamp),
                json_bytes(wrapper_spell(skill)),
            )
            zout.writestr(
                added_info(
                    f"data/capitale/capitale_abilities/fusilier/haute_capitale_fusils/{skill}.json",
                    timestamp,
                ),
                json_bytes(ability_definition(skill)),
            )


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--rpg-rc2", type=Path, required=True)
    parser.add_argument("--gun-b3", type=Path, required=True)
    parser.add_argument("--capskills-rc2f", type=Path, required=True)
    parser.add_argument(
        "--source",
        type=Path,
        default=Path(__file__).with_name("FirearmSpellBridge.java"),
    )
    parser.add_argument("--out-dir", type=Path, required=True)
    args = parser.parse_args()

    require_hash(args.rpg_rc2, "rpg")
    require_hash(args.gun_b3, "gun")
    require_hash(args.capskills_rc2f, "capskills")
    args.out_dir.mkdir(parents=True, exist_ok=True)

    rpg_output = args.out_dir / RPG_OUT
    capskills_output = args.out_dir / DP_OUT
    build_rpg(args.rpg_rc2, args.gun_b3, args.source, rpg_output)
    build_capskills(args.capskills_rc2f, capskills_output)

    results = {
        "rpg": sha256(rpg_output),
        "capskills": sha256(capskills_output),
    }
    for key, actual in results.items():
        if actual != EXPECTED_OUTPUTS[key]:
            raise SystemExit(
                f"non-reproducible {key} output: {actual} != {EXPECTED_OUTPUTS[key]}"
            )

    print(f"{RPG_OUT}  {results['rpg']}")
    print(f"{DP_OUT}  {results['capskills']}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
