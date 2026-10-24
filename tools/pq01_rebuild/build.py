"""Reconstruction PQ01-R2 : deux datapacks dérivés, aucun JAR reconstruit."""
from pathlib import Path
import hashlib,json,re,zipfile
from model import ROOT,N,IDS,Q,ROLES,OWNERS

HERE=Path(__file__).resolve().parent
OUT=HERE/'output';OUT.mkdir(exist_ok=True)
INPUTS={
 'core':('capitale_core_1.5.7_V2_SEQUENCE_I_SI1(1).zip','dfbc034fa9ea481105c76c461e88911790b733e939401960029653caa4fc482a'),
 'quests':('hc_quetes_b22_V2_SEQUENCE_I_SI1b.zip','ad3bed0ff8e08921f1c99e757b7002c43a788676fa0ed0b18af10d7e8527fe0c')}
FILES={'core':'capitale_core_1.5.7_V2_SEQUENCE_I_SI1_PQ01_R2.zip','quests':'hc_quetes_b22_V2_SEQUENCE_I_SI1b_PQ01_R2.zip'}
PREFIX='quest/spawn_dirigeable_couronne/'
SCORES=['hch_pq1node','hcf_pq1_a','hcf_pq1_c','hcf_pq1_av','hcf_pq1_gain','hcf_pq1_test','hcf_pq1_ver','hcd_pq1_banter',
 'hcm_pq1_active','hcm_pq1_node','hcm_pq1_tok','hcm_pq1_pick','hcm_pq1_valid','hcm_pq1_temp','hcm_pq1_voice','hcm_pq1_ttl','hcm_pq1_commit','hcm_pq1_dock','hcm_pq1_role','hcm_pq1_poll']
SAVED=['hcq_pq01','QUEST_PROLOGUE','CAP_QUETEACTIVE',*SCORES[:8]]
def digest(data):return hashlib.sha256(data).hexdigest()
def load(path):
 with zipfile.ZipFile(path) as z:
  assert z.testzip() is None
  return {p:z.read(p) for p in z.namelist() if not p.endswith('/')}
def save(pack,path):
 tmp=path.with_suffix(path.suffix+'.partial')
 with zipfile.ZipFile(tmp,'w',zipfile.ZIP_DEFLATED,compresslevel=9) as z:
  for p,b in sorted(pack.items()):
   info=zipfile.ZipInfo(p,(2026,10,22,0,0,0));info.compress_type=zipfile.ZIP_DEFLATED;z.writestr(info,b)
 with zipfile.ZipFile(tmp) as z: assert z.testzip() is None
 tmp.replace(path)
def j(v):return json.dumps(v,ensure_ascii=False,separators=(',',':'))
def function_path(name):return 'data/capitale/function/'+name+'.mcfunction'
def tell(text,color='gray'):return 'tellraw @s '+j({'text':text,'color':color})
def cmd(path):return 'function capitale:pq01/'+path
def build():
 packs={}
 for k,(name,sha) in INPUTS.items():
  p=ROOT/'upload'/name
  assert digest(p.read_bytes())==sha,('Entrée différente',name)
  packs[k]=load(p)
 before={k:v.copy() for k,v in packs.items()};core=packs['core'];quests=packs['quests']
 def put(name,lines):
  core[function_path(name)]=(('# PQ01-R2 — candidat texte, repli statique documenté.\n')+'\n'.join(lines)+'\n').encode()
 def f(name,lines):put('pq01/'+name,lines)
 def get(name):return core[function_path(name)].decode()
 # Réserver seulement des noms réellement libres dans les entrées reçues.
 original='\n'.join(b.decode(errors='replace') for p,b in core.items() if p.endswith('.mcfunction'))
 for s in SCORES: assert len(s)<=16 and not re.search(r'(?<![\w])'+s+r'(?![\w])',original),s
 assert not re.search(r'CAP_QDIALOG_OWNER (?:matches|set) 9[1-6]\b',original)
 f('load',[
  *['scoreboard objectives add '+s+' dummy' for s in SCORES],
  'execute unless data storage capitale:pq01_runtime nonce run data modify storage capitale:pq01_runtime nonce set value 1000000000',
  *['execute unless data storage capitale:pq01_config voices.'+k+' run data modify storage capitale:pq01_config voices.'+k+' set value {sound:"",ticks:0}' for k in N],
  'execute unless data storage capitale:pq01_config timeout_ticks run data modify storage capitale:pq01_config timeout_ticks set value 3600',
  'execute unless data storage capitale:pq01_config allow_banter run data modify storage capitale:pq01_config allow_banter set value 0',
  'execute as @a[scores={CAP_QDIALOG_OWNER=91..96}] at @s run '+cmd('close_self')])
 core[function_path('core/load')]+=b'\nfunction capitale:pq01/load\n'
 core[function_path('core/tick')]=( 'execute as @a[scores={hcm_pq1_active=1}] at @s run '+cmd('session/tick_self')+'\n').encode()+core[function_path('core/tick')]
 f('ensure_self',[
  'function capitale:player/ensure_runtime_self','function hc_quetes:migration/ensure/pq01',
  *['scoreboard players add @s '+s+' 0' for s in SCORES[:8]],
  'execute unless score @s hcf_pq1_ver matches 1 run '+cmd('upgrade_self')])
 f('upgrade_self',[
  'execute if score @s hcq_pq01 matches 10.. run scoreboard players set @s hcf_pq1_a 1',
  'execute if score @s hcq_pq01 matches 40.. run scoreboard players set @s hcf_pq1_c 1',
  'execute if score @s hcq_pq01 matches 100.. run scoreboard players set @s hcf_pq1_gain 1',
  'scoreboard players set @s hcf_pq1_ver 1'])
 f('context_self',[
  'execute store result score @s hcm_pq1_valid run indivissequence context',
  'execute unless score @s hcm_pq1_valid matches 1 run return 0','return 1'])
 f('session/save_macro',[
  '$data modify storage capitale:pq01_runtime sessions."$(owner)" set value {character:"$(character)",dimension:"$(dimension)"}',
  *['$data modify storage capitale:pq01_runtime sessions."$(owner)".'+axis+' set from entity @s Pos['+str(i)+']' for i,axis in enumerate('xyz')]])
 f('session/context_macro',[
  '$execute unless data storage capitale:pq01_runtime sessions."$(owner)"{character:"$(character)"} run return 0',
  '$data modify storage capitale:pq01_runtime current set from storage capitale:pq01_runtime sessions."$(owner)"',
  'return run '+cmd('session/spatial_macro')+' with storage capitale:pq01_runtime current'])
 f('session/identity_macro',[
  '$execute unless data storage capitale:pq01_runtime sessions."$(owner)"{character:"$(character)"} run return 0','return 1'])
 f('transition/check_self',[
  'execute store result score @s hcm_pq1_valid run '+cmd('context_self'),
  'execute unless score @s hcm_pq1_valid matches 1 run return 0',
  'return run '+cmd('session/identity_macro')+' with storage hc_quetes:session current'])
 f('transition/abort_self',[
  'scoreboard players set @s CAP_QSEQ 0','scoreboard players set @s CAP_QSEQ_TIMER 0',
  tell('[PQ01] Transition arrêtée : contexte de personnage changé. Reprenez auprès du capitaine, ou de Géraud si vous êtes déjà au quai.','gold')])
 f('session/spatial_macro',[
  '$execute unless dimension $(dimension) run return 0',
  '$execute positioned $(x) $(y) $(z) unless entity @s[distance=..10] run return 0','return 1'])
 f('session/check_self',[
  'execute unless score @s hcm_pq1_active matches 1 run return 0',
  'execute unless score @s CAP_QDIALOG_OWNER matches 91..96 run return 0',
  'execute unless score @s CAP_QDIALOG_OWNER = @s hcm_pq1_role run return 0',
  'execute unless score @s CAP_QDIALOG_KEEP matches 1.. run return 0',
  'execute unless score @s hcm_pq1_ttl matches 1.. run return 0',
  'execute store result score @s hcm_pq1_valid run '+cmd('context_self'),
  'execute unless score @s hcm_pq1_valid matches 1 run return 0',
  'return run '+cmd('session/context_macro')+' with storage hc_quetes:session current'])
 f('session/tick_self',[
  'scoreboard players remove @s hcm_pq1_ttl 1',
  'execute if score @s hcm_pq1_voice matches 1.. run scoreboard players remove @s hcm_pq1_voice 1',
  'scoreboard players add @s hcm_pq1_poll 1',
  'execute if score @s hcm_pq1_poll matches ..4 run return 0',
  'scoreboard players set @s hcm_pq1_poll 0',
  'execute store result score @s hcm_pq1_valid run '+cmd('session/check_self'),
  'execute unless score @s hcm_pq1_valid matches 1 run '+cmd('close_self')])
 f('voice/play_macro',['$playsound $(sound) voice @s ~ ~ ~ 1 1','$scoreboard players set @s hcm_pq1_voice $(ticks)',
  'execute if score @s hcm_pq1_voice matches ..-1 run scoreboard players set @s hcm_pq1_voice 0',
  'execute if score @s hcm_pq1_voice matches 12001.. run scoreboard players set @s hcm_pq1_voice 12000'])
 f('voice/stop_macro',['$stopsound @s voice $(sound)'])
 f('voice/stop_self',[
  'execute if score @s hcm_pq1_node matches '+str(IDS[k])+' unless data storage capitale:pq01_config voices.'+k+'{sound:""} if data storage capitale:pq01_config voices.'+k+'.sound run '+cmd('voice/stop_macro')+' with storage capitale:pq01_config voices.'+k for k in N])
 f('close_self',[
  cmd('voice/stop_self'),
  'execute if score @s CAP_QDIALOG_OWNER matches 91..96 run function capitale:quest/dialogue/clear_self',
  *['scoreboard players set @s '+s+' 0' for s in ('hcm_pq1_active','hcm_pq1_tok','hcm_pq1_pick','hcm_pq1_node','hcm_pq1_voice','hcm_pq1_ttl','hcm_pq1_role','hcm_pq1_poll')]])
 # Jeton neuf à chaque affichage. Aucun ancien bouton ne partage le même bloc de seize valeurs.
 f('buttons_self',[
  'execute store result score #nonce hcm_pq1_temp run data get storage capitale:pq01_runtime nonce',
  'scoreboard players add #nonce hcm_pq1_temp 16',
  'execute unless score #nonce hcm_pq1_temp matches 1000000000..1999999900 run scoreboard players set #nonce hcm_pq1_temp 1000000000',
  'execute store result storage capitale:pq01_runtime nonce int 1 run scoreboard players get #nonce hcm_pq1_temp',
  'scoreboard players operation @s hcm_pq1_tok = #nonce hcm_pq1_temp',
  *[line for i in list(range(1,8))+[15] for line in [
   'scoreboard players operation @s hcm_pq1_temp = @s hcm_pq1_tok',
   'scoreboard players add @s hcm_pq1_temp '+str(i),
   'execute store result storage capitale:pq01_runtime buttons.b'+str(i)+' int 1 run scoreboard players get @s hcm_pq1_temp']],
  'scoreboard players set @s QuestChoix 0','scoreboard players enable @s QuestChoix'])
 for key,n in N.items():
  lines=[cmd('voice/stop_self'),'scoreboard players set @s hcm_pq1_node '+str(IDS[key]),
   'scoreboard players set @s hch_pq1node '+str(IDS[n['resume']]),'scoreboard players set @s hcm_pq1_voice 0']
  effects={
   'intro_a_done':['execute unless score @s hcf_pq1_a matches 1 run '+cmd('intro_a_done_self')],
   'intro_c_done':['scoreboard players set @s hcf_pq1_c 1'],
   'av':['scoreboard players set @s hcf_pq1_av 1']}
  lines+=effects.get(n['effect'],[])
  lines+=['tellraw @s '+j([{'text':'['+ROLES[n['role']]+'] ','color':'yellow'},{'text':s,'color':'white'}]) for s in n['lines']]
  lines+=['execute unless data storage capitale:pq01_config voices.'+key+'{sound:""} if data storage capitale:pq01_config voices.'+key+'.sound run '+cmd('voice/play_macro')+' with storage capitale:pq01_config voices.'+key,
   cmd('buttons_self'),cmd('menu/'+key)+' with storage capitale:pq01_runtime buttons']
  f('node/'+key,lines)
  buttons=[]
  for i,c in enumerate(n['choices'],1):
   l='$tellraw @s '+j({'text':'['+c['label']+']','color':'yellow','click_event':{'action':'run_command','command':'/trigger QuestChoix set $(b'+str(i)+')'}})
   if c.get('condition')=='av': l='$execute if score @s hcf_pq1_av matches 1 run '+l[len('$'):]
   buttons.append(l)
  buttons.append('$tellraw @s '+j({'text':'[Fermer la conversation]','color':'gray','click_event':{'action':'run_command','command':'/trigger QuestChoix set $(b15)'}}))
  f('menu/'+key,buttons)
  choices=[]
  for i,c in enumerate(n['choices'],1):
   guard='execute if score @s hcm_pq1_pick matches '+str(i)
   if c.get('condition')=='av':guard+=' if score @s hcf_pq1_av matches 1'
   target=c['target'];t={'close':'close_self','dock':'dock_self','complete':'complete_self','banter_start':'banter_self'}.get(target,'node/'+target)
   choices.append(guard+' run return run '+cmd(t))
  choices.append(tell('Ce choix ne correspond plus à cette conversation.'))
  f('choice/'+key,choices)
 f('dispatch_self',[
  'execute store result score @s hcm_pq1_valid run '+cmd('session/check_self'),
  'execute unless score @s hcm_pq1_valid matches 1 run return run '+cmd('close_self'),
  'scoreboard players operation @s hcm_pq1_pick = @s QuestChoix',
  'scoreboard players operation @s hcm_pq1_pick -= @s hcm_pq1_tok',
  'scoreboard players set @s QuestChoix 0','scoreboard players enable @s QuestChoix',
  'execute if score @s hcm_pq1_pick matches 15 run return run '+cmd('close_self'),
  'execute if score @s hcm_pq1_voice matches 1.. run return run '+cmd('voice/wait_self'),
  *['execute if score @s hcm_pq1_node matches '+str(IDS[k])+' run return run '+cmd('choice/'+k) for k in N],
  'return run '+cmd('close_self')])
 f('voice/wait_self',[tell('La réplique est encore en cours. Attendez sa fin, puis utilisez le même bouton.')])
 core[function_path('quest/dialogue/dispatch_self')]=('execute if score @s CAP_QDIALOG_OWNER matches 91..96 run return run '+cmd('dispatch_self')+'\n').encode()+core[function_path('quest/dialogue/dispatch_self')]
 f('intro_a_done_self',[
  'scoreboard players set @s hcf_pq1_a 1','function hc_quetes:migration/write/pq01/v10',
  'scoreboard players set @s QUEST_PROLOGUE 10','scoreboard players set @s CAP_QUETEACTIVE 1',
  'function capitale:quest/objective/parler_capitaine_cabine_self'])
 # Ouverture : valide le personnage Nexus et n'interrompt aucune autre conversation.
 common=['execute store result score @s hcm_pq1_valid run '+cmd('context_self'),
  'execute unless score @s hcm_pq1_valid matches 1 run return 0',
  cmd('ensure_self'),
  'execute if score @s hcm_pq1_active matches 1 run return 0',
  'execute unless score @s CAP_QSEQ matches 0 run return 0',
  'execute if score @s CAP_QDIALOG_OWNER matches 2..90 run return 0',
  cmd('session/save_macro')+' with storage hc_quetes:session current',
  'scoreboard players set @s hcm_pq1_active 1','scoreboard players set @s CAP_QDIALOG_KEEP 1',
  'scoreboard players set @s hcm_pq1_poll 0',
  'execute store result score @s hcm_pq1_ttl run data get storage capitale:pq01_config timeout_ticks',
  'execute unless score @s hcm_pq1_ttl matches 200..12000 run scoreboard players set @s hcm_pq1_ttl 3600']
 for role,owner in OWNERS.items():
  pre=[]
  if role=='altheon':pre.append('execute if score @s hcq_mq03 matches 10..30 run return run function hc_quetes:runtime/interact/altheon_brumeforge')
  lines=[*pre,*common,'scoreboard players set @s CAP_QDIALOG_OWNER '+str(owner),'scoreboard players set @s hcm_pq1_role '+str(owner)]
  if role=='geraud':
   lines+=['execute if score @s hcq_pq01 matches 100.. run return run '+cmd('node/f_geraud'),
    'execute if score @s hcq_pq01 matches 40..99 run return run '+cmd('open_geraud_quai_self'),
    'execute if score @s hcf_pq1_a matches 1 run return run '+cmd('node/ga_menu'),
    'function hc_quetes:migration/write/pq01/v10','scoreboard players set @s QUEST_PROLOGUE 10','scoreboard players set @s CAP_QUETEACTIVE 1',
    'hcd_interpellation nearest_named Géraud Rivet',
    *['execute if score @s hch_pq1node matches '+str(IDS[k])+' run return run '+cmd('node/'+k) for k in N if re.fullmatch('a[1-8]',k)],cmd('node/a1')]
  elif role=='altheon':
   lines+=['execute if score @s hcq_pq01 matches 40.. run return run '+cmd('node/f_altheon'),
    'execute unless score @s hcf_pq1_a matches 1 run return run '+cmd('before_geraud_self'),
    'function hc_quetes:migration/write/pq01/v20','scoreboard players set @s CAP_QUETEACTIVE 1',
    'execute if score @s hcf_pq1_c matches 1 run return run '+cmd('node/c_menu'),
    'hcd_interpellation nearest_named Althéon Brumeforge',
    *['execute if score @s hch_pq1node matches '+str(IDS[k])+' run return run '+cmd('node/'+k) for k in N if re.fullmatch('c[0-6]',k)],cmd('node/c0')]
  else:
   lines+=['execute if score @s hcq_pq01 matches 40.. run return run '+cmd('node/f_'+role),
    'execute unless score @s hcf_pq1_a matches 1 run return run '+cmd('before_geraud_self'),cmd('node/'+role+'_menu')]
  f('open/'+role+'_self',lines)
 f('before_geraud_self',[tell('Terminez d’abord votre premier échange avec Géraud Rivet.'),cmd('close_self')])
 f('open_geraud_quai_self',[
  *['execute if score @s hch_pq1node matches '+str(IDS[k])+' run return run '+cmd('node/'+k) for k in N if k.startswith(('e1','e2','e_','ex'))],cmd('node/e1')])
 # Repli statique : aucun PNJ global déplacé ou dupliqué. L'ambiance demeure personnelle.
 f('banter_self',[
  'execute unless data storage capitale:pq01_config {allow_banter:1} run return run '+cmd('banter_unavailable_self'),
  'tag @s add hcm_pq1_viewer',
  'scoreboard players set @s hcm_pq1_temp 0',
  'execute if entity @a[tag=!hcm_pq1_viewer,distance=..16,limit=1] run scoreboard players set @s hcm_pq1_temp 1',
  'tag @s remove hcm_pq1_viewer',
  'execute if score @s hcm_pq1_temp matches 1 run return run '+cmd('banter_unavailable_self'),
  'execute if score @s hcd_pq1_banter matches 1.. run return run '+cmd('banter_unavailable_self'),
  'scoreboard players set @s hcd_pq1_banter 1200',cmd('node/b0')])
 f('banter_unavailable_self',[tell('Cet échange d’ambiance n’est pas disponible pour le moment.'),cmd('node/hugues_menu')])
 core[function_path('core/tick')]+=b'\nscoreboard players remove @a[scores={hcd_pq1_banter=1..}] hcd_pq1_banter 1\n'
 # Hooks historiques conservés aux mêmes adresses, y compris leurs fonctions near.
 redirects={'npc/geraud_rivet/interact_self':'geraud',PREFIX+'geraud_large_start_self':'geraud',
  PREFIX+'geraud_quai_interact_self':'geraud',PREFIX+'geraud_quai_complete_self':'geraud',
  PREFIX+'capitaine_large/interact_self':'altheon',PREFIX+'capitaine_large/ouvrir_conversation_self':'altheon',
  PREFIX+'capitaine_large/near_intro_self':'altheon',PREFIX+'capitaine_large/near_self':'altheon'}
 for i,role in enumerate(('gautier','hugues','anselme','perrin'),1):
  for suffix in ('_self','_quai_self'):redirects[PREFIX+'matelots/matelot_0'+str(i)+suffix]=role
 for name,role in redirects.items():put(name,[cmd('open/'+role+'_self')])
 for p in list(core):
  stem='data/capitale/function/'+PREFIX+'capitaine_large/'
  if p.startswith(stem) and p.endswith('.mcfunction') and any(t in p for t in ('reponse_','afficher_choix')):
   put(p.split('/function/',1)[1][:-11],[cmd('open/altheon_self')])
 # Les anciens séquenceurs PQ01 sont explicitement repris ; 301–303 restent l'amarrage original.
 seq='quest/dialogue/sequence/dispatch_self'
 prefix=[]
 for low,high,role in [(101,103,'geraud'),(201,251,'altheon'),(401,403,'geraud')]:
  entry='legacy_'+role+'_'+str(low)+'_self'
  f(entry,['scoreboard players set @s CAP_QSEQ 0','scoreboard players set @s CAP_QSEQ_TIMER 0',cmd('open/'+role+'_self')])
  prefix.append('execute if score @s CAP_QSEQ matches '+str(low)+'..'+str(high)+' run return run '+cmd(entry))
 core[function_path(seq)]=('\n'.join(prefix)+'\n').encode()+core[function_path(seq)]
 for p in list(core):
  if p.startswith('data/capitale/function/'+PREFIX+'sequence/') and p.endswith('.mcfunction'):
   name=p.split('/function/',1)[1][:-11]
   if '/sequence/geraud_large_' in p:put(name,[cmd('legacy_geraud_101_self')])
   elif '/sequence/capitaine_' in p:put(name,[cmd('legacy_altheon_201_self')])
   elif '/sequence/geraud_quai_' in p:put(name,[cmd('legacy_geraud_401_self')])
 # Préserver exactement les récompenses originales, à l'exception du rang prématuré.
 body=before['core'][function_path(PREFIX+'sequence/geraud_quai_completion_final_self')].decode()
 rank='execute if score @s CAP_RANGSOCIAL matches 0..9 run scoreboard players set @s CAP_RANGSOCIAL 10\n'
 assert body.count(rank)==1
 body=body.replace(rank,'')
 put('pq01/reward_body_self',[
  'execute unless score @s hcm_pq1_commit matches 1 run return 0',
  'execute unless score @s hcq_pq01 matches 40..99 run return 0',
  'scoreboard players set @s hcm_pq1_commit 0',*body.rstrip().splitlines()])
 f('complete_self',[
  'execute unless score @s hcm_pq1_node matches '+str(IDS['ex4'])+' run return 0',
  'execute unless score @s hcq_pq01 matches 40..99 run return run '+cmd('close_self'),
  'execute if score @s hcf_pq1_gain matches 1 run return run '+cmd('close_self'),
  'execute if score @s hcf_pq1_test matches 1 run return run '+cmd('test/complete_self'),
  'scoreboard players set @s hcf_pq1_gain 1','scoreboard players set @s hcm_pq1_commit 1',cmd('close_self'),cmd('reward_body_self')])
 f('test/complete_self',[
  'function hc_quetes:migration/write/pq01/v100',cmd('close_self'),
  tell('[PQ01 — test] Fin atteinte. Aucune récompense, aucun changement de rang ni lancement de PQ02. Utilisez /function capitale:pq01/admin/test_fin.','gold')])
 f('dock_self',[
  'execute unless score @s hcm_pq1_node matches '+str(IDS['cx4'])+' run return 0',
  'execute unless score @s hcf_pq1_c matches 1 run return 0',
  'execute unless score @s hcq_pq01 matches 10..39 run return run '+cmd('close_self'),
  cmd('close_self'),'scoreboard players set @s hcm_pq1_dock 1',
  'function capitale:'+PREFIX+'capitaine_large/conclure_et_arriver_self'])
 put(PREFIX+'capitaine_large/conclure_et_arriver_self',[
  'execute unless score @s hcm_pq1_dock matches 1 run return run '+cmd('open/altheon_self'),
  'scoreboard players set @s hcm_pq1_dock 0',
  'scoreboard players set @s CAP_QSEQ 301','scoreboard players set @s CAP_QSEQ_TIMER 1'])
 # La téléportation reste celle reçue. En relecture, ne pas déplacer le point de réapparition réel.
 p=function_path(PREFIX+'sequence/arrivee_tp_self')
 assert b'spawnpoint @s 93 228 82' in core[p]
 core[p]=core[p].replace(b'spawnpoint @s 93 228 82',b'execute unless score @s hcf_pq1_test matches 1 run spawnpoint @s 93 228 82')
 for stage,state in (('arrivee_prepare_self','10..39'),('arrivee_tp_self','10..39'),('arrivee_objectif_self','40..99')):
  p=function_path(PREFIX+'sequence/'+stage)
  guard=[
   'execute store result score @s hcm_pq1_valid run '+cmd('transition/check_self'),
   'execute unless score @s hcm_pq1_valid matches 1 run return run '+cmd('transition/abort_self'),
   'execute unless score @s hcq_pq01 matches '+state+' run return run '+cmd('transition/abort_self')]
  core[p]=('\n'.join(guard)+'\n').encode()+core[p]
 # Sauvegarde de relecture : compte ET personnage Nexus. Rien d'autre n'est réinitialisé.
 admin_pre=['execute store result score @s hcm_pq1_valid run '+cmd('context_self'),
  'execute unless score @s hcm_pq1_valid matches 1 run return 0',
  cmd('ensure_self'),
  'execute unless score @s CAP_QSEQ matches 0 run return 0',
  'execute if score @s CAP_QDIALOG_OWNER matches 1.. run return 0']
 f('admin/test_debut',[*admin_pre,cmd('test/start_macro')+' with storage hc_quetes:session current'])
 f('test/start_macro',[
  '$execute if data storage capitale:pq01_tests players."$(owner)"."$(character)" run return run '+cmd('test/already_self'),
  '$data modify storage capitale:pq01_tests players."$(owner)"."$(character)" set value {dimension:"$(dimension)",scores:{}}',
  *['$data modify storage capitale:pq01_tests players."$(owner)"."$(character)".'+axis+' set from entity @s Pos['+str(i)+']' for i,axis in enumerate('xyz')],
  *['$execute store result storage capitale:pq01_tests players."$(owner)"."$(character)".scores.'+s+' int 1 run scoreboard players get @s '+s for s in SAVED],
  *['scoreboard players set @s '+s+' 0' for s in SCORES[:8]],
  'scoreboard players set @s hcf_pq1_test 1','scoreboard players set @s hcf_pq1_ver 1',
  'function hc_quetes:migration/write/pq01/v0','scoreboard players set @s QUEST_PROLOGUE 10','scoreboard players set @s CAP_QUETEACTIVE 1',
  'execute in minecraft:overworld run tp @s 764 299 1101',
  tell('[PQ01 — test] Relecture activée pour ce personnage. Retour : /function capitale:pq01/admin/test_fin.','gold'),
  'indivissequence refresh'])
 f('test/already_self',[tell('[PQ01 — test] Une sauvegarde existe déjà pour ce personnage. Terminez la relecture avant d’en commencer une autre.','red')])
 f('error/character_self',[tell('[PQ01] Sélectionnez un personnage Nexus et attendez la fin de son chargement.','red')])
 f('error/busy_self',[tell('[PQ01] Fermez la conversation en cours et attendez la fin de la transition avant cette commande.','red')])
 f('admin/test_fin',[
  'execute store result score @s hcm_pq1_valid run '+cmd('context_self'),
  'execute unless score @s hcm_pq1_valid matches 1 run return 0',
  cmd('ensure_self'),
  'execute if score @s CAP_QDIALOG_OWNER matches 1..90 run return 0',
  'execute if score @s CAP_QSEQ matches 1.. run return 0',
  cmd('test/end_macro')+' with storage hc_quetes:session current'])
 f('test/end_macro',[
  '$execute unless data storage capitale:pq01_tests players."$(owner)"."$(character)" run return run '+cmd('test/no_backup_self'),
  cmd('close_self'),
  '$data modify storage capitale:pq01_tests restore set from storage capitale:pq01_tests players."$(owner)"."$(character)"',
  *['execute store result score @s '+s+' run data get storage capitale:pq01_tests restore.scores.'+s for s in SAVED],
  cmd('test/teleport_macro')+' with storage capitale:pq01_tests restore',
  '$data remove storage capitale:pq01_tests players."$(owner)"."$(character)"',
  'data remove storage capitale:pq01_tests restore','indivissequence refresh',
  tell('[PQ01 — test] États PQ01 et position restaurés pour ce personnage.','gold')])
 f('test/teleport_macro',['$execute in $(dimension) run tp @s $(x) $(y) $(z)'])
 f('test/no_backup_self',[tell('[PQ01 — test] Aucune sauvegarde de relecture pour ce personnage. Aucun état modifié.','red')])
 f('admin/reparer_scene_gautier',[
  'execute store result score @s hcm_pq1_valid run '+cmd('context_self'),
  'execute unless score @s hcm_pq1_valid matches 1 run return 0',
  cmd('ensure_self'),
  'execute unless score @s hcq_pq01 matches 0..19 run return 0',
  'execute if score @s CAP_QDIALOG_OWNER matches 1..90 run return 0',
  cmd('close_self'),'scoreboard players set @s hcf_pq1_a 0','scoreboard players set @s hch_pq1node 0',
  tell('[PQ01] Seule l’introduction de Géraud est réarmée. Aucune récompense ni autre quête réinitialisée.','gold')])
 f('admin/fermer_dialogue',[
  'execute if score @s CAP_QDIALOG_OWNER matches 91..96 run '+cmd('close_self')])
 f('admin/status',[
  tell('[PQ01-R2] Candidat texte : interruption Gautier en repli statique ; aucun PNJ déplacé ou cloné.','gold'),
  'scoreboard players get @s hcq_pq01','scoreboard players get @s hcf_pq1_test','scoreboard players get @s hcf_pq1_gain'])
 # Points individuels pour les waypoints : position réelle de l'admin, sans déplacement ni remplacement de PNJ.
 for phase in ('large','quai'):
  for role in ROLES:
   name=role+'_'+phase
   f('pose/'+name,[
    'execute store result score @s hcm_pq1_valid run '+cmd('context_self'),
    'execute unless score @s hcm_pq1_valid matches 1 run return 0',
    'data modify storage hc_quetes:lieux pq01_'+name+' set value {r:3}',
    *['execute store result storage hc_quetes:lieux pq01_'+name+'.'+axis+' int 1 run data get entity @s Pos['+str(i)+']' for i,axis in enumerate('xyz')],
    'data modify storage hc_quetes:lieux pq01_'+name+'.dimension set from storage hc_quetes:session current.dimension',
    tell('[PQ01] Point '+ROLES[role]+' ('+phase+') enregistré à votre position. Configurez séparément le PNJ existant dans EasyNPC.','gold')])
 # Les refus administrateur sont explicites ; les hooks near restent silencieux lorsqu'un autre dialogue occupe le joueur.
 for admin in ('admin/test_debut','admin/test_fin','admin/reparer_scene_gautier'):
  path=function_path('pq01/'+admin)
  s=core[path].decode()
  s=s.replace('execute unless score @s hcm_pq1_valid matches 1 run return 0','execute unless score @s hcm_pq1_valid matches 1 run return run '+cmd('error/character_self'))
  for condition in ('unless score @s CAP_QSEQ matches 0','if score @s CAP_QSEQ matches 1..','if score @s CAP_QDIALOG_OWNER matches 1..','if score @s CAP_QDIALOG_OWNER matches 1..90'):
   s=s.replace('execute '+condition+' run return 0','execute '+condition+' run return run '+cmd('error/busy_self'))
  core[path]=s.encode()
 # Catalogue de Clément : identifiant public inchangé, aucun changement aux 69 autres fiches.
 cat='data/hc_quetes/hcq_quests/pq_01_sous_pavillon_couronne.json'
 catalog=json.loads(quests[cat]);catalog['rewards']=[v for v in catalog['rewards'] if v.get('text')!='Rang de Visiteur']
 catalog['source']['active']['max']=99
 catalog['source']['turn_in']={'objective':'hcq_pq01','min':40,'max':99}
 catalog['objectives'][0]['done']={'objective':'hcf_pq1_a','min':1,'max':1}
 catalog['objectives'][2]['done']['min']=40
 for objective,point in zip(catalog['objectives'],('geraud_large','altheon_large','altheon_large','geraud_quai')):
  objective['waypoint']['target']='lieu:pq01_'+point
 quests[cat]=(json.dumps(catalog,ensure_ascii=False,indent=2)+'\n').encode()
 # Les nouveaux boutons « Je vous écoute » portent un jeton de session ; retirer uniquement le relais fixe PQ01 de valeur 5.
 dispatch=function_path('quest/dialogue/dispatch_self')
 core[dispatch]=('\n'.join(l for l in core[dispatch].decode().splitlines() if not ('migration/state/pq01/20' in l and 'QuestChoix matches 5' in l))+'\n').encode()
 for pack in packs.values():
  meta=json.loads(pack['pack.mcmeta']);meta['pack']['description']+=' — PQ01-R2 : dialogues et relecture ; candidat texte, scène statique'
  pack['pack.mcmeta']=(json.dumps(meta,ensure_ascii=False,indent=2)+'\n').encode()
 report={}
 for k,pack in packs.items():
  save(pack,OUT/FILES[k]);base=before[k]
  report[k]={'input':INPUTS[k][0],'input_sha256':INPUTS[k][1],'output':FILES[k],'output_sha256':digest((OUT/FILES[k]).read_bytes()),
   'changed':[p for p in sorted(base) if pack[p]!=base[p]],'added':sorted(set(pack)-set(base)),'removed':sorted(set(base)-set(pack))}
 (OUT/'diffs.json').write_text(json.dumps(report,ensure_ascii=False,indent=2)+'\n')
 (OUT/'dialogues.json').write_text(json.dumps(N,ensure_ascii=False,indent=2)+'\n')
 print(json.dumps({k:{f:v for f,v in r.items() if f not in ('changed','added','removed')} for k,r in report.items()},ensure_ascii=False,indent=2))
 return before,packs,report
if __name__=='__main__':build()
