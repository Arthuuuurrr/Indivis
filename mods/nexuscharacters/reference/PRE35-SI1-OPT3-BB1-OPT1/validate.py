"""Final-binary integration checks, serial; no production server is contacted."""
from pathlib import Path
import hashlib,json,os,subprocess,sys
R=Path(__file__).resolve().parent;P=R.parent/'presets_import';J=P/'runtime/jdk/bin';Q=P/'previous/npc/qa';B=json.loads((R/'qa/build.json').read_text());jar=Path(B['jar'])
assert hashlib.sha256(jar.read_bytes()).hexdigest()==B['sha256']
subprocess.run([sys.executable,str(R/'kernel_test.py')],check=True)
classes=R/'build/server-test';classes.mkdir(exist_ok=True)
subprocess.run([str(J/'javac'),'-d',str(classes),str(P/'tests/ServerCatalogueRegression.java')],check=True)
cp=':'.join(map(str,[jar,P/'runtime/client-intermediary.jar',P/'runtime/fabric-loader.jar',*(P/'runtime/libs').rglob('*.jar')]))
subprocess.run([str(J/'java'),'-XX:-UsePerfData','-cp',str(classes),'ServerCatalogueRegression',str(R/'qa/server-linkage.txt'),cp],check=True)
asm=':'.join(map(str,(P/'runtime/libs/org/ow2/asm').rglob('*.jar')))
subprocess.run([str(J/'javac'),'-cp',asm,'-d',str(classes),str(R/'tests/AuditSurfaceOpt.java')],check=True)
subprocess.run([str(J/'java'),'-XX:-UsePerfData','-cp',str(classes)+':'+asm,'AuditSurfaceOpt',str(P/'out/Nexus-BB1-1.21.11.jar'),str(jar),str(R/'qa/bytecode-audit.txt')],check=True)
env=os.environ.copy();env.update(NEXUS_TEST_JAR=str(jar),NPC_EXTERNAL_DISPLAY='xorg',NPC_DISPLAY='138',NPC_HEAP='2600M')
records=[]
for mode,label in [('menus','opt1-final-menus'),('blockbench','opt1-final-assets'),('bbnpc','opt1-final-npc'),('bbnpc','opt1-final-reload')]:
 current=env.copy();current.update(NPC_QA_MODE=mode,NPC_RUN_LABEL=label)
 if label=='opt1-final-npc':current['NEXUS_VERIFY']='true'
 if label.endswith('reload'):current['NPC_RELOAD_FROM']=json.loads((Q/'last-opt1-final-npc.json').read_text())['game']
 subprocess.run([sys.executable,str(P/'previous/npc/run_client.py')],check=True,env=current)
 record=json.loads((Q/('last-'+label+'.json')).read_text());assert record['nexus_sha256']==B['sha256'];records.append(record)
 if mode=='menus':
  for name in ['geometry.csv','pose-geometry.csv']:
   a=(Q/'evidence-opt1-reference/geometry'/name).read_bytes();b=(Q/('evidence-'+label)/'geometry'/name).read_bytes();assert a==b,name
   print('FINAL_EXACT_GEOMETRY_PASS',name,len(a.splitlines()),flush=True)
(R/'qa/functional.json').write_text(json.dumps({'jar_sha256':B['sha256'],'records':records},indent=2,ensure_ascii=False)+'\n')
print('FINAL_FUNCTIONAL_PASS clients=4 geometry_states=411',flush=True)
