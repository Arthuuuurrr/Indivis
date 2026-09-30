from pathlib import Path
import base64, hashlib, json

root = Path(__file__).resolve().parent
m = json.loads((root / "manifest.json").read_text(encoding="utf-8"))
encoded = "".join(
    "".join((root / name).read_text(encoding="ascii").split())
    for name in m["parts"]
)
data = base64.b64decode(encoded, validate=True)
sha = hashlib.sha256(data).hexdigest()
if sha != m["artifact_sha256"]:
    raise SystemExit(f"SHA-256 mismatch: {sha} != {m['artifact_sha256']}")
out = root.parent.parent / m["artifact"]
out.write_bytes(data)
print(out)
print(sha)