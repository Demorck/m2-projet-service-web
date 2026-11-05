# Explications

## Routes

Certaines routes sont peut être pas autorisées pour le rôle `USER` alors qu'elles pourraient l'être, notamment sur les 
GET. Le sujet indique juste que les `USER` peuvent créer et éditer leur propres évaluations. Du coup, les utilisateurs
n'ont accès qu'à ça. Pour modifier ça, y a seulement à rajouter `@PreAuthorize("hasRole('USER')")` sur les routes 
qu'on souhaite.

Certaines routes (notamment création/suppression) renvoie un 200 alors que je pourrais renvoyer un 201 CREATED ou
204 (NO CONTENT). Je trouve qu'un 200 c'est plus simple, car si c'est "OK", ça veut dire que ça a bien été fait

## Écarts du sujet

J'ai pris certaines "libertés" / "écarts", notamment :
- UUID au lieu d'un nombre entier positif
- `mean` au lieu de `moyenne` (tout est en anglais)
- Suppression réservée à l'admin. Je sais pas si ça compte comme modification

## S3

J'ai créé des services S3 pour chaque service car je trouve ça plus propre. Le s3 est générique et chaque sous-service
appelle ce qu'il a besoin et construit ce qu'il a besoin.
Mes images sont dans
Pour les évaluations: `<BUCKET_NAME>/maximilien-7f0ed9/<RESTAURANT_ID>/<EVALUATION_ID>/<IMAGE_ID>`
Pour le restaurant: `<BUCKET_NAME>/maximilien-7f0ed9/<RESTAURANT_ID>/<IMAGE_ID>-cover`

J'aime bien quand c'est bien rangé. On retrouve les images par évaluation et dans le dossier du restaurant, seulement
les images du restaurant. On pourrait en rajouter par exemple pour les menus, etc. 

## Erreurs

J'ai surchargé `handleExceptionInternal` et `handleMethodArgumentNotValid` pour renvoyer les mêmes erreurs tout le temps.
Certains cas ne sont pas pris pour renvoyer les mêmes erreurs notamment les 500 et les 401/403. Je ne me suis pas plus
renseigné sur ça.

## Tests

J'ai pas testé l'IndexService car je ne sais pas comment le tester puisque ça prend les fichiers sur l'ordinateur. 
En fait, je trouve ça plutôt compliqué à tester et c'est moyennement unitaire du coup.
J'avoue avoir eu du mal à comprendre la façon dont on construit les tests efficacement mais j'ai compris.

## Indexation

Je pense qu'il est mieux d'indexer seulement le contenu et de faire un appel à la base de donnée à chaque fois que de 
mettre tout via Lucene car les données sont à deux endroits à la fois. J'ai laissé comme ça.

## Autres

- J'ai commenté que deux portions (le RoleConverter et le S3Repository) car le reste est assez explicite.
- J'ai utilisé Java 27
- Le .env a `S3_ACCESS_KEY` et `S3_SECRET_KEY` pour le S3
- Vous aviez parlé de problème N+1 en cours mais j'avoue qu'à part faire une jointure à la manuelle sur les tables, je
ne sais pas si y a un autre moyen (quand on récupère les évaluations de tous les restaurants). J'ai aussi pas trop compris la source du problème mais je sais juste c'est quand on fait un calcul sur un findAll de la base.

## AI Disclosure
Je préfère être transparent quant à mon utilisation de l'IA générative
Aucun code n'a été généré par IA générative. L'utilisation a été seulement pour des exemples (hors du projet, pour éviter d'avoir le contexte et donc une solution) après avoir bloqué pas mal de temps sur la doc/problèmes pour que je comprenne.
En fait, comme StackOverflow est quasi mort, c'est plus efficace de faire ça que de chercher pendant super longtemps.
Je l'ai utilisé aussi pour confronter un choix que j'ai pris, pour voir ce qui existait ou comment on pourrait le faire autrement par exemple:
- La moyenne est calculée en java (ça me semblait plus simple) mais y a aussi en DB
- Record pour des DTO
- Organisation par couche ou fonctionnalités (en fait, par fonctionnalité j'ai vu qu'on peut faire un package-info.java et dire ce qu'on exporte / importe ce qui est assez propre je trouve)
- Le controleur appelle le service puis lucene / le service appelle tout

En fait, mes projets persos sont souvent pour apprendre / comprendre des choses (Disques wii, la décompilation, etc.) du coup je trouve ça nul pour moi de générer du code.
Si vous estimez quand même que ma note doit être baissée, je comprendrais. L'AI disclosure est juste là pour être transparent

---

Sinon je suis assez content, j'ai appris pas mal de choses (et encore merci pour le cours, il était super !)

(si vous voulez regarder un stream de moi qui programme le début du projet: https://www.twitch.tv/videos/2913837470 (je suis assez lent, c'est l'effet stream où on doit pas laisser de blanc et le cerveau prend aussi du "process" car y a le stream))
