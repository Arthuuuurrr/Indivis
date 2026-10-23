from pathlib import Path
import json,os,subprocess,zipfile
from build import HERE,NAME

def main():
    out=HERE/'audit';out.mkdir(exist_ok=True);classes=HERE/'build/tests';classes.mkdir(parents=True,exist_ok=True)
    jar=HERE/'dist'/NAME
    def run(args,log):
        p=subprocess.run(args,cwd=HERE,capture_output=True,text=True);(out/log).write_text(p.stdout+p.stderr);print(p.stdout+p.stderr,end='')
        assert p.returncode==0,log
    run(['java','--module','jdk.compiler/com.sun.tools.javac.Main','--release','17','-cp',str(jar),'-d',str(classes),*map(str,sorted((HERE/'tests/java').rglob('*.java')))],'compile-tests.log')
    cp=str(classes)+os.pathsep+str(jar)
    for name in ('ControllerBench','AdapterBench'):run(['java','-cp',cp,'net.indivis.admin.'+name],name+'.log')
    with zipfile.ZipFile(jar) as z:
        assert z.testzip() is None
        metadata=json.loads(z.read('fabric.mod.json'));assert metadata['id']=='indivis_admin'
        assert metadata['depends']['minecraft']=='1.21.11'
        assert metadata['mixins']==['indivis_admin.mixins.json']
        assert all(not n.startswith(('net/minecraft/','net/fabricmc/','net/tompsen/','org/spongepowered/')) and 'Bench' not in n for n in z.namelist())
        mixins=json.loads(z.read('indivis_admin.mixins.json'));assert mixins['mixins']==['NexusProfileSaveMixin']
        assert mixins['injectors']['defaultRequire']==1
        tag=json.loads(z.read('data/minecraft/tags/function/load.json'));assert tag['values']==['indivis_admin:profile/load']
        rows=[l.split('\t') for l in z.read('admin_actions.tsv').decode().splitlines()]
        assert len(rows)==148 and len({r[3] for r in rows})==148
    run(['java','--module','jdk.compiler/com.sun.tools.javac.Main','--add-exports','java.base/jdk.internal.org.objectweb.asm=ALL-UNNAMED','--add-exports','java.base/jdk.internal.org.objectweb.asm.tree=ALL-UNNAMED','--add-exports','java.base/jdk.internal.org.objectweb.asm.tree.analysis=ALL-UNNAMED','-d',str(HERE/'build/tools'),str(HERE/'tools/VerifyClasses.java')],'compile-verifier.log')
    run(['java','--add-exports','java.base/jdk.internal.org.objectweb.asm=ALL-UNNAMED','--add-exports','java.base/jdk.internal.org.objectweb.asm.tree=ALL-UNNAMED','--add-exports','java.base/jdk.internal.org.objectweb.asm.tree.analysis=ALL-UNNAMED','-cp',str(HERE/'build/tools'),'VerifyClasses',str(HERE/'build/classes')],'bytecode.log')
    summary={'status':'CANDIDAT_A_TESTER_EN_JEU','minecraft_fabric_tested':False,'catalogue_actions':148,
      'controller_bench':(out/'ControllerBench.log').read_text().strip(),'adapter_bench':(out/'AdapterBench.log').read_text().strip(),
      'bytecode':(out/'bytecode.log').read_text().strip(),'jar_crc':'OK','no_test_or_minecraft_classes_shipped':True,'nexus_presave_compatibility_mixin':True}
    (out/'validation.json').write_text(json.dumps(summary,ensure_ascii=False,indent=2)+'\n');print(json.dumps(summary,ensure_ascii=False))

if __name__=='__main__':main()
