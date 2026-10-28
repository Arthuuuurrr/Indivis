from pathlib import Path
import subprocess,json
R=Path(__file__).resolve().parent;RT=R.parent/'opt2/runtime';J=RT/'jdk/bin';C=R/'qa/audit-classes'
asm=':'.join(map(str,(RT/'libs/org/ow2/asm').rglob('*.jar')))
subprocess.run([str(J/'javac'),'-cp',asm,'-d',str(C),str(R/'tools/AuditOpt3.java')],check=True)
result=subprocess.run([str(J/'java'),'-cp',str(C)+':'+asm,'AuditOpt3',str(R.parent/'opt2/out/NexusCharacters-Indivis-1.21.11-PRE35-SI1-OPT2.jar'),str(R/'out/NexusCharacters-Indivis-1.21.11-PRE35-SI1-OPT3.jar')],capture_output=True,text=True)
print(result.stdout,result.stderr,flush=True)
if result.returncode:raise SystemExit('Bytecode audit failed')
(R/'qa/bytecode-audit.txt').write_text(result.stdout)
