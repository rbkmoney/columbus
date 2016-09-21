# U may run it
# docker run -p=5432:5432  -e POSTGRES_PASSWORD=postgres -e POSTGRES_USER=postgres -e POSTGRES_DB=columbus
# docker run -ti  --volumes-from my_pg   dr.rbkmoney.com/rbkmoney/bustermaze:3ad50e51cbc0982d98c593433d12fbc83ccfded0 /bin/bash
# docker run  --volumes-from gdb   test_columb
FROM dr.rbkmoney.com/rbkmoney/postgres:9.6
MAINTAINER Semenkov Alexey <a.semenkov@rbkmoney.com>
COPY GeoLite2-city.mmdb /var/geodata/GeoLite2-city.mmdb
COPY GeoLite2-City-Locations-en.csv /var/geodata/GeoLite2-City-Locations-en.csv
COPY GeoLite2-City-Locations-ru.csv /var/geodata/GeoLite2-City-Locations-ru.csv
VOLUME /var/geodata


