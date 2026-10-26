"""HF6 : réponses avec questions disponibles et choix mis en attente sans perte."""
import json,re,math
from model import N
def apply(packs):
    core=packs['core'];base='data/capitale/function/pq01/'
    def path(k):return base+k+'.mcfunction'
    menus={'ga_menu','gautier_menu','hugues_menu','anselme_menu','perrin_menu','c_menu','e_menu'}
    for key,node in N.items():
        if key=='c0':continue
        choices=node['choices'];parent=choices[0]['target'] if len(choices)==1 and choices[0]['label']=='Continuer.' else None
        answer=parent in menus and (key.startswith(('ga_','gautier_','hugues_','anselme_','perrin_','e_')) or re.fullmatch(r'c[a-e][0-9]+',key)) and key not in menus
        frame=core[path('frame/'+key)].decode()
        delay=max(60,min(600,math.ceil(sum(len(t) for t in node['lines'])*20/35)+20))
        frame=re.sub(r':\d+:\$\(voice\)',':'+str(delay)+':$(voice)',frame)
        if answer:
            core[path('menu/'+key)]=core[path('menu/'+parent)]
            core[path('choice/'+key)]=core[path('choice/'+parent)]
            frame=frame.replace(':$(b1):',':0:')
        core[path('frame/'+key)]=frame.encode()
        text=core[path('node/'+key)].decode()
        text=text.replace(f'execute if data storage capitale:pq01_config voices.{key}.ticks store result',f'execute unless data storage capitale:pq01_config voices.{key}{{sound:""}} if data storage capitale:pq01_config voices.{key}.sound if data storage capitale:pq01_config voices.{key}.ticks store result')
        core[path('node/'+key)]=text.encode()
    core[path('load')]+=b'\nscoreboard objectives add hcm_pq1_queued dummy\nscoreboard objectives add hcm_pq1_input dummy\n'
    core[path('buttons_self')]=(b'scoreboard players set @s hcm_pq1_queued 0\n'
        b'execute store result score @s hcm_pq1_ttl run data get storage capitale:pq01_config timeout_ticks\n'
        b'execute unless score @s hcm_pq1_ttl matches 200..12000 run scoreboard players set @s hcm_pq1_ttl 3600\n')+core[path('buttons_self')]
    core[path('close_self')]+=b'scoreboard players set @s hcm_pq1_queued 0\nscoreboard players set @s hcm_pq1_input 0\n'
    text=core[path('dispatch_self')].decode()
    text=text.replace('scoreboard players operation @s hcm_pq1_pick = @s QuestChoix','scoreboard players operation @s hcm_pq1_input = @s QuestChoix\nscoreboard players operation @s hcm_pq1_pick = @s QuestChoix')
    text=text.replace('execute if score @s hcm_pq1_pick matches 15','execute unless score @s hcm_pq1_pick matches 1..7 unless score @s hcm_pq1_pick matches 15 run return 0\nexecute if score @s hcm_pq1_pick matches 15',1)
    text=text.replace('run return run function capitale:pq01/voice/wait_self','run return run function capitale:pq01/queue_self')
    core[path('dispatch_self')]=text.encode()
    core[path('queue_self')]=b'scoreboard players operation @s hcm_pq1_queued = @s hcm_pq1_input\nreturn 1\n'
    core[path('flush_self')]=b'scoreboard players operation @s QuestChoix = @s hcm_pq1_queued\nscoreboard players set @s hcm_pq1_queued 0\nfunction capitale:pq01/dispatch_self\n'
    core[path('session/tick_self')]=core[path('session/tick_self')].replace(b'scoreboard players add @s hcm_pq1_poll 1',b'execute if score @s hcm_pq1_queued matches 1.. if score @s hcm_pq1_voice matches 0 unless score @s hcm_pq1_wait matches 1 run function capitale:pq01/flush_self\nscoreboard players add @s hcm_pq1_poll 1')
    meta=json.loads(core['pack.mcmeta']);meta['pack']['description']+=' — HF6 : questions et enchaînements'
    core['pack.mcmeta']=(json.dumps(meta,ensure_ascii=False,indent=2)+'\n').encode()
