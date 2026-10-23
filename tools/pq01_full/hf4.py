"""Corrections immersives HF4, appliquées uniquement à PQ01 et à son conflit Anselme."""
import json
from model import N, IDS, ROLES

def apply(packs):
    core=packs['core']; quests=packs['quests']
    def path(name):return 'data/capitale/function/pq01/'+name+'.mcfunction'
    def put(name,lines):core[path(name)]=('# PQ01 R3-HF4\n'+'\n'.join(lines)+'\n').encode()
    for key,node in N.items():
        if key=='c0':
            put('node/c0',['return run function capitale:pq01/node/c1'])
            continue
        old=core[path('node/'+key)].decode().splitlines()
        speech=[line for line in old if line.startswith('tellraw @s ')]
        voice=[line for line in old if 'run function capitale:pq01/voice/play_macro' in line]
        body=[line for line in old if line and not line.startswith('#') and line not in speech+voice and not line.startswith('function capitale:pq01/buttons_self') and not line.startswith('function capitale:pq01/menu/')]
        automatic=len(node['choices'])==1 and node['choices'][0]['label']=='Continuer.'
        delay=max(60,min(1200,sum(len(text) for text in node['lines'])+40))
        frame=f'indivis:pq01:v1:{key}:{node["role"]}:'+('$(b1)' if automatic else '0')+f':{delay}:$(voice)'
        put('frame/'+key,['$tellraw @s '+json.dumps({'text':'','insertion':frame},ensure_ascii=False,separators=(',',':'))])
        gate=['execute unless score @s hcm_pq1_active matches 1 run return 0',
              f'execute store result score @s hcm_pq1_camera run indivispq01 line_{key}',
              'execute unless score @s hcm_pq1_camera matches 1 run return 0']
        put('node/'+key,gate+body+['function capitale:pq01/buttons_self',
            'scoreboard players set @s hcm_pq1_temp 0',
            f'execute if data storage capitale:pq01_config voices.{key}.ticks store result score @s hcm_pq1_temp run data get storage capitale:pq01_config voices.{key}.ticks',
            'execute if score @s hcm_pq1_temp matches ..-1 run scoreboard players set @s hcm_pq1_temp 0',
            'execute if score @s hcm_pq1_temp matches 12001.. run scoreboard players set @s hcm_pq1_temp 12000',
            'execute store result storage capitale:pq01_runtime buttons.voice int 1 run scoreboard players get @s hcm_pq1_temp',
            f'function capitale:pq01/frame/{key} with storage capitale:pq01_runtime buttons']+speech+voice+
            [f'function capitale:pq01/menu/{key} with storage capitale:pq01_runtime buttons'])
        menu=[]
        for index,choice in enumerate(node['choices'],1):
            if automatic:continue
            label=choice['label'];color='yellow'
            if key=='c_menu' and choice['target']=='cx1':
                label+=" (Terminer l’entretien et lancer l’amarrage.)";color='green'
            button='$tellraw @s '+json.dumps({'text':'['+label+']','color':color,'click_event':{'action':'run_command','command':f'/trigger QuestChoix set $(b{index})'}},ensure_ascii=False,separators=(',',':'))
            if choice.get('condition')=='av':button='$execute if score @s hcf_pq1_av matches 1 run '+button[1:]
            menu.append(button)
        menu.append('$tellraw @s '+json.dumps({'text':'[Fermer la conversation]','color':'gray','click_event':{'action':'run_command','command':'/trigger QuestChoix set $(b15)'}},ensure_ascii=False,separators=(',',':')))
        put('menu/'+key,menu)
    for name in ['open/altheon_self']:
        core[path(name)]=core[path(name)].replace(b'function capitale:pq01/node/c0',b'function capitale:pq01/node/c1')
    for name in ['near_self','near_intro_self']:
        core['data/capitale/function/quest/spawn_dirigeable_couronne/capitaine_large/'+name+'.mcfunction']=b'# PQ01 HF4 : conversation volontaire au clic.\nreturn 0\n'
    put('near_altheon_self',['return 0'])
    put('scene/wait_self',['return 0'])
    put('voice/wait_self',['return 0'])
    # Le matelot Anselme ne doit pas déclencher l'archiviste Anselme Kerrec.
    key='data/hc_quetes/advancement/interact/pnj_anselme.json'
    advancement=json.loads(quests[key])
    advancement['criteria']['clic']['conditions']['entity'].append({
        'condition':'minecraft:inverted','term':{'condition':'minecraft:entity_properties','entity':'this','predicate':{'nbt':"{Tags:['hcm_pq1_anselme']}"}}})
    quests[key]=(json.dumps(advancement,ensure_ascii=False,indent=2)+'\n').encode()
    for name,pack in packs.items():
        if name=='journal':continue
        meta=json.loads(pack['pack.mcmeta']);meta['pack']['description']+=' — HF4 : dialogues et scènes PQ01'
        pack['pack.mcmeta']=(json.dumps(meta,ensure_ascii=False,indent=2)+'\n').encode()
