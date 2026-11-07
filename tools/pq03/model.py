"""PQ03 : citations du dossier du 7 novembre, sans réécriture de ses répliques."""
from pathlib import Path
import re,json,hashlib
H=Path(__file__).resolve().parent;SCRIPT=H/'kit/PQ03_finalisation_Work_2026-11-07.md'
TEXT=SCRIPT.read_text();SPLIT=re.split(r'^### ([A-Z]\d\d)\..*$',TEXT,flags=re.M)
PARTS={k:re.findall('«([^»]+)»',v) for k,v in zip(SPLIT[1::2],SPLIT[2::2])};USED=set();N={}
def q(k,i):USED.add((k,i));return PARTS[k][i].strip()
def c(k,i,target,effect='',condition=''):return dict(text=q(k,i),target=target,effect=effect,condition=condition,source_section=k,source_index=i)
def nav(text,target,condition=''):return dict(text=text,target=target,effect='',condition=condition)
def node(key,section='',refs=(),choices=(),next='',condition=''):
 N[key]=dict(id=len(N)+1,key=key,speaker='aurele',camera='aurele',editorial=section,lines=[q(section,i) for i in refs],source_lines=[],choices=list(choices),next=next,condition=condition)
INTRO=[c('A01',1,'a_watch'),c('A01',2,'a_service'),c('A01',3,'a_routes')]
ROUTES=[c('A01',7,'a_market'),c('A01',9,'a_depth'),c('A01',11,'a_questions'),c('A01',12,'event:later')]
TOPICS=[c('B03',0,'b_job'),c('B04',0,'b_rules'),c('A01',1,'a_watch'),c('A01',2,'a_service'),nav('Vous évoquiez le grondement du Cœur.','b_rumble',condition='rumble'),nav('Revenons à notre trajet.','event:context')]
CONT=[nav('Une autre question.','t_menu'),nav('Reprenons notre route.','event:resume')]
node('a01','A01',[0],INTRO)
node('a_questions',choices=INTRO)
node('a_watch','A01',[4],[nav('Une autre question.','event:questions'),c('A01',3,'a_routes',condition='intro')])
node('a_service','A01',[5],[nav('Une autre question.','event:questions'),c('A01',3,'a_routes',condition='intro')])
node('a_routes','A01',[6],ROUTES)
node('a_market','A01',[8],next='event:market')
node('a_depth','A01',[10],next='event:depth')
node('b_walk','B01',[0,1])
node('t_menu',choices=TOPICS)
RUMBLE=[c('B02',2,'b_rumble_origin'),c('B02',4,'b_rumble_worry'),c('B02',6,'b_rumble_learn'),*CONT]
node('b_rumble','B02',[0,1],RUMBLE,condition='rumble')
for key,i in [('origin',3),('worry',5),('learn',7)]:node('b_rumble_'+key,'B02',[i],[nav('Revenons au grondement.','b_rumble'),*CONT],condition='rumble')
JOB=[c('B03',2,'b_job_monotony'),c('B03',4,'b_job_people'),c('B03',6,'b_job_like'),*CONT]
node('b_job','B03',[1],JOB)
for key,i in [('monotony',3),('people',5),('like',7)]:node('b_job_'+key,'B03',[i],JOB)
DISC=[c('B04',8,'event:questions',effect='discipline:1',condition='undecided'),c('B04',9,'event:questions',effect='discipline:2',condition='undecided'),c('B04',10,'event:questions',effect='discipline:3',condition='undecided')]
RULES=[c('B04',2,'b_rules_flex'),c('B04',4,'b_rules_discipline'),c('B04',6,'b_rules_freedom'),nav('Je voudrais préciser mon avis.','b_position',condition='undecided'),*CONT]
node('b_rules','B04',[1],RULES)
for key,i in [('flex',3),('discipline',5),('freedom',7)]:node('b_rules_'+key,'B04',[i],RULES)
node('b_position','B04',[1],DISC)
node('b_circulation','B05',[0,1],[c('B05',2,'b_discreet'),c('B05',4,'b_uniform'),*CONT])
node('b_discreet','B05',[3],[c('B05',4,'b_uniform'),*CONT])
node('b_uniform','B05',[5],CONT)
MARKET=[c('B06',1,'b_market_know'),c('B06',4,'b_market_thanks'),c('B06',6,'b_market_already'),c('B06',8,'b_market_bye')]
node('b_market','B06',[0,3],MARKET)
node('b_market_know','B06',[2],MARKET)
for key,i in [('thanks',5),('already',7),('bye',9)]:node('b_market_'+key,'B06',[i],next='event:return')
node('b_reunion','B07',[0],[c('B07',1,'b_reunion_yes'),c('B07',3,'b_reunion_look'),c('B07',5,'b_reunion_no')])
REUNION=[c('B07',8,'event:depth'),c('B07',9,'t_menu'),c('B07',10,'event:later')]
for key,i in [('yes',2),('look',4),('no',6)]:node('b_reunion_'+key,'B07',[i,7],REUNION)
node('b_reunion_routes','B07',[7],REUNION)
node('c_district','C01',[0,1],[c('C01',2,'c_different'),c('C01',4,'c_judge'),*CONT])
node('c_different','C01',[3],[c('C01',4,'c_judge'),*CONT])
node('c_judge','C01',[5],CONT)
node('c_approach','C02',[0,1],[c('C02',2,'c_trouble'),*CONT])
node('c_trouble','C02',[3],CONT)
FINAL=[c('C04',i,key) for i,key in [(0,'c_alone'),(2,'c_danger'),(4,'c_home'),(6,'c_back'),(8,'c_position')]]
node('c03','C03',[0,1,2,3],FINAL)
node('c_questions',choices=FINAL)
for key,i in [('alone',1),('danger',3),('home',5),('back',7)]:node('c_'+key,'C04',[i],FINAL)
node('c_position','C05',[0],[c('C05',i,key,effect='separation:'+str(v)) for i,key,v in [(1,'c_thanks',1),(3,'c_neutral',2),(5,'c_critical',3),(7,'c_contempt',4)]])
for key,i in [('thanks',2),('neutral',4),('critical',6),('contempt',8)]:node('c_'+key,'C05',[i],next='c_conclusion')
node('c_conclusion','C06',[0],[c('C06',i,'c_bye') for i in [1,2,3]])
node('c_bye','C06',[4],next='event:complete')
node('c_recall','C07',[0])
N['c_recall']['objective']=q('C07',1)
node('post',choices=[nav('Je voudrais vous poser une question.','t_menu')])
node('post_neutral',choices=[nav('Je voudrais vous poser une question.','t_menu')])
node('post_unfriendly',choices=[nav('Je voudrais vous poser une question.','t_menu')])
import json as _json
_old=(H/'baseline_core/data/capitale/function/quest/sous_le_regard_du_coeur/aurele/postquest_self.mcfunction').read_text()
_greetings=[_json.loads(line[line.index('['):])[1]['text'].strip() for line in _old.splitlines() if 'run tellraw' in line]
for key,text in zip(['post','post_neutral','post_unfriendly'],_greetings):N[key]['lines']=[text];N[key]['source_existing_core']='aurele/postquest_self'

EVENTS={'market','depth','later','return','resume','questions','context','complete'}
for n in N.values():
 assert len(n['choices'])<=13,n['key']
 for target in [c['target'] for c in n['choices']]+([n['next']] if n['next'] else []):assert target in N or target.startswith('event:') and target[6:] in EVENTS,target
def export():return dict(schema=1,script_sha256=hashlib.sha256(SCRIPT.read_bytes()).hexdigest(),nodes=list(N.values()),events=sorted(EVENTS),source_quotes_used=sorted(USED),editorial_notes=['Les raccords C04 sont conservés selon le dossier fourni.','Les choix sont mémorisés une fois ; leur résultat est versé à la clôture C06.','B02 dépend de la confirmation admin du grondement audible à D.'])
if __name__=='__main__':print(json.dumps(export(),ensure_ascii=False,indent=2))
