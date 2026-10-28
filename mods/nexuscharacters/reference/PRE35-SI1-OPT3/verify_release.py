from pathlib import Path
import csv,hashlib,json,zipfile
from PIL import Image,ImageChops
R=Path(__file__).resolve().parent;OLD=R.parent/'opt2';Q=OLD/'previous/npc/qa'
jar=R/'out/NexusCharacters-Indivis-1.21.11-PRE35-SI1-OPT3.jar'
sha=hashlib.sha256(jar.read_bytes()).hexdigest()
build=json.loads((R/'qa/build.json').read_text());exact=json.loads((R/'qa/exact.json').read_text());comparison=json.loads((R/'qa/comparison.json').read_text())
assert sha==build['sha256']==exact['candidate_sha256']==comparison['candidate_sha256']
assert build['compact'] and 'OPT3_BYTECODE_AUDIT_PASS' in (R/'qa/bytecode-audit.txt').read_text()
assert comparison['geometry_states']==216 and not comparison['geometry_differences']
records={}
for name in ['opt3-perf','opt3-menus','opt3-oracle-future','opt3-reload']:
    r=json.loads((Q/('last-'+name+'.json')).read_text());records[name]=r
    assert r['nexus_sha256']==sha and 'PASS' in r['pass'],(name,'stale/failed evidence')
    loaded=next((Path(r['game'])/'mods').glob('NexusCharacters*.jar'))
    assert hashlib.sha256(loaded.read_bytes()).hexdigest()==sha,(name,'loaded bytes differ')
    assert not (Path(r['game'])/'evidence/failure.txt').exists(),name
def evidence(name):return Path(records[name]['game'])/'evidence'
def counters(path):return {k:int(v) for k,v in (line.split('=') for line in path.read_text().splitlines())}
oracle=counters(evidence('opt3-oracle-future')/'opt-counters.txt');reuse=counters(evidence('opt3-oracle-future')/'opt-reuse.txt');transforms=counters(evidence('opt3-oracle-future')/'opt-transforms.txt')
volumes=counters(evidence('opt3-oracle-future')/'opt3-PartVolumeCache.txt');clip=counters(evidence('opt3-oracle-future')/'opt3-CompactSurfaceClipper.txt')
assert oracle['oracleCalls']>1000 and oracle['oraclePolygons']>100000 and reuse['hits']>0
assert transforms['oracleCalls']>100 and transforms['oraclePolygons']>10000
assert volumes['oracleCalls']>100 and volumes['hits']>0 and volumes['peakBytes']<=8*1024*1024
assert clip['oracleCalls']>100 and clip['oraclePolygons']>100
for marker in ['FUTURE_ASSETS hair=930 beard=930 outfit=930','OPT2_ORACLE_STRESS_PASS','OPT2_LIVE_CONFIG_PASS']:
    assert marker in records['opt3-oracle-future']['pass'],marker
assert 'WORLD_RELOAD_PASS' in records['opt3-reload']['pass'] and records['opt3-reload']['fixed_facing']
timed=evidence('opt3-perf')
for name in ['opt-counters.txt','opt-transforms.txt','opt3-PartVolumeCache.txt','opt3-CompactSurfaceClipper.txt']:
    assert counters(timed/name)['oracleCalls']==0,('Oracle active during timing',name)
assert '-XX:StartFlightRecording' not in (Q/'last-opt3-perf.json').read_text()
before=json.loads((Q/'last-opt2-menus.json').read_text());A=Path(before['game'])/'evidence';B=evidence('opt3-menus')
pixels=[]
for i in range(23):
    name=f'menu-{i:02d}.png';a=Image.open(A/name).convert('RGB');b=Image.open(B/name).convert('RGB')
    assert a.size==b.size
    box=ImageChops.difference(a,b).getbbox();assert box is None,('Menu changed',name,box);pixels.append(name)
geometry={}
for name in ['geometry.csv','pose-geometry.csv']:
    assert (A/'geometry'/name).read_bytes()==(B/'geometry'/name).read_bytes(),name
    geometry[name]=len((B/'geometry'/name).read_text().splitlines())
a=OLD/'previous/pre35/qa/baseline-hand/arm-geometry.csv';b=OLD/'previous/pre35/qa/candidate-hand/arm-geometry.csv'
assert a.read_bytes()==b.read_bytes(),'First-person geometry changed';geometry['arms']=len(b.read_text().splitlines())
base=OLD/'out/NexusCharacters-Indivis-1.21.11-PRE35-SI1-OPT2.jar'
with zipfile.ZipFile(base) as old,zipfile.ZipFile(jar) as new:
    assert new.testzip() is None
    changed=sorted(n for n in old.namelist() if old.read(n)!=new.read(n));added=sorted(set(new.namelist())-set(old.namelist()))
    assert changed==build['changed_entries'] and added==build['added_entries']
    expected={'fabric.mod.json',*['net/tompsen/nexuscharacters/'+n+'.class' for n in ['ExactSurfaceClipper','PoseScratch$Workspace','PosedSurfaceSupport$Grid','PosedSurfaceSupport','SpatialSurfaceCleaner$Index']]}
    assert set(changed)==expected,changed
    assets=[n for n in old.namelist() if n.startswith('assets/')];assert len(assets)==4966
    assert all(old.read(n)==new.read(n) for n in assets)
    for name in ['GenericSkinLayerSupport','LowerJointSupport','SurfaceGeometry','PartSurfaceReuse','PreparedSurfaceCache','SpatialSurfaceCleaner']:
        n='net/tompsen/nexuscharacters/'+name+'.class';assert old.read(n)==new.read(n),name
    assert json.loads(new.read('fabric.mod.json'))['version']=='1.0.0-beta.35+indivis.1.21.11.SI1.OPT3'
audit={'sha256':sha,'unchanged_assets':4966,'pixel_identical_menus':pixels,'geometry':geometry,'oracle':oracle,'reuse':reuse,'transforms':transforms,'volumes':volumes,'compact_clipper':clip,'records':records,'build':build,'limitations':['Software GL, not production FPS.','Full modpack, spells/dialogue camera and authenticated Mojang account not reproduced.','Synthetic offline vault manifest/owner warnings retained; player-vault persistence not validated.','World reload checks actual EasyNPC UUIDs/recipes/tags.']}
(R/'qa/release-audit.json').write_text(json.dumps(audit,indent=2)+'\n')
print('OPT3_RELEASE_PASS',sha,'pixel-identical menus=23','exact regression states='+str(sum(geometry.values())))
