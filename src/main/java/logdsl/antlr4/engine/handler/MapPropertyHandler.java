package logdsl.antlr4.engine.handler;

import org.mvel2.asm.MethodVisitor;
import org.mvel2.integration.PropertyHandler;
import org.mvel2.integration.VariableResolverFactory;
import org.mvel2.optimizers.impl.asm.ProducesBytecode;

import java.util.Map;

/**
 * @author caohongyu
 * date 2021/3/24
 * Copyright © 2004-2021 chy.com. All Rights Reserved.
 **/
public class MapPropertyHandler implements PropertyHandler, ProducesBytecode {

    @Override
    public Object getProperty(String name, Object contextObj, VariableResolverFactory variableFactory) {
        return contextObj == null ? null : ((Map)contextObj).get(name);
    }

    @Override
    public Object setProperty(String name, Object contextObj, VariableResolverFactory variableFactory, Object value) {
        if (contextObj != null) {
            ((Map)contextObj).put(name, value);
        }
        return value;
    }

    @Override
    public void produceBytecodeGet(MethodVisitor mv, String propertyName, VariableResolverFactory factory) {

    }

    @Override
    public void produceBytecodePut(MethodVisitor mv, String propertyName, VariableResolverFactory factory) {

    }

}
