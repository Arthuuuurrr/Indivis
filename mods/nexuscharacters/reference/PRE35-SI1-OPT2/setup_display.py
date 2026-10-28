"""Rootless, project-local Xvfb. No apt, dpkg install, users or system changes."""
from pathlib import Path
import hashlib,lzma,re,subprocess,urllib.request
R=Path(__file__).resolve().parent/'runtime/xvfb';R.mkdir(parents=True,exist_ok=True)
base='https://archive.ubuntu.com/ubuntu/'
def fetch(url,path,sha=None):
    if path.exists():data=path.read_bytes()
    else:
        with urllib.request.urlopen(url,timeout=60) as f:data=f.read()
        path.write_bytes(data)
    if sha:assert hashlib.sha256(data).hexdigest()==sha,('Package checksum differs',url)
    return data
packages={}
for section in ['main','universe']:
    raw=fetch(base+'dists/noble/'+section+'/binary-amd64/Packages.xz',R/(section+'-Packages.xz'))
    for block in lzma.decompress(raw).decode().split('\n\n'):
        fields={}
        for line in block.splitlines():
            if line and not line[0].isspace() and ': ' in line:
                key,value=line.split(': ',1);fields[key]=value
        if 'Package' in fields:packages[fields['Package']]=fields
names=['xvfb','xserver-common','x11-xkb-utils','libxfont2','libxkbfile1']
for name in names:
    p=packages[name];path=R/(name+'.deb');fetch(base+p['Filename'],path,p['SHA256'])
    subprocess.run(['dpkg-deb','-x',str(path),str(R)],check=True)
    print('LOCAL_PACKAGE',name,p['Version'],flush=True)
subprocess.run(['ldd',str(R/'usr/bin/Xvfb')],check=True)
print('DISPLAY_FILES_READY',str(R),flush=True)
