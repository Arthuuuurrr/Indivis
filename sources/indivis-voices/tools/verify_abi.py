#!/usr/bin/env python3
"""Check audited binary APIs without initializing the old mods or inventing stubs."""
import argparse
import hashlib
from pathlib import Path
import struct
import zipfile


class ClassFile:
    def __init__(self, data):
        self.data, self.pos = data, 8
        assert data[:4] == b"\xca\xfe\xba\xbe"
        pool = [None] * self.u2()
        i = 1
        while i < len(pool):
            tag = self.u1()
            if tag == 1:
                size = self.u2(); pool[i] = self.take(size).decode("utf-8", errors="replace")
            elif tag in (5, 6):
                self.take(8); i += 1
            else:
                self.take({3: 4, 4: 4, 7: 2, 8: 2, 9: 4, 10: 4, 11: 4, 12: 4,
                           15: 3, 16: 2, 17: 4, 18: 4, 19: 2, 20: 2}[tag])
            i += 1
        self.take(6)
        self.take(self.u2() * 2)
        self.fields = self.members(pool)
        self.methods = self.members(pool)

    def take(self, n):
        p = self.pos; self.pos += n; return self.data[p:self.pos]

    def u1(self): return self.take(1)[0]
    def u2(self): return struct.unpack(">H", self.take(2))[0]

    def members(self, pool):
        result = {}
        for _ in range(self.u2()):
            flags, name, descriptor = self.u2(), self.u2(), self.u2()
            result[(pool[name], pool[descriptor])] = flags
            for _ in range(self.u2()):
                self.take(2); size = struct.unpack(">I", self.take(4))[0]; self.take(size)
        return result


def check(jar, sha, classes):
    assert hashlib.sha256(jar.read_bytes()).hexdigest() == sha, "Build différent : réauditer les interfaces"
    with zipfile.ZipFile(jar) as z:
        for name, fields, methods in classes:
            c = ClassFile(z.read(name + ".class"))
            for field in fields:
                assert field in c.fields, (name, "field", field)
            for method in methods:
                assert method in c.methods, (name, "method", method)
    print(f"OK : {jar.name}, signatures binaires réellement présentes")


if __name__ == "__main__":
    p = argparse.ArgumentParser(description=__doc__)
    p.add_argument("--dialogue", type=Path, required=True)
    p.add_argument("--nexus", type=Path)
    a = p.parse_args()
    prefix = "net/hautecapitale/dialogue/"
    check(a.dialogue, "a2c21f24889f96880c472d168ea55cf65e84ff154ee1bbcc22dbeaa9df1d4057", [
        (prefix + "session/DialogueManager", [], [("session", "(Lnet/minecraft/class_1657;)Ljava/util/Optional;")]),
        (prefix + "session/DialogueSession", [("id", "I"), ("pnj", "Ljava/util/UUID;"), ("pnjEntityId", "I"), ("pnjNom", "Ljava/lang/String;"), ("monde", "Lnet/minecraft/class_5321;")], [("toucher", "(J)V")]),
        (prefix + "client/DialogueClientState", [("REPLIQUES", "Ljava/util/List;"), ("pageRemise", "Z"), ("captureVersion", "I"), ("fenetreJusquaMs", "J")],
         [("captureActive", "()Z"), ("sessionId", "()I"), ("pnjUuid", "()Ljava/util/UUID;"), ("typewriterActifCapture", "()Z"), ("typewriterVitesse", "()I")]),
        (prefix + "client/capture/CaptureDialogueScreen", [("panneau", "L" + prefix + "client/screen/DialoguePanel;")], []),
        (prefix + "client/screen/DialoguePanel", [], [("typewriter", "(ZI)V"), ("choixSous", "(DD)I"), ("avancer", "()Z"),
         ("page", "()I"), ("pages", "()I"), ("historiqueVisible", "()Z")]),
    ])
    if a.nexus:
        check(a.nexus, "24e68bc46405c8c40de030b192372da2a14bae7df1b28cee3f9db04cfe528e90", [
            ("net/tompsen/nexuscharacters/NexusCharacters", [], [("getSelectedCharacter", "(Lnet/minecraft/class_3222;)Lnet/tompsen/nexuscharacters/CharacterDto;")]),
            ("net/tompsen/nexuscharacters/CharacterDto", [], [("id", "()Ljava/util/UUID;")]),
        ])
