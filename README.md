# Minesweeper

## Hvordan kjøre
- Ha siste/nylig versjon av java installert
- Installer maven (mvn)
- Sjekk at `storage` mappen eksisterer i roten av programmet, hvis ikke, så opprett den
- Kjør `mvn javafx:run` in roten av prosjektet (mac)

## Beskrivelse av appen

## Mappestruktur

### Storage
- Programmet bruker `storage` mappen for å lagre tilstand
- Det er viktig at denne mappen eksisterer når programmet kjøres, ellers krasjer det
- Hvert Board har en egen mappe som består av et tall/id i stigende rekkefølge
- Inne i denne mappen ligger det en `board.bin` fil som inneholder layouten til Boardet
- Det ligger også eventuelle Games som spilles på Boardet i denne mappen
- Hvert game er på følgende format `{date}.bin`, hvor date er datoen når Gamet ble opprettet
