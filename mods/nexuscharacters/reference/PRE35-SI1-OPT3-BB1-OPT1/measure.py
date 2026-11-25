"""Balanced serial A/B repetitions of the FINAL binary; all poses animate."""
from pathlib import Path
import hashlib,json,subprocess,sys
R=Path(__file__).resolve().parent;perf=R.parent/'perf_bb1_20261125';b=json.loads((R/'qa/build.json').read_text());jar=Path(b['jar'])
assert hashlib.sha256(jar.read_bytes()).hexdigest()==b['sha256']
# Finish the additional-asset integration check before any timed clients.
subprocess.run([sys.executable,str(R/'future.py')],check=True)
labels=[]
for family,repeat in [('reference',1),('candidate',1),('candidate',2),('reference',2),('reference',3),('candidate',3)]:
 label=f'opt1-final-{family}-{repeat}';args=[sys.executable,str(perf/'run.py'),'BB1','--label',label,'--animated-only']
 if family=='candidate':args+=['--jar',str(jar),'--expected-sha',b['sha256']]
 subprocess.run(args,check=True);labels.append(label)
(R/'qa/performance-runs.json').write_text(json.dumps({'candidate_sha256':b['sha256'],'labels':labels},indent=2)+'\n')
print('FINAL_PERFORMANCE_PASS runs=6 paired_repeats=3',flush=True)
