from pathlib import Path
import json, subprocess, zipfile, concurrent.futures
R=Path(__file__).resolve().parents[1]; RT=R/'runtime'; RT.mkdir(exist_ok=True)
def download(url,out):
 out=Path(out);out.parent.mkdir(parents=True,exist_ok=True)
 if not out.exists():subprocess.run(['curl','-LsSf','--retry','2','--max-time','180',url,'-o',str(out)],check=True)
 return out
download('https://api.adoptium.net/v3/binary/latest/21/ga/linux/x64/jdk/hotspot/normal/eclipse',R/'jdk.tar.gz')
if not (R/'jdk/bin/java').exists():
 (R/'jdk').mkdir(exist_ok=True);subprocess.run(['tar','-xzf',str(R/'jdk.tar.gz'),'-C',str(R/'jdk'),'--strip-components=1'],check=True)
download('https://piston-meta.mojang.com/mc/game/version_manifest_v2.json',RT/'manifest.json')
version=next(v for v in json.loads((RT/'manifest.json').read_text())['versions'] if v['id']=='1.21.11')
download(version['url'],RT/'version.json');V=json.loads((RT/'version.json').read_text())
jobs=[(V['downloads']['client']['url'],RT/'client.jar'),('https://maven.fabricmc.net/net/fabricmc/intermediary/1.21.11/intermediary-1.21.11-v2.jar',RT/'intermediary.jar'),('https://maven.fabricmc.net/net/fabricmc/tiny-remapper/0.13.1/tiny-remapper-0.13.1-fat.jar',RT/'remapper.jar'),('https://maven.fabricmc.net/net/fabricmc/fabric-loader/0.19.5/fabric-loader-0.19.5.jar',RT/'fabric-loader.jar')]
for l in V['libraries']:
 a=l.get('downloads',{}).get('artifact')
 if a:jobs.append((a['url'],RT/'libs'/a['path']))
with concurrent.futures.ThreadPoolExecutor(max_workers=10) as e:
 for p in e.map(lambda a:download(*a),jobs):print(p.name,flush=True)
with zipfile.ZipFile(RT/'intermediary.jar') as z:(RT/'mappings.tiny').write_bytes(z.read('mappings/mappings.tiny'))
libs=sorted((RT/'libs').rglob('*.jar'));(RT/'classpath.txt').write_text(':'.join(str(x) for x in libs))
if not (RT/'client-intermediary.jar').exists():subprocess.run([str(R/'jdk/bin/java'),'-Xmx1800M','-jar',str(RT/'remapper.jar'),str(RT/'client.jar'),str(RT/'client-intermediary.jar'),str(RT/'mappings.tiny'),'official','intermediary',*map(str,libs)],check=True)
print('BOOTSTRAP_READY',flush=True)
