#!/usr/bin/env python3
"""Execute the audited dispatcher in an isolated Fabric server, without private mods or players."""
import hashlib
import json
import os
from pathlib import Path
import re
import shutil
import signal
import subprocess
import sys
import leovic_smoke_cases

ROOT = Path(__file__).resolve().parents[1]


def prepare():
    fixture = (ROOT / 'src/test/resources/core-1.5.7-dispatch.mcfunction').read_bytes()
    lines = fixture.decode().splitlines()
    assert hashlib.sha256('\n'.join(lines).encode()).hexdigest() == '7c2fa10c295379b9bab0ac4e5157ebe916a42a30d070408d7f57a3859acc6b4a'
    branches = [(int(m[1]), m[2]) for line in lines if (m := re.fullmatch(
        r'execute if score @s CAP_FLAG matches 1 if score @s CAP_QSEQ matches (\d+) run function (capitale:\S+)', line))]
    assert len(branches) == 38
    run = ROOT / 'build/smoke-server'
    if run.exists():
        shutil.rmtree(run)
    pack = run / 'world/datapacks/voices_guard_smoke'
    pack.mkdir(parents=True)
    (run / 'eula.txt').write_text('eula=true\n')
    (run / 'server.properties').write_text('server-ip=127.0.0.1\nserver-port=0\nonline-mode=false\n'
        'level-type=minecraft:flat\ngenerate-structures=false\nview-distance=2\nsimulation-distance=2\n'
        'function-permission-level=4\npause-when-empty-seconds=-1\nmax-tick-time=180000\n')
    (pack / 'pack.mcmeta').write_text(json.dumps({'pack': {'description': 'Isolated Voices dispatcher test',
        'min_format': [94, 1], 'max_format': [94, 1]}}))

    def function(name, text):
        namespace, path = name.split(':')
        file = pack / 'data' / namespace / 'function' / (path + '.mcfunction')
        file.parent.mkdir(parents=True, exist_ok=True)
        file.write_text(text)

    function('capitale:quest/dialogue/sequence/dispatch_self', fixture.decode())
    native_next = {101: 102, 102: 103, 103: 0, 201: 202, 202: 203, 203: 0}
    targets = {}
    for sequence, target in branches:
        outcome = native_next.get(sequence, 0)
        if target in targets:
            assert targets[target] == outcome
        targets[target] = outcome
    # Deliberate test stubs: count invocations and expose the native sequence transition.
    # No real quest, reward, NPC, camera or profile code is executed by this smoke test.
    for target, outcome in targets.items():
        function(target, f'scoreboard players add @s IV_CALLS 1\nscoreboard players set @s CAP_QSEQ {outcome}\n'
            f'scoreboard players set @s CAP_QSEQ_TIMER {20 if outcome else 0}\n')
    tag = pack / 'data/minecraft/tags/function/load.json'
    tag.parent.mkdir(parents=True, exist_ok=True)
    tag.write_text(json.dumps({'values': ['iv_voice_smoke:load']}))
    a = '@e[type=minecraft:marker,tag=iv_voice_a,limit=1]'
    b = '@e[type=minecraft:marker,tag=iv_voice_b,limit=1]'
    leovic_commands, leovic_expected = leovic_smoke_cases.install(function, a, b)
    load = ['scoreboard objectives add ' + name + ' dummy' for name in
        ['CAP_FLAG', 'CAP_QSEQ', 'CAP_QSEQ_TIMER', 'IV_CALLS'] + leovic_smoke_cases.OBJECTIVES]
    load += ['scoreboard objectives add QuestChoix trigger']
    load += ['forceload add 0 0', 'schedule function iv_voice_smoke:run 10t replace']
    function('iv_voice_smoke:load', '\n'.join(load) + '\n')
    commands = ['summon minecraft:marker 0 80 0 {Tags:["iv_voice_a","iv_voice_all"]}',
        'summon minecraft:marker 1 80 0 {Tags:["iv_voice_b","iv_voice_all"]}']
    expected = []
    for sequence, target in branches:
        outcome = targets[target]
        token = f'IV_VOICES_BRANCH_{sequence}_OK'
        expected.append(token)
        commands += [f'scoreboard players set {a} CAP_QSEQ {sequence}', f'scoreboard players set {a} IV_CALLS 0',
            f'execute as {a} run function capitale:quest/dialogue/sequence/dispatch_self',
            f'execute as {a} if score @s IV_CALLS matches 1 if score @s CAP_QSEQ matches {outcome} '
            f'if score @s CAP_QSEQ_TIMER matches {20 if outcome else 0} run say {token}']
    for actor, sequence in [(a, 101), (b, 201)]:
        commands += [f'scoreboard players set {actor} CAP_QSEQ {sequence}', f'scoreboard players set {actor} IV_CALLS 0']
    commands += ['execute as @e[type=minecraft:marker,tag=iv_voice_all] run function capitale:quest/dialogue/sequence/dispatch_self',
        f'execute as {a} if score @s IV_CALLS matches 1 if score @s CAP_QSEQ matches 102 '
        f'as {b} if score @s IV_CALLS matches 1 if score @s CAP_QSEQ matches 202 run say IV_VOICES_TWO_SOURCES_OK']
    expected.append('IV_VOICES_TWO_SOURCES_OK')
    commands += [f'scoreboard players set {a} CAP_QSEQ 101', f'scoreboard players set {a} IV_CALLS 0']
    for call, sequence in [(1, 102), (2, 103), (3, 0)]:
        token = f'IV_VOICES_GERAUD_STEP_{call}_OK'
        expected.append(token)
        commands += [f'execute as {a} run function capitale:quest/dialogue/sequence/dispatch_self',
            f'execute as {a} if score @s IV_CALLS matches {call} if score @s CAP_QSEQ matches {sequence} '
            f'if score @s CAP_QSEQ_TIMER matches {20 if sequence else 0} run say {token}']
    commands += leovic_commands
    expected += leovic_expected
    commands += ['say IV_VOICES_SMOKE_FINISHED', 'stop']
    expected.append('IV_VOICES_SMOKE_FINISHED')
    function('iv_voice_smoke:run', '\n'.join(commands) + '\n')
    return expected


def main():
    expected = prepare()
    output = ROOT / 'build/sequence-guard-smoke.log'
    with output.open('w') as log:
        process = subprocess.Popen(['bash', 'gradlew', '--no-daemon', '--console=plain', 'runServer', '--args=nogui'],
            cwd=ROOT, stdout=log, stderr=subprocess.STDOUT, start_new_session=True)
        try:
            code = process.wait(timeout=240)
        except subprocess.TimeoutExpired:
            os.killpg(process.pid, signal.SIGTERM)
            try:
                process.wait(timeout=10)
            except subprocess.TimeoutExpired:
                os.killpg(process.pid, signal.SIGKILL)
            raise RuntimeError('Isolated dispatcher smoke test timed out; see ' + str(output))
    text = output.read_text()
    missing = [token for token in expected if token not in text]
    ready = 'garde anti-cascade du core 1.5.7 active' in text
    result = {'success': code == 0 and not missing and ready, 'exit_code': code, 'guard_active': ready,
        'checks': len(expected) - 1, 'missing_checks': missing,
        'leovic_native_checks': sum(token.startswith('IV_VOICES_LEOVIC_') for token in expected),
        'scope': 'Fabric development server, audited dispatcher and 14 native Leovic functions; marker sources, instrumented branch entries and stubbed private/world services; no player, client rendering or real escort'}
    (ROOT / 'build/sequence-guard-smoke.json').write_text(json.dumps(result, indent=2) + '\n')
    print(json.dumps(result, ensure_ascii=False))
    if not result['success']:
        print(text[-16000:], file=sys.stderr)
        raise SystemExit(1)


if __name__ == '__main__':
    main()
