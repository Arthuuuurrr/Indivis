from pathlib import Path
import subprocess, zipfile, json, hashlib, tempfile
R=Path(__file__).resolve().parent
BASE=R.parent/'opt-bases/NexusCharacters-Indivis-1.21.11-PRE35-SI1.jar'
RT=R.parent/'pre31/runtime'; J=R.parent/'pre31/jdk/bin'
BASE_SHA='399a4c64a7672e60ebbaaa52044cb547f5c41576039fe5aafe2c42485e2e8a8a'
assert hashlib.sha256(BASE.read_bytes()).hexdigest()==BASE_SHA
(R/'build').mkdir(exist_ok=True);(R/'qa').mkdir(exist_ok=True)
work=Path(tempfile.mkdtemp(prefix='classes-',dir=R/'build'));patch=work/'patch';patch.mkdir()
exports=['--add-exports=java.base/jdk.internal.org.objectweb.asm=ALL-UNNAMED','--add-exports=java.base/jdk.internal.org.objectweb.asm.tree=ALL-UNNAMED']
subprocess.run([str(J/'javac'),*exports,'-d',str(work),str(R/'tools/PatchOpt1.java')],check=True)
subprocess.run([str(J/'java'),*exports,'-cp',str(work),'PatchOpt1',str(BASE),str(patch)],check=True)
joml=next((RT/'libs').rglob('joml-1.10.8.jar'))
cp=':'.join(map(str,[patch,BASE,joml,RT/'client-intermediary.jar',RT/'skinlayers.jar']))
subprocess.run([str(J/'javac'),'-proc:none','-encoding','UTF-8','-cp',cp,'-d',str(patch),*map(str,(R/'src').rglob('*.java'))],check=True)
target=R/'NexusCharacters-Indivis-1.21.11-PRE35-SI1-OPT1.jar';temporary=target.with_suffix('.building')
patches={p.relative_to(patch).as_posix():p.read_bytes() for p in patch.rglob('*.class')}
changed=[]
with zipfile.ZipFile(BASE) as src,zipfile.ZipFile(temporary,'w') as dst:
 for info in src.infolist():
  data=src.read(info)
  if info.filename in patches:data=patches.pop(info.filename);changed.append(info.filename)
  if info.filename=='fabric.mod.json':
   meta=json.loads(data);meta['version']='1.0.0-beta.35+indivis.1.21.11.SI1.OPT1'
   meta['description']=str(meta.get('description',''))+' | OPT1: exact indexed surface preparation.'
   data=json.dumps(meta,ensure_ascii=False,indent=2).encode();changed.append(info.filename)
  dst.writestr(info,data)
 for name,data in sorted(patches.items()):
  info=zipfile.ZipInfo(name,(2026,10,26,0,0,0));info.compress_type=zipfile.ZIP_DEFLATED;dst.writestr(info,data);changed.append(name)
with zipfile.ZipFile(BASE) as old,zipfile.ZipFile(temporary) as new:
 assert new.testzip() is None
 unexpected=[n for n in old.namelist() if old.read(n)!=new.read(n) and n not in changed]
 assert not unexpected
 assert all(not n.startswith('assets/') for n in changed)
temporary.replace(target)
record={'jar':target.name,'sha256':hashlib.sha256(target.read_bytes()).hexdigest(),'base_sha256':BASE_SHA,'changed_entries':changed,'classes':str(patch),'byte_identical_assets':True}
(R/'qa/build.json').write_text(json.dumps(record,indent=2)+'\n')
print('OPT1_BUILD_PASS',json.dumps(record),flush=True)
