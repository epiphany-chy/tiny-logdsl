package logdsl.antlr4.utils;


import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 时间处理工具类
 */
@Slf4j
public class DateHandleUtils {


    public static String dateFormatByNginxTime(String nginxTime){
        try{

            nginxTime =  nginxTime.replace("[","").replace("]","").replace("+0800"," +0800");
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MMM/yyyy:hh:mm:ss Z", Locale.ENGLISH);
            Date date = formatter.parse(nginxTime);
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return  format.format(date);
        }catch (Exception e){
          log.error("dateFormatByNginxTime is error",e);
          return nginxTime;
        }
    }

    public static void main(String[] args) {
        System.out.println(dateFormatByNginxTime("[07/Mar/2004:16:05:49 +0800]"));
    }
}
