package com.rbkmoney.columbus.dao;

import com.rbkmoney.columbus.model.CityLocation;
import com.rbkmoney.columbus.model.Lang;
import com.rbkmoney.columbus.exception.DaoException;

public interface CityLocationsDao {

    CityLocation getByGeoId(int geoId, Lang lang) throws DaoException;

}

