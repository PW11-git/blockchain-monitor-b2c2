Projekt: Monitor Danych Blockchain

Co zawiera:
- architekturę 3-warstwową:
  1) access      -> połączenie z Sepolią i pobieranie bloków/transakcji
  2) business    -> filtrowanie, agregacja, statystyki
  3) reporting   -> raportowanie do konsoli i raport końcowy
- obsługę wyjątków
- testy jednostkowe JUnit 5 dla warstwy logiki biznesowej
- konfigurację JaCoCo w pom.xml

Jak uruchomić:
1. Uzupełnij URL RPC w pliku:
   src/main/java/pl/example/blockchainmonitor/config/AppConfig.java
2. Uruchom:
   mvn test
   mvn compile
3. Start aplikacji:
   uruchom klasę BlockchainMonitorApplication

Uwaga:
- klasa Web3jSepoliaClient używa Web3j i jest gotowa pod Sepolię
- bez ważnego URL RPC pobieranie z sieci nie zadziała
- logika biznesowa i raportowanie działają niezależnie od dostawcy danych
