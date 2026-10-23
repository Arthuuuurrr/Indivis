"""Test isolé de la création/copie et navigation des acteurs, avec Fabric réel."""
from pathlib import Path
import os,shutil,subprocess,zipfile
import build

ROOT=build.HERE.parent.parent
RT=ROOT/'work/pq01_hf4/runtime'
SERVER=RT/'server'
def main():
    classes=RT/'world-test-classes';classes.mkdir(exist_ok=True)
    sources=[build.HERE/'tests/world/Pq01WorldBench.java',build.HERE/'tests/world_doubles/net/hautecapitale/quetes/visibility/QuestVisibility.java',build.HERE/'tests/interaction_doubles/net/tompsen/nexuscharacters/NexusCharacters.java']
    subprocess.run(['java','--module','jdk.compiler/com.sun.tools.javac.Main','-proc:none','-cp',os.pathsep.join(map(str,[build.HERE/'contract_classes',build.HERE/'classes'])),'-d',str(classes),*map(str,sources)],check=True)
    with zipfile.ZipFile(build.OUT/build.FILES['journal']) as src,zipfile.ZipFile(SERVER/'mods/pq01-world-test.jar','w',zipfile.ZIP_DEFLATED) as out:
        for name in src.namelist():
            if (name.startswith('net/hautecapitale/quetes/') and not name.startswith('net/hautecapitale/quetes/visibility/')) or name=='indivis_pq01.mixins.json':out.writestr(name,src.read(name))
        for path in classes.rglob('*.class'):out.write(path,path.relative_to(classes).as_posix())
        out.writestr('fabric.mod.json','{"schemaVersion":1,"id":"indivis_pq01_worldtest","version":"4.0.0","environment":"server","entrypoints":{"main":["net.hautecapitale.quetes.sequence.Pq01WorldBench"]},"mixins":["indivis_pq01.mixins.json"],"depends":{"fabricloader":">=0.19.5","fabric-api":"*","minecraft":"1.21.11","easy_npc":"*"}}')
    i=1
    while (SERVER/f'world-run{i}').exists():i+=1
    if (SERVER/'world').exists():shutil.move(SERVER/'world',SERVER/f'world-run{i}')
    for path in [RT/'world-test-server.log',SERVER/'world-test-result.txt']:
        if path.exists():shutil.move(path,path.with_name(path.stem+f'-run{i}'+path.suffix))
    pack=SERVER/'world/datapacks/pq01_syntax';pack.mkdir(parents=True,exist_ok=True)
    with zipfile.ZipFile(build.OUT/build.FILES['core']) as src:
        for name in src.namelist():
            if name=='pack.mcmeta' or name.startswith('data/capitale/function/pq01/'):
                p=pack/name;p.parent.mkdir(parents=True,exist_ok=True);p.write_bytes(src.read(name))
    for kind in ['core','quests']:
        with zipfile.ZipFile(build.OUT/build.FILES[kind]) as src:
            for name in src.namelist():
                if '/predicate/' in name:
                    p=pack/name;p.parent.mkdir(parents=True,exist_ok=True);p.write_bytes(src.read(name))
    mc=ROOT/'work/pq01_hf2/runtime/minecraft_bundle'
    game=mc/'META-INF/versions/1.21.11/server-1.21.11.jar'
    java=ROOT/'work/pq01_hf2/runtime/jre/zulu21.52.203-ca-jre21.0.12.1-linux_x64/bin/java'
    cp=os.pathsep.join(map(str,[*sorted((RT/'libraries').glob('*.jar')),*sorted((mc/'META-INF/libraries').rglob('*.jar')),game]))
    with (RT/'world-test-server.log').open('w') as log:
        result=subprocess.run([str(java),'-Xmx2G','-Xms512M',f'-Dfabric.gameJarPath={game}','-Dfabric.log.disableAnsi=true','-cp',cp,'net.fabricmc.loader.impl.launch.knot.KnotServer','nogui'],cwd=SERVER,stdout=log,stderr=subprocess.STDOUT,timeout=180)
    print((SERVER/'world-test-result.txt').read_text())
    assert result.returncode==0
    assert (SERVER/'world-test-result.txt').read_text().startswith('PASS')
    log=(RT/'world-test-server.log').read_text()
    assert 'Failed to load function' not in log and 'Failed to parse' not in log
    print('PASS real Fabric function parsing: all PQ01 functions loaded')

if __name__=='__main__':main()
