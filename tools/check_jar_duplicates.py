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
for path in sorted(ROOT.rglob("*.jar")):
    data = path.read_bytes()
    try:
        text = data.decode("utf-8")
    except UnicodeDecodeError:
        text = None

    if text:
        m = LFS_RE.match(text)
    else:
        m = None

    if m:
        sha256 = m.group(1)
        size = int(m.group(2))
        storage = "lfs"
    else:
        sha256 = hashlib.sha256(data).hexdigest()
        size = len(data)
        storage = "local"

    entries.append((sha256, size, storage, path.as_posix()))

groups = {}
for row in entries:
    groups.setdefault(row[0], []).append(row)

duplicates = {sha: rows for sha, rows in groups.items() if len(rows) > 1}

print(f"JARs indexed: {len(entries)}")
for sha, size, storage, path in entries:
    print(f"{sha}  {size:>10}  {storage:>5}  {path}")

if duplicates:
    print("\nExact duplicate binaries detected:", file=sys.stderr)
    for sha, rows in duplicates.items():
        print(f"\nSHA-256 {sha}", file=sys.stderr)
        for _, size, _, path in rows:
            print(f"  {size:>10}  {path}", file=sys.stderr)
    sys.exit(1)

print("\nNo exact duplicate binaries detected.")
