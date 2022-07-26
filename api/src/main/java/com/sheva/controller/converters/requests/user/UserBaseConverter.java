package com.sheva.controller.converters.requests.user;

import com.sheva.controller.requests.user.UserCreateRequest;
import com.sheva.domain.Gender;
import com.sheva.domain.User;
import org.springframework.core.convert.converter.Converter;

import java.sql.Timestamp;
import java.util.Date;

public abstract class UserBaseConverter<S, T> implements Converter<S, T> {

    protected User doConvert(User userForUpdate, UserCreateRequest source) {

        userForUpdate.setUserName(source.getUserName());
        userForUpdate.setUserSurname(source.getUserSurname());
        userForUpdate.setGender(Gender.valueOf(source.getGender()));
        userForUpdate.setBirth(source.getBirth());

        userForUpdate.setIsDeleted(false);
        userForUpdate.setModificationDate(new Timestamp(new Date().getTime()));

        return userForUpdate;
    }
}
