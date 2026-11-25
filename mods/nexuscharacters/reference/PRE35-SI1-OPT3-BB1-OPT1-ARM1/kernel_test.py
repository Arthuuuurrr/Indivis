from pathlib import Path
import json,subprocess
R=Path(__file__).resolve().parent;B=json.loads((R/'qa/build.json').read_text());J=R.parent/'presets_import/runtime/jdk/bin';C=R/'build/tests';C.mkdir(exist_ok=True)
subprocess.run([str(J/'javac'),'-proc:none','-cp',B['classpath'],'-d',str(C),str(R/'tests/ArmorPreparedOracle.java')],check=True)
r=subprocess.run([str(J/'java'),'-XX:-UsePerfData','-cp',str(C)+':'+B['classpath'],'net.tompsen.nexuscharacters.ArmorPreparedOracle','3000'],capture_output=True,text=True)
print(r.stdout);print(r.stderr)if r.stderr else None
if r.returncode:raise SystemExit(r.returncode)
(R/'qa/kernel.txt').write_text(r.stdout)
