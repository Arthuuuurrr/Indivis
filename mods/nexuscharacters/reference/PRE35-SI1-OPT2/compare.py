from pathlib import Path
import csv,hashlib,json,statistics,sys
R=Path(__file__).resolve().parent;Q=R/'previous/npc/qa'
old=json.loads((Q/'last-opt1-perf.json').read_text());new=json.loads((Q/'last-opt2-perf.json').read_text())
original=json.loads((Q/'last-pre35-perf.json').read_text())
assert original['nexus_sha256']=='399a4c64a7672e60ebbaaa52044cb547f5c41576039fe5aafe2c42485e2e8a8a'
assert old['nexus_sha256']=='bdb1525d5e4448cb8127e38bdcdce063d629b7039a856bf9ea74be5ea06dea11'
assert new['nexus_sha256']==json.loads((R/'qa/build.json').read_text())['sha256']
assert 'NPC_CLIENT_PASS' in old['pass'] and 'NPC_CLIENT_PASS' in new['pass']
def sample(r):
    groups={};root=Path(r['game'])/'evidence'
    for row in csv.DictReader((root/'opt-perf.csv').open()):
        key=(row['kind'],row['pose'],int(row['entities']));groups.setdefault(key,[]).append(row)
    for key,rows in groups.items():
        assert len(rows)==5
        for row in rows:
            assert int(row['frames'])==24
            assert int(row['recomputes'])==(0 if key[1]=='static' else 24*key[2]),('Not unique poses',key,row)
    geometry=list(csv.DictReader((root/'opt-geometry.csv').open()))
    return groups,geometry
a,ag=sample(old);b,bg=sample(new);c,cg=sample(original);assert a.keys()==b.keys()==c.keys();assert len(ag)==len(bg)==len(cg)==216
diff=[(x,y) for x,y in zip(ag,bg) if x!=y];assert not diff,('Geometry differs',diff[:3])
assert cg==bg,'PRE35 geometry differs from OPT2'
rows=[]
for key in a:
    def cpu(row):return int(row['cpu_ns'])/int(row['frames'])/1e6
    def allocation(row):return int(row['allocated_bytes'])/int(row['frames'])/1048576
    before=statistics.median(map(cpu,a[key]));after=statistics.median(map(cpu,b[key]))
    ba=statistics.median(map(allocation,a[key]));aa=statistics.median(map(allocation,b[key]))
    pre=statistics.median(map(cpu,c[key]));prea=statistics.median(map(allocation,c[key]))
    rows.append(dict(kind=key[0],pose=key[1],entities=key[2],pre35_cpu_ms=round(pre,5),opt1_cpu_ms=round(before,5),opt2_cpu_ms=round(after,5),cpu_reduction_vs_opt1_pct=round((1-after/before)*100,2),cpu_reduction_vs_pre35_pct=round((1-after/pre)*100,2),pre35_allocated_MiB=round(prea,5),opt1_allocated_MiB=round(ba,5),opt2_allocated_MiB=round(aa,5),allocation_reduction_vs_opt1_pct=round((1-aa/ba)*100,2),allocation_reduction_vs_pre35_pct=round((1-aa/prea)*100,2)))
record={'candidate_sha256':new['nexus_sha256'],'base_sha256':old['nexus_sha256'],'original_sha256':original['nexus_sha256'],'scope':'Grouped extraction and native model preparation on real EasyNPC/OtherClientPlayer in Minecraft 1.21.11; no GPU/FPS claim; 320 global warm-up frames, 1500 static or 64 changing-pose per-case warm-up frames, 5 x 24 measured frames with genuinely unique poses','geometry_states':len(ag),'geometry_differences':diff,'rows':rows,'baseline':old,'original':original,'candidate':new}
(R/'qa/comparison.json').write_text(json.dumps(record,indent=2)+'\n')
with (R/'qa/MESURES_CPU.csv').open('w',newline='') as f:
    writer=csv.DictWriter(f,fieldnames=list(rows[0]));writer.writeheader();writer.writerows(rows)
print('EXACT_GEOMETRY_PAIR_PASS',len(ag))
for row in rows:print(json.dumps(row))
