"""Livraison HF6 : deux composants modifiés, guides et preuves ciblées."""
from pathlib import Path
import hashlib,json,re,shutil,zipfile
import build
ROOT=build.ROOT;HERE=build.HERE;OUT=build.OUT
NAME='Indivis_PQ01_R3_HF6_2026-10-23';DEST=ROOT/'deliverables'/NAME
def release():
    audit=OUT/'hf6_audit';validation=json.loads((audit/'validation.json').read_text());assert validation['status']=='PASS'
    if DEST.exists():shutil.rmtree(DEST)
    DEST.mkdir(parents=True)
    def copy(source,name):
        target=DEST/name;target.parent.mkdir(parents=True,exist_ok=True);shutil.copyfile(source,target)
    for kind in ['core','journal']:
        name=build.FILES[kind]
        p=OUT/name;assert hashlib.sha256(p.read_bytes()).hexdigest()==validation['files'][kind]['sha256']
        copy(p,('installation/mods/' if kind=='journal' else 'installation/datapacks/')+name)
    for name,target in [('README.md','README.md'),('INSTALLATION_ET_POSE_PNJ.md','docs/INSTALLATION_ET_POSE_PNJ.md'),('HOTFIX_ENCHAINEMENTS_R3_HF6.md','HOTFIX_ENCHAINEMENTS_R3_HF6.md'),('TESTS_EN_JEU.md','docs/TESTS_EN_JEU.md')]:copy(HERE/'reference_docs'/('HF6_'+name),target)
    for p in audit.iterdir():
        if p.is_file() and p.suffix in {'.json','.txt','.log'}:copy(p,'audit/'+p.name)
    (DEST/'SHA256SUMS').write_text(''.join(v['sha256']+'  '+('installation/mods/' if k=='journal' else 'installation/datapacks/')+v['filename']+'\n' for k,v in validation['files'].items() if k in ['core','journal']))
    packs={k:build.load(OUT/build.FILES[k]) for k in ['core','quests']}
    defined={p.split('/')[1]+':'+p.split('/function/',1)[1][:-11] for pack in packs.values() for p in pack if '/function/' in p and p.endswith('.mcfunction')}
    commands=set()
    for p in DEST.rglob('*.md'):commands.update(re.findall(r'/function ([a-z0-9_.-]+:[a-z0-9_./-]+)',p.read_text()))
    assert commands and not commands-defined,commands-defined
    archive=ROOT/'deliverables'/(NAME+'.zip')
    with zipfile.ZipFile(archive,'w',zipfile.ZIP_DEFLATED,compresslevel=9) as z:
        for p in sorted(DEST.rglob('*')):
            if p.is_file():z.write(p,NAME+'/'+p.relative_to(DEST).as_posix())
    with zipfile.ZipFile(archive) as z:assert z.testzip() is None
    result={'file':str(archive),'bytes':archive.stat().st_size,'sha256':hashlib.sha256(archive.read_bytes()).hexdigest(),'public_function_paths_checked':len(commands)}
    (OUT/'release_manifest.json').write_text(json.dumps(result,ensure_ascii=False,indent=2)+'\n');print(json.dumps(result,ensure_ascii=False,indent=2))
    return result
if __name__=='__main__':release()
