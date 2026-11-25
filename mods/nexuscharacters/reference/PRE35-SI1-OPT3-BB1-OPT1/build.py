from pathlib import Path
import hashlib,json,subprocess,tempfile,zipfile
R=Path(__file__).resolve().parent;P=R.parent/'presets_import';RT=P/'runtime';J=RT/'jdk/bin';BASE=P/'out/Nexus-BB1-1.21.11.jar'
assert hashlib.sha256(BASE.read_bytes()).hexdigest()=='a5f0c4a211a64503e30ffb93a6499a9172b7c22183542b18fc2e1a4cd231b10f'
for n in ['build','out','qa']:(R/n).mkdir(exist_ok=True)
work=Path(tempfile.mkdtemp(prefix='classes-',dir=R/'build'));classes=work/'patch';classes.mkdir()
asm=':'.join(map(str,(RT/'libs/org/ow2/asm').rglob('*.jar')))
subprocess.run([str(J/'javac'),'-cp',asm,'-d',str(work),str(R/'tools/PatchSurfaceOpt.java')],check=True)
subprocess.run([str(J/'java'),'-XX:-UsePerfData','-cp',str(work)+':'+asm,'PatchSurfaceOpt',str(BASE),str(classes)],check=True)
cp=':'.join(map(str,[classes,BASE,RT/'client-intermediary.jar',RT/'fabric-loader.jar',RT/'mods/skinlayers.jar',*(RT/'libs').rglob('*.jar'),*(P/'build/dependencies').glob('*.jar')]))
compiled=subprocess.run([str(J/'javac'),'-proc:none','-encoding','UTF-8','-cp',cp,'-d',str(classes),*map(str,(R/'src').rglob('*.java'))],capture_output=True,text=True)
if compiled.returncode:print(compiled.stderr);raise SystemExit(compiled.returncode)
patched={f.relative_to(classes).as_posix():f.read_bytes()for f in classes.rglob('*.class')};target=R/'out/Nexus-BB1-OPT1-1.21.11.jar'
with zipfile.ZipFile(BASE)as old,zipfile.ZipFile(target,'w')as new:
 for info in old.infolist():
  data=patched.pop(info.filename,old.read(info))
  if info.filename=='fabric.mod.json':
   meta=json.loads(data);meta['version']='1.0.0-beta.35+indivis.1.21.11.SI1.OPT3.BB1.OPT1';meta['description']+=' | BB1.OPT1: exact lower-allocation animated surfaces';data=json.dumps(meta,ensure_ascii=False,indent=2).encode()
  new.writestr(info,data)
 for name,data in sorted(patched.items()):
  info=zipfile.ZipInfo(name,(2026,11,25,0,0,0));info.compress_type=zipfile.ZIP_DEFLATED;new.writestr(info,data)
with zipfile.ZipFile(BASE)as old,zipfile.ZipFile(target)as new:
 assert new.testzip()is None
 changed=[n for n in old.namelist()if old.read(n)!=new.read(n)];added=sorted(set(new.namelist())-set(old.namelist()))
 assert all(old.read(n)==new.read(n)for n in old.namelist()if n.startswith('assets/'))
record={'jar':str(target),'sha256':hashlib.sha256(target.read_bytes()).hexdigest(),'base_sha256':hashlib.sha256(BASE.read_bytes()).hexdigest(),'changed':changed,'added':added,'classes':str(classes),'classpath':cp}
(R/'qa/build.json').write_text(json.dumps(record,indent=2)+'\n');print('SURFACE_OPT_BUILD_PASS',record['sha256'],changed)
