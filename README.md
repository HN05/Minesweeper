# Minesweeper

Dokumentet er formatert for github sin markdown syntax, hvis det er uleselig kan du lese dette dokumentet her [github](https://github.com/HN05/Minesweeper)

## Hvordan kjøre
- Ha siste/nylig versjon av java installert
- Installer maven (mvn)
- Sjekk at `storage` mappen eksisterer i roten av programmet, hvis ikke, så opprett den
- Kjør `mvn javafx:run` in roten av prosjektet (mac)

## Beskrivelse av appen

> [!IMPORTANT]
> **Appen følger de samme reglene som vanlig minesweeper:**
> - Spillet består av et rutenett hvor noen ruter er bomber
> - Hvis du tror en rute er en bombe, så kan du markere den med et flagg
> - Dette vil automatisk dekrementere 'Bombs left' uavhengig om det faktisk er en bombe
> - Hvis du tror en rute ikke er en bombe, så kan du reveale den
> - Hvis den ikke er en bombe vil den vise antall bomber som den naboer til (inkludert diagonalt)
> - Hvis den er en bombe så vil du tape
> - Du vinner når du har markert eller revealet alle ruter uten å treffe en bombe

> [!NOTE]
> **Når du starter appen:**
>
> • Du må først velge et brett – dette definerer antall ruter og hvor bombene er  
> • Deretter velger du et spill – et spill er et forsøk på å vinne brettet (dvs. en logg over aksjoner)  
> • Nye brett får automatisk et ID-nummer: høyeste eksisterende + 1 (starter på 1)  
> • Nye spill navngis automatisk med tidspunktet de ble opprettet  
> • Spillet lagres automatisk etter hver aksjon du utfører  
> • Ferdigspilte spill slettes automatisk (uansett om du vinner eller taper)  
> • Det finnes ikke UI for å slette brett eller spill manuelt

## Mappestruktur

### Storage
- Programmet bruker `storage` mappen for å lagre tilstand
- Det er viktig at denne mappen eksisterer når programmet kjøres, ellers krasjer det
- Hvert Board har en egen mappe som består av et tall/id i stigende rekkefølge
- Inne i denne mappen ligger det en `board.bin` fil som inneholder layouten til Boardet
- Det ligger også eventuelle Games som spilles på Boardet i denne mappen
- Hvert game er på følgende format `{date}.bin`, hvor date er datoen når Gamet ble opprettet

### Src
- All kildekoden ligger her
- I `test` mappen ligger alle testene
- I `main` mappen ligger all koden for selve programmet
- I `main` mappen er det en `java` mappe for java koden, og en `resources` mappe for ressurser som bilder og fxml for ui


## Teknisk info om appen

### Deler av pensum som er dekket
Store deler av pensum er dekket av appen, det er blant annet brukt observatør-observert arkitekturen mellom modellen og controlleren, så modellen alerter controlleren når den oppdateres, slik at controlleren kan oppdatere viewet. Delegering er også mye brukt, som `FileStorage` klassen, hvor all filhåndtering delegeres til den fra controlleren, eller `CellGenerator` som generer celler for forskjellige klasser. Jeg har også brukt konseptet **records** for `Action` klassen, siden det gir automatisk innkapsling on andre ønskelige funksjoner for immutabel (uforanderlig) data.

### Deler av pensum som er ikke er dekket
Jeg har ikke brukt arv noen steder i appen (utenom javafx app klassen), dette er fordi det ikke var noen passende steder for arv, delegering og interfaces er mye mer passende for appen etter min mening. Jeg har derfor heller ikke brukt abstrakte klasser, siden du må ha arv for at de skal være noe brukbar. Noe jeg kunne ha brukt er optionals, det er flere steder i appen hvor det er valid at verdiene er null, som `error` i `GameSelectView`, men jeg har valgt å ikke bruke optionals siden de er best brukt som returverdier for metoder/funksjoner for å signalisere at verdien kan være null. 

### MVC
Appen bruker MVC (Model-View-Controller):
- **Modellen** inneholder data og logikk for selve spillet
- **Controlleren** bestemmer hva og når noe skal vises, og knytter modellen sammen med viewet 
- **View** bestemmer hvordan noe skal vises for brukeren, altså farger, knapper, tekst og lignende. 

Når brukeren interagerer med ui-et så kaller viewet en lambda funksjon fra controlleren, og controlleren bestemmer seg da for hva den vil gjøre, og om den vil si ifra til modellen.   
Når modellen endrer seg så sier den ifra til controlleren, som bestemmer seg for hva den skal gjøre, den kan for eksempel tegne gridden på nytt med å kalle renderGrid på GameView.
Selve layouten er spesifisert i en App.fxml fil, det hadde nok vært bedre å ha flere av de, i hvert fall en egen for valg av brett/spill, siden det er blitt litt rotete i den filen. Noe annet som kunne ha vært bedre er å la viewene ha referansene til komponentene i fxml filen, siden slik jeg har satt det opp så bruker controlleren aldri (med noen få unntak) disse referansene, og sender de heller i kall til de forskjellige viewene.  

Under ser du hvordan model, view og controller interagerer med hverandre
![Sekvensdiagram av mvc](mvc.png)


### Testing av appen
Testene er skrevet når appen var så og si ferdig, siden i tidligere versjoner av appen så var det mye endring og refactorering, og testene måtte da også ha blitt endret, så jeg valgte å skrive testene når appen var i en nesten ferdigstilt tilstand.
