package com.roncoo.pay.controller.exception;
import javax.servlet.http.HttpServletRequest;
import com.roncoo.pay.common.core.dwz.DWZ;
import org.apache.commons.logging.Log;
import com.roncoo.pay.common.core.dwz.DwzAjax;
import org.apache.commons.logging.LogFactory;
import com.roncoo.pay.common.core.exception.BizException;
import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Spring异常拦截器.
 * 龙果学院：www.roncoo.com
 * @author zenghao
 */
@ControllerAdvice public class WebExceptionHandler {
  private static final Log LOG = LogFactory.getLog(WebExceptionHandler.class);

  /**
	 * shiro权限 异常
	 * <p/>
	 * 后续根据不同的需求定制即可
	 */
  @ExceptionHandler(value = { UnauthorizedException.class }) @ResponseStatus(value = HttpStatus.OK) public String processUnauthorizedException(HttpServletRequest request, UnauthorizedException e) {
    LOG.error("UnauthorizedException", e);
    DwzAjax dwz = new DwzAjax();
    dwz.setStatusCode(DWZ.ERROR);
    dwz.setMessage("\u4f60\u6ca1\u6709\u64cd\u4f5c\u6743\u9650");
    request.setAttribute("dwz", dwz);
    return "common/ajaxDone";
  }

  /**
	 * 业务异常
	 * <p/>
	 * 后续根据不同的需求定制即可
	 */
  @ExceptionHandler(value = { BizException.class }) @ResponseStatus(value = HttpStatus.OK) public String processBizException(HttpServletRequest request, BizException e) {
    LOG.error("BizException", e);
    DwzAjax dwz = new DwzAjax();
    dwz.setStatusCode(DWZ.ERROR);
    dwz.setMessage(e.getMsg());
    request.setAttribute("dwz", dwz);
    return "common/ajaxDone";
  }

  /**
	 * 总异常
	 */
  @ExceptionHandler(value = { Exception.class }) @ResponseStatus(value = HttpStatus.OK) public String processException(Exception e, HttpServletRequest request) {
    LOG.error("Exception", e);
    DwzAjax dwz = new DwzAjax();
    dwz.setStatusCode(DWZ.ERROR);
    dwz.setMessage("\u7cfb\u7edf\u5f02\u5e38");
    request.setAttribute("dwz", dwz);
    return "common/ajaxDone";
  }
}