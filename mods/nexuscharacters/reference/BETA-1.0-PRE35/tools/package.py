from pathlib import Path
import json,hashlib,zipfile,subprocess,shutil
R=Path(__file__).resolve().parents[1];Q=R/'qa';D=R/'delivery';D.mkdir(exist_ok=True)
checks=json.loads((Q/'comparisons.json').read_text())
subprocess.run(['python3',str(R/'tools/checkpoint_payload.py')],check=True,stdout=subprocess.DEVNULL)
base=R/'base/NexusCharacters-Indivis-1.21.11-PRE34-SI1.jar';jar=R/'NexusCharacters-Indivis-1.21.11-PRE35-SI1.jar'
assert hashlib.sha256(jar.read_bytes()).hexdigest()==checks['build']['jar_sha256']
subprocess.run(['python3',str(R/'assemble.py'),str(base),str(Q/'rebuilt.jar')],check=True)
assert (Q/'rebuilt.jar').read_bytes()==jar.read_bytes()
for p in [jar,R/'LISEZ-MOI-PRE35.txt',R/'PRE35-verifications.md']:shutil.copy2(p,D/p.name)
bundle=D/'Indivis-Nexus-PRE35-SI1.zip'
with zipfile.ZipFile(bundle,'w',zipfile.ZIP_DEFLATED) as z:
 for name in [jar.name,'LISEZ-MOI-PRE35.txt','PRE35-verifications.md']:
  info=zipfile.ZipInfo(name,(2026,10,25,0,0,0));info.compress_type=zipfile.ZIP_DEFLATED;z.writestr(info,(D/name).read_bytes())
with zipfile.ZipFile(bundle) as z:assert z.testzip() is None
manifest=[{'file':p.name,'size_bytes':p.stat().st_size,'sha256':hashlib.sha256(p.read_bytes()).hexdigest()} for p in [D/jar.name,bundle,D/'PRE35-verifications.md']]
(Q/'delivery-manifest.json').write_text(json.dumps(manifest,indent=2)+'\n')
print('PACKAGE35_PASS',json.dumps(manifest),flush=True)
