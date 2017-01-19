Prosty program do aktualizacji stanów i pobierania zamówień  z Programu PcMarkt do/z Sklepu Internetowego WooCommerce

Program łączy się do bazy PcMarket i pobiera aktualne stany z programu i aktualizuje je w sklepie za Pomocą API Restowego 

towary łączone są za pomocą product_id, który jest wpisany w polu Opis3 w programie PcMarket
- w programie jest zaimplementowana funkcja która wyszukuje towary ze sklepu internetowego i wpisuję odpowiednie product_id w polu Opis3 
aby towar był "powiązany" każde słowo z nazwy w programie musi wystąpić w nazwie w sklepie internetowym 
- Program może pobierać zamówienia ze sklepu internetowego i zapisywać je bezpośrednio do bazy PcMarket 
W przypadku nieznalezienia jakiegoś produkty do numeru zamówienia zostanie dodany ciąg znaków  „!!!” a w polu komentarz zostaną wpisane product_id które nie zostały odnalezione. 
Program można uruchomić z trzema opcjami (w celu uruchamiania go z linii komend lub skryptu )np. 

java -jar WooRepl.jar z – pobiera zamówienia i zapisuję je do bazy 

java -jar WooRepl.jar o – aktualizuje stany na sklepie pojedynczo  

java -jar WooRepl.jar a - aktualizuje stany na sklepie w pakietach (patrz opcja packet w konfiguracji) 
