# AI Hangman

Tento projekt je seminární prací, která implementuje hru Šibenice s využitím umělé inteligence pro generování nápověd. 
Projekt je napsán v jazyce Kotlin a využívá frameworky jako Jetpack Compose pro uživatelské rozhraní a Firebase Firestore pro ukládání dat.

## Funkce

- **Generování náhodného slova**: Hra náhodně vybere slovo ze souboru `slova.txt`.
- **Generování nápověd**: Pomocí modelu umělé inteligence Gemini-1.5-Flash-8B jsou generovány nápovědy pro hádané slovo.
- **Počítání chyb**: Hra sleduje počet chyb hráče a zobrazuje odpovídající obrázek šibenice.
- **Zobrazení výsledku**: Hra zobrazuje aktuální stav hádaného slova a informuje hráče o výhře nebo prohře.
- **Ukládání skóre**: Po každé hře se aktualizuje počet uhodnutých slov v databázi Firestore.

## Instalace

1. Klonujte tento repozitář:
    ```sh
    git clone https://github.com/PetrHorak123/ai-hangman.git
    ```
2. Otevřete projekt v Android Studio.
3. Vytvořte si Gemini API klíč v Google AI Studio [zde](https://aistudio.google.com/app/apikey). 
4. Přidejte svůj API klíč do souboru `BuildConfig`.
5. Spusťte aplikaci na emulátoru nebo fyzickém zařízení.

## Použití

1. Spusťte aplikaci a zvolte si uživatelské jméno.
2. Prohlédněte si žebříček nejlepších hráčů.
3. Hádajte písmena slova. Pokud je písmeno správné, zobrazí se na odpovídajících pozicích.
4. Pokud uděláte chybu, zobrazí se část šibenice.
5. Použijte nápovědy kliknutím na ikonu nápovědy v pravém horním rohu.
6. Hra končí, když uhodnete celé slovo nebo uděláte 8 chyb.

## Technologie

- Kotlin
- Jetpack Compose
- Firebase Firestore
- Generative AI Model (Gemini-1.5-Flash-8B)

