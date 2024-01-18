package logdsl.antlr4.engine.support;

import org.mvel2.util.JITClassLoader;

import java.security.AccessControlException;
import java.util.Optional;
import java.util.Set;

/**
 * filter ClassLoader.
 * avoid to load the unsafe class.
 * @author caohongyu
 * @date 2021/3/11
 * Copyright © 2004-2021 chy.com. All Rights Reserved.
 **/
public abstract class FilterClassLoader extends JITClassLoader {

    public FilterClassLoader(ClassLoader classLoader) {
        super(classLoader);
    }

    /**
     * get class if allow.
     * when forbidden, it will throw AccessControlException
     * @param name
     * @return
     * @throws AccessControlException
     */
    public abstract Optional<Class<?>> getIfAllow (String name) throws AccessControlException;

    /**
     * register forbidden class
     * @param forbiddenClass
     */
    public abstract void registerForbiddenClass (Set<Class<?>> forbiddenClass);

    /**
     * register allowed class.
     * @param allowClass
     */
    public abstract void registerAllowClass (Set<Class<?>> allowClass);
}
