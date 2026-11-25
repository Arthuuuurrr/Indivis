"""Actual final-client checks. Timings with exact-oracle checks are not perf data."""
from pathlib import Path
import argparse,hashlib,json,os,subprocess,sys
p=argparse.ArgumentParser();p.add_argument('--resume',action='store_true');args=p.parse_args()
R=Path(__file__).resolve().parent;P=R.parent/'presets_import';J=P/'runtime/jdk/bin';Q=P/'previous/npc/qa';B=json.loads((R/'qa/build.json').read_text());jar=Path(B['jar']);assert hashlib.sha256(jar.read_bytes()).hexdigest()==B['sha256']
subprocess.run([sys.executable,str(R/'kernel_test.py')],check=True)
classes=R/'build/audit';classes.mkdir(exist_ok=True);asm=':'.join(map(str,(P/'runtime/libs/org/ow2/asm').rglob('*.jar')))
subprocess.run([str(J/'javac'),'-cp',asm,'-d',str(classes),str(R/'tests/AuditArmorPrepared.java'),str(P/'tests/ServerCatalogueRegression.java')],check=True)
subprocess.run([str(J/'java'),'-XX:-UsePerfData','-cp',str(classes)+':'+asm,'AuditArmorPrepared',str(R.parent/'opt_bb1_20261125/out/Nexus-BB1-OPT1-1.21.11.jar'),str(jar),str(R/'qa/bytecode-audit.txt')],check=True)
deps=':'.join(map(str,[jar,P/'runtime/client-intermediary.jar',P/'runtime/fabric-loader.jar',*(P/'runtime/libs').rglob('*.jar')]))
subprocess.run([str(J/'java'),'-XX:-UsePerfData','-cp',str(classes),'ServerCatalogueRegression',str(R/'qa/server-linkage.txt'),deps],check=True)
env=os.environ.copy();env.update(NEXUS_TEST_JAR=str(jar),NPC_EXTERNAL_DISPLAY='xorg',NPC_DISPLAY='138',NPC_HEAP='2600M')
records=[]
for mode,label in [('menus','arm1-final-menus'),('blockbench','arm1-final-assets'),('bbnpc','arm1-final-npc'),('bbnpc','arm1-final-reload'),('future','arm1-final-future')]:
 e=env.copy();e.update(NPC_QA_MODE=mode,NPC_RUN_LABEL=label,NEXUS_VERIFY='true'if mode=='menus'or label=='arm1-final-npc'else'false')
 if label.endswith('reload'):e['NPC_RELOAD_FROM']=json.loads((Q/'last-arm1-final-npc.json').read_text())['game']
 saved=Q/('last-'+label+'.json');reuse=False
 if args.resume and saved.exists():
  old=json.loads(saved.read_text());evidence=Path(old['game'])/'evidence';reuse=old['nexus_sha256']==B['sha256']and(evidence/'pass.txt').exists()and not(evidence/'failure.txt').exists()
 if reuse:print('RESUMED_CONFIRMED_PASS',label,flush=True)
 else:subprocess.run([sys.executable,str(P/'previous/npc/run_client.py')],check=True,env=e)
 rec=json.loads((Q/('last-'+label+'.json')).read_text());assert rec['nexus_sha256']==B['sha256'];records.append(rec)
 if mode=='menus':
  a=(Q/'evidence-opt1-final-menus/geometry/geometry.csv').read_text().splitlines();b=(Q/('evidence-'+label)/'geometry/geometry.csv').read_text().splitlines();assert len(a)==len(b)==320
  rows=[i for i,s in enumerate(a)if s.startswith('first-')];assert all(a[i]==b[i]for i in rows),'First-person geometry changed'
  assert len((Q/('evidence-'+label)/'geometry/pose-geometry.csv').read_text().splitlines())==91
  print('ARMOR_FIRST_PERSON_RAW_IDENTICAL states='+str(len(rows))+'; world topology/UV verified in-process against OPT1',flush=True)
 if mode=='future':assert 'FUTURE_ASSETS hair=930 beard=930 outfit=930'in rec['pass']
(R/'qa/functional.json').write_text(json.dumps({'jar_sha256':B['sha256'],'records':records},indent=2,ensure_ascii=False)+'\n');print('ARMOR_FINAL_FUNCTIONAL_PASS clients=5',flush=True)
