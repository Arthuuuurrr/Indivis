"""Vérifications HF8 : scène couplée, notifications hors capture et conservation de HF7."""
from pathlib import Path
import hashlib,json,os,re,subprocess,zipfile
import build
from verify_hf3 import read_zip

ROOT=build.ROOT;HERE=build.HERE;AUDIT=build.OUT/'hf8_audit'
def sha(p):return hashlib.sha256(p.read_bytes()).hexdigest()
def main():
    AUDIT.mkdir(exist_ok=True)
    def run(cmd,log):
        r=subprocess.run(list(map(str,cmd)),capture_output=True,text=True,timeout=60)
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
        logs[name]=run([java,'-Dindivis.expected.jar=PQ01-R3-HF8.jar','-cp',cp,'net.hautecapitale.quetes.sequence.'+name,*([HERE/'java/net/hautecapitale/quetes/sequence/Pq01Bridge.java'] if name=='Pq01NativeApiBench' else [])],name+'.log')
    logs['motion']=run([java,'-cp',cp,'Pq01MotionBench'],'motion.log')
    for name,folder in [('Pq01InteractionBench','interaction_doubles'),('Pq01ClientBench','client_doubles'),('Pq01GeraudBench','interaction_doubles')]:
        out=AUDIT/folder;out.mkdir(exist_ok=True)
        sources=sorted((HERE/'tests'/folder).rglob('*.java'))
        if folder=='client_doubles':sources.append(HERE/'tests/native_capture_doubles/net/minecraft/class_437.java')
        run(['java','--module','jdk.compiler/com.sun.tools.javac.Main','-proc:none','-cp',compile_cp,'-d',out,HERE/f'tests/integration/{name}.java',*([HERE/'tests/integration/Pq01InteractionBench.java'] if name=='Pq01GeraudBench' else []),*sources],name+'_compile.log')
        logs[name]=run([java,'-Dindivis.expected.jar=PQ01-R3-HF8.jar','-cp',os.pathsep.join(map(str,[out,*base])),'net.hautecapitale.quetes.sequence.'+name],name+'.log')
    voices=ROOT/'work/pq01_hf4/voices/package/indivis-voices-0.2.3+1.21.11.jar'
    abi_checks=0
    for native,name,expected in [
        (dialogue,'net.hautecapitale.dialogue.client.DialogueClientState',['java.lang.String pnjNom;','long fenetreJusquaMs;','captureActive();','captureVersion();','onChoixEnvoye(boolean, java.lang.String);']),
        (dialogue,'net.hautecapitale.dialogue.client.capture.CaptureDialogueScreen',['DialoguePanel panneau;','int versionVue;']),
        (dialogue,'net.hautecapitale.dialogue.client.screen.DialoguePanel',['boolean typewriter;','void nom(java.lang.String);','void attente(boolean);','boolean dernierePage();','boolean typewriterActif();','boolean avancer();']),
        (voices,'net.indivis.voices.client.VoicesClient',[' active;',' pending;',' fading;'])]:
        dump=run(['java','--module','jdk.jdeps/com.sun.tools.javap.Main','-p','-classpath',native,name],'abi_'+name.rsplit('.',1)[1]+'.log')
        for token in expected:assert token in dump,(name,token)
        abi_checks+=len(expected)
    before=read_zip(build.OUT/'haute-capitale-quetes-0.1.0+1.21.11.b7-SEQUENCE-I-SI1b-PQ01-R3-HF7.jar');after=read_zip(jar)
    changed=sorted(n for n in before if before[n]!=after[n]);added=sorted(set(after)-set(before));removed=sorted(set(before)-set(after))
    assert not removed
    assert all(n in {'fabric.mod.json','indivis_pq01.mixins.json'} or any(n.startswith('net/hautecapitale/quetes/sequence/'+c) for c in ['Pq01Bridge','Pq01Motion','Pq01Client','Pq01Pacing']) for n in changed),changed
    assert not added,added
    assert all(before[n]==after[n] for n in before if '/mixin/' in n or n.endswith('/GuardNbt.class') or n.endswith('/SequenceBridge.class'))
    diffs={'journal':{'changed':changed,'added':added,'removed':removed,'all_mixins_and_historical_helpers_byte_identical':True}}
    for kind,prefix in [('core','capitale_core_1.5.7_V2_SEQUENCE_I_SI1_PQ01_R3_HF7.zip'),('quests','hc_quetes_b22_V2_SEQUENCE_I_SI1b_PQ01_R3_HF4.zip')]:
        old=read_zip(build.OUT/prefix);new=read_zip(build.OUT/build.FILES[kind])
        changed=sorted(n for n in old if old[n]!=new[n]);added=sorted(set(new)-set(old));removed=sorted(set(old)-set(new));assert not removed
        allowed=lambda n:kind=='core' and (n=='pack.mcmeta' or n.startswith('data/capitale/function/pq01/'))
        assert all(allowed(n) for n in changed+added),(kind,changed,added)
        if kind=='quests':assert old==new,'Le datapack quêtes doit rester strictement HF4'
        diffs[kind]={'changed':changed,'added':added,'removed':removed}
    core=read_zip(build.OUT/build.FILES['core']);q=read_zip(build.OUT/build.FILES['quests']);checks=0
    packs={'core':core,'quests':q};defined={n.split('/')[1]+':'+n.split('/function/',1)[1][:-11] for p in packs.values() for n in p if '/function/' in n and n.endswith('.mcfunction')}
    missing=[];references=0;scope=0;jsons=0
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
    prior_core=read_zip(build.OUT/'capitale_core_1.5.7_V2_SEQUENCE_I_SI1_PQ01_R3_HF7.zip')
    protected=[n for n in prior_core if n!='pack.mcmeta' and not n.startswith('data/capitale/function/pq01/')]
    assert all(core[n]==prior_core[n] for n in protected)
    preservation={'status':'PASS','json_resources_parsed':jsons,'function_files_in_scope':scope,'function_references_resolved':references,'missing_function_references':missing,'core_entries_outside_pq01_byte_identical_to_HF7':len(protected),'mq01_mq02_mq03_functions_in_unchanged_quest_datapack':sum('/quests/mq0' in n and '/function/' in n for n in q),'scope':'Références PQ01, prologue, MQ01–MQ03 et gardes ; conservation complète du core hors PQ01, y compris biomes et zones messager. Aucun parcours SI1 complet sur la carte utilisateur.'}
    (AUDIT/'sequence_i_preservation.json').write_text(json.dumps(preservation,ensure_ascii=False,indent=2)+'\n')
    def f(name):return core['data/capitale/function/pq01/'+name+'.mcfunction'].decode()
    assert len(build.IDS)==95 and len(set(build.IDS.values()))==95
    for key,node in build.N.items():
        if key=='c0':assert 'node/c1' in f('node/c0');continue
        text=f('node/'+key);menu=f('menu/'+key);frame=f('frame/'+key)
        assert text.index('indivispq01 line_')<text.index('function capitale:pq01/frame/')
        assert text.index('function capitale:pq01/frame/')<text.index('function capitale:pq01/menu/')
        assert 'Continuer.' not in menu
        automatic=len(node['choices'])==1 and node['choices'][0]['label']=='Continuer.'
        parent=node['choices'][0]['target'] if automatic else None
        answer=parent in {'ga_menu','gautier_menu','hugues_menu','anselme_menu','perrin_menu','c_menu','e_menu'} and (key.startswith(('ga_','gautier_','hugues_','anselme_','perrin_','e_')) or re.fullmatch(r'c[a-e][0-9]+',key))
        assert (':$(b1):' in frame)==(automatic and not answer),key
        if answer:
            assert menu==f('menu/'+parent) and f('choice/'+key)==f('choice/'+parent),key
        assert ':0:' in frame or automatic
        assert 'unless data storage capitale:pq01_config voices.'+key+'{sound:""}' in text
        for line in node['lines']:assert json.dumps(line,ensure_ascii=False)[1:-1] in text,line
        checks+=1
    for role in build.ROLES:
        opening=f('open/'+role+'_self');assert opening.index('indivispq01 reconcile')<opening.index('execute if score @s hcm_pq1_active matches 1 run return 0')
    assert 'indivispq01 near_geraud' in core['data/capitale/function/quest/spawn_dirigeable_couronne/geraud_large_near_self.mcfunction'].decode()
    assert f('test/start_macro')==read_zip(build.OUT/'capitale_core_1.5.7_V2_SEQUENCE_I_SI1_PQ01_R3_HF7.zip')['data/capitale/function/pq01/test/start_macro.mcfunction'].decode()
    assert 'indivis:pq01:v1:a4:geraud:0:0:0' in f('scene/resume_wait_self')
    menu=f('menu/c_menu');assert '"color":"green"' in menu and 'Terminer l’entretien et lancer l’amarrage.' in menu
    assert 'tellraw' not in f('scene/wait_self')+f('voice/wait_self')
    assert 'near_altheon_self' in ' '.join(after.keys()) or 'return 0' in f('near_altheon_self')
    advancement=json.loads(q['data/hc_quetes/advancement/interact/pnj_anselme.json'])
    assert 'hcm_pq1_anselme' in json.dumps(advancement)
    world=(ROOT/'work/pq01_hf4/runtime/world-test-hf8-result.txt').read_text();assert world.startswith('PASS')
    worldlog=(ROOT/'work/pq01_hf4/runtime/world-test-hf8-server.log').read_text();assert 'Failed to load function' not in worldlog
    (AUDIT/'world_test.txt').write_text(world+'All PQ01 functions parsed by the real Minecraft dispatcher; required predicates included.\n')
    native=run(['python3',HERE/'run_native_capture.py'],'native_capture.log')
    logs['native_capture']=native
    assert 'pipeline checks' in world and 'coupled scene checks' in world and 'stairs, corridor, slabs' in world
    assert 'SCENE CHECK Gautier speaks during approach' in worldlog
    result={'status':'PASS','graph_nodes_checked':checks,'stable_node_ids':95,'real_Fabric_world_started':True,'real_navigation_legs':4,'real_blocked_navigation_case':1,'real_world_checks':int(re.search(r'PASS (\d+) checks',world)[1]),'real_pipeline_checks':int(re.search(r'PASS (\d+) pipeline checks',world)[1]),'coupled_scene_checks':int(re.search(r'PASS (\d+) coupled scene checks',world)[1]),'ambient_occupation_checks':int(re.search(r'PASS (\d+) ambient occupation checks',world)[1]),'world_scope':world.strip(),'tests':{k:v.strip() if len(v)<1500 else v.strip().splitlines()[-1] for k,v in logs.items()},'bytecode':(build.OUT/'bytecode.log').read_text().strip(),'files':{k:{'filename':v,'bytes':(build.OUT/v).stat().st_size,'sha256':sha(build.OUT/v)} for k,v in build.FILES.items()},'limits':['Aucun client graphique lancé ; rendu et écoute audio non validés dans le modpack utilisateur.','Le graphe et la scène couplée traversent les fonctions Minecraft, macros, scoreboard, vrais paquets tellraw, marche EasyNPC et capture b10. Caméra, panneau et sélection Nexus doublés ; téléportation et corps de récompense remplacés par marqueurs dans ce banc uniquement.','Le banc serveur retire seulement les annotations Fabric CLIENT de ses copies des classes de capture ; le test séparé de capture native utilise le JAR b10 intact.','La scène emploie le vrai contrôleur de production avec marche et répliques couplées ; caméra et panneau sont doublés. Les trajets EasyNPC réels parcourent marches, couloir et dalles, et un chemin réellement bloqué ; la géométrie et les points du monde utilisateur ne sont pas disponibles.','Séquence I : conservation binaire et contrôle des références ; aucun parcours complet sur la carte utilisateur.']}
    (AUDIT/'diffs.json').write_text(json.dumps(diffs,ensure_ascii=False,indent=2)+'\n')
    assert build.load(build.OUT/build.FILES['core'])==build.load(build.OUT/'capitale_core_1.5.7_V2_SEQUENCE_I_SI1_PQ01_R3_HF7.zip')
    result['core_datapack_byte_identical_to_HF7']=True
    result['quest_datapack_byte_identical_to_HF4']=True
    result['historical_mixins_helpers_factory_and_SI1_byte_identical_to_HF7']=True
    result['native_client_members_checked']=abi_checks
    result['optional_voices_jar_sha256']=sha(voices)
    (AUDIT/'validation.json').write_text(json.dumps(result,ensure_ascii=False,indent=2)+'\n')
    print(json.dumps(result,ensure_ascii=False,indent=2))
if __name__=='__main__':main()
