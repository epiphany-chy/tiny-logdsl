/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package logdsl.antlr4.engine.support;


import com.google.common.collect.Sets;

import java.security.AccessControlException;
import java.util.HashMap;
import java.util.Optional;
import java.util.Set;

/**
 * the Security ScriptClassLoader use black list and white list to load the safe class.
 * @author caohongyu
 * @date 2021/2/23
 * Copyright © 2004-2021 chy.com. All Rights Reserved.
 **/
public class SecurityScriptClassLoader extends FilterClassLoader {

    private final Set<String> FORBIDDEN_CLASSES = Sets.newCopyOnWriteArraySet();

    private final HashMap<String, Class<?>> ALLOW_CLASSES = new HashMap<>();

    public SecurityScriptClassLoader(ClassLoader classLoader) {
        super(classLoader);
    }

    @Override
    public Class<?> defineClassX(String className, byte[] b, int start, int end) {
        return getIfAllow(className).orElse(super.defineClassX(className, b, start, end));
    }

    @Override
    public Class<?> loadClass(String name) throws AccessControlException, ClassNotFoundException {
        return getIfAllow(name).orElse(super.loadClass(name));
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        return getIfAllow(name).orElse(super.loadClass(name, resolve));
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        return getIfAllow(name).orElse(super.findClass(name));
    }

    @Override
    public Optional<Class<?>> getIfAllow(String name) throws AccessControlException {
        if (FORBIDDEN_CLASSES.contains(name)) {
            throw new AccessControlException("class: " + name + " forbidden to access!");
        }
        return Optional.ofNullable(ALLOW_CLASSES.get(name));
    }

    @Override
    public synchronized void registerForbiddenClass(Set<Class<?>> forbiddenClass) {
        for (Class<?> cls : forbiddenClass) {
            FORBIDDEN_CLASSES.add(cls.getName());
            FORBIDDEN_CLASSES.add(cls.getSimpleName());
            ALLOW_CLASSES.remove(cls.getName());
            ALLOW_CLASSES.remove(cls.getSimpleName());
        }
    }

    @Override
    public synchronized void registerAllowClass(Set<Class<?>> allowClass) {
        for (Class<?> cls : allowClass) {
            FORBIDDEN_CLASSES.remove(cls.getName());
            FORBIDDEN_CLASSES.remove(cls.getSimpleName());
            ALLOW_CLASSES.put(cls.getName(), cls);
            ALLOW_CLASSES.put(cls.getSimpleName(), cls);
        }
    }

}
