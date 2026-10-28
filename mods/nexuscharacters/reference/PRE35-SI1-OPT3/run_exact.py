from pathlib import Path
import subprocess,json,hashlib
R=Path(__file__).resolve().parent;OLD=R.parent/'opt2';RT=OLD/'runtime';J=RT/'jdk/bin';C=R/'qa/test-classes'
jar=R/'out/NexusCharacters-Indivis-1.21.11-PRE35-SI1-OPT3.jar'
libs=[jar,RT/'joml-1.10.8.jar',RT/'client-intermediary.jar',RT/'mods/skinlayers.jar',*sorted((RT/'libs').rglob('*.jar'))]
cp=':'.join(map(str,libs))
sources=[OLD/'baseline/tests/net/tompsen/nexuscharacters/ExactIndexRegression.java',OLD/'tests/net/tompsen/nexuscharacters/ExactTransformRegression.java']
metadata=json.loads((R/'qa/build.json').read_text());compact=metadata['compact'];volumes=metadata.get('volumes',True)
if volumes:sources.append(R/'tests/net/tompsen/nexuscharacters/VolumeRegression.java')
if compact:sources.append(R/'tests/net/tompsen/nexuscharacters/CompactClipRegression.java')
subprocess.run([str(J/'javac'),'-proc:none','-cp',cp,'-d',str(C),*map(str,sources)],check=True)
outputs=[]
for name in ['ExactTransformRegression','ExactIndexRegression']+(['VolumeRegression'] if volumes else [])+(['CompactClipRegression'] if compact else []):
    result=subprocess.run([str(J/'java'),'-Xmx1G','-cp',str(C)+':'+cp,'net.tompsen.nexuscharacters.'+name],capture_output=True,text=True)
    print(result.stdout,result.stderr,flush=True);outputs.append(result.stdout+result.stderr)
    if result.returncode:raise SystemExit('Failed '+name+' exit='+str(result.returncode))
record={'candidate_sha256':hashlib.sha256(jar.read_bytes()).hexdigest(),'output':outputs}
(R/'qa/exact.json').write_text(json.dumps(record,indent=2)+'\n')
