from pathlib import Path
import subprocess,zipfile,json,hashlib,os
root=Path(__file__).resolve().parent
base=Path('downloads/NexusCharacters-HauteCapitale-1.21.11-BETA-1.0-PRE24.jar')
assert hashlib.sha256(base.read_bytes()).hexdigest()=='abeb2a1135f9df7fc723adf24c97d2dce589267a420e375aff6148f3d9597f82'
cp=Path('pre25/compile-classpath.txt').read_text().strip()
# Compile against the unchanged PRE24 classes, independent of the runtime test slot.
cp=cp.replace('runtime/game/mods/NexusCharacters-PRE20.jar',str(base))
subprocess.run(['jdk21/bin/javac','-proc:none','-cp',cp,'-d',str(root/'classes'),str(root/'sources/HairVoxelSurface.java'),str(root/'sources/GenericSkinLayerSupport.java'),str(root/'sources/SurfaceGeometry.java'),str(root/'sources/PosedSurfaceSupport.java')],check=True)
patches={p.relative_to(root/'classes').as_posix():p.read_bytes() for p in (root/'classes').rglob('*.class')}
out=root/'NexusCharacters-HauteCapitale-1.21.11-BETA-1.0-PRE25.jar'
temp=out.with_suffix('.next')
with zipfile.ZipFile(base) as src:
    mod=json.loads(src.read('fabric.mod.json'));mod['version']='1.0.0-beta.25+hc.1.21.11'
    mod['description']='Haute Capitale PRE25: connected hair volume and rear strand clearance.'
    patches['fabric.mod.json']=(json.dumps(mod,ensure_ascii=False,indent=2)+'\n').encode()
    with zipfile.ZipFile(temp,'w',compression=zipfile.ZIP_DEFLATED,compresslevel=9) as dst:
        for n in sorted(set(src.namelist())|patches.keys()):
            if n.endswith('/'):continue
            info=zipfile.ZipInfo(n,(2026,10,3,0,0,0));info.compress_type=zipfile.ZIP_DEFLATED;info.external_attr=0o644<<16
            dst.writestr(info,patches[n] if n in patches else src.read(n))
    with zipfile.ZipFile(temp) as check:
        assert check.testzip() is None
        assets=[n for n in src.namelist() if n.startswith('assets/') and not n.endswith('/')]
        for n in assets:assert check.read(n)==src.read(n),n
os.replace(temp,out)
print('BUILD_PASS',out.stat().st_size,hashlib.sha256(out.read_bytes()).hexdigest(),'assets',len(assets))
