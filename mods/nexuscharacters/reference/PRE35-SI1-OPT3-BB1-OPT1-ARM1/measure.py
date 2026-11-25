from pathlib import Path
import argparse,hashlib,json,subprocess,sys
p=argparse.ArgumentParser();p.add_argument('--resume',action='store_true');args=p.parse_args()
R=Path(__file__).resolve().parent;PERF=R.parent/'perf_bb1_20261125';B=json.loads((R/'qa/build.json').read_text());jar=Path(B['jar']);base=R.parent/'opt_bb1_20261125/out/Nexus-BB1-OPT1-1.21.11.jar'
assert hashlib.sha256(jar.read_bytes()).hexdigest()==B['sha256']
labels=[]
for family,n in [('reference',1),('candidate',1),('candidate',2),('reference',2),('reference',3),('candidate',3)]:
 label=f'arm1-final-{family}-{n}';file=jar if family=='candidate'else base;sha=B['sha256']if family=='candidate'else B['base_sha256']
 saved=PERF/(label+'.json');reuse=False
 if args.resume and saved.exists():
  old=json.loads(saved.read_text());e=Path(old['evidence']);reuse=old['jar_sha256']==sha and old.get('animated_only')and not old['freeze']and(e/'pass.txt').exists()and not(e/'failure.txt').exists()
 if reuse:print('RESUMED_PERFORMANCE_PASS',label,flush=True)
 else:subprocess.run([sys.executable,str(PERF/'run.py'),'BB1','--label',label,'--animated-only','--jar',str(file),'--expected-sha',sha],check=True)
 labels.append(label)
(R/'qa/performance-runs.json').write_text(json.dumps({'candidate_sha256':B['sha256'],'labels':labels},indent=2)+'\n');print('ARMOR_FINAL_PERFORMANCE_PASS runs=6 paired_repeats=3',flush=True)
