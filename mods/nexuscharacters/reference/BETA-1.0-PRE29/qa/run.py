from pathlib import Path
import json, os, shutil, subprocess, sys, zipfile
ROOT = Path(__file__).resolve().parents[2]
WORK = ROOT / 'pre29'
RT = ROOT / 'runtime'
world = '--world' in sys.argv
baseline = '--baseline' in sys.argv
rotation = '--rotation' in sys.argv
initial_baseline = '--initial-baseline' in sys.argv
GAME = WORK / ('qa/initial-baseline-game' if initial_baseline else 'qa/world-game' if world else 'qa/baseline-game' if baseline else 'qa/game')
MODS = GAME / 'mods'
MODS.mkdir(parents=True, exist_ok=True)
for previous in MODS.glob('NexusCharacters-*.jar'): previous.unlink()
for name in ['fabric-api.jar', 'skinlayers.jar', 'sodium.jar', 'immediatelyfast.jar']:
    shutil.copy2(RT / 'game/mods' / name, MODS / name)
if baseline:
    with zipfile.ZipFile(ROOT / 'pre27-delivery/NexusCharacters-Indivis-1.21.11-BETA-1.0-PRE27.jar') as src, zipfile.ZipFile(MODS / 'NexusCharacters-PRE28.jar', 'w', zipfile.ZIP_DEFLATED) as dst:
        for item in src.infolist(): dst.writestr(item, src.read(item))
        for p in (WORK / 'classes/net/tompsen/nexuscharacters').glob('IndivisReadability*.class'):
            dst.write(p, p.relative_to(WORK / 'classes'))
else: shutil.copy2(WORK / ('NexusCharacters-Indivis-1.21.11-BETA-1.0-PRE28.jar' if initial_baseline else 'NexusCharacters-Indivis-1.21.11-BETA-1.0-PRE29.jar'), MODS / 'NexusCharacters-Test.jar')
if world:
    shutil.copy2(WORK / 'qa/puffish_skills.jar', MODS / 'puffish_skills.jar')
    target = GAME / 'saves/pre28-menu-test'
    if not target.exists(): shutil.copytree(RT / 'game/saves/pre21-actual-player', target)
with zipfile.ZipFile(ROOT / 'pre21/headless-agent.jar') as src, zipfile.ZipFile(MODS / 'headless-agent.jar', 'w') as dst:
    for item in src.infolist(): dst.writestr(item, src.read(item))
    dst.writestr('fabric.mod.json', json.dumps({'schemaVersion':1, 'id':'headlessagent', 'version':'1', 'environment':'client'}))
cp = (RT / 'classpath.txt').read_text().strip() + ':' + str(RT / 'fabric-loader.jar') + ':' + str(RT / 'client-intermediary.jar')
compile_cp = cp + ':' + ':'.join(map(str, [*MODS.glob('*.jar'), *sorted((ROOT / 'pre21').glob('fabric-*.jar'))]))
classes = WORK / 'qa/classes'
classes.mkdir(exist_ok=True)
subprocess.run([str(WORK / 'jdk/bin/javac'), '-proc:none', '-cp', compile_cp, '-d', str(classes), str(WORK / 'qa/MenuRegression.java')], check=True)
with zipfile.ZipFile(MODS / 'menu-harness.jar', 'w') as z:
    z.writestr('fabric.mod.json', json.dumps({'schemaVersion':1, 'id':'menu_harness', 'version':'28', 'environment':'client', 'entrypoints':{'client':['MenuRegression']}}))
    for p in classes.rglob('*.class'): z.write(p, p.relative_to(classes))
(GAME / 'options.txt').write_text('guiScale:2\nrenderDistance:3\nsimulationDistance:5\nmaxFps:60\n')
env = os.environ.copy()
env.update(LD_LIBRARY_PATH=str(RT / 'osmesa/usr/lib/x86_64-linux-gnu') + ':' + str(RT / 'natives'), LIBGL_ALWAYS_SOFTWARE='true', ALSOFT_DRIVERS='null')
cmd = [str(WORK / 'jdk/bin/java'), '-Xmx1800M', '--add-exports=java.base/jdk.internal.org.objectweb.asm=ALL-UNNAMED', '-javaagent:' + str(ROOT / 'pre21/headless-agent.jar'), '-Dorg.lwjgl.opengl.explicitInit=true', '-Dmixin.debug.export=true', '-Djava.library.path=' + str(RT / 'natives'), '-Dorg.lwjgl.opengl.libname=' + str(RT / 'osmesa/usr/lib/x86_64-linux-gnu/libOSMesa.so.8'), '-Dfabric.gameMappingNamespace=intermediary', '-Dfabric.runtimeMappingNamespace=intermediary', '-Dfabric.gameJarPath=' + str(RT / 'client-intermediary.jar'), '-cp', cp, 'net.fabricmc.loader.impl.launch.knot.KnotClient', '--gameDir', str(GAME), '--assetsDir', str(RT / 'assets'), '--assetIndex', '29', '--version', '1.21.11', '--username', 'PRE28Test', '--uuid', '00000000000000000000000000000001', '--accessToken', 'offline', '--width', '1920', '--height', '1080']
if world: cmd.insert(1, '-Dpre28.world=true')
if rotation: cmd.insert(1, '-Dpre28.rotation=true')
logfile = WORK / ('qa/initial-baseline.log' if initial_baseline else 'qa/world-render.log' if world else 'qa/baseline-render.log' if baseline else 'qa/rotation-render.log' if rotation else 'qa/render.log')
with logfile.open('w') as log:
    result = subprocess.run(cmd, cwd=ROOT, env=env, stdout=log, stderr=subprocess.STDOUT)
logtext = logfile.read_text()
print(logtext[-9000:])
print('CLIENT_EXIT', result.returncode, flush=True)
if initial_baseline:
    assert 'Initial body/cosmetic yaw mismatch before any drag' in logtext
    print('PRE28_INITIAL_YAW_NEGATIVE_CONTROL_PASS')
elif baseline:
    assert 'Avatar width clipped:' in logtext
    print('PRE27_NEGATIVE_CONTROL_PASS: previous fixed-width preview reproduces the defect')
else: assert result.returncode == 0 and 'PRE28_MENU_PASS' in logtext
