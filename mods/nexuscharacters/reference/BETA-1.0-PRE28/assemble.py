"""Reproduce the byte-exact PRE28 tested JAR from PRE27 plus the archived patch."""
from pathlib import Path
import argparse, base64, hashlib, io, zipfile

BASE_SHA = 'cb6169824f8f23b6afe3faa045b57ded89bb34eee12210e86c9401ad7568c15c'
RESULT_SHA = '32247a901654df15cdfad011c85e3cf16ccb02848f893c342029581a81c43574'
parser = argparse.ArgumentParser()
parser.add_argument('--base', type=Path, default=Path('mods/NexusCharacters-PRE27.zip'))
parser.add_argument('--patch', type=Path, default=Path(__file__).with_name('compiled-patches.zip.b64'))
parser.add_argument('--out', type=Path, default=Path('mods/NexusCharacters-Indivis-1.21.11-BETA-1.0-PRE28.jar'))
args = parser.parse_args()
base = args.base.read_bytes()
if args.base.suffix == '.zip':
    with zipfile.ZipFile(io.BytesIO(base)) as z:
        base = z.read('NexusCharacters-Indivis-1.21.11-BETA-1.0-PRE27.jar')
assert hashlib.sha256(base).hexdigest() == BASE_SHA, 'Wrong PRE27 base'
with zipfile.ZipFile(io.BytesIO(base)) as z:
    entries = {i.filename: z.read(i) for i in z.infolist()}
patch = base64.b64decode(args.patch.read_text(), validate=True)
with zipfile.ZipFile(io.BytesIO(patch)) as z:
    for name in z.namelist(): entries[name] = z.read(name)
args.out.parent.mkdir(parents=True, exist_ok=True)
with zipfile.ZipFile(args.out, 'w', zipfile.ZIP_DEFLATED) as z:
    for name, data in sorted(entries.items()):
        info = zipfile.ZipInfo(name, (2026, 10, 3, 0, 0, 0))
        info.compress_type = zipfile.ZIP_DEFLATED
        z.writestr(info, data)
assert hashlib.sha256(args.out.read_bytes()).hexdigest() == RESULT_SHA, 'Reproduction hash mismatch'
print('PRE28_REPRODUCED', RESULT_SHA)
