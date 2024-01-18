package logdsl.antlr4.openapi;

import logdsl.NginxLogLexer;
import logdsl.NginxLogParser;
import logdsl.antlr4.Loaders;
import logdsl.antlr4.engine.DSLEngine;
import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "/openapi")
@Slf4j
public class OpenApiControl {


    @RequestMapping("/logHandle")
    @ResponseBody
    public List<Map<String,String>> logHandle(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws FileNotFoundException {

        String logText = httpRequest.getParameter("logText");
        // 创建输入流
        ANTLRInputStream in_nocode = new ANTLRInputStream(logText);
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
        return loaders.rows;

    }
    @RequestMapping("/dslHandle")
    @ResponseBody
    public Object dslHandle(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws FileNotFoundException {

        String logText = httpRequest.getParameter("logText");
        String dslText = httpRequest.getParameter("dslText");
        // 创建输入流
        ANTLRInputStream in_nocode = new ANTLRInputStream(logText);
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

        List<Map<String,String>> input = loaders.rows;

        Map<String, Object> paramMap = new HashMap();
        paramMap.put("handleObj", input);

        Object result = DSLEngine.execute(dslText, paramMap);

        return result;

    }
}
