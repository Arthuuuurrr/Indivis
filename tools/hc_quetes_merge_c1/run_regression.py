"""Reuse HF8's real full-server fixture in an isolated directory, with C1 checks."""
from pathlib import Path
import json,os,shutil,sys
import reconcile
H=Path(__file__).resolve().parent;OLD=next(p for p in [H.parents[1]/'work/pq02',H.parents[1]/'tools/pq02'] if p.exists());REG=H/'regression';REG.mkdir(exist_ok=True)
for folder in ['reference','audit_prologue','dependencies']:
 target=REG/folder
 if not target.exists():target.symlink_to(OLD/folder,target_is_directory=True)
shutil.copytree(OLD/'tests',REG/'tests',dirs_exist_ok=True)
shutil.copyfile(H/'HcQuetesMergeBench.java',REG/'tests/HcQuetesMergeBench.java')
p=REG/'tests/Pq02EndToEndBench.java';s=p.read_text();old='Pq02AutomaticGiftBench.runAll(a);done=true;';assert s.count(old)==1
p.write_text(s.replace(old,'Pq02AutomaticGiftBench.runAll(a);HcQuetesMergeBench.runAll(a,b);done=true;'))
(REG/'reports').mkdir(exist_ok=True);(REG/'output').mkdir(exist_ok=True)
sys.path.insert(0,str(OLD));import build
build.FILES=build.FILES.copy();build.FILES['quests']=reconcile.DEST
for filename in [build.FILES['core'],build.JOURNAL_FILENAME]:
 target=REG/'output'/filename
 if not target.exists():target.symlink_to(OLD/'output'/filename)
shutil.copyfile(H/'output'/reconcile.DEST,REG/'output'/reconcile.DEST)
runtime=Path(json.loads((H/'runtime_plan.json').read_text())['runtime'])
case=runtime/'hcmerge_full_system'
if case.exists():shutil.rmtree(case)
assert not case.exists(), 'Disposable test world must be fresh'
source=(OLD/'run_native.py').read_text().replace("RT=Path('/tmp/indivis_pq02_runtime')",'RT=Path('+repr(str(runtime))+')')
source=source.replace('pq02_full_system_hf8','hcmerge_full_system')
needle="sources.append(H/'tests/Pq02AutomaticGiftBench.java')";assert needle in source
source=source.replace(needle,needle+"\nsources.append(H/'tests/HcQuetesMergeBench.java')")
fixture=REG/'tests/Pq02WorldBench.java';s=fixture.read_text();needle='stub(x[0],"capidentity");';assert needle in s
fixture.write_text(s.replace(needle,needle+'stub(x[0],"mmospawn");'))
needle="for files in orig.values():\n for n,b in files.items():";assert source.count(needle)==1
extra=['data/hc_quetes/function/aurele/','data/hc_quetes/function/carrec/','data/hc_quetes/function/hector/q5/','data/hc_quetes/function/pnj/aveline/','data/hc_quetes/function/lib/balayage.mcfunction','data/hc_quetes/function/marqueurs/aurele.mcfunction','data/hc_quetes/hcq_quests/']
source=source.replace(needle,'paths += '+repr(extra)+'\n'+needle)
# All auxiliary objectives exist after hc_quetes:load in production. The isolated
# fixture must declare selector-only flags too, before the cleanup tests use them.
needle="pack['data/fixture/function/objectives.mcfunction']=";assert source.count(needle)==1
source=source.replace(needle,"scores.update(re.findall(r'scoreboard objectives add (\\S+)',orig['quests']['data/hc_quetes/function/load.mcfunction'].decode()))\n"+needle)
# Unchanged equipment rewards belong to third-party mods absent from the fixture.
# They are not exercised here; resolve their calls without shipping substitutes.
needle='for n,b in pack.items():p=P/n;';assert needle in source
substitutes=['rec_vivres','rec_arbalete','rec_piece','rec_jambieres','rec_collier','rec_bicorne','rec_pique']
code="for loot in "+repr(substitutes)+":\n key='data/hc_quetes/function/objets/'+loot+'.mcfunction'\n if key in pack:pack[key]=b'return 1\\n'\n"
source=source.replace(needle,code+needle)
os.environ['PQ02_INTEGRATED']='1';os.environ['PQ02_FULL_SYSTEM']='1'
exec(compile(source,str(REG/'run_native.py'),'exec'),{'__name__':'__main__','__file__':str(REG/'run_native.py')})
