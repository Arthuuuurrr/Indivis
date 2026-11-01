"""HF9 : patch limité du JAR HF8 installé. Aucun datapack n'est reconstruit."""
from pathlib import Path
import hashlib,json,zipfile,subprocess,os,shutil
import build,model
H=Path(__file__).resolve().parent;RT=H/'runtime_hf9';OUT=H/'output'
BASE=OUT/'haute-capitale-quetes-HF12-PQ02-R1-HF8-1.21.11.jar'
DEST='haute-capitale-quetes-HF12-PQ02-R1-HF9-1.21.11.jar'
CORE='capitale_core_1.5.7_V2_SEQUENCE_I_SI1_PQ01_R3_HF9_QMARK2_PQ02_R1_HF8.zip'
QUESTS='hc_quetes_b22_V2_SEQUENCE_I_SI1b_PQ01_R3_HF4_QMARK2_PQ02_R1_HF8_FUSION_C1.zip'
def sha(p):return hashlib.sha256(p.read_bytes()).hexdigest()
def main():
 assert sha(BASE)=='92dbafab347b1fd775de6476d36e75b6bae2ebefbcc574bfaac141fc63c79180'
 assert sha(OUT/CORE)=='6ff671f7b71ff696980b1fdfa4302481dec30cac7db8eca6e0fb2c9bcd4a9aa6'
 source=H/'hf9_inputs'/QUESTS;assert sha(source)=='d9a0fb0d0aecd0eb4f9bc64f9cb7bae932ddaaa7f67273b042512c22ff9e67aa'
 shutil.copyfile(source,OUT/QUESTS)
 java=next((RT/'jdk').glob('*/bin/java'));mc=RT/'bootstrap/.fabric/remappedJars/minecraft-1.21.11-0.19.5/server-intermediary.jar'
 dialogue=build.ROOT/'upload/haute-capitale-dialogue-0.1.0+1.21.11.b10-DATAPACK-COMMAND-HOTFIX(1).jar'
 libs=[*sorted((RT/'libraries').glob('*.jar')),*sorted((RT/'minecraft_bundle/META-INF/libraries').rglob('*.jar')),mc,BASE,dialogue]
 classes=H/'classes_hf9';shutil.rmtree(classes,ignore_errors=True);classes.mkdir()
 sources=[H/'src'/n for n in ['Pq02Motion.java','Pq02Cameras.java','Pq02Runtime.java']]
 r=subprocess.run([java.with_name('javac'),'--release','21','-proc:none','-sourcepath','','-cp',os.pathsep.join(map(str,libs)),'-d',classes,*sources],capture_output=True,text=True)
 (H/'reports/hf9_compile.log').write_text(r.stdout+r.stderr);assert r.returncode==0,r.stderr
 old=build.read(BASE);result=old.copy()
 for p in classes.rglob('*.class'):result[p.relative_to(classes).as_posix()]=p.read_bytes()
 result['indivis_pq02_graph.json']=(json.dumps(model.export(),ensure_ascii=False,indent=2)+'\n').encode()
 meta=json.loads(old['fabric.mod.json']);meta['version']=meta['version'].replace('PQ02.R1.HF8','PQ02.R1.HF9')
 meta['description']+=' HF9 : pauses de lore D/H avec questions facultatives, marche plus rapide et continue, cadrages du magistrat centrés ; datapacks HF8/Fusion C1 inchangés.'
 result['fabric.mod.json']=(json.dumps(meta,ensure_ascii=False,indent=2)+'\n').encode();build.write(OUT/DEST,result)
 changed=sorted(n for n in old if result[n]!=old[n]);added=sorted(set(result)-set(old));preserved=sorted(n for n in old if result[n]==old[n])
 allowed={'fabric.mod.json','indivis_pq02_graph.json'}|{p.relative_to(classes).as_posix() for p in classes.rglob('*.class')}
 assert set(changed+added)<=allowed and all('pq01' not in n.lower() for n in changed+added)
 report={'status':'BUILT_NATIVE_PENDING','file':DEST,'sha256':sha(OUT/DEST),'bytes':(OUT/DEST).stat().st_size,'hf8_sha256':sha(BASE),'changed':changed,'added':added,'preserved_count':len(preserved),'datapacks_unchanged':{k:sha(OUT/k) for k in [CORE,QUESTS]},'source_sha256':{str(p.relative_to(H)):sha(p) for p in sources+[H/'model.py']}}
 (H/'reports/hf9_build.json').write_text(json.dumps(report,ensure_ascii=False,indent=2)+'\n');print(json.dumps(report,ensure_ascii=False,indent=2))
if __name__=='__main__':main()
