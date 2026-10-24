"""HF9 : blocs par interlocuteur et file de voix gardant les 92 prises existantes."""
import json,re
from model import N,IDS
MENUS={'ga_menu','gautier_menu','hugues_menu','anselme_menu','perrin_menu','c_menu','e_menu'}
def automatic(key):
 c=N[key]['choices'];return len(c)==1 and c[0]['label']=='Continuer.'
def answer(key):
 return automatic(key) and N[key]['choices'][0]['target'] in MENUS and (key.startswith(('ga_','gautier_','hugues_','anselme_','perrin_','e_')) or re.fullmatch(r'c[a-e][0-9]+',key))
def chain(key):
 result=[key]
 while automatic(result[-1]) and not answer(result[-1]):
  target=N[result[-1]]['choices'][0]['target']
  if target not in N or N[target]['role']!=N[key]['role'] or target in result:break
  result.append(target)
 return result
def effective(key):
 tail=chain(key)[-1]
 return N[N[tail]['choices'][0]['target']]['choices'] if answer(tail) else N[tail]['choices']
def manual(key):return automatic(chain(key)[-1]) and not answer(chain(key)[-1])
def apply(packs):
 core=packs['core'];base='data/capitale/function/pq01/'
 def path(k):return base+k+'.mcfunction'
 before=dict(core)
 def put(k,lines):core[path(k)]=('# PQ01 R3-HF9\n'+'\n'.join(lines)+'\n').encode()
 core[path('load')]+=b'\nscoreboard objectives add hcm_pq1_vqueue dummy\nscoreboard objectives add hcm_pq1_vplaying dummy\n'
 stop=['scoreboard players set @s hcm_pq1_vqueue 0']
 for key in N:
  if not N[key]['lines']:continue
  prefix=f'execute unless data storage capitale:pq01_config voices.{key}{{sound:""}} if data storage capitale:pq01_config voices.{key}.sound '
  stop.append(prefix+f'if score @s hcm_pq1_vplaying matches {IDS[key]} run function capitale:pq01/voice/stop_macro with storage capitale:pq01_config voices.{key}')
 stop+=['scoreboard players set @s hcm_pq1_vplaying 0','scoreboard players set @s hcm_pq1_voice 0']
 put('voice/stop_self',stop)
 dispatch=[]
 for key,node in N.items():
  if key=='c0':continue
  group=chain(key);tail=group[-1];original=before[path('node/'+key)].decode().splitlines()
  body=['execute unless score @s hcm_pq1_active matches 1 run return 0',f'execute store result score @s hcm_pq1_camera run indivispq01 line_{key}','execute unless score @s hcm_pq1_camera matches 1 run return 0','function capitale:pq01/voice/stop_self',f'scoreboard players set @s hcm_pq1_node {IDS[key]}',f'scoreboard players set @s hch_pq1node {IDS[node["resume"]]}']
  for part in group:
   for line in before[path('node/'+part)].decode().splitlines():
    if line.startswith('execute unless score @s hcf_') and 'run function capitale:pq01/' in line:body.append(line)
    elif re.match(r'scoreboard players set @s hcf_',line):body.append(line)
  body+=['function capitale:pq01/buttons_self','scoreboard players set @s hcm_pq1_temp 0']
  for part in group:
   body += [f'scoreboard players set @s hcm_pq1_vplaying 0',f'execute unless data storage capitale:pq01_config voices.{part}{{sound:""}} if data storage capitale:pq01_config voices.{part}.sound if data storage capitale:pq01_config voices.{part}.ticks store result score @s hcm_pq1_vplaying run data get storage capitale:pq01_config voices.{part}.ticks','execute if score @s hcm_pq1_vplaying matches ..-1 run scoreboard players set @s hcm_pq1_vplaying 0','execute if score @s hcm_pq1_vplaying matches 12001.. run scoreboard players set @s hcm_pq1_vplaying 12000','scoreboard players operation @s hcm_pq1_temp += @s hcm_pq1_vplaying']
  body+=['scoreboard players set @s hcm_pq1_vplaying 0','execute if score @s hcm_pq1_temp matches 12001.. run scoreboard players set @s hcm_pq1_temp 12000','execute store result storage capitale:pq01_runtime buttons.voice int 1 run scoreboard players get @s hcm_pq1_temp',f'function capitale:pq01/frame/{key} with storage capitale:pq01_runtime buttons']
  for part in group:body += [l for l in before[path('node/'+part)].decode().splitlines() if l.startswith('tellraw @s ')]
  body += [f'function capitale:pq01/menu/{key} with storage capitale:pq01_runtime buttons','scoreboard players set @s hcm_pq1_vqueue 1',f'function capitale:pq01/voice/block/{key}_1']
  if key=='a2':body.append('indivispq01 approach_early')
  put('node/'+key,body)
  # Le bloc finit exactement sur les choix du dernier paragraphe.
  core[path('menu/'+key)]=before[path('menu/'+tail)]
  choices=before[path('choice/'+tail)]
  if manual(key) and N[tail]['choices'][0]['target']=='dock':choices=b'execute if score @s hcm_pq1_pick matches 1 run scoreboard players set @s hcm_pq1_node '+str(IDS[tail]).encode()+b'\n'+choices
  core[path('choice/'+key)]=choices
  frame={'text':'','insertion':f'indivis:pq01:v1:{key}:{node["role"]}:'+('$(b1)' if manual(key) else '0')+':0:$(voice)'}
  put('frame/'+key,['$tellraw @s '+json.dumps(frame,ensure_ascii=False,separators=(',',':'))])
  for index,part in enumerate(group,1):
   next_index=index+1
   step=[f'scoreboard players set @s hcm_pq1_vqueue {next_index}',f'execute unless data storage capitale:pq01_config voices.{part}{{sound:""}} if data storage capitale:pq01_config voices.{part}.sound run return run function capitale:pq01/voice/block/play_{part}']
   if index<len(group):step.append(f'return run function capitale:pq01/voice/block/{key}_{next_index}')
   else:step+=['scoreboard players set @s hcm_pq1_vqueue 0','scoreboard players set @s hcm_pq1_vplaying 0']
   put(f'voice/block/{key}_{index}',step)
   dispatch.append(f'execute if score @s hcm_pq1_node matches {IDS[key]} if score @s hcm_pq1_vqueue matches {index} run return run function capitale:pq01/voice/block/{key}_{index}')
  put(f'voice/block/{key}_{len(group)+1}',['scoreboard players set @s hcm_pq1_vqueue 0','scoreboard players set @s hcm_pq1_vplaying 0'])
  dispatch.append(f'execute if score @s hcm_pq1_node matches {IDS[key]} if score @s hcm_pq1_vqueue matches {len(group)+1} run return run function capitale:pq01/voice/block/{key}_{len(group)+1}')
 for key,node in N.items():
  if key=='c0':continue
  put('voice/block/play_'+key,[f'scoreboard players set @s hcm_pq1_vplaying {IDS[key]}',f'function capitale:pq01/voice/play_macro with storage capitale:pq01_config voices.{key}','execute if score @s hcm_pq1_voice matches ..0 run scoreboard players set @s hcm_pq1_voice 1'])
 dispatch+=['scoreboard players set @s hcm_pq1_vqueue 0','scoreboard players set @s hcm_pq1_vplaying 0']
 put('voice/block/dispatch_self',dispatch)
 text=core[path('session/tick_self')].decode()
 text=text.replace('execute if score @s hcm_pq1_queued matches 1..', 'execute if score @s hcm_pq1_voice matches 0 if score @s hcm_pq1_vqueue matches 1.. run function capitale:pq01/voice/block/dispatch_self\nexecute if score @s hcm_pq1_vqueue matches 0 if score @s hcm_pq1_queued matches 1..',1)
 core[path('session/tick_self')]=text.encode()
 text=core[path('dispatch_self')].decode();marker='execute if score @s hcm_pq1_voice matches 1.. run return run function capitale:pq01/queue_self'
 assert marker in text
 core[path('dispatch_self')]=text.replace(marker,marker+'\nexecute if score @s hcm_pq1_vqueue matches 1.. run return run function capitale:pq01/queue_self').encode()
 put('admin/diagnostic_perrin',['indivispq01 diagnostic_perrin'])
 meta=json.loads(core['pack.mcmeta']);meta['pack']['description']+=' — HF9 : blocs de lecture et file de doublage'
 core['pack.mcmeta']=(json.dumps(meta,ensure_ascii=False,indent=2)+'\n').encode()
