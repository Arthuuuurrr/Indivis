"""Native post-voice Léovic functions; marker sources, no private mods or real escorts."""
import hashlib
import json
import re
from pathlib import Path

OBJECTIVES = ['CAP_QDIALOG_OWNER', 'CAP_QDIALOG_KEEP', 'CAP_QDIALOG_TOKEN', 'CAP_GUIDE_LOCK',
    'CAP_QUETEACTIVE', 'QUEST_GARDEPORT', 'QUEST_PROLOGUE', 'QUEST_SPAWN', 'CAP_DLG_RNG',
    'IV_ESCORT', 'IV_BLOCKED', 'IV_SETUP']


def install(function, a, b):
    root = Path(__file__).resolve().parents[1]
    fixture = json.loads((root / 'src/test/resources/core-1.5.7-leovic.json').read_text())
    assert fixture['coreSha256'] == '9b9c20bbc0a28d85384d7bb5e7d00199fee87af795fc081a24e3f0971c61e9df'
    counted = {'capitale:quest/le_registre_du_port/sequence/' + name + '_self' for name in
        ['leovic_offer_l2', 'leovic_reafficher_choix', 'leovic_accept_l2']}
    for entry in fixture['functions'].values():
        for objective in re.findall(r'(?:if|unless) score @s (\w+)', entry['source']):
            if objective not in OBJECTIVES and objective not in ['CAP_FLAG', 'CAP_QSEQ', 'CAP_QSEQ_TIMER', 'QuestChoix']:
                OBJECTIVES.append(objective)
    for name, entry in fixture['functions'].items():
        source = entry['source']
        assert hashlib.sha256(source.encode()).hexdigest() == entry['sha256'], name
        # The three branch entry counters are test instrumentation; native commands are intact.
        function(name, ('scoreboard players add @s IV_CALLS 1\n' if name in counted else '') + source)
    for name in ['player/ensure_runtime_self', 'dialogue/sound/parole_quete_self', 'quest/guidage/sync_self']:
        function('capitale:' + name, 'scoreboard players add @s IV_SETUP 0\n')
    function('capitale:quest/le_registre_du_port/escort/start_self', 'scoreboard players add @s IV_ESCORT 1\n')
    for name in ['quest/blocked_guide_self', 'quest/blocked_active_self']:
        function('capitale:' + name, 'scoreboard players add @s IV_BLOCKED 1\n')
    function('capitale:dialogue/random/roll_3_self', 'scoreboard players set @s CAP_DLG_RNG 1\n')
    reset = {'QUEST_SPAWN': 100, 'QUEST_GARDEPORT': 1, 'CAP_QUETEACTIVE': 4, 'CAP_GUIDE_LOCK': 0,
        'CAP_QDIALOG_OWNER': 2, 'CAP_QDIALOG_KEEP': 1, 'CAP_QSEQ': 0, 'CAP_QSEQ_TIMER': 0,
        'IV_ESCORT': 0, 'IV_BLOCKED': 0, 'IV_CALLS': 0}
    function('iv_voice_smoke:leovic_reset', '\n'.join(
        f'scoreboard players set @s {name} {value}' for name, value in reset.items()) + '\n')
    commands, expected = [], []

    def run(name, actor=a):
        commands.append(f'execute as {actor} run function {name}')

    def set_score(name, value, actor=a):
        commands.append(f'scoreboard players set {actor} {name} {value}')

    def check(label, scores, actor=a):
        token = 'IV_VOICES_LEOVIC_' + label + '_OK'
        expected.append(token)
        conditions = ' '.join(f'if score @s {name} matches {value}' for name, value in scores.items())
        commands.append(f'execute as {actor} {conditions} run say {token}')

    dispatch = 'capitale:quest/dialogue/sequence/dispatch_self'
    choices = 'capitale:quest/dialogue/dispatch_self'
    run('iv_voice_smoke:leovic_reset')
    set_score('CAP_QSEQ', 502)
    # The voice completion hands TIMER=0 to the native dispatcher. No choice is auto-selected.
    run(dispatch)
    menu = {'CAP_QSEQ': 0, 'CAP_QSEQ_TIMER': 0, 'CAP_QDIALOG_OWNER': 2,
        'CAP_QDIALOG_KEEP': 1, 'QUEST_GARDEPORT': 1, 'IV_ESCORT': 0, 'IV_CALLS': 1}
    check('MENU_AFTER_VOICE', menu)
    run(dispatch); check('MENU_NOT_REPLAYED', menu)
    set_score('QuestChoix', 11); run(choices)
    check('QUESTION', {'CAP_QSEQ': 501, 'CAP_QSEQ_TIMER': 20, 'QUEST_GARDEPORT': 1, 'IV_ESCORT': 0})
    commands += [f'scoreboard players remove {a} CAP_QSEQ_TIMER 1',
        f'execute as {a} if score @s CAP_QSEQ_TIMER matches 0 run function {dispatch}']
    check('UNVOICED_DELAY', {'CAP_QSEQ': 501, 'CAP_QSEQ_TIMER': 19, 'IV_CALLS': 1})
    set_score('CAP_QSEQ_TIMER', 0); run(dispatch)
    check('QUESTION_MENU_RETURN', {'CAP_QSEQ': 0, 'CAP_QSEQ_TIMER': 0, 'CAP_QDIALOG_OWNER': 2,
        'CAP_QDIALOG_KEEP': 1, 'QUEST_GARDEPORT': 1, 'IV_ESCORT': 0, 'IV_CALLS': 2})
    set_score('QuestChoix', 10); run(choices)
    check('ACCEPT_WAITS_FOR_VOICE', {'CAP_QSEQ': 512, 'CAP_QSEQ_TIMER': 20,
        'QUEST_GARDEPORT': 20, 'QUEST_PROLOGUE': 20, 'IV_ESCORT': 0})
    set_score('CAP_QSEQ_TIMER', 0); run(dispatch)
    check('ESCORT_AFTER_VOICE', {'CAP_QSEQ': 0, 'CAP_QSEQ_TIMER': 0, 'IV_ESCORT': 1})
    run(dispatch)
    check('ESCORT_ONCE', {'CAP_QSEQ': 0, 'IV_ESCORT': 1})
    run('iv_voice_smoke:leovic_reset')
    set_score('QuestChoix', 12); run(choices)
    check('DECLINE', {'QUEST_GARDEPORT': 2, 'CAP_QSEQ': 0, 'CAP_QSEQ_TIMER': 0,
        'CAP_QDIALOG_OWNER': 0, 'IV_ESCORT': 0})
    run('iv_voice_smoke:leovic_reset')
    set_score('CAP_GUIDE_LOCK', 1); set_score('QuestChoix', 10); run(choices)
    check('BUSY_GUIDE', {'QUEST_GARDEPORT': 1, 'CAP_QSEQ': 0, 'IV_BLOCKED': 1, 'IV_ESCORT': 0})
    run('iv_voice_smoke:leovic_reset', b)
    set_score('QuestChoix', 11, b); run(choices, b)
    check('SECOND_SOURCE', {'QUEST_GARDEPORT': 1, 'CAP_QSEQ': 501, 'CAP_QSEQ_TIMER': 20, 'IV_ESCORT': 0}, b)
    check('FIRST_SOURCE_INTACT', {'CAP_GUIDE_LOCK': 1, 'CAP_QSEQ': 0, 'IV_BLOCKED': 1, 'IV_ESCORT': 0})
    return commands, expected
