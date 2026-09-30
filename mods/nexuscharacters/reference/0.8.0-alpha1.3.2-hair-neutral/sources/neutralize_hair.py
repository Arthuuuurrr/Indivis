from PIL import Image
import numpy as np
from pathlib import Path

TARGET_MEAN = 172.0
TARGET_STD = 24.0

def normalize(path: Path):
    image = Image.open(path).convert("RGBA")
    rgba = np.array(image)
    alpha = rgba[:, :, 3].astype(np.float64) / 255.0
    mask = alpha > 0
    rgb = rgba[:, :, :3].astype(np.float64)
    luminance = (30 * rgb[:, :, 0] + 59 * rgb[:, :, 1] + 11 * rgb[:, :, 2]) / 100.0

    weights = alpha[mask]
    values = luminance[mask]
    mean = float(np.average(values, weights=weights))
    variance = float(np.average((values - mean) ** 2, weights=weights))
    std = variance ** 0.5

    if std < 1e-9:
        gray = np.full(luminance.shape, TARGET_MEAN)
    else:
        gray = TARGET_MEAN + (luminance - mean) * (TARGET_STD / std)

    gray = np.clip(np.rint(gray), 0, 255).astype(np.uint8)
    out = rgba.copy()
    for channel in range(3):
        out[:, :, channel][mask] = gray[mask]

    Image.fromarray(out, "RGBA").save(path, optimize=True)

for png in sorted(Path("assets/hair").glob("*.png")):
    normalize(png)
