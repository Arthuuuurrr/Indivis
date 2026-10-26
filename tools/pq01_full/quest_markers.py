"""Marqueurs de disponibilité du core, ajoutés aux datapacks HF9/HF4.

Aucun JAR n'est modifié. Les prédicats de migration sont ceux des interactions
réelles du core : les anciens nombres d'étape ne sont pas les valeurs hcq_*.
"""
from collections import defaultdict
import json

PREFIX = 'capitale:quest/markers/'
NPC_TYPES = ['allay', 'bogged', 'cat', 'chicken', 'creeper', 'drowned',
             'enderman', 'evoker', 'fox', 'ghast', 'horse', 'horse_skeleton',
             'horse_zombie', 'humanoid', 'humanoid_slim', 'illusioner',
             'iron_golem', 'piglin', 'piglin_brute', 'piglin_zombified', 'pig',
             'pillager', 'skeleton', 'stray', 'wither_skeleton', 'slime',
             'spider', 'cave_spider', 'villager', 'wandering_trader', 'vex',
             'vindicator', 'witch', 'wolf', 'zombie', 'zombie_husk',
             'zombie_villager', 'skeleton_horse', 'zombie_horse', 'zombified_piglin', 'husk']

GIVERS = {
 'geraud': (['Géraud Rivet', 'Géraud', 'Quartier-maître Géraud Rivet'], ['hcq_pnj_geraud']),
 'leovic': (['Léovic', 'Garde Léovic'], ['npc_leovic_registre_port', 'hcq_pnj_leovic']),
 'aurele': (['Aurèle Veyrane', 'Aurèle'], ['npc_aurele_veyrane', 'hcq_pnj_aurele']),
 'roch': (['Roch Vallet', 'Roch'], ['npc_roch_vallet', 'hcq_pnj_roch']),
 'magistrat': (['Magistrat des Profondeurs', 'Magistrat'], ['hcq_pnj_magistrat']),
 'mira': (['Mira'], ['npc_mira_q01', 'hcq_pnj_mira']),
 'elias': (['Elias Ferbois', 'Élias Ferbois', 'Elias', 'Élias'], ['hcq_pnj_elias_ferbois']),
 'lysandre': (['Lysandre'], ['hcq_pnj_lysandre']),
 'roland': (['Roland'], ['hcq_pnj_roland']),
 'odon': (['Odon Varenne', 'Sieur Odon', 'Odon'], ['hcq_pnj_odon', 'hcq_pnj_sieur_odon']),
 'libraire': (['Libraire agréé', 'Libraire'], ['hcq_pnj_libraire']),
 'aldren': (['Aldren le Traqueur', 'Aldren'], ['hcq_pnj_aldren']),
 'veilleuse': (['Veilleuse des Profondeurs', 'Veilleuse'], ['hcq_pnj_veilleuse']),
 'technicienne': (['Technicienne du Cœur', 'Technicienne du Coeur', 'Technicienne'], ['hcq_pnj_technicienne']),
 'employe_banque': (['Employé de la Banque', 'Employé de banque'], ['hcq_pnj_employe_banque']),
 'veteran': (['Vétéran de la Garde du Cœur', 'Vétéran de la Garde du Coeur', 'Vétéran'], ['hcq_pnj_veteran']),
 'commis_port': (['Commis du Port', 'Commis du port'], ['hcq_pnj_commis_port']),
 'commis_messageries': (['Commis des Messageries', 'Commis des messageries'], ['hcq_pnj_commis_messageries']),
 'greffier': (['Greffier des Quais', 'Greffier des quais'], ['hcq_pnj_greffier']),
 'maelor': (['Maelor Veyne', 'Maelor'], ['hcq_pnj_maelor']),
}

def clause(states=None, unless=None, scores=None, not_scores=None):
 return {'states': states or {}, 'unless': unless or {}, 'scores': scores or {}, 'not_scores':not_scores or {}}

def quest(qid, giver, rules, source, guide=True, active=None):
 return dict(qid=qid, giver=giver, rules=rules, source=source, guide=guide, active=active)

Q = [
 quest('pq01', 'geraud', [clause({'pq01':'0'}, not_scores={'hcm_pq1_active':'1..'})],
       'npc/geraud_rivet/interact_self', guide=False),
 quest('pq02', 'leovic', [clause({'pq01':'100','pq02':s}) for s in ['0','10','1_to_2']],
       'quest/le_registre_du_port/leovic/interact_self', active=4),
 quest('pq03', 'aurele', [clause({'pq02':'100_to_','pq03':s}) for s in ['0','1','10']],
       'quest/sous_le_regard_du_coeur/aurele/interact_self', active=3),
 quest('pq04', 'roch', [clause({'pq03':'100_to_','pq04':s}) for s in ['0','1','10']],
       'quest/au_seuil_des_profondeurs/roch/interact_self', active=5),
 quest('pq05', 'magistrat', [clause({'pq04':'100_to_','pq05':s}, scores={'CAP_RANGSOCIAL':'..19'}) for s in ['0','1','10']],
       'quest/une_adresse_dans_les_profondeurs/magistrat/interact_self', active=6),
 quest('sqhcci01', 'magistrat', [clause({'sqhcci01':'0'}, scores={'CAP_RANGSOCIAL':'20..29'})],
       'quest/une_adresse_dans_les_profondeurs/magistrat/interact_self', guide=False),
 quest('sqbaob01', 'mira', [clause({'sqbaob01':'0'})],
       'quest/side/bas_anneaux/q01_quartier_oublies/accept_self', guide=False),
 quest('sqbaob02', 'elias', [clause({'sqbaob02':'0'}, {'sqbaob03':'20'})],
       'npc/elias_ferbois/interact_self', guide=False),
 quest('sqbaob03', 'lysandre', [clause({'sqbaob03':'0'})],
       'quest/side/bas_anneaux/q03_marche_mille_voix/interact_self', guide=False),
 quest('sqbaob04', 'roland', [clause({'sqbaob04':'0'}, {'sqbaob03':'40'}),
                           clause({'sqbaob04':'0','sqbaob03':'40'}, {'sqbaob01':'102'})],
       'npc/roland/interact_self', guide=False),
 quest('sqhcso01', 'odon', [clause({'sqhcso01':s}) for s in ['0','1_to_2']],
       'quest/divers/acte_dette/sieur_odon/interact_self'),
 quest('sqhcso02', 'libraire', [clause({'sqhcso02':'0'})], 'npc/libraire/work_offer_self'),
 quest('sqhcso03', 'aldren', [clause({'sqhcso03':'0'})], 'quest/side/menace_orcs/interact_self', guide=False),
 quest('rqhc01', 'veilleuse', [clause({'rqhc01':'0'}, not_scores={'hcd_rqhc01':'1..'})],
       'quest/divers/lampe_manquante/veilleuse/interact_player', guide=False),
 quest('rqhc02', 'technicienne', [clause(unless={'rqhc02':'20_to_50'}, not_scores={'hcd_rqhc02':'1..'})],
       'quest/divers/relais_coeur/technicien/interact_self'),
 quest('dqhc01', 'employe_banque', [clause(unless={'dqhc01':'10'}, not_scores={'hcd_dqhc01':'1..'})],
       'quest/journalieres/banque/employe/interact_player'),
 quest('dqhc02', 'veteran', [clause({'dqhc02':s}, scores={'CAP_CD_BEER_GC':'0'}) for s in ['0','100_to_']],
       'quest/journalieres/biere_veteran/interact_self', guide=False),
 quest('dqhc03', 'commis_port', [clause(unless={'dqhc03':'1_to_2'}, not_scores={'hcd_dqhc03':'1..'})],
       'quest/journalieres/registre_port/commis/interact_self'),
 quest('dqhc04', 'commis_messageries', [clause(unless={'dqhc04':'20_to_30'}, not_scores={'hcd_dqhc04':'1..'})],
       'quest/journalieres/ressort_clocher/donneur/interact_self'),
 quest('dqhc05', 'greffier', [clause(unless={'dqhc05':'20'}, not_scores={'hcd_dqhc05':'1..'})],
       'quest/journalieres/temoignages_quais/greffier/interact_self'),
 quest('sqhcvn01', 'maelor', [clause({'sqhcvn01':'0'}, scores={'CAP_RANGSOCIAL':'20..'}),
                           clause({'sqhcvn01':'1_to_2'})], 'quest/vent_noir/maelor/interact_self', active=0),
 quest('sqhcvn02', 'maelor', [clause({'sqhcvn01':'100','sqhcvn02':'0'})],
       'quest/vent_noir/maelor/interact_self', active=0),
 quest('sqhcvn03', 'maelor', [clause({'sqhcvn02':'100','sqhcvn03':'0'})],
       'quest/vent_noir/maelor/interact_self', active=0),
 quest('sqhcvn04', 'maelor', [clause({'sqhcvn03':'100','sqhcvn04':'0'})],
       'quest/vent_noir/maelor/interact_self', active=0),
]

def path(s): return 'data/capitale/function/quest/markers/'+s+'.mcfunction'
def pred(qid, state): return 'hc_quetes:migration/state/'+qid+'/'+state
def json_bytes(o): return (json.dumps(o, ensure_ascii=False, indent=2)+'\n').encode()

def apply(packs,version=1):
 core, quests = packs['core'], packs['quests']
 import re
 context=('capitale:quest_markers' if version==2 else 'hc_quetes:session')+' current'
 guide = core['data/capitale/function/quest/guidage/sync_self.mcfunction'].decode()
 guide_predicates = re.findall(r'execute if predicate (\S+) run scoreboard players set @s CAP_GUIDE_LOCK 1', guide)
 assert len(guide_predicates)==5, 'La règle de guidage a changé : réauditer les disponibilités.'
 generated = {}
 def f(name, lines):
  generated[path(name)] = ('# QMARK'+str(version)+' : visibilité personnelle des offres du core.\n'+'\n'.join(lines)+'\n').encode()
 f('guide_free', ['execute if predicate '+p+' run return 0' for p in guide_predicates]+['return 1'])
 by_giver = defaultdict(list)
 for q in Q:
  assert 'data/capitale/function/'+q['source']+'.mcfunction' in core, q
  lines=[]
  if q['guide']: lines.append('execute unless function '+PREFIX+'guide_free run return 0')
  if q['active'] is not None:
   if q['active']==0:lines.append('execute unless score @s CAP_QUETEACTIVE matches 0 run return 0')
   else:lines.append('execute unless score @s CAP_QUETEACTIVE matches 0 unless score @s CAP_QUETEACTIVE matches '+str(q['active'])+' run return 0')
  for rule in q['rules']:
   cond=[]
   for word, key in [('if','states'),('unless','unless')]:
    for qid,state in rule[key].items():
     assert 'data/hc_quetes/predicate/migration/state/'+qid+'/'+state+'.json' in quests, (q['qid'],qid,state)
     cond.append(word+' predicate '+pred(qid,state))
   for objective,match in rule['scores'].items():cond.append('if score @s '+objective+' matches '+match)
   for objective,match in rule['not_scores'].items():cond.append('unless score @s '+objective+' matches '+match)
   lines.append('execute '+' '.join(cond)+' run return 1')
  f('available/'+q['qid'],lines+['return 0'])
  by_giver[q['giver']].append(q['qid'])
 f('update_self', ['function '+PREFIX+'giver/'+giver+' with storage '+context for giver in GIVERS])
 npc_filter='type=#capitale:quest_markers/npcs,tag=!hcm_private,distance=..24,sort=nearest,limit=1'
 owner='tag=cap_qmark,tag=hcm_owner_$(owner)'
 current=owner+',tag=hcm_character_$(character)'
 for giver,(names,tags) in GIVERS.items():
  f('giver/'+giver, ['execute if function '+PREFIX+'available/'+qid+' run return run function '+PREFIX+'locate/'+giver+' with storage '+context for qid in by_giver[giver]]+['return 0'])
  if version==2:f('giver_available/'+giver, ['execute if function '+PREFIX+'available/'+qid+' run return 1' for qid in by_giver[giver]]+['return 0'])
  selectors=[f'@e[{npc_filter},tag=cap_qgiver_{giver}]']+[f'@e[{npc_filter},tag={t}]' for t in tags]+[f'@e[{npc_filter},name={json.dumps(n,ensure_ascii=False)}]' for n in names]
  f('locate/'+giver, ['execute at '+sel+' run return run function '+PREFIX+'at/'+giver+' with storage '+context for sel in selectors]+['return 0'])
  selector='@e[type=minecraft:text_display,'+current+',tag=cap_qgiver_'+giver+',limit=1]'
  tag_list=['hcm_private','cap_qmark','cap_qmark_live','cap_qgiver_'+giver,'hcm_owner_$(owner)','hcm_character_$(character)']
  if version==2:tag_list.append('cap_qmark_v2')
  style='see_through:1b,brightness:{block:15,sky:15}' if version==2 else 'see_through:0b'
  height='3.05' if version==2 else '2.6'
  nbt='{Tags:'+json.dumps(tag_list,separators=(',',':'))+',text:{text:"!",color:"yellow",bold:true},billboard:"center",shadow:1b,'+style+',background:0,line_width:64,view_range:0.4f,teleport_duration:20}'
  migration=['$execute as '+selector+' unless entity @s[tag=cap_qmark_v2] run data merge entity @s {see_through:1b,brightness:{block:15,sky:15}}',
             '$tag '+selector+' add cap_qmark_v2'] if version==2 else []
  f('at/'+giver,[
   '$execute positioned ~ ~'+height+' ~ unless entity '+selector+' run summon minecraft:text_display ~ ~ ~ '+nbt,
   *migration,
   '$tag '+selector+' add cap_qmark_live',
   '$execute positioned ~ ~'+height+' ~ as '+selector+' unless entity @s[distance=..0.05] run tp @s ~ ~ ~',
   'return 1'])
  f('liaison/'+giver,[
   '# Facultatif : seulement pour un PNJ renommé qui ne possède aucun alias connu.',
   'tag @e[type=#capitale:quest_markers/npcs,tag=!hcm_private,distance=..4,sort=nearest,limit=1] add cap_qgiver_'+giver,
   *(['tag @e[type=#capitale:quest_markers/npcs,tag=!hcm_private,distance=..4,sort=nearest,limit=1] remove hcm_qmark_auto_'+giver] if version==2 else [])])
  f('liberer/'+giver,['tag @e[type=#capitale:quest_markers/npcs,tag=cap_qgiver_'+giver+',distance=..4,sort=nearest,limit=1] remove cap_qgiver_'+giver])
 f('tick_self',[
  *(['$data modify storage capitale:quest_markers current set value {owner:"$(owner)",character:"$(character)"}'] if version==2 else []),
  '$tag @e[type=minecraft:text_display,'+owner+'] remove cap_qmark_live',
  'execute unless score @s CAP_PROFILE_LOCK matches 1.. at @s run function '+PREFIX+'update_self with storage '+context,
  '$kill @e[type=minecraft:text_display,'+owner+',tag=!cap_qmark_live]'])
 # L'entrée s'exécute après le contexte Nexus du joueur courant ; aucun sélecteur @p.
 second='data/hc_quetes/function/runtime/second.mcfunction'
 hook='function '+PREFIX+'tick_self with storage hc_quetes:session current\n'
 assert hook.encode() not in quests[second]
 if version==1:quests[second]+=b'\n# QMARK1 : offres du core, personnage Nexus courant.\n'+hook.encode()
 else:
  f('version',['return 2'])
  f('load',['scoreboard objectives add hcm_qmark_probe dummy','data modify storage capitale:quest_markers version set value 2'])
  core['data/capitale/function/core/load.mcfunction']+='\n# QMARK2 : repères pilotés par le journal HF12.\nfunction capitale:quest/markers/load\n'.encode()
 # Les noms et tags ne recherchent que les types EasyNPC ; aucune sérialisation NBT par tick.
 generated['data/capitale/tags/entity_type/quest_markers/npcs.json']=json_bytes({'replace':False,'values':[{'id':'easy_npc:'+t,'required':False} for t in NPC_TYPES]})
 core.update(generated)
 catalog={'version':'QMARK'+str(version),'radius':24,'interval_ticks':20,'quests':Q,'givers':{g:{'names':n,'tags':t} for g,(n,t) in GIVERS.items()},'guide_predicates':guide_predicates}
 if version==2:
  # Déduire les alias d'interaction sans exécuter leurs fonctions et sans toucher aux PNJ.
  texts={n.split('/')[1]+':'+n.split('/function/',1)[1][:-11]:b.decode() for n,b in core.items() if '/function/' in n and n.endswith('.mcfunction')}
  reach={k:set() for k in texts}
  for q in Q:reach['capitale:'+q['source']].add(q['giver'])
  edges={k:set(re.findall(r'(?:^|\brun )function ([a-z0-9_:/.-]+)',v,re.M))&set(texts) for k,v in texts.items()}
  while True:
   changed=False
   for k,targets in edges.items():
    merged=reach[k].union(*(reach[t] for t in targets))
    if merged!=reach[k]:reach[k]=merged;changed=True
   if not changed:break
  commands={k:next(iter(v)) for k,v in reach.items() if len(v)==1 and (k.endswith(('/interact_self','/interact_player','/work_offer_self','/quest_self')) or any(k=='capitale:'+q['source'] for q in Q))}
  commands.update({'capitale:npc/'+g+'/interact_self':g for g in ['mira','lysandre','roland','aldren']})
  commands['capitale:npc/elias_ferbois/interact_self']='elias'
  catalog['command_aliases']=dict(sorted(commands.items()))
  rows=[]
  for giver,(names,tags) in GIVERS.items():
   rows.extend('name\t'+giver+'\t'+n for n in names)
   rows.extend('tag\t'+giver+'\t'+t for t in tags)
  rows.extend('function\t'+giver+'\t'+command for command,giver in sorted(commands.items()))
  packs['journal']['indivis_core_quest_givers.tsv']=('\n'.join(rows)+'\n').encode()
 core['docs/quest_markers/'+catalog['version']+'_CATALOGUE.json']=json_bytes(catalog)
 for kind in ['core','quests']:
  meta=json.loads(packs[kind]['pack.mcmeta']);meta['pack']['description']+=' — '+catalog['version']+' : ! personnels des donneurs du core'
  packs[kind]['pack.mcmeta']=json_bytes(meta)
 return catalog
