import hashlib,json,sys,zipfile
from pathlib import Path
base,classes,out=map(Path,sys.argv[1:])
assert hashlib.sha256(base.read_bytes()).hexdigest()=='c24927630d01771bbe45080794cf4428f2af15a2f737b2a4fd382746dd505ab2','Wrong PRE17 base'
patches={str(p.relative_to(classes)):p.read_bytes() for p in classes.rglob('*.class')}
with zipfile.ZipFile(base) as src:
 mod=json.loads(src.read('fabric.mod.json'));mod['version']='1.0.0-beta.19+hc.1.21.11';mod['description']='Haute Capitale PRE19: distinct base and outer pixel volumes for hair, classic beards and clothing, with bounded limb seams and dynamic assets.'
 mixins=json.loads(src.read('nexuscharacters.client.mixins.json'));mixins['client']+=['GenericPlayerLayersMixin','GenericLayerReloadMixin','GenericFirstPersonLayersMixin']
 patches['fabric.mod.json']=(json.dumps(mod,ensure_ascii=False,indent=2)+'\n').encode()
 patches['nexuscharacters.client.mixins.json']=(json.dumps(mixins,indent=2)+'\n').encode()
 out.parent.mkdir(parents=True,exist_ok=True)
 with zipfile.ZipFile(out,'w',compression=zipfile.ZIP_DEFLATED,compresslevel=9) as dst:
  for n in sorted(set(src.namelist())|patches.keys()):
   if n.endswith('/'):continue
   entry=zipfile.ZipInfo(n,date_time=(2026,10,1,0,0,0));entry.compress_type=zipfile.ZIP_DEFLATED;entry.external_attr=0o644<<16
   dst.writestr(entry,patches[n] if n in patches else src.read(n))
with zipfile.ZipFile(out) as z:
 assert z.testzip() is None
 with zipfile.ZipFile(base) as src:
  for n in src.namelist():
   if n.startswith('assets/') and not n.endswith('/'):assert z.read(n)==src.read(n),n
print(out,hashlib.sha256(out.read_bytes()).hexdigest(),'changed entries:',len(patches))
