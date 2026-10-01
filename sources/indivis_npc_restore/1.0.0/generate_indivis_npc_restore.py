from pathlib import Path
import csv, json, zipfile

VERSION = '1.0.0'
PACK_NAME = f'indivis_npc_restore-{VERSION}'
BATCH_SIZE = 8
DELAY = '2s'
ROOT = Path(__file__).resolve().parent
MANIFEST = ROOT / 'manifest_indivis_npc_restore_1.0.0.csv'
MANIFEST_PARTS = sorted(ROOT.glob('manifest_indivis_npc_restore_1.0.0.part*.csv'))
OUT = ROOT / PACK_NAME

rows = []
manifest_files = [MANIFEST] if MANIFEST.exists() else MANIFEST_PARTS
if not manifest_files:
    raise FileNotFoundError('Aucun manifeste EasyNPC trouvé.')
for manifest_file in manifest_files:
    with manifest_file.open(encoding='utf-8-sig') as f:
        for row in csv.DictReader(f, delimiter=';'):
            rows.append(row)

def mode_records(column):
    recs = [(r['name'], r['uuid']) for r in rows if r.get(column) == 'yes']
    return sorted(recs, key=lambda x: (x[0].casefold(), x[1]))

MODES = {
    'story': mode_records('story'),
    'safe': mode_records('safe'),
    'all_known': mode_records('all_known'),
}

funcroot = OUT / 'data' / 'indivis_npc_restore' / 'function'
internal = funcroot / 'internal'
internal.mkdir(parents=True, exist_ok=True)

(OUT / 'pack.mcmeta').write_text(json.dumps({
    'pack': {
        'description': 'Indivis - restauration EasyNPC 7.12.1 (MC 1.21.11)',
        'min_format': [94, 1],
        'max_format': [94, 1],
    }
}, ensure_ascii=False, indent=2), encoding='utf-8')

batch_counts = {}
for mode, records in MODES.items():
    (funcroot / f'restore_{mode}.mcfunction').write_text(
        f'# Indivis EasyNPC recovery: {mode}\n'
        'execute if data storage indivis_npc_restore:state {running:1b} run tellraw @a [{"text":"[NPC Restore] Une restauration est déjà en cours.","color":"yellow"}]\n'
        f'execute unless data storage indivis_npc_restore:state {{running:1b}} run function indivis_npc_restore:internal/{mode}_start\n',
        encoding='utf-8')
    (internal / f'{mode}_start.mcfunction').write_text(
        'data modify storage indivis_npc_restore:state running set value 1b\n'
        f'data modify storage indivis_npc_restore:state mode set value "{mode}"\n'
        f'tellraw @a [{{"text":"[NPC Restore] Démarrage : {mode} ({len(records)} UUID connus).","color":"gold"}}]\n'
        f'function indivis_npc_restore:internal/{mode}_01\n', encoding='utf-8')
    batches = [records[i:i+BATCH_SIZE] for i in range(0, len(records), BATCH_SIZE)]
    batch_counts[mode] = len(batches)
    for i, batch in enumerate(batches, 1):
        lines = [f'# Batch {i}/{len(batches)} - {mode}']
        for name, uuid in batch:
            lines += [f'# {name}', f'easy_npc spawn {uuid}']
        if i < len(batches):
            lines.append(f'schedule function indivis_npc_restore:internal/{mode}_{i+1:02d} {DELAY} replace')
        else:
            lines += [
                'data remove storage indivis_npc_restore:state running',
                'data remove storage indivis_npc_restore:state mode',
                f'tellraw @a [{{"text":"[NPC Restore] Terminé : {mode}. Vérifie les PNJ et leurs trajets.","color":"green"}}]',
            ]
        (internal / f'{mode}_{i:02d}.mcfunction').write_text('\n'.join(lines) + '\n', encoding='utf-8')

reset = [
    'data remove storage indivis_npc_restore:state running',
    'data remove storage indivis_npc_restore:state mode',
]
for mode, count in batch_counts.items():
    reset += [f'schedule clear indivis_npc_restore:internal/{mode}_{i:02d}' for i in range(1, count + 1)]
reset.append('tellraw @a [{"text":"[NPC Restore] État et planifications de restauration annulés.","color":"yellow"}]')
(funcroot / 'reset.mcfunction').write_text('\n'.join(reset) + '\n', encoding='utf-8')

help_lines = [
    'tellraw @s [{"text":"Indivis NPC Restore 1.0.0","color":"gold","bold":true}]',
    f'tellraw @s [{{"text":"restore_story : {len(MODES["story"])} UUID de quête/histoire.","color":"white"}}]',
    f'tellraw @s [{{"text":"restore_safe : {len(MODES["safe"])} UUID vus récemment + histoire.","color":"white"}}]',
    f'tellraw @s [{{"text":"restore_all_known : {len(MODES["all_known"])} UUID connus des logs/backups.","color":"red"}}]',
    'tellraw @s [{"text":"Annulation : /function indivis_npc_restore:reset","color":"gray"}]',
]
(funcroot / 'help.mcfunction').write_text('\n'.join(help_lines) + '\n', encoding='utf-8')

# Export a normalized manifest in the generated pack.
with (OUT / 'manifest.csv').open('w', encoding='utf-8-sig', newline='') as f:
    fieldnames = ['uuid','name','story','safe','all_known','seen_2026_09_30','latest_backup_date','tags','source']
    writer = csv.DictWriter(f, fieldnames=fieldnames, delimiter=';')
    writer.writeheader()
    writer.writerows(rows)
readme = ROOT / 'README.md'
if readme.exists():
    (OUT / 'README.txt').write_text(readme.read_text(encoding='utf-8'), encoding='utf-8')

zip_path = ROOT / f'indivis_npc_restore-{VERSION}-mc1.21.11-easynpc7.12.1.zip'
with zipfile.ZipFile(zip_path, 'w', zipfile.ZIP_DEFLATED) as z:
    for p in OUT.rglob('*'):
        if p.is_file():
            z.write(p, p.relative_to(OUT))
print(zip_path)
