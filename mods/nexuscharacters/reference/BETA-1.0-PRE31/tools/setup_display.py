from pathlib import Path
import shutil
R=Path(__file__).resolve().parents[1]/'runtime/xvfb/usr/bin'
# The disposable test display invokes the locally unpacked xkbcomp.
data=(R/'Xvfb').read_bytes();assert b'/usr/bin' in data
(R/'Xvfb-local').write_bytes(data.replace(b'/usr/bin',b'/tmp/xkb'));(R/'Xvfb-local').chmod(0o755)
target=Path('/tmp/xkb');target.mkdir(exist_ok=True);shutil.copy2(R/'xkbcomp',target/'xkbcomp')
