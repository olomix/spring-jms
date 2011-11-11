Spring JMS
==========

Пример использования Spring совместно с ActiveMQ в случае когда у вас есть
сервер, который раздает задания на клиентов. И есть много клиентов,
которые обрабатывают задачи и возвращают на сервер результат.

Основной смысл — клиентская и серверная часть упакована в одном приложении
для простоты деплоймента. На одной из машин нужно запускать приложением с параметром
-server, на всех других с параметром -client.

Клиенты и сервер соединяются через ssl. Сертификат зашит в приложении и успользуется
один и для сервера и для клиентов. Задача ставится так, чтоб во-первых
к серверу не могли подсоединиться левые клиенты. И во-вторых, чтоб нельзя было прослушать
трафик между сервером и клиентами.

Сборка
------

	mvn package

Запуск
------

Для запуска клиентской и серверной части в одном JVM

	java -jar target/spring-jms-1.0-SNAPSHOT.jar -all

Или в разных JVM запустить

	java -jar target/spring-jms-1.0-SNAPSHOT.jar -server
	java -jar target/spring-jms-1.0-SNAPSHOT.jar -client
	java -jar target/spring-jms-1.0-SNAPSHOT.jar -client
	...

Если запускается на разных машинах, то можно указать серверу и клинтам на какой IP:PORT коннектиться
	java -jar target/spring-jms-1.0-SNAPSHOT.jar -client -brokerUrl ssl://localhost:61616
