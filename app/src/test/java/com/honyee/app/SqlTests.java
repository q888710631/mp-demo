package com.honyee.app;

import com.honyee.app.mapper.PersonMapper;
import com.honyee.app.mapper.PersonTestMapper;
import com.honyee.app.model.test.Person;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.Reader;
import java.util.List;

/**
 * 直接执行Mapper测试
 */
public class SqlTests {

    static Reader readerConfig;

    @BeforeAll
    public static void init() throws IOException {
        readerConfig = Resources.getResourceAsReader("test/mybatis/config/mybatis-config.xml");
    }

    private <T> T createMapper(Class<T> t) {
        SqlSessionFactory builder = new SqlSessionFactoryBuilder().build(readerConfig);
        SqlSession sqlSession = builder.openSession();
        return sqlSession.getConfiguration().getMapper(t, sqlSession);
    }

    @Test
    public void queryTest() {
        PersonTestMapper mapper = createMapper(PersonTestMapper.class);
        List<Person> all = mapper.findAll();
        System.out.println();
    }
}
