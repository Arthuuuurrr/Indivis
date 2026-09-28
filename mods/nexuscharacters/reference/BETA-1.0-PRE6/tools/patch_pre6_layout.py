#!/usr/bin/env python3
from pathlib import Path
import struct
import sys

def patch_utf8(data: bytes, old: str, new: str) -> bytes:
    old_b = old.encode("utf-8")
    new_b = new.encode("utf-8")
    needle = struct.pack(">H", len(old_b)) + old_b
    repl = struct.pack(">H", len(new_b)) + new_b
    count = data.count(needle)
    if count != 1:
        raise SystemExit(f"Expected exactly one UTF8 constant {old!r}, found {count}")
    return data.replace(needle, repl, 1)

if len(sys.argv) != 2:
    raise SystemExit("usage: patch_pre6_layout.py <CharacterCreationAppearance65Mixin.class>")

path = Path(sys.argv[1])
data = path.read_bytes()

# Keep the existing responsive layout intact; only teach its existing
# startsWith() checks the labels that are actually displayed at runtime.
data = patch_utf8(data, "Oreilles elfiques", "Oreilles")
data = patch_utf8(data, "Barbe nordique", "Ornement")

path.write_bytes(data)
print(f"Patched {path}: runtime cosmetic labels are now recognized by layout")
