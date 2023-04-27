package com.honyee.app;

import com.honyee.app.utils.JsonUtil;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;
import org.noear.snack.ONode;

public class JsonTests {
    private static final Logger log = LoggerFactory.getLogger(JsonTests.class);


    @Test
    public void testJsonPath() {
        String[] paths = new String[]{"$.data.message", "$.data.logo", "$.data.title"
        };
        String source = "{\n" +
            "    \"data\": {\n" +
            "        \"message\": \"新\",\n" +
            "        \"logo\": \"新\",\n" +
            "        \"title\": \"新\"\n" +
            "    }\n" +
            "}";
        String target = "{\n" +
            "    \"data\": {\n" +
            "        \"message\": \"旧\",\n" +
//            "        \"logo\": \"旧\",\n" +
            "        \"name\": \"旧\",\n" +
            "        \"title\": \"旧\"\n" +
            "    }\n" +
            "}";

        // 合并指定项页面设置
        String result = JsonUtil.merge(source, target, paths);
        log.info(() -> "source:\n" + source);
        log.info(() -> "target:\n" + target);
        log.info(() -> "result:\n" + result);
    }

    @Test
    public void testLoadJson() {
        String json = null;
        ONode oNode = ONode.loadStr(json);
        ONode decoration = oNode.select("$.LAUNCH");
        int anInt = decoration.getInt();
        json = "{\"compName\":\"AnnualCardProd\",\"mode\":\"5\",\"data\":{\"title\":\"展示门票模式\",\"prods\":[{\"id\":3333,\"platformLid\":\"148833\",\"title\":\"zt年卡dev\",\"imgPath\":\"https://wanju-image.12301.cc/minipro/assets/bg/mall-placeholder.png\",\"firstPicture\":\"https://wanju-image.12301.cc/minipro/assets/bg/mall-placeholder2.png\",\"enable\":null,\"monthSaleNum\":5,\"totalSaleNum\":null,\"minSalePrice\":0,\"marketPrice\":0,\"productType\":\"ANNUAL_CARD_PACKAGE\",\"productTypeValue\":\"I\",\"venusId\":0,\"levelType\":\"\",\"area\":\"2|54|0\",\"areaId\":54,\"city\":\"河东区\",\"initial\":\"H\",\"orderIndex\":54,\"orderIndex2\":null,\"ticketTotal\":12,\"ticketList\":[{\"id\":149549,\"enable\":true,\"platformTicketId\":\"473004\",\"platformPid\":470108,\"platformProductId\":\"148833\",\"title\":\"sgn家庭三人卡需实名需人脸立即激活\",\"productName\":\"zt年卡dev sgn家庭三人卡需实名需人脸立即激活\",\"notes\":\"\",\"supplierId\":\"3207191\",\"validPeriodContent\":\"激活后30天有效\",\"todayCanSale\":true,\"productType\":\"ANNUAL_CARD_PACKAGE\",\"instruction\":\"\",\"validPeriodType\":\"MANY_DAY_AFTER_ACTIVE\",\"salePrice\":0,\"marketPrice\":0,\"startDate\":\"2023-04-19\",\"endDate\":\"2026-08-31\",\"customBookActive\":true,\"needFaceImage\":true,\"annualPact\":\"家庭卡\",\"faceImageMaxNum\":3,\"needIdentityCode\":true,\"familyCardNum\":3,\"stock\":6,\"annualCardPrivilegeGroupList\":[{\"id\":21160,\"platformGroupId\":1475,\"name\":\"喵星人星球2-测试333\",\"withLimit\":false,\"dayUseLimit\":-1,\"monthUseLimit\":-1,\"totalUseLimit\":-1,\"annualCardPrivileges\":[{\"id\":23335,\"ticket\":{\"id\":140939,\"platformTicketId\":\"134628\",\"platformProductId\":null,\"platformTicketPid\":null,\"title\":\"测试333\",\"pName\":\"喵星人星球2测试333\",\"pNameWithConnector\":\"喵星人星球2-测试333\"}}]}],\"activePeriodContent\":\"售出后30天内可激活\",\"availableTimePeriod\":\"不限\"},{\"id\":149548,\"enable\":true,\"platformTicketId\":\"473003\",\"platformPid\":470107,\"platformProductId\":\"148833\",\"title\":\"sgn个人卡激活需实名需人脸立即激活\",\"productName\":\"zt年卡dev sgn个人卡激活需实名需人脸立即激活\",\"notes\":\"\",\"supplierId\":\"3207191\",\"validPeriodContent\":\"激活后30天有效\",\"todayCanSale\":true,\"productType\":\"ANNUAL_CARD_PACKAGE\",\"instruction\":\"\",\"validPeriodType\":\"MANY_DAY_AFTER_ACTIVE\",\"salePrice\":0,\"marketPrice\":0,\"startDate\":\"2023-04-19\",\"endDate\":\"2026-08-31\",\"customBookActive\":true,\"needFaceImage\":true,\"annualPact\":\"\",\"faceImageMaxNum\":1,\"needIdentityCode\":true,\"familyCardNum\":0,\"stock\":9,\"annualCardPrivilegeGroupList\":[{\"id\":21157,\"platformGroupId\":1474,\"name\":\"喵星人星球2-测试333\",\"withLimit\":false,\"dayUseLimit\":-1,\"monthUseLimit\":-1,\"totalUseLimit\":-1,\"annualCardPrivileges\":[{\"id\":23332,\"ticket\":{\"id\":140939,\"platformTicketId\":\"134628\",\"platformProductId\":null,\"platformTicketPid\":null,\"title\":\"测试333\",\"pName\":\"喵星人星球2测试333\",\"pNameWithConnector\":\"喵星人星球2-测试333\"}}]}],\"activePeriodContent\":\"售出后30天内可激活\",\"availableTimePeriod\":\"不限\"}],\"salerId\":6052270,\"applyDid\":3207191,\"rowKey\":3},{\"id\":4648,\"platformLid\":\"152517\",\"title\":\"wdh-年卡4\",\"imgPath\":\"http://pft-scenic.12301.cc/material_bank/1681805001270.jpeg\",\"firstPicture\":\"http://pft-scenic.12301.cc/material_bank/1681805017591.jpeg\",\"enable\":null,\"monthSaleNum\":1,\"totalSaleNum\":null,\"minSalePrice\":100,\"marketPrice\":100,\"productType\":\"ANNUAL_CARD_PACKAGE\",\"productTypeValue\":\"I\",\"venusId\":0,\"levelType\":\"\",\"area\":\"1|36|0\",\"areaId\":36,\"city\":\"西城区\",\"initial\":\"X\",\"orderIndex\":36,\"orderIndex2\":null,\"ticketTotal\":1,\"ticketList\":[{\"id\":154605,\"enable\":true,\"platformTicketId\":\"492905\",\"platformPid\":490009,\"platformProductId\":\"152517\",\"title\":\"卡卡更健康2\",\"productName\":\"wdh-年卡4 卡卡更健康2\",\"notes\":\"\",\"supplierId\":\"3207191\",\"validPeriodContent\":\"激活后365天有效\",\"todayCanSale\":true,\"productType\":\"ANNUAL_CARD_PACKAGE\",\"instruction\":\"\",\"validPeriodType\":\"MANY_DAY_AFTER_ACTIVE\",\"salePrice\":100,\"marketPrice\":100,\"startDate\":\"2023-04-26\",\"endDate\":\"2024-04-16\",\"customBookActive\":false,\"needFaceImage\":false,\"annualPact\":\"\",\"faceImageMaxNum\":1,\"needIdentityCode\":false,\"familyCardNum\":0,\"stock\":0,\"annualCardPrivilegeGroupList\":[{\"id\":21689,\"platformGroupId\":2172,\"name\":\"喵星人星球2-测试333\",\"withLimit\":false,\"dayUseLimit\":-1,\"monthUseLimit\":-1,\"totalUseLimit\":-1,\"annualCardPrivileges\":[{\"id\":23879,\"ticket\":{\"id\":140939,\"platformTicketId\":\"134628\",\"platformProductId\":null,\"platformTicketPid\":null,\"title\":\"测试333\",\"pName\":\"喵星人星球2测试333\",\"pNameWithConnector\":\"喵星人星球2-测试333\"}}]}],\"activePeriodContent\":\"售出后30天内可激活\",\"availableTimePeriod\":\"不限\"}],\"salerId\":6060014,\"applyDid\":3207191,\"rowKey\":4}]}}";
        ONode select = ONode.loadStr(json).select("$.data.prods.id");
        System.out.println(select.ary());
    }
}
