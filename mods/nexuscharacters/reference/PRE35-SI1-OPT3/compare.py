from pathlib import Path
import csv,hashlib,json,statistics,sys
R=Path(__file__).resolve().parent;Q=R.parent/'opt2/previous/npc/qa'
before_label=sys.argv[1] if len(sys.argv)>1 else 'opt2-reference-perf'
after_label=sys.argv[2] if len(sys.argv)>2 else 'opt3-perf'
before=json.loads((Q/('last-'+before_label+'.json')).read_text());after=json.loads((Q/('last-'+after_label+'.json')).read_text())
assert before['nexus_sha256']=='7491ec3feb751b37ff0fe3773b4bb6d75c631220913c97b8e22143d9356fc116'
def read(record):
    root=Path(record['game'])/'evidence';groups={}
    for row in csv.DictReader((root/'opt-perf.csv').open()):
        key=(row['kind'],row['pose'],int(row['entities']));groups.setdefault(key,[]).append(row)
    for key,rows in groups.items():
        assert len(rows)==5
        for row in rows:assert int(row['frames'])==24 and int(row['recomputes'])==(0 if key[1]=='static' else 24*key[2]),(key,row)
    return groups,list(csv.DictReader((root/'opt-geometry.csv').open()))
a,ag=read(before);b,bg=read(after);assert a.keys()==b.keys() and len(ag)==len(bg)==216
assert ag==bg,'Changing-pose geometry differs'
rows=[]
for key in a:
    def cpu(row):return int(row['cpu_ns'])/int(row['frames'])/1e6
    def alloc(row):return int(row['allocated_bytes'])/int(row['frames'])/1048576
    ba=statistics.median(map(cpu,a[key]));aa=statistics.median(map(cpu,b[key]))
    bb=statistics.median(map(alloc,a[key]));ab=statistics.median(map(alloc,b[key]))
    rows.append(dict(kind=key[0],pose=key[1],entities=key[2],opt2_cpu_ms=round(ba,5),opt3_cpu_ms=round(aa,5),cpu_reduction_pct=round((1-aa/ba)*100,2),opt2_allocated_MiB=round(bb,5),opt3_allocated_MiB=round(ab,5),allocation_reduction_pct=round((1-ab/bb)*100,2)))
record={'base_sha256':before['nexus_sha256'],'candidate_sha256':after['nexus_sha256'],'geometry_states':len(ag),'geometry_differences':[],'baseline':before,'candidate':after,'rows':rows,'scope':'Render thread CPU for grouped state extraction and native model preparation, not total frame time/FPS. 320 global warm-up frames; 1500 static/64 moving per-case; five samples of 24 unique frames.'}
name='comparison' if after_label=='opt3-perf' else 'comparison-'+after_label
(R/'qa'/(name+'.json')).write_text(json.dumps(record,indent=2)+'\n')
with (R/'qa'/(name+'.csv')).open('w',newline='') as f:
    writer=csv.DictWriter(f,fieldnames=list(rows[0]));writer.writeheader();writer.writerows(rows)
for row in rows:print(json.dumps(row),flush=True)
print('EXACT_PAIRED_GEOMETRY_PASS',len(ag))
