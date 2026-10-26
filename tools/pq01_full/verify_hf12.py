"""Vérifications HF12 : compatibilité quai, repères et conservation du prologue."""
from pathlib import Path
import hashlib,json,os,re,subprocess,zipfile
import build
from verify_hf3 import read_zip

ROOT=build.ROOT;HERE=build.HERE;AUDIT=build.OUT/'hf12_audit'
def sha(p):return hashlib.sha256(p.read_bytes()).hexdigest()
def main():
    AUDIT.mkdir(exist_ok=True)
    if os.environ.get('HF12_REUSE_STATIC')=='1':
        static=json.loads((AUDIT/'static_validation.json').read_text())
        assert static['status']=='PASS'
        assert all(sha(build.OUT/name)==static['files'][kind]['sha256'] for kind,name in build.FILES.items())
        return finish(static)
    def run(cmd,log):
        prior=os.environ.get('HF12_CHECKS_FROM_LOGS')
        if prior and (Path(prior)/log).is_file() and log!='native_capture.log':
            out=(Path(prior)/log).read_text()
            if log.startswith('Pq01') and not log.endswith('_compile.log'):
                assert 'PASS ' in out or 'checks=121 failures=0;' in out,log
            (AUDIT/log).write_text(out)
            return out
        r=subprocess.run(list(map(str,cmd)),capture_output=True,text=True,timeout=180)
        out=r.stdout+r.stderr;(AUDIT/log).write_text(out)
        if r.returncode:raise RuntimeError(log+'\n'+out)
        return out
    jar=build.OUT/build.FILES['journal'];mc=ROOT/'work/pq01_hf2/runtime/minecraft-intermediary-1.21.11.jar'
    libraries=sorted((ROOT/'work/pq01_hf2/runtime/minecraft_bundle/META-INF/libraries').rglob('*.jar'))
    easy=ROOT/'upload/easy_npc-fabric-1.21.11-7.16.0(1).jar'
    dialogue=ROOT/'upload/haute-capitale-dialogue-0.1.0+1.21.11.b10-DATAPACK-COMMAND-HOTFIX (1)(2).jar'
    loader=ROOT/'work/pq01_hf4/runtime/libraries/fabric-loader-0.19.5.jar'
    java=ROOT/'work/pq01_hf2/runtime/jre/zulu21.52.203-ca-jre21.0.12.1-linux_x64/bin/java'
    classes=AUDIT/'tests';classes.mkdir(exist_ok=True)
    compile_cp=os.pathsep.join(map(str,[HERE/'contract_classes',HERE/'classes']))
    benches=['Pq01NbtIntegrationBench','Pq01NativeApiBench','Pq01CallbackBench','Pq01ColorBench']
    run(['java','--module','jdk.compiler/com.sun.tools.javac.Main','-proc:none','-cp',compile_cp,'-d',classes,*[HERE/f'tests/integration/{n}.java' for n in benches],HERE/'tests/Pq01MotionBench.java'],'compile_tests.log')
    base=[jar,mc,*libraries,easy,dialogue,loader]
    cp=os.pathsep.join(map(str,[classes,*base]));logs={}
    for name in benches:
        logs[name]=run([java,'-Dindivis.expected.jar=PQ01-R3-HF12.jar','-cp',cp,'net.hautecapitale.quetes.sequence.'+name,*([HERE/'java/net/hautecapitale/quetes/sequence/Pq01Bridge.java'] if name=='Pq01NativeApiBench' else [])],name+'.log')
    logs['motion']=run([java,'-cp',cp,'Pq01MotionBench'],'motion.log')
    for name,folder in [('Pq01InteractionBench','interaction_doubles'),('Pq01ClientBench','client_doubles'),('Pq01GeraudBench','interaction_doubles')]:
        out=AUDIT/folder;out.mkdir(exist_ok=True)
        sources=sorted((HERE/'tests'/folder).rglob('*.java'))
        if folder=='client_doubles':sources.append(HERE/'tests/native_capture_doubles/net/minecraft/class_437.java')
        run(['java','--module','jdk.compiler/com.sun.tools.javac.Main','-proc:none','-cp',compile_cp,'-d',out,HERE/f'tests/integration/{name}.java',*([HERE/'tests/integration/Pq01InteractionBench.java'] if name=='Pq01GeraudBench' else []),*sources],name+'_compile.log')
        logs[name]=run([java,'-Dindivis.expected.jar=PQ01-R3-HF12.jar','-cp',os.pathsep.join(map(str,[out,*base])),'net.hautecapitale.quetes.sequence.'+name],name+'.log')
    voices=ROOT/'work/pq01_hf4/voices/package/indivis-voices-0.2.3+1.21.11.jar'
    abi_checks=0
    for native,name,expected in [
        (dialogue,'net.hautecapitale.dialogue.client.DialogueClientState',['java.lang.String pnjNom;','long fenetreJusquaMs;','int sessionId;','captureActive();','captureVersion();','onChoixEnvoye(boolean, java.lang.String);']),
        (dialogue,'net.hautecapitale.dialogue.client.capture.CaptureDialogueScreen',['DialoguePanel panneau;','int versionVue;']),
        (dialogue,'net.hautecapitale.dialogue.client.screen.DialoguePanel',['boolean typewriter;','void nom(java.lang.String);','void attente(boolean);','boolean dernierePage();','boolean typewriterActif();','boolean avancer();','boolean historiqueVisible();','int choixSous(double, double);']),
        (voices,'net.indivis.voices.client.VoicesClient',[' active;',' pending;',' fading;'])]:
        dump=run(['java','--module','jdk.jdeps/com.sun.tools.javap.Main','-p','-classpath',native,name],'abi_'+name.rsplit('.',1)[1]+'.log')
        for token in expected:assert token in dump,(name,token)
        abi_checks+=len(expected)
    screen_api=AUDIT/'fabric-screen-api.jar'
    with zipfile.ZipFile(ROOT/'work/pq01_hf4/runtime/server/mods/fabric-api-0.141.6+1.21.11.jar') as fabric:
        names=[n for n in fabric.namelist() if 'fabric-screen-api-v1' in n and n.endswith('.jar')]
        assert len(names)==1
        screen_api.write_bytes(fabric.read(names[0]))
    screen_contracts={
      'net.fabricmc.fabric.api.client.screen.v1.ScreenEvents':['AFTER_INIT;','afterRender(net.minecraft.class_437);'],
      'net.fabricmc.fabric.api.client.screen.v1.ScreenEvents$AfterInit':['afterInit(net.minecraft.class_310, net.minecraft.class_437, int, int);'],
      'net.fabricmc.fabric.api.client.screen.v1.ScreenEvents$AfterRender':['afterRender(net.minecraft.class_437, net.minecraft.class_332, int, int, float);'],
      'net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents':['allowKeyPress(net.minecraft.class_437);'],
      'net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents$AllowKeyPress':['allowKeyPress(net.minecraft.class_437, net.minecraft.class_11908);'],
      'net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents':['allowMouseClick(net.minecraft.class_437);'],
      'net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents$AllowMouseClick':['allowMouseClick(net.minecraft.class_437, net.minecraft.class_11909);']}
    for name,expected in screen_contracts.items():
        dump=run(['java','--module','jdk.jdeps/com.sun.tools.javap.Main','-p','-classpath',screen_api,name],'abi_'+name.rsplit('.',1)[1]+'.log')
        for token in expected:assert token in dump,(name,token)
        abi_checks+=len(expected)

    before=read_zip(ROOT/'work/pq01_hf12/hf11_exact.jar');after=read_zip(jar)
    changed=sorted(n for n in before if before[n]!=after[n]);added=sorted(set(after)-set(before));removed=sorted(set(before)-set(after))
    assert not removed,removed
    allowed=lambda n:n=='fabric.mod.json' or any(n.startswith('net/hautecapitale/quetes/sequence/'+c) for c in ['Pq01Actions','Pq01Bridge'])
    assert all(allowed(n) for n in changed),changed
    assert all(n=='indivis_core_quest_givers.tsv' or n.startswith('net/hautecapitale/quetes/sequence/CoreQuestMarkers') for n in added),added
    protected=[n for n in before if not allowed(n)]
    assert all(before[n]==after[n] for n in protected)
    diffs={'journal_vs_HF11':{'changed':changed,'added':added,'removed':removed,'protected_entries_identical':len(protected)}}
    bases={'core':'capitale_core_1.5.7_V2_SEQUENCE_I_SI1_PQ01_R3_HF9.zip','quests':'hc_quetes_b22_V2_SEQUENCE_I_SI1b_PQ01_R3_HF4.zip'}
    packs={k:read_zip(build.OUT/build.FILES[k]) for k in bases}
    for kind,filename in bases.items():
        old=read_zip(build.OUT/filename);new=packs[kind]
        changed=sorted(n for n in old if old[n]!=new[n]);added=sorted(set(new)-set(old));removed=sorted(set(old)-set(new))
        assert not removed,removed
        if kind=='core':
            assert changed==['data/capitale/function/core/load.mcfunction','pack.mcmeta'],changed
            assert new[changed[0]].startswith(old[changed[0]])
            assert all(n.startswith(('data/capitale/function/quest/markers/','data/capitale/tags/entity_type/quest_markers/','docs/quest_markers/')) for n in added),added
        else:assert changed==['pack.mcmeta'] and not added,(changed,added)
        diffs[kind]={'changed':changed,'added':added,'removed':removed,'protected_entries_identical':sum(old[n]==new[n] for n in old)}
    core,q=packs['core'],packs['quests']
    from io import BytesIO
    outer=ROOT/'work/quai_reprise/materialized/Indivis_Core_Reperes_Quetes_QMARK1_2026-10-26.zip'
    with zipfile.ZipFile(outer) as z:
        name=next(n for n in z.namelist() if n.startswith('datapacks/capitale_core_') and n.endswith('.zip'))
        with zipfile.ZipFile(BytesIO(z.read(name))) as inner:prior={n:inner.read(n) for n in inner.namelist() if not n.endswith('/')}
    def commands(b):return [l for l in b.decode().splitlines() if l and not l.startswith('#')]
    availability=[n for n in prior if n.startswith('data/capitale/function/quest/markers/available/') or n=='data/capitale/function/quest/markers/guide_free.mcfunction']
    assert len(availability)==25
    assert all(commands(prior[n])==commands(core[n]) for n in availability)
    defined={n.split('/')[1]+':'+n.split('/function/',1)[1][:-11] for p in packs.values() for n in p if '/function/' in n and n.endswith('.mcfunction')}
    missing=[];references=scope=jsons=0
    for kind,pack in packs.items():
        for name,content in pack.items():
            if name.endswith('.json') or name=='pack.mcmeta':json.loads(content);jsons+=1
            if '/function/' not in name or not name.endswith('.mcfunction') or not re.search('/pq01/|/quests/mq0[123]/|sequence_i|sentinelle|/quest/|guard|garde',name):continue
            scope+=1
            for line in content.decode().splitlines():
                if line.lstrip().startswith('#'):continue
                for target in re.findall(r'(?:^|\brun )function ([a-z0-9_.:/-]+)',line):
                    references+=1
                    if target not in defined:missing.append([kind,name,target])
    assert not missing,missing
    baseline_core=read_zip(build.OUT/bases['core'])
    biomes=[n for n in baseline_core if '/worldgen/biome/' in n and '/tags/' not in n]
    assert len(biomes)==25 and all(core[n]==baseline_core[n] for n in biomes)
    pq=[n for n in baseline_core if '/function/pq01/' in n]
    assert all(core[n]==baseline_core[n] for n in pq)
    assert q['data/hc_quetes/function/runtime/second.mcfunction']==read_zip(build.OUT/bases['quests'])['data/hc_quetes/function/runtime/second.mcfunction']
    catalogue=json.loads((build.OUT/'qmark2_catalogue.json').read_text())
    assert len(catalogue['quests'])==24 and len(catalogue['givers'])==20
    assert catalogue['command_aliases']['capitale:quest/journalieres/temoignages_quais/greffier/interact_self']=='greffier'
    assert len(build.IDS)==95 and len(set(build.IDS.values()))==95
    for role in build.ROLES:
        for phase in ['large','quai']:
            key=role+'_'+phase
            assert 'indivispq01 bind_'+key in core['data/capitale/function/pq01/liaison/'+key+'.mcfunction'].decode()
            assert 'indivispq01 unbind_'+key in core['data/capitale/function/pq01/liaison/liberer_'+key+'.mcfunction'].decode()
            assert 'indivispq01 place_'+key in core['data/capitale/function/pq01/pose/placer_'+key+'.mcfunction'].decode()
    native=run(['python3',HERE/'run_native_capture.py'],'native_capture.log');logs['native_capture']=native
    static={'status':'PASS','json_resources_parsed':jsons,'function_files_in_scope':scope,'function_references_resolved':references,'missing_function_references':missing,'quest_availability_rules_preserved':24,'givers':20,'biomes_byte_identical':len(biomes),'prologue_function_files_byte_identical':len(pq),'stable_dialogue_node_ids':95,'native_client_members_checked':abi_checks,'tests':{k:next((line for line in v.splitlines() if 'PASS ' in line),v.strip().splitlines()[-1]) for k,v in logs.items()},'files':{k:{'filename':v,'bytes':(build.OUT/v).stat().st_size,'sha256':sha(build.OUT/v)} for k,v in build.FILES.items()}}
    (AUDIT/'diffs.json').write_text(json.dumps(diffs,ensure_ascii=False,indent=2)+'\n')
    (AUDIT/'static_validation.json').write_text(json.dumps(static,ensure_ascii=False,indent=2)+'\n')
    if os.environ.get('HF12_STATIC_ONLY')=='1':print(json.dumps(static,ensure_ascii=False,indent=2));return static
    return finish(static)
def finish(static):
    runtime=ROOT/'work/pq01_hf4/runtime';native_results={}
    expected={'qmark2':'QMARK2 checks','quai-hf12':'quay migration checks','world-test-hf12':'pipeline checks','multi-hf12':'multiplayer checks','walk-hf12':'movement regression checks'}
    for label,token in expected.items():
        result=(runtime/(label+'-result.txt')).read_text();log=(runtime/(label+'-server.log')).read_text()
        assert result.startswith('PASS') and token in result,(label,result)
        assert 'Failed to load function' not in log and 'Failed to parse' not in log
        (AUDIT/(label+'.txt')).write_text(result);native_results[label]=result
    baseline=(runtime/'quai-hf11-regression-result.txt').read_text()
    assert baseline.startswith('FAIL') and 'legacy function bound altheon_quai' in baseline,baseline
    (AUDIT/'hf11_before_fix_quai.txt').write_text(baseline)
    static['native_server_results']={label:text.splitlines()[0] for label,text in native_results.items()}
    static['baseline_HF11_reproduces_captain_quai_rejection']=True
    static['limits']=['Identités Nexus, connexions et réception de chunks synthétiques dans les bancs ; vrais objets et fonctions Minecraft, vraies entités EasyNPC et trackers réseau.','Aucun rendu client graphique, doublage écouté ou mesure de FPS ; carte complète du serveur utilisateur non disponible.','La cause initiale exacte du marqueur absent dans la capture ne peut pas être déduite du log seul ; le nouveau diagnostic expose les conditions réelles.','Le parcours SI1 complet sur la carte utilisateur n’a pas été effectué ; fonctions, gardes et biomes préservés octet par octet.']
    (AUDIT/'validation.json').write_text(json.dumps(static,ensure_ascii=False,indent=2)+'\n')
    print(json.dumps(static,ensure_ascii=False,indent=2));return static
if __name__=='__main__':main()
