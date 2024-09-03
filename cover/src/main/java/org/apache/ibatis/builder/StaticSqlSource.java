package org.apache.ibatis.builder;

import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.session.Configuration;
import org.mybatis.logging.Logger;
import org.mybatis.logging.LoggerFactory;

import java.util.List;

public class StaticSqlSource implements SqlSource {
    private static final Logger log = LoggerFactory.getLogger(StaticSqlSource.class);

    private final String sql;
    private final List<ParameterMapping> parameterMappings;
    private final Configuration configuration;

    public static boolean initComplete = false;

    public StaticSqlSource(Configuration configuration, String sql) {
        this(configuration, sql, null);
    }

    public StaticSqlSource(Configuration configuration, String sql, List<ParameterMapping> parameterMappings) {
        // 解决日志打印N个换行的问题
        this.sql = sql.replaceAll("\n"," ");
        this.parameterMappings = parameterMappings;
        this.configuration = configuration;
        if (initComplete) {
            log.debug(() -> this.sql);
        }
    }

    @Override
    public BoundSql getBoundSql(Object parameterObject) {
        return new BoundSql(configuration, sql, parameterMappings, parameterObject);
    }

}