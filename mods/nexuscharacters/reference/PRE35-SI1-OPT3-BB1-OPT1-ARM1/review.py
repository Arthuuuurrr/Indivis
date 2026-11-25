from pathlib import Path
import json,math
from PIL import Image,ImageDraw,ImageFont
R=Path(__file__).resolve().parent;P=R.parent/'presets_import';Q=P/'previous/npc/qa/evidence-arm1-final-assets';OUT=R/'qa/review';OUT.mkdir(exist_ok=True)
entries=json.loads((P/'qa/build.json').read_text())['entries'];font=ImageFont.truetype('/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf',12)
for kind in ['coiffures','barbes','oreilles']:
 rows=[(i,e)for i,e in enumerate(entries)if e['kind']==kind]
 for page in range(math.ceil(len(rows)/12)):
  subset=rows[page*12:(page+1)*12];sheet=Image.new('RGB',(1200,math.ceil(len(subset)/4)*240),'#eee8da');draw=ImageDraw.Draw(sheet)
  for cell,(i,e)in enumerate(subset):
   x=cell%4*300;y=cell//4*240
   for side in [0,1]:
    name=f'preset-{i:02d}-back.png'if side else f'preset-{i:02d}-{kind}-{e["id"]}.png'
    with Image.open(Q/name)as source:
     image=source.crop((1100,200,1700,980));image.thumbnail((150,300));sheet.paste(image,(x+side*150,y+25))
   draw.text((x+5,y+5),f'{i:02d} {e["label"]}',fill='black',font=font)
  sheet.save(OUT/f'{kind}-{page+1}.png')
print('ARMOR_CONTACT_SHEETS_READY',len(list(OUT.glob('*.png'))))
