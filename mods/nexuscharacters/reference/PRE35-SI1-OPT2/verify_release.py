from pathlib import Path
import csv,hashlib,json,zipfile
from PIL import Image,ImageChops
R=Path(__file__).resolve().parent;Q=R/'previous/npc/qa'
jar=R/'out/NexusCharacters-Indivis-1.21.11-PRE35-SI1-OPT2.jar'
sha=hashlib.sha256(jar.read_bytes()).hexdigest()
build=json.loads((R/'qa/build.json').read_text());exact=json.loads((R/'qa/exact.json').read_text());comparison=json.loads((R/'qa/comparison.json').read_text())
assert sha==build['sha256']==exact['candidate_sha256']==comparison['candidate_sha256']
assert comparison['geometry_states']==216 and not comparison['geometry_differences']
records={}
for name in ['opt2-perf','opt2-menus','opt2-oracle-future','opt2-reload']:
    r=json.loads((Q/('last-'+name+'.json')).read_text());records[name]=r
    assert r['nexus_sha256']==sha and 'PASS' in r['pass'],(name,'stale/failed evidence')
    loaded=next((Path(r['game'])/'mods').glob('NexusCharacters*.jar'))
    assert hashlib.sha256(loaded.read_bytes()).hexdigest()==sha,(name,'loaded bytes differ')
    assert not (Path(r['game'])/'evidence/failure.txt').exists(),name
def evidence(name):return Path(records[name]['game'])/'evidence'
def counters(path):return {k:int(v) for k,v in (line.split('=') for line in path.read_text().splitlines())}
oracle=counters(evidence('opt2-oracle-future')/'opt-counters.txt');reuse=counters(evidence('opt2-oracle-future')/'opt-reuse.txt');transforms=counters(evidence('opt2-oracle-future')/'opt-transforms.txt')
assert oracle['oracleCalls']>1000 and oracle['oraclePolygons']>100000 and reuse['hits']>0
assert transforms['oracleCalls']>100 and transforms['oraclePolygons']>10000
assert 'FUTURE_ASSETS hair=930 beard=930 outfit=930' in records['opt2-oracle-future']['pass']
assert 'OPT2_ORACLE_STRESS_PASS' in records['opt2-oracle-future']['pass']
assert 'OPT2_LIVE_CONFIG_PASS' in records['opt2-oracle-future']['pass']
assert 'WORLD_RELOAD_PASS' in records['opt2-reload']['pass'] and records['opt2-reload']['fixed_facing']
timed=evidence('opt2-perf');assert counters(timed/'opt-counters.txt')['oracleCalls']==0
assert counters(timed/'opt-transforms.txt')['oracleCalls']==0
before=json.loads((Q/'last-opt1-menus.json').read_text());A=Path(before['game'])/'evidence';B=evidence('opt2-menus')
pixels=[]
for i in range(23):
    name=f'menu-{i:02d}.png';a=Image.open(A/name).convert('RGB');b=Image.open(B/name).convert('RGB')
    assert a.size==b.size
    box=ImageChops.difference(a,b).getbbox();assert box is None,('Menu changed',name,box);pixels.append(name)
geometry={}
for name in ['geometry.csv','pose-geometry.csv']:
    assert (A/'geometry'/name).read_bytes()==(B/'geometry'/name).read_bytes(),name
    geometry[name]=len((B/'geometry'/name).read_text().splitlines())
a=R/'previous/pre35/qa/baseline-hand/arm-geometry.csv';b=R/'previous/pre35/qa/candidate-hand/arm-geometry.csv'
assert a.read_bytes()==b.read_bytes(),'First-person geometry changed';geometry['arms']=len(b.read_text().splitlines())
base=R.parent/'analysis-opt1/inputs/NexusCharacters-Indivis-1.21.11-PRE35-SI1-OPT1(1).jar'
with zipfile.ZipFile(base) as old,zipfile.ZipFile(jar) as new:
    assert new.testzip() is None
    changed=[n for n in old.namelist() if old.read(n)!=new.read(n)]
    assert changed==build['changed_entries']
    assets=[n for n in old.namelist() if n.startswith('assets/')];assert len(assets)==4966
    assert all(old.read(n)==new.read(n) for n in assets)
    assert old.read('net/tompsen/nexuscharacters/GenericSkinLayerSupport.class')==new.read('net/tompsen/nexuscharacters/GenericSkinLayerSupport.class')
    assert old.read('net/tompsen/nexuscharacters/LowerJointSupport.class')==new.read('net/tompsen/nexuscharacters/LowerJointSupport.class')
    assert json.loads(new.read('fabric.mod.json'))['version']=='1.0.0-beta.35+indivis.1.21.11.SI1.OPT2'
audit={'sha256':sha,'unchanged_assets':4966,'pixel_identical_menus':pixels,'geometry':geometry,'oracle':oracle,'reuse':reuse,'transforms':transforms,'records':records,'build':build,'limitations':['Software GL, not a production FPS measurement.','Full modpack, actual spells/dialogue-camera and authenticated Mojang accounts not reproduced in this OPT2 session.','Synthetic offline QA retains PRE35 vault manifest/owner warnings; player-vault persistence is not validated.','Original PRE35/SI1 server, network and UI code are unchanged; real NPC saved-world reload is checked.']}
(R/'qa/release-audit.json').write_text(json.dumps(audit,indent=2)+'\n')
print('OPT2_RELEASE_PASS',sha,'pixel-identical menus=23','exact regression states='+str(sum(geometry.values())))
