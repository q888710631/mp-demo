package com.honyee.app.utils.crypt;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class XmlUtil {

    private static final Logger log = LoggerFactory.getLogger(XmlUtil.class);

    private XmlUtil() {}

    /**
     * 解析xml 字节点
     */
    public static Map<String, String> doXMLChildParse(Object strxml) throws IOException, JDOMException {
        return doXMLParse(String.format("<xml>%s</xml>", strxml));
    }

    public static Map<String, String> doXMLParseChild(Map<String, String> xmlMap, String childKey) throws IOException, JDOMException {
        return doXMLParse("<xml>" + xmlMap.getOrDefault(childKey, "") + "</xml>");
    }

    public static Map<String, String> doXMLParseChild(String strxml) throws IOException, JDOMException {
        return doXMLParse("<xml>" + strxml + "</xml>");
    }

    /**
     * 解析xml
     */
    public static Map<String, String> doXMLParse(String strxml) throws IOException, JDOMException {
        if (strxml == null) {
            return Collections.emptyMap();
        }
        strxml = strxml.replaceFirst("encoding=\".*\"", "encoding=\"UTF-8\"");
        if ("".equals(strxml)) {
            return Collections.emptyMap();
        }
        Map<String, String> map = new HashMap<>();
        try (InputStream in = new ByteArrayInputStream(strxml.getBytes(StandardCharsets.UTF_8))) {
            SAXBuilder builder = new SAXBuilder();
            Document doc = builder.build(in);
            Element root = doc.getRootElement();
            for (Object o : root.getChildren()) {
                Element e = (Element) o;
                String k = e.getName();
                List<?> children = e.getChildren();
                if (children.isEmpty()) {
                    map.put(k, e.getTextNormalize());
                } else {
                    map.put(k, getChildrenText(children));
                }
            }
        }
        return map;
    }

    /**
     * 获取子结点的xml
     */
    public static String getChildrenText(List<?> children) {
        StringBuilder sb = new StringBuilder();
        if (children != null && !children.isEmpty()) {
            for (Object child : children) {
                Element e = (Element) child;
                String name = e.getName();
                String value = e.getTextNormalize();
                List<?> list = e.getChildren();
                sb.append(String.format("<%s>", name));
                if (!list.isEmpty()) {
                    sb.append(getChildrenText(list));
                }
                sb.append(value);
                sb.append(String.format("</%s>", name));
            }
        }
        return sb.toString();
    }

    @SuppressWarnings("unused")
    public static String getRequestXml(SortedMap<String, String> parameters) {
        StringBuilder sb = new StringBuilder();
        sb.append("<xml>");
        Set<Map.Entry<String, String>> es = parameters.entrySet();
        for (Map.Entry<String, String> e : es) {
            String k = e.getKey();
            String v = e.getValue();
            if ("attach".equalsIgnoreCase(k) || "body".equalsIgnoreCase(k) || "sign".equalsIgnoreCase(k)) {
                sb.append(String.format("<%s><![CDATA[%s]]></%s>", k, v, k));
            } else {
                sb.append(String.format("<%s>%s</%s>", k, v, k));
            }
        }
        sb.append("</xml>");
        return sb.toString();
    }

    @SuppressWarnings("unchecked")
    public static Object[] parseXmlToList(String xml) {
        List<Map<String, String>> argMapList = new ArrayList<>();
        Map<String, String> retMap = new HashMap<>();
        try (StringReader read = new StringReader(xml)) {
            // 创建新的输入源SAX 解析器将使用 InputSource 对象来确定如何读取 XML 输入
            InputSource source = new InputSource(read);
            // 创建一个新的SAXBuilder
            SAXBuilder sb = new SAXBuilder();
            // 通过输入源构造一个Document
            Document doc = sb.build(source);
            Element root = doc.getRootElement(); // 指向根节点
            List<Element> es = root.getChildren();
            if (es != null && !es.isEmpty()) {
                for (Element element : es) {
                    retMap.put(element.getName(), element.getText());
                }
            }
            argMapList.add(retMap);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return new Object[] { argMapList };
    }
}
