from pathlib import Path
import json,urllib.request,hashlib,tarfile,zipfile,concurrent.futures
HERE=Path(__file__).resolve().parent;P=json.loads((HERE/'runtime_plan.json').read_text());RT=Path(P['runtime']);RT.mkdir(exist_ok=True)
def get(url):
 with urllib.request.urlopen(url,timeout=180) as r:return r.read()
def download(row):
 url,path,digest=row;path=Path(path);path.parent.mkdir(parents=True,exist_ok=True)
 if path.is_file() and (digest is None or hashlib.sha1(path.read_bytes()).hexdigest()==digest):return str(path)
 b=get(url)
 if digest:assert hashlib.sha1(b).hexdigest()==digest,url
 path.write_bytes(b);return str(path)
profile=json.loads(get('https://meta.fabricmc.net/v2/versions/loader/1.21.11/0.19.5/server/json'));(RT/'fabric_server_profile.json').write_text(json.dumps(profile))
jobs=[(P['jdk']['download_url'],RT/'jdk.tar.gz',None),(P['server']['url'],RT/'minecraft_server.jar',P['server']['sha1']),('https://maven.fabricmc.net/net/fabricmc/intermediary/1.21.11/intermediary-1.21.11.jar',RT/'libraries/intermediary-1.21.11.jar',None),('https://maven.fabricmc.net/net/fabricmc/fabric-api/fabric-api/0.141.6+1.21.11/fabric-api-0.141.6+1.21.11.jar',RT/'fabric-api.jar',None),('https://repo.maven.apache.org/maven2/org/benf/cfr/0.152/cfr-0.152.jar',RT/'cfr.jar',None)]
for lib in profile['libraries']:
 group,artifact,version=lib['name'].split(':');rel=group.replace('.','/')+'/'+artifact+'/'+version+'/'+artifact+'-'+version+'.jar'
 jobs.append((lib.get('url','https://libraries.minecraft.net/').rstrip('/')+'/'+rel,RT/'libraries'/Path(rel).name,None))
with concurrent.futures.ThreadPoolExecutor(max_workers=5) as ex:
 for result in ex.map(download,jobs):print('Runtime dependency:',Path(result).name,flush=True)
if not (RT/'jdk').exists():
 (RT/'jdk').mkdir()
 with tarfile.open(RT/'jdk.tar.gz') as t:t.extractall(RT/'jdk',filter='data')
with zipfile.ZipFile(RT/'minecraft_server.jar') as z:
 for n in z.namelist():
  if n.startswith(('META-INF/libraries/','META-INF/versions/')) and not n.endswith('/'):
   p=RT/'minecraft_bundle'/n;p.parent.mkdir(parents=True,exist_ok=True);p.write_bytes(z.read(n))
manifest={str(Path(x[1]).relative_to(RT)):{'url':x[0],'sha256':hashlib.sha256(Path(x[1]).read_bytes()).hexdigest()} for x in jobs}
(HERE/'runtime_dependencies.json').write_text(json.dumps(manifest,indent=2)+'\n')
print('PASS runtime downloaded and archive integrity checked',flush=True)
