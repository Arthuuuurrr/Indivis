from PIL import Image
from pathlib import Path
import numpy as np

root = Path('assets/nexuscharacters/textures/cosmetic')
base = np.array(Image.open(root / 'beard_base.png').convert('RGBA')).astype(np.float32)
colors = ['black','brown','chestnut','blond','light_blond','dark_blond','red','bright_red','auburn','gray','white','silver']
ornaments = {
    'iron_ring': (116,120,124),
    'bronze_ring': (154,99,53),
    'silver_ring': (197,203,209),
    'gold_ring': (212,168,62),
    'runic_bead': (75,142,148),
    'engraved_clasp': (178,150,82),
    'braid_tip': (133,137,141),
}
lum = base[:, :, 0] / max(1.0, float(base[:, :, 0].mean()))
for color in colors:
    src = np.array(Image.open(root / f'beard_{color}.png').convert('RGBA'))
    for ornament, rgb in ornaments.items():
        out = src.copy()
        for y in range(40, 64):
            for x in range(40, 64):
                factor = float(lum[y, x])
                out[y, x, :3] = [max(0, min(255, round(channel * factor))) for channel in rgb]
                out[y, x, 3] = 255
        Image.fromarray(out, 'RGBA').save(root / f'beard_{color}_{ornament}.png', optimize=True)
