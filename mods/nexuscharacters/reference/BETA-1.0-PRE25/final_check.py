import sys,json,hashlib,zipfile,base64,io,re
from pathlib import Path
root=Path(__file__).resolve().parent
jar=Path(sys.argv[1]) if len(sys.argv)>1 else root/'candidate.jar'
meta=json.loads((root/'build-manifest.json').read_text())
assert hashlib.sha256(jar.read_bytes()).hexdigest()==meta['jar_sha256']
data=base64.b64decode((root/'compiled-patches.zip.b64').read_bytes())
assert hashlib.sha256(data).hexdigest()==meta['compiled_archive_sha256']
with zipfile.ZipFile(jar) as z,zipfile.ZipFile(io.BytesIO(data)) as patches:
    assert z.testzip() is None
    assert json.loads(z.read('fabric.mod.json'))['version']=='1.0.0-beta.25+hc.1.21.11'
    assert sum(n.startswith('assets/') and not n.endswith('/') for n in z.namelist())==4958
    for name,sha in meta['class_sha256'].items():
        assert hashlib.sha256(patches.read(name)).hexdigest()==sha
        assert z.read(name)==patches.read(name)
for name,sha in meta['source_sha256'].items():
    assert hashlib.sha256((root/'sources'/name).read_bytes()).hexdigest()==sha
for run,count in [('delivery-actual',60),('delivery-catalog',273),('delivery-menu',12)]:
    ev=root/'evidence'/run
    assert json.loads((ev/'run.json').read_text())['jar_sha256']==meta['jar_sha256']
    text=(ev/'status.log').read_text()
    assert 'ACTUAL_PLAYER_REGRESSION_PASS' in text and 'FAILURE' not in text
    assert len(re.findall(r'POSE_GEOMETRY_RESULT .*coplanar=0 crossings=0',text))==count
menu=(root/'evidence/delivery-menu/status.log').read_text()
assert menu.count('HAIR_REAR_DEPTH')==6
assert 'HAIR_REAR_RASTER_SEAM_PASS samples=41 exposedClothing=0' in menu
assert (root/'evidence/delivery-actual/status.log').read_text().count('FIRST_PERSON_CLOTHING_GRID_PASS')==48
assert (root/'evidence/pre24-comparison/status.log').read_text().count('EXPECTED_PRE24_DEPTH_REJECTION')==6
print('PRE25_FINAL_CHECK_PASS 345 poses; 48 clothing-arm checks; 6 depth checks; 41 raster samples; exact delivery SHA')
