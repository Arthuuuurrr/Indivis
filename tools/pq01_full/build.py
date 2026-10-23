from pathlib import Path
import hashlib,json,zipfile,subprocess,sys,shutil,os

HERE=Path(__file__).resolve().parent;ROOT=HERE.parents[1];OUT=HERE/'output';OUT.mkdir(exist_ok=True)
sys.path.insert(0,str(ROOT/'work/pq01_rebuild'))
from model import N,IDS,ROLES
R2=HERE/'r2'
JAR=ROOT/'upload/haute-capitale-quetes-0.1.0+1.21.11.b7-SEQUENCE-I-SI1b.jar'
FILES={'core':'capitale_core_1.5.7_V2_SEQUENCE_I_SI1_PQ01_R3_HF5.zip','quests':'hc_quetes_b22_V2_SEQUENCE_I_SI1b_PQ01_R3_HF4.zip','journal':'haute-capitale-quetes-0.1.0+1.21.11.b7-SEQUENCE-I-SI1b-PQ01-R3-HF5.jar'}
def sha(b):return hashlib.sha256(b).hexdigest()
def load(p):
 with zipfile.ZipFile(p) as z:assert z.testzip() is None;return {n:z.read(n) for n in z.namelist() if not n.endswith('/')}
def save(pack,p):
 temp=p.with_suffix(p.suffix+'.partial')
 with zipfile.ZipFile(temp,'w',zipfile.ZIP_DEFLATED,compresslevel=9) as z:
  for n,b in sorted(pack.items()):
   info=zipfile.ZipInfo(n,(2026,10,22,0,0,0));info.compress_type=zipfile.ZIP_DEFLATED;z.writestr(info,b)
 with zipfile.ZipFile(temp) as z:assert z.testzip() is None
 temp.replace(p)
def run(args,log):
 r=subprocess.run(list(map(str,args)),cwd=ROOT,capture_output=True,text=True);(OUT/log).write_text(r.stdout+r.stderr)
 if r.returncode:raise RuntimeError(log+'\n'+r.stdout+r.stderr)
def func(p):return 'data/capitale/function/pq01/'+p+'.mcfunction'
def build():
 inputs={'core':R2/'capitale_core_1.5.7_V2_SEQUENCE_I_SI1_PQ01_R2.zip','quests':R2/'hc_quetes_b22_V2_SEQUENCE_I_SI1b_PQ01_R2.zip','journal':JAR}
 assert sha(JAR.read_bytes())=='7d17bc58795f47561a873bff7ce07ecbb3838ba521bddc9a9c2bdd7e86eb8665'
 baseline={k:load(p) for k,p in inputs.items()};packs={k:v.copy() for k,v in baseline.items()};core=packs['core']
 def f(p,lines):core[func(p)]=('# PQ01-R3 — scène personnelle et commande vérifiée à l’assemblage.\n'+'\n'.join(lines)+'\n').encode()
 core[func('load')]+=b'\nscoreboard objectives add hcm_pq1_wait dummy\nscoreboard objectives add hcm_pq1_camera dummy\n'
 core[func('close_self')]=b'indivispq01 close\n'+core[func('close_self')]
 for role in ROLES:
  p=func('open/'+role+'_self');s=core[p].decode()
  s='\n'.join(l for l in s.splitlines() if not l.startswith('hcd_interpellation nearest_named'))+'\n'
  owner=91+list(ROLES).index(role)
  marker='scoreboard players set @s hcm_pq1_role '+str(owner)+'\n'
  assert s.count(marker)==1
  camera='execute store result score @s hcm_pq1_camera run indivispq01 camera_'+role+'\n'
  camera+='execute unless score @s hcm_pq1_camera matches 1..2 run hcd_interpellation nearest_named '+ROLES[role]+'\n'
  if role=='geraud':camera+='execute if score @s hcm_pq1_camera matches 2 run return 0\n'
  core[p]=s.replace(marker,marker+camera).encode()
 for old,new,action in [('a2','a3','approach'),('a6','a7','return')]:
  p=func('choice/'+old);s=core[p].decode();before='function capitale:pq01/node/'+new
  assert before in s;core[p]=s.replace(before,'function capitale:pq01/scene/'+action+'_self').encode()
  f('scene/'+action+'_self',[
   'execute store result score @s hcm_pq1_camera run indivispq01 '+action,
   'execute unless score @s hcm_pq1_camera matches 1 run function capitale:pq01/node/'+new])
 s=core[func('dispatch_self')].decode();marker='execute if score @s hcm_pq1_pick matches 15 run return run function capitale:pq01/close_self\n'
 assert marker in s;core[func('dispatch_self')]=s.replace(marker,marker+'execute if score @s hcm_pq1_wait matches 1 run return run function capitale:pq01/scene/wait_self\n').encode()
 f('scene/wait_self',['tellraw @s {"text":"Gautier est en déplacement. Le dialogue reprend à son arrivée ; vous pouvez toujours fermer.","color":"gray"}'])
 f('scene/resume_wait_self',['function capitale:pq01/buttons_self','function capitale:pq01/scene/wait_self','function capitale:pq01/scene/resume_close_macro with storage capitale:pq01_runtime buttons'])
 f('scene/resume_close_macro',['$tellraw @s {"text":"[Fermer la scène]","color":"gray","click_event":{"action":"run_command","command":"/trigger QuestChoix set $(b15)"}}'])
 f('banter_self',['execute store result score @s hcm_pq1_camera run indivispq01 banter','execute unless score @s hcm_pq1_camera matches 1 run function capitale:pq01/banter_unavailable_self'])
 for role in ROLES:
  for phase in ('large','quai'):
   key=role+'_'+phase
   f('liaison/'+key,['indivispq01 bind_'+key])
   f('liaison/liberer_'+key,['indivispq01 unbind_'+key])
   f('pose/placer_'+key,['indivispq01 place_'+key])
 for name in ('gautier_poste','gautier_approche','hugues_charge','anselme_instruments','perrin_a','perrin_b','gautier_poste_quai','hugues_charge_quai','anselme_instruments_quai','perrin_a_quai','perrin_b_quai'):f('pose/'+name,['indivispq01 point_'+name])
 f('admin/nettoyer_scenes',['indivispq01 cleanup'])
 f('admin/status',['indivispq01 status','scoreboard players get @s hcq_pq01','scoreboard players get @s hcf_pq1_test','scoreboard players get @s hcf_pq1_gain'])
 for path,blob in list(core.items()):
  if path.endswith('.mcfunction') and b'# PQ01-R2 \xe2\x80\x94 candidat texte, repli statique document\xc3\xa9.' in blob:
   core[path]=blob.replace(b'# PQ01-R2 \xe2\x80\x94 candidat texte, repli statique document\xc3\xa9.','# PQ01-R3 — dialogues approuvés et scènes personnelles.'.encode())
 for k in ('core','quests'):
  meta=json.loads(packs[k]['pack.mcmeta']);meta['pack']['description']=meta['pack']['description'].split(' — PQ01-R2')[0]+' — PQ01-R3 : dialogues, scènes individuelles mobiles et relecture'
  packs[k]['pack.mcmeta']=(json.dumps(meta,ensure_ascii=False,indent=2)+'\n').encode()
 from hf4 import apply
 apply(packs)
 from hf5 import apply as apply_hf5
 apply_hf5(packs)
 classes=HERE/'classes';contracts=HERE/'compile_contracts';contract_classes=HERE/'contract_classes'
 for p in (classes,contracts,contract_classes):shutil.rmtree(p,ignore_errors=True);p.mkdir()
 definitions={
 'net/fabricmc/api/ModInitializer.java':'package net.fabricmc.api; public interface ModInitializer { void onInitialize(); }',
 'net/fabricmc/api/ClientModInitializer.java':'package net.fabricmc.api; public interface ClientModInitializer { void onInitializeClient(); }',
 'net/minecraft/class_1297.java':'package net.minecraft; public class class_1297 { public java.util.Set<String> method_5752(){return null;} }',
 'net/minecraft/class_1352.java':'package net.minecraft; public class class_1352 {}',
 'net/hautecapitale/quetes/sequence/SequenceBridge.java':'''package net.hautecapitale.quetes.sequence; public class SequenceBridge {static Object call(Object o,String n,Object...a){return null;} static Object server(Object p){return null;} static java.util.UUID uuid(Object p){return null;} static int score(Object p,String n){return 0;} static void run(Object p,String c){} static String quote(String s){return s;} }''',
 'net/hautecapitale/quetes/sequence/GuardNbt.java':'''package net.hautecapitale.quetes.sequence; public class GuardNbt { static Object read(Object c,String k){return null;} static Object copy(Object c){return null;} static Object compound(){return null;} static void put(Object c,String k,Object v){} static void string(Object c,String k,String v){} static void integer(Object c,String k,int v){} static Object stringValue(String v){return null;} static Object list(java.util.Collection<?> v){return null;} static Object entity(Object e){return null;} static void save(Object s,String k,Object n){} static Object childOrCreate(Object c,String k){return null;} }''',
 'org/spongepowered/asm/mixin/Mixin.java':'package org.spongepowered.asm.mixin; @java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.CLASS) @java.lang.annotation.Target(java.lang.annotation.ElementType.TYPE) public @interface Mixin { String[] targets(); }',
 'org/spongepowered/asm/mixin/injection/At.java':'package org.spongepowered.asm.mixin.injection; @java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME) public @interface At { String value(); }',
 'org/spongepowered/asm/mixin/injection/Inject.java':'package org.spongepowered.asm.mixin.injection; @java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME) @java.lang.annotation.Target(java.lang.annotation.ElementType.METHOD) public @interface Inject { String[] method(); At[] at(); boolean cancellable() default false; }',
 'org/spongepowered/asm/mixin/injection/callback/CallbackInfo.java':'package org.spongepowered.asm.mixin.injection.callback; public class CallbackInfo { public void cancel(){} }',
 'org/spongepowered/asm/mixin/injection/callback/CallbackInfoReturnable.java':'package org.spongepowered.asm.mixin.injection.callback; public class CallbackInfoReturnable<T> extends CallbackInfo { public void setReturnValue(T v){} }'
 }
 mixin_classpath=os.environ.get('INDIVIS_MIXIN_CLASSPATH','')
 if mixin_classpath:definitions={n:s for n,s in definitions.items() if not n.startswith('org/spongepowered/')}
 for n,s in definitions.items():p=contracts/n;p.parent.mkdir(parents=True,exist_ok=True);p.write_text(s+'\n')
 ids=HERE/'java/net/hautecapitale/quetes/sequence/Pq01Ids.java'
 ids.write_text('package net.hautecapitale.quetes.sequence; import java.util.*; public final class Pq01Ids {private static final Map<String,Integer> IDS=Map.ofEntries('+','.join('Map.entry('+json.dumps(k)+','+str(v)+')' for k,v in IDS.items())+'); private static final Map<String,String> ROLES=Map.ofEntries('+','.join('Map.entry('+json.dumps(k)+','+json.dumps(N[k]['role'])+')' for k in IDS)+'); public static int id(String s){return IDS.get(s);} public static String role(String s){return ROLES.get(s);} public static Set<String> keys(){return IDS.keySet();} }\n')
 javac=['java','--module','jdk.compiler/com.sun.tools.javac.Main']
 run([*javac,'-d',contract_classes,*sorted(contracts.rglob('*.java'))],'compile_contracts.log')
 exports=['--add-exports','java.base/jdk.internal.org.objectweb.asm=ALL-UNNAMED','--add-exports','java.base/jdk.internal.org.objectweb.asm.tree=ALL-UNNAMED','--add-exports','java.base/jdk.internal.org.objectweb.asm.tree.analysis=ALL-UNNAMED']
 cp=str(contract_classes)+(os.pathsep+mixin_classpath if mixin_classpath else '')
 run([*javac,*exports,'-proc:none','-classpath',cp,'-sourcepath','','-d',classes,*sorted((HERE/'java').rglob('*.java'))],'compile.log')
 vis='net/hautecapitale/quetes/visibility/QuestVisibility.class';inp=HERE/'QuestVisibility.original.class';inp.write_bytes(packs['journal'][vis]);patched=HERE/'QuestVisibility.patched.class'
 run(['java',*exports,'-cp',classes,'PatchVisibility',inp,patched],'patch.log');packs['journal'][vis]=patched.read_bytes()
 for p in sorted((classes/'net/hautecapitale').rglob('*.class')):packs['journal'][str(p.relative_to(classes))]=p.read_bytes()
 meta=json.loads(packs['journal']['fabric.mod.json']);meta['version']='0.1.0+1.21.11.b7.SEQUENCE-I.SI1b.PQ01.R3.HF5';meta['description']+=' PQ01-R3-HF5 : proximité de Géraud et reprise après fermeture corrigées ; correctifs HF4 conservés.'
 meta['entrypoints']['main'].append('net.hautecapitale.quetes.sequence.Pq01Initializer')
 meta['entrypoints'].setdefault('client',[]).append('net.hautecapitale.quetes.sequence.Pq01Client')
 dialogue=load(next((ROOT/'upload').glob('haute-capitale-dialogue*.jar')));dialogue_id=json.loads(dialogue['fabric.mod.json'])['id'];meta['depends'][dialogue_id]='>=0.1.0'
 meta['mixins'].append('indivis_pq01.mixins.json');packs['journal']['fabric.mod.json']=(json.dumps(meta,ensure_ascii=False,indent=2)+'\n').encode()
 packs['journal']['indivis_pq01.mixins.json']=json.dumps({'required':True,'minVersion':'0.8','package':'net.hautecapitale.quetes.pq01.mixin','compatibilityLevel':'JAVA_21','mixins':['Pq01EntityMixin','Pq01GoalMixin'],'injectors':{'defaultRequire':1}},indent=2).encode()
 for n in definitions:assert n[:-5]+'.class' not in {p for p in packs['journal'] if p not in baseline['journal']},n
 report={}
 for k,pack in packs.items():
  save(pack,OUT/FILES[k]);base=baseline[k]
  report[k]={'input':inputs[k].name,'input_sha256':sha(inputs[k].read_bytes()),'output':FILES[k],'output_sha256':sha((OUT/FILES[k]).read_bytes()),'changed':sorted(p for p in base if pack[p]!=base[p]),'added':sorted(set(pack)-set(base)),'removed':sorted(set(base)-set(pack))}
 run(['java',*exports,'-cp',classes,'VerifyClasses',OUT/FILES['journal']],'bytecode.log')
 (OUT/'diffs.json').write_text(json.dumps(report,ensure_ascii=False,indent=2)+'\n')
 print(json.dumps({k:{'changed':len(v['changed']),'added':len(v['added']),'sha256':v['output_sha256']} for k,v in report.items()},indent=2));return packs,report
if __name__=='__main__':build()
