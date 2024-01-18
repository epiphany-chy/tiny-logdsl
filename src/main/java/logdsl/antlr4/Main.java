package logdsl.antlr4;

import logdsl.NginxLogLexer;
import logdsl.NginxLogParser;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.io.FileInputStream;
import java.io.InputStream;

public class Main {
    public static void main(String[] args) {
        try {
            // 创建输入流
            InputStream input = new FileInputStream("/Users/caohongyu5/Documents/project/tools/tiny-logdsl-antlr4/src/main/resources/access.log");
            ANTLRInputStream in_nocode = new ANTLRInputStream(input);
            // 创建ANTLR词法分析器
            NginxLogLexer lexer = new NginxLogLexer(in_nocode);
            // 创建ANTLR语法分析器
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            NginxLogParser parser = new NginxLogParser(tokens);
            ParseTreeWalker walker = new ParseTreeWalker();

            Loaders loaders = new Loaders();
            // 获取根节点
            ParseTree tree = parser.accessLog();
            walker.walk(loaders, tree);
            System.out.println(loaders.rows);

        } catch (Exception e) {
            if (e.getMessage() != null) {
            } else {
                e.printStackTrace();
            }
        }
    }
}
