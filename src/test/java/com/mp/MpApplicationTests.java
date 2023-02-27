package com.mp;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mp.config.MybatisPlusTenantHandler;
import com.mp.dto.CitySimpleDTO;
import com.mp.enums.StateEnum;
import com.mp.mapper.CityMapper;
import com.mp.mapper.ProductMapper;
import com.mp.mapper.TenantMapper;
import com.mp.model.City;
import com.mp.model.Product;
import com.mp.model.Tenant;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

@SpringBootTest
class MpApplicationTests {
    private Logger log = LoggerFactory.getLogger(MpApplicationTests.class);

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

    @Resource
    TenantMapper tenantMapper;

    @BeforeAll
    public static void beforeAll() {
        MybatisPlusTenantHandler.setTenantValue(1L);
        System.out.println("### 租户注入");
    }

    @Test
    public void emptyInit() {

        log.info(() -> "emptyInit");
    }

    @Test
    void queryTenant() {
        List<Tenant> tenants = tenantMapper.selectList(new QueryWrapper<>());
        log.info(() -> "queryTenant");
    }

    @Test
    void queryCity() {
        com.github.pagehelper.Page<City> page = PageHelper.startPage(PAGE_HELPER_START, PAGE_SIZE).doSelectPage(() -> cityMapper.selectList(new QueryWrapper<>()));
        System.out.println();

        PageInfo<City> pageInfo = PageHelper.startPage(PAGE_HELPER_START, PAGE_SIZE).doSelectPageInfo(() -> cityMapper.selectList(new QueryWrapper<>()));
        List<City> list = pageInfo.getList();

        long count = PageHelper.count(() -> cityMapper.selectList(new QueryWrapper<>()));

        City city = new City();
        city.setId(1);
        List<CitySimpleDTO> simple = cityMapper.findSimple(city);

        List<CitySimpleDTO> objects = sqlSession.selectList("com.mp.mapper.CityMapper.findSimple", city, new RowBounds(ROW_BOUNDS_START, PAGE_SIZE));

        CityMapper mapper = sqlSession.getMapper(CityMapper.class);
        log.info(() -> "queryCity");
    }

    @Test
    public void createProduct() {
        Product p = new Product();
        p.setTitle("产品" + System.currentTimeMillis());
        p.setState(StateEnum.SUCCESS);
        productMapper.insert(p);
        log.info(() -> "createProduct");
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
        log.info(() -> "queryProduct");
    }

    @Test
    public void deleteProduct() {
        productMapper.deleteById(999);
        log.info(() -> "deleteProduct");
    }
}
