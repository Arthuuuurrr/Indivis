"""Serial real-client checks; every report is tied to the final binary hash."""
from pathlib import Path
import json,os,subprocess,sys
R=Path(__file__).resolve().parent;J=R/'runtime/jdk/bin';jar=R/'out/NexusCharacters-Indivis-1.21.11-PRE35-SI1-OPT3-BB1.jar'
def run(*args,env=None):subprocess.run(list(map(str,args)),check=True,env=env)
run(sys.executable,R/'build.py')
asm=':'.join(map(str,(R/'runtime/libs/org/ow2/asm').rglob('*.jar')));dest=R/'build/audit';dest.mkdir(exist_ok=True)
run(J/'javac','-cp',asm,'-d',dest,R/'tools/AuditBlockbench.java',R/'tests/ServerCatalogueRegression.java')
run(J/'java','-cp',str(dest)+':'+asm,'AuditBlockbench',R/'base/NexusCharacters-Indivis-1.21.11-PRE35-SI1-OPT3(1).jar',jar,R/'qa/audit.json')
deps=[jar,R/'runtime/client-intermediary.jar',R/'runtime/fabric-loader.jar',*(R/'runtime/libs').rglob('*.jar')]
run(J/'java','-cp',dest,'ServerCatalogueRegression',R/'qa/server-linkage.txt',':'.join(map(str,deps)))
env=os.environ.copy();env.update(NEXUS_TEST_JAR=str(jar),NPC_EXTERNAL_DISPLAY='xorg',NPC_DISPLAY='138',NPC_HEAP='2600M')
runner=R/'previous/npc/run_client.py'
baseline=R/'previous/npc/qa/last-bb1-base.json'
if not baseline.exists():
    old=env.copy();old.update(NEXUS_TEST_JAR=str(R/'base/NexusCharacters-Indivis-1.21.11-PRE35-SI1-OPT3(1).jar'),NPC_QA_MODE='menus-baseline',NPC_RUN_LABEL='bb1-base');run(sys.executable,runner,env=old)
for mode,label in [('blockbench','bb1-client'),('menus','bb1-legacy'),('bbnpc','bb1-npc'),('bbnpc','bb1-npc-reload')]:
    current=env.copy();current.update(NPC_QA_MODE=mode,NPC_RUN_LABEL=label)
    if label.endswith('reload'):current['NPC_RELOAD_FROM']=json.loads((R/'previous/npc/qa/last-bb1-npc.json').read_text())['game']
    run(sys.executable,runner,env=current)
run(sys.executable,R/'review_images.py',R/'previous/npc/qa/evidence-bb1-client')
run(sys.executable,R/'package_release.py')
