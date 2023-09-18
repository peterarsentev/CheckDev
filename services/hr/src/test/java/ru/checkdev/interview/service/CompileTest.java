package ru.checkdev.interview.service;

import org.junit.Ignore;
import org.junit.Test;


import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
/**

 *
 * @author Petr Arsentev (parsentev@yandex.ru)
 * @version $Id$
 * @since 0.1
 */
@Ignore
public class CompileTest {
    @Test
    public void executeTest() {
        String source = ""
               + "class User {}"
               + "public class Calc {\n"
               + "    public int add(int first, int second) {\n"
               + "        return first + second;\n"
               + "    }\n"
               + "}";
        String test = "import org.junit.Test;"
               + "import static org.hamcrest.CoreMatchers.is;\n"
               + "import static org.junit.Assert.*;\n"
               + "import java.io.IOException;\n"
               + "\n"
               + "public class CalcTest {\n"
               + "    @Test\n"
               + "    public void whenOneToOneThenTwo() {\n"
               + "        try {\n"
               + "            new java.io.File(\"hello_from_test.log\").createNewFile();\n"
               + "        } catch (java.io.IOException e) {\n"
               + "            e.printStackTrace();\n"
               + "        }"
               + "        Calc calc = new Calc();\n"
               + "        User user = new User();"
               + "        int result = calc.add(1, 1);\n"
               + "        assertThat(result, is(2));\n"
               + "    }\n"
               + "    @Test\n"
               + "    public void whenTwoToTwoThenTwo() {\n"
               + "        Calc calc = new Calc();\n"
               + "        User user = new User();"
               + "        int result = calc.add(2, 2);\n"
               + "        assertThat(result, is(4));\n"
               + "    }\n"
               + "}";
        long start = System.currentTimeMillis();
        String log = new Compile().task(
                new Compile.Source("Calc", source),
                new Compile.Source("CalcTest", test)
        ).getInfo();
        System.out.println(log);
        assertThat(true, is(true));
    }
}