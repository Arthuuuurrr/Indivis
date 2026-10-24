# PQ01 — Prompts Qwen en français

Version du 24 octobre 2026. Aucun audio généré ; textes et instructions sont fournis séparément.

## Utilisation

Ces consignes visent Qwen3-TTS VoiceDesign ou CustomVoice 1.7B avec contrôle par instructions. Dans une interface, placer la réplique seule dans le champ de texte, choisir le français, puis placer le prompt dans le champ de description/instructions. Avec l’API officielle, les champs correspondants sont `text`, `language="French"` et `instruct`. CustomVoice demande aussi une voix `speaker` réellement prise en charge par le modèle ; ce dossier n’en invente pas.

CustomVoice 0.6B ignore les instructions de style dans l’implémentation officielle consultée. Une interface de clonage Base n’accepte pas nécessairement ces mêmes instructions. Les fichiers JSONL sont un manifeste de prises à adapter à l’interface utilisée ; ils ne constituent pas une commande universelle d’import.

Réutiliser la même voix choisie et la même référence pour chaque personnage. Une description VoiceDesign répétée ne garantit pas à elle seule un timbre strictement identique sur toutes les prises. Pour une voix déjà doublée, conserver l’identité existante, en particulier celle d’Althéon dans MQ03.

Le dossier `qwen/prises/` contient un prompt complet et le texte exact pour chaque prise. `qwen/prises_pq01.jsonl` rassemble les mêmes champs pour un traitement par lot. Les noms propres et les points de suspension restent dans `text` ; ne pas y ajouter de didascalies ou de balises SSML non documentées.

## Prompts de personnage

### Althéon Brumeforge — capitaine

```text

Français naturel et intelligible. Jeu de dialogue réaliste, adressé à un interlocuteur proche. Variation d'intention à l'intérieur des phrases, diction nette, pauses expressives courtes. Lire exactement le texte fourni, sans annoncer le nom du personnage, l'identifiant ou les consignes. Conserver les noms propres et la ponctuation du texte. Pas de musique, bruitage, applaudissement, rire ou respiration théâtrale ajoutés. Personnage : Althéon Brumeforge. Voix masculine mature, médium grave, assise et souple. Diction précise, débit posé mais vivant. L'autorité vient de la maîtrise et de l'écoute. Accueillir un passager secouru, puis prendre au sérieux son amnésie. Distinguer nettement ce qui est établi de ce qui reste inconnu. Rassurer sans promettre de guérison. Les réponses sur la mission sont factuelles ; la prudence ne cache pas un secret. À la fin, laisser apparaître une sollicitude personnelle discrète. Conserver la même identité vocale que dans MQ03. Laisser respirer les articulations du raisonnement ; rendre les questions réellement ouvertes. Après « Je vois... », une petite suspension humaine, puis reprendre avec chaleur. Réduire les pauses dans les deux premières phrases qui forment un seul accueil. Éviter : Froideur administrative, déclamation militaire, monotonie, ton prophétique ou connaissance du mystère de l'amnésie.

```

### Géraud Rivet — quartier-maître

```text

Français naturel et intelligible. Jeu de dialogue réaliste, adressé à un interlocuteur proche. Variation d'intention à l'intérieur des phrases, diction nette, pauses expressives courtes. Lire exactement le texte fourni, sans annoncer le nom du personnage, l'identifiant ou les consignes. Conserver les noms propres et la ponctuation du texte. Pas de musique, bruitage, applaudissement, rire ou respiration théâtrale ajoutés. Personnage : Géraud Rivet. Voix masculine expérimentée, plutôt grave et légèrement rugueuse, sans forçage de gorge. Parole concrète, débit variable, ironie sèche. Se soucier d'abord de l'état physique du passager. Avec Gautier, couper net et rétablir la discipline, puis retrouver aussitôt le ton ordinaire. Sur les quais, orienter efficacement le passager ; derrière la brusquerie, la protection devient plus visible au moment de le laisser partir. A4 « Matelot ! » est une rupture brève et ferme, sans hurlement. A5 reste tenu et précis. A7 relâche la tension avec un soupçon de sourire. EX3 laisse une courte hésitation affectueuse, sans lourdeur. Éviter : Pirate caricatural, colère permanente, cynisme cruel, gentillesse uniforme ou hurlement continu.

```

### Gautier — matelot

```text

Français naturel et intelligible. Jeu de dialogue réaliste, adressé à un interlocuteur proche. Variation d'intention à l'intérieur des phrases, diction nette, pauses expressives courtes. Lire exactement le texte fourni, sans annoncer le nom du personnage, l'identifiant ou les consignes. Conserver les noms propres et la ponctuation du texte. Pas de musique, bruitage, applaudissement, rire ou respiration théâtrale ajoutés. Personnage : Gautier. Voix masculine adulte plutôt jeune, médium clair, diction appliquée et énergique. Compétent, ambitieux, convaincu que le règlement l'aidera à bien faire. Lorsqu'il interpelle Géraud en approche, entrer avec assurance et empressement, puis laisser la phrase en suspens comme prévu. La réprimande provoque une réponse plus rapide et brièvement embarrassée. Dans sa conversation facultative, retrouver de la tenue, défendre son service et son ambition. La timidité est ponctuelle. L'interruption se joue pendant son approche : voix dirigée vers Géraud, sans bruitage ni essoufflement ajouté. A6 est un acquiescement vif. Dans la joute avec Hugues, répliques serrées, sérieuses du point de vue de Gautier. Éviter : Adolescent naïf, incapable, bégaiement systématique, soumission apeurée dans toutes les répliques.

```

### Hugues — matelot vétéran

```text

Français naturel et intelligible. Jeu de dialogue réaliste, adressé à un interlocuteur proche. Variation d'intention à l'intérieur des phrases, diction nette, pauses expressives courtes. Lire exactement le texte fourni, sans annoncer le nom du personnage, l'identifiant ou les consignes. Conserver les noms propres et la ponctuation du texte. Pas de musique, bruitage, applaudissement, rire ou respiration théâtrale ajoutés. Personnage : Hugues. Voix masculine adulte mûre, médium grave, un peu usée mais intelligible. Vétéran débrouillard, peu impressionné par les registres, loyal à ses collègues. Humour populaire discret, porté par les intentions et les fins de phrase. L'accueil quitte une tâche agaçante pour accueillir le passager. Quand le sauvetage est évoqué, faire tomber le sourire un instant avant de revenir à un soulagement retenu. Dans la joute, « Oui. » et « Pas encore. » sont courts et secs ; laisser le partenaire répondre sans installer de grands blancs. Les plaintes sur les caisses restent celles d'un professionnel au travail. Éviter : Clown, ivrogne, insolence envers le capitaine, antimilitarisme ou accent comique imposé.

```

### Anselme — timonier

```text

Français naturel et intelligible. Jeu de dialogue réaliste, adressé à un interlocuteur proche. Variation d'intention à l'intérieur des phrases, diction nette, pauses expressives courtes. Lire exactement le texte fourni, sans annoncer le nom du personnage, l'identifiant ou les consignes. Conserver les noms propres et la ponctuation du texte. Pas de musique, bruitage, applaudissement, rire ou respiration théâtrale ajoutés. Personnage : Anselme. Voix masculine adulte, médium précis, articulation nette. À l'accueil, attention encore prise par les instruments ; devenir disponible une fois le geste terminé. Spécialiste passionné et fier de son métier. Les explications prennent un peu de vitesse et de relief, tout en restant compréhensibles. Corriger « bâtiment » avec une pointe d'orgueil technique, sans rabaisser le passager. Le dirigeable est un outil habituel de son monde, pas une découverte merveilleuse. Au début, deux « Un instant... » correspondent à une vérification en cours ; pauses courtes et différentes. Les énumérations techniques se lient. Au quai, commencer une explication avec plaisir, puis se retenir lorsqu'elle pourrait s'allonger. Éviter : Mépris savant, récitation scolaire, émerveillement devant une technologie rarissime, débit précipité incompréhensible.

```

### Perrin — matelot sociable

```text

Français naturel et intelligible. Jeu de dialogue réaliste, adressé à un interlocuteur proche. Variation d'intention à l'intérieur des phrases, diction nette, pauses expressives courtes. Lire exactement le texte fourni, sans annoncer le nom du personnage, l'identifiant ou les consignes. Conserver les noms propres et la ponctuation du texte. Pas de musique, bruitage, applaudissement, rire ou respiration théâtrale ajoutés. Personnage : Perrin. Voix masculine adulte jeune, médium lumineux, sourire perceptible, débit spontané. Très à l'aise avec les inconnus, curieux et un peu indiscret. Il veut créer immédiatement une complicité. Parler du Port avec plaisir, comme d'un endroit vivant qu'il connaît. Face à l'amnésie, conserver la délicatesse : son intérêt pour l'histoire n'est jamais une moquerie. Dédramatiser les formalités sans dire qu'on peut les éviter. Adresses directes, petites relances naturelles, fins de phrase ouvertes. Au mot « malheureusement », laisser passer une vraie limite à ce qu'il sait, puis retrouver la convivialité. Pendant son service, rester disponible sans jouer une course ni une agitation permanente. Éviter : Séduction appuyée, vendeur publicitaire, gouailleur cruel, rire sur l'amnésie ou enthousiasme crié.

```

## Exemple de prise complète

**Gautier, interruption A3 — instructions :**

```text

Français naturel et intelligible. Jeu de dialogue réaliste, adressé à un interlocuteur proche. Variation d'intention à l'intérieur des phrases, diction nette, pauses expressives courtes. Lire exactement le texte fourni, sans annoncer le nom du personnage, l'identifiant ou les consignes. Conserver les noms propres et la ponctuation du texte. Pas de musique, bruitage, applaudissement, rire ou respiration théâtrale ajoutés. Identité de Gautier : Voix masculine adulte plutôt jeune, médium clair, diction appliquée et énergique. Compétent, ambitieux, convaincu que le règlement l'aidera à bien faire. Lorsqu'il interpelle Géraud en approche, entrer avec assurance et empressement, puis laisser la phrase en suspens comme prévu. La réprimande provoque une réponse plus rapide et brièvement embarrassée. Dans sa conversation facultative, retrouver de la tenue, défendre son service et son ambition. La timidité est ponctuelle. Intention de cette prise : Intervenir avec empressement et assurance en venant vers Géraud. Susprendre la dernière proposition, prêt à poursuivre ; l'interruption coupe cet élan.

```

**Texte à synthétiser séparément :**

```text

Quartier-maître ! Le capitaine a demandé que notre passager lui soit présenté avant l'arrivée. Nous devrions peut-être...

```

## Références de compatibilité

Documentation officielle consultée le 24 octobre 2026 : [Qwen3-TTS](https://github.com/QwenLM/Qwen3-TTS) ; [implémentation des méthodes de synthèse](https://github.com/QwenLM/Qwen3-TTS/blob/main/qwen_tts/inference/qwen3_tts_model.py). Les promesses de continuité, de durée exacte et de restitution du français restent à vérifier à l’écoute ; ces prompts n’ont pas fait l’objet d’une génération audio dans ce lot.
