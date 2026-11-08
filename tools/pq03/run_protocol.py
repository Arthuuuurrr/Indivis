from pathlib import Path
import os,subprocess,shutil,json,hashlib
import build
H=Path(__file__).resolve().parent;OLD=H.parent/'pq02';REG=H/'protocol_regression';REG.mkdir(exist_ok=True)
for folder in ['reference','audit_prologue']:
 if not (REG/folder).exists():(REG/folder).symlink_to(OLD/folder,target_is_directory=True)
shutil.copytree(OLD/'tests',REG/'tests',dirs_exist_ok=True)
for name in ['Pq03ProtocolBench.java','Pq03CoexistenceBench.java']:shutil.copy2(H/'tests'/name,REG/'tests'/name)
(REG/'reports').mkdir(exist_ok=True);(REG/'output').mkdir(exist_ok=True);build.JOURNAL_FILENAME=build.FILES['mod'];shutil.copy2(H/'output'/build.JOURNAL_FILENAME,REG/'output'/build.JOURNAL_FILENAME)
source=(OLD/'run_protocol.py').read_text().replace("RT=Path('/tmp/indivis_pq02_runtime')",'RT=Path('+repr(str(build.RT.resolve()))+')')
coexist=os.environ.get('PQ03_PROTOCOL_MODE')=='pq02'
driver='Pq03CoexistenceBench' if coexist else 'Pq03ProtocolBench';label='protocol_pq02_with_pq03' if coexist else 'protocol_pq03'
# The graphical surface remains a test double; provide its real width field for overlay layout.
screen=OLD/'reference/tools/pq01_full/tests/native_capture_doubles/net/minecraft/class_437.java'
(REG/'hf1_client_doubles').mkdir(exist_ok=True)
(REG/'hf1_client_doubles/class_437.java').write_text(screen.read_text().replace('public int field_22790=540;', 'public int field_22790=540;public int field_22789=960;'))
source=source.replace("fixture/'net/minecraft/class_437.java'","H/'hf1_client_doubles/class_437.java'")
source=source.replace('Pq02ProtocolBench',driver).replace('protocol_hf8',label).replace("H.parents[1]/'upload'","build.ROOT/'upload'")
source=source.replace('all 111 nodes and readiness','111 PQ02 nodes with PQ03 listener' if coexist else '57 PQ03 nodes and readiness')
if coexist:source=source.replace('sources=[','sources=[H/\'tests/Pq02ProtocolBench.java\',')
exec(compile(source,str(REG/'run_protocol.py'),'exec'),{'__name__':'__main__','__file__':str(REG/'run_protocol.py')})
for name in [label+'.txt',label+'_fixture.json']:shutil.copy2(REG/'reports'/name,H/'reports'/name)
