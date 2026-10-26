from pathlib import Path
import csv,json,statistics,hashlib
R=Path(__file__).resolve().parents[1];Q=R.parent/'nexus-npc/qa'
def record(label):
 data=json.loads((Q/('last-'+label+'.json')).read_text());assert 'NPC_CLIENT_PASS' in data['pass'];return data
old=record('opt-baseline');new=record('opt-candidate')
A=Path(old['game'])/'evidence';B=Path(new['game'])/'evidence'
a=list(csv.DictReader((A/'opt-geometry.csv').open()));b=list(csv.DictReader((B/'opt-geometry.csv').open()))
assert len(a)==len(b)==216
differences=[{'before':x,'after':y} for x,y in zip(a,b) if x!=y]
def samples(folder):
 groups={}
 for row in csv.DictReader((folder/'opt-perf.csv').open()):
  key=(row['kind'],row['pose'],int(row['entities']));groups.setdefault(key,[]).append(row)
 return groups
x,y=samples(A),samples(B);assert x.keys()==y.keys()
assert all(len(v)==5 for v in [*x.values(),*y.values()]),'Final timing requires five samples per case'
rows=[]
for key in x:
 def cpu(sample):return int(sample['cpu_ns'])/int(sample['frames'])/1e6
 def allocation(sample):return int(sample['allocated_bytes'])/int(sample['frames'])/1048576
 before=statistics.median(map(cpu,x[key]));after=statistics.median(map(cpu,y[key]))
 rows.append({'kind':key[0],'pose':key[1],'entities':key[2],'before_cpu_ms':round(before,4),'after_cpu_ms':round(after,4),
   'cpu_reduction_pct':round((1-after/before)*100,2),'speedup':round(before/after,3),
   'before_allocated_MiB':round(statistics.median(map(allocation,x[key])),4),
   'after_allocated_MiB':round(statistics.median(map(allocation,y[key])),4),
   'recomputes_before':[int(v['recomputes']) for v in x[key]],'recomputes_after':[int(v['recomputes']) for v in y[key]]})
result={'scope':'Grouped extraction + native model preparation; real EasyNPC / OtherClientPlayer; 320 unique three-entity warm-up frames, then 32 per-case warm-up frames, 5 samples x 24 frames; no GPU/FPS claim',
  'baseline_nexus_sha256':old['nexus_sha256'],'candidate_nexus_sha256':new['nexus_sha256'],'geometry_states':len(a),
  'geometry_differences':differences,'rows':rows,'baseline':old,'candidate':new}
(R/'qa/comparison.json').write_text(json.dumps(result,ensure_ascii=False,indent=2)+'\n')
print('GEOMETRY_COMPARISON',len(a),'differences',len(differences))
for row in rows:print(json.dumps(row))
assert not differences,'Cross-run geometry differs; inspect deterministic animation fixture before release'
