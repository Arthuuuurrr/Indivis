"""Deterministic OPT2 -> OPT3 candidate; all non-target entries untouched."""
from pathlib import Path
import hashlib,json,os,subprocess,tempfile,zipfile
R=Path(__file__).resolve().parent;RT=R.parent/'opt2/runtime';J=RT/'jdk/bin'
BASE=R.parent/'opt2/out/NexusCharacters-Indivis-1.21.11-PRE35-SI1-OPT2.jar'
BASE_SHA='7491ec3feb751b37ff0fe3773b4bb6d75c631220913c97b8e22143d9356fc116'
assert hashlib.sha256(BASE.read_bytes()).hexdigest()==BASE_SHA
for p in ['build','qa','out']:(R/p).mkdir(exist_ok=True)
work=Path(tempfile.mkdtemp(prefix='classes-',dir=R/'build'));patch=work/'patch';patch.mkdir()
asm=':'.join(map(str,(RT/'libs/org/ow2/asm').rglob('*.jar')))
subprocess.run([str(J/'javac'),'-cp',asm,'-d',str(work),str(R/'tools/PatchOpt3.java')],check=True)
compact=os.environ.get('OPT3_COMPACT','true')=='true'
volumes=os.environ.get('OPT3_VOLUMES','true')=='true'
subprocess.run([str(J/'java'),'-Dopt3.compact='+str(compact).lower(),'-Dopt3.volumes='+str(volumes).lower(),'-cp',str(work)+':'+asm,'PatchOpt3',str(BASE),str(patch)],check=True)
cp=':'.join(map(str,[patch,BASE,RT/'joml-1.10.8.jar',RT/'client-intermediary.jar',RT/'mods/skinlayers.jar']))
sources=[]
if volumes:sources.extend([R/'src/net/tompsen/nexuscharacters/PartVolumeCache.java',R/'src/net/tompsen/nexuscharacters/SpatialSurfaceCleaner.java'])
if compact:sources.append(R/'src/net/tompsen/nexuscharacters/CompactSurfaceClipper.java')
subprocess.run([str(J/'javac'),'-proc:none','-encoding','UTF-8','-cp',cp,'-d',str(patch),*map(str,sources)],check=True)
patches={p.relative_to(patch).as_posix():p.read_bytes() for p in patch.rglob('*.class')}
# Only the Index constructor changes. Other freshly compiled helper classes
# are not shipped; OPT2's exact cleaner and fragment logic stay byte-identical.
for name in list(patches):
    if 'SpatialSurfaceCleaner' in name and not name.endswith('SpatialSurfaceCleaner$Index.class'):del patches[name]
filename=os.environ.get('OPT3_FILENAME','NexusCharacters-Indivis-1.21.11-PRE35-SI1-OPT3.jar')
assert Path(filename).name==filename
target=R/'out'/filename
temporary=target.with_suffix('.building')
with zipfile.ZipFile(BASE) as src,zipfile.ZipFile(temporary,'w') as dst:
    for info in src.infolist():
        data=src.read(info)
        if info.filename in patches:data=patches.pop(info.filename)
        if info.filename=='fabric.mod.json':
            meta=json.loads(data);meta['version']='1.0.0-beta.35+indivis.1.21.11.SI1.OPT3'
            if volumes:meta['description']+=' | OPT3: bounded exact per-part volume reuse.'
            else:meta['description']+=' | OPT3: '+('exact clipping scratch; ' if compact else '')+'OPT2 volume construction retained.'
            data=json.dumps(meta,ensure_ascii=False,indent=2).encode()
        dst.writestr(info,data)
    for name,data in sorted(patches.items()):
        info=zipfile.ZipInfo(name,(2026,10,28,0,0,0));info.compress_type=zipfile.ZIP_DEFLATED;dst.writestr(info,data)
with zipfile.ZipFile(BASE) as old,zipfile.ZipFile(temporary) as new:
    assert new.testzip() is None
    changed=sorted(n for n in old.namelist() if old.read(n)!=new.read(n));added=sorted(set(new.namelist())-set(old.namelist()))
    assets=[n for n in old.namelist() if n.startswith('assets/')]
    assert all(old.read(n)==new.read(n) for n in assets)
temporary.replace(target)
record={'jar':str(target),'sha256':hashlib.sha256(target.read_bytes()).hexdigest(),'base_sha256':BASE_SHA,'changed_entries':changed,'added_entries':added,'unchanged_assets':len(assets),'classes':str(patch)}
record['compact']=compact
record['volumes']=volumes
recordname=os.environ.get('OPT3_BUILD_RECORD','build.json');assert Path(recordname).name==recordname
(R/'qa'/recordname).write_text(json.dumps(record,indent=2)+'\n')
print('OPT3_BUILD_PASS',json.dumps(record),flush=True)
