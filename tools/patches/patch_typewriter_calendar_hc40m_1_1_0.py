#!/usr/bin/env python3
"""Rebuild the Haute Capitale Typewriter Calendar HC40M variant from the exact 2026-05-14 Arthur base JAR."""
from __future__ import annotations
import hashlib, json, sys, tempfile, zipfile
from pathlib import Path

EXPECTED_INPUT_SHA256 = "e700fdcae5a9d683aa2fba1acc587eb2b1dfd1a4d9bc1b7c17b67a5a5b8bdcf7"
VERSION = "1.1.0-hc40m"
MONTHS = {
    1:"GIVRE", 2:"DÉGEL", 3:"SEMAILLES", 4:"AVERSES", 5:"FRONDAISONS", 6:"LONGS-JOURS",
    7:"BRAISES", 8:"HAUTES LUMIÈRES", 9:"RÉCOLTES", 10:"CUIVRES", 11:"BRUMES", 12:"RENOUVEAU",
}

def sha256(path: Path) -> str:
    return hashlib.sha256(path.read_bytes()).hexdigest()

def display_lines(rng: str, annual: bool, include_day: bool) -> str:
    lines=[]
    for n,name in MONTHS.items():
        if annual:
            comps='[{"text":"——","color":"yellow","bold":true},{"text":" ANNÉE "},{"score":{"name":"year","objective":"refresh_daycounter"}},{"text":" / '+name+'"}'
            end=' ——'
        else:
            comps='[{"text":"— ANNÉE "},{"score":{"name":"year","objective":"refresh_daycounter"}},{"text":" / '+name+'"}'
            end=' —'
        if include_day:
            comps+=',{"text":" / JOUR "},{"score":{"name":"day_of_month","objective":"refresh_daycounter"}}'
        comps+=',{"text":"'+end+'"}]'
        lines.append(f'execute if score @s refresh_daycounter2 matches {rng} if score month refresh_daycounter matches {n} run title @s actionbar {comps}')
    return "\n".join(lines)

def patch(root: Path) -> None:
    # The new server system changes only timeOfDay to 0.5x. The calendar already uses
    # "time query day", so its date arithmetic requires no 20->40 minute conversion.
    # Only halve the dawn trigger window to preserve the old ~4 seconds of real time.
    for p in (
        root/"data/vanilla_refresh/functions/day_counter/root.mcfunction",
        root/"1.21-above/data/vanilla_refresh/function/day_counter/root.mcfunction",
    ):
        s=p.read_text(encoding="utf-8")
        if "matches 1..80" not in s: raise RuntimeError(f"unexpected base: {p}")
        p.write_text("# HC40M: timeOfDay 0.5x; 1..40 keeps the original ~4 s dawn window.\n"+s.replace("matches 1..80","matches 1..40"),encoding="utf-8")

    month_note=(
        "# Calendrier impérial Haute Capitale : 1 Givre, 2 Dégel, 3 Semailles, 4 Averses, "
        "5 Frondaisons, 6 Longs-Jours, 7 Braises, 8 Hautes Lumières, 9 Récoltes, "
        "10 Cuivres, 11 Brumes, 12 Renouveau.\n"
        "# Le score month reste un index interne.\n"
    )
    for p in (
        root/"data/vanilla_refresh/functions/day_counter/date.mcfunction",
        root/"1.21-above/data/vanilla_refresh/function/day_counter/date.mcfunction",
    ):
        s=p.read_text(encoding="utf-8")
        p.write_text(s.replace("# Arthur — conversion du compteur de jours en date lisible\n","# Arthur — conversion du compteur de jours en date lisible\n"+month_note),encoding="utf-8")

    daily_old1='execute if score @s refresh_daycounter2 matches 125..159 run title @s actionbar [{"text": "— ANNÉE "},{"score":{"name":"year","objective":"refresh_daycounter"}},{"text": " / MOIS "},{"score":{"name":"month","objective":"refresh_daycounter"}},{"text": " —"}]'
    daily_old2='execute if score @s refresh_daycounter2 matches 160..235 run title @s actionbar [{"text": "— ANNÉE "},{"score":{"name":"year","objective":"refresh_daycounter"}},{"text": " / MOIS "},{"score":{"name":"month","objective":"refresh_daycounter"}},{"text": " / JOUR "},{"score":{"name":"day_of_month","objective":"refresh_daycounter"}},{"text": " —"}]'
    for p in (
        root/"data/vanilla_refresh/functions/day_counter/animation.mcfunction",
        root/"1.21-above/data/vanilla_refresh/function/day_counter/animation.mcfunction",
    ):
        s=p.read_text(encoding="utf-8")
        if daily_old1 not in s or daily_old2 not in s: raise RuntimeError(f"unexpected daily animation base: {p}")
        s=s.replace(daily_old1,display_lines("125..159",False,False))
        s=s.replace(daily_old2,display_lines("160..235",False,True))
        s=s.replace("Année / Mois / Jour","Année / Nom du mois / Jour")
        p.write_text(s,encoding="utf-8")

    annual_old1='execute if score @s refresh_daycounter2 matches 1150..1185 run title @s actionbar [{"text": "——","color": "yellow","bold": true},{"text": " ANNÉE "},{"score":{"name":"year","objective":"refresh_daycounter"}},{"text": " / MOIS "},{"score":{"name":"month","objective":"refresh_daycounter"}},{"text": " ——"}]'
    annual_old2='execute if score @s refresh_daycounter2 matches 1190..1260 run title @s actionbar [{"text": "——","color": "yellow","bold": true},{"text": " ANNÉE "},{"score":{"name":"year","objective":"refresh_daycounter"}},{"text": " / MOIS "},{"score":{"name":"month","objective":"refresh_daycounter"}},{"text": " / JOUR "},{"score":{"name":"day_of_month","objective":"refresh_daycounter"}},{"text": " ——"}]'
    for p in (
        root/"data/vanilla_refresh/functions/day_counter/animation2.mcfunction",
        root/"1.21-above/data/vanilla_refresh/function/day_counter/animation2.mcfunction",
    ):
        s=p.read_text(encoding="utf-8")
        if annual_old1 not in s or annual_old2 not in s: raise RuntimeError(f"unexpected annual animation base: {p}")
        s=s.replace(annual_old1,display_lines("1150..1185",True,False))
        s=s.replace(annual_old2,display_lines("1190..1260",True,True))
        p.write_text(s,encoding="utf-8")

    fabric=root/"fabric.mod.json"
    d=json.loads(fabric.read_text(encoding="utf-8"))
    d.update(version=VERSION,name="Typewriter Calendar — Haute Capitale",description="Calendrier Haute Capitale 365 jours, mois impériaux nommés, compatible cycle jour/nuit 40 min.")
    fabric.write_text(json.dumps(d,ensure_ascii=False,indent=4)+"\n",encoding="utf-8")

    # Metadata mirrors.
    quilt=root/"quilt.mod.json"; q=json.loads(quilt.read_text(encoding="utf-8"))
    q["quilt_loader"]["version"]=VERSION
    q["quilt_loader"]["metadata"]["name"]="Typewriter Calendar — Haute Capitale"
    q["quilt_loader"]["metadata"]["description"]="Calendrier Haute Capitale 365 jours, mois impériaux nommés, compatible cycle jour/nuit 40 min."
    quilt.write_text(json.dumps(q,ensure_ascii=False,separators=(",",":"))+"\n",encoding="utf-8")

    mods=root/"META-INF/mods.toml"; s=mods.read_text(encoding="utf-8")
    s=s.replace("version = '1.0'","version = '1.1.0-hc40m'").replace("displayName = 'Typewriter Daycounter'","displayName = 'Typewriter Calendar - Haute Capitale'")
    mods.write_text(s,encoding="utf-8")

    pack=root/"pack.mcmeta"; s=pack.read_text(encoding="utf-8")
    pack.write_text(s.replace('"translate": "Adds a daycounter, displaying at the start of each day"','"text": "Haute Capitale — calendrier impérial 365 j / cycle 40 min"'),encoding="utf-8")

def main(inp: Path,out: Path) -> None:
    if sha256(inp)!=EXPECTED_INPUT_SHA256: raise RuntimeError(f"wrong input SHA-256: {sha256(inp)}")
    with tempfile.TemporaryDirectory() as td:
        root=Path(td)
        with zipfile.ZipFile(inp) as z: z.extractall(root)
        patch(root)
        with zipfile.ZipFile(out,"w",compression=zipfile.ZIP_DEFLATED,compresslevel=9) as z:
            for p in sorted(root.rglob("*")):
                if p.is_file(): z.write(p,p.relative_to(root).as_posix())
    with zipfile.ZipFile(out) as z:
        assert z.testzip() is None
        root=z.read("1.21-above/data/vanilla_refresh/function/day_counter/root.mcfunction").decode()
        assert "matches 1..40" in root and "matches 1..80" not in root
        anim=z.read("1.21-above/data/vanilla_refresh/function/day_counter/animation.mcfunction").decode()
        assert all(m in anim for m in MONTHS.values()) and "/ MOIS " not in anim

if __name__=="__main__":
    if len(sys.argv)!=3: raise SystemExit("usage: patch_typewriter_calendar_hc40m_1_1_0.py <base.jar> <output.jar>")
    main(Path(sys.argv[1]),Path(sys.argv[2]))
    print(sha256(Path(sys.argv[2])))
