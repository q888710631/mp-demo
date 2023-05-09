package com.honyee.app.config;

public class Constants {
    // 基本包路径
    public static final String BASE_PACKAGE = "com.honyee.app";
    // mybatis的mapper接口路径
    public static final String MAPPER_PACKAGE = BASE_PACKAGE + ".mapper";
    // model包路径
    public static final String MODEL_PACKAGE = BASE_PACKAGE + ".model";
    // proxy包路径
    public static final String PROXY_PACKAGE = BASE_PACKAGE + ".proxy";

    // 实体属性名
    public static final String FIELD_CREATE_DATE = "createDate";
    public static final String FIELD_UPDATE_DATE = "updateDate";
    public static final String FIELD_TENANT_ID = "tenantId";

    // 表字段名
    public static final String COLUMN_TENANT_ID = "tenant_id";

    // 中文范围
    public static final String CN_RANGE = "\\u4e00-\\u9fa5";
    // trace id
    public static final String TRACE_ID = "traceId";

    // 正则 手机号
    public static final String REGEXP_PHONE_NUMBER = "^[1-9][0-9]{10}$";



}
