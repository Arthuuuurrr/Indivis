from pathlib import Path
import subprocess,json
R=Path(__file__).resolve().parent
b=json.loads((R/'qa/build.json').read_text())
J=R.parent/'presets_import/runtime/jdk/bin';cp=b['classpath'];c=R/'build/tests';c.mkdir(exist_ok=True)
subprocess.run([str(J/'javac'),'-proc:none','-cp',cp,'-d',str(c),str(R/'tests/SurfaceKernelOracle.java')],check=True)
r=subprocess.run([str(J/'java'),'-XX:-UsePerfData','-cp',str(c)+':'+cp,'net.tompsen.nexuscharacters.SurfaceKernelOracle','20000'],check=True,capture_output=True,text=True)
print(r.stdout,end='');(R/'qa/kernel.txt').write_text(r.stdout)
