"""Aggregate Java execution samples only while our native workload is on-stack."""
from pathlib import Path
import subprocess,json,collections,sys,re
R=Path(__file__).resolve().parents[1];J=R.parent/'pre31/jdk/bin'
source=Path(sys.argv[1]).resolve()
data=json.loads(subprocess.check_output([str(J/'jfr'),'print','--json','--stack-depth','256','--events','jdk.ExecutionSample',str(source)]))
log=source.parents[2]/('client-'+source.parent.name+'.log')
times=re.findall(r'\[(\d\d:\d\d:\d\d)\] \[Render thread/INFO\]: \[STDOUT\]: OPT_PERF ',log.read_text())
assert times
start,end=min(times),max(times)
threads=collections.Counter(e['values']['sampledThread']['javaName'] for e in data['recording']['events'])
tops=collections.Counter();nexus=collections.Counter();kinds=collections.Counter();total=0;truncated=0
for event in data['recording']['events']:
 frames=event['values'].get('stackTrace',{}).get('frames',[])
 names=[f['method']['type']['name'].replace('/','.')+'.'+f['method']['name'] for f in frames]
 values=event['values'];time=values['startTime'][11:19]
 # This recording starts before Minecraft renames the main thread. JFR retains
 # its initial name; Java thread id 1 is the actual render/workload thread.
 if values['sampledThread']['javaThreadId']!=1 or not(start<=time<=end):continue
 total+=1
 truncated+=bool(values.get('stackTrace',{}).get('truncated'))
 if names:tops[names[0]]+=1
 for n in set(n for n in names if n.startswith('net.tompsen.nexuscharacters.')):nexus[n]+=1
 if frames:kinds[frames[0].get('type','unknown')]+=1
result={'workload_samples':total,'truncated_stacks':truncated,'log_time_window':[start,end],
 'threads':threads,'first_event_time':data['recording']['events'][0]['values']['startTime'],'top_frames':tops.most_common(35),
 'inclusive_nexus_frames':nexus.most_common(45),'top_frame_kind':kinds,
 'note':'Inclusive frames overlap; not additive. Java samples, not GPU/FPS.'}
print(json.dumps(result,indent=2))
(R/'qa/workload-jfr.json').write_text(json.dumps(result,indent=2)+'\n')
