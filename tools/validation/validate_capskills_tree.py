#!/usr/bin/env python3
"""Static validator for the CapSkills 0.10.19 RC2F datapack."""
from __future__ import annotations

import collections
import json
import sys
import zipfile
from pathlib import Path

PREFIX = "data/capskills/puffish_skills/categories/doctrine/"
FILES = {
    "skills": PREFIX + "skills.json",
    "definitions": PREFIX + "definitions.json",
    "connections": PREFIX + "connections.json",
    "category": PREFIX + "category.json",
    "config": "data/capskills/puffish_skills/config.json",
}

EXPECTED_JSON_COUNT = 549
EXPECTED_SKILLS = 338
EXPECTED_COMPONENTS = [17, 321]
EXPECTED_ABILITY_JSON = 265


def load_json(zf: zipfile.ZipFile, name: str):
    return json.loads(zf.read(name).decode("utf-8"))


def edges_from_connections(data: dict) -> list[tuple[str, str]]:
    edges: list[tuple[str, str]] = []
    for section in data.values():
        if not isinstance(section, dict):
            continue
        for kind, values in section.items():
            if kind not in {"bidirectional", "directed"}:
                continue
            if not isinstance(values, list):
                continue
            for pair in values:
                if isinstance(pair, list) and len(pair) == 2:
                    edges.append((pair[0], pair[1]))
    return edges


def component_sizes(nodes: set[str], edges: list[tuple[str, str]]) -> list[int]:
    adj = {n: set() for n in nodes}
    for a, b in edges:
        if a in adj and b in adj:
            adj[a].add(b)
            adj[b].add(a)

    seen: set[str] = set()
    sizes: list[int] = []
    for start in nodes:
        if start in seen:
            continue
        q = [start]
        seen.add(start)
        size = 0
        while q:
            cur = q.pop()
            size += 1
            for nxt in adj[cur]:
                if nxt not in seen:
                    seen.add(nxt)
                    q.append(nxt)
        sizes.append(size)
    return sorted(sizes)


def main() -> int:
    if len(sys.argv) != 2:
        print("usage: validate_capskills_tree.py <capitale_skills_RC2F.zip>", file=sys.stderr)
        return 2

    path = Path(sys.argv[1])
    problems: list[str] = []

    try:
        with zipfile.ZipFile(path) as zf:
            names = zf.namelist()
            if len(names) != len(set(names)):
                problems.append("duplicate ZIP entries")
            if zf.testzip():
                problems.append("corrupt ZIP member")

            for key, name in FILES.items():
                if name not in names:
                    problems.append(f"missing Puffish file: {name}")

            all_json = [n for n in names if n.endswith(".json")]
            invalid_json = []
            for name in all_json:
                try:
                    load_json(zf, name)
                except Exception as exc:
                    invalid_json.append((name, str(exc)))
            if invalid_json:
                problems.append(f"invalid JSON files: {len(invalid_json)}")

            if len(all_json) != EXPECTED_JSON_COUNT:
                problems.append(f"JSON count {len(all_json)} != {EXPECTED_JSON_COUNT}")

            ability_json = [n for n in all_json if "/capitale_abilities/" in n]
            if len(ability_json) != EXPECTED_ABILITY_JSON:
                problems.append(
                    f"ability JSON count {len(ability_json)} != {EXPECTED_ABILITY_JSON}"
                )

            if all(name in names for name in FILES.values()):
                skills = load_json(zf, FILES["skills"])
                definitions = load_json(zf, FILES["definitions"])
                connections = load_json(zf, FILES["connections"])

                skill_ids = set(skills)
                definition_ids = set(definitions)

                if len(skill_ids) != EXPECTED_SKILLS:
                    problems.append(f"skill count {len(skill_ids)} != {EXPECTED_SKILLS}")
                if len(definition_ids) != EXPECTED_SKILLS:
                    problems.append(
                        f"definition count {len(definition_ids)} != {EXPECTED_SKILLS}"
                    )

                missing_defs = sorted(skill_ids - definition_ids)
                orphan_defs = sorted(definition_ids - skill_ids)
                if missing_defs:
                    problems.append(f"missing definitions: {len(missing_defs)}")
                if orphan_defs:
                    problems.append(f"orphan definitions: {len(orphan_defs)}")

                edges = edges_from_connections(connections)
                broken = [(a, b) for a, b in edges if a not in skill_ids or b not in skill_ids]
                if broken:
                    problems.append(f"broken connection endpoints: {len(broken)}")

                normalized = [tuple(sorted((a, b))) for a, b in edges]
                duplicate_pairs = sum(c - 1 for c in collections.Counter(normalized).values() if c > 1)
                if duplicate_pairs:
                    problems.append(f"duplicate undirected connection pairs: {duplicate_pairs}")

                comps = component_sizes(skill_ids, edges)
                if comps != EXPECTED_COMPONENTS:
                    problems.append(f"component sizes {comps} != {EXPECTED_COMPONENTS}")

                craft_nodes = {
                    "voie_metiers",
                    "alteration_alchimiste_1",
                    "alteration_alchimiste_2",
                    "archives_lecteur_1",
                    "artisanat_agriculteur_1",
                    "artisanat_agriculteur_2",
                    "artisanat_forgeron_1",
                    "artisanat_forgeron_2",
                    "artisanat_reparateur_enclume_1",
                    "enchanteur_1",
                    "enchanteur_2",
                    "enchanteur_main_artisan_1",
                    "enchanteur_maitre_enclume_1",
                    "profondeurs_mineur_1",
                    "profondeurs_mineur_2",
                    "profondeurs_prospection_1",
                    "profondeurs_prospection_2",
                }
                if not craft_nodes <= skill_ids:
                    problems.append("expected 17-node métiers/crafting branch is incomplete")

    except zipfile.BadZipFile as exc:
        problems.append(f"invalid ZIP: {exc}")

    if problems:
        print(f"FAIL: {len(problems)} problem(s)")
        for problem in problems:
            print(f"- {problem}")
        return 1

    print("PASS: CapSkills RC2F static tree invariants match archived final state")
    print("Expected disconnected branch: 17-node métiers/crafting component")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
