"""Assemble les preuves de l'audit sans reconstruire les fichiers de jeu."""
from pathlib import Path
import json,re,hashlib,zipfile
import build
H=Path(__file__).resolve().parent;R=H/'reports'

def main():
    static=json.loads((R/'prologue_audit_static.json').read_text())
    protocol=(R/'prologue_audit_protocol.txt').read_text()
    native=(R/'prologue_audit_native.txt').read_text()
    m=re.search(r'^PASS (\d+) PQ01 real client protocol checks; blocks=94, manual=28;',protocol,re.M)
    assert m and static['status']=='PASS'
    n=re.search(r'^PASS (\d+) native checks',native);assert n
    lines=native.splitlines();prefix='prologue audit: '
    blocks=[s for s in lines if s.startswith(prefix+'bloc natif ')]
    edges=[s for s in lines if s.startswith(prefix+'choix natif ')]
    assert len(blocks)==411 and len(edges)==317
    assert len({s.split('bloc natif ',1)[1].split(' choix=')[0] for s in blocks})==94
    digest=hashlib.sha256((H/'output'/build.JOURNAL_FILENAME).read_bytes()).hexdigest()
    assert digest=='a401eec8b5fbe1396c651715cd457a01ff86cb531c5bcc5686352b44a9f19285'
    assert digest==json.loads((R/'prologue_audit_native_fixture.json').read_text())['mod_production_sha256']==json.loads((R/'prologue_audit_protocol_fixture.json').read_text())['jar_sha256']
    assert digest==json.loads((R/'hf4.json').read_text())['production_jar_sha256']
    for kind,expected in static['sha256'].items():assert hashlib.sha256((H/'output'/build.FILES[kind]).read_bytes()).hexdigest()==expected
    server=(R/'prologue_audit_native_server.log').read_text()
    assert not any(x in server for x in ['Failed to load function','Failed to parse','MixinApplyError','[Indivis PQ01-R3-HF11]','[Indivis PQ02 1.0]','java.lang.AssertionError'])
    compat=json.loads((R/'prologue_nexus_compatibility.json').read_text())
    assert all(x['identical_to_opt1'] for x in compat['score_policy_classes'].values())
    assert not compat['removed_classes']
    fresh={'static_checks':static['checks'],'internal_prologue_functions':static['resources'],'pq01_dialogue_blocks':94,'pq01_direct_choices':317,'pq01_client_protocol_checks':int(m[1]),'pq01_server_assertions':int(n[1]),'native_loaded_functions':json.loads((R/'prologue_audit_native_fixture.json').read_text())['functions']}
    report={'status':'PASS_WITH_DOCUMENTED_LIMITS','date':'2026-10-29','production_revision':'HF12-PQ02-R1-HF4','production_jar_sha256':digest,'production_files_changed':[],'new_checks':fresh,'same_jar_previous_pq02_validation':{'native_assertions':2425,'client_protocol_assertions':566,'native_readiness_assertions':12,'gautier_assertions':15,'pq02_blocks':111},'datapack_sha256':static['sha256'],'nexus_opt3_score_policy_unchanged_from_test_reference':True,'native_scope':['94 PQ01 blocks','317 direct choice routes through trigger and production dispatch','Geraud stationary spawn guard and movement proximity','Perrin A-B-A real EasyNPC navigation for two players','Perrin owner isolation and public zero-speed preservation','PQ01 completion to canonical PQ02=30 and once-only Crown reputation','PQ03/PQ04/PQ05 offer conditions and final prologue normalization'],'reserved_findings':[{'id':'GUI_LEOVIC','status':'UNRESOLVED_IN_LIVE_CLIENT','detail':'HF4 native and protocol checks pass; actual caller of original ECRAN_FERME is absent from supplied logs; no rendered client was run.'},{'id':'LEGACY_SHARED_ESCORTS','status':'STATIC_DESIGN_CONFIRMED_LIVE_CONFLICT_NOT_REPRODUCED','detail':'Aurele/Roch/Colin use global guide armor stands and reset their route state in start functions; their per-character lock does not itself establish per-player scene isolation. No real-map two-player escort tested.'}],'limits':['Graphics, actual map obstacles, cameras as rendered, FPS and voice playback not tested','Native players/connections and Nexus selection are synthetic; no full character selector or disk serialization test','Protocol uses real b10 state/Fabric events but graphical surfaces, client world/entities and C2S output are doubled','All direct PQ01 branches tested with their optional Avaleiv knowledge enabled; banter entry remains removed from Hugues menus','PQ03-PQ05: function/reference integrity, offer conditions and normalization verified; complete map-dependent escorts/payment flow not executed','The initial audit fixture expected PQ02=10 instead of canonical30 and later used an undeclared result objective; corrected in the test fixture only before the final passing run']}
    (R/'prologue_audit.json').write_text(json.dumps(report,ensure_ascii=False,indent=2)+'\n')
    print(json.dumps(report,ensure_ascii=False,indent=2))

if __name__=='__main__':main()
