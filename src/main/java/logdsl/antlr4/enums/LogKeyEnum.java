package logdsl.antlr4.enums;


/**
 * 日志key枚举类，统一管理
 * scheme host remoteAddr serverAddr datetimetz request statuscode bytes referer
 * useragent upstreamAddr upstreamResponseTime requestTime
 * @date 2023/12/15
 */
public enum LogKeyEnum {

    scheme("scheme", "scheme"),
    host("host", "host"),
    remoteAddr("clientIp", "remoteAddr"),
    serverAddr("localIp", "serverAddr"),
    datetimetz("requestTime", "datetimetz"),
    request("request", "request"),
    statuscode("status", "statuscode"),
    bytes("responseSize", "bytes"),
    referer("referer", "referer"),
    useragent("ua", "useragent"),
    /**
     * 后端机器ip
     */
    upstreamAddr("serverIp", "upstreamAddr"),
    /**
     * 负载耗时
     */
    upstreamResponseTime("nginxTime", "upstreamResponseTime"),
    /**
     * 请求响应总耗时
     */
    requestTime("allTime", "requestTime");

    private String dslName;
    private String formatName;

    LogKeyEnum(String dslName, String formatName) {
        this.dslName = dslName;
        this.formatName = formatName;
    }

    public String getDslName() {
        return this.dslName;
    }

    public String getFormatName() {
        return this.formatName;
    }
}
