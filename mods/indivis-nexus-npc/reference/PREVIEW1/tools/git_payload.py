from pathlib import Path
import json,sys
R=Path(__file__).resolve().parents[1];prefix='mods/indivis-nexus-npc/reference/PREVIEW1/'
files=[*R.glob('*.py'),*R.glob('*.md'),*R.glob('src/**/*.java'),*R.glob('resources/**/*'),*R.glob('tests/**/*.java'),*R.glob('tools/*.py'),*R.glob('tools/*.java')]
files=[p for p in files if p.is_file()]
files+=list((R/'qa').glob('*.json'))
for mode in ['npc','reload','future','visual','menus-baseline','menus']:
 for name in ['pass.txt','hashes.json','npc-cpu.csv']:
  p=R/'qa'/('evidence-'+mode)/name
  if p.exists():files.append(p)
start=int(sys.argv[1]) if len(sys.argv)>1 else 0;end=int(sys.argv[2]) if len(sys.argv)>2 else len(files)
print(json.dumps([{'path':prefix+p.relative_to(R).as_posix(),'mode':'100644','type':'blob','content':p.read_text()} for p in files[start:end]],ensure_ascii=False))
