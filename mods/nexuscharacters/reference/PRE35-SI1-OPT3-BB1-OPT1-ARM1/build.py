from pathlib import Path
import hashlib,json,subprocess,tempfile,zipfile
R=Path(__file__).resolve().parent;P=R.parent/'presets_import';RT=P/'runtime';J=RT/'jdk/bin';BASE=R.parent/'opt_bb1_20261125/out/Nexus-BB1-OPT1-1.21.11.jar'
assert hashlib.sha256(BASE.read_bytes()).hexdigest()=='5017ec3f87d4d72053855341050ada3bd80f262e4238c32d0da1bc982842f5e3'
for n in ['build','out','qa']:(R/n).mkdir(exist_ok=True)
work=Path(tempfile.mkdtemp(prefix='classes-',dir=R/'build'));classes=work/'patch';classes.mkdir()
asm=':'.join(map(str,(RT/'libs/org/ow2/asm').rglob('*.jar')))
subprocess.run([str(J/'javac'),'-cp',asm,'-d',str(work),str(R/'tools/PatchArmorPrepared.java')],check=True)
subprocess.run([str(J/'java'),'-XX:-UsePerfData','-cp',str(work)+':'+asm,'PatchArmorPrepared',str(BASE),str(classes)],check=True)
cp=':'.join(map(str,[classes,BASE,RT/'client-intermediary.jar',RT/'fabric-loader.jar',RT/'mods/skinlayers.jar',*(RT/'libs').rglob('*.jar'),*(P/'build/dependencies').glob('*.jar')]))
c=subprocess.run([str(J/'javac'),'-proc:none','-encoding','UTF-8','-cp',cp,'-d',str(classes),*map(str,(R/'src').rglob('*.java'))],capture_output=True,text=True)
if c.returncode:print(c.stderr);raise SystemExit(c.returncode)
patched={f.relative_to(classes).as_posix():f.read_bytes()for f in classes.rglob('*.class')};target=R/'out/Nexus-ARM1-1.21.11.jar'
with zipfile.ZipFile(BASE)as old,zipfile.ZipFile(target,'w')as new:
 for info in old.infolist():
  data=patched.pop(info.filename,old.read(info))
  if info.filename=='fabric.mod.json':
   m=json.loads(data);m['version']='1.0.0-beta.35+indivis.1.21.11.SI1.OPT3.BB1.OPT1.ARM1';m['description']+=' | ARM1: immutable prepared parts and conservative animated contact regions';data=json.dumps(m,ensure_ascii=False,indent=2).encode()
  new.writestr(info,data)
 for name,data in sorted(patched.items()):
  info=zipfile.ZipInfo(name,(2026,11,25,0,0,0));info.compress_type=zipfile.ZIP_DEFLATED;new.writestr(info,data)
with zipfile.ZipFile(BASE)as old,zipfile.ZipFile(target)as new:
 assert new.testzip()is None
 changed=[n for n in old.namelist()if old.read(n)!=new.read(n)];added=sorted(set(new.namelist())-set(old.namelist()));assert all(old.read(n)==new.read(n)for n in old.namelist()if n.startswith('assets/'))
b={'jar':str(target),'sha256':hashlib.sha256(target.read_bytes()).hexdigest(),'base_sha256':hashlib.sha256(BASE.read_bytes()).hexdigest(),'changed':changed,'added':added,'classes':str(classes),'classpath':cp}
(R/'qa/build.json').write_text(json.dumps(b,indent=2)+'\n');print('ARMOR_PREPARED_BUILD_PASS',b['sha256'],changed)
