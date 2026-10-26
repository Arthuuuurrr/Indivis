"""Banc QMARK1 : fonctions/predicats livrés, et cas d'état indépendants."""
import json,re,zipfile
import build
from build_quest_markers import FILES
from quest_markers import Q,GIVERS,pred

def prepare(pack,server):
 data={}
 for filename in FILES.values():
  with zipfile.ZipFile(build.OUT/filename) as z:data.update({n:z.read(n) for n in z.namelist() if not n.endswith('/')})
 wanted=lambda n: n.startswith(('data/capitale/function/quest/markers/','data/capitale/tags/entity_type/quest_markers/',
                   'data/hc_quetes/predicate/migration/state/','data/hc_quetes/function/migration/ensure/',
                   'data/hc_quetes/function/migration/write/')) or n in ['data/hc_quetes/function/runtime/second.mcfunction','data/hc_quetes/function/runtime/markers.mcfunction']
 for n,b in data.items():
  if wanted(n):p=pack/n;p.parent.mkdir(parents=True,exist_ok=True);p.write_bytes(b)
 # Les intégrations extérieures ne participent pas à ce test des marqueurs.
 # Le runtime/second réel reste intact, y compris sa nouvelle dernière ligne.
 second=data['data/hc_quetes/function/runtime/second.mcfunction'].decode()
 for target in re.findall(r'\bfunction ([a-z0-9_.:/-]+)',second):
  if target.startswith('capitale:quest/markers/'):continue
  n='data/'+target.replace(':','/function/')+'.mcfunction'
  p=pack/n;p.parent.mkdir(parents=True,exist_ok=True);p.write_text('return 0\n')
 def native(qid,state):
  obj=json.loads(data['data/hc_quetes/predicate/migration/state/'+qid+'/'+state+'.json'])['scores']
  return {k:v.get('min',v.get('max',0)) for k,v in obj.items()}
 def value(s):return int(s.split('..')[0] or s.split('..')[1]) if '..' in s else int(s)
 base={'CAP_PROFILE_LOCK':0,'CAP_QUETEACTIVE':0,'CAP_RANGSOCIAL':0,'CAP_CD_BEER_GC':0,'hcm_pq1_active':0}
 for n,b in data.items():
  if n.startswith('data/hc_quetes/predicate/migration/state/') and n.endswith('.json'):
   for objective in json.loads(b).get('scores',{}):base[objective]=0
 for q in Q:
  base['hcq_'+q['qid']]=0
  for rule in q['rules']:
   for objective in list(rule['scores'])+list(rule['not_scores']):base[objective]=0
 cases=[];positive={}
 accepted={'pq01':'20','pq02':'20','pq03':'20','pq04':'20','pq05':'20','sqhcci01':'20',
           'sqbaob01':'20','sqbaob02':'20','sqbaob03':'20','sqbaob04':'20','sqhcso01':'20',
           'sqhcso02':'20','sqhcso03':'10','rqhc01':'10','rqhc02':'20','dqhc01':'10','dqhc02':'10',
           'dqhc03':'1','dqhc04':'20','dqhc05':'20','sqhcvn01':'20','sqhcvn02':'20','sqhcvn03':'20','sqhcvn04':'20'}
 for q in Q:
  for i,rule in enumerate(q['rules']):
   scores=base.copy()
   for qid,state in rule['states'].items():scores.update(native(qid,state))
   for objective,match in rule['scores'].items():scores[objective]=value(match)
   for qid,state in rule['unless'].items():
    for objective,n in native(qid,state).items():
     if scores[objective]==n:scores[objective]=max(0,n-1) if n else 100
   cases.append(dict(qid=q['qid'],scores=scores,expected=1,label=q['qid']+' offered variant '+str(i)))
   positive.setdefault(q['qid'],scores)
  scores=positive[q['qid']].copy()
  write=data['data/hc_quetes/function/migration/write/'+q['qid']+'/v'+accepted[q['qid']]+'.mcfunction'].decode()
  assigned=dict((k,int(n)) for k,n in re.findall(r'^scoreboard players set @s (\S+) (-?\d+)$',write,re.M))
  assert 'hcq_'+q['qid'] in assigned,q['qid']
  scores.update(assigned)
  cases.append(dict(qid=q['qid'],scores=scores,expected=0,label=q['qid']+' accepted: no !'))
  if q['guide']:
   scores=positive[q['qid']].copy();scores.update(native('pq02','20'))
   cases.append(dict(qid=q['qid'],scores=scores,expected=0,label=q['qid']+' escort blocks offer'))
  if q['active'] is not None:
   scores=positive[q['qid']].copy();scores['CAP_QUETEACTIVE']=999
   cases.append(dict(qid=q['qid'],scores=scores,expected=0,label=q['qid']+' incompatible active quest'))
  for objective in q['rules'][0]['not_scores']:
   scores=positive[q['qid']].copy();scores[objective]=1
   cases.append(dict(qid=q['qid'],scores=scores,expected=0,label=q['qid']+' '+objective+' blocks offer'))
  if q['qid']=='dqhc02':
   scores=positive[q['qid']].copy();scores['CAP_CD_BEER_GC']=1
   cases.append(dict(qid=q['qid'],scores=scores,expected=0,label='veteran cooldown blocks offer'))
 # Exclusions causées par le routeur du PNJ, pas par la définition du journal.
 for qid,changes,label in [('sqbaob02',native('sqbaob03','20'),'Elias busy with delivery'),
                           ('sqbaob04',{**native('sqbaob03','40'),**native('sqbaob01','102')},'Roland busy with delivery')]:
  cases.append(dict(qid=qid,scores={**positive[qid],**changes},expected=0,label=label))
 for qid,obj,n,label in [('sqhcvn01','CAP_RANGSOCIAL',19,'Maelor resident requirement'),
                        ('sqhcci01','CAP_RANGSOCIAL',30,'citizen no new citizenship offer'),
                        ('pq05','CAP_RANGSOCIAL',20,'resident no residence offer')]:
  cases.append(dict(qid=qid,scores={**positive[qid],obj:n},expected=0,label=label))
 for qid,previous in [('pq02','pq01'),('pq03','pq02'),('pq04','pq03'),('pq05','pq04'),('sqhcvn02','sqhcvn01'),('sqhcvn03','sqhcvn02'),('sqhcvn04','sqhcvn03')]:
  cases.append(dict(qid=qid,scores={**positive[qid],'hcq_'+previous:0},expected=0,label=qid+' missing prerequisite'))
 givers=[dict(giver=g, name=names[0], scores=positive[next(q['qid'] for q in Q if q['giver']==g)]) for g,(names,tags) in GIVERS.items()]
 # Actualiser les objectifs du banc à partir des fonctions testées.
 objectives=set(base)|{'qmark_probe'}
 for p in pack.rglob('*.mcfunction'):
  objectives.update(re.findall(r'(?:@s|#[A-Za-z0-9_]+) ([A-Za-z][A-Za-z0-9_]*)',p.read_text()))
 (server/'qmark-fixtures.json').write_text(json.dumps(dict(base=base,cases=cases,givers=givers,objectives=sorted(objectives)),ensure_ascii=False))

