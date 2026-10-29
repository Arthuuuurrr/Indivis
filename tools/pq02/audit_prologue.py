"""Audit des ressources livrées, sans modification du core ni des mods."""
from pathlib import Path
import json, re, hashlib, collections
import build

H=Path(__file__).resolve().parent
R=H/'reports'; A=H/'audit_prologue'; A.mkdir(exist_ok=True)

def text(value):
    if isinstance(value,str): return value
    if isinstance(value,list): return ''.join(map(text,value))
    return value.get('text','')+''.join(map(text,value.get('extra',[])))

def main():
    packs={k:build.read(H/'output'/build.FILES[k]) for k in ['core','quests']}
    functions={}
    for files in packs.values():
        for n,b in files.items():
            m=re.fullmatch(r'data/([^/]+)/function/(.+)\.mcfunction',n)
            if m:functions[m[1]+':'+m[2]]=b.decode()
    roots=['capitale:pq01/','capitale:pq02/','capitale:quest/spawn_dirigeable_couronne/',
           'capitale:quest/le_registre_du_port/','capitale:quest/sous_le_regard_du_coeur/',
           'capitale:quest/au_seuil_des_profondeurs/','capitale:quest/une_adresse_dans_les_profondeurs/',
           'capitale:quest/prologue/']
    selected={n:s for n,s in functions.items() if any(n.startswith(p) for p in roots)}
    missing=[]
    for n,s in selected.items():
        executable='\n'.join(line for line in s.splitlines() if line and not line.startswith('#') and not line.startswith('tellraw '))
        for ns,p in re.findall(r'\bfunction (capitale|hc_quetes):([a-z0-9_/.-]+)',executable):
            if ns+':'+p not in functions:missing.append({'caller':n,'target':ns+':'+p})
    checks=[]
    def check(ok,label):
        checks.append({'ok':bool(ok),'label':label})
    check(not missing,'Toutes les références littérales internes du prologue existent')
    nodes=[]
    for name in sorted(n for n in functions if n.startswith('capitale:pq01/node/')):
        key=name.rsplit('/',1)[-1]; src=functions[name]
        m=re.search(r'scoreboard players set @s hcm_pq1_node (\d+)',src)
        if m is None:continue
        frame=functions.get('capitale:pq01/frame/'+key,'')
        fm=re.search(r'indivis:pq01:v1:([^:]+):([^:]+):([^:]+):([^:]+):([^"}]+)',frame)
        check(fm is not None,'Trame de lecture '+key)
        if not fm:continue
        menu=functions['capitale:pq01/menu/'+key]
        data={'key':key,'id':int(m[1]),'role':fm[2], 'frame':':'.join(['indivis','pq01','v1',key,fm[2],'1000000001' if '$(b1)'==fm[3] else fm[3],fm[4],'0']),
              'lines':[],'choices':[], 'edges':[]}
        for line in src.splitlines():
            if line.startswith('tellraw @s '):data['lines'].append(text(json.loads(line[11:])))
        for line in menu.splitlines():
            if 'tellraw @s ' not in line:continue
            raw=line.split('tellraw @s ',1)[1]
            for i in [1,2,3,4,5,6,7,15]:raw=raw.replace('$(b'+str(i)+')',str(1000000000+i))
            data['choices'].append(json.loads(raw))
        choice=functions.get('capitale:pq01/choice/'+key,'')
        for pick,target in re.findall(r'hcm_pq1_pick matches (\d+)[^\n]*?function capitale:pq01/node/([a-z0-9_]+)',choice):
            data['edges'].append({'pick':int(pick),'target':target})
        check(bool(data['lines']) or key in {'c_menu','e_menu'},'Réplique ou menu de questions explicite '+key)
        check(not any(x.strip() in ['...','…'] for x in data['lines']),'Aucun bloc de suspension seul '+key)
        check(any('b15' in l for l in menu.splitlines()),'Fermeture explicite '+key)
        nodes.append(data)
    check(len(nodes)==94,'94 blocs PQ01 analysés et un alias c0')
    ids={n['id'] for n in nodes};check(ids==set(range(1,96))-{51},'Identifiants PQ01 uniques et complets hors alias c0')
    check('return run function capitale:pq01/node/c1' in functions['capitale:pq01/node/c0'],'c0 redirige immédiatement vers c1')
    keys={n['key'] for n in nodes}
    check(all(e['target'] in keys for n in nodes for e in n['edges']),'Tous les choix directs PQ01 visent un nœud existant')
    for role,first,total in [('geraud','ga_12',5),('gautier','gautier_23',6),('hugues','hugues_36',5),('anselme','anselme_49',6),('perrin','perrin_62',6)]:
        n=next(n for n in nodes if n['key']==first)
        check(len(n['edges'])>=total,'Questions encore accessibles après une réponse : '+role)
    captain=functions['capitale:pq01/menu/c_menu']
    check('"color":"green"' in captain and 'amarrage' in captain,'Choix de clôture du capitaine vert et explicite')
    check('Écouter leur échange' not in '\n'.join(s for n,s in selected.items() if n.startswith('capitale:pq01/menu/hugues')),'Choix 6 de Hugues absent des menus')
    all_original={}
    for k,f in packs.items():
        all_original[k]=hashlib.sha256((H/'output'/build.FILES[k]).read_bytes()).hexdigest()
    report={'status':'PASS' if all(c['ok'] for c in checks) else 'FAIL','checks':len(checks),'resources':len(selected),'pq01_nodes':len(nodes),'pq01_direct_edges':sum(len(n['edges']) for n in nodes),'quest_scope':['PQ01','PQ02','PQ03','PQ04','PQ05'],'missing_internal_functions':missing,'sha256':all_original,'evidence':checks,'limits':['Literal internal references only; runtime macros and pathfinding need native checks','PQ03–PQ05 stories unchanged; no real map or public NPC export available']}
    (A/'pq01_protocol_fixture.json').write_text(json.dumps(nodes,ensure_ascii=False,indent=2)+'\n')
    (R/'prologue_audit_static.json').write_text(json.dumps(report,ensure_ascii=False,indent=2)+'\n')
    print(json.dumps({k:v for k,v in report.items() if k!='evidence'},ensure_ascii=False,indent=2))
    print('Failed checks:',[c['label'] for c in checks if not c['ok']])
    assert report['status']=='PASS'

if __name__=='__main__':main()
