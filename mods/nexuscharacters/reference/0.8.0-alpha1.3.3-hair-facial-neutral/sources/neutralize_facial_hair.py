from PIL import Image
import numpy as np
from pathlib import Path
TARGET_MEAN = 172.0
TARGET_STD = 24.0

def normalize(path: Path):
    rgba = np.array(Image.open(path).convert('RGBA'))
    alpha = rgba[:, :, 3].astype(np.float64) / 255.0
    mask = alpha > 0
    rgb = rgba[:, :, :3].astype(np.float64)
    lum = (30*rgb[:,:,0] + 59*rgb[:,:,1] + 11*rgb[:,:,2]) / 100.0
    values, weights = lum[mask], alpha[mask]
    mean = float(np.average(values, weights=weights))
    std = float(np.sqrt(np.average((values-mean)**2, weights=weights)))
    gray = np.full(lum.shape, TARGET_MEAN) if std < 1e-9 else TARGET_MEAN + (lum-mean)*(TARGET_STD/std)
    gray = np.clip(np.rint(gray), 0, 255).astype(np.uint8)
    out = rgba.copy()
    for c in range(3): out[:,:,c][mask] = gray[mask]
    Image.fromarray(out, 'RGBA').save(path, optimize=True)

for png in sorted(Path('assets/nexuscharacters/appearance_parts_v068/facial_hair').glob('*.png')):
    normalize(png)
