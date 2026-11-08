"""PQ03 R1 : copie des deux archives installées et extension du seul journal HF9."""
from pathlib import Path
import json,hashlib,zipfile,os,subprocess,shutil,re
import model
H=Path(__file__).resolve().parent;ROOT=H.parents[1];OUT=H/'output';OUT.mkdir(exist_ok=True);R=H/'reports';R.mkdir(exist_ok=True)
RT=H.parent/'pq02/runtime_hf9'
BASE={'core':ROOT/'upload/capitale_core_1.5.7_V2_SEQUENCE_I_SI1_PQ01_R3_HF9_QMARK2_PQ02_R1_HF8(2).zip','quests':ROOT/'upload/hc_quetes_b22_V2_SEQUENCE_I_SI1b_PQ01_R3_HF4_QMARK2_PQ02_R1_HF8_FUSION_C1 (1).zip','mod':H/'baseline_mod/haute-capitale-quetes-HF12-PQ02-R1-HF9-1.21.11.jar'}
EXPECTED={'core':'6ff671f7b71ff696980b1fdfa4302481dec30cac7db8eca6e0fb2c9bcd4a9aa6','quests':'d9a0fb0d0aecd0eb4f9bc64f9cb7bae932ddaaa7f67273b042512c22ff9e67aa','mod':'bce5ff70728f9e72617db734ecf83f1043fe343687a3806c3c3cd8841bdcae11'}
FILES={'core':'capitale_core_1.5.7_V2_SEQUENCE_I_SI1_PQ01_R3_HF9_QMARK2_PQ02_R1_HF8_PQ03_R1_HF1.zip','quests':'hc_quetes_b22_V2_SEQUENCE_I_SI1b_PQ01_R3_HF4_QMARK2_PQ02_R1_HF8_FUSION_C1_PQ03_R1.zip','mod':'haute-capitale-quetes-HF12-PQ02-HF9-PQ03-R1-HF1-1.21.11.jar'}
SCORES=['CAP_PQ3_VER','CAP_PQ3_NODE','CAP_PQ3_DA','CAP_PQ3_DB','CAP_PQ3_RA','CAP_PQ3_RB','CAP_PQ3_IMPR','CAP_PQ3_WP','CAP_PQ3_PAUSE','CAP_PQ3_REW','CAP_PQ3_END','CAP_PQ3_RECALL','CAP_PQ3_RUMBLE','CAP_PQ3_BRANCH']
def sha(b):return hashlib.sha256(b).hexdigest()
def read(path):
 with zipfile.ZipFile(path) as z:
  assert z.testzip() is None and len(z.namelist())==len(set(z.namelist()))
  return {n:z.read(n) for n in z.namelist() if not n.endswith('/')}
def write(path,files):
 with zipfile.ZipFile(path,'w',zipfile.ZIP_DEFLATED,compresslevel=9) as z:
  for n,b in sorted(files.items()):i=zipfile.ZipInfo(n,(2026,11,7,0,0,0));i.compress_type=zipfile.ZIP_DEFLATED;z.writestr(i,b)
 with zipfile.ZipFile(path) as z:assert z.testzip() is None
def main():
 for k,p in BASE.items():assert sha(p.read_bytes())==EXPECTED[k],k
 old={k:read(p) for k,p in BASE.items()};packs={k:d.copy() for k,d in old.items()};core=packs['core'];quests=packs['quests'];mod=packs['mod'];reasons={}
 def put(path,body,why):
  n='data/capitale/function/'+path+'.mcfunction';core[n]=('# PQ03-R1 — '+why+'\n'+body.rstrip()+'\n').encode();reasons[n]=why
 prefix='data/capitale/function/quest/sous_le_regard_du_coeur/'
 for n in old['core']:
  if not n.startswith(prefix) or not n.endswith('.mcfunction'):continue
  path=n[len('data/capitale/function/'):-11];part=n[len(prefix):]
  if part.startswith(('escort/waypoints/','escort/debug/')):continue
  if part.startswith('aurele/'):
   action='near' if part.split('/')[-1].startswith('near_') else 'aurele'
   put(path,'indivispq03 '+action,'Entrée historique conservée ; scène privée du personnage actif')
  elif part.startswith('sequence/'):
   put(path,'return 0','Ancien minuteur neutralisé ; progression par le dialogue privé explicite')
  else:
   cmd='indivispq03 clear_scenes' if 'failsafe' in part else 'indivispq03 pause' if part=='escort/stop_self.mcfunction' else 'indivispq03 prepare_route' if part=='escort/setup/summon_guide_here.mcfunction' else 'indivispq03 bind_aurele' if part=='escort/setup/tag_nearest_npc.mcfunction' else 'indivispq03 aurele' if part.startswith('escort/start_to_') else 'return 0'
   put(path,cmd,'Remplacement du guide mondial par le pathfinding privé ; aucun sélecteur de propriétaire nearest')
 for n in old['core']:
  if n.startswith('data/capitale/function/npc/aurele/') and n.endswith('.mcfunction'):
   if n.endswith('near_debug_self.mcfunction'):continue
   path=n[len('data/capitale/function/'):-11]
   if 'interact' in n:body='function capitale:pq03/open_self'
   elif n.endswith(('near_npc.mcfunction','near_distance_npc.mcfunction','near_self.mcfunction')):body='execute if entity @s[type=minecraft:player] run indivispq03 near\nexecute unless entity @s[type=minecraft:player] at @s as @a[distance=..3] at @s run indivispq03 near'
   else:body='indivispq03 near'
   put(path,body,'Contexte exact de chaque joueur ; proximité bornée à trois blocs')
 put('pq03/open_self','execute unless predicate hc_quetes:migration/state/pq02/100_to_ run return 0\nindivispq03 aurele','PQ02 doit être terminée ; aucune progression anticipée')
 # Preserve all aliases but prevent old public actions from invoking global escort logic.
 n=prefix+'aurele/interact_self.mcfunction';core[n]=b'# PQ03-R1\nfunction capitale:pq03/open_self\n'
 put('pq03/load','\n'.join('scoreboard objectives add '+s+' dummy' for s in SCORES+['hcm_pq3_ui','hcm_pq3_check'])+'\nscoreboard objectives add Pq03Choix trigger','Scores CAP_ persistants par Nexus ; aucune réinitialisation de joueur')
 put('pq03/complete_once_self','''execute unless score @s hcq_pq03 matches 90 run return 0
execute unless score @s CAP_PQ3_END matches 1 run return 0
execute unless score @s CAP_PQ3_DB matches 1..4 run return 0
execute if score @s CAP_PQ3_REW matches 1.. run return 0
execute store result score @s hcm_pq3_check run indivispq03 rewards_ready
execute unless score @s hcm_pq3_check matches 1 run return 0
execute store result score @s hcm_pq3_check run indivispq03 reputation_ready
execute unless score @s hcm_pq3_check matches 1 run return 0
scoreboard players set @s CAP_PQ3_REW 1
function capitale:rewards/daily_bonus/roll/tech_self
function capitale:reward/skills/core/sous_regard_coeur_self
function hc_quetes:migration/write/pq03/v100
scoreboard players set @s QUEST_PROLOGUE 40
scoreboard players set @s CAP_QUETEACTIVE 0
scoreboard players set @s CAP_GUIDE_LOCK 0
scoreboard players set @s CAP_GUIDE_ID 0
scoreboard players set @s CAP_GUIDE_MISS_T 0
title @s times 10 70 20
title @s title {"text":"Quête terminée","color":"gold","bold":true}
title @s subtitle {"text":"Sous le regard du Cœur","color":"white"}
function capitale:dialogue/sound/gain_quete_self
return 1''','Clôture effective C06 ; réputation déjà appliquée aux choix ; autres récompenses conservées')
 put('pq03/reputation/instant_ready_self','return 1','Contrat HF1 : gains immédiats, sans versement de réputation à la conclusion')
 for name,command in [('liaison/aurele','bind_aurele'),('admin/preparer_parcours','prepare_route'),('admin/diagnostic','status'),('admin/diagnostic_points','points'),('admin/nettoyer_scenes','clear_scenes'),('admin/confirmer_grondement','rumble_on'),('admin/desactiver_grondement','rumble_off')]:put('pq03/'+name,'indivispq03 '+command,'Administration ponctuelle sans progression ni récompense')
 for name,target in [('afficher_points','show_waypoints'),('masquer_points','clear_waypoints')]:put('pq03/admin/'+name,'function capitale:quest/sous_le_regard_du_coeur/escort/debug/'+target,'Visualisation existante ; vrais repères conservés')
 n='data/capitale/function/core/load.mcfunction';core[n]+=b'\n# PQ03-R1\nfunction capitale:pq03/load\n';reasons[n]='Déclaration PQ03'
 n='data/capitale/function/core/tick.mcfunction';core[n]=b''.join(l for l in core[n].splitlines(keepends=True) if b'quest/sous_le_regard_du_coeur/escort/tick' not in l);reasons[n]='Retrait des deux appels du guide mondial Aurèle ; tous les autres appels conservés'
 for file,command in [('before_save_self','suspend'),('after_load_self','restore')]:
  n='data/capitale/function/profiles/'+file+'.mcfunction';core[n]=(b'# PQ03-R1\nindivispq03 '+command.encode()+b'\n')+core[n];reasons[n]='Scène privée suspendue/restaurée pour le seul personnage, checkpoint périodique préservé'
 put('quest/guidage/reset/aurele_self','indivispq03 suspend','Suspension privée sans téléportation ni retour forcé à un ancien état')
 n='data/capitale/function/quest/guidage/lost/second_self.mcfunction';lines=core[n].decode().splitlines();found=[i for i,l in enumerate(lines) if 'guide_aurele_coeur' in l];assert len(found)==1;lines[found[0]]='execute if score @s CAP_GUIDE_LOCK matches 1.. if score @s CAP_GUIDE_ID matches 2 store result score @s CAP_FLAG run indivispq03 guide_valid';core[n]=('\n'.join(lines)+'\n').encode();reasons[n]='Surveillance Aurèle par personnage ; aucun nearest ni réinitialisation après éloignement'
 n='data/hc_quetes/hcq_quests/pq_03_sous_le_regard_du_coeur.json';d=json.loads(quests[n]);d['description']="Aurèle Veyrane, de la Garde du Cœur, vous propose les commerces du Cercle marchand ou un trajet direct jusqu’à l’ascenseur des Profondeurs. Les commerces sont facultatifs ; Aurèle rejoint son poste pendant votre visite."
 d['rewards'][0]['text']='Réputation de la Garde du Cœur : de −1 à +2 selon vos décisions'
 descriptions={'suivre_cercle':'Découvrir le Cercle marchand avec Aurèle (facultatif), ou choisir le trajet direct','retrouver_ascenseur':'Convenir du départ vers les Profondeurs ; après les commerces, retrouver Aurèle au premier ascenseur','suivre_profondeurs':'Suivre Aurèle jusqu’à l’ascenseur des Profondeurs','reparler_aurele':'Terminer la conversation avec Aurèle devant l’ascenseur des Profondeurs'}
 for obj in d['objectives']:obj['text']=descriptions[obj['id']];obj['waypoint']['target']='tag:hcm_pq3_aurele';obj['waypoint']['label']='Aurèle Veyrane'
 quests[n]=(json.dumps(d,ensure_ascii=False,indent=2)+'\n').encode();reasons['quests/'+n]='Deux routes explicites ; récompense exacte ; cible privée ; ID/états/objectifs conservés'
 for kind in ['core','quests']:
  m=json.loads(packs[kind]['pack.mcmeta']);m['pack']['description']+=' — PQ03-R1 : Sous le regard du Cœur, deux routes privées';packs[kind]['pack.mcmeta']=(json.dumps(m,ensure_ascii=False,indent=2)+'\n').encode()
 java=next((RT/'jdk').glob('*/bin/java'));mc=RT/'bootstrap/.fabric/remappedJars/minecraft-1.21.11-0.19.5/server-intermediary.jar';dialogue=next(ROOT.joinpath('upload').glob('haute-capitale-dialogue*.jar'))
 libs=[*sorted((RT/'libraries').glob('*.jar')),*sorted((RT/'minecraft_bundle/META-INF/libraries').rglob('*.jar')),mc,BASE['mod'],dialogue];classes=H/'classes';shutil.rmtree(classes,ignore_errors=True);classes.mkdir()
 sources=sorted((H/'src').rglob('*.java'));cmd=[java.with_name('javac'),'--release','21','-proc:none','-sourcepath','','-cp',os.pathsep.join(map(str,libs)),'-d',classes,*sources]
 result=subprocess.run(cmd,capture_output=True,text=True);(R/'compile.log').write_text(result.stdout+result.stderr);assert result.returncode==0,result.stderr
 for p in classes.rglob('*.class'):mod[p.relative_to(classes).as_posix()]=p.read_bytes()
 graph=model.export();mod['indivis_pq03_graph.json']=(json.dumps(graph,ensure_ascii=False,indent=2)+'\n').encode()
 graph2=json.loads(mod['indivis_pq02_graph.json']);changed2=0
 for node in graph2['nodes']:
  for choice in node['choices']:
   if choice['text']=='Que vais-je trouver là-haut ?':choice['text']='Que vais-je trouver au Cœur ?';changed2+=1
 assert changed2>0;graph2['editorial_pq03_followup']='7 novembre 2026 : question du joueur vers le Cœur, ascenseur descendant. Aucun identifiant ou branche modifié.';mod['indivis_pq02_graph.json']=(json.dumps(graph2,ensure_ascii=False,indent=2)+'\n').encode()
 config={'required':True,'minVersion':'0.8','package':'net.hautecapitale.quetes.pq03.mixin','compatibilityLevel':'JAVA_21','mixins':['Pq03VisibilityMixin','Pq03WaypointMixin','Pq03DialogueCloseMixin','Pq03EntityMixin','Pq03NexusSaveMixin'],'client':['Pq03ScreenLifecycleMixin'],'injectors':{'defaultRequire':1}};mod['indivis_pq03.mixins.json']=(json.dumps(config,indent=2)+'\n').encode()
 m=json.loads(mod['fabric.mod.json']);m['version']+='.PQ03.R1.HF1';m['description']+=' PQ03-R1 intégré : deux itinéraires privés, réputation immédiate et notification séparée des répliques.';m['entrypoints']['main'].append('net.hautecapitale.quetes.sequence.Pq03Runtime');m['entrypoints']['client'].append('net.hautecapitale.quetes.sequence.Pq03Client');m['mixins'].append('indivis_pq03.mixins.json');mod['fabric.mod.json']=(json.dumps(m,ensure_ascii=False,indent=2)+'\n').encode()
 diffs={}
 for kind,pack in packs.items():
  assert set(old[kind])<=set(pack)
  write(OUT/FILES[kind],pack);diffs[kind]={'changed':sorted(n for n in old[kind] if old[kind][n]!=pack[n]),'added':sorted(set(pack)-set(old[kind])),'removed':[],'preserved':sum(old[kind][n]==pack[n] for n in old[kind])}
 manifest={'status':'COMPILED','files':{k:{'filename':n,'sha256':sha((OUT/n).read_bytes()),'bytes':(OUT/n).stat().st_size} for k,n in FILES.items()},'inputs':EXPECTED,'diffs':diffs,'reasons':reasons,'pq02_direction_choices_only':changed2,'graph_nodes':len(graph['nodes']),'source_sha256':{str(p.relative_to(H)):sha(p.read_bytes()) for p in sources+[H/'model.py']}}
 (R/'build.json').write_text(json.dumps(manifest,ensure_ascii=False,indent=2)+'\n');(R/'narrative_graph.json').write_text(json.dumps(graph,ensure_ascii=False,indent=2)+'\n');print(json.dumps({k:v for k,v in manifest.items() if k not in ['reasons','source_sha256']},ensure_ascii=False,indent=2))
if __name__=='__main__':main()
