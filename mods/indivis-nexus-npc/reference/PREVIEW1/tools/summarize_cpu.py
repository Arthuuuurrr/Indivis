from pathlib import Path
import csv,json,statistics
R=Path(__file__).resolve().parents[1]
rows=list(csv.DictReader((R/'qa/evidence-reload/npc-cpu.csv').open()))
summary=[]
for pose in ['static','walking']:
 for count in [1,4,8]:
  samples=[r for r in rows if r['pose']==pose and int(r['npcs'])==count]
  summary.append({'pose':pose,'npcs':count,'median_cpu_ms_per_group_pass':round(statistics.median(int(r['cpu_ns'])/int(r['frames'])/1e6 for r in samples),3),'median_allocated_MiB_per_group_pass':round(statistics.median(int(r['allocated_bytes'])/int(r['frames'])/1024**2 for r in samples),3)})
out={'scope':'One grouped CPU pass over the listed number of real EasyNPC entities. Extraction + native model preparation; no GPU, world or cosmetic submission. Walking uses synthetic changing poses.','samples':summary}
(R/'qa/npc-cpu-summary.json').write_text(json.dumps(out,indent=2)+'\n')
print(json.dumps(out))
