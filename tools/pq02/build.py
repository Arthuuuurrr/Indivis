"""PQ02 R1 — copie exacte des deux datapacks fournis + extension compilée Java 21."""
from pathlib import Path
import hashlib,json,zipfile,subprocess,os,sys,shutil
import model
HERE=Path(__file__).resolve().parent;ROOT=HERE.parents[1];OUT=HERE/'output';OUT.mkdir(exist_ok=True)
BASE={'core':ROOT/'upload/capitale_core_1.5.7_V2_SEQUENCE_I_SI1_PQ01_R3_HF9_QMARK2(1).zip','quests':ROOT/'upload/hc_quetes_b22_V2_SEQUENCE_I_SI1b_PQ01_R3_HF4_QMARK2(1).zip'}
EXPECTED={'core':'8aff8d9c45d275b9d281f5b915baad5e88e7a6c2147bc9806b4fff3cc7a7a8c4','quests':'72aa0cf454853f65e2cad769ba200e524c711c75260fe6a1d54c148c90788ba7'}
FILES={'core':'capitale_core_1.5.7_V2_SEQUENCE_I_SI1_PQ01_R3_HF9_QMARK2_PQ02_R1_HF1.zip','quests':'hc_quetes_b22_V2_SEQUENCE_I_SI1b_PQ01_R3_HF4_QMARK2_PQ02_R1.zip','mod':'indivis-pq02-1.0.6+1.21.11.jar'}
JOURNAL_FILENAME='haute-capitale-quetes-HF12-PQ02-R1-HF6-1.21.11.jar'
PERSIST=['CAP_PQ2_VER','CAP_PQ2_NODE','CAP_PQ2_DA','CAP_PQ2_DG','CAP_PQ2_DM','CAP_PQ2_DD','CAP_PQ2_IMPR','CAP_PQ2_SENT','CAP_PQ2_ANECD','CAP_PQ2_PORT','CAP_PQ2_STATUS','CAP_PQ2_OFFER','CAP_PQ2_REG','CAP_PQ2_REW','CAP_PQ2_WP','CAP_PQ2_PAUSE']
def sha(b):return hashlib.sha256(b).hexdigest()
def read(p):
 with zipfile.ZipFile(p) as z:assert z.testzip() is None;return {n:z.read(n) for n in z.namelist() if not n.endswith('/')}
def write(p,content):
 with zipfile.ZipFile(p,'w',zipfile.ZIP_DEFLATED,compresslevel=9) as z:
  for n,b in sorted(content.items()):
   info=zipfile.ZipInfo(n,(2026,10,26,0,0,0));info.compress_type=zipfile.ZIP_DEFLATED;z.writestr(info,b)
 with zipfile.ZipFile(p) as z:assert z.testzip() is None

def main():
 for k,p in BASE.items():assert sha(p.read_bytes())==EXPECTED[k],k
 old={k:read(p) for k,p in BASE.items()};packs={k:v.copy() for k,v in old.items()};core=packs['core'];quests=packs['quests'];reasons={}
 def path(s):return 'data/capitale/function/'+s+'.mcfunction'
 def put(s,lines,why):
  n=path(s);core[n]=('# PQ02-R1 — '+why+'\n'+'\n'.join(lines)+'\n').encode();reasons[n]=why
 def prefix(s,lines,why):
  n=path(s);core[n]=('# PQ02-R1 — '+why+'\n'+'\n'.join(lines)+'\n').encode()+core[n];reasons[n]=why
 # Seuls les douze fichiers Hugues identifiés sont modifiés dans PQ01.
 for key in ['hugues_menu','hugues_36','hugues_38','hugues_40','hugues_42','hugues_44']:
  n=path('pq01/menu/'+key);before=core[n].decode().splitlines(keepends=True);removed=[l for l in before if 'Écouter leur échange sur les procédures.' in l];assert len(removed)==1 and '$(b6)' in removed[0]
  core[n]=''.join(l for l in before if l not in removed).encode();reasons[n]='PQ01/Hugues : seule ligne du choix 6 retirée ; numéros conservés'
  n=path('pq01/choice/'+key);text=core[n].decode();target='execute if score @s hcm_pq1_pick matches 6 run return run function capitale:pq01/banter_self';assert text.count(target)==1
  core[n]=text.replace(target,'execute if score @s hcm_pq1_pick matches 6 run return 0').encode();reasons[n]='PQ01/Hugues : ancien choix 6 neutralisé, moteur de banter conservé'
 # Raccord MQ03 manquant constaté sur le capitaine lié au quai dans HF12.
 priority='execute if score @s hcq_mq03 matches 10..30 run return run function hc_quetes:runtime/interact/altheon_brumeforge'
 assert priority in core[path('pq01/open/altheon_self')].decode()
 prefix('quest/spawn_dirigeable_couronne/capitaine_quai/interact_self',[priority],'Raccord MQ03 : même priorité que le routeur au large, sans changement de stade ou de texte MQ')
 # Entrées historiques préservées ; leur implémentation devient la machine narrative PQ02.
 leovic=['interact_self','offer_self','reoffer_self','accept_self','accept_body_154_self','accept_commit_self','open_choices_self','show_choices_self','complete_self','lead_to_ascenseur_self','lead_to_ascenseur_commit_self','explain_registration_self','gift_after_quest_self','postquest_self','postquest_returning_self','decline_self','arrive_ascenseur_self']
 for name in leovic:
  n='quest/le_registre_du_port/leovic/'+name
  if path(n) in core:put(n,['indivispq02 leovic'],'PQ02 : entrée historique vers le dialogue validé, aucun don ni achèvement implicite')
 for name in ['leovic_accept_l2_self','leovic_return_after_completion_self','leovic_offer_l2_self','leovic_completion_self','leovic_reafficher_choix_self','leovic_to_ascenseur_self']:
  n='quest/le_registre_du_port/sequence/'+name
  if path(n) in core:put(n,['indivispq02 leovic'],'PQ02 : ancienne séquence automatique retirée ; progression explicite, aucun +5')
 put('quest/le_registre_du_port/leovic/near_offer_self',['indivispq02 near'],'PQ02 : interpellation ciblée, bornée à trois blocs et reprise au clic')
 put('npc/leovic/near_self',['execute if entity @s[type=minecraft:player] run function capitale:quest/le_registre_du_port/leovic/near_offer_self','execute unless entity @s[type=minecraft:player] at @s as @a[distance=..3] at @s run function capitale:quest/le_registre_du_port/leovic/near_offer_self'],'PQ02 : contexte de chaque joueur proche, aucune sélection globale du plus proche')
 for name in ['register_self','register_commit_self','register_paid_self','register_paid_success_self']:
  prefix('quest/le_registre_du_port/magistrat/'+name,['execute if score @s hcq_pq02 matches 40..90 run return run function capitale:pq02/open_magistrat'],'PQ02 : entretien assisté prioritaire, autres inscriptions et citoyenneté inchangées')
 put('quest/le_registre_du_port/escort/tick',['return 0'],'PQ02 : ancien guide mondial désactivé ; parcours A–P repris par instance personnelle')
 put('pq02/open_magistrat',['indivispq02 magistrat'],'Entretien à trois au poste existant du magistrat')
 put('pq02/open_leovic',['indivispq02 leovic'],'Ouverture/reprise du dialogue de Léovic')
 put('pq02/load',[*['scoreboard objectives add '+s+' dummy' for s in PERSIST],'scoreboard objectives add hcm_pq2_ui dummy','scoreboard objectives add hcm_pq2_check dummy','scoreboard objectives add Pq02Choix trigger','data modify storage capitale:pq02_setup version set value 1'],'Objectifs persistants et trigger isolé, aucun score de joueur réinitialisé')
 core[path('core/load')]+=b'\n# PQ02-R1\nfunction capitale:pq02/load\n';reasons[path('core/load')]='Déclaration des objectifs PQ02'
 prefix('profiles/before_save_self',['indivispq02 suspend'],'PQ02 : suspension et fermeture avant la sauvegarde Nexus du personnage')
 core[path('profiles/after_load_self')]+=b'\n# PQ02-R1 : nettoyage de session uniquement, aucune progression.\nindivispq02 restore\n';reasons[path('profiles/after_load_self')]='PQ02 : cache de scène invalidé après chargement du profil'
 put('pq02/register_once_self',['execute unless score @s hcq_pq02 matches 50 run return 0','execute unless score @s CAP_QDIALOG_OWNER matches 97 run return 0','execute if score @s CAP_PQ2_REG matches 1.. run return 0','scoreboard players set @s CAP_PQ2_REG 1','scoreboard players add @s CAP_RANGSOCIAL 0','execute if score @s CAP_RANGSOCIAL matches ..9 run scoreboard players set @s CAP_RANGSOCIAL 10','function capitale:bounds/rangsocial_self','function capitale:access/recalculate_by_rank_self','function capitale:display/prefix/sync_self','function hc_quetes:migration/write/pq02/v40','return 1'],'Inscription idempotente, Visiteur minimum, aucun rang abaissé')
 put('pq02/gift_once_self',['execute unless score @s CAP_QDIALOG_OWNER matches 97 run return 0','execute unless score @s hcq_pq02 matches 90..100 run return 0','execute unless score @s CAP_PQ2_OFFER matches 1 run return 0','execute if score @s CAP_LEOVIC_GIFT matches 1.. run return 0','scoreboard players set @s CAP_LEOVIC_GIFT 1','give @s capitale_currency:martin_dor 50','function capitale:rewards/daily_bonus/roll/port_self','execute at @s run playsound minecraft:entity.experience_orb.pickup master @s ~ ~ ~ 1.15 1.15','return 1'],'Don personnel unique, ancien bonus de port conservé, aucun effet de réputation')
 put('pq02/complete_once_self',['execute unless score @s hcq_pq02 matches 90 run return 0','execute unless score @s CAP_QDIALOG_OWNER matches 97 run return 0','execute unless score @s CAP_PQ2_REG matches 1 run return 0','execute unless score @s CAP_PQ2_DD matches 1..4 run return 0','execute if score @s CAP_PQ2_REW matches 1.. run return 0','execute store result score @s hcm_pq2_check run indivispq02 rewards_ready','execute unless score @s hcm_pq2_check matches 1 run return 0','scoreboard players set @s CAP_PQ2_REW 1','function capitale:reward/skills/core/registre_port_self','function capitale:rewards/daily_bonus/roll/port_self','function hc_quetes:migration/write/pq02/v100','scoreboard players set @s QUEST_PROLOGUE 30','scoreboard players set @s CAP_QUETEACTIVE 0','scoreboard players set @s CAP_GUIDE_LOCK 0','scoreboard players set @s CAP_GUIDE_ID 0','scoreboard players set @s CAP_GUIDE_MISS_T 0','title @s times 10 70 20','title @s title {"text":"Quête terminée","color":"gold","bold":true}','title @s subtitle {"text":"Le registre du Port","color":"white"}','function capitale:dialogue/sound/gain_quete_self','return 1'],'Achèvement explicite et unique ; compétences/XP/bonus conservés ; +5 supprimé')
 for role in ['leovic','magistrat','sentinel']:
  put('pq02/liaison/'+('sentinelle' if role=='sentinel' else role),['indivispq02 bind_'+role],'Association d’un PNJ existant, sans modification d’apparence ou d’action')
 for path2,cmd in [('diagnostic','status'),('diagnostic_points','points'),('preparer_parcours','prepare_route'),('nettoyer_scenes','clear_scenes')]:put('pq02/admin/'+path2,['indivispq02 '+cmd],'Administration ciblée de PQ02')
 put('pq02/admin/preparer_guide_historique',['return run function capitale:pq02/admin/preparer_parcours'],'Compatibilité : ancien nom conservé vers la préparation automatique du parcours')
 for name,target in [('afficher_points','show_waypoints'),('masquer_points','clear_waypoints')]:
  put('pq02/admin/'+name,['function capitale:quest/le_registre_du_port/escort/debug/'+target],'Visualisation historique des repères existants, sans déplacement des markers')
 # La seconde portion de l'escorte utilise les mêmes règles de guidage que la première.
 extra=['execute if predicate hc_quetes:migration/state/pq02/50 run scoreboard players set @s CAP_GUIDE_LOCK 1','execute if predicate hc_quetes:migration/state/pq02/50 run scoreboard players set @s CAP_GUIDE_ID 1']
 core[path('quest/guidage/sync_self')]+=('\n# PQ02-R1 : courte escorte après inscription.\n'+'\n'.join(extra)+'\n').encode();reasons[path('quest/guidage/sync_self')]='Verrou d’escorte également sur le trajet M–P'
 n=path('quest/markers/guide_free');text=core[n].decode();assert text.endswith('return 1\n');core[n]=text.replace('return 1\n','execute if predicate hc_quetes:migration/state/pq02/50 run return 0\nreturn 1\n').encode();reasons[n]='Marqueurs : même verrou PQ02 M–P que l’acceptation des quêtes'
 n='docs/quest_markers/QMARK2_CATALOGUE.json';catalog=json.loads(core[n]);catalog['guide_predicates'].append('hc_quetes:migration/state/pq02/50');core[n]=(json.dumps(catalog,ensure_ascii=False,indent=2)+'\n').encode();reasons[n]='Catalogue de guidage synchronisé'
 # Même ID, mêmes plages et mêmes objectifs du journal ; textes et cibles privés corrigés.
 n='data/hc_quetes/hcq_quests/pq_02_registre_port.json';definition=json.loads(quests[n]);assert definition['id']=='pq_02_registre_port'
 definition['description']="Léovic, de la Garde du Port, vous attend près des tentes pour régulariser votre arrivée auprès du magistrat, sous les Archives. Il vous indiquera ensuite l'ascenseur voisin qui mène au Cœur."
 definition['rewards']=[{'text':t} for t in ["Réputation de la Garde du Port : de −1 à +3 selon vos décisions","165 points d’expérience","Inscription au registre du Port (Visiteur, sans rétrogradation)","Un bonus du port (tirage aléatoire)","Don personnel facultatif de Léovic : 50 Martins d’or, une fois par personnage, avec le second bonus du port existant"]]
 for objective in definition['objectives']:
  if objective['id']=='parler_leovic':objective['text']="Parler à Léovic près des tentes de la Garde du Port et accepter son escorte"
  if objective['id']=='magistrat':objective['text']="S'entretenir avec le magistrat sous les Archives, en présence de Léovic"
  if objective['id']=='retour_leovic':objective['text']="Conclure l'entretien avec Léovic et gagner l'ascenseur voisin"
  if objective['id']=='suivre_ascenseur':objective['text']="Suivre Léovic jusqu'à l'ascenseur voisin, vers le Cœur"
  if objective['id']!='parler_leovic' and objective.get('waypoint',{}).get('target')=='tag:npc_leovic_registre_port':objective['waypoint']['target']='tag:hcm_pq2_leovic'
 quests[n]=(json.dumps(definition,ensure_ascii=False,indent=2)+'\n').encode();reasons['quests/'+n]='PQ02 : récompenses exactes et waypoint de l’acteur du personnage ; ID/états conservés'
 for kind in packs:
  meta=json.loads(packs[kind]['pack.mcmeta']);meta['pack']['description']+=' — PQ02-R1 : Le registre du Port v1.0'+(' — HF1 : commandes de préparation adaptées aux repères' if kind=='core' else '');packs[kind]['pack.mcmeta']=(json.dumps(meta,ensure_ascii=False,indent=2)+'\n').encode()
 # Compilation réelle, sans réécriture des classes historiques.
 rt=Path('/tmp/indivis_pq02_runtime');java=next((rt/'jdk').glob('*/bin/java'));javac=java.with_name('javac');classes=HERE/'classes';shutil.rmtree(classes,ignore_errors=True);classes.mkdir()
 journal=ROOT/'upload/haute-capitale-quetes-0.1.0+1.21.11.b7-SEQUENCE-I-SI1b-PQ01-R3-HF12(1).jar';dialogue=ROOT/'upload/haute-capitale-dialogue-0.1.0+1.21.11.b10-DATAPACK-COMMAND-HOTFIX(1).jar'
 mc=rt/'bootstrap/.fabric/remappedJars/minecraft-1.21.11-0.19.5/server-intermediary.jar';assert mc.exists()
 libs=[*sorted((rt/'libraries').glob('*.jar')),*sorted((rt/'minecraft_bundle/META-INF/libraries').rglob('*.jar')),mc,journal,dialogue]
 cp=os.pathsep.join(map(str,libs));sources=sorted(p for p in (HERE/'src').rglob('*.java') if not any(x.startswith('.') for x in p.relative_to(HERE/'src').parts))
 cmd=[str(javac),'--release','21','-proc:none','-cp',cp,'-d',str(classes),*map(str,sources)]
 r=subprocess.run(cmd,capture_output=True,text=True);(HERE/'reports/compile.log').write_text(r.stdout+r.stderr)
 if r.returncode:raise RuntimeError(r.stderr)
 mod={p.relative_to(classes).as_posix():p.read_bytes() for p in classes.rglob('*.class') if not any(x.startswith('.') for x in p.relative_to(classes).parts)};mod['indivis_pq02_graph.json']=(json.dumps(model.export(),ensure_ascii=False,indent=2)+'\n').encode()
 metadata={'schemaVersion':1,'id':'indivis_pq02','version':'1.0.6+1.21.11','name':'Indivis — PQ02 : Le registre du Port','description':'Refonte PQ02 v1.0, escortes privées, décisions et don par personnage Nexus. Requiert le core PQ02-R1-HF1.','environment':'*','entrypoints':{'main':['net.hautecapitale.quetes.sequence.Pq02Runtime'],'client':['net.hautecapitale.quetes.sequence.Pq02Client']},'mixins':['indivis_pq02.mixins.json'],'depends':{'minecraft':'1.21.11','java':'>=21','fabricloader':'>=0.19.5','fabric-api':'>=0.141.6','haute_capitale_quetes':'*','haute_capitale_dialogue':'*','easy_npc':'7.16.0','nexuscharacters':'*'}}
 mod['fabric.mod.json']=json.dumps(metadata,ensure_ascii=False,indent=2).encode();mod['indivis_pq02.mixins.json']=json.dumps({'required':True,'minVersion':'0.8','package':'net.hautecapitale.quetes.pq02.mixin','compatibilityLevel':'JAVA_21','mixins':['Pq02VisibilityMixin','Pq02WaypointMixin','Pq02DialogueCloseMixin','Pq02EntityMixin','Pq02NexusCheckpointMixin'],'client':['Pq02ScreenLifecycleMixin'],'injectors':{'defaultRequire':1}}).encode()
 for kind,p in packs.items():write(OUT/FILES[kind],p)
 write(OUT/FILES['mod'],mod)
 diffs={}
 for kind,pack in packs.items():
  changed=sorted(n for n in old[kind] if old[kind][n]!=pack[n]);added=sorted(set(pack)-set(old[kind]));removed=sorted(set(old[kind])-set(pack));assert not removed
  diffs[kind]={'changed':changed,'added':added,'removed':removed,'unchanged':sum(old[kind][n]==pack[n] for n in old[kind]),'reasons':{n:reasons.get(n,reasons.get('quests/'+n,'Métadonnées de version')) for n in changed+added}}
 (HERE/'reports/diffs.json').write_text(json.dumps(diffs,ensure_ascii=False,indent=2)+'\n')
 manifest={'status':'BUILT_NOT_YET_VALIDATED','files':{k:{'path':str(OUT/name),'filename':name,'bytes':(OUT/name).stat().st_size,'sha256':sha((OUT/name).read_bytes())} for k,name in FILES.items()},'inputs':EXPECTED,'java':subprocess.check_output([java,'-version'],stderr=subprocess.STDOUT,text=True).strip(),'compiled_sources':[str(p.relative_to(HERE)) for p in sources]}
 (HERE/'reports/build.json').write_text(json.dumps(manifest,ensure_ascii=False,indent=2)+'\n');print(json.dumps(manifest,ensure_ascii=False,indent=2))
if __name__=='__main__':main()
