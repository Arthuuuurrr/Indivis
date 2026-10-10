from pathlib import Path
import subprocess,zipfile,json,hashlib
R=Path(__file__).resolve().parent; BASE=R/'base/NexusCharacters-Indivis-1.21.11-BETA-1.0-PRE29.jar';J=R/'jdk/bin'
assert hashlib.sha256(BASE.read_bytes()).hexdigest()=='b09135fd42719bcf0e4753db22615db3bce4c8ff31783ff01a5ad8fdf202e96c'
cp=':'.join(map(str,[BASE,R/'runtime/client-intermediary.jar',R/'runtime/skinlayers.jar',*sorted((R/'runtime/libs').rglob('*.jar'))]));(R/'compile-cp.txt').write_text(cp)
exports=['--add-exports=java.base/jdk.internal.org.objectweb.asm=ALL-UNNAMED','--add-exports=java.base/jdk.internal.org.objectweb.asm.tree=ALL-UNNAMED']
(R/'tools/classes').mkdir(exist_ok=True);subprocess.run([str(J/'javac'),*exports,'-d',str(R/'tools/classes'),str(R/'tools/Patch.java')],check=True)
(R/'classes').mkdir(exist_ok=True)
subprocess.run([str(J/'java'),*exports,'-cp',str(R/'tools/classes')+':'+cp,'Patch',str(BASE),str(R/'classes')],check=True)
subprocess.run([str(J/'javac'),'-proc:none','-cp',str(R/'classes')+':'+cp,'-d',str(R/'classes'),*map(str,(R/'src').rglob('*.java'))],check=True)
with zipfile.ZipFile(BASE) as z:entries={n:z.read(n) for n in z.namelist()}
for f in (R/'classes').rglob('*.class'):entries[f.relative_to(R/'classes').as_posix()]=f.read_bytes()
meta=json.loads(entries['fabric.mod.json']);meta['version']='1.0.0-beta.30+indivis.1.21.11';meta['description']='Indivis PRE30: cached render reflection and reusable pose scratch buffers.';entries['fabric.mod.json']=json.dumps(meta,ensure_ascii=False,indent=2).encode()
out=R/'NexusCharacters-Indivis-1.21.11-BETA-1.0-PRE30.jar'
temporary=out.with_suffix('.building')
with zipfile.ZipFile(temporary,'w',zipfile.ZIP_DEFLATED) as z:
 for n,b in sorted(entries.items()):
  info=zipfile.ZipInfo(n,(2026,10,9,0,0,0));info.compress_type=zipfile.ZIP_DEFLATED;z.writestr(info,b)
with zipfile.ZipFile(temporary) as z:assert z.testzip() is None
temporary.replace(out)
print('PRE30_CANDIDATE',hashlib.sha256(out.read_bytes()).hexdigest())
