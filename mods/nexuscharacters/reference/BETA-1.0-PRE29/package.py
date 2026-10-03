from pathlib import Path
import base64, hashlib, zipfile
from PIL import Image
W=Path(__file__).resolve().parent
base=W/'NexusCharacters-Indivis-1.21.11-BETA-1.0-PRE28.jar'
candidate=W/'NexusCharacters-Indivis-1.21.11-BETA-1.0-PRE29.jar'
assert hashlib.sha256(candidate.read_bytes()).hexdigest() == 'b09135fd42719bcf0e4753db22615db3bce4c8ff31783ff01a5ad8fdf202e96c'
with zipfile.ZipFile(base) as b, zipfile.ZipFile(candidate) as c:
    changed = [n for n in b.namelist() if b.read(n)!=c.read(n)]
    assert not any(n.startswith('assets/') for n in changed)
    allowed={'fabric.mod.json','net/tompsen/nexuscharacters/IndivisReadability.class','net/tompsen/nexuscharacters/mixin/client/IndivisNameFieldContrastMixin.class','net/tompsen/nexuscharacters/mixin/client/IndivisWorldlessViewportMixin.class'}
    allowed.add('net/tompsen/nexuscharacters/IndivisReadability$1.class')
    assert set(changed)==allowed, changed
    with zipfile.ZipFile(W/'compiled-patches.zip','w',zipfile.ZIP_DEFLATED) as z:
        for n in sorted(changed): z.writestr(n,c.read(n))
    (W/'qa/bytes-audit.txt').write_text('Assets and all other classes unchanged from PRE28.\nChanged entries:\n'+'\n'.join(changed)+'\n')
(W/'compiled-patches.zip.b64').write_text(base64.b64encode((W/'compiled-patches.zip').read_bytes()).decode())
with zipfile.ZipFile(W/'NexusCharacters-PRE29.zip','w',zipfile.ZIP_DEFLATED) as z: z.write(candidate,candidate.name)
canvas=Image.new('RGB',(1280,360))
for j,index in enumerate([1,18]):
    im=Image.open(W/'qa/evidence'/('menu-%02d.png'%index)).convert('RGB').resize((640,360))
    canvas.paste(im,((j%2)*640,(j//2)*360))
canvas.save(W/'qa/verified-menus.jpg',quality=90)
print('PRE29_PACKAGED', candidate.stat().st_size, changed, (W/'compiled-patches.zip.b64').stat().st_size)
