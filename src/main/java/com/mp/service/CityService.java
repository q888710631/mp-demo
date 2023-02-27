package com.mp.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mp.mapper.CityMapper;
import com.mp.model.City;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class CityService extends ServiceImpl<CityMapper, City> implements InitializingBean {

    @Resource
    CityMapper cityMapper;

    @Override
    public CityMapper getBaseMapper() {
        return cityMapper;
    }

    @Override
    public void afterPropertiesSet() {

    }
}
