from pathlib import Path
import subprocess,zipfile,json,os,sys
r=Path('runtime');cp='pre23/classes:'+(r/'classpath.txt').read_text().strip()+':'+str(r/'client-intermediary.jar')+':'+str(r/'fabric-loader.jar')+':'+':'.join(str(p) for p in (r/'game/mods').glob('*.jar'))
with zipfile.ZipFile(r/'game/mods/fabric-api.jar') as z:
 for n in z.namelist():
  if 'fabric-lifecycle-events-v1' in n or 'fabric-api-base-' in n:
   p=Path('pre23')/Path(n).name;p.write_bytes(z.read(n));cp+=':'+str(p)
subprocess.run(['jdk21/bin/javac','-proc:none','-cp',cp,'-d','pre23/harness','pre23/tests/SubmittedGeometryAudit.java','pre23/tests/PoseAudit.java','pre23/tests/ReportedRegression.java','pre23/tests/AccountSkinRegression.java','pre23/tests/ActualPlayerRegression.java','pre23/tests/CatalogRegression.java'],check=True)
target=r/'game/mods/preview-harness.jar';temporary=target.with_suffix('.next')
with zipfile.ZipFile(temporary,'w') as z:
 z.writestr('fabric.mod.json',json.dumps({'schemaVersion':1,'id':'preview_harness','version':'23','environment':'client','entrypoints':{'client':[os.environ.get('HARNESS_ENTRY','ReportedRegression')]}}))
 for p in Path('pre23/harness').rglob('*.class'):z.write(p,p.relative_to('pre23/harness'))
 for p in Path('pre20/test-assets').rglob('*.png'):z.write(p,p.relative_to('pre20/test-assets'))
 for p in Path('pre23/test-assets').rglob('*'):
  if p.is_file():z.write(p,p.relative_to('pre23/test-assets'))
os.replace(temporary,target)
print('HARNESS_COMPILED')
