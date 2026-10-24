"""Banc sur l’état de capture natif de Dialogue b10, sans rendu client."""
from pathlib import Path
import subprocess,os
import build
r=build.ROOT;s=build.HERE;out=r/'work/pq01_hf9/native-capture';out.mkdir(parents=True,exist_ok=True)
cp=os.pathsep.join(map(str,[s/'contract_classes',s/'classes']))
sources=[s/'tests/integration/Pq01NativeCaptureBench.java',*sorted((s/'tests/native_capture_doubles').rglob('*.java'))]
subprocess.run(['java','--module','jdk.compiler/com.sun.tools.javac.Main','-proc:none','-cp',cp,'-d',str(out),*map(str,sources)],check=True)
libs=sorted((r/'work/pq01_hf2/runtime/minecraft_bundle/META-INF/libraries').rglob('*.jar'))
cp=os.pathsep.join(map(str,[out,s/'output'/build.FILES['journal'],r/'work/pq01_hf2/runtime/minecraft-intermediary-1.21.11.jar',*libs,r/'upload/haute-capitale-dialogue-0.1.0+1.21.11.b10-DATAPACK-COMMAND-HOTFIX (1)(2).jar',r/'work/pq01_hf4/runtime/libraries/fabric-loader-0.19.5.jar']))
java=r/'work/pq01_hf2/runtime/jre/zulu21.52.203-ca-jre21.0.12.1-linux_x64/bin/java'
import json,re
from model import N,ROLES
core=build.load(build.OUT/build.FILES['core']);rows=[]
for key,node in N.items():
    if key=='c0':continue
    messages=[]
    for part in ['frame','node','menu']:
        source=core[f'data/capitale/function/pq01/{part}/{key}.mcfunction'].decode()
        for line in source.splitlines():
            if 'tellraw @s ' not in line:continue
            text=line.split('tellraw @s ',1)[1]
            text=re.sub(r'\$\(b(\d+)\)',lambda m:str(1000000000+int(m[1])),text).replace('$(voice)','0')
            messages.append(text)
    from hf9 import effective,manual
    expected=(0 if manual(key) else len(effective(key)))+1
    rows.append({'key':key,'speaker':ROLES[node['role']],'choices':expected,'messages':messages})
fixtures=r/'work/pq01_hf9/native-capture-fixtures.json';fixtures.write_text(json.dumps(rows,ensure_ascii=False))
p=subprocess.run([str(java),'-cp',cp,'net.hautecapitale.quetes.sequence.Pq01NativeCaptureBench',str(fixtures)],capture_output=True,text=True)
(r/'work/pq01_hf9/native-capture.log').write_text(p.stdout+p.stderr);print(p.stdout+p.stderr);raise SystemExit(p.returncode)
