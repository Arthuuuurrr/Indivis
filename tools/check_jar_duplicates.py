#!/usr/bin/env python3
from pathlib import Path
import hashlib
import re
import sys

ROOT = Path("jars")
LFS_RE = re.compile(
    r"^version https://git-lfs\.github\.com/spec/v1\n"
    r"oid sha256:([0-9a-f]{64})\n"
    r"size (\d+)\n?$"
)

entries = []
errors = []

for path in sorted(ROOT.glob("*.jar")):
    data = path.read_bytes()
    try:
        text = data.decode("utf-8")
    except UnicodeDecodeError:
        text = None

    m = LFS_RE.match(text) if text else None
    if m:
        sha256 = m.group(1)
        size = int(m.group(2))
        storage = "lfs"
    else:
        sha256 = hashlib.sha256(data).hexdigest()
        size = len(data)
        storage = "local"

    entries.append((sha256, size, storage, path.as_posix()))

    if size < 1024:
        errors.append(f"Suspicious JAR size {size} bytes: {path}")

nested = sorted(ROOT.rglob("*.jar"))
flat = sorted(ROOT.glob("*.jar"))
if nested != flat:
    errors.append("All active JARs must be directly inside jars/; nested JAR folders are forbidden.")

by_sha = {}
for row in entries:
    by_sha.setdefault(row[0], []).append(row)

for sha, rows in sorted(by_sha.items()):
    if len(rows) > 1:
        errors.append(
            "Exact duplicate SHA-256 "
            + sha
            + ": "
            + ", ".join(r[3] for r in rows)
        )

print(f"Active server JARs indexed: {len(entries)}")
for sha, size, storage, path in entries:
    print(f"{sha} {size:>10} {storage:>5} {path}")

if errors:
    print("\nLibrary policy violations:", file=sys.stderr)
    for err in errors:
        print(f"- {err}", file=sys.stderr)
    sys.exit(1)

print("\nFlat server-current JAR library is coherent.")
