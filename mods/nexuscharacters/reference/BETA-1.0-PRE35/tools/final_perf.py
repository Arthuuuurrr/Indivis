from pathlib import Path
import subprocess,os,json,datetime
R=Path(__file__).resolve().parents[1];runs=[]
for baseline in [True,False]:
 kind='baseline' if baseline else 'candidate'
 env=os.environ.copy();env['PRE35_DISPLAY']='128'
 start=datetime.datetime.now(datetime.UTC).isoformat()
 cmd=['python3',str(R/'run_client.py'),'--perf']+(['--baseline'] if baseline else [])
 subprocess.run(cmd,check=True,env=env)
 run=json.loads((R/'qa'/('last-'+kind+'-perf.json')).read_text());run['orchestration_started_utc']=start;runs.append(run)
(R/'qa/final-perf-runs.json').write_text(json.dumps(runs,indent=2)+'\n')
print('FINAL35_PERF_PASS sequential_clients=true',flush=True)
