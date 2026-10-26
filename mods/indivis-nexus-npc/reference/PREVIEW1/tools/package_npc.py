from pathlib import Path
import hashlib,json,zipfile,shutil
R=Path(__file__).resolve().parents[1];OUT=R/'release';OUT.mkdir(exist_ok=True)
jar=R/'Indivis-Nexus-NPC-0.1.0-PREVIEW1-1.21.11.jar';manifest=json.loads((R/'qa/build.json').read_text())
assert hashlib.sha256(jar.read_bytes()).hexdigest()==manifest['sha256']=='cc9cfb403e48c21a22d14e1bcc15152d3ae74dc86d5ab9ddae9a926d35836047'
for mode in ['npc','reload','future','menus','visual']:
 record=json.loads((R/('qa/last-'+mode+'.json')).read_text());assert record['jar_sha256']==manifest['sha256'];assert (R/('qa/evidence-'+mode)/'pass.txt').exists()
assert not json.loads((R/'qa/comparison.json').read_text())['failures']
entries={}
for name in ['GUIDE-PNJ-NEXUS.md','RAPPORT-VERIFICATION.md']:entries[name]=(R/name).read_bytes()
entries['mods/'+jar.name]=jar.read_bytes()
with zipfile.ZipFile(jar) as z:
 for name in z.namelist():
  if name.endswith('.npc.snbt'):entries['presets-easynpc/'+Path(name).name]=z.read(name)
entries['apercus/presets-face.png']=(R/'qa/evidence-npc/presets-face.png').read_bytes()
entries['apercus/presets-dos.png']=(R/'qa/evidence-visual/presets-dos.png').read_bytes()
for name in ['build.json','comparison.json','jar-audit.json','npc-cpu-summary.json']:entries['verification/'+name]=(R/'qa'/name).read_bytes()
entries['verification/npc-cpu.csv']=(R/'qa/evidence-reload/npc-cpu.csv').read_bytes()
for mode in ['npc','reload','future','visual']:entries['verification/'+mode+'-pass.txt']=(R/('qa/evidence-'+mode)/'pass.txt').read_bytes()
entries['SHA256SUMS.txt']=(''.join(hashlib.sha256(data).hexdigest()+'  '+name+'\n' for name,data in sorted(entries.items()))).encode()
target=OUT/'Indivis-PNJ-Nexus-PREVIEW1.zip'
with zipfile.ZipFile(target,'w') as z:
 for name,data in sorted(entries.items()):
  info=zipfile.ZipInfo(name,(2026,10,26,0,0,0));info.compress_type=zipfile.ZIP_DEFLATED;z.writestr(info,data)
with zipfile.ZipFile(target) as z:
 assert z.testzip() is None
 assert z.read('mods/'+jar.name)==jar.read_bytes()
 assert len([n for n in z.namelist() if n.endswith('.npc.snbt')])==5
shutil.copy2(jar,OUT/jar.name)
shutil.copy2(R/'RAPPORT-VERIFICATION.md',OUT/'RAPPORT-VERIFICATION.md')
print(json.dumps({'zip':str(target),'zip_sha256':hashlib.sha256(target.read_bytes()).hexdigest(),'zip_bytes':target.stat().st_size,'jar':str(OUT/jar.name),'report':str(OUT/'RAPPORT-VERIFICATION.md'),'entries':len(entries)}))
