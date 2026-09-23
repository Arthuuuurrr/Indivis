#!/usr/bin/env python3
from pathlib import Path
import hashlib
import re
import sys

ROOT = Path("artifacts/jars")
LFS_RE = re.compile(
    r"^version https://git-lfs\.github\.com/spec/v1\n"
    r"oid sha256:([0-9a-f]{64})\n"
    r"size (\d+)\n?$"
)

entries = []
errors = []

for path in sorted(ROOT.rglob("*.jar")):
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

    module = path.relative_to(ROOT).parts[0]
    entries.append((module, sha256, size, storage, path.as_posix()))

    if size < 1024:
        errors.append(f"Suspicious JAR size {size} bytes: {path}")

# Policy: the active server library contains at most one JAR per module.
by_module = {}
for row in entries:
    by_module.setdefault(row[0], []).append(row)

for module, rows in sorted(by_module.items()):
    if len(rows) > 1:
        errors.append(
            "Multiple active JARs for module "
            + module
            + ": "
            + ", ".join(r[4] for r in rows)
        )

# Exact binary duplicates under different module/name.
by_sha = {}
for row in entries:
    by_sha.setdefault(row[1], []).append(row)

for sha, rows in sorted(by_sha.items()):
    if len(rows) > 1:
        errors.append(
            "Exact duplicate SHA-256 "
            + sha
            + ": "
            + ", ".join(r[4] for r in rows)
        )

print(f"Active server JARs indexed: {len(entries)}")
for module, sha, size, storage, path in entries:
    print(f"{module:28} {sha} {size:>10} {storage:>5} {path}")

if errors:
    print("\nLibrary policy violations:", file=sys.stderr)
    for err in errors:
        print(f"- {err}", file=sys.stderr)
    sys.exit(1)

print("\nServer-current library is coherent.")
