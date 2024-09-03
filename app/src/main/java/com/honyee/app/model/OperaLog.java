package com.honyee.app.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.honyee.app.config.mybatis.handler.MybatisJsonTypeEntityHandler;
import com.honyee.app.model.base.BaseTenantEntity;
import com.honyee.app.enums.OperaTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;

@Schema(title = "操作日志，尚未写入数据库")
public class OperaLog extends BaseTenantEntity {
    @Schema(title = "id")
    @TableId(type = IdType.AUTO)
    private Serializable id;

    @Schema(title = "链路ID")
    @TableField("trace_id")
    private String traceId;

    @Schema(title = "请求URL")
    @TableField("request_url")
    private String requestUrl;

    @Schema(title = "请求Method")
    @TableField("request_method")
    private String requestMethod;

    @Schema(title = "操作类型")
    @TableField("opera_type")
    private OperaTypeEnum operaType;

    @Schema(title = "标题")
    @TableField("title")
    private String title;

    @Schema(title = "实体ID")
    @TableField("entity_id")
    private Serializable entityId;

    @Schema(title = "改动前的值")
    @TableField(value = "entity_before", typeHandler = MybatisJsonTypeEntityHandler.class)
    private Object entityBefore;

    @Schema(title = "改动后的值")
    @TableField(value = "entity_after", typeHandler = MybatisJsonTypeEntityHandler.class)
    private Object entityAfter;

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public void setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
    }

    public OperaTypeEnum getOperaType() {
        return operaType;
    }

    public void setOperaType(OperaTypeEnum operaType) {
        this.operaType = operaType;
    }

    public Object getEntityBefore() {
        return entityBefore;
    }

    public void setEntityBefore(Object entityBefore) {
        this.entityBefore = entityBefore;
    }

    public Object getEntityAfter() {
        return entityAfter;
    }

    public void setEntityAfter(Object entityAfter) {
        this.entityAfter = entityAfter;
    }

    public Serializable getId() {
        return id;
    }

    public void setId(Serializable id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Serializable getEntityId() {
        return entityId;
    }

    public void setEntityId(Serializable entityId) {
        this.entityId = entityId;
    }
}
