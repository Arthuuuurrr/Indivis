"""Reconstruct the exact PRE27 runtime-tested candidate."""
from pathlib import Path
import base64, hashlib, io, json, zipfile

ROOT = Path(__file__).resolve().parent
BASE = ROOT.parents[2] / 'NexusCharacters-HauteCapitale-1.21.11-BETA-1.0-PRE26.jar'
OUT = ROOT.parents[2] / 'NexusCharacters-Indivis-1.21.11-BETA-1.0-PRE27.jar'
assert hashlib.sha256(BASE.read_bytes()).hexdigest() == 'ea070e3920cd7c74926115886949b911bd887ed408eac975524ba667f941496c'
bundle = base64.b64decode((ROOT / 'compiled-patches.zip.b64').read_text())
assert hashlib.sha256(bundle).hexdigest() == 'c3faf9208fd085f60fb719dce39f90428015a58c26f844f252053baf82d15c72'
with zipfile.ZipFile(io.BytesIO(bundle)) as patch:
    patches = {n: patch.read(n) for n in patch.namelist() if not n.endswith('/')}
with zipfile.ZipFile(BASE) as src:
    mod = json.loads(src.read('fabric.mod.json'))
    mod['version'] = '1.0.0-beta.27+indivis.1.21.11'
    mod['description'] = 'L’Indivis PRE27: parchment character menus, complete palettes and rotatable previews.'
    patches['fabric.mod.json'] = (json.dumps(mod, ensure_ascii=False, indent=2) + '\n').encode()
    cfg = json.loads(src.read('nexuscharacters.client.mixins.json'))
    cfg['client'] += ['IndivisCreationMixin', 'IndivisSelectionMixin', 'IndivisWorldPreviewMixin', 'IndivisPreviewNameMixin']
    patches['nexuscharacters.client.mixins.json'] = (json.dumps(cfg, indent=2) + '\n').encode()
    with zipfile.ZipFile(OUT, 'w', compression=zipfile.ZIP_DEFLATED, compresslevel=9) as dst:
        for name in sorted(set(src.namelist()) | set(patches)):
            if name.endswith('/'):
                continue
            info = zipfile.ZipInfo(name, (2026, 10, 3, 0, 0, 0))
            info.compress_type = zipfile.ZIP_DEFLATED
            info.external_attr = 0o644 << 16
            dst.writestr(info, patches[name] if name in patches else src.read(name))
with zipfile.ZipFile(BASE) as src, zipfile.ZipFile(OUT) as dst:
    unchanged = [n for n in src.namelist() if not n.endswith('/') and n not in patches]
    assert all(src.read(n) == dst.read(n) for n in unchanged)
sha = hashlib.sha256(OUT.read_bytes()).hexdigest()
assert sha == 'cb6169824f8f23b6afe3faa045b57ded89bb34eee12210e86c9401ad7568c15c', sha
print(f'PRE27_ASSEMBLY_PASS candidate={OUT} sha256={sha} unchanged_entries={len(unchanged)}')
