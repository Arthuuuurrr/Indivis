from pathlib import Path
import subprocess,zipfile,json,os
entry=os.environ.get('HARNESS_ENTRY','ActualPlayerRegression')
cp='pre26/classes:'+Path('pre26/compile-classpath.txt').read_text().strip()+':'+':'.join(str(p) for p in Path('pre26').glob('*.jar'))+':'+':'.join(str(p) for p in Path('runtime/game/mods').glob('*.jar'))
sources=[str(p) for p in Path('pre26/tests').glob('*.java') if p.name not in ('ClothingProfileRegression.java','HairProfileRegression.java')]
subprocess.run(['jdk21/bin/javac','-proc:none','-cp',cp,'-d','pre26/harness',*sources],check=True)
jar=Path('runtime/game/mods/preview-harness.jar');tmp=jar.with_suffix('.next')
with zipfile.ZipFile(tmp,'w') as z:
 mod={'schemaVersion':1,'id':'preview_harness','version':'24','environment':'client','entrypoints':{'client':[entry]}}
 if entry=='ClothingComparison':
  mod['mixins']=['pre24-comparison.mixins.json']
  z.writestr('pre24-comparison.mixins.json',json.dumps({'required':True,'package':'pre24test','compatibilityLevel':'JAVA_21','client':['ClothingComparisonMixin']}))
 z.writestr('fabric.mod.json',json.dumps(mod))
 for p in Path('pre26/harness').rglob('*.class'):z.write(p,p.relative_to('pre26/harness'))
 assets={}
 for root in ('pre20/test-assets','pre26/test-assets','pre26/fixture-assets'):
  for p in Path(root).rglob('*'):
   if p.is_file() and p.suffix in ('.png','.json'):assets[p.relative_to(root).as_posix()]=p
 for name,p in assets.items():z.write(p,name)
os.replace(tmp,jar)
print('HARNESS_COMPILED entry='+entry)
