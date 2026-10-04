#!/usr/bin/env python3
from __future__ import annotations
import argparse, hashlib, json, shutil, subprocess, tempfile, zipfile
from pathlib import Path

EXPECTED = {
    "gun": "0979a7c94e1e959f12b2f8474aee86fc760d3b19a38507c4169b92a79d36e800",
    "capskills": "c9b330330bd3cf3f5df5c8c6749acba0d02d18902fbc4e20524b0f9caa6f6ebc",
}
EXPECTED_OUTPUTS = {
    "gun": "eff4d5451ebe7d660fbc2acb640c48b1a2028591d934610c66e5c353b28db94c",
    "capskills": "dde382493a8598ec0f2bfcf13d0b05798b18c62da0761659a3595dcb4b83b6f6",
}

def sha256(path: Path) -> str:
    return hashlib.sha256(path.read_bytes()).hexdigest()

def json_bytes(value) -> bytes:
    return (json.dumps(value, ensure_ascii=False, indent=2) + "\n").encode("utf-8")

def added_info(name: str) -> zipfile.ZipInfo:
    info = zipfile.ZipInfo(name, (2026, 9, 23, 17, 20, 0))
    info.compress_type = zipfile.ZIP_DEFLATED
    info.create_system = 3
    info.external_attr = 0o644 << 16
    return info

def compile_helper(source: Path, work: Path) -> Path:
    src = work / "src"
    classes = work / "classes"
    classes.mkdir(parents=True)
    stubs = {
        "net/minecraft/class_1937.java":
            "package net.minecraft; public class class_1937 { public boolean method_8608(){return false;} }",
        "net/minecraft/class_1309.java":
            "package net.minecraft; import java.util.*; public class class_1309 { public class_1937 method_73183(){return null;} public Set<String> method_5752(){return new HashSet<>();} }",
        "net/minecraft/class_1657.java":
            "package net.minecraft; public class class_1657 extends class_1309 {}",
    }
    for relative, content in stubs.items():
        target = src / relative
        target.parent.mkdir(parents=True, exist_ok=True)
        target.write_text(content, encoding="utf-8")
    helper = src / "net/hautecapitale/fusils/skills/CapSkillsFusilAuthority.java"
    helper.parent.mkdir(parents=True, exist_ok=True)
    shutil.copy2(source, helper)
    subprocess.run(["javac", "--release", "21", "-d", str(classes),
                    *[str(p) for p in src.rglob("*.java")]], check=True)
    return classes / "net/hautecapitale/fusils/skills/CapSkillsFusilAuthority.class"

def compile_patcher(source: Path, work: Path) -> Path:
    classes = work / "patchclasses"
    classes.mkdir()
    subprocess.run([
        "javac",
        "--add-exports", "java.base/jdk.internal.org.objectweb.asm=ALL-UNNAMED",
        "-d", str(classes), str(source)
    ], check=True)
    return classes

def build_gun(base: Path, helper_source: Path, patch_source: Path, output: Path) -> None:
    with tempfile.TemporaryDirectory() as td:
        work = Path(td)
        helper = compile_helper(helper_source, work)
        patchclasses = compile_patcher(patch_source, work)
        with zipfile.ZipFile(base) as zin:
            gunplay = work / "GunplayManager.class"
            hunter = work / "HunterSkills.class"
            gunplay.write_bytes(zin.read("net/hautecapitale/fusils/gun/GunplayManager.class"))
            hunter.write_bytes(zin.read("net/hautecapitale/fusils/skills/HunterSkills.class"))
        gunplay_out = work / "GunplayManager.patched.class"
        hunter_out = work / "HunterSkills.patched.class"
        subprocess.run([
            "java",
            "--add-exports", "java.base/jdk.internal.org.objectweb.asm=ALL-UNNAMED",
            "-cp", str(patchclasses), "PatchFirearmAuthority",
            str(gunplay), str(gunplay_out), str(hunter), str(hunter_out)
        ], check=True)

        with zipfile.ZipFile(base) as zin, zipfile.ZipFile(output, "w") as zout:
            for info in zin.infolist():
                data = zin.read(info.filename)
                if info.filename == "fabric.mod.json":
                    manifest = json.loads(data)
                    manifest["version"] = "0.1.0+1.21.11.b4-CAPSKILLS-AUTHORITY"
                    manifest["description"] = (
                        "Armes à poudre noire et d'artificier pour la branche Fusil du Chasseur : "
                        "pistolet à silex, mousquet, arquebuse, tromblon, fusil à rouages, canon à main. "
                        "Statistiques, munitions spéciales et modifications pilotées par données. "
                        "Système original écrit pour le serveur Haute Capitale (Fabric 1.21.11) ; "
                        "aucun asset ni code d'Iron's Arms 'n Artifice n'est réutilisé. "
                        "B4 : autorité serveur CapSkills sur le tir et les compétences Fusilier."
                    )
                    data = json_bytes(manifest)
                elif info.filename == "net/hautecapitale/fusils/gun/GunplayManager.class":
                    data = gunplay_out.read_bytes()
                elif info.filename == "net/hautecapitale/fusils/skills/HunterSkills.class":
                    data = hunter_out.read_bytes()
                zout.writestr(info, data)
            zout.writestr(
                added_info("net/hautecapitale/fusils/skills/CapSkillsFusilAuthority.class"),
                helper.read_bytes(),
            )

def build_capskills(base: Path, output: Path) -> None:
    definitions_path = "data/capskills/puffish_skills/categories/doctrine/definitions.json"
    plan_path = "data/capitale/integration/fusils_capskills_plan.json"
    with zipfile.ZipFile(base) as zin, zipfile.ZipFile(output, "w") as zout:
        for info in zin.infolist():
            data = zin.read(info.filename)
            if info.filename == "pack.mcmeta":
                data = json_bytes({"pack": {
                    "min_format": 94, "max_format": 94,
                    "description": "Haute Capitale CapSkills 0.10.21 — firearm Spellbar + server authority"
                }})
            elif info.filename == definitions_path:
                definitions = json.loads(data)
                node = definitions["classe_fusilier"]
                node["description"] = (
                    "§aEMBRANCHEMENT GRATUIT — 0 POINT§r\n"
                    "§7Débloque l’usage serveur des armes à feu de Haute Capitale et donne accès aux spécialisations Fusilier.§r\n"
                    "§8----------------§r\n"
                    "§7Le serveur vérifie ce déblocage au moment de chaque tir ; un reset retire immédiatement l’autorisation effective.§r\n"
                    "§bArmes :§r pistolet à silex, mousquet, arquebuse, tromblon, fusil à rouages et canon à main."
                )
                node["rewards"] = [{"type": "puffish_skills:command", "data": {
                    "unlock_command": "tag @s add capskills.fusils.use",
                    "lock_command": "tag @s remove capskills.fusils.use"
                }}]
                data = json_bytes(definitions)
            elif info.filename == plan_path:
                plan = json.loads(data)
                plan["server_authority"] = {
                    "version": "haute_capitale_fusils b4 CAPSKILLS AUTHORITY",
                    "base_tag": "capskills.fusils.use",
                    "skill_tags": "capskills.fusils.<skillId>",
                    "normal_fire": "server requires base tag for players",
                    "native_skills": "server requires base + specific skill tag for players",
                    "non_players": "preserved",
                    "client_prediction": "not hard-blocked; server stays authoritative",
                }
                data = json_bytes(plan)
            zout.writestr(info, data)

def main() -> int:
    ap = argparse.ArgumentParser()
    ap.add_argument("--gun-b3", type=Path, required=True)
    ap.add_argument("--capskills-0-10-20", type=Path, required=True)
    ap.add_argument("--out-dir", type=Path, required=True)
    args = ap.parse_args()
    if sha256(args.gun_b3) != EXPECTED["gun"]:
        raise SystemExit("unexpected gun b3 SHA-256")
    if sha256(args.capskills_0_10_20) != EXPECTED["capskills"]:
        raise SystemExit("unexpected CapSkills 0.10.20 SHA-256")

    here = Path(__file__).parent
    args.out_dir.mkdir(parents=True, exist_ok=True)
    gun_out = args.out_dir / "haute_capitale_fusils-0.1.0+1.21.11.b4-CAPSKILLS-AUTHORITY.jar"
    dp_out = args.out_dir / "capitale_skills_BETA_0_10_21_FIREARMS_SERVER_AUTHORITY.zip"
    build_gun(args.gun_b3, here / "CapSkillsFusilAuthority.java",
              here / "PatchFirearmAuthority.java", gun_out)
    build_capskills(args.capskills_0_10_20, dp_out)
    actual = {"gun": sha256(gun_out), "capskills": sha256(dp_out)}
    if actual != EXPECTED_OUTPUTS:
        raise SystemExit(f"non-reproducible output: {actual}")
    print(actual)
    return 0

if __name__ == "__main__":
    raise SystemExit(main())
