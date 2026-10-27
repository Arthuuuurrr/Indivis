"""PQ02 : graphe éditorial du script v1.0, citations lues dans la source fournie."""
from pathlib import Path
import re,json,hashlib
HERE=Path(__file__).resolve().parent
SCRIPT=HERE/'kit/Indivis_PQ02_Script_Consolide_v1_0.md'
LINES=SCRIPT.read_text().splitlines();USED=set();N={}
def q(line,index=0):
    text=re.findall('«([^»]+)»',LINES[line-1])[index].strip();USED.add((line,index));return text
def c(line,target,index=0,effect='',condition=''):
    return {'text':q(line,index),'target':target,'effect':effect,'condition':condition,'source_line':line}
def raw(text,target,effect='',condition=''):
    return {'text':text,'target':target,'effect':effect,'condition':condition}
def node(key,refs=(),choices=(),next='',speaker='leovic',camera='',editorial='',condition=''):
    texts=[q(r[0],r[1]) if isinstance(r,tuple) else q(r) for r in refs]
    N[key]={'id':len(N)+1,'key':key,'speaker':speaker,'lines':texts,'source_lines':[r[0] if isinstance(r,tuple) else r for r in refs],'choices':list(choices),'next':next,'camera':camera or ('three' if key.startswith('m01') or key.startswith('m05c') else speaker),'editorial':editorial or 'P02-'+key.upper().replace('_','-'),'condition':condition}
A=[c(80,'a04'),c(81,'a05'),c(82,'a06'),c(83,'a07',effect='subject_guard'),c(84,'a08')]
T=[c(141,'t02'),c(142,'t03',condition='port'),c(143,'t04'),c(144,'t05',condition='sentinel'),c(145,'t06',condition='anecdote'),c(146,'t_resume')]
M=[c(383,'m05a'),c(384,'m05b',effect='status_known'),c(385,'m05c'),c(386,'m05d'),c(387,'m06')]
F=[c(465,'f02'),c(466,'f03'),c(467,'f04'),c(468,'f05'),c(469,'f06')]
node('a01',[54],[c(58,'a02a',effect='arrival:1'),c(59,'a02b',effect='arrival:2'),c(60,'a02c',effect='arrival:3')])
for k,line in [('a02a',62),('a02b',64),('a02c',66)]:node(k,[line,72,74,76],A)
node('a_menu',[],A)
node('a04',[86],[c(90,'a04a'),c(91,'a04b'),c(92,'a_menu')])
for k,line in [('a04a',94),('a04b',96)]:node(k,[line],[raw('Revenons aux formalités.','a04'),c(92,'a_menu')])
node('a05',[98],[c(100,'a05b'),c(100,'a_menu',1)])
node('a05b',[(102,1)],A)
node('a06',[104],[c(106,'a06b'),c(106,'a_menu',1)])
node('a06b',[108],A)
node('a07',[110],A)
node('a08',[114],[c(118,'a09'),c(119,'a08a')])
node('a08a',[121],next='event:rest')
node('a09',[123],next='event:start_walk')
node('t_menu',[],T)
node('t01',[133],editorial='P02-T01')
node('t02',[150],[c(154,'t02a',effect='anecdote'),c(155,'t02b'),c(156,'t03'),c(157,'t_menu')])
node('t02a',[159],[c(161,'t02a_stay'),c(161,'t02a_leave',1),c(161,'t_menu',2)])
for k,line in [('t02a_stay',163),('t02a_leave',165),('t02b',169)]:node(k,[line],[raw('Parlez-moi encore de votre expérience.','t02'),c(157,'t_menu')])
node('t03',[173],[c(177,'t03a'),c(178,'t03b'),c(179,'t02b'),c(180,'t_menu')])
for k,line in [('t03a',182),('t03b',184)]:node(k,[line],[raw('Revenons au Port.','t03'),c(180,'t_menu')])
node('t04',[188],[c(192,'t04a'),c(193,'t04b'),c(194,'t04c'),c(195,'t04d'),c(196,'t_menu')])
node('t04a',[198],[c(200,'t04a_reputation'),c(200,'t04a_cooperation',1),c(200,'t04',2)])
for k,line in [('t04a_reputation',202),('t04a_cooperation',204)]:node(k,[line],[c(200,'t04',2),c(196,'t_menu')])
node('t04b',[208],[c(212,'t04b_a',effect='guard:1'),c(213,'t04b_b',effect='guard:2'),c(214,'t04b_c',effect='guard:3')])
for k,line in [('t04b_a',216),('t04b_b',218),('t04b_c',220)]:node(k,[line],[c(200,'t04',2),c(196,'t_menu')])
node('t04c',[224],[c(226,'t04c_field'),c(226,'t04c_mission',1),c(226,'t_menu',2)])
for k,line in [('t04c_field',228),('t04c_mission',230),('t04d',234)]:node(k,[line],[c(200,'t04',2),c(196,'t_menu')])
node('t_sentinel_observed',[240],editorial='P02-T05-OBSERVATION')
node('t05',[244],[c(248,'t05a'),c(249,'t05b'),c(250,'t05c'),c(251,'t_menu')],condition='sentinel')
for k,line in [('t05a',253),('t05b',255),('t05c',257)]:node(k,[line],[raw('Revenons aux Sentinelles.','t05'),c(251,'t_menu')],condition='sentinel')
node('t06',[261],[c(265,'t06a'),c(266,'t06b'),c(267,'t06c')],condition='anecdote')
for k,line in [('t06a',269),('t06b',271),('t06c',273)]:node(k,[line,275],T,condition='anecdote')
node('t_resume',[281],next='event:resume_walk')
node('t07',[289,291],[c(295,'t07a'),c(296,'t07b'),c(297,'t07c')])
node('t07a',[299,303],next='m01a')
node('t07b',[301,303],next='m01a')
node('t07c',[303],next='m01a')
node('m01a',[315],next='m01b',speaker='magistrat')
node('m01b',[317],next='m01c')
node('m01c',[319,321],next='m01d',speaker='magistrat')
node('m01d',[323],next='m02')
node('m02',[325,329],[c(333,'m02a'),c(334,'m02b'),c(335,'m02c')],speaker='magistrat')
for k,line in [('m02a',337),('m02b',339),('m02c',341)]:node(k,[line,347],[c(351,'m03a'),c(352,'m03a'),c(353,'m03b')],speaker='magistrat')
for k,line in [('m03a',355),('m03b',357)]:node(k,[line,361],[c(365,'m04a',effect='magistrate:1'),c(366,'m04b',effect='magistrate:2'),c(367,'m04c',effect='magistrate:3')],speaker='magistrat')
node('m04a',[(369,0)],next='m04a_leovic',speaker='magistrat')
node('m04a_leovic',[(369,1)],next='m_menu')
node('m04b',[371,379],M,speaker='magistrat')
node('m04c',[(373,0)],next='m04c_leovic',speaker='magistrat')
node('m04c_leovic',[(373,1)],next='m_menu')
node('m_menu',[379],M,speaker='magistrat')
node('m_questions',[],M,speaker='magistrat')
node('m05a',[389],[c(391,'m05a_unknown'),c(391,'m_questions',1)],speaker='magistrat')
node('m05a_unknown',[393],M,speaker='magistrat')
node('m05b',[395],[c(397,'m05b_resident'),c(397,'m_questions',1)],speaker='magistrat')
node('m05b_resident',[399],M,speaker='magistrat')
node('m05c',[401],next='m05c_leovic',speaker='magistrat')
node('m05c_leovic',[403],next='m05c_magistrat')
node('m05c_magistrat',[405],M,speaker='magistrat')
node('m05d',[407],M,speaker='magistrat')
node('m06',[411],next='event:register',speaker='magistrat')
node('m06_registered',[417],[c(421,'m06a'),c(422,'m06b'),c(423,'m06c')],speaker='magistrat')
for k,line in [('m06a',425),('m06b',427),('m06c',429)]:node(k,[line],next='m07',speaker='magistrat')
node('m07',[435],[c(439,'m07a'),c(440,'m07b'),c(441,'m07c')])
for k,line in [('m07a',443),('m07b',445),('m07c',447)]:node(k,[line,449],next='event:elevator')
node('f_menu',[461],F)
node('f_questions',[],F)
node('f02',[471],F)
node('f03',[473],[c(475,'f03_prefer'),c(475,'event:practical_menu',1)])
node('f03_prefer',[477],next='event:practical_menu')
node('f04',[479],[c(481,'f04_return'),c(481,'event:practical_menu',1)])
node('f04_return',[483],next='event:practical_menu')
node('f05',[485],next='event:practical_menu')
node('f05_short',[(487,0)],next='event:practical_menu')
node('f06',[491,495,497],[c(501,'f06a',effect='gift:1'),c(502,'f06b',effect='gift:2'),c(503,'f06c',effect='gift:3'),c(504,'f06d',effect='gift:4')])
node('f06a',[506],[c(508,'event:gift'),c(508,'f_report',1)])
node('f06b',[510],[c(512,'event:gift'),c(512,'f_report',1)])
node('f06c',[514],next='event:gift')
node('f06d',[516,518],next='f_report')
node('f_report',[522],next='f07')
node('f07',[528,530],[c(534,'f07a'),c(535,'f07b'),c(536,'f07c')])
for k,line in [('f07a',538),('f07b',540),('f07c',542)]:node(k,[line],next='event:complete')
POST=[c(466,'f03'),c(467,'f04'),c(468,'f05'),c(567,'post_gift',condition='gift_pending')]
for k,line in [('post_very_friendly',556),('post_friendly',557),('post_neutral',558),('post_unfriendly',559)]:node(k,[line],POST,editorial='P02-POST-ACCUEIL')
node('post_menu',[],POST,editorial='P02-POST-MENU')
node('post_gift',[569],[c(573,'event:late_gift'),c(574,'post_menu')],editorial='P02-POST-DON',condition='gift_pending')
EVENTS={'rest','start_walk','resume_walk','register','elevator','practical_menu','gift','complete','late_gift'}
for key,n in N.items():
    targets=[ch['target'] for ch in n['choices']]+([n['next']] if n['next'] else [])
    for target in targets:assert target in N or target.startswith('event:') and target[6:] in EVENTS,(key,target)
assert len({n['id'] for n in N.values()})==len(N)
# Les seules formulations ajoutées sont des retours de navigation, sans réplique PNJ ajoutée.
NAVIGATION_ADDITIONS=sorted({c['text'] for n in N.values() for c in n['choices'] if 'source_line' not in c})
def export():return {'schema':1,'script_sha256':hashlib.sha256(SCRIPT.read_bytes()).hexdigest(),'nodes':list(N.values()),'events':sorted(EVENTS),'navigation_additions':NAVIGATION_ADDITIONS,'source_quotes_used':[list(p) for p in sorted(USED)]}
if __name__=='__main__':
    data=export();(HERE/'reports/narrative_graph.json').write_text(json.dumps(data,ensure_ascii=False,indent=2)+'\n');print('PQ02 graph:',len(N),'nodes;',len(USED),'script quotes;',len(NAVIGATION_ADDITIONS),'navigation labels')
