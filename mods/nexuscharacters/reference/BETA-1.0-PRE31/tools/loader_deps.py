from pathlib import Path
import json,subprocess,concurrent.futures
R=Path(__file__).resolve().parents[1]/'runtime'
meta=json.loads((R/'loader.json').read_text());jobs=[]
for kind in ['common','client']:
 for item in meta['libraries'].get(kind,[]):
  group,name,version=item['name'].split(':');path=group.replace('.','/')+'/'+name+'/'+version+'/'+name+'-'+version+'.jar'
  jobs.append((item['url'].rstrip('/')+'/'+path,R/'libs'/path))
def get(job):
 url,out=job;out.parent.mkdir(parents=True,exist_ok=True)
 if not out.exists():subprocess.run(['curl','-LsSf','--retry','2','--max-time','90',url,'-o',str(out)],check=True)
 print(out.name,flush=True)
with concurrent.futures.ThreadPoolExecutor(max_workers=6) as pool:list(pool.map(get,jobs))
(R/'classpath.txt').write_text(':'.join(str(p.resolve()) for p in sorted((R/'libs').rglob('*.jar'))))
