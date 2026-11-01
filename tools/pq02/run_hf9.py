"""Banc HF9 sur le vrai JAR et le datapack Fusion C1 conservé."""
from pathlib import Path
import sys,uuid,shutil
import build,build_hf9
H=Path(__file__).resolve().parent;C1=next(p for p in [H.parent/'hc_quetes_merge_20261101',H.parent/'hc_quetes_merge_c1'] if p.exists())
build.JOURNAL_FILENAME=build_hf9.DEST
sys.path.insert(0,str(C1))
source=(C1/'run_regression.py').read_text()
label='pq02_full_system_hf9_'+uuid.uuid4().hex[:8]
source=source.replace('hcmerge_full_system',label)
source=source.replace("shutil.copyfile(H/'output'/reconcile.DEST,REG/'output'/reconcile.DEST)","shutil.copyfile(OLD/'output'/reconcile.DEST,REG/'output'/reconcile.DEST)")
needle="source=source.replace(needle,needle+\"\\nsources.append(H/'tests/HcQuetesMergeBench.java')\")"
assert source.count(needle)==1
source=source.replace(needle,"source=source.replace(needle,needle+\"\\nsources.append(H/'tests/HcQuetesMergeBench.java')\\nsources.append(H/'tests/Pq02PacingBench.java')\")")
exec(compile(source,str(C1/'run_regression.py'),'exec'),{'__name__':'__main__','__file__':str(C1/'run_regression.py')})
for suffix in ['.txt','_fixture.json','_server.log']:
 shutil.copyfile(C1/'regression/reports'/(label+suffix),H/'reports'/('hf9_native'+suffix))
