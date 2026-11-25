from pathlib import Path
import collections,json,subprocess,sys
R=Path(__file__).resolve().parent;P=R.parent/'perf_bb1_20261125';label=sys.argv[1]if len(sys.argv)>1 else'arm1-opt1-profile';record=json.loads((P/(label+'.json')).read_text());file=Path(record['evidence'])/'profile.jfr'
r=subprocess.run(['java','-XX:-UsePerfData','--module','jdk.jfr/jdk.jfr.internal.tool.Main','print','--json','--events','jdk.ExecutionSample,jdk.ObjectAllocationSample','--stack-depth','80',str(file)],capture_output=True,text=True,check=True)
events=json.loads(r.stdout[r.stdout.index('{'):])['recording']['events'];cpu=collections.Counter();alloc=collections.Counter();types=collections.Counter();traces=[]
for e in events:
 v=e['values'];frames=(v.get('stackTrace')or{}).get('frames',[]);methods=[f['method']['type']['name'].replace('/','.')+'.'+f['method']['name']for f in frames];t=v.get('eventThread')or v.get('sampledThread')or{}
 if 'net.tompsen.nexuscharacters.GenericSkinLayerSupport.player'not in methods or(t.get('javaName')!='Render thread'and t.get('javaThreadId')!=1):continue
 leaf=next((m for m in methods if m.startswith('net.tompsen.nexuscharacters.')),None);traces.append({'event':e['type'],'start_time':v['startTime'],'thread_id':t.get('javaThreadId'),'allocation_weight':v.get('weight'),'methods':methods})
 if e['type']=='jdk.ObjectAllocationSample':alloc[leaf]+=v['weight'];types[v['objectClass']['name']]+=v['weight']
 else:cpu[leaf]+=1
def summary(c):
 total=sum(c.values());return[{'method':k,'weight':v,'share_percent':100*v/total}for k,v in c.most_common(25)]
out={'label':label,'jar_sha256':record['jar_sha256'],'allocation_leaves':summary(alloc),'cpu_leaves':summary(cpu),'allocated_types':summary(types),'events_filtered':len(traces)}
(R/'qa'/('profile-'+label+'.json')).write_text(json.dumps(out,indent=2)+'\n');(R/'qa'/('stacks-'+label+'.json')).write_text(json.dumps(traces,indent=2)+'\n');print(json.dumps({'label':label,'filtered':len(traces),'cpu':out['cpu_leaves'][:8]},indent=2))
