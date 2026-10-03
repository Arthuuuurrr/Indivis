from pathlib import Path
import base64, hashlib, io, zipfile
BASE_SHA = '32247a901654df15cdfad011c85e3cf16ccb02848f893c342029581a81c43574'
RESULT_SHA = 'b09135fd42719bcf0e4753db22615db3bce4c8ff31783ff01a5ad8fdf202e96c'
HERE = Path(__file__).resolve().parent
with zipfile.ZipFile('mods/NexusCharacters-PRE28.zip') as z:
    base = z.read('NexusCharacters-Indivis-1.21.11-BETA-1.0-PRE28.jar')
assert hashlib.sha256(base).hexdigest() == BASE_SHA
with zipfile.ZipFile(io.BytesIO(base)) as z: entries = {i.filename:z.read(i) for i in z.infolist()}
with zipfile.ZipFile(io.BytesIO(base64.b64decode((HERE/'compiled-patches.zip.b64').read_text(), validate=True))) as z:
    for n in z.namelist(): entries[n] = z.read(n)
out = Path('mods/NexusCharacters-Indivis-1.21.11-BETA-1.0-PRE29.jar')
with zipfile.ZipFile(out,'w',zipfile.ZIP_DEFLATED) as z:
    for n,data in sorted(entries.items()):
        i=zipfile.ZipInfo(n,(2026,10,3,0,0,0)); i.compress_type=zipfile.ZIP_DEFLATED; z.writestr(i,data)
assert hashlib.sha256(out.read_bytes()).hexdigest() == RESULT_SHA
print('PRE29_REPRODUCED', RESULT_SHA)
