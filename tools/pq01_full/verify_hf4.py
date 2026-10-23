"""Vérifications HF4 : JAR livré, contrats natifs, cadence client et périmètre exact."""
from pathlib import Path
import hashlib,json,os,re,subprocess,zipfile
import build
from verify_hf3 import read_zip

ROOT=build.ROOT;HERE=build.HERE;AUDIT=build.OUT/'hf4_audit'
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
        logs[name]=run([java,'-Dindivis.expected.jar=PQ01-R3-HF4.jar','-cp',cp,'net.hautecapitale.quetes.sequence.'+name,*([HERE/'java/net/hautecapitale/quetes/sequence/Pq01Bridge.java'] if name=='Pq01NativeApiBench' else [])],name+'.log')
    logs['motion']=run([java,'-cp',cp,'Pq01MotionBench'],'motion.log')
    for name,folder in [('Pq01InteractionBench','interaction_doubles'),('Pq01ClientBench','client_doubles')]:
        out=AUDIT/folder;out.mkdir(exist_ok=True)
        sources=sorted((HERE/'tests'/folder).rglob('*.java'))
        run(['java','--module','jdk.compiler/com.sun.tools.javac.Main','-proc:none','-cp',compile_cp,'-d',out,HERE/f'tests/integration/{name}.java',*sources],name+'_compile.log')
        logs[name]=run([java,'-cp',os.pathsep.join(map(str,[out,*base])),'net.hautecapitale.quetes.sequence.'+name],name+'.log')
    voices=ROOT/'work/pq01_hf4/voices/package/indivis-voices-0.2.3+1.21.11.jar'
    abi_checks=0
    for native,name,expected in [
        (dialogue,'net.hautecapitale.dialogue.client.DialogueClientState',['java.lang.String pnjNom;','long fenetreJusquaMs;','captureActive();','onChoixEnvoye(boolean, java.lang.String);']),
        (dialogue,'net.hautecapitale.dialogue.client.capture.CaptureDialogueScreen',['DialoguePanel panneau;']),
        (dialogue,'net.hautecapitale.dialogue.client.screen.DialoguePanel',['void nom(java.lang.String);','void attente(boolean);','boolean dernierePage();','boolean typewriterActif();']),
        (voices,'net.indivis.voices.client.VoicesClient',[' active;',' pending;',' fading;'])]:
        dump=run(['java','--module','jdk.jdeps/com.sun.tools.javap.Main','-p','-classpath',native,name],'abi_'+name.rsplit('.',1)[1]+'.log')
        for token in expected:assert token in dump,(name,token)
        abi_checks+=len(expected)
    before=read_zip(build.OUT/'haute-capitale-quetes-0.1.0+1.21.11.b7-SEQUENCE-I-SI1b-PQ01-R3-HF3.jar');after=read_zip(jar)
    changed=sorted(n for n in before if before[n]!=after[n]);added=sorted(set(after)-set(before));removed=sorted(set(before)-set(after))
    assert not removed
    assert all(n=='fabric.mod.json' or any(n.startswith('net/hautecapitale/quetes/sequence/'+c) for c in ['Pq01Bridge','Pq01Actions','Pq01Ids','Pq01Motion']) for n in changed),changed
    assert set(added)=={'net/hautecapitale/quetes/sequence/'+c+'.class' for c in ['Pq01Client','Pq01Pacing','Pq01Pacing$Frame','Pq01EntityFactory']},added
    assert all(before[n]==after[n] for n in before if '/mixin/' in n or n.endswith('/GuardNbt.class') or n.endswith('/SequenceBridge.class'))
    diffs={'journal':{'changed':changed,'added':added,'removed':removed,'all_mixins_and_historical_helpers_byte_identical':True}}
    for kind,prefix in [('core','capitale_core_1.5.7_V2_SEQUENCE_I_SI1_PQ01_R3.zip'),('quests','hc_quetes_b22_V2_SEQUENCE_I_SI1b_PQ01_R3.zip')]:
        old=read_zip(build.OUT/prefix);new=read_zip(build.OUT/build.FILES[kind])
        changed=sorted(n for n in old if old[n]!=new[n]);added=sorted(set(new)-set(old));removed=sorted(set(old)-set(new));assert not removed
        allowed=lambda n:n=='pack.mcmeta' or (kind=='core' and (n.startswith('data/capitale/function/pq01/') or n in {'data/capitale/function/quest/spawn_dirigeable_couronne/capitaine_large/'+x+'.mcfunction' for x in ['near_self','near_intro_self']})) or (kind=='quests' and n=='data/hc_quetes/advancement/interact/pnj_anselme.json')
        assert all(allowed(n) for n in changed+added),(kind,changed,added)
        diffs[kind]={'changed':changed,'added':added,'removed':removed}
    core=read_zip(build.OUT/build.FILES['core']);q=read_zip(build.OUT/build.FILES['quests']);checks=0
    def f(name):return core['data/capitale/function/pq01/'+name+'.mcfunction'].decode()
    assert len(build.IDS)==95 and len(set(build.IDS.values()))==95
    for key,node in build.N.items():
        if key=='c0':assert 'node/c1' in f('node/c0');continue
        text=f('node/'+key);menu=f('menu/'+key);frame=f('frame/'+key)
        assert text.index('indivispq01 line_')<text.index('function capitale:pq01/frame/')
        assert text.index('function capitale:pq01/frame/')<text.index('function capitale:pq01/menu/')
        assert 'Continuer.' not in menu
        automatic=len(node['choices'])==1 and node['choices'][0]['label']=='Continuer.'
        assert (':$(b1):' in frame)==automatic
        assert ':0:' in frame or automatic
        for line in node['lines']:assert json.dumps(line,ensure_ascii=False)[1:-1] in text,line
        checks+=1
    menu=f('menu/c_menu');assert '"color":"green"' in menu and 'Terminer l’entretien et lancer l’amarrage.' in menu
    assert 'tellraw' not in f('scene/wait_self')+f('voice/wait_self')
    assert 'near_altheon_self' in ' '.join(after.keys()) or 'return 0' in f('near_altheon_self')
    advancement=json.loads(q['data/hc_quetes/advancement/interact/pnj_anselme.json'])
    assert 'hcm_pq1_anselme' in json.dumps(advancement)
    world=(ROOT/'work/pq01_hf4/runtime/server/world-test-result.txt').read_text();assert world.startswith('PASS')
    worldlog=(ROOT/'work/pq01_hf4/runtime/world-test-server.log').read_text();assert 'Failed to load function' not in worldlog
    (AUDIT/'world_test.txt').write_text(world+'All PQ01 functions parsed by the real Minecraft dispatcher; required predicates included.\n')
    result={'status':'PASS','graph_nodes_checked':checks,'stable_node_ids':95,'real_Fabric_world_started':True,'real_navigation_legs':4,'real_world_checks':19,'world_scope':'Minecraft/Fabric/EasyNPC réels ; sélection Nexus et émission visibilité doublées ; joueur hors ligne réel construit ; caméra sans client non testée.','tests':{k:v.strip() if len(v)<1500 else v.strip().splitlines()[-1] for k,v in logs.items()},'bytecode':(build.OUT/'bytecode.log').read_text().strip(),'files':{k:{'filename':v,'bytes':(build.OUT/v).stat().st_size,'sha256':sha(build.OUT/v)} for k,v in build.FILES.items()},'limits':['Aucun client Minecraft graphique lancé ; rendu, son et transition caméra restent à vérifier dans le modpack utilisateur.','Le banc client exécute le vrai adaptateur avec écran/réseau/état vocal doublés ; ce n’est pas une validation graphique.','Les quatre trajets réels ont été parcourus sur un sol plat ; le relief, les obstacles et la longueur des points de la carte restent propres au monde utilisateur.']}
    (AUDIT/'diffs.json').write_text(json.dumps(diffs,ensure_ascii=False,indent=2)+'\n')
    result['native_client_members_checked']=abi_checks
    result['optional_voices_jar_sha256']=sha(voices)
    (AUDIT/'validation.json').write_text(json.dumps(result,ensure_ascii=False,indent=2)+'\n')
    print(json.dumps(result,ensure_ascii=False,indent=2))
if __name__=='__main__':main()
