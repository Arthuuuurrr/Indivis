from pathlib import Path
import gzip,subprocess,os,string,secrets,json,shutil
R=Path(__file__).resolve().parents[1];RT=R.parent/'pre31/runtime';D=RT/'xvfb';D.mkdir(exist_ok=True)
blocks=gzip.decompress((RT/'Packages.gz').read_bytes()).decode().split('\n\n')
needed={'xvfb','x11-xkb-utils','libxfont2','libxkbfile1'}
for block in blocks:
 data=dict(line.split(': ',1) for line in block.splitlines() if ': ' in line and not line.startswith(' '))
 if data.get('Package') not in needed:continue
 p=D/(data['Package']+'.deb')
 subprocess.run(['curl','-LsSf','--retry','2','--max-time','90','https://deb.debian.org/debian/'+data['Filename'],'-o',str(p)],check=True)
 subprocess.run(['dpkg-deb','-x',str(p),str(D)],check=True)
for attempt in range(100):
 target=Path('/tmp/'+''.join(secrets.choice(string.ascii_lowercase) for _ in range(3)))
 try:target.mkdir();break
 except FileExistsError:continue
else:raise RuntimeError('No private display helper directory')
assert len(str(target))==8
shutil.copy2(D/'usr/bin/xkbcomp',target/'xkbcomp')
(target/'owner.txt').write_text(str(R))
binary=(D/'usr/bin/Xvfb').read_bytes();assert b'/usr/bin' in binary
output=D/'usr/bin/Xvfb-local';output.write_bytes(binary.replace(b'/usr/bin',str(target).encode()));output.chmod(0o755)
(RT/'display.json').write_text(json.dumps({'helper_directory':str(target)}))
print('DISPLAY_READY',flush=True)
