# Columbus
Сервис получения местоположения по IP

При старте поднимает ин мемори базу данных  от maxmind.com которая читает из файла *.mmdb.
База maxmind принимает только два вида запросов, дай мне страну по IP, дай мне город по IP.
Ответы содержат id объектов, из одного пространства значейний для разных типов объектов
(Континет, Страна, Подразделение первого уровня, Подразделение второго уровня, Город),
имена и коды обектов на разных языках, и доп информацию.


### Найденые проблемы:
* Запрос вида дай мне город, не всегда находит город, и может вернуть только континет или страну.
(например для ip: 89.218.51.9)
* Для некоторых типов обектов(например, подразделений первого уровня - области в РФ) нет своего geoname_id в базе, 
хотя подразделение есть и фигирирует в описаниях других объектов. Тестовые запросы в бд
<pre>
select DISTINCT subdivision_1_iso_code, subdivision_1_name
FROM city_locations_en
WHERE country_name = 'Russia'
--return 83 records

select DISTINCT subdivision_1_iso_code, subdivision_1_name
FROM city_locations_en
WHERE country_name = 'Russia' and subdivision_2_iso_code is null and city_name is null;
--return 41 records
</pre>










