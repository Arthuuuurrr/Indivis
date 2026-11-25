"""Forward-compatibility fixture: three extra PNG assets, not edits to the JAR."""
from pathlib import Path
import hashlib,json,os,subprocess,sys
R=Path(__file__).resolve().parent;P=R.parent/'presets_import';B=json.loads((R/'qa/build.json').read_text());jar=Path(B['jar']);assert hashlib.sha256(jar.read_bytes()).hexdigest()==B['sha256']
env=os.environ.copy();env.update(NEXUS_TEST_JAR=str(jar),NPC_EXTERNAL_DISPLAY='xorg',NPC_DISPLAY='138',NPC_HEAP='2600M',NPC_QA_MODE='future',NPC_RUN_LABEL='opt1-final-future',NEXUS_VERIFY='false')
subprocess.run([sys.executable,str(P/'previous/npc/run_client.py')],check=True,env=env)
record=json.loads((P/'previous/npc/qa/last-opt1-final-future.json').read_text());assert record['nexus_sha256']==B['sha256'] and 'FUTURE_ASSETS hair=930 beard=930 outfit=930'in record['pass']
(R/'qa/future.json').write_text(json.dumps(record,ensure_ascii=False,indent=2)+'\n');print('FINAL_FUTURE_ASSETS_PASS hair=930 beard=930 outfit=930',flush=True)
