package com.github.sd4324530.fastweixin.servlet;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 微信公众平台交互操作基类，提供几乎所有微信公众平台交互方式
 * 基于springmvc框架，方便使用此框架的项目集成
 *
 * @author peiyu
 */
@Controller public abstract class WeixinControllerSupport extends WeixinSupport {
  /**
     * 绑定微信服务器
     *
     * @param request 请求
     * @return 响应内容
     */
  @RequestMapping(method = RequestMethod.GET) @ResponseBody protected final String bind(HttpServletRequest request) {
    if (isLegal(request)) {
      return request.getParameter("echostr");
    } else {
      return "";
    }
  }

  /**
     * 微信消息交互处理
     *
     * @param request http 请求对象
     * @return 响应给微信服务器的消息报文
     * @throws ServletException 异常
     * @throws IOException      IO异常
     */
  @RequestMapping(method = RequestMethod.POST) protected final void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

<<<<<<< /usr/src/app/output/sd4324530/fastweixin/b9c911aea2bd0710b54afc94fb09c55e813ed9b6/src/main/java/com/github/sd4324530/fastweixin/servlet/WeixinControllerSupport.java/left.java
    String result = processRequest(request);
=======
    if (isLegal(request)) {
      String result = processRequest(request);
      response.setContentType("text/xml;charset=UTF-8");
      PrintWriter writer = response.getWriter();
      writer.write(result);
      writer.close();
    }
>>>>>>> /usr/src/app/output/sd4324530/fastweixin/b9c911aea2bd0710b54afc94fb09c55e813ed9b6/src/main/java/com/github/sd4324530/fastweixin/servlet/WeixinControllerSupport.java/right.java

    response.setContentType("text/xml;charset=UTF-8");
    response.getWriter().write(result);

<<<<<<< /usr/src/app/output/sd4324530/fastweixin/b9c911aea2bd0710b54afc94fb09c55e813ed9b6/src/main/java/com/github/sd4324530/fastweixin/servlet/WeixinControllerSupport.java/left.java
    return null;
=======
>>>>>>> Unknown file: This is a bug in JDime.
  }
}