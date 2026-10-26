"""Release gate. Requires fresh paired CPU, exact-oracle and regression evidence."""
from pathlib import Path
import json,zipfile,hashlib
from PIL import Image,ImageChops

R=Path(__file__).resolve().parents[1];Q=R.parent/'nexus-npc/qa';P=R.parent/'nexus-pre35/qa'
base=R.parent/'opt-bases/NexusCharacters-Indivis-1.21.11-PRE35-SI1.jar'
jar=R/'NexusCharacters-Indivis-1.21.11-PRE35-SI1-OPT1.jar'
sha=hashlib.sha256(jar.read_bytes()).hexdigest()
allowed={'fabric.mod.json','net/tompsen/nexuscharacters/ExactSurfaceCleaner.class',
 'net/tompsen/nexuscharacters/PosedSurfaceSupport$Grid.class','net/tompsen/nexuscharacters/PoseScratch$Workspace.class'}
with zipfile.ZipFile(base) as a,zipfile.ZipFile(jar) as b:
 assert b.testzip() is None
 old=set(a.namelist());new=set(b.namelist());assert old<=new
 changed={n for n in old if a.read(n)!=b.read(n)};assert changed==allowed,changed
 added=new-old
 assert all(n.startswith(('net/tompsen/nexuscharacters/SpatialSurfaceCleaner',
   'net/tompsen/nexuscharacters/PartSurfaceReuse')) and n.endswith('.class') for n in added)
 assets=[n for n in old if n.startswith('assets/')]
 assert all(a.read(n)==b.read(n) for n in assets)

def record(label):
 r=json.loads((Q/('last-'+label+'.json')).read_text())
 assert r['nexus_sha256']==sha,(label,'stale JAR')
 assert hashlib.sha256(next((Path(r['game'])/'mods').glob('NexusCharacters*.jar')).read_bytes()).hexdigest()==sha,(label,'loaded JAR mismatch')
 return r,Path(r['game'])/'evidence'

comparison=json.loads((R/'qa/comparison.json').read_text())
assert comparison['candidate_nexus_sha256']==sha
assert comparison['samples_per_case']==5
assert comparison['geometry_states']==216 and not comparison['geometry_differences']
timed,te=record('opt-candidate')
assert not (Path(timed['game'])/'cost.jfr').exists(),'Final timing was recorded with JFR enabled'
timed_counts=dict(line.split('=') for line in (te/'opt-counters.txt').read_text().splitlines())
assert int(timed_counts['oracleCalls'])==0,'Final timing still has the exact oracle enabled'
oracle,oe=record('opt-oracle2')
counters=dict(line.split('=') for line in (oe/'opt-counters.txt').read_text().splitlines())
reuse=dict(line.split('=') for line in (oe/'opt-reuse.txt').read_text().splitlines())
assert int(counters['oracleCalls'])>1000 and int(counters['oraclePolygons'])>500000
assert int(reuse['hits'])>0,'Part-reuse was not exercised under the exact oracle'

menus,me=record('opt-menus')
bm=json.loads((Q/'last-opt-menus-baseline.json').read_text());be=Path(bm['game'])/'evidence'
pixel=[]
for i in range(23):
 name=f'menu-{i:02d}.png'
 a=Image.open(be/name).convert('RGB');b=Image.open(me/name).convert('RGB')
 assert a.size==b.size
 box=ImageChops.difference(a,b).getbbox();pixel.append({'file':name,'difference_box':box})
assert all(v['difference_box'] is None for v in pixel),'Menu pixels differ from PRE35'
geometry=[]
for name in ('geometry.csv','pose-geometry.csv'):
 a=(be/'geometry'/name).read_bytes();b=(me/'geometry'/name).read_bytes()
 assert a==b,('menu geometry differs',name)
 geometry.append({'file':name,'states':len(a.splitlines())})
a=(P/'baseline-hand/arm-geometry.csv').read_bytes();b=(P/'candidate-hand/arm-geometry.csv').read_bytes()
assert a==b,'First-person geometry differs'
geometry.append({'file':'arm-geometry.csv','states':len(a.splitlines())})

future,fe=record('opt-future');visual,ve=record('opt-visual-reload')
assert 'FUTURE_ASSETS hair=930 beard=930 outfit=930' in future['pass']
assert 'future930' in future['pass']
assert visual['reload_from'] and visual['fixed_facing']
assert 'WORLD_RELOAD_PASS' in visual['pass']
result={'sha256':sha,'base_sha256':hashlib.sha256(base.read_bytes()).hexdigest(),
 'unchanged_assets':len(assets),'changed_original_entries':sorted(changed),'added_entries':sorted(added),
 'oracle':counters,'reuse':reuse,'menu_pixels':pixel,'menu_geometry':geometry,
 'records':{'oracle':oracle,'menus':menus,'future':future,'visual_reload':visual},
 'limitations':['CPU preparation measurements are not GPU FPS. Software GL test environment.',
  'Synthetic offline account: original PRE35 vault/manifest owner warnings, no production persistence validation.',
  'Full production pack, actual spells and dialogue-camera integrations are not reproduced.']}
(R/'qa/release-audit.json').write_text(json.dumps(result,ensure_ascii=False,indent=2)+'\n')
print('OPT1_RELEASE_AUDIT_PASS assets='+str(len(assets))+' exact_geometry_states='+str(sum(v['states'] for v in geometry))
 +' pixel_identical_menus='+str(sum(v['difference_box'] is None for v in pixel))+'/23')
