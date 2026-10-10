import json,pathlib,subprocess,concurrent.futures,zipfile
R=pathlib.Path(__file__).parent/'runtime'; V=json.loads((R/'version.json').read_text()); R.mkdir(exist_ok=True)
def get(url,out):
 out=pathlib.Path(out);out.parent.mkdir(parents=True,exist_ok=True)
 if not out.exists():subprocess.run(['curl','-LsSf','--retry','2','--max-time','120',url,'-o',str(out)],check=True)
 return out
jobs=[(V['downloads']['client']['url'],R/'client.jar'),('https://maven.fabricmc.net/net/fabricmc/intermediary/1.21.11/intermediary-1.21.11-v2.jar',R/'intermediary.jar'),('https://maven.fabricmc.net/net/fabricmc/tiny-remapper/0.13.1/tiny-remapper-0.13.1-fat.jar',R/'remapper.jar'),('https://maven.fabricmc.net/net/fabricmc/fabric-loader/0.19.5/fabric-loader-0.19.5.jar',R/'fabric-loader.jar')]
for l in V['libraries']:
 a=l.get('downloads',{}).get('artifact')
 if a:jobs.append((a['url'],R/'libs'/a['path']))
with concurrent.futures.ThreadPoolExecutor(max_workers=12) as e:
 for x in e.map(lambda a:get(*a),jobs):print(x,flush=True)
with zipfile.ZipFile(R/'intermediary.jar') as z:(R/'mappings.tiny').write_bytes(z.read('mappings/mappings.tiny'))
cp=':'.join(str(x.resolve()) for x in (R/'libs').rglob('*.jar'));(R/'classpath.txt').write_text(cp)
subprocess.run([str(pathlib.Path('pre30/jdk/bin/java').resolve()),'-Xmx1800M','-jar',str(R/'remapper.jar'),str(R/'client.jar'),str(R/'client-intermediary.jar'),str(R/'mappings.tiny'),'official','intermediary',*[str(x) for x in (R/'libs').rglob('*.jar')]],check=True)
