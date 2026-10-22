"""Tests du contrôleur réel, de Nexus fourni et des archives finales ; pas un serveur Minecraft."""
import csv,importlib.util,io,json,sys,unittest,zipfile,subprocess
from pathlib import Path
import build as native
HERE=native.HERE;ROOT=native.ROOT;OUT=native.OUT

def module(name,path):
 spec=importlib.util.spec_from_file_location(name,path);m=importlib.util.module_from_spec(spec);spec.loader.exec_module(m);return m

def run(args,log):
 p=subprocess.run(list(map(str,args)),cwd=ROOT,capture_output=True,text=True);(OUT/log).write_text(p.stdout+p.stderr)
 if p.returncode:raise RuntimeError(log+'\n'+p.stdout+p.stderr)
 return p.stdout

def validate():
 old=module('r2_build',ROOT/'work/pq01_rebuild/build.py');old.OUT=OUT;old.FILES={k:native.FILES[k] for k in ('core','quests')}
 sys.modules['build']=old
 try:base=module('r2_tests',ROOT/'work/pq01_rebuild/validate.py')
 finally:sys.modules['build']=native
 stream=io.StringIO();result=unittest.TextTestRunner(stream=stream,verbosity=2).run(unittest.defaultTestLoader.loadTestsFromTestCase(base.CandidateTests))
 (OUT/'datapack_tests.log').write_text(stream.getvalue());print(stream.getvalue());assert result.wasSuccessful()
 tests=HERE/'tests';bench=HERE/'test_classes';bench.mkdir(exist_ok=True)
 nexus=next((ROOT/'upload').glob('NexusCharacters*.jar'));nexus_classes=HERE/'nexus_classes';entry='net/tompsen/nexuscharacters/V2QuestPolicy.class'
 with zipfile.ZipFile(nexus) as z:p=nexus_classes/entry;p.parent.mkdir(parents=True,exist_ok=True);p.write_bytes(z.read(entry))
 source=(ROOT/'work/pq01_rebuild/tests/NexusPolicyBench.java').read_text().replace('"hcm_pq1_poll"','"hcm_pq1_poll","hcm_pq1_wait","hcm_pq1_camera"')
 (tests/'NexusPolicyBench.java').write_text(source)
 javac=['java','--module','jdk.compiler/com.sun.tools.javac.Main']
 cp=str(HERE/'classes')+':'+str(nexus_classes)
 run([*javac,'-cp',cp,'-d',bench,*sorted(tests.glob('*.java'))],'test_compile.log')
 motion=run(['java','-cp',str(bench)+':'+cp,'Pq01MotionBench'],'motion_tests.log');print(motion)
 policy=run(['java','-cp',str(bench)+':'+cp,'NexusPolicyBench'],'nexus_policy.log');assert policy.startswith('PASS 44');print(policy)
 before={k:dict(v) for k,v in base.BEFORE.items()};before['journal']=native.load(native.JAR)
 after={k:native.load(OUT/f) for k,f in native.FILES.items()}
 journal=after['journal'];original=before['journal'];vis='net/hautecapitale/quetes/visibility/QuestVisibility.class'
 preserved_classes=[p for p in original if p.endswith('.class') and p!=vis]
 assert all(journal[p]==original[p] for p in preserved_classes)
 assert set(original)<=set(journal)
 for forbidden in ('org/spongepowered/','net/minecraft/','net/fabricmc/','PatchVisibility.class','VerifyClasses.class'):
  assert not any(p.startswith(forbidden) for p in journal if p not in original),forbidden
 meta=json.loads(journal['fabric.mod.json']);assert 'net.hautecapitale.quetes.sequence.Pq01Initializer' in meta['entrypoints']['main']
 assert json.loads(journal['indivis_pq01.mixins.json'])['injectors']['defaultRequire']==1
 core=after['core'];f=lambda p:core[native.func(p)].decode()
 for role in base.N:
  assert f('node/'+role)==base.body('node/'+role)
 assert f('dispatch_self').index('matches 15')<f('dispatch_self').index('hcm_pq1_wait matches 1')
 assert 'hcm_pq1_camera matches 2 run return 0' in f('open/geraud_self')
 assert 'scene/approach_self' in f('choice/a2') and 'scene/return_self' in f('choice/a6')
 for role in base.ROLES if hasattr(base,'ROLES') else ('geraud','altheon','gautier','hugues','anselme','perrin'):
  for phase in ('large','quai'):
   for path,cmd in [('liaison/','bind_'),('liaison/liberer_','unbind_'),('pose/placer_','place_')]:
    assert 'indivispq01 '+cmd+role+'_'+phase in f(path+role+'_'+phase)
 assert [base.IDS['a'+str(i)] for i in range(1,9)]==list(range(1,9))
 diffs={}
 with (OUT/'DIFFS_DEPUIS_SI1_EXHAUSTIFS.csv').open('w',newline='') as out:
  w=csv.writer(out);w.writerow(['archive','modification','chemin','sha256_avant','sha256_apres'])
  for k,pack in after.items():
   oldpack=before[k];d={kind:[] for kind in ('changed','added','removed')}
   for p in sorted(set(oldpack)|set(pack)):
    if oldpack.get(p)==pack.get(p):continue
    kind='added' if p not in oldpack else 'removed' if p not in pack else 'changed';d[kind].append(p)
    w.writerow([k,kind,p,native.sha(oldpack[p]) if p in oldpack else '',native.sha(pack[p]) if p in pack else ''])
   diffs[k]={**d,'preserved':sum(pack.get(p)==b for p,b in oldpack.items())}
 (OUT/'diffs_depuis_si1.json').write_text(json.dumps(diffs,ensure_ascii=False,indent=2)+'\n')
 report={'status':'PASS','minecraft_runtime_test':False,'datapack_test_cases':result.testsRun,'motion_test_cases':17,'motion_log':motion.strip(),'nexus_policy_assertions':44,'journal_original_classes_byte_identical':len(preserved_classes),'journal_original_class_patched':vis,'nodes':len(base.N),'approved_dialogue_occurrences':145,'candidate_dependencies':base.dependencies(base.AFTER),'baseline_dependencies':base.dependencies(base.BEFORE),'diff_summary':{k:{s:len(v[s]) for s in ('changed','added','removed')} for k,v in diffs.items()},'bytecode':(OUT/'bytecode.log').read_text().strip(),'limits':['Aucun serveur Minecraft/Fabric ni monde démarré ici.','BasicVerifier ne résout pas les API Minecraft.','La politique Nexus est testée directement ; les changements de personnage en jeu restent à recetter.','Navigation, rendu des modèles et caméra à vérifier sur la copie du monde avec les PNJ réels.']}
 (OUT/'validation.json').write_text(json.dumps(report,ensure_ascii=False,indent=2)+'\n');return report
if __name__=='__main__':validate()
