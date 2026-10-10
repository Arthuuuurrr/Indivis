import json,pathlib,subprocess,concurrent.futures,zipfile
R=pathlib.Path('pre30/runtime');v=json.loads((R/'version.json').read_text());A=R/'assets';(A/'indexes').mkdir(parents=True,exist_ok=True)
subprocess.run(['curl','-LsSf','--max-time','60',v['assetIndex']['url'],'-o',str(A/'indexes/29.json')],check=True)
a=json.loads((A/'indexes/29.json').read_text())['objects'];required={k:x for k,x in a.items() if '/font/' in k or '/textures/' in k or '/resourcepacks/' in k or k.startswith('icons/') or k in ('minecraft/lang/en_us.json','minecraft/lang/fr_fr.json','minecraft/sounds.json')}
print('assets required',len(required),sum(x['size'] for x in required.values()),flush=True)
def download(x):
 h=x['hash'];p=A/'objects'/h[:2]/h;p.parent.mkdir(parents=True,exist_ok=True)
 if not p.exists():subprocess.run(['curl','-LsSf','--retry','4','--retry-all-errors','--max-time','60','https://resources.download.minecraft.net/'+h[:2]+'/'+h,'-o',str(p)],check=True)
with concurrent.futures.ThreadPoolExecutor(max_workers=4) as e:list(e.map(download,required.values()))
N=R/'natives';N.mkdir(exist_ok=True)
for p in (R/'libs').rglob('*natives-linux.jar'):
 with zipfile.ZipFile(p) as z:
  for name in z.namelist():
   if name.endswith('.so'):(N/pathlib.Path(name).name).write_bytes(z.read(name))
print('assets and natives ready',flush=True)
