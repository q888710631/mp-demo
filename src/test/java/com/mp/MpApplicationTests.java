package com.mp;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.pagehelper.PageHelper;
import com.mp.config.Constants;
import com.mp.config.MybatisPlusTenantHandler;
import com.mp.enums.StateEnum;
import com.mp.mapper.CityMapper;
import com.mp.mapper.ProductMapper;
import com.mp.model.City;
import com.mp.model.Product;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.reflections.Reflections;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.ReflectionUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;


@SpringBootTest
class MpApplicationTests {
    // PageHelper 分页起始
    static final int PAGE_HELPER_START = 1;
    // RowBounds 分页起始
    static final int ROW_BOUNDS_START = 0;
    // 分页大小
    static final int PAGE_SIZE = 10;

    @Resource
    CityMapper cityMapper;

    @Resource
    SqlSession sqlSession;

    @Resource
    ProductMapper productMapper;

    @BeforeAll
    public static void beforeAll() {
        MybatisPlusTenantHandler.setTenantValue(1L);
        System.out.println("### 租户注入");
    }

    @Test
    public void emptyInit() {

        System.out.println();
    }

    @Test
    public void createProduct() {
        Product p = new Product();
        p.setTitle("产品" + System.currentTimeMillis());
        p.setState(StateEnum.SUCCESS);
        productMapper.insert(p);
        System.out.println();
    }

    @Test
    void queryCity() {
        // todo 判断 City没有继承多租户
        com.github.pagehelper.Page<City> page = PageHelper.startPage(PAGE_HELPER_START, PAGE_SIZE).doSelectPage(() -> cityMapper.selectList(new QueryWrapper<>()));
        System.out.println();

//        PageInfo<City> pageInfo = PageHelper.startPage(PAGE_HELPER_START, PAGE_SIZE).doSelectPageInfo(() -> cityMapper.selectList(new QueryWrapper<>()));
//        List<City> list = pageInfo.getList();

//        long count = PageHelper.count(() -> cityMapper.selectList(new QueryWrapper<>()));

//        City city = new City();
//        city.setId(1);
//        List<CitySimpleDTO> simple = cityMapper.findSimple(city);

//        List<CitySimpleDTO> objects = sqlSession.selectList("com.mp.mapper.CityMapper.findSimple", city, new RowBounds(ROW_BOUNDS_START, PAGE_SIZE));

//        CityMapper mapper = sqlSession.getMapper(CityMapper.class);
    }

    @Test
    void queryProduct() {
        //
        LambdaQueryWrapper<Product> eq = new LambdaQueryWrapper<Product>().eq(Product::getId, "");
        //
        List<Product> products = productMapper.selectList(new QueryWrapper<Product>().orderByDesc("id"));
        //
        Page<Product> page2 = new Page<>(PAGE_HELPER_START, PAGE_SIZE);
        productMapper.selectPage(page2, new QueryWrapper<Product>().orderByDesc("id"));
        List<Product> records = page2.getRecords();
        System.out.println();
    }

    @Test
    public void deleteProduct() {
        productMapper.deleteById(999);
    }
}
