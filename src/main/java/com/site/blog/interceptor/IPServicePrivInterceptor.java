package com.site.blog.interceptor;

import com.site.blog.annotation.IPServicePriv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * IP 接口 白名单校验
 */
@Component
public class IPServicePrivInterceptor extends HandlerInterceptorAdapter {
    private static final Logger logger = LoggerFactory.getLogger(IPServicePrivInterceptor.class);
    @Autowired
    private IpFilterService ipService ;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String isCheckIPPriv = DictConstants.getDictValueByCode("biz.para.check.ip.filter","0");
        if("0".equals(isCheckIPPriv)){
            //不开启校验IP白名单，1开启
            return true ;
        }
        IPServicePriv annotation;
        if(handler instanceof HandlerMethod) {
            annotation = ((HandlerMethod) handler).getMethodAnnotation(IPServicePriv.class);
        }else{
            return true;
        }
        if(annotation == null){
            return true;
        }
        String clientIp = IPInfoUtil.getIpAddr(request);
        if(clientIp == null){
            return true ;
        }
        String requestMethod = request.getRequestURI() ;
        List<IpFilterEntity> ifcs = ipService.findListByPropertyValues("ip",new Object[] {clientIp}) ;
        if(ifcs != null && ifcs.size() > 0){
            boolean isHasPriv = false ;
            for (String ifc : ifcs.get(0).getIfcs().split("\\,")){
                if(requestMethod.contains(ifc)){
                    //多个接口中有一个包含于当前的请求接口路径则有
                    isHasPriv = true ;
                }
            }
            if(isHasPriv){
                return true;
            }else{
                throw new ValidationException("你没有权限访问该接口[网络受限]");
            }
        }else{
            throw new ValidationException("你没有权限访问该接口[网络受限]");
        }
    }

    /**
     * 为response设置header，实现跨域
     */
    private void setHeader(HttpServletRequest request, HttpServletResponse response){
        //跨域的header设置
        response.setHeader("Access-control-Allow-Origin", request.getHeader("Origin"));
        response.setHeader("Access-Control-Allow-Methods", request.getMethod());
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Headers", request.getHeader("Access-Control-Request-Headers"));
        //防止乱码，适用于传输JSON数据
        response.setHeader("Content-Type","application/json;charset=UTF-8");
    }
}
