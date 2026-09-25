#!/usr/bin/env python3
from __future__ import annotations
import argparse, hashlib, json, shutil, subprocess, tempfile, zipfile
from pathlib import Path

RC13_SHA = "50ec8ae31733ee454b16e36f1e60a0521947e6d28cbab97159d1b6f22b96dc48"
RC14_SHA = "6946c10ef60cc02db10aef7c83ba5b7b9ef0c0f4d29da46bcf8805560670d9ea"
OUT = "spell_engine-fabric-1.10.5.001+1.21.11-HC-TEST3-RC14-ITEM-USE-SPELL-ICON.jar"

def sha(path: Path) -> str:
    return hashlib.sha256(path.read_bytes()).hexdigest()

def main() -> int:
    ap=argparse.ArgumentParser()
    ap.add_argument("--rc13",type=Path,required=True)
    ap.add_argument("--out-dir",type=Path,required=True)
    args=ap.parse_args()
    if sha(args.rc13)!=RC13_SHA: raise SystemExit("unexpected RC13 SHA-256")
    here=Path(__file__).parent
    args.out_dir.mkdir(parents=True,exist_ok=True)
    with tempfile.TemporaryDirectory() as td:
        w=Path(td); (w/"stubs/net/minecraft").mkdir(parents=True); (w/"classes").mkdir(); (w/"patchclasses").mkdir()
        (w/"stubs/net/minecraft/class_1799.java").write_text("package net.minecraft; public class class_1799 {}\n")
        (w/"stubs/net/minecraft/class_2960.java").write_text("package net.minecraft; public class class_2960 { public static class_2960 method_60654(String s){return null;} }\n")
        subprocess.run(["javac","--release","21","-d",str(w/"classes"),str(w/"stubs/net/minecraft/class_1799.java"),str(w/"stubs/net/minecraft/class_2960.java")],check=True)
        subprocess.run(["javac","--release","21","-cp",f"{args.rc13}:{w/'classes'}","-d",str(w/"classes"),str(here/"HcItemUseSpellIcon.java")],check=True)
        subprocess.run(["javac","--add-exports","java.base/jdk.internal.org.objectweb.asm=ALL-UNNAMED","--add-exports","java.base/jdk.internal.org.objectweb.asm.tree=ALL-UNNAMED","-d",str(w/"patchclasses"),str(here/"PatchRc14ItemUseIcon.java")],check=True)
        with zipfile.ZipFile(args.rc13) as z:
            (w/"HudRenderHelper.class").write_bytes(z.read("net/spell_engine/client/gui/HudRenderHelper.class"))
        subprocess.run(["java","--add-exports","java.base/jdk.internal.org.objectweb.asm=ALL-UNNAMED","--add-exports","java.base/jdk.internal.org.objectweb.asm.tree=ALL-UNNAMED","-cp",str(w/"patchclasses"),"PatchRc14ItemUseIcon",str(w/"HudRenderHelper.class"),str(w/"HudRenderHelper.patched.class")],check=True)
        out=args.out_dir/OUT
        with zipfile.ZipFile(args.rc13) as zin, zipfile.ZipFile(out,"w",zipfile.ZIP_DEFLATED) as zout:
            for info in zin.infolist():
                data=zin.read(info.filename)
                if info.filename=="fabric.mod.json":
                    m=json.loads(data)
                    m["version"]="1.10.5.001+1.21.11-HC-TEST3-RC14-ITEM-USE-SPELL-ICON"
                    m["description"]="HC TEST3 RC14: RC13 plus spell-icon rendering for the vanilla-use/right-click SpellHotbar slot, with ItemStack fallback."
                    data=(json.dumps(m,ensure_ascii=False,indent=2)+"\n").encode()
                elif info.filename=="net/spell_engine/client/gui/HudRenderHelper.class":
                    data=(w/"HudRenderHelper.patched.class").read_bytes()
                zout.writestr(info,data)
            info=zipfile.ZipInfo("net/spell_engine/client/util/HcItemUseSpellIcon.class",(2026,9,23,18,45,0))
            info.compress_type=zipfile.ZIP_DEFLATED; info.create_system=3; info.external_attr=0o644<<16
            zout.writestr(info,(w/"classes/net/spell_engine/client/util/HcItemUseSpellIcon.class").read_bytes())
    actual=sha(out)
    if actual!=RC14_SHA: raise SystemExit(f"non-reproducible RC14: {actual}")
    print(out,actual)
    return 0

if __name__=="__main__":
    raise SystemExit(main())
