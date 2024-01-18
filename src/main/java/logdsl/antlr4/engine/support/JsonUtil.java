package logdsl.antlr4.engine.support;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.plexus.util.StringUtils;
import org.springframework.util.ObjectUtils;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.Callable;

/**
 * @author caohongyu
 *  2019/2/1: 19:43

 **/
@Slf4j
@UtilityClass
public class JsonUtil {

    private static final ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
        mapper.setDefaultPropertyInclusion(JsonInclude.Include.NON_NULL);
        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.disable(SerializationFeature.FAIL_ON_UNWRAPPED_TYPE_IDENTIFIERS);
        mapper.registerModule(new SimpleModule());
        mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        mapper.configure(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT, false);
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS"));
        mapper.setTimeZone(TimeZone.getDefault());
        mapper.registerModule(new Jdk8Module());
        mapper.registerModule(new JavaTimeModule());

        mapper.configure(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS, true);
        mapper.configure(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN, true);
        mapper.setNodeFactory(JsonNodeFactory.withExactBigDecimals(true));
    }

    public static ObjectMapper mapper () {
        return mapper.copy();
    }

    public static <T> byte [] to (T entity) {
        return wrapper(entity, () -> entity instanceof byte[] ? (byte[]) entity : mapper.writeValueAsBytes(entity));
    }

    public static <T> T from (byte [] value, Class<T> clazz) {
        return wrapper(value, () -> mapper.readValue(value, clazz));
    }

    public static <T> T from (byte [] value, TypeReference<T> type) {
        return wrapper(value, () -> mapper.readValue(value, type));
    }

    public static <T> String toJson (T entity) {
        return wrapper(entity, () -> entity instanceof String ? (String) entity : mapper.writeValueAsString(entity));
    }

    public static <T> T fromJson (String json, Class<T> clazz) {
        if(StringUtils.isNotBlank(json) && Character.isLetterOrDigit(json.charAt(0)) && clazz == Object.class){
            json = "\"" + json + "\"";
        }
        String finalJson = json;
        return wrapper(json, () -> mapper.readValue(finalJson, clazz));
    }

    public static <T> T fromJson (String json, TypeReference<T> typeReference) {
        //非json转换成map
        if(StringUtils.isNotBlank(json) && Character.isLetterOrDigit(json.charAt(0)) && typeReference.getType().getTypeName().contains("Map")){
            return null;
        }

        if(StringUtils.isNotBlank(json) && Character.isLetterOrDigit(json.charAt(0)) && typeReference.getType().getTypeName().equals("java.lang.Object")){
            json = "\"" + json + "\"";
        }
        String finalJson = json;
        return wrapper(json, () -> mapper.readValue(finalJson, typeReference));
    }

    public static <T> T fromJson (String json, JavaType javaType) {
        return wrapper(json, () -> mapper.readValue(json, javaType));
    }

    private static <T, R> R wrapper (T value, Callable<R> callable) {
        try {
            return ObjectUtils.isEmpty(value) ? null : callable.call();
        } catch (Exception e) {
            return null;
        }
    }

    public static void main(String[] args) {
        try {
            String example ="1,2";
            Object o =  fromJson(example, List.class);
//            Map<String,Object> map = fromJson(example, new TypeReference<Map>(){});
//            System.out.println("s1:"+s1);
//            System.out.println("result:"+result);
//            String  str = "罗长";
//            byte[] sb = str.getBytes();
//            String result = new String(sb);
            System.out.println("o:"+o);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
