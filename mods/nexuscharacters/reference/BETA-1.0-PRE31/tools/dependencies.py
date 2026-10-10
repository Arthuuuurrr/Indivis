from pathlib import Path
import subprocess,json,concurrent.futures,zipfile
R=Path(__file__).resolve().parents[1];RT=R/'runtime';RT.mkdir(exist_ok=True)
def get(url,p):
 p.parent.mkdir(parents=True,exist_ok=True)
 if not p.exists():subprocess.run(['curl','-LsSf','--retry','2','--max-time','120',url,'-o',str(p)],check=True)
 return p
def mod(project,version,name):
 data=RT/(name+'-versions.json')
 get('https://api.modrinth.com/v2/project/'+project+'/version?game_versions=%5B%221.21.11%22%5D&loaders=%5B%22fabric%22%5D',data)
 vs=json.loads(data.read_text());v=next(x for x in vs if x['version_number']==version or x['version_number'].startswith(version+'+') or x['version_number']=='mc1.21.11-'+version+'-fabric')
 f=next((x for x in v['files'] if x['primary']),v['files'][0]);get(f['url'],RT/(name+'.jar'));print(name,v['version_number'],flush=True)
with concurrent.futures.ThreadPoolExecutor(max_workers=4) as pool:
 list(pool.map(lambda x:mod(*x),[('fabric-api','0.141.6','fabric-api'),('3dskinlayers','1.10.2','skinlayers'),('sodium','0.8.7','sodium'),('immediatelyfast','1.14.2','immediatelyfast')]))
print('MODS_READY',flush=True)
