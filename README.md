# BookSwap

Aplikacja webowa, która umożliwia wymianę książek pomiędzy użytkownikami w prosty i szybki sposób.

### Prezentacja działania
Na prezentacji otworzono obok siebie dwa ekrany aplikacji z zalogowanymi różnymi użytkownikami, aby przedstawić jak działa wymiana.




https://user-images.githubusercontent.com/32033773/182342333-8b50ef36-0cb3-4417-bedc-eb7ca724132c.mp4





### Przed uruchomieniem aplikacji należy zainstalować:

  * Intellij IDEA (bądź inne wybrane środowisko programistyczne).
  * Node.js oraz npm package manager - do uruchomienia części klienta.
  * Java 11, Spring Framework, Maven i Tomcat - do uruchomienia części serwera.
  * PostgreSQL - należy utworzyć bazę danych i w pliku application.properties części serwera podać do niej użytkownika, hasło oraz schemat.

Następnie należy osobno uruchomić część klienta i serwera. <br />
Warstwa klienta znajduje się w katalogu bookswap-gui. Należy ją uruchomić komendą `ng serve`. Przed uruchomieniem należy pobrać potrzebne zależności komendą `npm install`. <br />
Warstwa serwera znajduje sie w katalogu bookswap-app. Należy ją uruchomić wykonując funkcję main w klasie BookswapAppApplication lub komendą `mvn spring-boot:run`.<br />

Pod adresem http://localhost:4200/ powinna znajdować się uruchomiona aplikacja. Początkowo powinna wyświetlać się strona logowania.
