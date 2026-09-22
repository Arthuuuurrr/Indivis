#!/usr/bin/env python3
"""Generate Spell Engine spell->icon mappings from a CapSkills datapack.

Expected input: CapSkills 0.10.19 RC2F (or compatible layout).
Outputs:
- JSON audit/resource map
- TSV consumed by PatchSpellIcons.java
"""
from __future__ import annotations

import json
import sys
import zipfile
from collections import defaultdict
from pathlib import Path

DEFINITIONS = "data/capskills/puffish_skills/categories/doctrine/definitions.json"


def ability_id_from_path(name: str) -> str | None:
    if not name.startswith("data/") or "/capitale_abilities/" not in name or not name.endswith(".json"):
        return None
    parts = name.split("/")
    namespace = parts[1]
    marker = parts.index("capitale_abilities")
    path = "/".join(parts[marker + 1 :])[:-5]
    return f"{namespace}:{path}"


def main() -> int:
    if len(sys.argv) != 4:
        print(
            "usage: generate_capskills_icon_map.py <capskills.zip> <out.json> <out.tsv>",
            file=sys.stderr,
        )
        return 2

    source = Path(sys.argv[1])
    out_json = Path(sys.argv[2])
    out_tsv = Path(sys.argv[3])

    with zipfile.ZipFile(source) as zf:
        defs = json.loads(zf.read(DEFINITIONS))

        ability_to_spell: dict[str, str] = {}
        for name in zf.namelist():
            ability_id = ability_id_from_path(name)
            if not ability_id:
                continue
            data = json.loads(zf.read(name))
            spell = data.get("spell")
            if isinstance(spell, str) and spell:
                ability_to_spell[ability_id] = spell

        spell_icons: dict[str, set[str]] = defaultdict(set)
        unmapped: list[dict[str, str]] = []
        rewarded_with_spell = 0

        for definition in defs.values():
            if not isinstance(definition, dict):
                continue

            icon = definition.get("icon")
            texture = None
            if isinstance(icon, dict) and icon.get("type") == "texture":
                data = icon.get("data")
                if isinstance(data, dict) and isinstance(data.get("texture"), str):
                    texture = data["texture"]

            for reward in definition.get("rewards", []):
                if not isinstance(reward, dict) or reward.get("type") != "haute_capitale_rpg:abilities":
                    continue
                data = reward.get("data", {})
                for ability in data.get("abilities", []):
                    spell = ability_to_spell.get(ability)
                    if not spell:
                        continue
                    rewarded_with_spell += 1
                    if texture:
                        spell_icons[spell].add(texture)
                    else:
                        unmapped.append({"spell": spell, "ability": ability})

        conflicts = {
            spell: sorted(textures)
            for spell, textures in spell_icons.items()
            if len(textures) > 1
        }
        if conflicts:
            print(json.dumps({"conflicts": conflicts}, indent=2, ensure_ascii=False), file=sys.stderr)
            return 1

        mapping = {spell: next(iter(textures)) for spell, textures in sorted(spell_icons.items())}

        payload = {
            "source": "CapSkills 0.10.19 RC2F TREE HARD RESTORE",
            "mapping_count": len(mapping),
            "spell_to_icon": mapping,
            "unmapped_ability_icons": unmapped,
        }
        out_json.write_text(json.dumps(payload, indent=2, ensure_ascii=False) + "\n", encoding="utf-8")
        out_tsv.write_text(
            "".join(f"{spell}\t{texture}\n" for spell, texture in mapping.items()),
            encoding="utf-8",
        )

    print(f"rewarded abilities with spell: {rewarded_with_spell}")
    print(f"mapping entries: {len(mapping)}")
    print(f"unmapped explicit icons: {len(unmapped)}")
    print("conflicts: 0")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
