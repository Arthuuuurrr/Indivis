from pathlib import Path
import json, os, shutil, subprocess, zipfile
ROOT = Path(__file__).resolve().parents[2]
WORK = ROOT / 'pre28'
RT = ROOT / 'runtime'
GAME = WORK / 'qa/game'
MODS = GAME / 'mods'
MODS.mkdir(parents=True, exist_ok=True)
for name in ['fabric-api.jar', 'skinlayers.jar', 'sodium.jar', 'immediatelyfast.jar']:
    shutil.copy2(RT / 'game/mods' / name, MODS / name)
shutil.copy2(WORK / 'NexusCharacters-Indivis-1.21.11-BETA-1.0-PRE28.jar', MODS / 'NexusCharacters-PRE28.jar')
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
with (WORK / 'qa/render.log').open('w') as log:
    result = subprocess.run(cmd, cwd=ROOT, env=env, stdout=log, stderr=subprocess.STDOUT)
logtext = (WORK / 'qa/render.log').read_text()
print(logtext[-9000:])
assert result.returncode == 0 and 'PRE28_MENU_PASS' in logtext
