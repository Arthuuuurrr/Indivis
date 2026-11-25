from pathlib import Path
import subprocess,sys
R=Path(__file__).resolve().parent
for script in ['build.py','validate.py','measure.py']:
 subprocess.run([sys.executable,str(R/script)],check=True)
print('FINAL_BINARY_AND_CLIENT_SUITE_PASS',flush=True)
