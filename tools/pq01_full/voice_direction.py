"""Dossier vocal français : textes canoniques, direction humaine et prompts Qwen séparés."""
from pathlib import Path
import json,sys,csv,hashlib
import build
from model import N,ROLES

HERE=Path(__file__).resolve().parent
DEST=HERE/'voice_direction'
PROFILES={
 'altheon':("Althéon Brumeforge — capitaine", "Voix masculine mature, médium grave, assise et souple. Diction précise, débit posé mais vivant. L'autorité vient de la maîtrise et de l'écoute. Accueillir un passager secouru, puis prendre au sérieux son amnésie. Distinguer nettement ce qui est établi de ce qui reste inconnu. Rassurer sans promettre de guérison. Les réponses sur la mission sont factuelles ; la prudence ne cache pas un secret. À la fin, laisser apparaître une sollicitude personnelle discrète. Conserver la même identité vocale que dans MQ03.", "Laisser respirer les articulations du raisonnement ; rendre les questions réellement ouvertes. Après « Je vois... », une petite suspension humaine, puis reprendre avec chaleur. Réduire les pauses dans les deux premières phrases qui forment un seul accueil.", "Froideur administrative, déclamation militaire, monotonie, ton prophétique ou connaissance du mystère de l'amnésie."),
 'geraud':("Géraud Rivet — quartier-maître", "Voix masculine expérimentée, plutôt grave et légèrement rugueuse, sans forçage de gorge. Parole concrète, débit variable, ironie sèche. Se soucier d'abord de l'état physique du passager. Avec Gautier, couper net et rétablir la discipline, puis retrouver aussitôt le ton ordinaire. Sur les quais, orienter efficacement le passager ; derrière la brusquerie, la protection devient plus visible au moment de le laisser partir.", "A4 « Matelot ! » est une rupture brève et ferme, sans hurlement. A5 reste tenu et précis. A7 relâche la tension avec un soupçon de sourire. EX3 laisse une courte hésitation affectueuse, sans lourdeur.", "Pirate caricatural, colère permanente, cynisme cruel, gentillesse uniforme ou hurlement continu."),
 'gautier':("Gautier — matelot", "Voix masculine adulte plutôt jeune, médium clair, diction appliquée et énergique. Compétent, ambitieux, convaincu que le règlement l'aidera à bien faire. Lorsqu'il interpelle Géraud en approche, entrer avec assurance et empressement, puis laisser la phrase en suspens comme prévu. La réprimande provoque une réponse plus rapide et brièvement embarrassée. Dans sa conversation facultative, retrouver de la tenue, défendre son service et son ambition. La timidité est ponctuelle.", "L'interruption se joue pendant son approche : voix dirigée vers Géraud, sans bruitage ni essoufflement ajouté. A6 est un acquiescement vif. Dans la joute avec Hugues, répliques serrées, sérieuses du point de vue de Gautier.", "Adolescent naïf, incapable, bégaiement systématique, soumission apeurée dans toutes les répliques."),
 'hugues':("Hugues — matelot vétéran", "Voix masculine adulte mûre, médium grave, un peu usée mais intelligible. Vétéran débrouillard, peu impressionné par les registres, loyal à ses collègues. Humour populaire discret, porté par les intentions et les fins de phrase. L'accueil quitte une tâche agaçante pour accueillir le passager. Quand le sauvetage est évoqué, faire tomber le sourire un instant avant de revenir à un soulagement retenu.", "Dans la joute, « Oui. » et « Pas encore. » sont courts et secs ; laisser le partenaire répondre sans installer de grands blancs. Les plaintes sur les caisses restent celles d'un professionnel au travail.", "Clown, ivrogne, insolence envers le capitaine, antimilitarisme ou accent comique imposé."),
 'anselme':("Anselme — timonier", "Voix masculine adulte, médium précis, articulation nette. À l'accueil, attention encore prise par les instruments ; devenir disponible une fois le geste terminé. Spécialiste passionné et fier de son métier. Les explications prennent un peu de vitesse et de relief, tout en restant compréhensibles. Corriger « bâtiment » avec une pointe d'orgueil technique, sans rabaisser le passager. Le dirigeable est un outil habituel de son monde, pas une découverte merveilleuse.", "Au début, deux « Un instant... » correspondent à une vérification en cours ; pauses courtes et différentes. Les énumérations techniques se lient. Au quai, commencer une explication avec plaisir, puis se retenir lorsqu'elle pourrait s'allonger.", "Mépris savant, récitation scolaire, émerveillement devant une technologie rarissime, débit précipité incompréhensible."),
 'perrin':("Perrin — matelot sociable", "Voix masculine adulte jeune, médium lumineux, sourire perceptible, débit spontané. Très à l'aise avec les inconnus, curieux et un peu indiscret. Il veut créer immédiatement une complicité. Parler du Port avec plaisir, comme d'un endroit vivant qu'il connaît. Face à l'amnésie, conserver la délicatesse : son intérêt pour l'histoire n'est jamais une moquerie. Dédramatiser les formalités sans dire qu'on peut les éviter.", "Adresses directes, petites relances naturelles, fins de phrase ouvertes. Au mot « malheureusement », laisser passer une vraie limite à ce qu'il sait, puis retrouver la convivialité. Pendant son service, rester disponible sans jouer une course ni une agitation permanente.", "Séduction appuyée, vendeur publicitaire, gouailleur cruel, rire sur l'amnésie ou enthousiasme crié."),
}
CUES={
 'a1':"Découvrir le passager debout : soulagement franc sur l'exclamation, puis conseil protecteur, sans dramatiser la frayeur.",
 'a2':"Donner les faits du sauvetage ; ton utile et direct. Garder la fin disponible pour être interrompu.",
 'a3':"Intervenir avec empressement et assurance en venant vers Géraud. Susprendre la dernière proposition, prêt à poursuivre ; l'interruption coupe cet élan.",
 'a4':"Couper immédiatement : attaque sèche et autorité nette sur ce seul mot. Volume contenu, aucune longue traîne.",
 'a5':"Recadrer avec calme ferme. Marquer l'ordre de retour au poste, puis fermer la remarque sans ajouter de menace.",
 'a6':"Accuser réception vivement. Un bref embarras, discipline immédiate ; repartir après la fin de la réplique.",
 'a7':"Revenir au passager ; relâcher les épaules dans la voix, ironie discrète et sans méchanceté.",
 'a8':"Répondre naturellement à la demande sur le capitaine, puis indiquer sa cabine. Une seule intention de conseil pratique, sans pause cérémonieuse avant les questions.",
 'ga_menu':"Proposer les dernières questions tout en restant occupé ; légère impatience professionnelle, aucune hostilité.",
 'ga_12':"Confirmer le sauvetage avec pudeur et un humour sec ; ne pas chercher les remerciements.",
 'ga_14':"Présenter son métier avec une fierté concrète. Relier les tâches dans un même mouvement de phrase.",
 'ga_16':"Exprimer un respect ancien du capitaine ; l'exigence de l'officier paraît normale et justifiée.",
 'ga_18':"Distinguer l'ordre juste du mauvais moment. Petite pointe amusée sur Gautier, fin indulgente.",
 'ga_20':"Autoriser le départ avec chaleur pratique ; rappeler naturellement qu'il s'agit d'un passager.",
 'gautier_menu':"Reconnaître sa maladresse avec un embarras déjà maîtrisé ; chercher à rétablir une bonne impression.",
 'gautier_23':"Minimiser d'abord la colère de Géraud, puis admettre le mauvais moment. Hésitation brève, pas de panique.",
 'gautier_25':"Réagir vivement au mot « peur », puis rendre son respect de Géraud plus prudent et sincère.",
 'gautier_27':"Affirmer sa compétence sans hésiter. L'ambition se devine dans le regain d'énergie de la seconde phrase.",
 'gautier_29':"Assumer franchement son désir d'avancement. Conviction appliquée, sans arrogance méprisante.",
 'gautier_31':"Donner une consigne d'orientation précise, comme une tâche qu'il maîtrise parfaitement.",
 'gautier_33':"Remercier, puis laisser apparaître une sollicitude un peu moins aisée que son ton réglementaire.",
 'hugues_menu':"Marmonner une lassitude concrète sur les caisses, puis s'apercevoir du passager et l'accueillir correctement.",
 'hugues_36':"Répondre factuellement sur la cargaison, avec le soulagement pratique d'un poids en moins.",
 'hugues_38':"Rappeler simplement l'ordre reçu ; renvoyer les décisions politiques au capitaine, sans insinuation de complot.",
 'hugues_40':"Contrer sa propre plainte avec un sourire. La loyauté envers l'équipage apparaît avant la confidence finale.",
 'hugues_42':"Évoquer un beau pays connu par ses ports ; le regret de ne pas visiter reste léger et terre à terre.",
 'hugues_44':"Devenir un instant grave au souvenir de l'état du rescapé, puis laisser entendre le soulagement de le voir debout.",
 'hugues_46':"Refermer la plainte avec un peu d'humour et souhaiter bonne route sans s'attarder.",
 'anselme_menu':"Terminer une vérification avant de s'adresser au passager. Les suspensions sont des gestes en cours, pas un oubli du texte.",
 'anselme_49':"Nuancer la question puis se passionner pour la maîtrise du pilotage ; accélération modérée et articulation précise.",
 'anselme_51':"Réagir avec un enthousiasme sincère, porté par le plaisir d'un bâtiment qui répond aux commandes.",
 'anselme_53':"Reconnaître honnêtement les limites d'une localisation ; aucune intuition mystérieuse sur le passé du joueur.",
 'anselme_55':"Amorcer un exposé avec plaisir. L'opposition entre court et exact est une taquinerie de spécialiste ; relier clairement les mécanismes.",
 'anselme_57':"Corriger le vocabulaire avec une pointe de fierté, puis partager son intérêt. Le sourire compense la pédanterie.",
 'anselme_59':"Accepter le départ avec soulagement pratique et revenir mentalement aux instruments. Souhait final cordial.",
 'perrin_menu':"Créer immédiatement le contact avec le rescapé ; sourire audible, curiosité chaleureuse plutôt qu'intrusion agressive.",
 'perrin_62':"Se défendre d'une indiscrétion avec un sourire, puis reconnaître le caractère inhabituel de la situation sans rire de l'amnésie.",
 'perrin_64':"Laisser jaillir l'envie d'une bonne histoire, puis reconnaître avec douceur qu'il ne sait rien de plus.",
 'perrin_66':"Parler des quais avec un vrai plaisir de familier ; débit vivant, invitation à découvrir leur animation.",
 'perrin_68':"Faire vivre la diversité du Port par des contrastes légers ; observation souriante, jamais catalogue débité.",
 'perrin_70':"Dédramatiser l'enregistrement en restant clair sur son caractère nécessaire. Pas de conseil pour contourner les règles.",
 'perrin_72':"Encourager le passager à aller voir le capitaine ; confidence amicale qui adoucit sa première impression.",
 'b0':"Contrôler une tâche avec sérieux et efficacité, sans agressivité préalable.",
 'b1':"Réponse brève, sèche et tranquille. Aucun long silence comique autour de ce mot.",
 'b2':"Vérifier le second point avec la même logique de procédure, très naturellement.",
 'b3':"Admettre le registre non signé avec une économie de mots, presque placide.",
 'b4':"Énoncer la conséquence comme une évidence réglementaire ; il croit vraiment à cette logique.",
 'b5':"Faire une remarque d'expérience avec une ironie populaire légère ; viser la paperasse, pas l'intelligence de Gautier.",
 'b6':"Défendre la procédure avec conviction. Pas de colère, ni d'embarras cette fois.",
 'b7':"Constater l'échec de la plaisanterie avec une résignation amusée ; clore la joute sans rire ajouté.",
 'c1':"Lever les yeux de son travail et accueillir le passager à hauteur d'homme. Présentation assurée et cordiale ; lier naturellement la suite de l'accueil.",
 'c2':"Exprimer le soulagement de voir le rescapé remis, puis une inquiétude sincère au souvenir de son état.",
 'c3':"Reconnaître simplement qu'il espérait lui-même une réponse. Franchise attentive, aucune suspicion d'interrogatoire.",
 'c4':"Poser une vraie question avec douceur et attendre l'autre. Ne pas suggérer que le capitaine connaît déjà la réponse.",
 'c5':"Accueillir l'aveu d'amnésie. Brève suspension de réflexion, de l'ordre d'une respiration ; ni gravité prophétique ni grand blanc.",
 'c6':"Admettre les limites de son aide, puis offrir une perspective concrète ; passer de la préoccupation à une autorité protectrice.",
 'ca1':"Rapporter les observations de la vigie avec clarté et mesure. Le secours relève d'une décision normale d'officier.",
 'ca2':"Distinguer précisément position connue et zone non explorée. Éviter toute spéculation ajoutée.",
 'ca3':"Expliquer que la sécurité du rescapé primait. Fermeté tranquille, jamais reproche au joueur.",
 'cb1':"Présenter les faits de la mission officielle. Ton professionnel courant, sans dramatiser les pièces d'artillerie.",
 'cb2':"Expliquer les relations entre États avec recul ; conserver une parole adressée, pas un cours récité.",
 'cb3':"Refuser de transformer des hypothèses en certitudes. Honnêteté intellectuelle, pas rétention mystérieuse d'information.",
 'cc1':"Décrire Avaleiv comme une principauté indépendante connue des routes commerciales. Précision simple, aucun exotisme émerveillé.",
 'cc2':"Situer ce qu'il connaît des routes et des usages, puis en reconnaître la limite ; naturel d'un capitaine expérimenté.",
 'cd1':"Donner une première image utile de la Capitale. Accueil mesuré, sans promettre un refuge parfait.",
 'cd2':"Introduire l'enregistrement comme une formalité concrète. Ton clair et pratique, sans intimidation.",
 'cd3':"Rassurer sur l'absence de souvenirs et le rapport de secours ; énoncer une aide concrète sans promettre de passe-droit.",
 'cd4':"Décrire des peuples et des caractères variés. Réalisme accueillant, prudence sans menace.",
 'ce1':"Reconnaître d'emblée qu'il n'est pas médecin. Délicatesse et honnêteté, sans sécheresse.",
 'ce2':"Redonner de l'espoir avec précaution ; aucune promesse de guérison et aucun sourire déplacé.",
 'ce3':"Conclure simplement qu'il est trop tôt pour renoncer. Chaleur basse, sans formule héroïque.",
 'cx1':"Clore l'entretien et expliquer la suite de l'arrivée ; ordre courant, aucune commande de manœuvre ajoutée.",
 'cx2':"Retenir le passager une dernière seconde pour une attention personnelle, sans suspense lourd.",
 'cx3':"Déculpabiliser le passager avec sincérité. La douceur se renforce sans devenir sentimentale.",
 'cx4':"Donner la permission de reprendre pied avant de chercher plus loin. Fin ouverte et paisible ; laisser la dernière syllabe finir avant la transition de scène.",
 'e1':"Retrouver le passager sur le quai et lui souhaiter la bienvenue. Petite correction amusée sur le lieu exact.",
 'e2':"Préparer les derniers conseils avant de libérer le passage. Efficacité protectrice, rythme lié.",
 'e_127':"Indiquer Léovic et l'enregistrement comme la prochaine étape concrète. Appuyer le nom utile sans solennité.",
 'e_129':"Expliquer comment répondre aux gardes avec pragmatisme ; rassurer sans promettre l'absence de contrôle.",
 'e_131':"Faire comprendre les limites d'accès aux quartiers avec une mise en garde ferme mais sobre.",
 'e_133':"Décrire les obligations de retour du bâtiment ; la fatigue ordinaire du travail affleure.",
 'e_135':"Écarter les remerciements avec pudeur et un humour sec. La bienveillance reste perceptible.",
 'ex1':"Rassembler l'échange et accepter le départ ; courte ponctuation orale, pas de nouvelle attente.",
 'ex2':"Encourager à chercher Léovic et à se familiariser avec l'agitation. Parler comme quelqu'un qui y est habitué.",
 'ex3':"Laisser paraître l'attachement derrière la brusquerie. Courte hésitation, souhait sincère de revoir le passager en bonne santé.",
 'ex4':"Retrouver son énergie de quartier-maître. Congédier avec chaleur et revenir à l'équipage.",
 'f_altheon':"Faire un bref suivi cordial, puis annoncer son retour au travail ; même capitaine, sans relancer l'entretien.",
 'f_gautier':"Annoncer une procédure achevée avec une fierté professionnelle discrète ; consigne nette et utile.",
 'f_hugues':"Retrouver les inventaires avec une lassitude amusée. Sourire complice à celui qui peut partir.",
 'f_anselme':"Savourer un amarrage correct, amorcer une précision technique puis se retenir avec bonne grâce.",
 'f_perrin':"Encourager la découverte de la ville avec chaleur. La plaisanterie sur les bureaux reste légère.",
 'f_geraud':"Indiquer que la voie est libre, puis souhaiter bonne route franchement, sans nouvelle consigne de quête.",
}

COMMON="Français naturel et intelligible. Jeu de dialogue réaliste, adressé à un interlocuteur proche. Variation d'intention à l'intérieur des phrases, diction nette, pauses expressives courtes. Lire exactement le texte fourni, sans annoncer le nom du personnage, l'identifiant ou les consignes. Conserver les noms propres et la ponctuation du texte. Pas de musique, bruitage, applaudissement, rire ou respiration théâtrale ajoutés."

def make():
 DEST.mkdir(exist_ok=True);(DEST/'qwen/prises').mkdir(parents=True,exist_ok=True)
 takes={k:v for k,v in N.items() if v['lines']}
 assert set(CUES)==set(takes),(set(takes)-set(CUES),set(CUES)-set(takes))
 incoming={k:[] for k in takes}
 for src,node in N.items():
  for choice in node['choices']:
   if choice['target'] in incoming and choice['label']!='Continuer.':incoming[choice['target']].append(choice['label'])
 human=['# PQ01 — Direction d’acteur en français','Version du 24 octobre 2026. Six rôles ; dialogues du prologue conservés mot pour mot. Ce dossier remplace la direction R2 qui décrivait encore une scène statique.','## Brief commun',
  "Le protagoniste vient d’être secouru et ne se souvient de rien. L’équipage ignore l’origine de son amnésie. Jouer la sollicitude, les habitudes de travail et les relations entre collègues. Aucun personnage n’annonce un destin, un secret connu ou le mystère des Anciens, du Cœur ou du monolithe. Les dirigeables font partie de la vie courante de l’Empire. Avaleiv est une principauté elfique indépendante, partenaire de livraison de l’Empire.",
  "Français naturel, proximité de conversation, sans accent médiéval ou marin caricatural. Une attaque et une fin de phrase nettes ; les petites répliques restent petites. Le partenaire peut reprendre dès que le sens et la dernière syllabe sont achevés. Les temps de jeu appartiennent à la prise : ils ne demandent pas d’ajouter un silence technique dans le dialogue.",
  "Les textes ci-dessous sont ceux des PNJ. Les questions du joueur servent seulement de contexte lorsqu’elles sont indiquées. Les identifiants servent au classement des prises et ne se lisent pas.",
  '## Continuité et diction',"Différencier les six rôles par l’intention, l’énergie et le rythme, autant que par le timbre. Garder la même voix au large et au quai. Conserver les prononciations du projet pour Althéon, Avaleiv, Léovic et les noms de lieux ; utiliser une prise de référence commune avant l’enregistrement. Gautier, Hugues, Anselme et Perrin sont annoncés par leur seul prénom.",
  '## Fiches des six rôles']
 prompts=['# PQ01 — Prompts Qwen en français','Version du 24 octobre 2026. Aucun audio généré ; textes et instructions sont fournis séparément.','## Utilisation',
  "Ces consignes visent Qwen3-TTS VoiceDesign ou CustomVoice 1.7B avec contrôle par instructions. Dans une interface, placer la réplique seule dans le champ de texte, choisir le français, puis placer le prompt dans le champ de description/instructions. Avec l’API officielle, les champs correspondants sont `text`, `language=\"French\"` et `instruct`. CustomVoice demande aussi une voix `speaker` réellement prise en charge par le modèle ; ce dossier n’en invente pas.",
  "CustomVoice 0.6B ignore les instructions de style dans l’implémentation officielle consultée. Une interface de clonage Base n’accepte pas nécessairement ces mêmes instructions. Les fichiers JSONL sont un manifeste de prises à adapter à l’interface utilisée ; ils ne constituent pas une commande universelle d’import.",
  "Réutiliser la même voix choisie et la même référence pour chaque personnage. Une description VoiceDesign répétée ne garantit pas à elle seule un timbre strictement identique sur toutes les prises. Pour une voix déjà doublée, conserver l’identité existante, en particulier celle d’Althéon dans MQ03.",
  "Le dossier `qwen/prises/` contient un prompt complet et le texte exact pour chaque prise. `qwen/prises_pq01.jsonl` rassemble les mêmes champs pour un traitement par lot. Les noms propres et les points de suspension restent dans `text` ; ne pas y ajouter de didascalies ou de balises SSML non documentées.",
  '## Prompts de personnage']
 for role,(title,profile,beats,avoid) in PROFILES.items():
  human += ['### '+title,profile,'**Rythme et ruptures.** '+beats,'**À éviter.** '+avoid]
  prompt=COMMON+' Personnage : '+ROLES[role]+'. '+profile+' '+beats+' Éviter : '+avoid
  (DEST/'qwen'/f'{role}.txt').write_text(prompt+'\n')
  prompts += ['### '+title,'```text',prompt,'```']
 human += ['## Déroulé et direction par prise',"L’ordre ci-dessous suit les scènes A (première rencontre), B (matelots et joute), C (capitaine), E (débarquement) et les réponses après la fin de PQ01. Les branches facultatives gardent une entrée et une sortie autonomes : ne pas jouer comme si la question précédente avait forcément été choisie."]
 rows=[]
 for key,node in takes.items():
  role=node['role'];title,profile,beats,avoid=PROFILES[role];text='\n\n'.join(node['lines'])
  instruct=COMMON+' Identité de '+ROLES[role]+' : '+profile+' Intention de cette prise : '+CUES[key]
  row={'id':key,'character':ROLES[role],'role':role,'language':'French','text':text,'instruct':instruct,'context_not_spoken':incoming[key],'suggested_filename':f'pq01_{key}_{role}.wav','datapack_hook':f'voices.{key}'}
  rows.append(row)
  human += ['### '+key+' — '+ROLES[role], '**Intention.** '+CUES[key]]
  if incoming[key]:human += ['**Question précédente possible, non lue :** '+ ' / '.join('« '+q+' »' for q in incoming[key])]
  human += ['**Texte à dire :**','> '+text.replace('\n','\n> ')]
  (DEST/'qwen/prises'/f'{key}.txt').write_text('PERSONNAGE : '+ROLES[role]+'\nLANGUE : French\n\nINSTRUCTIONS (champ instruct) :\n'+instruct+'\n\nTEXTE SEUL (champ text) :\n'+text+'\n')
 human += ['## Consignes de prise et livraison',
  "Enregistrer séparément chaque identifiant, en conservant la même place de micro, le même niveau et le même timbre pour un rôle. Proposer une prise principale et, si utile, une variante plus retenue. Garder une marge propre au début et à la fin ; enlever les grands blancs accidentels sans couper les consonnes, respirations utiles ou fins de mots. Livrer des prises sèches, sans ambiance ni réverbération imprimée.",
  "A3/A4 : la sensation d’interruption vient de l’attaque et de la phrase suspendue ; ne pas mélanger les deux voix dans un même fichier. A6 : le retour de Gautier commence après sa réponse. B1/B3 et C5 : conserver la brièveté, sans gonfler artificiellement leur durée. CX4 : terminer la voix avant l’arrivée au quai. EX3 : chaleur pudique, puis retour à l’énergie d’équipage sur EX4.",
  "Reporter la durée réellement utilisée dans le hook `voices.<identifiant>.ticks` au moment de l’intégration audio. Les points de déplacement, les boutons et les temps de navigation ne font pas partie de la prise. Aucun fichier son existant ni aucun hook n’est modifié par ce dossier."]
 prompts += ['## Exemple de prise complète', '**Gautier, interruption A3 — instructions :**','```text',next(x['instruct'] for x in rows if x['id']=='a3'),'```','**Texte à synthétiser séparément :**','```text',takes['a3']['lines'][0],'```','## Références de compatibilité',
  'Documentation officielle consultée le 24 octobre 2026 : [Qwen3-TTS](https://github.com/QwenLM/Qwen3-TTS) ; [implémentation des méthodes de synthèse](https://github.com/QwenLM/Qwen3-TTS/blob/main/qwen_tts/inference/qwen3_tts_model.py). Les promesses de continuité, de durée exacte et de restitution du français restent à vérifier à l’écoute ; ces prompts n’ont pas fait l’objet d’une génération audio dans ce lot.']
 (DEST/'DIRECTION_ACTEUR_FR.md').write_text('\n\n'.join(human)+'\n')
 (DEST/'PROMPTS_QWEN_FR.md').write_text('\n\n'.join(prompts)+'\n')
 (DEST/'qwen/prises_pq01.jsonl').write_text(''.join(json.dumps(r,ensure_ascii=False)+'\n' for r in rows))
 with (DEST/'REPLIQUES_PQ01.tsv').open('w',newline='') as f:
  w=csv.writer(f,delimiter='\t');w.writerow(['id','personnage','texte','intention','hook']);w.writerows((r['id'],r['character'],r['text'],CUES[r['id']],r['datapack_hook']) for r in rows)
 report={'roles':len(PROFILES),'takes':len(rows),'texts_match_canonical_nodes':all(r['text']=='\n\n'.join(N[r['id']]['lines']) for r in rows),'all_takes_have_french_direction':all(r['id'] in CUES and r['instruct'] for r in rows),'no_audio_generated':True,'no_datapack_hook_changed':True}
 assert report['texts_match_canonical_nodes'] and report['all_takes_have_french_direction']
 (DEST/'validation.json').write_text(json.dumps(report,ensure_ascii=False,indent=2)+'\n')
 print(json.dumps(report,ensure_ascii=False))
 return report
if __name__=='__main__':make()
