from pathlib import Path
import hashlib,json,subprocess,zipfile,shutil
R=Path(__file__).resolve().parent
RT=R.parent/'pre31/runtime';J=R.parent/'pre31/jdk/bin'
BASE=R/'base/NexusCharacters-Indivis-1.21.11-PRE34-SI1.jar'
BASE_SHA='454bd4206b50737bf386d65eb09f4f0e3ad1294a1fd4e6df426f9fe205cb31d0'
assert hashlib.sha256(BASE.read_bytes()).hexdigest()==BASE_SHA,'Wrong PRE34 SI1 base'
JOML=next((RT/'libs').rglob('joml-1.10.8.jar'))
exports=['--add-exports=java.base/jdk.internal.org.objectweb.asm=ALL-UNNAMED','--add-exports=java.base/jdk.internal.org.objectweb.asm.tree=ALL-UNNAMED']
tools=R/'build/tools';tools.mkdir(parents=True,exist_ok=True)
subprocess.run([str(J/'javac'),*exports,'-d',str(tools),str(R/'tools/Patch35.java')],check=True)
prod=R/'build/production';prod.mkdir(parents=True,exist_ok=True)
subprocess.run([str(J/'javac'),'-proc:none','-cp',str(BASE)+':'+str(JOML),'-d',str(prod),*map(str,(R/'src').rglob('*.java'))],check=True)
qa=R/'build/oracle';qa.mkdir(parents=True,exist_ok=True)
subprocess.run([str(J/'java'),*exports,'-cp',str(tools),'Patch35',str(BASE),str(prod)],check=True)
subprocess.run([str(J/'java'),*exports,'-cp',str(tools),'Patch35',str(BASE),str(qa),'oracle'],check=True)
for p in prod.rglob('ExactSurface*.class'):
 d=qa/p.relative_to(prod);d.parent.mkdir(parents=True,exist_ok=True);shutil.copy2(p,d)
def archive(target,patchdir):
 patches={p.relative_to(patchdir).as_posix():p.read_bytes() for p in patchdir.rglob('*.class')}
 temporary=target.with_suffix('.building')
 with zipfile.ZipFile(BASE) as src,zipfile.ZipFile(temporary,'w') as dst:
  for info in src.infolist():
   data=patches.pop(info.filename,src.read(info))
   if info.filename=='fabric.mod.json':
    meta=json.loads(data);meta['version']='1.0.0-beta.35+indivis.1.21.11.SI1'
    meta['description']=str(meta.get('description',''))+' | PRE35 SI1: exact surface clipping and per-pass query reuse.'
    data=json.dumps(meta,ensure_ascii=False,indent=2).encode()
   dst.writestr(info,data)
  for name,data in sorted(patches.items()):
   info=zipfile.ZipInfo(name,(2026,10,25,0,0,0));info.compress_type=zipfile.ZIP_DEFLATED
   dst.writestr(info,data)
 with zipfile.ZipFile(temporary) as z:assert z.testzip() is None
 temporary.replace(target)
target=R/'NexusCharacters-Indivis-1.21.11-PRE35-SI1.jar'
archive(target,prod)
instrumented=R/'qa/NexusCharacters-PRE35-instrumented.jar';instrumented.parent.mkdir(exist_ok=True)
archive(instrumented,qa)
subprocess.run([str(J/'javac'),'-proc:none','-cp',str(instrumented)+':'+str(JOML),'-d',str(qa),str(R/'tests/net/tompsen/nexuscharacters/SurfaceComparisonOracle.java')],check=True)
archive(instrumented,qa)
audit={'base_sha256':BASE_SHA,'jar_sha256':hashlib.sha256(target.read_bytes()).hexdigest(),'oracle_sha256':hashlib.sha256(instrumented.read_bytes()).hexdigest(),'version':'1.0.0-beta.35+indivis.1.21.11.SI1'}
(R/'qa/build-audit.json').write_text(json.dumps(audit,indent=2)+'\n')
print('BUILD35_PASS',json.dumps(audit),flush=True)
