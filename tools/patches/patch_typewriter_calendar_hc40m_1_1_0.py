#!/usr/bin/env python3
"""Deterministic HC40M patch for the exact Arthur Typewriter Calendar base JAR."""
from __future__ import annotations
import hashlib, json, sys, tempfile, zipfile
from pathlib import Path

EXPECTED_INPUT_SHA256 = "e700fdcae5a9d683aa2fba1acc587eb2b1dfd1a4d9bc1b7c17b67a5a5b8bdcf7"
VERSION = "1.1.0-hc40m"
ZIP_DATE = (2026, 9, 30, 0, 0, 0)
MONTHS = {
    1:"GIVRE", 2:"DÉGEL", 3:"SEMAILLES", 4:"AVERSES", 5:"FRONDAISONS", 6:"LONGS-JOURS",
    7:"BRAISES", 8:"HAUTES LUMIÈRES", 9:"RÉCOLTES", 10:"CUIVRES", 11:"BRUMES", 12:"RENOUVEAU",
}

def sha256(path: Path) -> str:
    return hashlib.sha256(path.read_bytes()).hexdigest()

def display_lines(rng: str, annual: bool, include_day: bool) -> str:
    out=[]
    for n,name in MONTHS.items():
        if annual:
            parts='[{"text":"——","color":"yellow","bold":true},{"text":" ANNÉE "},{"score":{"name":"year","objective":"refresh_daycounter"}},{"text":" / '+name+'"}'
            tail=' ——'
        else:
            parts='[{"text":"— ANNÉE "},{"score":{"name":"year","objective":"refresh_daycounter"}},{"text":" / '+name+'"}'
            tail=' —'
        if include_day:
            parts+=',{"text":" / JOUR "},{"score":{"name":"day_of_month","objective":"refresh_daycounter"}}'
        parts+=',{"text":"'+tail+'"}]'
        out.append(f'execute if score @s refresh_daycounter2 matches {rng} if score month refresh_daycounter matches {n} run title @s actionbar {parts}')
    return "\n".join(out)

def patch_tree(root: Path) -> None:
    for p in (
        root/"data/vanilla_refresh/functions/day_counter/root.mcfunction",
        root/"1.21-above/data/vanilla_refresh/function/day_counter/root.mcfunction",
    ):
        s=p.read_text(encoding="utf-8")
        if "matches 1..80" not in s:
            raise RuntimeError(f"unexpected dawn trigger in {p}")
        p.write_text(
            "# HC40M compatibility: timeOfDay runs at 0.5x; 1..40 keeps the original ~4 s dawn detection window.\n"
            + s.replace("matches 1..80","matches 1..40"), encoding="utf-8")

    note=(
        "# Calendrier impérial Haute Capitale — noms canoniques :\n"
        "# 1 Givre, 2 Dégel, 3 Semailles, 4 Averses, 5 Frondaisons, 6 Longs-Jours,\n"
        "# 7 Braises, 8 Hautes Lumières, 9 Récoltes, 10 Cuivres, 11 Brumes, 12 Renouveau.\n"
        "# Le score numérique month est conservé uniquement comme index interne.\n"
    )
    for p in (
        root/"data/vanilla_refresh/functions/day_counter/date.mcfunction",
        root/"1.21-above/data/vanilla_refresh/function/day_counter/date.mcfunction",
    ):
        s=p.read_text(encoding="utf-8")
        p.write_text(s.replace("# Arthur — conversion du compteur de jours en date lisible\n",
                               "# Arthur — conversion du compteur de jours en date lisible\n"+note),encoding="utf-8")

    d1='execute if score @s refresh_daycounter2 matches 125..159 run title @s actionbar [{"text": "— ANNÉE "},{"score":{"name":"year","objective":"refresh_daycounter"}},{"text": " / MOIS "},{"score":{"name":"month","objective":"refresh_daycounter"}},{"text": " —"}]'
    d2='execute if score @s refresh_daycounter2 matches 160..235 run title @s actionbar [{"text": "— ANNÉE "},{"score":{"name":"year","objective":"refresh_daycounter"}},{"text": " / MOIS "},{"score":{"name":"month","objective":"refresh_daycounter"}},{"text": " / JOUR "},{"score":{"name":"day_of_month","objective":"refresh_daycounter"}},{"text": " —"}]'
    for p in (
        root/"data/vanilla_refresh/functions/day_counter/animation.mcfunction",
        root/"1.21-above/data/vanilla_refresh/function/day_counter/animation.mcfunction",
    ):
        s=p.read_text(encoding="utf-8")
        if d1 not in s or d2 not in s: raise RuntimeError(f"unexpected daily animation in {p}")
        s=s.replace(d1,"# Nom impérial du mois (au lieu du numéro).\n"+display_lines("125..159",False,False))
        s=s.replace(d2,display_lines("160..235",False,True))
        s=s.replace("# Arthur — affichage quotidien au format Année / Mois / Jour",
                    "# Arthur — affichage quotidien au format Année / Nom du mois / Jour")
        p.write_text(s,encoding="utf-8")

    a1='execute if score @s refresh_daycounter2 matches 1150..1185 run title @s actionbar [{"text": "——","color": "yellow","bold": true},{"text": " ANNÉE "},{"score":{"name":"year","objective":"refresh_daycounter"}},{"text": " / MOIS "},{"score":{"name":"month","objective":"refresh_daycounter"}},{"text": " ——"}]'
    a2='execute if score @s refresh_daycounter2 matches 1190..1260 run title @s actionbar [{"text": "——","color": "yellow","bold": true},{"text": " ANNÉE "},{"score":{"name":"year","objective":"refresh_daycounter"}},{"text": " / MOIS "},{"score":{"name":"month","objective":"refresh_daycounter"}},{"text": " / JOUR "},{"score":{"name":"day_of_month","objective":"refresh_daycounter"}},{"text": " ——"}]'
    for p in (
        root/"data/vanilla_refresh/functions/day_counter/animation2.mcfunction",
        root/"1.21-above/data/vanilla_refresh/function/day_counter/animation2.mcfunction",
    ):
        s=p.read_text(encoding="utf-8")
        if a1 not in s or a2 not in s: raise RuntimeError(f"unexpected annual animation in {p}")
        s=s.replace(a1,"# Nom impérial du mois (au lieu du numéro).\n"+display_lines("1150..1185",True,False))
        s=s.replace(a2,display_lines("1190..1260",True,True))
        p.write_text(s,encoding="utf-8")

    fabric=root/"fabric.mod.json"
    f=json.loads(fabric.read_text(encoding="utf-8"))
    f["version"]=VERSION
    f["name"]="Typewriter Calendar — Haute Capitale"
    f["description"]="Calendrier Haute Capitale 365 jours, mois impériaux nommés, compatible cycle jour/nuit 40 min."
    fabric.write_text(json.dumps(f,ensure_ascii=False,indent=4)+"\n",encoding="utf-8")

    quilt=root/"quilt.mod.json"
    q=json.loads(quilt.read_text(encoding="utf-8"))
    q["quilt_loader"]["version"]=VERSION
    q["quilt_loader"]["metadata"]["name"]="Typewriter Calendar — Haute Capitale"
    q["quilt_loader"]["metadata"]["description"]="Calendrier Haute Capitale 365 jours, mois impériaux nommés, compatible cycle jour/nuit 40 min."
    quilt.write_text(json.dumps(q,ensure_ascii=False,separators=(",",":"))+"\n",encoding="utf-8")

    mods=root/"META-INF/mods.toml"
    s=mods.read_text(encoding="utf-8")
    s=s.replace("version = '1.0'","version = '1.1.0-hc40m'")
    s=s.replace("displayName = 'Typewriter Daycounter'","displayName = 'Typewriter Calendar - Haute Capitale'")
    s=s.replace("description = 'Adds a daycounter, displaying at the start of each day'",
                "description = 'Haute Capitale 365-day calendar with named imperial months and 40-minute day-cycle compatibility'")
    mods.write_text(s,encoding="utf-8")

    pack=root/"pack.mcmeta"
    s=pack.read_text(encoding="utf-8")
    pack.write_text(s.replace('"translate": "Adds a daycounter, displaying at the start of each day"',
                              '"text": "Haute Capitale — calendrier impérial 365 j / cycle 40 min"'),encoding="utf-8")

    meta=root/"__arthur_meta"; meta.mkdir(exist_ok=True)
    (meta/"changelog_calendar_hc40m_2026-09-30.txt").write_text(
        "Haute Capitale — Typewriter Calendar 1.1.0-hc40m\nDate : 2026-09-30\n\n"
        "Compatibilité : date fondée sur `time query day`; fenêtre aube 1..80 -> 1..40 pour timeOfDay 0.5x.\n"
        "Aucun tickrate, randomTickSpeed, timer météo, cooldown ou autre système n'est modifié.\n\n"
        "Mois : Givre, Dégel, Semailles, Averses, Frondaisons, Longs-Jours, Braises, Hautes Lumières, Récoltes, Cuivres, Brumes, Renouveau.\n"
        "Le numéro de mois reste interne et n'est plus affiché.\n\n"
        "Biomes : aucune référence de biome dans ce JAR; aucune migration d'ID nécessaire ici.\n",
        encoding="utf-8")

def build(inp: Path,out: Path) -> None:
    if sha256(inp)!=EXPECTED_INPUT_SHA256:
        raise RuntimeError(f"unexpected input SHA-256: {sha256(inp)}")
    with tempfile.TemporaryDirectory() as td:
        root=Path(td)
        with zipfile.ZipFile(inp) as z: z.extractall(root)
        patch_tree(root)
        with zipfile.ZipFile(out,"w",compression=zipfile.ZIP_DEFLATED,compresslevel=9) as z:
            for p in sorted(root.rglob("*")):
                if not p.is_file(): continue
                info=zipfile.ZipInfo(p.relative_to(root).as_posix(),ZIP_DATE)
                info.compress_type=zipfile.ZIP_DEFLATED
                info.external_attr=(0o100644 & 0xFFFF)<<16
                z.writestr(info,p.read_bytes(),compress_type=zipfile.ZIP_DEFLATED,compresslevel=9)
    validate(out)

def validate(out: Path) -> None:
    with zipfile.ZipFile(out) as z:
        assert z.testzip() is None
        for n in z.namelist():
            if n.endswith(".json") or n=="pack.mcmeta": json.loads(z.read(n).decode("utf-8"))
        r=z.read("1.21-above/data/vanilla_refresh/function/day_counter/root.mcfunction").decode("utf-8")
        assert "matches 1..40" in r and "matches 1..80" not in r
        for n in ("animation.mcfunction","animation2.mcfunction"):
            s=z.read("1.21-above/data/vanilla_refresh/function/day_counter/"+n).decode("utf-8")
            assert all(m in s for m in MONTHS.values()) and "/ MOIS " not in s
            for line in s.splitlines():
                if " run title " in line and " actionbar " in line:
                    json.loads(line.split(" actionbar ",1)[1])

if __name__=="__main__":
    if len(sys.argv)!=3: raise SystemExit(f"usage: {sys.argv[0]} <base.jar> <output.jar>")
    build(Path(sys.argv[1]),Path(sys.argv[2]))
    print(sha256(Path(sys.argv[2])))
