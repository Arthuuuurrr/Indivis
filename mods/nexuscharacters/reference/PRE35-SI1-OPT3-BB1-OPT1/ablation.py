from pathlib import Path
import json,subprocess,sys
R=Path(__file__).resolve().parent;perf=R.parent/'perf_bb1_20261125/run.py'
for variant in ['canonical-only','no-clip','no-writer','no-memo','no-scratch']:
 subprocess.run([sys.executable,str(R/'build.py'),'--variant',variant],check=True)
 b=json.loads((R/('qa/build-'+variant+'.json')).read_text())
 subprocess.run([sys.executable,str(perf),'BB1','--label','opt1-ablation-'+variant,'--pilot','--jar',b['jar'],'--expected-sha',b['sha256']],check=True)
subprocess.run([sys.executable,str(perf),'BB1','--label','opt1-ablation-original','--pilot'],check=True)
print('ABLATION_PASS pilots=6 including unchanged BB1',flush=True)
