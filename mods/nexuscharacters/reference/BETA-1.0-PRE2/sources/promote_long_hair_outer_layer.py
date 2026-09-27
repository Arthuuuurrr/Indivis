from pathlib import Path
from PIL import Image
import numpy as np

ROOT = Path(__import__('sys').argv[1])
HAIR = ROOT / 'assets/nexuscharacters/appearance_parts_v064/hair'

for path in sorted(HAIR.glob('hair_long_*.png')):
    im = Image.open(path).convert('RGBA')
    a = np.array(im)
    for y in range(16, 32):
        for x in range(16, 40):
            if a[y, x, 3] > 0:
                a[y + 16, x] = a[y, x]
    Image.fromarray(a, 'RGBA').save(path, optimize=True)
