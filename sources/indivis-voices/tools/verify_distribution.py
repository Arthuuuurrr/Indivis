#!/usr/bin/env python3
"""Verify the shipped JAR, not just the separate, development audio bank."""
import hashlib
import json
from pathlib import Path
import sys
import zipfile
from verify_inputs import verify_human_mappings


def verify(jar):
    with zipfile.ZipFile(jar) as archive:
        catalog = json.loads(archive.read('indivis-voices/catalog.json'))
        verify_human_mappings(catalog, json.loads(archive.read('indivis-voices/human-takes.json')))
        sounds = json.loads(archive.read('assets/indivis_voices/sounds.json'))
        assert not any(name.endswith(('.wav', '.flac')) for name in archive.namelist()), 'Lossless masters must stay outside the mod'
        assert not any(name.endswith('.mcfunction') for name in archive.namelist()), 'Test fixtures and quest functions must not be shipped'
        for line in catalog['lines']:
            asset = 'assets/indivis_voices/sounds/' + line['asset']
            assert hashlib.sha256(archive.read(asset)).hexdigest() == line['sha256'], asset
            event = sounds[line['sound']]['sounds']
            expected = 'indivis_voices:' + line['asset'].removesuffix('.ogg')
            assert len(event) == 1 and event[0]['name'] == expected and event[0]['stream'] is True, line['id']
        metadata = json.loads(archive.read('fabric.mod.json'))
        assert metadata['id'] == 'indivis_voices'
        assert '${version}' not in metadata['version'], 'Version was not expanded'
    print(f"OK : JAR {metadata['version']}, {len(catalog['lines'])} OGG inclus, hashes et événements vérifiés ; aucun master")


if __name__ == '__main__':
    verify(Path(sys.argv[1]))
