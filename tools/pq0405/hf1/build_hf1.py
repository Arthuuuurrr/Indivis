"""Rebuild the Colin HF1 from the pinned complete R1 archive and these sources."""
from pathlib import Path
import argparse,hashlib,json,os,shutil,subprocess,zipfile

H=Path(__file__).resolve().parent
MOD='haute-capitale-quetes-PQ04-PQ05-R1-1.21.11.jar'
OUTPUT='haute-capitale-quetes-PQ04-PQ05-R1-HF1-1.21.11.jar'
R1_MOD_SHA256='f75eab9c6113d131480398109a2d17c31298ad7677d6ebe4dcca833d7ca53ccf'
R1_ARCHIVE_SHA256='44736b5b414788de120b6f10db9cd1089d4ae8cd23f1cc1afa98dcfd33aa3746'

def sha(body):return hashlib.sha256(body).hexdigest()
def write(path,files):
    path.parent.mkdir(parents=True,exist_ok=True)
    with zipfile.ZipFile(path,'w',zipfile.ZIP_DEFLATED,compresslevel=9) as z:
        for name,body in sorted(files.items()):
            entry=zipfile.ZipInfo(name,(2026,11,10,0,0,0))
            entry.compress_type=zipfile.ZIP_DEFLATED
            entry.external_attr=0o644<<16
            z.writestr(entry,body)

def main():
    parser=argparse.ArgumentParser()
    parser.add_argument('--r1',type=Path,required=True,help='Complete PQ04/PQ05 R1 installation ZIP')
    parser.add_argument('--workdir',type=Path,default=H.parent/'colin_hf1_work')
    parser.add_argument('--test',action='store_true')
    args=parser.parse_args()
    work=args.workdir.resolve()
    if sha(args.r1.read_bytes())!=R1_ARCHIVE_SHA256:raise ValueError('Unexpected complete R1 archive')
    with zipfile.ZipFile(args.r1) as z:
        original=z.read('serveur_et_clients/mods/'+MOD)
        if sha(original)!=R1_MOD_SHA256:raise ValueError('Unexpected R1 quest JAR; refusing a different base')
        z.extractall(work/'r1')
    with zipfile.ZipFile(work/'r1/sources/Indivis_PQ04_PQ05_R1_Sources.zip') as z:z.extractall(work/'sources')
    runtime=work/'sources/tools/pq02/runtime_hf9'
    mc=runtime/'bootstrap/.fabric/remappedJars/minecraft-1.21.11-0.19.5/server-intermediary.jar'
    if not mc.exists():
        subprocess.run(['python3',work/'sources/tools/pq0405/prepare_runtime.py'],check=True)
        subprocess.run(['python3',work/'sources/tools/pq02/bootstrap_hf9.py'],check=True)
    classes=H/'classes'
    shutil.rmtree(classes,ignore_errors=True)
    classes.mkdir()
    reports=H/'reports'
    reports.mkdir(exist_ok=True)
    cp=os.pathsep.join(map(str,[work/'r1/serveur_et_clients/mods'/MOD,mc,*sorted((runtime/'libraries').glob('*.jar')),*sorted((runtime/'minecraft_bundle/META-INF/libraries').rglob('*.jar'))]))
    result=subprocess.run(['java','--module','jdk.compiler/com.sun.tools.javac.Main','-source','21','-target','21','-proc:none','-cp',cp,'-d',classes,H/'src/Pq45Runtime.java'],capture_output=True,text=True)
    (reports/'compile.log').write_text(result.stdout+result.stderr)
    if result.returncode:raise RuntimeError(result.stderr)
    with zipfile.ZipFile(work/'r1/serveur_et_clients/mods'/MOD) as z:before={n:z.read(n) for n in z.namelist()}
    after=dict(before)
    for f in classes.rglob('*.class'):after[f.relative_to(classes).as_posix()]=f.read_bytes()
    metadata=json.loads(after['fabric.mod.json'])
    metadata['version']+='.HF1'
    metadata['description']+=' PQ04/PQ05-R1-HF1 : liaison de Colin par identité/UUID et entrée de quête au clic.'
    after['fabric.mod.json']=(json.dumps(metadata,ensure_ascii=False,indent=2)+'\n').encode()
    changed=[n for n in before if before[n]!=after[n]]
    added=sorted(set(after)-set(before))
    allowed=lambda n:n=='fabric.mod.json' or n.startswith('net/hautecapitale/quetes/sequence/Pq45Runtime') and n.endswith('.class')
    assert all(allowed(n) for n in changed+added)
    target=H/'output'/OUTPUT
    write(target,after)
    report={'base_mod_sha256':sha(original),'output_filename':OUTPUT,'output_sha256':sha(target.read_bytes()),'output_bytes':target.stat().st_size,'changed_entries':sorted(changed),'added_entries':added,'removed_entries':[],'unchanged_entries':sum(before[n]==after[n] for n in before),'datapacks':'R1 bytes unchanged','scope':'Selection and public-click routing; R1 dialogue graph, economy, checkpoints and motion preserved'}
    (reports/'manifest_hf1.json').write_text(json.dumps(report,ensure_ascii=False,indent=2)+'\n')
    print(json.dumps(report,ensure_ascii=False,indent=2))
    if args.test:
        subprocess.run(['python3',H/'run_native.py'],check=True,env={**os.environ,'INDIVIS_PQ45_WORK':str(work)})

if __name__=='__main__':main()
