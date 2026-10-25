from pathlib import Path
import subprocess
R=Path(__file__).resolve().parents[1]; RT=R.parent/'pre31/runtime';J=R.parent/'pre31/jdk/bin'
cp=':'.join(map(str,[R/'qa/NexusCharacters-PRE35-instrumented.jar',RT/'client-intermediary.jar',RT/'fabric-loader.jar',RT/'skinlayers.jar',*sorted((RT/'libs').rglob('*.jar'))]))
classes=R/'qa/unit-classes';classes.mkdir(exist_ok=True)
subprocess.run([str(J/'javac'),'-proc:none','-cp',cp,'-d',str(classes),str(R/'tests/net/tompsen/nexuscharacters/ExactGeometryRegression.java')],check=True)
r=subprocess.run([str(J/'java'),'-Xmx1100M','-cp',str(classes)+':'+cp,'net.tompsen.nexuscharacters.ExactGeometryRegression'],text=True,stdout=subprocess.PIPE,stderr=subprocess.STDOUT)
(R/'qa/exact-geometry.txt').write_text(r.stdout);print(r.stdout,flush=True);assert r.returncode==0
