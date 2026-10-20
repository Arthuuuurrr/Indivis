#!/usr/bin/env python3
"""Test x3 source volume and the output limiter with Minecraft's exact Linux OpenAL library.

Uses a private loopback context: no audio device, player, global mixer or recording is modified.
"""
import array
import ctypes as C
import hashlib
import json
import math
from pathlib import Path
import urllib.request
import zipfile

ROOT = Path(__file__).resolve().parents[1]
NATIVES_URL = 'https://libraries.minecraft.net/org/lwjgl/lwjgl-openal/3.3.3/lwjgl-openal-3.3.3-natives-linux.jar'
NATIVES_SHA256 = '9030fed928a71eac2fdf4ce1643cc4ec724d8710e3b437bdce750bdc9e982b2a'


def native_library():
    folder = ROOT / 'build/playback-volume-native'
    folder.mkdir(parents=True, exist_ok=True)
    jar = folder / 'lwjgl-openal-3.3.3-natives-linux.jar'
    if not jar.exists():
        with urllib.request.urlopen(NATIVES_URL, timeout=45) as response:
            jar.write_bytes(response.read())
    assert hashlib.sha256(jar.read_bytes()).hexdigest() == NATIVES_SHA256
    library = folder / 'libopenal.so'
    with zipfile.ZipFile(jar) as archive:
        library.write_bytes(archive.read('linux/x64/org/lwjgl/openal/libopenal.so'))
    return C.CDLL(str(library))


def configure(a):
    signatures = {
        'alcLoopbackOpenDeviceSOFT': ([C.c_char_p], C.c_void_p),
        'alcCreateContext': ([C.c_void_p, C.POINTER(C.c_int)], C.c_void_p),
        'alcMakeContextCurrent': ([C.c_void_p], C.c_bool),
        'alcDestroyContext': ([C.c_void_p], None),
        'alcCloseDevice': ([C.c_void_p], C.c_bool),
        'alcGetIntegerv': ([C.c_void_p, C.c_int, C.c_int, C.POINTER(C.c_int)], None),
        'alcRenderSamplesSOFT': ([C.c_void_p, C.c_void_p, C.c_int], None),
        'alIsExtensionPresent': ([C.c_char_p], C.c_bool),
        'alGetString': ([C.c_int], C.c_char_p),
        'alGetFloat': ([C.c_int], C.c_float),
        'alGetError': ([], C.c_int),
        'alGenSources': ([C.c_int, C.POINTER(C.c_uint)], None),
        'alDeleteSources': ([C.c_int, C.POINTER(C.c_uint)], None),
        'alGenBuffers': ([C.c_int, C.POINTER(C.c_uint)], None),
        'alDeleteBuffers': ([C.c_int, C.POINTER(C.c_uint)], None),
        'alSourcef': ([C.c_uint, C.c_int, C.c_float], None),
        'alSourcei': ([C.c_uint, C.c_int, C.c_int], None),
        'alGetSourcef': ([C.c_uint, C.c_int, C.POINTER(C.c_float)], None),
        'alBufferData': ([C.c_uint, C.c_int, C.c_void_p, C.c_int, C.c_int], None),
        'alSourcePlay': ([C.c_uint], None),
        'alSourceStop': ([C.c_uint], None),
        'alDistanceModel': ([C.c_int], None),
    }
    for name, (args, result) in signatures.items():
        method = getattr(a, name)
        method.argtypes, method.restype = args, result


def source_float(a, source, property):
    value = C.c_float()
    a.alGetSourcef(source, property, C.byref(value))
    return value.value


def render(a, amplitude, gain):
    device = a.alcLoopbackOpenDeviceSOFT(None)
    assert device
    attributes = (C.c_int * 9)(0x1007, 48000, 0x1990, 0x1501, 0x1991, 0x1406, 0x199A, 1, 0)
    context = a.alcCreateContext(device, attributes)
    assert context and a.alcMakeContextCurrent(context)
    try:
        limiter = C.c_int()
        a.alcGetIntegerv(device, 0x199A, 1, C.byref(limiter))
        assert limiter.value == 1 and a.alIsExtensionPresent(b'AL_SOFT_gain_clamp_ex')
        assert a.alGetFloat(0x200E) >= 3
        version = a.alGetString(0xB002).decode()
        source, other, buffer = C.c_uint(), C.c_uint(), C.c_uint()
        a.alGenSources(1, C.byref(source)); a.alGenSources(1, C.byref(other))
        a.alGenBuffers(1, C.byref(buffer))
        samples = array.array('h', (round(32767 * amplitude * math.sin(2 * math.pi * 500 * n / 48000)) for n in range(48000)))
        data = (C.c_short * len(samples)).from_buffer(samples)
        a.alBufferData(buffer, 0x1101, data, len(samples) * 2, 48000)
        a.alDistanceModel(0)
        a.alSourcei(source, 0x1009, buffer.value)
        a.alSourcei(source, 0x202, 1)
        a.alSourcef(source, 0x100E, gain)
        a.alSourcef(source, 0x100A, gain)
        assert abs(source_float(a, source, 0x100E) - gain) < .0001
        # A different source keeps the default profile.
        assert source_float(a, other, 0x100A) == 1 and source_float(a, other, 0x100E) == 1
        a.alSourcePlay(source)
        output = (C.c_float * 96000)()
        a.alcRenderSamplesSOFT(device, output, 48000)
        stable = output[4096:]
        result = {'rms': math.sqrt(math.fsum(x * x for x in stable) / len(stable)),
            'peak': max(abs(x) for x in output), 'version': version, 'limiter': True}
        a.alSourceStop(source)
        a.alSourcef(source, 0x100E, 1)
        assert source_float(a, source, 0x100E) == 1
        a.alDeleteSources(1, C.byref(source)); a.alDeleteSources(1, C.byref(other))
        a.alDeleteBuffers(1, C.byref(buffer))
        assert a.alGetError() == 0
        return result
    finally:
        a.alcMakeContextCurrent(None)
        a.alcDestroyContext(context)
        a.alcCloseDevice(device)


def main():
    a = native_library(); configure(a)
    normal = render(a, .02, 1)
    boosted = render(a, .02, 3)
    loud = render(a, .9, 3)
    ratio = boosted['rms'] / normal['rms']
    assert 2.98 <= ratio <= 3.02, ratio
    assert loud['peak'] <= 1.0001, loud
    report = {'success': True, 'openal': normal['version'], 'natives_sha256': NATIVES_SHA256,
        'quiet_signal_rms_ratio': ratio, 'loud_signal_peak': loud['peak'], 'output_limiter_active': True,
        'other_source_unchanged': True, 'source_ceiling_restored': True,
        'scope': 'Minecraft Linux OpenAL 3.3.3 natives, private loopback context; no Minecraft client listening test'}
    (ROOT / 'build/playback-volume-check.json').write_text(json.dumps(report, indent=2) + '\n')
    print(json.dumps(report))


if __name__ == '__main__':
    main()
