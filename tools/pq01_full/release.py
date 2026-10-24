"""Livraison HF10 : seul JAR changé, guides complets et preuves synthétiques."""
from pathlib import Path
import hashlib,json,re,shutil,zipfile
import build
ROOT=build.ROOT;HERE=build.HERE;OUT=build.OUT
NAME='Indivis_PQ01_R3_HF10_2026-10-24';DEST=ROOT/'deliverables'/NAME
def release():
    audit=OUT/'hf10_audit';validation=json.loads((audit/'validation.json').read_text());assert validation['status']=='PASS'
    if DEST.exists():shutil.rmtree(DEST)
    DEST.mkdir(parents=True)
    def copy(source,name):
        target=DEST/name;target.parent.mkdir(parents=True,exist_ok=True);shutil.copyfile(source,target)
    journal=validation['files']['journal'];p=OUT/journal['filename']
    assert hashlib.sha256(p.read_bytes()).hexdigest()==journal['sha256']
    assert validation['core_datapack_byte_identical_to_HF9'] and validation['quest_datapack_byte_identical_to_HF4']
    copy(p,'installation/mods/'+p.name)
    for name,target in [('README.md','README.md'),('INSTALLATION_ET_POSE_PNJ.md','docs/INSTALLATION_ET_POSE_PNJ.md'),('HOTFIX_MULTIJOUEUR_R3_HF10.md','HOTFIX_MULTIJOUEUR_R3_HF10.md'),('TESTS_EN_JEU.md','docs/TESTS_EN_JEU.md')]:copy(HERE/'reference_docs'/('HF10_'+name),target)
    for p in audit.iterdir():
        if p.is_file() and p.suffix in {'.json','.txt','.log'}:copy(p,'audit/'+p.name)
    (DEST/'SHA256SUMS').write_text(journal['sha256']+'  installation/mods/'+journal['filename']+'\n')
    packs={k:build.load(OUT/build.FILES[k]) for k in ['core','quests']}
    defined={p.split('/')[1]+':'+p.split('/function/',1)[1][:-11] for pack in packs.values() for p in pack if '/function/' in p and p.endswith('.mcfunction')}
    commands=set()
    for p in DEST.rglob('*.md'):commands.update(re.findall(r'/function ([a-z0-9_.-]+:[a-z0-9_./-]+)',p.read_text()))
    assert commands and not commands-defined,commands-defined
    links=0
    for p in DEST.rglob('*.md'):
        for target in re.findall(r'\]\(([^)]+)\)',p.read_text()):
            if target.startswith(('https://','http://','#')):continue
            assert (p.parent/target.split('#',1)[0]).is_file(),(p,target)
            links+=1
    archive=ROOT/'deliverables'/(NAME+'.zip')
    with zipfile.ZipFile(archive,'w',zipfile.ZIP_DEFLATED,compresslevel=9) as z:
        for p in sorted(DEST.rglob('*')):
            if p.is_file():z.write(p,NAME+'/'+p.relative_to(DEST).as_posix())
    with zipfile.ZipFile(archive) as z:
        assert z.testzip() is None
        jars=[n for n in z.namelist() if n.endswith('.jar')];assert len(jars)==1
        assert hashlib.sha256(z.read(jars[0])).hexdigest()==journal['sha256']
        assert not [n for n in z.namelist() if n.endswith('.zip')]
    result={'file':str(archive),'bytes':archive.stat().st_size,'sha256':hashlib.sha256(archive.read_bytes()).hexdigest(),'journal_sha256':journal['sha256'],'public_function_paths_checked':len(commands),'local_doc_links_checked':links,'only_changed_binary':True,'no_datapack_reset_or_rebinding':True}
    (OUT/'release_manifest.json').write_text(json.dumps(result,ensure_ascii=False,indent=2)+'\n');print(json.dumps(result,ensure_ascii=False,indent=2));return result
if __name__=='__main__':release()
