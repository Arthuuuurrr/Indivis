from pathlib import Path
import json,hashlib
from PIL import Image,ImageChops
R=Path(__file__).resolve().parents[1];a=R/'qa/evidence-menus-baseline';b=R/'qa/evidence-menus'
result={'pixel_identical_menus':0,'geometry_rows':{},'failures':[]}
for i in range(23):
 name=f'menu-{i:02d}.png';x=Image.open(a/name).convert('RGB');y=Image.open(b/name).convert('RGB')
 if x.size==y.size and ImageChops.difference(x,y).getbbox() is None:result['pixel_identical_menus']+=1
 else:result['failures'].append(name)
for name in ['geometry.csv','pose-geometry.csv']:
 x=(a/'geometry'/name).read_bytes();y=(b/'geometry'/name).read_bytes()
 result['geometry_rows'][name]=len(x.splitlines())
 if x!=y:result['failures'].append(name)
result['jar_sha256']=hashlib.sha256((R/'Indivis-Nexus-NPC-0.1.0-PREVIEW1-1.21.11.jar').read_bytes()).hexdigest()
(R/'qa/comparison.json').write_text(json.dumps(result,indent=2)+'\n');print(json.dumps(result));assert not result['failures']
