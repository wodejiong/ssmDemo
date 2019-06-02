package demo.wj.controller;

import demo.wj.domain.SysLog;
import demo.wj.service.SysLogService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Date;

@Component
@Aspect
public class LogAop {
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private SysLogService sysLogService;

    private Date visitTime;
    private Class clazz;
    private Method method;

    @Before("execution(* demo.wj.controller.*.*(..))")
    public void doBefore(JoinPoint jp) throws Exception {
        visitTime = new Date();
        clazz = jp.getTarget().getClass();//目标对象类

        String methodName = jp.getSignature().getName();//执行的对象名
        Object[] args = jp.getArgs();
        if (args == null && args.length == 0) {
            method = clazz.getMethod(methodName);
        } else {
            Class[] classes = new Class[args.length];
            for (int i = 0; i < args.length; i++) {
                classes[i] = args[i].getClass();
            }
            method = clazz.getMethod(methodName, classes);
        }

    }

    @After("execution(* demo.wj.controller.*.*(..))")
    public void doAfter(JoinPoint jp) throws Exception {
        SysLog sysLog=new SysLog();
        sysLog.setVisitTime(visitTime);
        Long excutionTime = new Date().getTime() - visitTime.getTime();
        sysLog.setExecutionTime(excutionTime);

        String url = null;
        if (clazz != null) {
            RequestMapping clazzAnnotation= (RequestMapping) clazz.getAnnotation(RequestMapping.class);
            if (method != null) {
                RequestMapping methodAnnotation = method.getAnnotation(RequestMapping.class);
                String[] clazzValue = clazzAnnotation.value();
                String[] methodValue = methodAnnotation.value();
                url =  clazzValue[0]  + methodValue[0];
                sysLog.setUrl(url);
                String ip = request.getRemoteAddr();
                sysLog.setIp(ip);

                SecurityContext context = SecurityContextHolder.getContext();
                User user = (User) context.getAuthentication().getPrincipal();
                String username = user.getUsername();
                sysLog.setUsername(username);

                sysLog.setMethod("[类名]" + clazz.getName() + "[方法名]" + method.getName());

                sysLogService.save(sysLog);
            }


        }

    }
}
