"""Deterministic, narrowly scoped OPT1 -> OPT2 build. No asset edits."""
from pathlib import Path
import hashlib,json,os,subprocess,tempfile,zipfile

R=Path(__file__).resolve().parent;RT=R/'runtime';J=RT/'jdk/bin'
BASE=Path(os.environ.get('NEXUS_OPT1_JAR',str(R.parent/'analysis-opt1/inputs/NexusCharacters-Indivis-1.21.11-PRE35-SI1-OPT1(1).jar'))).resolve()
BASE_SHA='bdb1525d5e4448cb8127e38bdcdce063d629b7039a856bf9ea74be5ea06dea11'
assert hashlib.sha256(BASE.read_bytes()).hexdigest()==BASE_SHA
(R/'build').mkdir(exist_ok=True);(R/'qa').mkdir(exist_ok=True);(R/'out').mkdir(exist_ok=True)
work=Path(tempfile.mkdtemp(prefix='classes-',dir=R/'build'));patch=work/'patch';patch.mkdir()
asm=':'.join(map(str,(RT/'libs/org/ow2/asm').rglob('*.jar')))
subprocess.run([str(J/'javac'),'-cp',asm,'-d',str(work),str(R/'tools/PatchOpt2.java')],check=True)
subprocess.run([str(J/'java'),'-cp',str(work)+':'+asm,'PatchOpt2',str(BASE),str(patch)],check=True)
cp=':'.join(map(str,[BASE,RT/'joml-1.10.8.jar',RT/'client-intermediary.jar',RT/'mods/skinlayers.jar']))
sources=[R/'src/net/tompsen/nexuscharacters'/f for f in ('CellQueryCache.java','SpatialSurfaceCleaner.java','RenderClassLookup.java')]
subprocess.run([str(J/'javac'),'-proc:none','-encoding','UTF-8','-cp',cp,'-d',str(patch),*map(str,sources)],check=True)
patches={p.relative_to(patch).as_posix():p.read_bytes() for p in patch.rglob('*.class')}
target=R/'out/NexusCharacters-Indivis-1.21.11-PRE35-SI1-OPT2.jar'
temporary=target.with_suffix('.building')
with zipfile.ZipFile(BASE) as src,zipfile.ZipFile(temporary,'w') as dst:
    for info in src.infolist():
        data=src.read(info)
        if info.filename in patches:data=patches.pop(info.filename)
        if info.filename=='fabric.mod.json':
            meta=json.loads(data);meta['version']='1.0.0-beta.35+indivis.1.21.11.SI1.OPT2'
            meta['description']=meta['description']+' | OPT2: exact query cache, local clipping buffers and cached class resolution.'
            data=json.dumps(meta,ensure_ascii=False,indent=2).encode()
        dst.writestr(info,data)
    for name,data in sorted(patches.items()):
        info=zipfile.ZipInfo(name,(2026,10,27,0,0,0));info.compress_type=zipfile.ZIP_DEFLATED;dst.writestr(info,data)
with zipfile.ZipFile(BASE) as old,zipfile.ZipFile(temporary) as new:
    assert new.testzip() is None
    changed=sorted(n for n in old.namelist() if old.read(n)!=new.read(n));added=sorted(set(new.namelist())-set(old.namelist()))
    allowed=['fabric.mod.json','net/tompsen/nexuscharacters/GenericSkinLayerSupport.class','net/tompsen/nexuscharacters/LowerJointSupport.class']
    assert all(n in allowed or n.startswith('net/tompsen/nexuscharacters/SpatialSurfaceCleaner') for n in changed),changed
    assert set(added)=={'net/tompsen/nexuscharacters/CellQueryCache.class','net/tompsen/nexuscharacters/RenderClassLookup.class'},added
    assets=[n for n in old.namelist() if n.startswith('assets/')]
    assert all(old.read(n)==new.read(n) for n in assets)
temporary.replace(target)
record={'jar':str(target),'sha256':hashlib.sha256(target.read_bytes()).hexdigest(),'base_sha256':BASE_SHA,'changed_entries':changed,'added_entries':added,'unchanged_assets':len(assets),'classes':str(patch)}
(R/'qa/build.json').write_text(json.dumps(record,indent=2)+'\n')
print('OPT2_BUILD_PASS',json.dumps(record),flush=True)
