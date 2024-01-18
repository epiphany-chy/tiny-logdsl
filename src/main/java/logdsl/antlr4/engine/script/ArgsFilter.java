package logdsl.antlr4.engine.script;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import logdsl.antlr4.engine.support.JsonUtil;
import java.util.*;
import org.springframework.util.ObjectUtils;

/**
 * filter the key from the value with loop.
 * @author caohongyu
 * date 2021/5/17
 * Copyright © 2004-2021 chy.com. All Rights Reserved.
 **/
public interface ArgsFilter {

    Logger log = LoggerFactory.getLogger(ArgsFilter.class);

    String CLASS = "class";

    /**
     * trim the key loop.
     * @param value
     * @param key
     * @return
     */
    static Object filterLoop(Object value, String key) {
        if (value == null) {
            return null;
        }
        Object target;
        if (value instanceof String) {
            target = JsonUtil.fromJson((String) value, Object.class);
        } else {
            //resolve the path include Object properties
            target = JsonUtil.fromJson(JsonUtil.toJson(value), Object.class);
        }
        boolean result = false;
        try {
            result = removeKey(target, key);
        } catch (Exception err) {
            log.warn("args filter exception! filed: {}", key, err);
        }
        return result ? target : value;
    }

    /**
     * remove key from map
     * @param value
     * @param key
     * @return boolean
     */
    static boolean removeMapKey(Map value, String key) {
        if (value == null) {
            return false;
        }
        Iterator<Map.Entry> iterator = value.entrySet().iterator();
        Set<Boolean> resultSet = new HashSet<>(2);

        while (iterator.hasNext()) {
            Map.Entry entry = iterator.next();
            //if equals the target key, remove it.
            if (Objects.equals(entry.getKey(), key) && entry.getValue() instanceof String) {
                iterator.remove();
                resultSet.add(true);
            }
            resultSet.add(removeKey(entry.getValue(), key));
        }
        return resultSet.contains(Boolean.TRUE);
    }

    /**
     * remove key from collection.
     * @param value
     * @param key
     * @return boolean
     */
    static boolean removeListMapKey(List value, String key) {
        if (value == null) {
            return false;
        }
        Set<Boolean> resultSet = new HashSet<>(2);
        value.forEach(e -> resultSet.add(removeKey(e, key)));
        return resultSet.contains(Boolean.TRUE);
    }

    /**
     * remove key from value
     * @param value
     * @param key
     * @return boolean.
     */
    static boolean removeKey(Object value, String key) {
        if (ObjectUtils.isEmpty(value)) {
            return false;
        }
        if (value instanceof Map) {
            return removeMapKey((Map) value, key);
        }
        if (value.getClass().isArray()) {
            return removeListMapKey(Arrays.asList(value), key);
        }
        if (value instanceof Collection) {
            return removeListMapKey(new ArrayList((Collection) value), key);
        }
        return false;
    }

}
