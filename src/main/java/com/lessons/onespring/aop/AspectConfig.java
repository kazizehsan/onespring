package com.lessons.onespring.aop;

import com.lessons.onespring.entities.User;
import com.lessons.onespring.utils.HttpUtils;
import com.lessons.onespring.services.intf.AuditLogService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Aspect
@Component
public class AspectConfig {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private AuditLogService auditLogService;

    @Autowired
    private AspectHandlerUserEvents aspectHandlerUserEvents;

//    @AfterThrowing(pointcut="@annotation(AspectEventCatcher)", throwing="ex")
//    public void doAfterThrowing(JoinPoint joinPoint, Exception ex) throws JSONException {
//    }

    @AfterReturning(
            pointcut="@annotation(com.lessons.onespring.aop.AspectEventCatcher) && " +
                    "!(@annotation(org.springframework.web.bind.annotation.PutMapping) ||" +
                    "@annotation(org.springframework.web.bind.annotation.DeleteMapping))",
            returning="result")
    public void auditAdviceForSuccessfulPostsGets(JoinPoint joinPoint, Object result) throws Throwable {
        this.auditSuccessfulPostsGets(joinPoint, result);
    }

    @Around(
            "@annotation(com.lessons.onespring.aop.AspectEventCatcher) && " +
            "(@annotation(org.springframework.web.bind.annotation.PutMapping) || " +
                    "@annotation(org.springframework.web.bind.annotation.DeleteMapping))"
    )
    public Object auditAdviceForSuccessfulUpdatesDeletes(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        return this.auditSuccessfulUpdatesDeletes(proceedingJoinPoint);
    }

    private Object auditSuccessfulUpdatesDeletes(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Object result = null;
        MethodSignature signature = (MethodSignature) proceedingJoinPoint.getSignature();
        AspectEventCatcher aspectEventCatcher = signature.getMethod().getAnnotation(AspectEventCatcher.class);
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
                .currentRequestAttributes())
                .getRequest();

        switch (aspectEventCatcher.eventType()) {
            case USER_UPDATE:
                result = aspectHandlerUserEvents.userUpdateHandler(proceedingJoinPoint, request);
                break;
            case USER_DELETE:
                result = aspectHandlerUserEvents.userDeleteHandler(proceedingJoinPoint, request);
                break;
            default:
        }

        return result;
    }

    private void auditSuccessfulPostsGets(JoinPoint joinPoint, Object result) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        AspectEventCatcher aspectEventCatcher = signature.getMethod().getAnnotation(AspectEventCatcher.class);
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
                .currentRequestAttributes())
                .getRequest();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AspectEventDetails aspectEventDetails = new AspectEventDetails();
        aspectEventDetails.setPath(request.getRequestURI());

        switch (aspectEventCatcher.eventType()) {
            case LOGIN:
                aspectHandlerUserEvents.userLoginHandler(joinPoint, request, result);
                break;
            case LOGOUT:
                aspectEventDetails.setResource_id(((User) authentication.getPrincipal()).getId().toString());
                aspectEventDetails.setResource_name(User.class.getSimpleName());
                auditLogService.save(true, HttpUtils.getClientIp(request), AspectEventType.LOGOUT.toString(), aspectEventDetails.getJson());
                break;
            case CHANGE_PASSWORD:
                aspectEventDetails.setResource_id(((User) authentication.getPrincipal()).getId().toString());
                aspectEventDetails.setResource_name(User.class.getSimpleName());
                auditLogService.save(true, HttpUtils.getClientIp(request), AspectEventType.CHANGE_PASSWORD.toString(), aspectEventDetails.getJson());
                break;
            case USER_CREATE:
                aspectHandlerUserEvents.userCreateHandler(request, result);
                break;
            default:
        }
    }
}
