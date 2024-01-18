package logdsl.antlr4.engine.script;

import logdsl.antlr4.engine.support.JsonUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import logdsl.antlr4.engine.mvel2.math.MapProcessor;
import org.codehaus.plexus.util.StringUtils;

import java.net.URL;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;


/**
 * the static method provider for the express script use.
 *
 * @author caohongyu
 * @date 2021/3/10
 * Copyright © 2004-2021 chy.com. All Rights Reserved.
 **/
@Slf4j
public class StaticScript {



    /**
     * 日志分组函数
     * 类似于sql的group by 语句，对日志进行分组，常见对referer、host、ip等进行分组查询
     * @param original  日志格式化后的list
     * @param key     分组的key
     * @return  分组后的map
     */

    public static  Map<String, List<Map<String,String>>> $groupByKeys(List<Map<String,String>> original, String key) {


        Map<String, List<Map<String,String>>> res =   original.stream().collect(Collectors.groupingBy(map->map.get(key)));


        return res;
    }


    /**
     * 求平均值，最大值，最小值，和
     * 对耗时进行聚合分析
     * @param original  日志格式化后的list
     * @param key     int型的字段
     * @return  分组后的map
     */

    public static  Map<String, Object> $timeAnalysis(List<Map<String,String>> original, String key) {

        Map<String, Object> remap = new HashMap<>();
        DoubleSummaryStatistics intSummaryStatistics =   original.stream().collect( Collectors.summarizingDouble(map-> Double.parseDouble(map.get(key))));
        remap.put("average",intSummaryStatistics.getAverage());
        remap.put("max",intSummaryStatistics.getMax());
        remap.put("min",intSummaryStatistics.getMin());
        remap.put("sum",intSummaryStatistics.getSum());
        remap.put("count",intSummaryStatistics.getCount());
        return remap;
    }

    /**
     * 包装map函数
     * 对耗时进行聚合分析
     * @param original  日志格式化后的list
     * @param index  序号
     * @param key    包装map 的key
     * @return  分组后的map
     */

    public static  Map<String, Map<String,String>> $resultCovert(List<Map<String,String>> original, int index, String key) {

        Map<String,String> res =   original.get(index);
        Map<String,Map<String,String>> covermap = new HashMap<>();
        covermap.put(key,res);
        return covermap;
    }

    /**
     * 常用查询语句的函数集合
     * 对个别字段进行但条件查询。
     * @param original  日志格式化后的list
     * @param key     int型的字段
     * @return  分组后的map
     */

    public static   List<Map<String,String>> $selectByKey(List<Map<String,String>> original, String key,String value,String type) {
        List<Map<String,String>> result = new ArrayList<>();
        switch(type){
            case "=":
                result = original.stream().filter(v -> v.get(key).equals(value)).collect(Collectors.toList());
                break;
            case "like":
                result = original.stream().filter(v -> v.get(key).contains(value)).collect(Collectors.toList());
                break;
            case "<":
                result = original.stream().filter(v -> Double.parseDouble(v.get(key))< Double.parseDouble(value)).collect(Collectors.toList());
                break;
            case ">":
                result = original.stream().filter(v -> Double.parseDouble(v.get(key))> Double.parseDouble(value)).collect(Collectors.toList());
                break;
            case ">=":
                result = original.stream().filter(v -> Double.parseDouble(v.get(key)) >= Double.parseDouble(value)).collect(Collectors.toList());
                break;
            case "<=":
                result = original.stream().filter(v -> Double.parseDouble(v.get(key)) <= Double.parseDouble(value)).collect(Collectors.toList());
                break;
            default :
              log.error("暂不支持{}的查询类型",type);
                break;
        }

        return result;
    }



    /**
     * 向original中填充target
     * target的key支持表达多层级，向original填充时按层级进行填充；如果original不存在子节点，则创建并填充
     * original里子节点为json时，也支持向json中的节点进行填充
     * .e.g.
     * original = ['a': ['a1': 'va1'], 'b': 'vb0']
     * target = ['c': 'vc0', 'a.a2': 'va2']
     * append(original, target)
     * return：['a': ['a1': 'va1', 'a2': 'va2'], 'b': 'vb0', 'c': 'vc0']
     *
     * @param original the original map.
     * @param target   the target map.
     * @return result map.
     */
    public static Map $append(Map original, Map target) {
        return MapProcessor.append(original, target);
    }

    /**
     * 从original中裁剪掉指定的key
     * target中的元素支持多层级，裁剪时按层级进行索引指定位置并移除
     * .e.g.
     * original = ['a': ['a1': 'va1', 'a2': 'va2'], 'b': 'vb0', 'c': 'vc0']
     * target = ['c', 'a.a2']
     * exclude(original, target)
     * return：['a': ['a1': 'va1'], 'b': 'vb0']
     *
     * @param original the original map.
     * @param target   the path key collection.
     * @return result map.
     */
    public static Map $exclude(Map original, Collection target) {
        return MapProcessor.exclude(original, target);
    }

    /**
     * 从value中过滤所有指定的key
     * value中的元素支持多层级，过滤时按层级进行搜索指定的key并移除
     * .e.g.
     * value = ['a': ['a1': 'va1', 'a2': 'va2'], 'b': 'vb0', 'a2': 'vc0']
     * key = 'a2'
     * filterLoop(value, key)
     * return：['a': ['a1': 'va1'], 'b': 'vb0']
     * @param value the value object.
     * @param key the key.
     * @return result object.
     */
    public static Object $trim (Object value, String key) {
        return ArgsFilter.filterLoop(value, key);
    }

    /**
     * encode the original String by URLEncoder with charset.
     *
     * @param original the original String
     * @param charset  the charset String
     * @return the encoded String
     */
    @SneakyThrows
    public static String $urlEncode(String original, String charset) {
        return URLEncoder.encode(original, charset);
    }

    /**
     * object -> json.
     *
     * @param entity the original object.
     * @param <E>    the type of original.
     * @return json.
     */
    public static <E> String $toJson(E entity) {
        return JsonUtil.toJson(entity);
    }

    /**
     * json -> object
     *
     * @param json the original json.
     * @param cls  the target class type.
     * @param <E>  the type of target.
     * @return target object.
     */
    public static <E> E $fromJson(String json, Class<E> cls) {
        return JsonUtil.fromJson(json, cls);
    }

    /**
     * referer -> domain
     *
     * @param referer the original referer.
     * @return target domain.
     */
    public static String $getDomain(String referer) {
        try {

            return StringUtils.isEmpty(referer) ? null : new URL(referer).getHost();
        } catch (Exception e) {
            return null;
        }
    }


    public static Map $fromJsonStr(String json) {
        return JsonUtil.fromJson(json, Map.class);
    }

    public static int $random100(){
      int random = (int)(Math.random() * 100);
      return random;
    }

    public static int $random(int mod){
        if(mod <= 0){
            return 0;
        }
        int random = (int)(Math.random() * mod);
        return random;
    }

    public static void main(String[] args) {
        String aa ="0.32";
        System.out.println(Double.valueOf(aa));
    }


}
