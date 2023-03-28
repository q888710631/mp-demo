package com.honyee.app;

import org.junit.jupiter.api.Test;

import javax.tools.*;
import java.io.File;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;

/**
 * Java 中的 EVAL 工具是通过动态编译和加载代码实现的，可以在运行时动态生成、编译、加载和执行代码。
 */
public class EvalExampleTests {
    @Test
    public void test() throws Exception {
        // 创建一个编译器
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        // 创建一个文件管理器，用于管理编译器的输入和输出
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
        // 创建一个输出流，用于将代码写入文件
        StringWriter writer = new StringWriter();
        writer.write("public class HelloWorld {");
        writer.write("    public static void main(String[] args) {");
        writer.write("        System.out.println(\"Hello, world!\");");
        writer.write("    }");
        writer.write("}");
        writer.close();
        // 将代码写入文件
        JavaFileObject source = new JavaSourceFromString("HelloWorld", writer.toString());
        // 编译代码，并将编译的结果输出到指定的目录
        Iterable<? extends JavaFileObject> compilationUnits = Arrays.asList(source);
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        // 创建一个编译任务
        compiler.getTask(null, fileManager, diagnostics, null, null, compilationUnits).call();
        fileManager.close();
        // 加载编译后的类，并调用它的 main 方法
        URLClassLoader classLoader = URLClassLoader.newInstance(new URL[]{new File("").toURI().toURL()});
        Class<?> cls = Class.forName("HelloWorld", true, classLoader);
        Method method = cls.getMethod("main", String[].class);
        method.invoke(null, (Object) new String[]{});
    }
}

// 定义一个用于将代码写入文件的类
class JavaSourceFromString extends SimpleJavaFileObject {
    final String code;

    JavaSourceFromString(String name, String code) {
        super(URI.create("string:///" + name.replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE);
        this.code = code;
    }

    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors) {
        return code;
    }
}