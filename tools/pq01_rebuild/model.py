"""Graphe PQ01 : textes extraits littéralement du prompt fourni, aucun doublage généré."""
from pathlib import Path
import re, json

ROOT=Path(__file__).resolve().parents[2]
PROMPT=ROOT/'upload/PROMPT_WORK_PQ01_DIRIGEABLE.md'
text=PROMPT.read_text()
section=text[text.index('## SCÈNE A'):text.index('# 5. SYSTÈMES')]
Q=[m.group(1) for m in re.finditer('« ([^»]+) »',section)]
assert len(Q)==150 and Q[88]=='Non. Je ne me souviens de rien avant votre sauvetage.'
N={}
ROLES={'geraud':'Géraud Rivet','altheon':'Althéon Brumeforge','gautier':'Gautier','hugues':'Hugues','anselme':'Anselme','perrin':'Perrin'}
OWNERS={r:91+i for i,r in enumerate(ROLES)}
def option(label,target,condition=None):
    return {'label':Q[label] if isinstance(label,int) else label,'target':target,**({'condition':condition} if condition else {})}
def node(key,role,lines=(),choices=(),effect=None,resume=None):
    assert key not in N
    N[key]={'role':role,'lines':[Q[x] if isinstance(x,int) else x for x in lines], 'choices':list(choices), 'effect':effect,'resume':resume or key}
    return key
def linear(key,role,lines,target,effect=None,resume=None):
    return node(key,role,lines,[option('Continuer.',target)],effect,resume)

node('a1','geraud',[0],[option(1,'a2')])
linear('a2','geraud',[2],'a3')
linear('a3','gautier',[3],'a4',resume='a4')
linear('a4','geraud',[4],'a5')
linear('a5','geraud',[5],'a6')
linear('a6','gautier',[6],'a7',resume='a7')
node('a7','geraud',[7],[option(8,'a8')])
linear('a8','geraud',[9],'ga_menu')
node('ga_menu','geraud',[11],[option(i,'ga_'+str(i)) for i in (12,14,16,18,20)],effect='intro_a_done')
for i in (12,14,16,18,20): linear('ga_'+str(i),'geraud',[i+1],'close' if i==20 else 'ga_menu')

for role,start in [('gautier',22),('hugues',35),('anselme',48),('perrin',61)]:
    menu=role+'_menu'
    choices=[option(i,role+'_'+str(i)) for i in range(start+1,start+12,2)]
    if role=='hugues': choices.insert(-1,option('Écouter leur échange sur les procédures.','banter_start'))
    node(menu,role,[start],choices)
    for i in range(start+1,start+12,2):
        linear(role+'_'+str(i),role,[i+1],'close' if i==start+11 else menu)
for i in range(8): linear('b'+str(i),'gautier' if i%2==0 else 'hugues',[74+i], 'b'+str(i+1) if i<7 else 'hugues_menu')

node('c0','altheon',[],[option('Je vous écoute.','c1')])
linear('c1','altheon',[83],'c2')
node('c2','altheon',[84],[option(85,'c3')])
linear('c3','altheon',[86],'c4')
node('c4','altheon',[87],[option(88,'c5')])
linear('c5','altheon',[89],'c6')
linear('c6','altheon',[90],'c_menu')
node('c_menu','altheon',[],[option(91,'ca1'),option(97,'cb1'),option(103,'cc1','av'),option(107,'cd1'),option(114,'ce1'),option(119,'cx1')],effect='intro_c_done')
node('ca1','altheon',[92],[option(93,'ca2'),option('Revenir aux questions.','c_menu')])
node('ca2','altheon',[94],[option(95,'ca3'),option('Revenir aux questions.','c_menu')])
linear('ca3','altheon',[96],'c_menu')
node('cb1','altheon',[98],[option(99,'cb2'),option('Revenir aux questions.','c_menu')],effect='av')
node('cb2','altheon',[100],[option(101,'cb3'),option('Revenir aux questions.','c_menu')])
linear('cb3','altheon',[102],'c_menu')
node('cc1','altheon',[104],[option(105,'cc2'),option('Revenir aux questions.','c_menu')])
linear('cc2','altheon',[106],'c_menu')
linear('cd1','altheon',[108],'cd2')
node('cd2','altheon',[109],[option(110,'cd3'),option(112,'cd4'),option('Revenir aux questions.','c_menu')])
node('cd3','altheon',[111],[option(112,'cd4'),option('Revenir aux questions.','c_menu')])
linear('cd4','altheon',[113],'c_menu')
linear('ce1','altheon',[115],'ce2')
node('ce2','altheon',[116],[option(117,'ce3'),option('Revenir aux questions.','c_menu')])
linear('ce3','altheon',[118],'c_menu')
for i in range(4): linear('cx'+str(i+1),'altheon',[120+i],'cx'+str(i+2) if i<3 else 'dock')

linear('e1','geraud',[125],'e2')
linear('e2','geraud',[126],'e_menu')
node('e_menu','geraud',[],[option(i,'e_'+str(i)) for i in (127,129,131,133,135)]+[option(137,'ex1')])
for i in (127,129,131,133,135):linear('e_'+str(i),'geraud',[i+1],'e_menu')
linear('ex1','geraud',[138],'ex2')
linear('ex2','geraud',[139],'ex3')
node('ex3','geraud',[140],[option(141,'ex4')])
linear('ex4','geraud',[142],'complete')
for i,role in enumerate(('altheon','gautier','hugues','anselme','perrin')):linear('f_'+role,role,[145+i],'close')
linear('f_geraud','geraud',['La passerelle reste libre. Le Port est devant vous ; bonne route à terre.'],'close')

IDS={key:i+1 for i,key in enumerate(N)}
assert len(IDS)<1000
TERMINALS={'close','dock','complete','banter_start'}
for key,value in N.items():
    assert len(value['choices'])<=7
    assert value['resume'] in N
    for choice in value['choices']: assert choice['target'] in N or choice['target'] in TERMINALS,(key,choice)
used=set(x for n in N.values() for x in n['lines'])|set(c['label'] for n in N.values() for c in n['choices'])
expected=[q for i,q in enumerate(Q) if i not in (10,82,124,143,144)]
assert all(q in used for q in expected),[q for q in expected if q not in used]
if __name__=='__main__':
    print(json.dumps({'nodes':len(N),'approved_dialogue_lines':len(expected),'graph':N},ensure_ascii=False,indent=2))
