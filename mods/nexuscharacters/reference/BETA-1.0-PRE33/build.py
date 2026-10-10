from pathlib import Path
import subprocess, zipfile, json, hashlib

R = Path(__file__).resolve().parent
RT = R.parent / 'pre31/runtime'
J = R.parent / 'pre31/jdk/bin'
BASE = R / 'base/NexusCharacters-PRE32.jar'
HUD = next((R/'hud-base').rglob('*.jar'))
assert hashlib.sha256(BASE.read_bytes()).hexdigest() == '203fb5e54098836c34a375723a8ce234c5aa884739f4a41113120db5a5465032'
cp = ':'.join(map(str, [BASE, RT/'client-intermediary.jar', RT/'skinlayers.jar', *sorted((RT/'libs').rglob('*.jar'))]))
(R/'compile-cp.txt').write_text(cp)
exports = ['--add-exports=java.base/jdk.internal.org.objectweb.asm=ALL-UNNAMED', '--add-exports=java.base/jdk.internal.org.objectweb.asm.tree=ALL-UNNAMED']
(R/'tools/classes').mkdir(parents=True, exist_ok=True)
(R/'classes').mkdir(exist_ok=True)
subprocess.run([str(J/'javac'), *exports, '-d', str(R/'tools/classes'), str(R/'tools/Patch33.java')], check=True)
subprocess.run([str(J/'java'), *exports, '-cp', str(R/'tools/classes')+':'+cp, 'Patch33', str(BASE), str(R/'classes'),str(HUD),str(R/'hud-classes')], check=True)
subprocess.run([str(J/'javac'), '-proc:none', '-cp', str(R/'classes')+':'+cp, '-d', str(R/'classes'), *map(str,(p for p in (R/'src').rglob('*.java') if p.name!='FirstPersonSurfaceSupport.java'))], check=True)
with zipfile.ZipFile(BASE) as z: entries = {n:z.read(n) for n in z.namelist()}
for f in (R/'classes').rglob('*.class'): entries[f.relative_to(R/'classes').as_posix()] = f.read_bytes()
meta = json.loads(entries['fabric.mod.json'])
meta['version'] = '1.0.0-beta.33+indivis.1.21.11'
meta['description'] = 'Indivis PRE33: exact full-model surface reuse and cached optional field lookup.'
entries['fabric.mod.json'] = json.dumps(meta, ensure_ascii=False, indent=2).encode()
out = R/'NexusCharacters-Indivis-1.21.11-BETA-1.0-PRE33.jar'
temporary = out.with_suffix('.building')
with zipfile.ZipFile(temporary,'w',zipfile.ZIP_DEFLATED) as z:
    for n,b in sorted(entries.items()):
        info=zipfile.ZipInfo(n,(2026,10,10,0,0,0));info.compress_type=zipfile.ZIP_DEFLATED;z.writestr(info,b)
with zipfile.ZipFile(temporary) as z: assert z.testzip() is None
temporary.replace(out)
print('PRE33_CANDIDATE',hashlib.sha256(out.read_bytes()).hexdigest())

(R/'hud-classes').mkdir(exist_ok=True)
subprocess.run([str(J/'javac'),'-proc:none','-d',str(R/'hud-classes'),*map(str,(R/'hud-src').rglob('*.java'))],check=True)
with zipfile.ZipFile(HUD) as z: hud_entries={n:z.read(n) for n in z.namelist()}
for f in (R/'hud-classes').rglob('*.class'):hud_entries[f.relative_to(R/'hud-classes').as_posix()]=f.read_bytes()
meta=json.loads(hud_entries['fabric.mod.json']);meta['version']='1.3.3-beta.1+race-health-modifier-compat.opt1'
hud_entries['fabric.mod.json']=json.dumps(meta,ensure_ascii=False,indent=2).encode()
hud_out=R/'capitale_rp_hud_BETA_1_3_3_OPT1_1.21.11.jar'
with zipfile.ZipFile(hud_out,'w',zipfile.ZIP_DEFLATED) as z:
 for n,b in sorted(hud_entries.items()):
  info=zipfile.ZipInfo(n,(2026,10,10,0,0,0));info.compress_type=zipfile.ZIP_DEFLATED;z.writestr(info,b)
with zipfile.ZipFile(hud_out) as z:assert z.testzip() is None
print('HUD_OPT1_CANDIDATE',hashlib.sha256(hud_out.read_bytes()).hexdigest())
