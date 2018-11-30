# MyStrom 2 MST EM

This application collects values from a MyStrom adapter https://mystrom.ch/de/
Logs values to a CSV file.
And reports aggregated values all 15m to MST EM  https://www.mst.ch/de/content/em-solutions

While connection problems will raw values be kept in memory.

## Run application

``java -ea -jar target/mystrom2em-jar-with-dependencies.jar config.xml``

If you want to change log configuration specify a custom log4j.xml

``java -ea -Dlog4j.configuration="file:log4j.xml" -jar target/mystrom2em-jar-with-dependencies.jar config.xml``


## Debug HTTP Connection

Add this vm arguments to get a full debug log of http connection:

`` -Djava.util.logging.config.file=logging.properties -Djavax.net.debug=all``
