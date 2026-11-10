from pathlib import Path
import os,subprocess,zipfile,json,shutil,hashlib,urllib.request

H=Path(__file__).resolve().parent
ROOT=Path(os.environ.get('INDIVIS_PQ45_WORK',str(H.parent))).resolve()
RT=ROOT/'sources/tools/pq02/runtime_hf9'
SERVER=H/'native_server'
CLASSES=H/'test_classes'
JAR=H/'output/haute-capitale-quetes-PQ04-PQ05-R1-HF1-1.21.11.jar'
MC=RT/'bootstrap/.fabric/remappedJars/minecraft-1.21.11-0.19.5/server-intermediary.jar'

def write(path,files):
    path.parent.mkdir(parents=True,exist_ok=True)
    with zipfile.ZipFile(path,'w',zipfile.ZIP_DEFLATED) as z:
        for name,body in sorted(files.items()):z.writestr(name,body)

def run():
    mods=SERVER/'mods'
    shutil.rmtree(mods,ignore_errors=True)
    mods.mkdir(parents=True)
    shutil.rmtree(CLASSES,ignore_errors=True)
    CLASSES.mkdir()
    libs=[*sorted((RT/'libraries').glob('*.jar')),*sorted((RT/'minecraft_bundle/META-INF/libraries').rglob('*.jar')),MC,JAR]
    cp=os.pathsep.join(map(str,libs))
    sources=[H/'src'/n for n in ['ColinBindingNative.java','NexusCharacters.java','DialogueManager.java','MakeColinSink.java']]
    r=subprocess.run(['java','--module','jdk.compiler/com.sun.tools.javac.Main','-source','21','-target','21','-proc:none','-cp',cp,'-d',CLASSES,*sources],capture_output=True,text=True)
    (H/'reports/native_compile.log').write_text(r.stdout+r.stderr)
    if r.returncode:raise RuntimeError(r.stderr)
    subprocess.run(['java','-cp',str(CLASSES)+os.pathsep+cp,'MakeColinSink',CLASSES],check=True)
    files={p.relative_to(CLASSES).as_posix():p.read_bytes() for p in CLASSES.rglob('*.class') if p.name!='MakeColinSink.class'}
    files['fabric.mod.json']=json.dumps({'schemaVersion':1,'id':'colin_binding_native_fixture','version':'1.0.0','entrypoints':{'main':['net.hautecapitale.quetes.sequence.ColinBindingNative']},'depends':{'fabric-api':'*','easy_npc':'7.16.0'}}).encode()
    write(mods/'fixture.jar',files)
    # Only metadata is changed in this test copy. The delivered bytecode is used verbatim.
    with zipfile.ZipFile(JAR) as z:files={n:z.read(n) for n in z.namelist()}
    files['fabric.mod.json']=json.dumps({'schemaVersion':1,'id':'colin_quest_code_fixture','version':'1.0.0'}).encode()
    write(mods/'quest_code_fixture.jar',files)
    npc=ROOT/'devtools/easy_npc-core-7.16.0.jar'
    npc.parent.mkdir(parents=True,exist_ok=True)
    expected='be3531fba0d3b0553e64ac8acae1906a882f41b32ac5e64386c3d6c4cbef6f8b'
    if not npc.exists():npc.write_bytes(urllib.request.urlopen('https://cdn.modrinth.com/data/Epm6R3P2/versions/DxBSEUSj/easy_npc-fabric-1.21.11-7.16.0.jar',timeout=40).read())
    assert hashlib.sha256(npc.read_bytes()).hexdigest()==expected
    shutil.copyfile(npc,mods/'easy_npc.jar')
    shutil.copyfile(RT/'fabric-api.jar',mods/'fabric-api.jar')
    (SERVER/'eula.txt').write_text('eula=true\n')
    (SERVER/'server.properties').write_text('online-mode=false\nserver-ip=127.0.0.1\nserver-port=25581\nview-distance=2\nsimulation-distance=2\nspawn-protection=0\nmax-tick-time=0\npause-when-empty-seconds=0\nlevel-type=minecraft:flat\ngenerator-settings={"layers":[{"block":"minecraft:bedrock","height":1},{"block":"minecraft:dirt","height":2},{"block":"minecraft:grass_block","height":1}],"biome":"minecraft:plains"}\ngenerate-structures=false\n')
    shutil.rmtree(SERVER/'world',ignore_errors=True)
    result=SERVER/'colin-tests.txt'
    result.unlink(missing_ok=True)
    game=RT/'minecraft_bundle/META-INF/versions/1.21.11/server-1.21.11.jar'
    cp=os.pathsep.join(map(str,[*sorted((RT/'libraries').glob('*.jar')),*sorted((RT/'minecraft_bundle/META-INF/libraries').rglob('*.jar')),game]))
    with (H/'reports/native_server.log').open('w') as log:
        r=subprocess.run(['java','-XX:ActiveProcessorCount=2','-Xmx2G',f'-Dfabric.gameJarPath={game}','-Dfabric.log.disableAnsi=true','-cp',cp,'net.fabricmc.loader.impl.launch.knot.KnotServer','nogui'],cwd=SERVER,stdout=log,stderr=subprocess.STDOUT,timeout=160)
    if result.exists():
        shutil.copyfile(result,H/'reports/native_checks.txt')
        print(result.read_text())
    else:print((H/'reports/native_server.log').read_text()[-6000:])
    assert r.returncode==0 and result.exists() and result.read_text().startswith('PASS')

if __name__=='__main__':run()
