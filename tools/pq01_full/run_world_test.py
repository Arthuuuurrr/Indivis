"""Test isolé de la création/copie et navigation des acteurs, avec Fabric réel."""
from pathlib import Path
import os,shutil,subprocess,zipfile,json,re,datetime
import build

ROOT=build.HERE.parent.parent
RT=ROOT/'work/pq01_hf4/runtime'
SERVER=RT/('server-hf9-'+datetime.datetime.now(datetime.timezone.utc).strftime('%Y%m%d%H%M%S%f'))
def main():
    SERVER.mkdir(exist_ok=True);(SERVER/'mods').mkdir(exist_ok=True)
    for name in ['eula.txt','server.properties']:
        if not (SERVER/name).exists():shutil.copyfile(RT/'server'/name,SERVER/name)
    # Les joueurs du banc sont injectés seulement le temps du tick contrôlé.
    # Éviter la pause automatique du serveur vide après 60 s.
    props=SERVER/'server.properties';props.write_text(re.sub(r'^pause-when-empty-seconds=.*$', 'pause-when-empty-seconds=0',props.read_text(),flags=re.M))
    for p in (RT/'server/mods').glob('*.jar'):
        if p.name!='pq01-world-test.jar' and not (SERVER/'mods'/p.name).exists():shutil.copyfile(p,SERVER/'mods'/p.name)
    classes=RT/'world-test-classes';classes.mkdir(exist_ok=True)
    shutil.rmtree(classes);classes.mkdir()
    sources=[build.HERE/'tests/world/Pq01WorldBench.java',build.HERE/'tests/world/Pq01PipelineBench.java',build.HERE/'tests/world/Pq01SceneWorldBench.java',build.HERE/'tests/world/Pq01AmbientWorldBench.java',build.HERE/'tests/world_doubles/net/hautecapitale/quetes/visibility/QuestVisibility.java',build.HERE/'tests/interaction_doubles/net/tompsen/nexuscharacters/NexusCharacters.java',build.HERE/'tests/client_doubles/net/hautecapitale/dialogue/client/capture/CaptureDialogueScreen.java',*sorted((build.HERE/'tests/native_capture_doubles').rglob('*.java'))]
    subprocess.run(['java','--module','jdk.compiler/com.sun.tools.javac.Main','-proc:none','-cp',os.pathsep.join(map(str,[build.HERE/'contract_classes',build.HERE/'classes'])),'-d',str(classes),*map(str,sources)],check=True)
    export=['--add-exports','java.base/jdk.internal.org.objectweb.asm=ALL-UNNAMED']
    subprocess.run(['java','--module','jdk.compiler/com.sun.tools.javac.Main',*export,'-d',str(classes),str(build.HERE/'tests/world/MakePacketSink.java')],check=True)
    subprocess.run(['java',*export,'-cp',str(classes),'MakePacketSink',str(classes)],check=True)
    asm=RT/'libraries/asm-9.10.1.jar'
    subprocess.run(['java','--module','jdk.compiler/com.sun.tools.javac.Main','-cp',str(asm),'-d',str(classes),str(build.HERE/'tests/world/StripClientEnvironment.java')],check=True)
    subprocess.run(['java','-cp',os.pathsep.join(map(str,[classes,asm])),'StripClientEnvironment',str(ROOT/'upload/haute-capitale-dialogue-0.1.0+1.21.11.b10-DATAPACK-COMMAND-HOTFIX (1)(2).jar'),str(classes)],check=True)
    with zipfile.ZipFile(build.OUT/build.FILES['journal']) as src,zipfile.ZipFile(SERVER/'mods/pq01-world-test.jar','w',zipfile.ZIP_DEFLATED) as out:
        for name in src.namelist():
            if (name.startswith('net/hautecapitale/quetes/') and not name.startswith('net/hautecapitale/quetes/visibility/')):out.writestr(name,src.read(name))
            elif name=='indivis_pq01.mixins.json':
                config=json.loads(src.read(name));config['mixins']+=config.pop('client',[]);out.writestr(name,json.dumps(config))
        for path in classes.rglob('*.class'):out.write(path,path.relative_to(classes).as_posix())
        out.writestr('fabric.mod.json','{"schemaVersion":1,"id":"indivis_pq01_worldtest","version":"4.0.0","environment":"server","entrypoints":{"main":["net.hautecapitale.quetes.sequence.Pq01WorldBench"]},"mixins":["indivis_pq01.mixins.json"],"depends":{"fabricloader":">=0.19.5","fabric-api":"*","minecraft":"1.21.11","easy_npc":"*"}}')
    i=1
    while (SERVER/f'world-run{i}').exists():i+=1
    # Monde de test jetable, distinct de toute carte fournie par l’utilisateur.
    if (SERVER/'world').exists():shutil.rmtree(SERVER/'world')
    for path in [RT/'world-test-hf9-server.log',SERVER/'world-test-result.txt']:
        if path.exists():path.unlink()
    pack=SERVER/'world/datapacks/pq01_syntax';pack.mkdir(parents=True,exist_ok=True)
    with zipfile.ZipFile(build.OUT/build.FILES['core']) as src:
        for name in src.namelist():
            if name=='pack.mcmeta' or name.startswith('data/capitale/function/pq01/'):
                p=pack/name;p.parent.mkdir(parents=True,exist_ok=True);p.write_bytes(src.read(name))
    for kind in ['core','quests']:
        with zipfile.ZipFile(build.OUT/build.FILES[kind]) as src:
            for name in src.namelist():
                if name=='data/hc_quetes/predicate/migration/state/pq02/0.json':
                    p=pack/name;p.parent.mkdir(parents=True,exist_ok=True);p.write_bytes(src.read(name))
    # Garder les vraies fonctions PQ01 et migration ; isoler uniquement les intégrations externes.
    with zipfile.ZipFile(build.OUT/build.FILES['quests']) as src:
        for name in src.namelist():
            if name=='data/hc_quetes/function/migration/ensure/pq01.mcfunction' or name.startswith('data/hc_quetes/function/migration/write/pq01/'):
                p=pack/name;p.parent.mkdir(parents=True,exist_ok=True);p.write_bytes(src.read(name))
    overrides={'data/capitale/function/pq01/context_self.mcfunction':'return 1\n',
      'data/capitale/function/player/ensure_runtime_self.mcfunction':'return 1\n',
      'data/capitale/function/quest/dialogue/clear_self.mcfunction':'scoreboard players set @s CAP_QDIALOG_OWNER 0\n',
      'data/capitale/function/quest/spawn_dirigeable_couronne/capitaine_large/conclure_et_arriver_self.mcfunction':'scoreboard players set @s pipeline_dock 1\n',
      'data/capitale/function/pq01/reward_body_self.mcfunction':'scoreboard players set @s pipeline_reward 1\n'}
    for name,data in overrides.items():p=pack/name;p.parent.mkdir(parents=True,exist_ok=True);p.write_text(data)
    from model import N
    from hf9 import effective,manual
    scores={'QuestChoix','CAP_QDIALOG_OWNER','CAP_QDIALOG_KEEP','pipeline_dock','pipeline_reward','hcq_pq01','CAP_QSEQ'}
    for p in pack.rglob('*.mcfunction'):
        text=p.read_text()
        scores.update(re.findall(r'(?<!\S)(?:@s|#[A-Za-z0-9_]+) ([A-Za-z][A-Za-z0-9_]*)',text))
    menus={'ga_menu','gautier_menu','hugues_menu','anselme_menu','perrin_menu','c_menu','e_menu'}
    edges=[];repeat=[]
    for key,node in N.items():
        if key=='c0':continue
        choices=effective(key);automatic=manual(key)
        for i,ch in enumerate(choices):
            if ch['target']=='banter_start':continue
            edges.append({'key':key,'role':node['role'],'pick':i,'target':ch['target'],'automatic':automatic,'choices':1 if automatic else len(choices)+1})
        if key in {'ga_menu','gautier_menu','hugues_menu','anselme_menu','perrin_menu','e_menu'}:
            count=next((i for i,c in enumerate(choices) if c['target']=='banter_start'),len(choices)-1)
            repeat.append({'key':key,'role':node['role'],'count':count,'choices':len(choices)+1})
    (SERVER/'pipeline-fixtures.json').write_text(json.dumps({'scores':sorted(scores),'edges':edges,'menus':repeat},ensure_ascii=False))
    assert not list((pack/'data/hc_quetes/function/migration/items').glob('*')),'stale fixture files'
    print('Prepared fresh PQ01 fixture:',pack,flush=True)
    mc=ROOT/'work/pq01_hf2/runtime/minecraft_bundle'
    game=mc/'META-INF/versions/1.21.11/server-1.21.11.jar'
    java=ROOT/'work/pq01_hf2/runtime/jre/zulu21.52.203-ca-jre21.0.12.1-linux_x64/bin/java'
    cp=os.pathsep.join(map(str,[*sorted((RT/'libraries').glob('*.jar')),*sorted((mc/'META-INF/libraries').rglob('*.jar')),game]))
    with (RT/'world-test-hf9-server.log').open('w') as log:
        result=subprocess.run([str(java),'-Xmx2G','-Xms512M',f'-Dfabric.gameJarPath={game}','-Dfabric.log.disableAnsi=true','-cp',cp,'net.fabricmc.loader.impl.launch.knot.KnotServer','nogui'],cwd=SERVER,stdout=log,stderr=subprocess.STDOUT,timeout=180)
    print((SERVER/'world-test-result.txt').read_text())
    shutil.copyfile(SERVER/'world-test-result.txt',RT/'world-test-hf9-result.txt')
    assert result.returncode==0
    assert (SERVER/'world-test-result.txt').read_text().startswith('PASS')
    log=(RT/'world-test-hf9-server.log').read_text()
    assert 'Failed to load function' not in log and 'Failed to parse' not in log
    print('PASS real Fabric function parsing: all PQ01 functions loaded')

if __name__=='__main__':main()
