from pathlib import Path
import zipfile,io,base64,json,hashlib,shutil
R=Path(__file__).resolve().parents[1];jar=R/'NexusCharacters-Indivis-1.21.11-BETA-1.0-PRE31.jar';buffer=io.BytesIO();patch_entries={}
with zipfile.ZipFile(buffer,'w',zipfile.ZIP_DEFLATED) as z:
 for p in sorted((R/'classes').rglob('*.class')):
  n=p.relative_to(R/'classes').as_posix();b=p.read_bytes();patch_entries[n]=hashlib.sha256(b).hexdigest();info=zipfile.ZipInfo(n,(2026,10,10,0,0,0));info.compress_type=zipfile.ZIP_DEFLATED;z.writestr(info,b)
patch=buffer.getvalue();(R/'compiled-patches.zip.b64').write_text(base64.b64encode(patch).decode()+'\n')
manifest={'base_sha256':'1c164be6dc8eef9372120ae384b65c5d2be8ebaa7fed67461b986726a5cff922','candidate_sha256':hashlib.sha256(jar.read_bytes()).hexdigest(),'patch_sha256':hashlib.sha256(patch).hexdigest(),'patch_entries':patch_entries,'version':'1.0.0-beta.31+indivis.1.21.11','description':'Indivis PRE31: primitive surface serialization and first-person local geometry reuse.'}
(R/'build-manifest.json').write_text(json.dumps(manifest,indent=2)+'\n')
D=R/'delivery';D.mkdir(exist_ok=True);shutil.copy2(jar,D/jar.name);shutil.copy2(R/'PRE31-verifications.md',D/'PRE31-verifications.md')
with zipfile.ZipFile(D/'NexusCharacters-PRE31.zip','w',zipfile.ZIP_DEFLATED) as z:
 for p in [D/jar.name,D/'PRE31-verifications.md']:z.write(p,p.name)
with zipfile.ZipFile(D/'NexusCharacters-PRE31.zip') as z:assert z.testzip() is None;assert z.read(jar.name)==jar.read_bytes()
print('PACKAGE_PASS',manifest['candidate_sha256'])
