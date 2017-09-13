package com.insightsuen.stayfoolish.model;

import org.greenrobot.greendao.converter.PropertyConverter;

/**
 * Created by InSight Suen on 2017/9/12.
 */

public class NoteTypeConverter implements PropertyConverter<NoteType, String> {

    @Override
    public NoteType convertToEntityProperty(String databaseValue) {
        return NoteType.valueOf(databaseValue);
    }

    @Override
    public String convertToDatabaseValue(NoteType entityProperty) {
        return entityProperty.name();
    }
}
