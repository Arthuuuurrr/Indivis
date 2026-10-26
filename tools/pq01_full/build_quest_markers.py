"""Assemblage actuel HF12/QMARK2 ; trois composants, vérifications dédiées."""
import json, hashlib
import build
from quest_markers import apply

FILES={k:build.FILES[k] for k in ['core','quests']}

def main():
 packs,audit=build.build()
 print(json.dumps({'version':'HF12/QMARK2','files':build.FILES},ensure_ascii=False,indent=2))

if __name__=='__main__':main()
