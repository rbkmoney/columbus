package com.rbkmoney.columbus.dao;

import com.rbkmoney.columbus.model.CityLocation;
import com.rbkmoney.columbus.model.Lang;
import com.rbkmoney.columbus.exception.DaoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CityLocationsDaoImpl extends NamedParameterJdbcDaoSupport implements CityLocationsDao {
    Logger log = LoggerFactory.getLogger(this.getClass());

    private static String RU_LOCATION_TABLE = "clb.city_locations_ru";
    private static String ENG_LOCATION_TABLE = "clb.city_locations_en";

    @Override
    public List<CityLocation> getByGeoIds(Set<Integer> geoIdset, Lang lang) throws DaoException {
        //TODO improve performance by doing only one request to db
        List<CityLocation> cityLocations = new ArrayList<>();
        for(int geoId: geoIdset){
            CityLocation cityLocation = getByGeoId(geoId, lang);
            cityLocations.add(cityLocation);
        }
        return cityLocations;
    }

    @Override
    public CityLocation getByGeoId(int geoId, Lang lang) {

        String locationTableName = "";
        if (lang.equals(Lang.RU)) {
            locationTableName = RU_LOCATION_TABLE;
        } else if (lang.equals(Lang.ENG)) {
            locationTableName = ENG_LOCATION_TABLE;
        } else {
            throw new DaoException("Unsupported language: " + lang.name());
        }

        String sql = "SELECT " +
                " geoname_id, locale_code, continent_code, continent_name, country_iso_code, " +
                " country_name, subdivision_1_iso_code, subdivision_1_name, subdivision_2_iso_code, " +
                " subdivision_2_name, city_name, metro_code, time_zone " +
                " FROM  " + locationTableName +
                " WHERE geoname_id = :geoname_id ";

        MapSqlParameterSource source = new MapSqlParameterSource("geoname_id", geoId);
        log.trace("SQL: {}, Params: {}", sql, source.getValues());
        return getNamedParameterJdbcTemplate().queryForObject(
                sql,
                source,
                getRowMapper()
        );
    }

    public CityLocationsDaoImpl(DataSource ds) {
        setDataSource(ds);
    }

    public static RowMapper<CityLocation> getRowMapper() {
        return BeanPropertyRowMapper.newInstance(CityLocation.class);
    }

}
