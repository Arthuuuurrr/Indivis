import hashlib,json,zipfile,sys
from pathlib import Path
root=Path(__file__).resolve().parent
if len(sys.argv)<2:raise SystemExit('Usage: python assemble.py PRE21.jar [output.jar]')
base=Path(sys.argv[1])
assert hashlib.sha256(base.read_bytes()).hexdigest()=='44845c16f6e61849fc30b18305f4eb728f0e26a2fff06b0109a14cbc4885c018'
out=Path(sys.argv[2]) if len(sys.argv)>2 else root/'NexusCharacters-HauteCapitale-1.21.11-BETA-1.0-PRE22.jar'
patches={p.relative_to(root/'classes').as_posix():p.read_bytes() for p in (root/'classes').rglob('*.class')}
with zipfile.ZipFile(base) as src:
 mod=json.loads(src.read('fabric.mod.json'));mod['version']='1.0.0-beta.22+hc.1.21.11'
 mod['description']='Haute Capitale PRE22: partial cosmetic extrusion and coplanar face cleanup for native and Sodium rendering.'
 patches['fabric.mod.json']=(json.dumps(mod,ensure_ascii=False,indent=2)+'\n').encode()
 out.parent.mkdir(parents=True,exist_ok=True)
 with zipfile.ZipFile(out,'w',compression=zipfile.ZIP_DEFLATED,compresslevel=9) as dst:
  for n in sorted(set(src.namelist())|patches.keys()):
   if n.endswith('/'):continue
   entry=zipfile.ZipInfo(n,date_time=(2026,10,2,0,0,0));entry.compress_type=zipfile.ZIP_DEFLATED;entry.external_attr=0o644<<16
   dst.writestr(entry,patches[n] if n in patches else src.read(n))
with zipfile.ZipFile(out) as z,zipfile.ZipFile(base) as src:
 assert z.testzip() is None
 for n in src.namelist():
  if n.startswith('assets/'):assert z.read(n)==src.read(n),n
print(out,hashlib.sha256(out.read_bytes()).hexdigest())
