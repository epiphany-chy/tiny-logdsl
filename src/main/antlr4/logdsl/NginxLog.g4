grammar NginxLog;

// 定义日志整体结构，由行组成
accessLog
    : (rows? EOL)+ rows? EOF
    ;

// 定义每行日志的格式 ，使用标准格式
//nginx 数据格式如下
//'$scheme $http_host $remote_addr $server_addr [$time_local] "$request" '
//'$status $body_bytes_sent "$http_referer" "$http_user_agent" '
//'$upstream_addr '
//'$upstream_response_time $request_time';
//实际 nginx日志的输入为
//http ceshi.wz.dsl.com 110.243.70.31 100.99.96.38 [13/Dec/2023:15:29:37 +0800] "POST /gw/generic/aladdin/newna/m/addRateLimitCountForBottomBubble HTTP/1.0" 200 403 "-" "okhttp/4.11.0" 11.247.33.156 0.032 0.032
rows
    : scheme host remoteAddr serverAddr datetimetz request statuscode bytes referer useragent upstreamAddr upstreamResponseTime requestTime?
    ;
scheme
    : STRING
    ;
host
    : STRING
    | IP
    ;

upstreamAddr
    : STRING
    | SERVERIP
    ;

upstreamResponseTime
    : STRING
    ;

requestTime
    : STRING
    ;

remoteAddr
    : STRING
    | IP
    ;

serverAddr
    : STRING
    | IP
    ;

datetimetz
    : '[' DATE ':' TIME  TZ ']'
    ;

DATE
    : [0-9]+ '/' STRING '/' [0-9]+
    ;

TIME
    : [0-9]+ ':' [0-9]+ ':' [0-9]+
    ;

TZ
    : '+' [0-9]+
    ;

referer
    : LITERAL
    ;

request
    : LITERAL
    ;

useragent
    : LITERAL
    ;

statuscode
    : STRING
    ;

bytes
    : STRING
    ;

LITERAL
    : '"' ~ '"'* '"'
    ;

IP
    : [0-9]+ '.' [0-9]+ '.' [0-9]+ '.' [0-9]+
    ;

SERVERIP
    : [0-9]+ '.' [0-9]+ '.' [0-9]+ '.' [0-9]+ ':' [0-9]+
    ;

STRING
    : [a-zA-Z0-9();._-]+
    ;

EOL
    : '\r'? '\n'
    ;

WS
    : [ \t\r\n] -> skip
    ;