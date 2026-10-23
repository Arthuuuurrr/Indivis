"""Régression du verrou avec scripts réels et modèle séquentiel de commandes, sans moteur Minecraft."""
from pathlib import Path
from collections import deque
import argparse,copy,hashlib,json,subprocess,zipfile
from build import HERE,NAME

def main():
 ap=argparse.ArgumentParser();ap.add_argument('--core',type=Path,required=True);ap.add_argument('--nexus',type=Path,required=True);a=ap.parse_args()
 audit=HERE/'audit';audit.mkdir(exist_ok=True);jar=HERE/'dist'/NAME
 with zipfile.ZipFile(a.core) as z:original=z.read('data/capitale/function/profiles/before_save_self.mcfunction').decode()
 with zipfile.ZipFile(jar) as z:wrapper=z.read('data/indivis_admin/function/profile/before_save_self.mcfunction').decode()
 with zipfile.ZipFile(a.nexus) as z:
  entry='net/tompsen/nexuscharacters/V2QuestPolicy.class';dest=HERE/'build/reference'/entry;dest.parent.mkdir(parents=True,exist_ok=True);dest.write_bytes(z.read(entry))
 funcs={'capitale:profiles/before_save_self':original,'indivis_admin:profile/before_save_self':wrapper}
 calls=[]
 def execute(name,who,states):
  queue=deque(l.strip() for l in funcs[name].splitlines() if l.strip() and not l.lstrip().startswith('#'))
  s=states[who]
  while queue:
   line=queue.popleft()
   if line.startswith('execute store success score @s '):
    parts=line.split();destination=parts[5];key=parts[-1];s[destination]=int(key in s);continue
   if line.startswith(('execute if score @s ','execute unless score @s ')):
    condition,body=line.split(' run ',1);parts=condition.split();matches=s.get(parts[4])==int(parts[-1]);matches=not matches if parts[1]=='unless' else matches
    if matches:queue.appendleft(body)
    continue
   if line.startswith('function '):
    child=line.split()[1]
    if child=='capitale:profiles/pre_save_runtime_flush_self':calls.append(who)
    else:queue.extendleft(reversed([l.strip() for l in funcs[child].splitlines() if l.strip() and not l.lstrip().startswith('#')]))
    continue
   if line.startswith('scoreboard players set @s '):
    parts=line.split();s[parts[4]]=int(parts[5]);continue
   if line.startswith('scoreboard players operation @s '):
    parts=line.split();s[parts[4]]=s[parts[7]];continue
   if line.startswith('scoreboard players reset @s '):s.pop(line.split()[4],None);continue
   raise AssertionError('Commande non modélisée : '+line)
 checks=0
 def check(value,name):
  nonlocal checks
  checks+=1
  if not value:raise AssertionError(name)
 before={'A':{'CAP_PROFILE_LOCK':0,'CAP_RANGSOCIAL':20},'B':{'CAP_PROFILE_LOCK':0,'CAP_RANGSOCIAL':30}}
 old=copy.deepcopy(before);execute('capitale:profiles/before_save_self','A',old);check(old['A']['CAP_PROFILE_LOCK']==1,'régression originale reproduite')
 for previous in (0,1,7,None):
  state=copy.deepcopy(before)
  if previous is None:state['A'].pop('CAP_PROFILE_LOCK')
  else:state['A']['CAP_PROFILE_LOCK']=previous
  expected=copy.deepcopy(state);calls.clear();execute('indivis_admin:profile/before_save_self','A',state)
  check(state==expected,'restauration exacte du verrou '+str(previous));check(calls==['A'],'flush original exécuté une fois')
 state=copy.deepcopy(before);calls.clear()
 for i in range(20):execute('indivis_admin:profile/before_save_self','A' if i%2==0 else 'B',state)
 check(state==before,'sauvegardes répétées de deux joueurs isolées');check(calls.count('A')==calls.count('B')==10,'vingt flushs conservés')
 # Une file parent applique d'abord le wrapper complet puis sa commande suivante :
 # la restauration est dans la même fonction, y compris si l'appel Nexus est imbriqué dans Brigadier.
 check(wrapper.index('function capitale:profiles/before_save_self')<wrapper.index('operation @s CAP_PROFILE_LOCK'),'restauration après le hook enfant')
 exports=['--add-exports','java.base/jdk.internal.org.objectweb.asm=ALL-UNNAMED','--add-exports','java.base/jdk.internal.org.objectweb.asm.tree=ALL-UNNAMED']
 def run(args,name):
  p=subprocess.run(list(map(str,args)),capture_output=True,text=True);(audit/name).write_text(p.stdout+p.stderr);print(p.stdout+p.stderr,end='');assert p.returncode==0,name
 cp=str(jar)+':'+str(HERE/'build/reference');tools=HERE/'build/tools'
 run(['java','--module','jdk.compiler/com.sun.tools.javac.Main',*exports,'-cp',cp,'-d',tools,HERE/'tools/CheckNexusTarget.java',HERE/'tools/CheckProfilePolicy.java'],'compile-regressions.log')
 run(['java',*exports,'-cp',str(tools)+':'+cp,'CheckNexusTarget',a.nexus],'nexus-target.log')
 run(['java','-cp',str(tools)+':'+cp,'CheckProfilePolicy'],'profile-policy.log')
 report={'status':'PASS','command_model_assertions':checks,'minecraft_fabric_tested':False,'model':'Scripts réels exécutés dans un modèle séquentiel réduit ; pas un moteur Minecraft ou un parseur Brigadier.','core_sha256':hashlib.sha256(a.core.read_bytes()).hexdigest(),'nexus_sha256':hashlib.sha256(a.nexus.read_bytes()).hexdigest(),'compiled_mixin_handler_tested':True,'real_nexus_target_checked':True,'fabric_mixin_application_tested':False}
 (audit/'regressions.json').write_text(json.dumps(report,ensure_ascii=False,indent=2)+'\n');print(json.dumps(report,ensure_ascii=False))
if __name__=='__main__':main()
