package com.honyee.app;

import org.junit.jupiter.api.Test;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.concurrent.TimeUnit;

public class SpelExampleTests {
    @Test
    public void test() {
        // 创建一个表达式解析器
        ExpressionParser parser = new SpelExpressionParser();
        // 定义一个表达式，计算两个数的和
        Expression exp = parser.parseExpression("10 + 5");
        // 计算表达式的值
        int result = (Integer) exp.getValue();
        // 输出计算结果
        System.out.println(result);
    }

    @Test
    public void test2() {
        String expressionStr = "#query";
        ExpressionParser parser = new SpelExpressionParser(); // SpelExpressionParser是Spring内部对ExpressionParser的唯一最终实现类
        Expression exp = parser.parseExpression(expressionStr); // 把该表达式，解析成一个Expression对象：SpelExpression

        EvaluationContext context = new StandardEvaluationContext();
        context.setVariable("query", "honyee");

        System.out.println(exp.getValue(context)); // 3
    }

    @Test
    public void test3() {
        String greetingExp = "Hello, #{#user} ---> #{T(System).getProperty('user.home')}";
        ExpressionParser parser = new SpelExpressionParser();
        EvaluationContext context = new StandardEvaluationContext();
        context.setVariable("user", "honyee");

        Expression expression = parser.parseExpression(greetingExp, new TemplateParserContext());
        System.out.println(expression.getValue(context, String.class)); // Hello, honyee ---> C:\Users\xxxx
        TimeUnit t = TimeUnit.SECONDS;
    }


}
