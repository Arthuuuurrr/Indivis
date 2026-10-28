from pathlib import Path
import subprocess,json
R=Path(__file__).resolve().parents[1];RT=R.parent/'pre31/runtime';J=R.parent/'pre31/jdk/bin';C=R/'qa/test-classes'
cp=':'.join(map(str,[R/'NexusCharacters-Indivis-1.21.11-PRE35-SI1-OPT1.jar',next((RT/'libs').rglob('joml-1.10.8.jar')),RT/'client-intermediary.jar',RT/'skinlayers.jar']))
subprocess.run([str(J/'javac'),'-proc:none','-cp',cp,'-d',str(C),str(R/'tests/net/tompsen/nexuscharacters/ExactIndexRegression.java')],check=True)
r=subprocess.run([str(J/'java'),'-Xmx1G','-cp',str(C)+':'+cp,'net.tompsen.nexuscharacters.ExactIndexRegression'],text=True,capture_output=True)
print(r.stdout,r.stderr);r.check_returncode()
(R/'qa/exact-index-pass.txt').write_text(r.stdout)
