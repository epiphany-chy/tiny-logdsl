package logdsl.antlr4.engine;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author caohongyu
 * date 2021/3/10
 * Copyright © 2004-2021 chy.com. All Rights Reserved.
 **/
@Slf4j
public class DslEngineTest  {



    @Test
    public void testCompile() {
        String express = "1+2";
        Serializable result = DSLEngine.compile(express);
        System.out.println(result);
        log.info("result: {}", result);
        assert result != null;
    }

    @Test
    public void testExecute() {
        //EngineConfig.DEFAULT.getImportMethod().addAll(Lists.newArrayList(DateFormatUtils.class.getDeclaredMethods()));
        String express = "$append(['key1': ['key11': 'value11']], ['key1.key12': 122])";
        Object result = DSLEngine.execute(express, null);

        log.info("result: {}", result);
        assert result instanceof Map;
    }

    @Test
    public void testMd5UpperCase() {
        Map<String, Object> paramMap = new HashMap();
        paramMap.put("bizId", "123");
        paramMap.put("time", "11111");
        //
        Object result = DSLEngine.execute("$md5UpperCase(bizId+time+\"d0b05f697b824ccdba1d02ab82984cb0\")", paramMap);
        assert result != null;
        log.info("result:{}", result);
    }

    @Test
    public void testMd5LowerCase() {
        Map<String, Object> paramMap = new HashMap();
//        paramMap.put("bizId", "123");
//        paramMap.put("time", "11111");
//        Object result = DSLEngine.execute("$md5LowerCase(\"zhangshan\")", paramMap);
        Object result = DSLEngine.execute("$mod100(\"zhangshan\") > 70", paramMap);
        assert result != null;
        log.info("result:{}", result);
    }

    @Test
    public void logToJson() {
        Map<String, Object> paramMap = new HashMap();
//        paramMap.put("bizId", "123");
//        paramMap.put("time", "11111");
//        Object result = DSLEngine.execute("$md5LowerCase(\"zhangshan\")", paramMap);
        Object result = DSLEngine.execute("$logTextToMap('http ceshi.wz.dsl.com 110.243.70.31 100.99.96.38 [13/Dec/2023:15:29:37 +0800] \"POST /gw/generic/aladdin/newna/m/addRateLimitCountForBottomBubble HTTP/1.0\" 200 403 \"-\" \"okhttp/4.11.0\" 11.247.33.156:1601 0.032 0.032 10.253.158.254, 11.244.105.23 req_normal-',null)", paramMap);
        assert result != null;
        log.info("result:{}", result);
    }
}