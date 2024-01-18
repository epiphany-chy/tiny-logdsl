package logdsl.antlr4.engine;

import com.google.common.collect.Lists;
import logdsl.antlr4.engine.script.StaticScript;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.mvel2.MVEL;

import java.io.Serializable;

/**
 * @author caohongyu
 * @date 2021/2/23
 * Copyright © 2004-2021 chy.com. All Rights Reserved.
 **/
@Slf4j
public class DSLEngine {

    /**
     * init the default global config.
     */
    static {
        EngineConfig.DEFAULT.getImportMethod().addAll(Lists.newArrayList(StaticScript.class.getDeclaredMethods()));
    }

    /**
     * compile the express script if not compiled.
     * otherwise, return the cached result.
     * @param express
     * @return
     */
    @SneakyThrows
    public static Serializable compile (String express) {
        return EngineConfig.DEFAULT.cache.get(express);
    }

    /**
     * execute the script with input the context value.
     * @param express script
     * @param value context
     * @param <C> the type of context
     * @param <R> the type of result
     * @return result
     */
    @SneakyThrows
    public static <C, R> R execute (String express, C value) {
        return (R) MVEL.executeExpression(EngineConfig.DEFAULT.cache.get(express), value);
    }

}
