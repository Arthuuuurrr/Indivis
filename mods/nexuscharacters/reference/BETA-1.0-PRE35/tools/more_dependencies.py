from pathlib import Path
import json,hashlib,concurrent.futures,subprocess
R=Path(__file__).resolve().parents[1];RT=R.parent/'pre31/runtime';OUT=R/'runtime-extra'
def mod(project,version,name):
 meta=subprocess.check_output(['curl','-LsSf','--retry','2','--max-time','90','https://api.modrinth.com/v2/project/'+project+'/version?game_versions=%5B%221.21.11%22%5D&loaders=%5B%22fabric%22%5D'])
 versions=json.loads(meta)
 def match(v):
  n=v['version_number'];return n==version or n.startswith(version+'+') or n.startswith(version+'-') or n=='mc1.21.11-'+version+'-fabric'
 v=next(x for x in versions if match(x));f=next((x for x in v['files'] if x.get('primary')),v['files'][0]);p=OUT/(name+'.jar')
 subprocess.run(['curl','-LsSf','--retry','2','--max-time','120',f['url'],'-o',str(p)],check=True)
 assert hashlib.sha512(p.read_bytes()).hexdigest()==f['hashes']['sha512']
 print('DEPENDENCY',project,v['version_number'],flush=True)
 return {'project':project,'version':v['version_number'],'file':p.name,'sha256':hashlib.sha256(p.read_bytes()).hexdigest()}
mods=[('iris','1.10.7','iris'),('player-animation-library','1.1.10','pal'),('owo-lib','0.13.0','owo'),('cloth-config','21.11.153','cloth-config'),('entityculling','1.10.2','entityculling'),('skills','0.19.0','puffish_skills')]
with concurrent.futures.ThreadPoolExecutor(max_workers=5) as pool:result=list(pool.map(lambda x:mod(*x),mods))
(R/'qa/downloaded-dependencies.json').write_text(json.dumps(result,indent=2)+'\n')
