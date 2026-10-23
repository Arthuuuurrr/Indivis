"""Banc NBT/EasyNPC réel, audit des API et inventaire strict HF1 -> HF2."""
from pathlib import Path
import argparse,hashlib,json,os,re,subprocess,zipfile
import build

HERE=build.HERE
def sha(p):return hashlib.sha256(p.read_bytes()).hexdigest()
def read_zip(p):
    with zipfile.ZipFile(p) as z:
        assert z.testzip() is None
        return {n:z.read(n) for n in z.namelist() if not n.endswith('/')}

def main():
    p=argparse.ArgumentParser(description=__doc__)
    p.add_argument('--java21',required=True,type=Path)
    p.add_argument('--minecraft',required=True,type=Path)
    p.add_argument('--libraries',required=True,type=Path)
    p.add_argument('--easynpc',required=True,type=Path)
    p.add_argument('--dialogue',required=True,type=Path)
    p.add_argument('--baseline',type=Path,default=build.OUT/'haute-capitale-quetes-0.1.0+1.21.11.b7-SEQUENCE-I-SI1b-PQ01-R3-HF1.jar')
    p.add_argument('--audit',type=Path,default=build.OUT/'hf2_audit')
    args=p.parse_args();args.audit.mkdir(parents=True,exist_ok=True)
    classes=args.audit/'tests';classes.mkdir(exist_ok=True)
    jar=build.OUT/build.FILES['journal']
    def run(cmd,log):
        r=subprocess.run(list(map(str,cmd)),capture_output=True,text=True,timeout=60)
        text=r.stdout+r.stderr;(args.audit/log).write_text(text)
        if r.returncode:raise RuntimeError(log+'\n'+text)
        return text
    cp=os.pathsep.join(map(str,[HERE/'contract_classes',HERE/'classes']))
    run(['java','--module','jdk.compiler/com.sun.tools.javac.Main','-proc:none','-cp',cp,'-d',classes,HERE/'tests/integration/Pq01NbtIntegrationBench.java',HERE/'tests/integration/Pq01NativeApiBench.java',HERE/'tests/Pq01MotionBench.java'],'compile_tests.log')
    libraries=sorted(args.libraries.rglob('*.jar'));assert libraries,'Bibliothèques du bundle serveur requises'
    cp=os.pathsep.join(map(str,[classes,jar,args.minecraft,*libraries,args.easynpc,args.dialogue]))
    nbt=run([args.java21,'-cp',cp,'net.hautecapitale.quetes.sequence.Pq01NbtIntegrationBench'],'nbt_integration.log')
    api=run([args.java21,'-cp',cp,'net.hautecapitale.quetes.sequence.Pq01NativeApiBench',HERE/'java/net/hautecapitale/quetes/sequence/Pq01Bridge.java'],'native_api.log')
    motion=run([args.java21,'-cp',cp,'Pq01MotionBench'],'motion.log')
    before=read_zip(args.baseline);after=read_zip(jar);changed=sorted(n for n in before if before[n]!=after[n]);added=sorted(set(after)-set(before));removed=sorted(set(before)-set(after))
    assert not removed
    assert all(n=='fabric.mod.json' or n.startswith('net/hautecapitale/quetes/sequence/Pq01Bridge') for n in changed),changed
    assert set(added)=={'net/hautecapitale/quetes/sequence/Pq01Actions.class','net/hautecapitale/quetes/sequence/Pq01Nbt.class'},added
    assert all(before[n]==after[n] for n in before if '/mixin/' in n or n.endswith('/GuardNbt.class') or n.endswith('/SequenceBridge.class'))
    assert not any(n.startswith(('org/spongepowered/','net/minecraft/','net/fabricmc/','de/markusbordihn/')) for n in added)
    diffs={'baseline':args.baseline.name,'baseline_sha256':sha(args.baseline),'output':jar.name,'output_sha256':sha(jar),'bytes':jar.stat().st_size,'changed':changed,'added':added,'removed':removed,'identical_entries':sum(before[n]==after[n] for n in before),'historical_helpers_and_all_mixin_classes_byte_identical':True}
    (args.audit/'diffs.json').write_text(json.dumps(diffs,ensure_ascii=False,indent=2)+'\n')
    expected={'core':'a958d8b34edc558e5ba21309c0295c43f3a03dc6c3618f28eb14c448ab60e91c','quests':'f48bbecb9afcadbb1b74134646dde0f0cd2d12512171778f785da25d83f0fb47'}
    assert all(sha(build.OUT/build.FILES[k])==value for k,value in expected.items())
    result={'status':'PASS','minecraft_server_or_world_started':False,'real_minecraft_nbt_tested':True,'real_easynpc_action_data_and_registry_tested':True,'entity_storage_double_used':True,'nbt_assertions':int(re.search(r'PASS (\d+) assertions',nbt)[1]),'native_api_public_member_lookups':int(re.search(r'checks=(\d+)',api)[1]),'api_lookup_scope':'existence, visibilité publique et arité ; signatures publiées dans le log ; pas de test de comportement des méthodes de monde','motion':motion.strip(),'bytecode':(build.OUT/'bytecode.log').read_text().strip(),'datapacks_R3_byte_identical':True,'diffs':diffs,'inputs':{name:{'filename':path.name,'bytes':path.stat().st_size,'sha256':sha(path)} for name,path in [('minecraft_intermediary',args.minecraft),('easynpc',args.easynpc),('dialogue',args.dialogue)]},'limits':['Aucun serveur Fabric complet ni copie du monde du joueur démarré.','Le stockage d’entité est doublé dans le banc de mutation ; les conteneurs et sérialiseurs EasyNPC sont réels.','Navigation, caméra, interactions réseau et coexistence du modpack restent à observer en jeu.']}
    (args.audit/'validation.json').write_text(json.dumps(result,ensure_ascii=False,indent=2)+'\n')
    print(json.dumps({k:v for k,v in result.items() if k not in ('diffs','inputs','limits')},ensure_ascii=False,indent=2))
    return result

if __name__=='__main__':main()
