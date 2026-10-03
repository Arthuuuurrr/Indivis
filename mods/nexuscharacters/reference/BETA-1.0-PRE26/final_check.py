import sys,json,hashlib,zipfile,base64,io,re
from pathlib import Path
root=Path(__file__).resolve().parent
jar=Path(sys.argv[1]) if len(sys.argv)>1 else root/'NexusCharacters-HauteCapitale-1.21.11-BETA-1.0-PRE26.jar'
meta=json.loads((root/'build-manifest.json').read_text())
assert hashlib.sha256(jar.read_bytes()).hexdigest()==meta['jar_sha256']
data=base64.b64decode((root/'compiled-patches.zip.b64').read_bytes())
assert hashlib.sha256(data).hexdigest()==meta['compiled_archive_sha256']
with zipfile.ZipFile(jar) as z,zipfile.ZipFile(io.BytesIO(data)) as patches:
    assert z.testzip() is None
    assert json.loads(z.read('fabric.mod.json'))['version']=='1.0.0-beta.26+hc.1.21.11'
    assert sum(n.startswith('assets/') and not n.endswith('/') for n in z.namelist())==4958
    for name,sha in meta['class_sha256'].items():
        assert hashlib.sha256(patches.read(name)).hexdigest()==sha
        assert z.read(name)==patches.read(name)
for name,sha in meta['source_sha256'].items():
    assert hashlib.sha256((root/'sources'/name).read_bytes()).hexdigest()==sha
logs=base64.b64decode((root/'evidence-logs.zip.b64').read_bytes())
assert hashlib.sha256(logs).hexdigest()==meta['evidence_archive_sha256']
with zipfile.ZipFile(io.BytesIO(logs)) as ev:
    for run,count in [('actual',80),('catalog',1092),('menu',24)]:
        assert json.loads(ev.read(run+'/run.json'))['jar_sha256']==meta['jar_sha256']
        text=ev.read(run+'/status.log').decode()
        assert 'ACTUAL_PLAYER_REGRESSION_PASS' in text and 'FAILURE' not in text
        assert len(re.findall(r'POSE_GEOMETRY_RESULT .*coplanar=0 crossings=0',text))==count
    catalog=ev.read('catalog/status.log').decode()
    assert catalog.count('ARM_TEXTURE_COVERAGE_PASS')==364
    actual=ev.read('actual/status.log').decode()
    assert actual.count('FIRST_PERSON_CLOTHING_GRID_PASS')==64
    assert actual.count('NEW_ASSET_ARM_LAYOUT_PASS')==3
    menu=ev.read('menu/status.log').decode()
    assert menu.count('HAIR_REAR_DEPTH')==6
    assert 'HAIR_REAR_RASTER_SEAM_PASS samples=41 exposedClothing=0' in menu
    old=ev.read('pre25-comparison/status.log').decode()
    assert old.count('EXPECTED_PRE25_ARM_UV_REJECTION')==2
    assert json.loads(ev.read('pre25-comparison/run.json'))['jar_sha256']==meta['base_sha256']
print('PRE26_FINAL_CHECK_PASS 364 configurations; 1196 poses; 64 arm-grid checks; PRE25 rejection; exact delivery SHA')
