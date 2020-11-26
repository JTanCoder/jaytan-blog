package com.site.blog.util;

import cn.hutool.http.HttpRequest;
import com.google.gson.Gson;
import com.site.blog.dto.IpInfo;
import com.site.blog.dto.IpWeatherResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @author zhanghejie
 */
public class IPInfoUtil {

    private static final Logger log = LoggerFactory.getLogger(IPInfoUtil.class);

    /**
     * Mob官网注册申请即可
     */
    private final static String APPKEY = "2c7d3d9eda41a";
    /**
     * Mob全国天气预报接口
     */
    private final static String GET_WEATHER="http://apicloud.mob.com/v1/weather/ip?key="+ APPKEY +"&ip=";

    /**
     * 获取客户端IP地址
     * @param request 请求
     * @return
     */
    public static String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
            log.info("HTTP_CLIENT_IP ip: " + ip);
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
            log.info("HTTP_X_FORWARDED_FOR ip: " + ip);
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
            log.info("X-Real-IP ip: " + ip);
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
            if (ip.equals("127.0.0.1") || ip.equals("0:0:0:0:0:0:0:1")) {
                //根据网卡取本机配置的IP
                InetAddress inet = null;
                try {
                    inet = InetAddress.getLocalHost();
                } catch (UnknownHostException e) {
                    log.info("本机IP地址获取失败：",e);
                }
                ip = inet.getHostAddress();
            }
        }
        // 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
        if (ip != null && ip.length() > 15) {
            if (ip.indexOf(",") > 0) {
                ip = ip.substring(0, ip.indexOf(","));
            }
        }
        return ip;
    }

    /**
     * 获取本机的外网出口地址
     * @return
     */
    public static String getV4IP() {
        String ip = "";
        String chinaz = "http://ip.chinaz.com";
        StringBuilder inputLine = new StringBuilder();
        String read = "";
        URL url = null;
        HttpURLConnection urlConnection = null;
        BufferedReader in = null;
        try {
            url = new URL(chinaz);
            urlConnection = (HttpURLConnection) url.openConnection();
            in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));
            while ((read = in.readLine()) != null) {
                inputLine.append(read + "\r\n");
            }
        } catch (MalformedURLException e) {
            log.info("获取本机出口IP出错：",e);
        } catch (IOException e) {
            log.info("获取本机出口IP出错：",e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    log.info("获取本机出口IP出错：",e);
                }
            }
        }
        Pattern p = Pattern.compile("\\<dd class\\=\"fz24\">(.*?)\\<\\/dd>");
        Matcher m = p.matcher(inputLine.toString());
        if (m.find()) {
            String ipstr = m.group(1);
            ip = ipstr;
        }
        return ip;
    }
    /**
     * 获取IP返回地理天气信息
     * @param ip ip地址
     * @return
     */
    public static String getIpInfo(String ip){
        if(null != ip){
            String url = GET_WEATHER + ip;
            String result= HttpUtil.sendGet(url);
            return result;
        }
        return ip;
    }

    /**
     * 获取IP返回地理信息
     * @param ip ip地址
     * @return
     */
    public static String getIpCity(String ip){
        if(null != ip){
            String url = GET_WEATHER + ip;
            String json= HttpUtil.sendGet(url);
            String result="未知";
            try{
                IpWeatherResult weather=new Gson().fromJson(json,IpWeatherResult.class);
                result=weather.getResult().get(0).getCity()+" "+weather.getResult().get(0).getDistrct();
            }catch (Exception e){
                e.printStackTrace();
            }
            return result;
        }
        return null;
    }

    public static void getInfo(HttpServletRequest request, String p){
        try {
            IpInfo info = new IpInfo();
            info.setUrl(request.getRequestURL().toString());
            info.setP(p);
            String result = HttpRequest.post("https://api.bmob.cn/1/classes/url")
                    .header("X-Bmob-Application-Id", "46970b236e5feb2d9c843dce2b97f587")
                    .header("X-Bmob-REST-API-Key", "171674600ca49e62e0c7a2eafde7d0a4")
                    .header("Content-Type", "application/json")
                    .body(new Gson().toJson(info, IpInfo.class))
                    .execute().body();
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public static void main(String[] args){
        log.info(getIpInfo("IP测试"));
    }
}
