from pathlib import Path
import subprocess,zipfile,json,os
entry=os.environ.get('HARNESS_ENTRY','ActualPlayerRegression')
cp='pre24/classes:'+Path('pre23/compile-classpath.txt').read_text().strip()+':'+':'.join(str(p) for p in Path('pre23').glob('fabric-*.jar'))
sources=[str(p) for p in Path('pre24/tests').glob('*.java') if p.name not in ('ClothingProfileRegression.java',)]
subprocess.run(['jdk21/bin/javac','-proc:none','-cp',cp,'-d','pre24/harness',*sources],check=True)
jar=Path('runtime/game/mods/preview-harness.jar');tmp=jar.with_suffix('.next')
with zipfile.ZipFile(tmp,'w') as z:
 mod={'schemaVersion':1,'id':'preview_harness','version':'24','environment':'client','entrypoints':{'client':[entry]}}
 if entry=='ClothingComparison':
  mod['mixins']=['pre24-comparison.mixins.json']
  z.writestr('pre24-comparison.mixins.json',json.dumps({'required':True,'package':'pre24test','compatibilityLevel':'JAVA_21','client':['ClothingComparisonMixin']}))
 z.writestr('fabric.mod.json',json.dumps(mod))
 for p in Path('pre24/harness').rglob('*.class'):z.write(p,p.relative_to('pre24/harness'))
 for root in ('pre20/test-assets','pre23/test-assets'):
  for p in Path(root).rglob('*.png'):z.write(p,p.relative_to(root))
os.replace(tmp,jar)
print('HARNESS_COMPILED entry='+entry)
