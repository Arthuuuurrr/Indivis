from pathlib import Path
import json,zipfile,hashlib
R=Path(__file__).resolve().parents[1];Q=R/'qa'
run=json.loads((Q/'last-candidate-world.json').read_text());M=Q/'games'/run['run_id']/'mods'
result=[]
def inspect(p,embedded=False):
 data=p.read_bytes()
 with zipfile.ZipFile(p) as z:
  if 'fabric.mod.json' not in z.namelist():return
  meta=json.loads(z.read('fabric.mod.json'))
  if meta['id']=='pre30_harness':return
  result.append({'file':p.name,'id':meta['id'],'version':meta.get('version'),'embedded_compile_dependency':embedded,'size_bytes':len(data),'sha256':hashlib.sha256(data).hexdigest()})
for p in sorted(M.glob('*.jar')):inspect(p)
for p in sorted((Q/'dependency-classes').glob('*.jar')):inspect(p,True)
(Q/'test-mods.json').write_text(json.dumps(result,indent=2,ensure_ascii=False)+'\n')
print('TEST_INVENTORY35_READY',len(result))
