"""Assemble PQ02 dans le journal HF12 existant, avec correctif ciblé du rythme de Gautier."""
from pathlib import Path
import json,hashlib,zipfile,subprocess,os,shutil
import build
H=Path(__file__).resolve().parent
OUTPUT=H/'output'/build.JOURNAL_FILENAME
BASE=build.ROOT/'upload/haute-capitale-quetes-0.1.0+1.21.11.b7-SEQUENCE-I-SI1b-PQ01-R3-HF12(1).jar'
COMPANION=H/'output'/build.FILES['mod']
SHA_BASE='c888eec4fa5dad35127dd5a5e4e6c49ec0f870063d429877f4b2a48a09f5bb60'
def sha(p):return hashlib.sha256(p.read_bytes()).hexdigest()
def read(p):
 with zipfile.ZipFile(p) as z:
  assert len(z.namelist())==len(set(z.namelist())),p
  assert z.testzip() is None,p
  return {n:z.read(n) for n in z.namelist() if not n.endswith('/')}
def main():
 expected=json.loads((H/'reports/build.json').read_text())['files']['mod']['sha256']
 assert sha(BASE)==SHA_BASE and sha(COMPANION)==expected
 base=read(BASE);pq=read(COMPANION);meta=json.loads(base['fabric.mod.json']);extra=json.loads(pq['fabric.mod.json'])
 assert meta['id']=='haute_capitale_quetes' and extra['id']=='indivis_pq02'
 result=base.copy()
 # Correctif PQ01 explicitement demandé : deux classes, pas de réécriture du prologue.
 rt=Path('/tmp/indivis_pq02_runtime');javac=next((rt/'jdk').glob('*/bin/javac'))
 patch_classes=H/'pq01_patch_classes';shutil.rmtree(patch_classes,ignore_errors=True);patch_classes.mkdir()
 cp=os.pathsep.join(map(str,[BASE,*sorted((rt/'libraries').glob('*.jar'))]))
 r=subprocess.run([javac,'--release','21','-proc:none','-sourcepath','','-cp',cp,'-d',patch_classes,H/'src_pq01/Pq01Bridge.java'],capture_output=True,text=True)
 (H/'reports/hf3_compile_pq01.log').write_text(r.stdout+r.stderr);assert r.returncode==0,r.stderr
 patched_pq01=['net/hautecapitale/quetes/sequence/Pq01Bridge.class','net/hautecapitale/quetes/sequence/Pq01Bridge$1.class']
 for name in patched_pq01:assert name in base;result[name]=(patch_classes/name).read_bytes()
 for n,b in pq.items():
  if n=='fabric.mod.json':continue
  if n in result:assert result[n]==b,'Conflicting resource '+n
  result[n]=b
 for kind,entries in extra['entrypoints'].items():
  target=meta.setdefault('entrypoints',{}).setdefault(kind,[])
  for entry in entries:
   if entry not in target:target.append(entry)
 for mixin in extra['mixins']:
  if mixin not in meta.setdefault('mixins',[]):meta['mixins'].append(mixin)
 # Preserve the original dependency requirements, strengthening only the PQ02 minima.
 meta['depends'].update({'minecraft':'1.21.11','fabricloader':'>=0.19.5','fabric-api':'>=0.141.6','easy_npc':'7.16.0'})
 assert 'haute_capitale_quetes' not in meta['depends']
 meta.setdefault('breaks',{})['indivis_pq02']='*'
 meta['version']='0.1.0+1.21.11.b7.SEQUENCE-I.SI1b.PQ01.R3.HF12.PQ02.R1.HF4'
 meta['description']+=' PQ02-R1-HF4 intégré : Le registre du Port, escortes privées, dialogues et récompenses par personnage ; préparation adaptée aux repères existants ; ouverture de dialogue hors du contexte des fonctions imbriquées ; confirmation de disponibilité de la capture privée avant envoi des pages et reprise de Géraud pendant le retour de Gautier.'
 result['fabric.mod.json']=(json.dumps(meta,ensure_ascii=False,indent=2)+'\n').encode()
 build.write(OUTPUT,result)
 actual=read(OUTPUT)
 preserved=[n for n in base if n!='fabric.mod.json' and n not in patched_pq01];added=[n for n in actual if n not in base]
 assert all(actual[n]==base[n] for n in preserved)
 assert all(actual[n]==pq[n] for n in pq if n!='fabric.mod.json')
 assert not any(n.endswith('.jar') for n in added),'Not a jar-in-jar bundle'
 assert not any('Bench' in n or n.startswith(('net/minecraft/','net/tompsen/')) for n in added)
 for kind,entries in meta['entrypoints'].items():
  assert len(entries)==len(set(entries))
  for entry in entries:
   cls=entry if isinstance(entry,str) else entry['value'];assert cls.replace('.','/')+'.class' in actual
 for mixin in meta['mixins']:
  name=mixin if isinstance(mixin,str) else mixin['config'];assert name in actual
 report={'status':'ASSEMBLED_AND_BYTE_VERIFIED_NATIVE_PENDING','file':OUTPUT.name,'sha256':sha(OUTPUT),'bytes':OUTPUT.stat().st_size,'id':meta['id'],'version':meta['version'],'source_hf12_sha256':SHA_BASE,'source_pq02_sha256':expected,'original_entries_preserved':len(preserved),'original_classes_preserved':sum(n.endswith('.class') for n in preserved),'pq02_entries_identical':len(pq)-1,'added':sorted(added),'changed_original':['fabric.mod.json',*patched_pq01],'pq01_patch_sources':['src_pq01/Pq01Bridge.java'],'pq01_scope':'Gautier speed 1.0 to 1.25; Geraud resumes while Gautier returns to post' ,'entrypoints':meta['entrypoints'],'mixins':meta['mixins'],'depends':meta['depends'],'breaks':meta['breaks'],'datapacks_unchanged':{k:sha(H/'output'/build.FILES[k]) for k in ['core','quests']}}
 (H/'reports/merge.json').write_text(json.dumps(report,ensure_ascii=False,indent=2)+'\n')
 print(json.dumps(report,ensure_ascii=False,indent=2))
if __name__=='__main__':main()
