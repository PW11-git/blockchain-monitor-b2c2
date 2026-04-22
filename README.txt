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
1. Uruchom:
   mvn test
   mvn compile
2. Start aplikacji:
   uruchom klasę BlockchainMonitorApplication

Uwaga:
- klasa Web3jSepoliaClient używa Web3j i jest gotowa pod Sepolię
- bez ważnego URL RPC pobieranie z sieci nie zadziała
- logika biznesowa i raportowanie działają niezależnie od dostawcy danych
🔑 1. Połączenie z blockchainem
Web3j web3j = Web3j.build(new HttpService("TWÓJ_URL_Z_ALCHEMY"));

👉 Co to robi:

łączy Twój program z blockchainem (Ethereum)
używa URL z Alchemy

🗣️ Powiedz:
Tworzę połączenie z siecią blockchain poprzez API Alchemy.

🔍 2. Pobranie najnowszego bloku
EthBlock block = web3j.ethGetBlockByNumber(
    DefaultBlockParameterName.LATEST,
    true
).send();

👉 Co to robi:

pobiera najnowszy blok
true = pobiera też transakcje

🗣️:
Pobieram najnowszy blok wraz z transakcjami.

📦 3. Numer bloku
block.getBlock().getNumber()

👉 Co to:

numer bloku (np. 19485732)

🗣️:
Odczytuję numer aktualnego bloku.

🔗 4. Hash bloku
block.getBlock().getHash()

👉 Co to:

unikalny identyfikator bloku

🗣️:
Hash identyfikuje blok w sieci blockchain.

💸 5. Transakcje w bloku
List<EthBlock.TransactionResult> transactions = block.getBlock().getTransactions();

👉 Co to:

lista wszystkich transakcji w bloku

🗣️:
Pobieram listę transakcji zawartych w bloku.

🔄 6. Pętla po transakcjach
for (EthBlock.TransactionResult tx : transactions) {

👉 Co to:

przechodzisz po każdej transakcji
🧾 7. Dane transakcji
Transaction transaction = (Transaction) tx.get();

👉 Co to:

zamienia dane na „czytelną” formę
👤 8. Nadawca i odbiorca
transaction.getFrom();
transaction.getTo();

👉 Co to:

kto wysłał
kto dostał

🗣️:
Analizuję adres nadawcy i odbiorcy transakcji.

💰 9. Wartość transakcji
transaction.getValue();

👉 Co to:

ile ETH wysłano
⛽ 10. Gas
transaction.getGas();

👉 Co to:

ile „opłaty” za operację

🗣️:
Odczytuję zużycie gasu, czyli koszt wykonania transakcji.

📊 11. Wypisanie w konsoli
System.out.println(...)

👉 Co to:

pokazuje dane
🔁 12. Ciągłe działanie (monitor)
while (true) {

👉 Co to:

program działa cały czas
co chwilę pobiera nowe dane

🗣️:
Aplikacja działa w trybie ciągłym i monitoruje nowe bloki.

🧠 PODSUMOWANIE (najważniejsze zdanie)

👉
Program łączy się z blockchainem, pobiera najnowsze bloki i analizuje zawarte w nich transakcje.

🔥 Jak ktoś zapyta „co robi Twój projekt?”

Powiedz:

👉
Aplikacja monitoruje dane blockchain, pobiera aktualne bloki i analizuje transakcje w czasie rzeczywistym.