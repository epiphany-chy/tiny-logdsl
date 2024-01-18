package logdsl.antlr4;


import logdsl.NginxLogBaseListener;
import logdsl.NginxLogParser;
import logdsl.antlr4.enums.LogKeyEnum;
import logdsl.antlr4.utils.DateHandleUtils;

import java.util.*;

/**
 * NGINX日志转换为结构化数据核心处理类
 *
 */
public class Loaders extends NginxLogBaseListener {


    /**
     * 日志解析后的集合，方便后期的数据分析
     */
    public List<Map<String,String>> rows = new ArrayList<Map<String,String>>();

    /**
     * 定义map类型的作为存储
     * 对权威指南185页的代码进行了优化
     */
    Map<String,String> currentLog = new HashMap<>();




    @Override public void exitHost(NginxLogParser.HostContext ctx) {
        currentLog.put(LogKeyEnum.host.getDslName(),ctx.STRING().getText());
    }
    @Override public void exitStatuscode(NginxLogParser.StatuscodeContext ctx) {
        currentLog.put(LogKeyEnum.statuscode.getDslName(),ctx.STRING().getText());
    }

    @Override public void exitRequest(NginxLogParser.RequestContext ctx) {
        currentLog.put(LogKeyEnum.request.getDslName(),ctx.LITERAL().getText());
    }

    @Override public void exitUpstreamAddr(NginxLogParser.UpstreamAddrContext ctx) {
        currentLog.put(LogKeyEnum.upstreamAddr.getDslName(),ctx.getText());
    }

    @Override public void exitUpstreamResponseTime(NginxLogParser.UpstreamResponseTimeContext ctx) {
        currentLog.put(LogKeyEnum.upstreamResponseTime.getDslName(),ctx.STRING().getText());
    }

    @Override public void exitRequestTime(NginxLogParser.RequestTimeContext ctx) {
        currentLog.put(LogKeyEnum.requestTime.getDslName(),ctx.STRING().getText());
    }

    @Override public void exitRemoteAddr(NginxLogParser.RemoteAddrContext ctx) {
        currentLog.put(LogKeyEnum.remoteAddr.getDslName(),ctx.getText());
    }

    @Override public void exitDatetimetz(NginxLogParser.DatetimetzContext ctx) {
        currentLog.put(LogKeyEnum.datetimetz.getDslName(), DateHandleUtils.dateFormatByNginxTime(ctx.getText()));
    }

    @Override public void exitReferer(NginxLogParser.RefererContext ctx) {
        currentLog.put(LogKeyEnum.referer.getDslName(),ctx.LITERAL().getText());
    }

    @Override public void exitUseragent(NginxLogParser.UseragentContext ctx) {
        currentLog.put(LogKeyEnum.useragent.getDslName(),ctx.LITERAL().getText());
    }

    @Override public void exitBytes(NginxLogParser.BytesContext ctx) {
        currentLog.put(LogKeyEnum.bytes.getDslName(),ctx.STRING().getText());
    }

    @Override public void exitScheme(NginxLogParser.SchemeContext ctx) {
        currentLog.put(LogKeyEnum.scheme.getDslName(),ctx.STRING().getText());
    }

    @Override public void exitRows(NginxLogParser.RowsContext ctx) {
        //注意，需要深度克隆，否则得到list不对
        Map<String, String> setmap = new HashMap<String, String>();
        setmap.putAll(currentLog);
        rows.add(setmap);
    }

}
