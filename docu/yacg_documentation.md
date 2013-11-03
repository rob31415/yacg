
##einleitung

dieses dokument beschreibt das konzept eines projekts mit dem arbeitstitel "yagc" ("yet another computer game").

dieses projekt besteht im rahmen des tkv saar.
es handelt sich um die erschaffung eines computerspiels mit folgenden merkmalen:

- story-getrieben
- linear narrativ
- 3d-adventuregame 
- simulationsaspekt durch frei agierende aktoren
- wild-west setting
- offline singleplayer
- multiple ending


## organisatorisches

### rollen im projekt 

folgende rollen gilt es zu besetzen:

- projektmanagement (vision, koordination, antrieb) 
- storywriting (setting, plot, charakter, dialog) 
- gamedesign (gameplay, rätsel) 
- voice-actors 
- visual-art (2d/3d, animation) 
- audio-art 
- testing 
- coding

einem projektteilnehmer können durchaus mehrere rollen zugeteilt sein

### beteiligung

wenn du lust hast, mitzumachen, lies dich ein, komm am offenen tag in den hackerspace, lerne die beteiligten kennen, suche dir eine oder mehrere rollen im projekt aus und leg los :-D !

wir freuen uns auf mitstreiter.

### code

https://github.com/rob31415/yacg/

## content

### ästhetik

#### optisch
wenn wir es auch nur annähernd so prachtvoll hinbekommen wie call of juarez oder red dead redemption, wäre das wow.
weitläufig, offen, überwiegend karge wüstenlandschaft/steppe/präie, detaillierte kleine dörfchen und städchen, aber auch oase, hain/wald/naturpark-szenerie.

##### 3d-model-arsenal

- personen m/w im western-look: cowboy, cowgirl, banker, citizens, old-timers, farmers, rich land-owners, whores, undertaker, mexicans, indians, chinese'
- gebäude wie saloon, wohnhäuser (mit porch, aussendach, kellereingang), shops/stores, holzbaracken, scheunen, pferdekoppel/stall, friedhof, cornfields, sherrif's office/jail, kirche, zirkus, doctor, bahnhöfe
- tiere wie pferde, hunde, vögel, eidechsen, fliegen, mäuse
- transportmittel: kutschen, eisenbahn, pioneer wagons
- lanschaftsdetails wie katkeen, gräser, büsche, tumbleweed, cattle skull, railroad-tracks, wagon-wheels, haystacks, fireplace (schwenker), obligatorisches windrad (water pumping windmill), fässer, kisten, pferdetränken, wasserrohre, särge, wege aus brettern
- misc.: stühle, laternen, betten, kommoden, spiegel, schränke, kisten mit schloss, waffen, muni


#### akkustisch

wir machen unseren soundtrack - beeinflusst von klassischen italo western - selbst. ein lied existiert bereits: https://soundcloud.com/robsun/rob-sun-longing

- wüstenfeeling, karg, leer 
- windgeräusche, hawk-cries, bottleneck-slide-gitarre, banjo, maultrommel, chor-synth-pads, etc. 
- selbstgemachte musik, darf ruhig etwas rohen, semi-amateurhaften charme haben 
- musikstücke sollen der dramaturgischen situation angepasst sein 
- incidental music
- sprache des spiels deutsch oder englisch? (sollen voice-actors englisch sprechen, egal mit welchem akzent; oder deutsch, u.u. sogar absichtlich mit deutschem akzent)

### feel

open world feeling (man kann in fast jedes haus hineinlaufen), wobei die narration geradlinig verläuft.


### die welt

die welt ist eine einzige, allumfassende, grosse szene.
wir designen ein grosses quadratisches terrain per heighmap und malen dessen textur. darauf verteilen wir die statischen (häuser etc.) und dynamischen (npcs) polygonmodels.
wir brauchen einen szenengraphen, der alles hält und verwaltet und daten nach bedarf lädt/entlädt.


### scene-composing / content generation workflow

...todo...


## story



### gameplay

die aufgabe des gameplay-designs ist es, quests (rätsel/aufgaben) zu erfinden, die sich A) mit den vorhandenen charakteren und situationen in diesem setting glaubhaft darstellen lassen und B) an der story orientieren.

es schmückt quasi die story aus und setzt sie mit den gegebenen aktoren um.

am besten wäre hierfür eine deskriptive dsl und ein zugehöriger solver. 

z.b. soll man in einer scriptsprache, bzw. dsl einfach angeben können, ein bauer wacht um 6 h auf, melkt die kühe, bestellt das feld, macht mittagspause von 12 bis 1, macht abendessen von 7 bis 8, geht in die kneipe von 8 bis 10 und schläft bis 7. das ganze von mo-fr, am sa und so geht er auf den markt und macht nur das nötigste im stall.

seine frau kocht komplementär das essen bis 12 etc...

wir brauchen eine darstellung, ein system um diese simulation gezielt kontrollieren zu können. der spieler beeinflusst npcs, bring so abweichungen in die vorprogrammierten routineabläufe und daraus emergiert quasi dann die story.



### story


#### setting

haupt-handlungsspielort: mittelgrosse western stadt.

#### prämisse

ein mann entdeckt, sein hund hat die besondere fähigkeit, wasser zu finden.
der mann baut damit ein business auf - nämlich sich in der wüste für die wasserbelange der stadt zu kümmern - wie z.b. neue wasserquellen erschliessen, rohre verlegen.
die stadt ist abhängig von diesem business, da kein fluss in der nähe ist.
die stadt ist überhaupt erst an dieser stelle entstanden, weil es fruchtbares land gibt - auch ohne offensichtlich nutzbare wasserader.

der sohn des mannes - das ist die figur, die der player spielt - wird gezwungen das business zu übernehmen.
dies tut der vater, indem er seinen sohn als kind misshandelt indem er seinen kopf wiederholt in wasser taucht um ihn gefügig zu machen.
das spiel beginnt mit dem tod des vaters.

der vom spieler gesteuerte protagonist hat eine aversion gegen wasser entwickelt, wird aber trotzdem versuchen, das geschäft seines vaters weiterzuführen.

es wird eine "neurose-anzeige" ("neurosebalken") geben, welche anzeigt, wie stark die aversion, die zwanghafte angst des protagonisten ist.
ein spielerisches element wird sein, dass die ausführung von quests schwieriger wird, je höher die aversion gerade ist. werden viele quests trotz hoher aversion gelöst, ändert sich das ende der geschichte entsprechend.

der hauptkonflikt des protagonisten ist also seine panische aversion gegen wasser und seine lösungsstrategien.

er soll in teilweise sarkastische, teilweise gemeine, teilweise ambivalente situationen geworfen werden, durch die er sich dann herauskämpft. der spieler soll sich mit der figur gut identifizieren können, mit der figur mitfühlen können.
wir wollen es schaffen, die figur dem spieler vertraut zu machen, dass er sie gut kennenlernen kann, so dass sie für den spieler etwas besonderes ist.
wir wollen es schaffen, dem ganzen einen eigenwilligen, besonderen, bisweilen sarkastischen, bissigen, vlt auch sozialkritischen charakter zu geben - es sollte auch einen gewissen humor haben, jedoch - nach möglichkeit - nicht aufgesetzt oder ungewollt plump wirken.

es gibt eine doktorin, die ein serum entwickelt, welches die angst des protagonisten effektiv verringert. damit erweitert sich der aktionsradius, es steigert jedoch die abhängigkeit - mit den üblichen negativen auswirkungen (zb. handlungsunfähigkeit bei entzug, etc.).


der hund des vaters bekommt nachwuchs, das ist dann der hund des sohnes. dieser hund ist aber besessen vom trickster, denn der hund des vaters hat sich mit einem besessenen koyoten gepaart.
manchmal kommt der trickster im hund zum vorschein und lockt den protagonisten auf eine falsche fährte.


#### plot & storywriting

orientiert an http://en.wikipedia.org/wiki/Three-act_structure

**pt 1**

- setup:
- quests mit sozialer funktion, welche die meisten relevanten stadt-charaktere und deren tagesabläufe bekannt machen
- business-quests bzgl. windpumps erneuern/aufbauen (http://de.wikipedia.org/wiki/Western-Windrad)
- einführung in die problematik mit dem heilserum (zb via quests, um die zutaten dafür zu besorgen)

**pt 2**

- konfrontation:
- serum herstellen, aktionsradius des protagonisten erweitern 
- der protagnist macht drei grosse versuche um aus der stadt zu gehen: goldschürfer/miner, farmer, pioneer
- alle diese versuche scheitern aufgrund der abhängigkeit zum serum

**pt 3**

- auflösung:
- äusserer druck fällt weg, stadt wird von ihm unabhängig
- innerer druck fällt weg, er hat durch die erfüllung der quests stück für stück gelernt, couragiert trotz seiner angst etwas tun zu können
- man sieht ihn am ende in den sonnenuntergang reiten, irgendwohin, der spieler weiss nicht, wohin, aber, dass er eine chance hat, zu schaffen, was immer er auch tun wird

**questdesign**

die quests müssen so designed sein, dass man einzelne quests nur in einem jeweils bestimmten bereich der neurosestärke (z.b. von 60-80 %) lösen kann. ausserhalb dieses bereichs sind sie zu schwer und der spieler kommt nicht weiter.

es gibt einen akkumulierten gesamtwert, der kontinuierlich nach jeder questerfüllung anwächst. dieser wert, nennen wir ihn game-score, wächst umso stärker an, je weniger serum zum erfüllen der quest eingesetzt wurde, und infolgedessen umso schwieriger es für den protagonisten war, sie zu lösen.
es gibt verschiedene endings, die von diesem game-score abhängen. der game-score wird dem player präsentiert - auch schon während des spiels.
er könnte - wenn wir das so wollen - auf einer website veröffentlicht werden.


## technisches

wir benutzen die jmonkeyengine, eine java-basierte, freie 3d-gameengine.
die hauptentwicklungssprache ist scala, weil general purpose und multi-paradigm, jvm based, nice when it comes to internal dsl (http://de.slideshare.net/abhijit.sharma/writing-dsls-in-scala).

### coding

#### importing textured model from blender
funktioniert per code und per scene-composer; importiert auch lichtquellen

#### ide
- eclipse mit scala plugin - funktioniert einwandfrei
- scala in netbeans - plugin fkt zu unzuverlässig
- können wir zusätzlich auch clojure benutzen? das wäre wünschenswert.
- idea?
- vim?
- keine/nur console & texteditor? (-> sbt von hand ist ein graus; fkt, aber unzuverlässig)
- scene-composer von jmonkey? (-> todo: ausprobieren, ob uns das was erleichtert)
- build tool? (gradle, maven, sbt, ant. -> wir verwenden erstmal, was die ide vanillamässig anbietet)

#### scene composer (der jmonkey-netbeans-ide)
- fkt wie?
- wie hängt das mit code zusammen? (ist ein scene file ein scene composer output, welches per code geladen werden kann?)
- können/müssen/wollen wir die welt anders komponieren?

#### terrain
- doughnut vs border?
- wir brauchen ziemlich grosses terrain:  http://hub.jmonkeyengine.org/wiki/doku.php/jme3:advanced:endless_terraingrid
- wie wird das mesh modelliert?	-> mit blender und entsprechenden plugins
- wie werden texturen erstellt?	-> fürs terrain mit blender in 3d view malen
- wie kommen die daten in jme hinein?
- collision detection: http://hub.jmonkeyengine.org/wiki/doku.php/jme3:advanced:terrain_collision
- http://hub.jmonkeyengine.org/forum/topic/player-collision-on-a-terraingrid/

#### texturing
- ambient
- normal / parallax
- specular
- ambient occlusion

es gibt tools, die aus einer ambient texture alle anderen on the fly parametrisiert erstellt. für win und als gimp plugin für linux.

#### water
jme3 kann shader

#### vegetation
trees (kaktus)/grass/shrubs/debrie/etc.
generativ mit manuellem einfluss? z.b. particle system mit wheight paint?

#### dimensions
wie sind die metriken und dimensionen (im spiel, z.b zeit und distanz und technisch - z.b. terrainpatchgrösse)

#### lod/paging
für mesh  http://www.truancyfactory.com/tutorials/fallout3/fallout_lod.html#lodMeshes
für texture
für vegetation



### gameplay
quest-system überwiegend linear; ab und an aber entscheidungsfreiheit für den spieler
die npc sollen einen tages/wochen/jahresablauf haben. jahreszeiten (die wüste lebt)
im grunde sollen spieleraufgaben sich daran orientieren, darin einfügen - mit einem simulationsartigen feeling.

personen sollen tagesablauf haben und, je nach story-progress, etwas besonderes tun oder zu besonderer interaktion mit player bereit stehen

ein bsp: wenn der schmid schläft, kann man ein stück des metallkopfes seines hammers rausbrechen, dann schmiedet er einen fassring, gibt das dem fassmacher, der ein fass baut, das deswegen ein leck hat, welches dann wasser verliert und dazu führt, dass ein pflanzenfeld verdorrt, so dass der farmer, der das fass benutzt dem spieler eine aufgabe anbietet mit einem quest-gegenstand als belohnung. (ein zugegebenermassen sehr konstruiertes und nicht sehr realistisches bsp.)

es sollen sich manche rätsel an der manipulation der tagesabläufe der npc lösen lassen.
der zeitpunkt, an dem manche rätsel überhaupt entstehen, soll auch vom tagesablauf abhängen.

der spieler muss natürlich vom storyablauf und gamedesign her sensibilisiert werden, dorthin zu kucken, diese umstände im auge zu behalten und er muss feedback bekommen, wie seine versuche einfluss nehmen - aber das feedback soll auch durch das verhalten der npc geschehen. dabei darf es nicht vorkommen, dass der spieler in eine sackgasse gerät. 

so kann man z.b. auch sarkastische elemente rüberbringen - nach dem motto - ausgerechnet dann muss das passieren und eine tragische abfolge von ereignissen in gang setzen (wie zb im film u-turn) - der spieler soll sich an den kopf schlagen und denken - "oh nee, das gibts doch nicht!" - nicht in frust, sondern in belustigung.

aber nicht nur absurde, es sollen auch ernsthafte dinge passieren - auf eine gute mischung kommt es an - und darauf, dass es nicht zu konstruiert wirkt.


es gibt einen "story-tracker", ein globales objekt, das den verlauf der story managed und bei dem objekte und npcs erfragen wo die story steht und mit dieser info entscheiden, ob sie dinge tun sollen oder nicht

idee geklaut von bethesda: deklarative beschreibung der npc-actions, solver kümmert sich um realisierung
radiant ai
„Iss um zwei Uhr nachmittag in dieser Stadt“ (wollen wir)
radiant story
erschaffung neuer dynamischer quests (wollen wir nicht, ist ausserhalb unseres scopes)


#### audio engine

todo: fmod ausprobieren

#### ai

was sollen die beteiligten npc tun? mit welchen gegenständen wie interagieren?
was und wie implementieren?

##### state machine?
##### planning system?
http://www.bit-tech.net/gaming/2009/03/05/how-ai-in-games-works/3
##### ai-systeme in spielen

http://aigamedev.com/open/review/planning-in-games/

- shop (http://www.cs.umd.edu/projects/shop/description.html)
- strips
- htm
- behaviour trees
- utility systems
- expert system?
- rule engine?
http://martinfowler.com/bliki/RulesEngine.html
http://content.gpwiki.org/index.php/Rule-Based-AI

- jess?
nö, wollen wir nicht. ist nicht foss.

- drools?
http://stackoverflow.com/questions/514187/java-rule-engine-for-game-ai

- rule base vs. logic/constraint programming?

prolog has backword chaining + x features

rule base has forward chaining

bethesda radiant ai = rule based?

http%3A%2F%2Fdare.uva.nl%2Fdocument%2F142422&ei=v032UeeZOIixObi2gbgK&usg=AFQjCNGNGVfUk79Ebbuq6PGB_QJTe8OTmQ
what about prolog?

http%3A%2F%2Fjournals.sfu.ca%2FCALICO%2Findex.php%2Fcalico%2Farticle%2Fdownload%2F338%2F227&ei=3E72UcT7GIyuPOiugMAI&usg=AFQjCNHVhqdfaGKCq8-1uZI_Hh9rzpOFBw

nichts weist darauf hin, dass aktuelle games soetwas benutzen

- genetic algorithm?

- neural network?

- gibt es freie 3rd party game ai lib?
https://github.com/idmillington/aicore

gibts bei aigamedev was verwendbares?


### implementation big picture : aufbau genereller, tragender strukturen und interaktionen dazwischen 

#### world 

die welt soll aus einem riesen areal bestehen (einer landscape) welches eine quadratisch rechteckige form hat und in das alle räumliche geometrie (incl. audio-nodes) platziert ist. die welt besteht nicht aus einzelnen levels / geometry-szenen. es soll so sein, dass der spieler sich nahtlos zwischen aussen und innenräumen bewegen kann. das erfordert - zusätzlich zu culling, geometry batching und lod - ein gutes mem-mgmnt-system zum laden/entladen von "krempel", abhängig vom pov. idealerweise fallem dem spieler diese vorgänge nicht auf. es gibt entweder eine begrenzung des areals - z.b. durch unüberwindbare bergketten - oder die welt ist eine torusförmige, d.h. läuft der spieler quasi rechts aus dem bild, kommt er links wieder herein. diese entscheidung steht noch aus. 


#### scene-graph ("SG") 

zunächst gibt es einen SG, mit der die engine ihren kram verwaltet. models werden aus einem 3d-modeller (z.b. blender) exportiert incl. all ihrer assets (models, textures, materials, shaders, sounds, etc.). irgendwo - entweder im 3d-modeller selbst, oder in einem tool der engine - wird die komplette welt grafisch zusammengesetzt: landscape, npcs, häuser, innenräume, spatial audio-nodes, etc. 


#### in-game-objects graph ("IGOG") 

parallel zum SG wird ein IGOG aufgebaut. 

wir unterscheiden zwei kategorisch unterschiedliche typen von nodes und definieren sie namentlich als "statisch" und "dynamisch". ein node repräsentiert also entweder ein statisches objekt oder ein dynamisches objekt. 

die unterscheidung besteht darin, dass statische objekte lediglich für den SG relevant sind (z.b. skydome oder ein gebäude), wogegen dynamische objekte für das gameplay relevant sind. letztere tragen daher eine auswahl aus verschiedensten attributen, die zusammengenommen einen zustand repräsentieren. 


#### wer arbeitet auf dem IGOG? 

##### das mem-mgmnt 

es sorgt dafür, dass sich lediglich in einer bestimmten (performanceabhängig festzulegenden, relativ zum pov kugelförmigen) umgebung die daten SG-relevanter nodes (die aus aus einem storage kommen, zb hdd/ssd) im ram befinden. culling, geobatching, lod - entweder selbst implementiert oder hoffentlich bereits brauchbar in jme vorhanden (todo: rausfinden) - sorgt für den transport der viewport-relevanten daten in den ram der graka. 

##### das gameplay 

abstrakt gesehen besteht das spiel aus teils vorgegebenen, teils bedingten manipulationen gameplay-relevanter attribute der IGOG nodes. bedingungen sind z.b. räumlicher abstand, user-interaktion (knöpfchen drücken), zeit (in-game oder echt), collision-detection. eine aufgabe ist es, anhand der anforderungen an die spielmechanik - welche die gamedesignerin wiederum anhand der vorgabe des flusses/des ablaufs der story quasi generiert - die gameplay-attribute so zu gruppieren, gewissermassen auf fach-ebene zu bündeln (fachebene meint hier also die storyebene) und mit wetebereichen und konkreten startwerten zu besetzen, dass sie schlussendlich programmiertechnisch ideal handhabbar werden. 

##### interaktion zwischen IGOG und SG 

hier wird demnach die interaktion zwischen SG und IGOG deutlich. eine weitere aufgabe (nämlich die der coder) ist es, auch sie ideal zu systematisieren. 

anmerkung: attribute der IGOG-nodes können durchaus für beide aspekte - sowohl mem-mgmnt als auch gameplay - relavant sein, z.b. x,y,z koordinaten. 

eine weitere aufgabe ist folgende frage zu überdenken: nach welcher methode (u.u. mit welchen tools) wird der IGOG parallel zum SG erstellt und gepflegt?



## links

### texturen
- http://cgtextures.com/


### wild-west history

- http://en.wikipedia.org/wiki/Timeline_of_the_American_Old_West
- http://www.infoplease.com/timelines/slavery.html
- gegen ende der wild-west ära gab's auch autos. (http://www.fordification.com/timeline.htm)
- "Chinese and Japanese who came to Montana in the late 19th and early 20th centuries" (http://mansfieldfdn.org/publications-and-outreach-2/publications/from-the-far-east-to-the-old-west-chinese-and-japanese-settlers-in-montana/)
- http://arcana.wikidot.com/list-of-wild-west-professions

### ai

- http://aigamedev.com/
- ian millington buch (artificial intelligence for games) gibts frei als pdf
- http://www.dmoz.org/Computers/Artificial_Intelligence/Machine_Learning/Software/
- http://aigamedev.com/open/review/planning-in-games/

### misc

- http://de.slideshare.net/josezagal1/game-design-patterns-workshop-fdg2012-opening-remarks

