from pathlib import Path
import json,zipfile,hashlib,base64,io
R=Path(__file__).resolve().parents[1];prefix='mods/nexuscharacters/reference/BETA-1.0-PRE35/'
# Persist a byte-reproducible patch before the expensive client tests.
base=R/'base/NexusCharacters-Indivis-1.21.11-PRE34-SI1.jar';new=R/'NexusCharacters-Indivis-1.21.11-PRE35-SI1.jar'
with zipfile.ZipFile(base) as a,zipfile.ZipFile(new) as b:
 changes={n:b.read(n) for n in b.namelist() if n not in a.namelist() or a.read(n)!=b.read(n)}
 stream=io.BytesIO()
 with zipfile.ZipFile(stream,'w',zipfile.ZIP_DEFLATED) as z:
  for n,data in sorted(changes.items()):z.writestr(zipfile.ZipInfo(n,(2026,10,25,0,0,0)),data)
 (R/'compiled-patches.zip.b64').write_text(base64.b64encode(stream.getvalue()).decode()+'\n')
 manifest={'base_filename':base.name,'base_sha256':hashlib.sha256(base.read_bytes()).hexdigest(),'output_filename':new.name,'output_sha256':hashlib.sha256(new.read_bytes()).hexdigest(),'patch_zip_sha256':hashlib.sha256(stream.getvalue()).hexdigest(),'entries':{n:hashlib.sha256(data).hexdigest() for n,data in changes.items()}}
 (R/'build-manifest.json').write_text(json.dumps(manifest,indent=2)+'\n')
files=[R/'build.py',R/'run_client.py',R/'build-manifest.json',R/'compiled-patches.zip.b64',R/'qa/build-audit.json',R/'qa/exact-geometry.txt']
files+=list((R/'src').rglob('*.java'))+list((R/'tests').rglob('*.java'))+list((R/'tools').glob('*.py'))+list((R/'tools').glob('*.java'))
print(json.dumps([{'path':prefix+p.relative_to(R).as_posix(),'mode':'100644','type':'blob','content':p.read_text()} for p in files],ensure_ascii=False))
