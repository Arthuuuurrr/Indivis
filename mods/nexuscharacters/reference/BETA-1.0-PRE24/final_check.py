import hashlib,json,re,zipfile
from pathlib import Path
from PIL import Image

root=Path(__file__).resolve().parent
e=root/'evidence'
sha=lambda p:hashlib.sha256(p.read_bytes()).hexdigest()
expected='abeb2a1135f9df7fc723adf24c97d2dce589267a420e375aff6148f3d9597f82'
assert sha(root/'candidate.jar')==expected
for folder in ('actual','catalog','preview'):
 log=(e/folder/'verified.log').read_text()
 assert 'ACTUAL_PLAYER_REGRESSION_PASS' in log and 'FAILURE' not in log,folder
 assert all(a=='0' and b=='0' for a,b in re.findall(r'coplanar=(\d+) crossings=(\d+)',log)),folder
 assert json.loads((e/folder/'run.json').read_text())['jar_sha256']==expected,folder
assert 'configurations=57 poses=171' in (e/'catalog/verified.log').read_text()
assert 'cases=12 captures=36 missing=0 overlaps=0' in (e/'actual/verified.log').read_text()
hands=re.findall(r'FIRST_PERSON_CLOTHING_GRID_PASS case=(\d+) part=(\d+) texels=(\d+) maxDisplacementPixels=([\d.]+)',(e/'actual/verified.log').read_text())
assert len(hands)==48 and all(float(x[3])<=.001 for x in hands)
assert 'captures=24 overlaps=0 wide+slim=true' in (e/'preview/verified.log').read_text()
positive=(e/'candidate/verified.log').read_text()
negative=(e/'pre23-native/verified.log').read_text()
for log,failed in ((positive,0),(negative,4)):
 assert 'ACTUAL_PLAYER_REGRESSION_PASS' in log and 'FAILURE' not in log
 assert f'failedGridChecks={failed}' in log
 assert len(re.findall(r'CAPTURE pre24/evidence/',log))==48
 errors=[float(x) for x in re.findall(r'maxDisplacementPixels=([\d.]+)',log)]
 assert len(errors)==4
 assert all(x<=.001 for x in errors) if failed==0 else all(x>.01 for x in errors)
assert json.loads((e/'candidate/run.json').read_text())['jar_sha256']==expected
for folder in ('candidate','pre23-native'):
 for outfit in (1,20,22,23):
  for variant in range(4):
   images=[e/folder/f'outfit-{outfit}-variant-{variant}-angle-{a}.png' for a in range(3)]
   pixels=[hashlib.sha256(Image.open(p).convert('RGB').tobytes()).hexdigest() for p in images]
   assert len(set(pixels))==3,(folder,outfit,variant,'Camera angles duplicated')
assert 'CLOTHING_PROFILE_PASS' in (e/'clothing-profile.txt').read_text()
assets=json.loads((e/'asset-integrity.json').read_text())
assert assets['PRE24_sha256']==expected and assets['assets_checked']==4958 and not assets['assets_changed']
with zipfile.ZipFile(root/'candidate.jar') as z:
 assert z.testzip() is None
 assert json.loads(z.read('fabric.mod.json'))['version']=='1.0.0-beta.24+hc.1.21.11'
 assert not any('ClothingComparison' in n or 'EvidenceLog' in n or 'test-assets' in n for n in z.namelist())
print('PRE24_FINAL_CHECK_PASS 243 poses; 48 arm profiles; 96 comparative captures with three distinct angles; 4958 original assets; exact delivery SHA')
