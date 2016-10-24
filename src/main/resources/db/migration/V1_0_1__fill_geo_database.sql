
COPY clb.city_locations_ru
FROM '/var/geodata/GeoLite2-City-Locations-ru.csv'
DELIMITER ',' CSV HEADER;

COPY clb.city_locations_eng
FROM '/var/geodata/GeoLite2-City-Locations-en.csv'
DELIMITER ',' CSV HEADER;