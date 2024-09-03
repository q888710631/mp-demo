package com.honyee.app.service;

import com.honyee.app.mapper.PersonMapper;
import com.honyee.app.model.test.Person;
import com.honyee.app.service.base.MyService;
import org.springframework.stereotype.Service;

@Service
public class PersonService extends MyService<PersonMapper, Person> {

}
