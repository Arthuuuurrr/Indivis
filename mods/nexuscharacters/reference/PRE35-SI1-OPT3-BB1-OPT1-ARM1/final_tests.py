from pathlib import Path
import argparse,subprocess,sys
p=argparse.ArgumentParser();p.add_argument('--resume',action='store_true');args=p.parse_args()
R=Path(__file__).resolve().parent
for script in ['validate.py','measure.py']:
 subprocess.run([sys.executable,str(R/script),*(['--resume']if args.resume else[])],check=True)
print('ARMOR_FINAL_SUITE_PASS',flush=True)
