package com.rbkmoney.columbus.dao;

import com.rbkmoney.columbus.exception.DaoException;
import com.rbkmoney.columbus.model.CityLocation;
import com.rbkmoney.columbus.model.Lang;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

@Slf4j
public class CityLocationsDaoImpl extends NamedParameterJdbcDaoSupport implements CityLocationsDao {
    private final CityLocationRowMapper cityLocationRowMapper = new CityLocationRowMapper();

    private static String RU_LOCATION_TABLE = "clb.city_locations_ru";
    private static String ENG_LOCATION_TABLE = "clb.city_locations_en";

    @Override
    public List<CityLocation> getByGeoIds(Set<Integer> geoIdset, Lang lang) throws DaoException {
        String locationTableName = getLocationTableName(lang);

        String sql = "SELECT " +
                " geoname_id, locale_code, continent_code, continent_name, country_iso_code, " +
                " country_name, subdivision_1_iso_code, subdivision_1_name, subdivision_2_iso_code, " +
                " subdivision_2_name, city_name, metro_code, time_zone " +
                " FROM  " + locationTableName +
                " WHERE geoname_id in (:geoname_ids) ";

        MapSqlParameterSource source = new MapSqlParameterSource("geoname_ids", geoIdset);
        log.trace("SQL: {}, Params: {}", sql, source.getValues());
        return getNamedParameterJdbcTemplate().query(
                sql,
                source,
                cityLocationRowMapper
        );
    }

    private String getLocationTableName(Lang lang){
        if (lang.equals(Lang.RU)) {
            return RU_LOCATION_TABLE;
        } else if (lang.equals(Lang.ENG)) {
            return ENG_LOCATION_TABLE;
        } else {
            throw new DaoException("Unsupported language: " + lang.name());
        }
    }

    public CityLocationsDaoImpl(DataSource ds) {
        setDataSource(ds);
    }

    private static class CityLocationRowMapper implements RowMapper<CityLocation> {
        @Override
        public CityLocation mapRow(ResultSet resultSet, int i) throws SQLException {
            CityLocation cl = new CityLocation();
            cl.setGeonameId(resultSet.getInt("geoname_id"));
            cl.setLocaleCode(resultSet.getString("locale_code"));
            cl.setContinentCode(resultSet.getString("continent_code"));
            cl.setContinentName(resultSet.getString("continent_name"));
            cl.setCountryIsoCode(resultSet.getString("country_iso_code"));
            cl.setCountryName(resultSet.getString("country_name"));
            cl.setSubdivision1IsoCode(resultSet.getString("subdivision_1_iso_code"));
            cl.setSubdivision1Name(resultSet.getString("subdivision_1_name"));
            cl.setSubdivision2IsoCode(resultSet.getString("subdivision_2_iso_code"));
            cl.setSubdivision2IsoCode(resultSet.getString("subdivision_2_name"));
            cl.setCityName(resultSet.getString("city_name"));
            cl.setMetroCode(resultSet.getString("metro_code"));
            cl.setTimeZone(resultSet.getString("time_zone"));

            return cl;
        }
    }
}
