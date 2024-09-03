package com.honyee.app.config.mybatis.handler;

import com.fasterxml.jackson.core.type.TypeReference;
import com.honyee.app.model.test.Human;

import java.util.List;

public class ListHumanHandler extends MybatisJsonTypeEntityHandler<List<Human>> {

    public ListHumanHandler(Class<List<Human>> type) {
        super(type, new TypeReference<>() {});
    }
}