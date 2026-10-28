"""Serial client QA: never overlap CPU timings with another Minecraft client."""
from pathlib import Path
import json,os,subprocess
R=Path(__file__).resolve().parent;runner=R/'previous/npc/run_client.py'
base=R/'inputs/NexusCharacters-Indivis-1.21.11-PRE35-SI1.jar'
candidate=R/'out/NexusCharacters-Indivis-1.21.11-PRE35-SI1-OPT2.jar'
def run(label,jar,mode='npc',perf=False,verify=False,reload=None):
    env=os.environ.copy();env.update(NEXUS_TEST_JAR=str(jar),NPC_RUN_LABEL=label,NPC_QA_MODE=mode,NPC_DISPLAY='137',NPC_HEAP='2400M',OPT_PERF=str(perf).lower(),NEXUS_VERIFY=str(verify).lower())
    if reload:env['NPC_RELOAD_FROM']=str(reload)
    print('QA_NEXT',label,flush=True);subprocess.run(['python3',str(runner)],env=env,check=True)
if __name__=='__main__':
    run('opt2-perf',candidate,perf=True)
    run('opt2-menus',candidate,mode='menus')
    run('opt2-oracle-future',candidate,mode='future',verify=True)
    data=json.loads((R/'previous/npc/qa/last-opt2-perf.json').read_text())
    run('opt2-reload',candidate,mode='visual',reload=Path(data['game']))
    print('ALL_CLIENT_QA_COMPLETE',flush=True)
