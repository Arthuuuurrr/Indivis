from pathlib import Path
import concurrent.futures,hashlib,json,os,shutil,subprocess,zipfile
from bootstrap import get,metadata
R=Path(__file__).resolve().parent;RT=R/'runtime'
mods=[('Epm6R3P2','7.16.0','easy-npc-core'),('uTGjf7vA','7.16.0','easy-npc-config'),('iris','1.10.7','iris'),('player-animation-library','1.1.10','pal'),('owo-lib','0.13.0','owo'),('cloth-config','21.11.153','cloth-config'),('entityculling','1.10.2','entityculling'),('skills','0.19.0','puffish-skills')]
def mod(t):
    project,wanted,name=t
    vs=metadata('https://api.modrinth.com/v2/project/'+project+'/version?game_versions=%5B%221.21.11%22%5D&loaders=%5B%22fabric%22%5D',RT/(name+'-versions.json'))
    def match(v):
        n=v['version_number'];return n==wanted or n.startswith(wanted+'+') or n.startswith(wanted+'-') or n=='mc1.21.11-'+wanted+'-fabric'
    v=next(v for v in vs if match(v));f=next((f for f in v['files'] if f.get('primary')),v['files'][0])
    p=get(f['url'],RT/'mods'/(name+'.jar'),f['hashes']['sha512'],'sha512')
    print('TEST_DEPENDENCY',name,v['version_number'],flush=True)
    return {'project':project,'version':v['version_number'],'sha256':hashlib.sha256(p.read_bytes()).hexdigest()}
with concurrent.futures.ThreadPoolExecutor(max_workers=6) as pool:records=list(pool.map(mod,mods))
(RT/'test-dependencies.json').write_text(json.dumps(records,indent=2)+'\n')
D=RT/'xvfb';(D/'xkbbin').mkdir(exist_ok=True)
shutil.copy2(D/'usr/bin/xkbcomp',D/'xkbbin/xkbcomp')
binary=(D/'usr/bin/Xvfb').read_bytes();assert b'/usr/bin' in binary
patched=D/'usr/bin/Xvfb-local';patched.write_bytes(binary.replace(b'/usr/bin',b'./xkbbin'));patched.chmod(0o755)
N=R/'previous/npc';(N/'base').mkdir(exist_ok=True);(N/'qa').mkdir(exist_ok=True)
shutil.copy2(R.parent/'analysis-opt1/inputs/NexusCharacters-Indivis-1.21.11-PRE35-SI1-OPT1(1).jar',N/'base/NexusCharacters-Indivis-1.21.11-PRE35-SI1.jar')
shutil.copy2(R/'inputs/Indivis-Nexus-NPC-0.1.0-PREVIEW1-1.21.11(1).jar',N/'Indivis-Nexus-NPC-0.1.0-PREVIEW1-1.21.11.jar')
assert hashlib.sha256((N/'Indivis-Nexus-NPC-0.1.0-PREVIEW1-1.21.11.jar').read_bytes()).hexdigest()=='cc9cfb403e48c21a22d14e1bcc15152d3ae74dc86d5ab9ddae9a926d35836047'
alias=R/'previous/nexus-pre35'
if not alias.exists():alias.symlink_to('pre35',target_is_directory=True)
extra=N/'runtime-extra';extra.mkdir(exist_ok=True)
for p in (RT/'mods').glob('*.jar'):
    if p.name not in ['fabric-api.jar','skinlayers.jar','sodium.jar','immediatelyfast.jar']:shutil.copy2(p,extra/p.name)
print('TEST_FIXTURE_READY',flush=True)
