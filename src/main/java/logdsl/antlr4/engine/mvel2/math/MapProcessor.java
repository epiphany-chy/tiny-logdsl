package logdsl.antlr4.engine.mvel2.math;

import logdsl.antlr4.engine.support.JsonUtil;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author caohongyu
 * @date 2021/3/9
 * Copyright © 2004-2021 chy.com. All Rights Reserved.
 **/
public class MapProcessor {

    /**
     * merge 2 map, support put target into original by the path key.
     * if the path not exists in the original, it will create before put.
     * when the sub path value is json, it will be convert to map then continue.
     * @param original the original map []
     * @param target the target map ['a.b.c': 'value_1']
     * @return map ['a': ['b': ['c': 'value_1']]]
     */
    public static Map append (Map original, Map target) {
        if (original == null) {
            original = new HashMap();
        }
        Map<String, Object> reference = new HashMap(original);

        if (target == null || target.isEmpty()) {
            return reference;
        }
        target.forEach((path, value) -> {
            String [] paths = StringUtils.split((String)path, ".");
            int size = paths.length;
            Map<String, Object> arg = reference;
            for (int i = 0; i < size; ++i) {
                //当前层级名称
                String argName = paths[i];
                if (i == paths.length - 1) {
                    //最内层
                    arg.put(argName, value);
                } else {
                    //未到最内层
                    Object node = arg.computeIfAbsent(argName, k -> new HashMap());
                    if (node instanceof Map) {
                        arg = (Map<String, Object>) node;
                    } else if (node instanceof String) {
                        arg = JsonUtil.fromJson((String) node, Map.class);
                    }
                }
            }
        });

        return reference;
    }

    /**
     * remove the collection key form original map.
     * support the key with more path.
     * @param original the original map.
     * @param target the path key collection.
     * @return
     */
    public static Map exclude (Map original, Collection target) {
        if (original == null) {
            original = new HashMap();
        }
        Map<String, Object> reference = new HashMap(original);

        if (target == null || target.isEmpty()) {
            return reference;
        }
        target.forEach(path -> {
            String [] paths = StringUtils.split((String)path, ".");
            int size = paths.length;
            Map<String, Object> arg = reference;
            for (int i = 0; i < size; ++i) {
                //当前层级名称
                String argName = paths[i];
                if (i == paths.length - 1) {
                    //最内层
                    arg.remove(argName);
                } else {
                    //未到最内层
                    Object node = arg.get(argName);
                    if (node == null) {
                        break;
                    } else if (node instanceof String) {
                        arg = JsonUtil.fromJson((String) node, Map.class);
                    }
                }
            }
        });

        return reference;
    }

}
