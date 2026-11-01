"""Migration des états : les prédicats lisent uniquement les scores canoniques.

Les anciens scores partagés ne sont ni recréés ni entretenus comme curseurs.
Les points d'entrée de PNJ sont préservés pour les entités déjà placées.
"""
from pathlib import Path
from collections import defaultdict, Counter
import csv
import json
import re
import hashlib
import itertools

ROOT = Path(__file__).resolve().parent
NORM = ROOT / 'registries'

def csv_rows(name):
    return list(csv.DictReader((NORM / name).read_text(encoding='utf-8-sig').splitlines()))

ROWS = csv_rows('MATRICE_MIGRATION_66_QUETES.csv')
STATES = {r['old_id']: r for r in csv_rows('ETATS_HISTORIQUES_66_ET_PARTAGES.csv')}
BY_OLD = {r['old_id']: r for r in ROWS}
IDS = {r['old_id']: r['new_id'] for r in ROWS}
GROUPS = defaultdict(list)
for row in ROWS:
    GROUPS[row['old_state']].append(row)

def bounds(value, pattern):
    if '..' in pattern:
        a, b = pattern.split('..')
        return (not a or value >= int(a)) and (not b or value <= int(b))
    return value == int(pattern)

def cond_match(value, condition):
    return ('min' not in condition or value >= condition['min']) and ('max' not in condition or value <= condition['max'])

def exact_predicate(vector):
    return {'condition': 'minecraft:entity_scores', 'entity': 'this',
            'scores': {k: {'min': v, 'max': v} for k, v in sorted(vector.items())}}

class Migration:
    def __init__(self, core, quests):
        self.original = {'capitale': core.copy(), 'hc_quetes': quests.copy()}
        self.packs = {'capitale': core, 'hc_quetes': quests}
        all_text = '\n'.join(b.decode() for pack in self.packs.values() for p, b in pack.items() if p.endswith('.mcfunction'))
        self.models, self.predicates, self.generated, self.aux, self.aux_registry = {}, {}, {}, {}, []
        for old, rows in GROUPS.items():
            domain = sorted({0, *map(int, re.findall(r'scoreboard players set \S+ '+re.escape(old)+r' (-?\d+)\b', all_text))})
            stage = {}
            for row in rows:
                state = STATES[row['old_id']]
                lo, hi = int(state['active_min']), int(state['active_max'])
                complete = int(state['complete_min']) if state['completion_score'] == old else None
                positive = [v for v in domain if v > 0 and (len(rows) == 1 or lo <= v <= hi) and (complete is None or v < complete)]
                values = {v: (10 * (i+1)) for i, v in enumerate(positive)}
                if len(positive) > 1:
                    values[positive[-1]] = 90
                assert len(positive) <= 9, (old, positive)
                stage[row['new_scoreboard']] = values
            vectors = {}
            for value in domain:
                vector = {}
                for row in rows:
                    state = STATES[row['old_id']]
                    q, score = row['short_code'], row['new_scoreboard']
                    complete = int(state['complete_min']) if state['completion_score'] == old else None
                    done = complete is not None and value >= complete
                    if old in ('hcq_c', 'hcq_n', 'hcq_p') and int(state['active_min']) >= 20:
                        done = done and value // 20 == int(state['active_min']) // 20
                    if old == 'hcq_v':
                        if q in ('cqvchr02','sqvchr03'):
                            done = done and value < 40
                    vector[score] = 100 if done else stage[score].get(value, 0)
                    if len(rows) == 1:
                        finished = [v for v in domain if complete is not None and v >= complete]
                        if len(finished) > 1:
                            vector['hcf_'+q+'_end'] = finished.index(value)+1 if value in finished else 0
                vectors[value] = vector
            signatures = [json.dumps(v, sort_keys=True) for v in vectors.values()]
            assert len(signatures) == len(set(signatures)), ('Non invertible', old, vectors)
            self.models[old] = {'rows': rows, 'domain': domain, 'vectors': vectors,
                                'key': rows[0]['short_code'] if len(rows) == 1 else 'arc_'+old.removeprefix('hcq_')}
        self.make_auxiliary_mapping(all_text)
        self.legacy_pattern=re.compile(r'(?<![\w])(?:'+'|'.join(map(re.escape,[*self.models,*self.aux,*IDS]))+r')(?![\w])')

    def make_auxiliary_mapping(self, all_text):
        definitions = set(re.findall(r'scoreboard objectives add (\S+)', all_text))
        api = {'hcq_pnj','hcq_choix','hcq_journal','hcq_clic','hcq_monde'}
        special = {'hcq_tick':'hcm_tick','hcq_v_lut':'hcm_lectern',
                   'hcq_ch_lore':'hch_sqvchr01','hcq_ch_patrouille':'hch_sqvcpa01',
                   'hcq_ch_carrec':'hch_cqvcbb01','hcq_ch_bannieres':'hch_sqvcbn01'}
        self.aux.update({k:v for k,v in special.items() if k in definitions})
        counts = Counter()
        owner_folders = {
            'hector/q1/':'sqvccr01','hector/q5/':'sqvccr02','hector/q9/':'sqvccr03','hector/q11/':'sqvccr05',
            'aurele/q6/':'sqvcep01','aurele/q8/':'cqvcep02','aurele/q12/':'sqvcep03',
            'maelle/q3/':'cqvcra01','maelle/q7/':'sqvcra02','tobie/':'sqvcso01',
            'rosalinde/':'sqvcso02','anselme/':'sqvccr04','malaury/q13/':'sqvchr01',
            'malaury/b/':'cqvchr02','malaury/a/':'sqvchr04','patrouille/':'sqvcpa01',
            'carrec/':'cqvcbb01','bannieres/':'sqvcbn01',
        }
        files = [(p,b.decode()) for pack in self.packs.values() for p,b in pack.items() if p.endswith('.mcfunction')]
        for old in sorted(definitions):
            if old in self.models or old in api or old in self.aux or old.startswith('hcq_mrk_'):
                continue
            if not old.startswith('hcq_'):
                continue
            match = re.fullmatch(r'hcq_ch_j(\d\d)([a-z]?)',old)
            if match:
                self.aux[old]='hch_sqlpjo'+match[1]+('_'+match[2] if match[2] else '')
                continue
            owners=Counter()
            for p,text in files:
                if not re.search(r'\b'+re.escape(old)+r'\b',text):continue
                weight=3 if re.search(r'scoreboard players (?:set|add|remove) \S+ '+re.escape(old)+r'\b',text) else 1
                jm=re.search(r'/(?:j|jungle/j)(\d\d)/',p)
                if jm:owners['sqlpjo'+jm[1]]+=weight
                for folder,code in owner_folders.items():
                    if '/'+folder in p:owners[code]+=weight
            code=owners.most_common(1)[0][0] if owners else 'common'
            counts[code]+=1
            self.aux[old]=f'hcq_{code}_o{counts[code]}' if code!='common' else f'hca_vc_x{counts[code]}'
        for row in ROWS:
            if row['old_completed_state'].startswith('CAP_CD_'):
                old=row['old_completed_state'];new='hcd_'+row['short_code']
                for suffix,dst in [('',new),('_END',new+'_end')]:
                    if old+suffix in definitions:self.aux[old+suffix]=dst
        assert len(set(self.aux.values()))==len(self.aux), 'Auxiliary target collision'
        assert all(len(v)<=16 for v in self.aux.values()), [(k,v) for k,v in self.aux.items() if len(v)>16]

    def predicate(self, old, pattern):
        key=(old,pattern)
        if key in self.predicates:return self.predicates[key]
        model=self.models[old]
        slug=pattern.replace('..','_to_').replace('-','neg')
        name=f'hc_quetes:migration/state/{model["key"]}/{slug}'
        selected={value for value in model['domain'] if bounds(value,pattern)}
        candidates=[]
        objectives=list(next(iter(model['vectors'].values())))
        # Regroupe les intervalles qui n'acceptent que des états sélectionnés.
        for obj in objectives:
            values=sorted({v[obj] for v in model['vectors'].values()})
            for i,lo in enumerate(values):
                for hi in values[i:]:
                    covered={old for old,v in model['vectors'].items() if lo<=v[obj]<=hi}
                    if covered and covered<=selected:candidates.append((covered,{obj:{'min':lo,'max':hi}}))
        for value in selected:
            vector=model['vectors'][value]
            for length in range(2,len(objectives)+1):
                found=False
                for keys in itertools.combinations(objectives,length):
                    covered={v for v,scores in model['vectors'].items() if all(scores[k]==vector[k] for k in keys)}
                    if covered<=selected:
                        candidates.append((covered,{k:{'min':vector[k],'max':vector[k]} for k in keys}));found=True
                if found:break
        terms=[];remaining=set(selected)
        while remaining:
            coverage,scores=max(candidates,key=lambda candidate:(len(candidate[0]&remaining)/len(candidate[1]),len(candidate[0]&remaining)))
            assert coverage&remaining,(old,pattern,remaining)
            terms.append({'condition':'minecraft:entity_scores','entity':'this','scores':scores})
            remaining-=coverage
        predicate=terms[0] if len(terms)==1 else {'condition':'minecraft:any_of','terms':terms}
        self.generated['data/hc_quetes/predicate/'+name.split(':',1)[1]+'.json']=json.dumps(predicate,ensure_ascii=False,indent=2).encode()
        self.predicates[key]=name
        return name

    def write_name(self, old, value):
        return f'hc_quetes:migration/write/{self.models[old]["key"]}/v{value}'

    def replace_selectors(self, text):
        def replace(match):
            parts=match[1].split(',');remaining=[];pred=[]
            for part in parts:
                key,sep,value=part.partition('=')
                if key in self.models:pred.append('predicate='+self.predicate(key,value))
                else:remaining.append(part)
            return ','.join((['scores={'+','.join(remaining)+'}'] if remaining else [])+pred)
        return re.sub(r'scores=\{([^{}]*)\}',replace,text)

    def text(self, text):
        if not self.legacy_pattern.search(text):return text
        text=self.replace_selectors(text)
        for old,model in self.models.items():
            esc=re.escape(old)
            def replace_condition(m):
                if m[2]!='@s':raise ValueError('Score holder requires explicit adaptation: '+m[0])
                return m[1]+' predicate '+self.predicate(old,m[3])
            text=re.sub(r'\b(if|unless) score (\S+) '+esc+r' matches (-?\d+(?:\.\.-?\d*)?|\.\.-?\d+)',replace_condition,text)
            def replace_write(m):
                holder,value=m[1],int(m[2])
                assert value in model['vectors'],(old,value)
                return ('execute as '+holder+' run ' if holder!='@s' else '')+'function '+self.write_name(old,value)
            text=re.sub(r'\bscoreboard players set (\S+) '+esc+r' (-?\d+)\b',replace_write,text)
            def initialize(m):
                return ('execute as '+m[1]+' run ' if m[1]!='@s' else '')+'function hc_quetes:migration/ensure/'+model['key']
            text=re.sub(r'\bscoreboard players add (\S+) '+esc+r' 0\b',initialize,text)
            text=re.sub(r'\bscoreboard players reset (\S+) '+esc+r'\b',lambda m:('execute as '+m[1]+' run ' if m[1]!='@s' else '')+'function '+self.write_name(old,0),text)
            text=re.sub(r'^scoreboard objectives add '+esc+r' dummy.*$', '# Création centralisée : hc_quetes:migration/load',text,flags=re.M)
            text=re.sub(r'^scoreboard objectives remove '+esc+r'\s*$', '\n'.join('scoreboard objectives remove '+obj for obj in next(iter(model['vectors'].values()))),text,flags=re.M)
            text=re.sub(r'\bscoreboard objectives modify '+esc+r'\b','scoreboard objectives modify '+model['rows'][0]['new_scoreboard'],text)
            text=re.sub(r'\bscoreboard players get (\S+) '+esc+r'\b',lambda m:'scoreboard players get '+m[1]+' '+model['rows'][0]['new_scoreboard'],text)
            # Les composants score imprimés sont des vues, jamais des sources d'état.
            text=re.sub(r'("objective"\s*:\s*")'+esc+r'(")',r'\g<1>'+model['rows'][0]['new_scoreboard']+r'\2',text)
            text=re.sub(r'hcq(vis|hide)\.'+esc+r'\.(-?\d+)(?:\.(-?\d+))?',lambda m:'hcqv2'+m[1]+'.'+model['key']+'.'+m[2]+'.'+(m[3] or '2147483647'),text)
            text=text.replace(old+'=',model['rows'][0]['new_scoreboard']+'=').replace(old+' :',model['rows'][0]['new_scoreboard']+' :')
        for old,new in sorted(self.aux.items(),key=lambda kv:-len(kv[0])):
            text=re.sub(r'(?<![\w])'+re.escape(old)+r'(?![\w])',new,text)
        for old,new in IDS.items():text=text.replace(old,new)
        return text

    def score_condition(self, condition, row):
        if not condition or not isinstance(condition,dict):return condition
        old=condition.get('objective')
        if old in self.aux:return {**condition,'objective':self.aux[old]}
        if old not in self.models:return condition
        model=self.models[old]; own=row['new_scoreboard'] if row['old_state']==old else None
        if own is None:
            target=int(condition.get('min',condition.get('max',0)))
            candidates=[]
            for rr in model['rows']:
                ss=STATES[rr['old_id']]
                if int(ss['active_min'])<=target:candidates.append(rr)
            candidates.sort(key=lambda rr:int(STATES[rr['old_id']]['active_min']))
            own=(candidates[-1] if candidates else model['rows'][0])['new_scoreboard']
        matching={vector[own] for v,vector in model['vectors'].items() if cond_match(v,condition)}
        if len(model['rows'])>1 and matching!={0}:matching.discard(0)
        if not matching:return {'objective':own,'min':2147483647}
        return {'objective':own,'min':min(matching),'max':max(matching)}

    def migrate_card(self, card, row):
        card=json.loads(json.dumps(card))
        card['id']=row['new_id'];card['type']=row['new_type']
        card['prerequisites']=row['new_prerequisites'].split(';') if row['new_prerequisites'] else []
        if row['new_next']:card['next_quest']=row['new_next']
        else:card.pop('next_quest',None)
        def walk(obj):
            if isinstance(obj,dict):
                if 'objective' in obj and ('min' in obj or 'max' in obj):return self.score_condition(obj,row)
                return {key:walk(v) for key,v in obj.items()}
            if isinstance(obj,list):return [walk(v) for v in obj]
            if isinstance(obj,str):return self.text(obj)
            return obj
        return walk(card)

    def run(self):
        load=[]; initialize=[]
        for old,model in self.models.items():
            objectives=list(next(iter(model['vectors'].values())).keys())
            load += ['scoreboard objectives add '+obj+' dummy' for obj in objectives]
            ensure='\n'.join('scoreboard players add @s '+obj+' 0' for obj in objectives)+'\n'
            self.generated['data/hc_quetes/function/migration/ensure/'+model['key']+'.mcfunction']=ensure.encode()
            initialize.append('function hc_quetes:migration/ensure/'+model['key'])
            for value,vector in model['vectors'].items():
                lines=['# Autorité unique : états dédiés ; aucun ancien score écrit.']
                lines+=['scoreboard players set @s '+obj+' '+str(n) for obj,n in vector.items()]
                lines+=['return 1']
                name=self.write_name(old,value).split(':',1)[1]
                self.generated['data/hc_quetes/function/'+name+'.mcfunction']=('\n'.join(lines)+'\n').encode()
        for namespace,pack in self.packs.items():
            for path,data in list(pack.items()):
                if path.startswith('data/hc_quetes/hcq_quests/') and path.endswith('.json'):
                    card=json.loads(data);row=BY_OLD[card['id']]
                    del pack[path]
                    pack['data/hc_quetes/hcq_quests/'+row['new_id']+'.json']=json.dumps(self.migrate_card(card,row),ensure_ascii=False,indent=2).encode()
                elif path.endswith(('.mcfunction','.json')):
                    pack[path]=self.text(data.decode()).encode()
        # Tous les scores canoniques sont créés avant les routeurs.
        self.generated['data/hc_quetes/function/migration/load.mcfunction']=('\n'.join(sorted(set(load)))+'\n').encode()
        self.generated['data/hc_quetes/function/migration/load.mcfunction']+=b'scoreboard objectives add hca_v2_schema dummy\n'
        upgrade=['# Conversion unique des profils historiques ; ensuite seuls les scores canoniques sont lus.',
                 'execute if score @s hca_v2_schema matches 2 run return 1']
        for old,model in self.models.items():
            upgrade+=['execute if score @s '+old+' matches '+str(value)+' run function '+self.write_name(old,value)
                      for value in model['domain']]
        upgrade+=['scoreboard players set @s hca_v2_schema 2','return 1']
        self.generated['data/hc_quetes/function/migration/upgrade_self.mcfunction']=('\n'.join(upgrade)+'\n').encode()
        self.generated['data/hc_quetes/function/migration/initialize_self.mcfunction']=('\n'.join(initialize)+'\ntag @s add hcq_v2_ready\n').encode()
        self.packs['hc_quetes'].update(self.generated)
        qload='data/hc_quetes/function/load.mcfunction'
        self.packs['hc_quetes'][qload]=b'function hc_quetes:migration/load\nexecute as @a run function hc_quetes:migration/upgrade_self\n'+self.packs['hc_quetes'][qload]+b'\nexecute as @a run function hc_quetes:migration/initialize_self\n'
        coreload='data/capitale/function/core/load.mcfunction'
        if coreload in self.packs['capitale']:
            self.packs['capitale'][coreload]=b'function hc_quetes:migration/load\nexecute as @a run function hc_quetes:migration/upgrade_self\n'+self.packs['capitale'][coreload]
        profile='data/capitale/function/profiles/after_load_self.mcfunction'
        self.packs['capitale'][profile]=b'function hc_quetes:migration/upgrade_self\n'+self.packs['capitale'][profile]
        tick='data/hc_quetes/function/tick.mcfunction'
        self.packs['hc_quetes'][tick]=b'execute as @a[tag=!hcq_v2_ready] run function hc_quetes:migration/initialize_self\n'+self.packs['hc_quetes'][tick]
        leftovers=[]
        for namespace,pack in self.packs.items():
            for path,data in pack.items():
                if path.endswith('.mcfunction'):
                    if path.endswith('/migration/upgrade_self.mcfunction'):continue
                    for n,line in enumerate(data.decode().splitlines(),1):
                        if line.lstrip().startswith('#'):continue
                        for old in self.models:
                            if re.search(r'(?<![\w])'+re.escape(old)+r'(?![\w])',line):leftovers.append((path,n,old,line))
        if leftovers:raise ValueError(json.dumps(leftovers[:20],ensure_ascii=False,indent=2))
        return self.packs, {'models':self.models,'auxiliary_mapping':self.aux,
                           'predicates':len(self.predicates),'legacy_main_score_references_remaining':leftovers}
