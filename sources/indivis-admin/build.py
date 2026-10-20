from pathlib import Path
import hashlib,json,shutil,subprocess,zipfile

HERE=Path(__file__).resolve().parent
NAME='indivis-admin-0.1.0-alpha.1+1.21.11.jar'

def run(args):
    p=subprocess.run(args,cwd=HERE,text=True,capture_output=True)
    if p.returncode:raise RuntimeError(p.stdout+p.stderr)
    return p.stdout+p.stderr

def build():
    classes=HERE/'build/classes';tools=HERE/'build/tools'
    shutil.rmtree(HERE/'build',ignore_errors=True);classes.mkdir(parents=True);tools.mkdir()
    run(['java','--module','jdk.compiler/com.sun.tools.javac.Main','--release','17','-d',str(classes),*map(str,sorted((HERE/'src/main/java').rglob('*.java')))])
    run(['java','--module','jdk.compiler/com.sun.tools.javac.Main','--add-exports','java.base/jdk.internal.org.objectweb.asm=ALL-UNNAMED','-d',str(tools),str(HERE/'tools/BuildEntry.java')])
    run(['java','--add-exports','java.base/jdk.internal.org.objectweb.asm=ALL-UNNAMED','-cp',str(tools),'BuildEntry',str(classes)])
    metadata={'schemaVersion':1,'id':'indivis_admin','version':'0.1.0-alpha.1+1.21.11','name':'Indivis — Administration',
      'description':'Gestion ciblée des rangs, criminalité, réputations et accès via les fonctions natives du core. Version candidate à tester en jeu.',
      'authors':['Indivis'],'environment':'*','entrypoints':{'main':['net.indivis.admin.IndivisAdmin']},
      'depends':{'fabricloader':'>=0.18.0','minecraft':'1.21.11','java':'>=21','fabric-api':'*'}}
    data={'fabric.mod.json':(json.dumps(metadata,ensure_ascii=False,indent=2)+'\n').encode()}
    for root in (classes,HERE/'src/main/resources'):
        for p in root.rglob('*'):
            if p.is_file():data[p.relative_to(root).as_posix()]=p.read_bytes()
    out=HERE/'dist'/NAME;out.parent.mkdir(exist_ok=True)
    with zipfile.ZipFile(out.with_suffix('.partial'),'w',zipfile.ZIP_DEFLATED,compresslevel=9) as z:
        for name,content in sorted(data.items()):
            info=zipfile.ZipInfo(name,(2026,10,20,0,0,0));info.compress_type=zipfile.ZIP_DEFLATED;z.writestr(info,content,compresslevel=9)
    with zipfile.ZipFile(out.with_suffix('.partial')) as z:
        assert z.testzip() is None
        assert all(not n.startswith(('net/minecraft/','net/fabricmc/','net/tompsen/')) for n in z.namelist())
    out.with_suffix('.partial').replace(out)
    print(json.dumps({'jar':str(out),'bytes':out.stat().st_size,'sha256':hashlib.sha256(out.read_bytes()).hexdigest()}))
    return out

if __name__=='__main__':build()
