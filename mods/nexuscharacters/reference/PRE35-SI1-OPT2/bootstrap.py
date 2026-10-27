"""Restore an isolated MC 1.21.11 QA runtime from primary distribution metadata."""
from pathlib import Path
import concurrent.futures, hashlib, json, os, subprocess, urllib.request, zipfile, xml.etree.ElementTree as ET

R=Path(__file__).resolve().parent; RT=R/'runtime'
RT.mkdir(exist_ok=True)
def get(url,path,sha=None,algorithm='sha1'):
    path=Path(path)
    if path.exists() and (sha is None or hashlib.new(algorithm,path.read_bytes()).hexdigest()==sha):return path
    path.parent.mkdir(parents=True,exist_ok=True)
    with urllib.request.urlopen(urllib.request.Request(url,headers={'User-Agent':'Indivis-Nexus-QA/OPT2'}),timeout=60) as response:
        data=response.read()
    if sha and hashlib.new(algorithm,data).hexdigest()!=sha:raise ValueError('Checksum mismatch: '+url)
    path.write_bytes(data)
    print('FETCH',path.relative_to(R),len(data),flush=True)
    return path
def metadata(url,path):return json.loads(get(url,path).read_text())

def setup():
    archive=RT/'temurin21.tar.gz';expected='d7f5d5cf865a2902b70ff549b97586a7d3f234e016f041ab447ea740c7d66d1c'
    assert hashlib.sha256(archive.read_bytes()).hexdigest()==expected,'Unverified JDK'
    if not (RT/'jdk/bin/java').exists():
        (RT/'jdk').mkdir(exist_ok=True)
        subprocess.run(['tar','-xzf',str(archive),'-C',str(RT/'jdk'),'--strip-components=1'],check=True)
    manifest=metadata('https://piston-meta.mojang.com/mc/game/version_manifest_v2.json',RT/'version_manifest.json')
    entry=next(v for v in manifest['versions'] if v['id']=='1.21.11')
    version=json.loads(get(entry['url'],RT/'version.json',entry['sha1']).read_text())
    client=version['downloads']['client']; get(client['url'],RT/'client.jar',client['sha1'])
    tasks=[]
    def permitted(lib):
        result=not bool(lib.get('rules'))
        for rule in lib.get('rules',[]):
            osrule=rule.get('os',{})
            if osrule.get('name','linux')!='linux':continue
            if osrule.get('arch','x86_64') not in ('x86_64','amd64'):continue
            if 'features' in rule:continue
            result=rule['action']=='allow'
        return result
    for lib in version['libraries']:
        if not permitted(lib):continue
        downloads=lib.get('downloads',{})
        if 'artifact' in downloads:
            a=downloads['artifact'];tasks.append((a['url'],RT/'libs'/a['path'],a.get('sha1')))
        classifier=lib.get('natives',{}).get('linux','').replace('${arch}','64')
        if classifier and classifier in downloads.get('classifiers',{}):
            a=downloads['classifiers'][classifier];tasks.append((a['url'],RT/'libs'/a['path'],a.get('sha1')))
    with concurrent.futures.ThreadPoolExecutor(max_workers=8) as pool:list(pool.map(lambda t:get(*t),tasks))
    cp=[str(t[1]) for t in tasks]
    natives=RT/'natives';natives.mkdir(exist_ok=True)
    for _,path,_ in tasks:
        with zipfile.ZipFile(path) as z:
            for name in z.namelist():
                if name.endswith('.so') and '/' not in name:(natives/name).write_bytes(z.read(name))
    fabric='https://maven.fabricmc.net/'
    loader=metadata('https://meta.fabricmc.net/v2/versions/loader/1.21.11/0.19.5',RT/'loader.json')
    for lib in loader['launcherMeta']['libraries']['common']+loader['launcherMeta']['libraries']['client']:
        group,artifact,v=lib['name'].split(':');p=group.replace('.','/')+'/'+artifact+'/'+v+'/'+artifact+'-'+v+'.jar'
        cp.append(str(get(lib['url']+p,RT/'libs'/p)))
    get(fabric+'net/fabricmc/fabric-loader/0.19.5/fabric-loader-0.19.5.jar',RT/'fabric-loader.jar')
    get(fabric+'net/fabricmc/intermediary/1.21.11/intermediary-1.21.11-v2.jar',RT/'intermediary.jar')
    get(fabric+'net/fabricmc/tiny-remapper/0.11.2/tiny-remapper-0.11.2-fat.jar',RT/'tiny-remapper.jar')
    with zipfile.ZipFile(RT/'intermediary.jar') as z:(RT/'intermediary.tiny').write_bytes(z.read('mappings/mappings.tiny'))
    if not (RT/'client-intermediary.jar').exists():
        subprocess.run([str(RT/'jdk/bin/java'),'-Xmx2G','-jar',str(RT/'tiny-remapper.jar'),str(RT/'client.jar'),str(RT/'client-intermediary.jar'),str(RT/'intermediary.tiny'),'official','intermediary',*cp],check=True)
    cp.extend([str(RT/'fabric-loader.jar'),str(RT/'client-intermediary.jar')])
    (RT/'classpath.txt').write_text(':'.join(cp))
    get(fabric+'net/fabricmc/fabric-api/fabric-api/0.141.6+1.21.11/fabric-api-0.141.6+1.21.11.jar',RT/'mods/fabric-api.jar')
    get('https://github.com/tr7zw/3d-Skin-Layers/releases/download/1.10.2/skinlayers3d-fabric-1.10.2-mc1.21.11.jar',RT/'mods/skinlayers.jar','736f076b9f45bab1c118166857f46729f782ddd481a0e4e4ee0bc110643f2756','sha256')
    for project,number in [('sodium','0.8.7'),('immediatelyfast','1.14.2'),('easy-npc','7.16.0'),('geckolib','5.4.5')]:
        versions=metadata('https://api.modrinth.com/v2/project/'+project+'/version?game_versions=%5B%221.21.11%22%5D&loaders=%5B%22fabric%22%5D',RT/(project+'-versions.json'))
        v=next(v for v in versions if v['version_number']==number or v['version_number'].startswith(number+'+') or number in v['version_number'])
        a=next((f for f in v['files'] if f.get('primary')),v['files'][0])
        get(a['url'],RT/'mods'/(project+'.jar'),a['hashes']['sha512'],'sha512')
    assets=version['assetIndex'];index=metadata(assets['url'],RT/'assets/indexes'/(assets['id']+'.json'))
    # Download only game fonts/textures required by the regression client. Audio
    # is disabled; no full music pack or server assets are needed for this QA.
    asset_tasks=[]
    for name,v in index['objects'].items():
        if name.startswith(('minecraft/sounds/','minecraft/lang/')):continue
        h=v['hash'];asset_tasks.append(('https://resources.download.minecraft.net/'+h[:2]+'/'+h,RT/'assets/objects'/h[:2]/h,h))
    with concurrent.futures.ThreadPoolExecutor(max_workers=8) as pool:list(pool.map(lambda t:get(*t),asset_tasks))
    print('RUNTIME_READY',version['id'],len(cp),len(asset_tasks),flush=True)

if __name__=='__main__':setup()
