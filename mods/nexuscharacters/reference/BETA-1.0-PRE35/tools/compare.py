from pathlib import Path
import zipfile,json,hashlib,csv,statistics,re,subprocess
from PIL import Image,ImageChops
R=Path(__file__).resolve().parents[1];Q=R/'qa';meta=json.loads((Q/'build-audit.json').read_text())
base=R/'base/NexusCharacters-Indivis-1.21.11-PRE34-SI1.jar';new=R/'NexusCharacters-Indivis-1.21.11-PRE35-SI1.jar'
assert hashlib.sha256(new.read_bytes()).hexdigest()==meta['jar_sha256']
with zipfile.ZipFile(base) as a,zipfile.ZipFile(new) as b:
 assert len(set(b.namelist()))==len(b.namelist())
 removed=sorted(set(a.namelist())-set(b.namelist()));assert not removed
 changed=[n for n in a.namelist() if a.read(n)!=b.read(n)];added=sorted(set(b.namelist())-set(a.namelist()))
 assert set(changed)=={'fabric.mod.json','net/tompsen/nexuscharacters/SurfaceGeometry.class','net/tompsen/nexuscharacters/PosedSurfaceSupport.class'},changed
 assert len(added)==4 and all('/ExactSurface' in n and n.endswith('.class') for n in added),added
 assets=[n for n in a.namelist() if n.startswith('assets/')];assert all(a.read(n)==b.read(n) for n in assets)
 archive={'original_entries':len(a.namelist()),'identical_original_entries':len(a.namelist())-len(changed),'identical_assets':len(assets),'identical_original_classes':sum(n.endswith('.class') and a.read(n)==b.read(n) for n in a.namelist()),'changed':changed,'added':added,'removed':removed}
exports=['--add-exports=java.base/jdk.internal.org.objectweb.asm=ALL-UNNAMED','--add-exports=java.base/jdk.internal.org.objectweb.asm.tree=ALL-UNNAMED']
J=R.parent/'pre31/jdk/bin';classes=R/'build/tools'
subprocess.run([str(J/'javac'),*exports,'-d',str(classes),str(R/'tools/Verify.java'),str(R/'tools/Patch35.java'),str(R/'tools/Verify35.java')],check=True)
result=subprocess.check_output([str(J/'java'),*exports,'-cp',str(classes),'Verify35',str(base),str(new),str(Q/'NexusCharacters-PRE35-instrumented.jar')],text=True)
(Q/'bytecode.txt').write_text(result)
states={}
for filename,count in [('geometry.csv',320),('pose-geometry.csv',91),('multi-geometry.csv',96)]:
 a=(Q/'baseline'/filename).read_text().splitlines();b=(Q/'candidate'/filename).read_text().splitlines()
 assert a==b,filename+' geometry mismatch';assert len(a)==count,(filename,len(a));states[filename]=len(a)
images=[]
for p in sorted((Q/'baseline-evidence').glob('menu-[0-9][0-9].png')):
 q=Q/'evidence'/p.name
 with Image.open(p) as a,Image.open(q) as b:
  assert a.size==b.size and a.mode==b.mode and a.tobytes()==b.tobytes(),'Pixel mismatch '+p.name
  images.append({'file':p.name,'dimensions':a.size,'rgba_sha256':hashlib.sha256(a.convert('RGBA').tobytes()).hexdigest(),'baseline_png_sha256':hashlib.sha256(p.read_bytes()).hexdigest(),'candidate_png_sha256':hashlib.sha256(q.read_bytes()).hexdigest()})
assert len(images)==23
perf={}
for version in ['baseline','candidate']:
 rows=list(csv.DictReader((Q/version/'multi-perf.csv').open()));assert len(rows)==20
 for row in rows:assert int(row['recompute_count'])==int(row['players'])*int(row['frames'])
 perf[version]={name:{'cpu_ms_per_pose':statistics.median(int(r['cpu_ns'])/(int(r['players'])*int(r['frames']))/1e6 for r in rows if r['case']==name),'allocation_bytes_per_pose':statistics.median(int(r['allocated_bytes'])/(int(r['players'])*int(r['frames'])) for r in rows if r['case']==name)} for name in ['walking1','walking4','walking8','relative4']}
for kind in ['candidate','oracle']:
 for mode in ['world']:
  run=json.loads((Q/('last-'+kind+'-'+mode+'.json')).read_text())
  assert run['jar_sha256']==meta['oracle_sha256' if kind=='oracle' else 'jar_sha256']
for mode in ['menus','future','perf']:
 run=json.loads((Q/('last-candidate-'+mode+'.json')).read_text());assert run['jar_sha256']==meta['jar_sha256']
assert 'ORACLE35_PASS' in (Q/'oracle-world-evidence/oracle-counts.txt').read_text()
assert 'EXACT35_PASS sample_cases=24000 grid_modes=481' in (Q/'exact-geometry.txt').read_text()
timing=json.loads((Q/'final-perf-runs.json').read_text())
assert len(timing)==2 and timing[0]['run_id'].startswith('baseline-') and timing[1]['run_id'].startswith('candidate-')
assert timing[0]['end_utc']<=timing[1]['start_utc'],'Performance clients overlapped'
for name in ['walking1','walking4','walking8','relative4']:
 assert perf['candidate'][name]['cpu_ms_per_pose']<perf['baseline'][name]['cpu_ms_per_pose'],'No measured CPU improvement '+name
r={'build':meta,'archive':archive,'bytecode':result.strip(),'geometry_states':states,'screenshots':images,'multi_perf':perf,'runs':{p.name:json.loads(p.read_text()) for p in Q.glob('last-*.json')}}
(Q/'comparisons.json').write_text(json.dumps(r,indent=2,ensure_ascii=False)+'\n')
(Q/'screenshot-manifest.json').write_text(json.dumps(images,indent=2)+'\n')
print('COMPARE35_PASS states='+str(sum(states.values()))+' images='+str(len(images))+' assets='+str(len(assets)),flush=True)
