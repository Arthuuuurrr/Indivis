"""Régression du crash Mixin : lecteur de production, sans lancer Minecraft."""
from pathlib import Path
import argparse, hashlib, json, os, subprocess, urllib.request

HERE=Path(__file__).resolve().parent

def main():
    parser=argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--old-journal',type=Path,required=True)
    parser.add_argument('--journal',type=Path,required=True)
    parser.add_argument('--old-admin',type=Path,required=True)
    parser.add_argument('--admin',type=Path,required=True)
    args=parser.parse_args()
    deps=HERE/'dependencies';deps.mkdir(exist_ok=True)
    manifest=json.loads((HERE/'dependencies.json').read_text())
    for item in manifest:
        path=deps/item['file']
        if not path.exists() or hashlib.sha256(path.read_bytes()).hexdigest()!=item['sha256']:
            with urllib.request.urlopen(item['url'],timeout=45) as response:
                data=response.read()
            if len(data)!=item['bytes'] or hashlib.sha256(data).hexdigest()!=item['sha256']:
                raise ValueError('Dependency integrity: '+item['file'])
            path.write_bytes(data)
    cp=os.pathsep.join(str(deps/item['file']) for item in manifest)
    classes=HERE/'test_classes';classes.mkdir(exist_ok=True)
    def run(command,log):
        p=subprocess.run(list(map(str,command)),capture_output=True,text=True)
        output=p.stdout+p.stderr;(HERE/log).write_text(output)
        print(output,end='')
        if p.returncode:raise RuntimeError(log)
    run(['java','--module','jdk.compiler/com.sun.tools.javac.Main','-proc:none','-cp',cp,'-d',classes,HERE/'VerifyMixinAnnotations.java',HERE/'CompareMixinCode.java'],'compile-regression.log')
    cp=str(classes)+os.pathsep+cp
    run(['java','-cp',cp,'VerifyMixinAnnotations',2,args.old_journal,1,args.old_admin,0,args.journal,0,args.admin],'annotation_regression.log')
    logs=[]
    for old,new,names in [
        (args.old_journal,args.journal,['net/hautecapitale/quetes/pq01/mixin/Pq01EntityMixin.class','net/hautecapitale/quetes/pq01/mixin/Pq01GoalMixin.class']),
        (args.old_admin,args.admin,['net/indivis/admin/mixin/NexusProfileSaveMixin.class'])]:
        run(['java','-cp',cp,'CompareMixinCode',old,new,*names],'comparison-part.log')
        logs.append((HERE/'comparison-part.log').read_text())
    (HERE/'mixin_code_comparison.log').write_text(''.join(logs))

if __name__=='__main__':main()
