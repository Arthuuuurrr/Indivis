"""Build an additive, data-only collection; never rebuild or replace the mod."""
from pathlib import Path
import hashlib, json, zipfile, sys, shutil

R = Path(__file__).resolve().parents[1]
C = R / 'collections/morphologies-v2'
S = C / 'staging'
JAR = R / 'Indivis-Nexus-NPC-0.1.0-PREVIEW1-1.21.11.jar'
JAR_SHA = 'cc9cfb403e48c21a22d14e1bcc15152d3ae74dc86d5ab9ddae9a926d35836047'
LIMITS = {'high_elf': (1.02, 1.12, .76, .96), 'wood_elf': (.90, .98, .78, .98),
          'dwarf': (.78, .86, 1.05, 1.25), 'nordic': (1.02, 1.12, 1., 1.22)}
IDS = ['haut_elfe_sentinelle_v2', 'elfe_sylvestre_v2', 'nain_artisan_v2',
       'naine_artisane_v2', 'nordique_eclaireur_v2']

def digest(data):
    return hashlib.sha256(data).hexdigest()

def native(p):
    quote = lambda value: json.dumps(value, ensure_ascii=False)
    entity = 'easy_npc:humanoid_slim' if p['slim'] else 'easy_npc:humanoid'
    saved = {'preset': p, 'originalSkin': '{Type:"DEFAULT",Timestamp:0L}', 'originalRoot': '{}'}
    content = 'indivis-nexus-npc:v1:' + json.dumps(saved, ensure_ascii=False, separators=(',', ':'))
    scale = f"[{p['build']}f,{p['height']}f,{p['build']}f]"
    return ('{PresetMetadata:{author:"Indivis",category:"Indivis — Morphologies V2",created:1L,modified:1L,'
            'description:"Morphologie raciale centrale. Module Indivis Nexus NPC PREVIEW1 requis.",entityTypeId:'
            + quote(entity) + ',name:' + quote(p['name']) + ',variantType:'
            + quote('ALEX' if p['slim'] else 'STEVE') + ',version:"1.0.0"},data:{id:' + quote(entity)
            + ',EasyNPCVersion:3,PersistenceRequired:1b,CustomNameVisible:1b,CustomName:{text:'
            + quote(p['name']) + '},ModelData:{Root:{Scale:' + scale + '}},SkinData:{Type:"DEFAULT",Name:'
            + quote('Nexus: ' + p['id']) + ',Content:' + quote(content)
            + ',Timestamp:0L},Status:{finalized:1b}}}\n').encode()

def prepare():
    assert digest(JAR.read_bytes()) == JAR_SHA, 'The tested companion mod must remain unchanged'
    old = {}
    with zipfile.ZipFile(JAR) as jar:
        for f in sorted((R / 'resources/presets').glob('*.json')):
            assert jar.read('presets/' + f.name) == f.read_bytes(), 'An original preset was altered'
            old[f.stem] = digest(f.read_bytes())
    presets = []
    for id in IDS:
        f = C / 'presets' / (id + '.json')
        p = json.loads(f.read_text())
        assert p['id'] == id and id not in old
        loh, hih, lob, hib = LIMITS[p['race']]
        assert abs(p['height'] - (loh + hih) / 2) < .00001
        assert abs(p['build'] - (lob + hib) / 2) < .00001
        folder = S / 'config/indivis-nexus-npc/presets'
        folder.mkdir(parents=True, exist_ok=True)
        shutil.copy2(f, folder / f.name)
        model = 'humanoid_slim' if p['slim'] else 'humanoid'
        folder = S / 'config/easy_npc/preset' / model
        folder.mkdir(parents=True, exist_ok=True)
        (folder / (id + '.npc.snbt')).write_bytes(native(p))
        presets.append(p)
    manifest = {'collection': 'morphologies-v2', 'mod_sha256_unchanged': JAR_SHA,
                'original_presets_unchanged': old, 'new_presets': presets}
    (C / 'manifest.json').write_text(json.dumps(manifest, ensure_ascii=False, indent=2) + '\n')
    print('COLLECTION_PREPARED', len(presets), 'original presets unchanged:', len(old), flush=True)

def package():
    prepare()
    evidence = R / 'qa/evidence-collection'
    reload = R / 'qa/evidence-collection-reload'
    for mode in ['collection', 'collection-reload']:
        record = json.loads((R / ('qa/last-' + mode + '.json')).read_text())
        assert record['jar_sha256'] == JAR_SHA
        assert 'NPC_CLIENT_PASS' in record['pass']
        for id in IDS:
            assert id in record['pass']
    entries = {p.relative_to(S).as_posix(): p.read_bytes() for p in S.rglob('*') if p.is_file()}
    entries['LIRE-MOI.md'] = (C / 'LIRE-MOI.md').read_bytes()
    entries['VERIFICATIONS.md'] = (C / 'VERIFICATIONS.md').read_bytes()
    entries['manifest.json'] = (C / 'manifest.json').read_bytes()
    for name in ['presets-face.png', 'presets-dos.png', 'presets-trois-quarts.png']:
        entries['apercus/' + name] = (evidence / name).read_bytes()
    for mode, folder in [('collection', evidence), ('rechargement', reload)]:
        entries['verifications/' + mode + '-pass.txt'] = (folder / 'pass.txt').read_bytes()
    entries['SHA256SUMS'] = ''.join(digest(data) + '  ' + name + '\n' for name, data in sorted(entries.items())).encode()
    target = R / 'release/Indivis-PNJ-Morphologies-V2.zip'
    with zipfile.ZipFile(target, 'w') as z:
        for name, data in sorted(entries.items()):
            info = zipfile.ZipInfo(name, (2026, 10, 26, 0, 0, 0))
            info.compress_type = zipfile.ZIP_DEFLATED
            z.writestr(info, data)
    with zipfile.ZipFile(target) as z:
        assert z.testzip() is None
        assert len([n for n in z.namelist() if n.endswith('.json') and n.startswith('config/')]) == 5
        assert len([n for n in z.namelist() if n.endswith('.npc.snbt')]) == 5
        assert not any(n.endswith(('.jar', '.class')) for n in z.namelist())
    record = {'file': str(target), 'sha256': digest(target.read_bytes()), 'bytes': target.stat().st_size,
              'entries': len(entries), 'unchanged_mod_sha256': JAR_SHA}
    (C / 'release.json').write_text(json.dumps(record, indent=2) + '\n')
    print('COLLECTION_PACKAGE_PASS', json.dumps(record), flush=True)

if __name__ == '__main__':
    package() if '--package' in sys.argv else prepare()
