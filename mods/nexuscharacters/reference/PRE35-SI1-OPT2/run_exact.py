from pathlib import Path
import subprocess,json,hashlib
R=Path(__file__).resolve().parent;RT=R/'runtime';J=RT/'jdk/bin';C=R/'qa/test-classes'
jar=R/'out/NexusCharacters-Indivis-1.21.11-PRE35-SI1-OPT2.jar'
cp=':'.join(map(str,[jar,RT/'joml-1.10.8.jar',RT/'client-intermediary.jar',RT/'mods/skinlayers.jar']))
sources=[R/'baseline/tests/net/tompsen/nexuscharacters/ExactIndexRegression.java',R/'tests/net/tompsen/nexuscharacters/ExactTransformRegression.java']
subprocess.run([str(J/'javac'),'-proc:none','-cp',cp,'-d',str(C),*map(str,sources)],check=True)
outputs=[]
for name in ['ExactTransformRegression','ExactIndexRegression']:
    result=subprocess.run([str(J/'java'),'-Xmx1G','-cp',str(C)+':'+cp,'net.tompsen.nexuscharacters.'+name],capture_output=True,text=True)
    print(result.stdout,result.stderr,flush=True);outputs.append(result.stdout+result.stderr);result.check_returncode()
record={'candidate_sha256':hashlib.sha256(jar.read_bytes()).hexdigest(),'java':subprocess.check_output([str(J/'java'),'-version'],stderr=subprocess.STDOUT,text=True),'output':outputs}
(R/'qa/exact.json').write_text(json.dumps(record,indent=2)+'\n')

