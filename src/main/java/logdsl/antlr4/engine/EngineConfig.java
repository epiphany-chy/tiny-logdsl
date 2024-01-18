package logdsl.antlr4.engine;

import com.google.common.base.Equivalence;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import logdsl.antlr4.engine.support.FilterClassLoader;
import logdsl.antlr4.engine.support.SafeAccessor;
import logdsl.antlr4.engine.support.SecurityScriptClassLoader;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.mvel2.MVEL;
import org.mvel2.ParserContext;
import org.mvel2.compiler.AbstractParser;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

/**
 * the static logdsl.antlr4.engine config.
 * @author caohongyu
 * date 2021/3/11
 * Copyright © 2004-2021 chy.com. All Rights Reserved.
 **/
@Slf4j
public class EngineConfig {

    @Getter
    public static final EngineConfig DEFAULT = new EngineConfig();

    static {
        Set<Class<?>> forbidden = Sets.newHashSet(
                System.class,
                SecurityManager.class,
                ClassLoader.class,
                Runtime.class,
                Compiler.class,
                Thread.class,
                ThreadLocal.class
        );
        EngineConfig.DEFAULT.classLoader.registerForbiddenClass(forbidden);
        for (Class<?> cls : forbidden) {
            AbstractParser.LITERALS.put(cls.getSimpleName(), SafeAccessor.class);
            AbstractParser.CLASS_LITERALS.put(cls.getSimpleName(), SafeAccessor.class);
        }
    }

    @Getter
    List<Method> importMethod = Lists.newLinkedList();

    @Getter
    private CacheLoader loader = new CacheLoader () {
        @Override
        public Object load(Object o) throws Exception {
            ParserContext ctx = ParserContext.create();
            ctx.getParserConfiguration().setClassLoader(classLoader);
            importMethod.forEach(v -> ctx.addImport(v.getName(), v));
            return MVEL.compileExpression((String) o, ctx);
        }
    };

    @Setter
    LoadingCache<String, Serializable> cache = builder("initialCapacity=256,maximumSize=204800,expireAfterAccess=30m", null).build(loader);

    @Setter
    @Getter
    private FilterClassLoader classLoader = new SecurityScriptClassLoader(Thread.currentThread().getContextClassLoader());

    /**
     * builder for guava cache.
     * rewrite key hash and equals.
     * @param spec config spec.
     * @param listener remove listener.
     * @param <K> type of key.
     * @param <V> type of value.
     * @return CacheBuilder
     */
    @NonNull
    public static <K, V> CacheBuilder<K, V> builder (@NonNull String spec, RemovalListener<K, V> listener) {
        if (listener == null) {
            listener = notification -> {
            };
        }
        CacheBuilder<K, V> builder = CacheBuilder.from(spec).concurrencyLevel(Runtime.getRuntime().availableProcessors() << 1).removalListener(listener);
        try {
            Field field = builder.getClass().getDeclaredField("keyEquivalence");
            field.setAccessible(true);
            field.set(builder, Equivalence.equals());
        } catch (Exception e) {
            log.warn("can't rewrite cache keyEquivalence!");
        }
        return builder;
    }
}
