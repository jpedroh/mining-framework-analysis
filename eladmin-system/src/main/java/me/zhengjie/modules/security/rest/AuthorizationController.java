package me.zhengjie.modules.security.rest;
import cn.hutool.core.util.IdUtil;
import com.wf.captcha.base.Captcha;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.annotation.Log;
import me.zhengjie.annotation.rest.AnonymousDeleteMapping;
import me.zhengjie.annotation.rest.AnonymousGetMapping;
import me.zhengjie.annotation.rest.AnonymousPostMapping;
import me.zhengjie.config.RsaProperties;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.modules.security.config.bean.LoginCodeEnum;
import me.zhengjie.modules.security.config.bean.LoginProperties;
import me.zhengjie.modules.security.config.bean.SecurityProperties;
import me.zhengjie.modules.security.security.TokenProvider;
import me.zhengjie.modules.security.service.dto.AuthUserDto;
import me.zhengjie.modules.security.service.dto.JwtUserDto;
import me.zhengjie.modules.security.service.OnlineUserService;
import me.zhengjie.utils.RsaUtils;
import me.zhengjie.utils.RedisUtils;
import me.zhengjie.utils.SecurityUtils;
import me.zhengjie.utils.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author Zheng Jie
 * @date 2018-11-23
 * 授权、根据token获取用户详细信息
 */
@Slf4j @RestController @RequestMapping(value = "/auth") @RequiredArgsConstructor @Api(tags = "\u7cfb\u7edf\uff1a\u7cfb\u7edf\u6388\u6743\u63a5\u53e3") public class AuthorizationController {
  private final SecurityProperties properties;

  private final RedisUtils redisUtils;

  private final OnlineUserService onlineUserService;

  private final TokenProvider tokenProvider;

  private final AuthenticationManagerBuilder authenticationManagerBuilder;

  @Resource private LoginProperties loginProperties;

  @Log(value = "\u7528\u6237\u767b\u5f55") @ApiOperation(value = "\u767b\u5f55\u6388\u6743") @AnonymousPostMapping(value = "/login") public ResponseEntity<Object> login(@Validated @RequestBody AuthUserDto authUser, HttpServletRequest request) throws Exception {
    String password = RsaUtils.decryptByPrivateKey(RsaProperties.privateKey, authUser.getPassword());
    String code = (String) redisUtils.get(authUser.getUuid());
    redisUtils.del(authUser.getUuid());
    if (StringUtils.isBlank(code)) {
      log.error("\u9a8c\u8bc1\u7801\u4e0d\u5b58\u5728\u6216\u5df2\u8fc7\u671f");
      Map<String, Object> errorData = new HashMap<>(2);
      errorData.put("message", "\u9a8c\u8bc1\u7801\u4e0d\u5b58\u5728\u6216\u5df2\u8fc7\u671f");
      errorData.put("status", 400);
      return new ResponseEntity<>(errorData, HttpStatus.BAD_REQUEST);
    }
    if (StringUtils.isBlank(authUser.getCode()) || !authUser.getCode().equalsIgnoreCase(code)) {
      log.error("\u9a8c\u8bc1\u7801\u9519\u8bef");
      Map<String, Object> errorData = new HashMap<>(2);
      errorData.put("message", "\u9a8c\u8bc1\u7801\u9519\u8bef");
      errorData.put("status", 400);
      return new ResponseEntity<>(errorData, HttpStatus.BAD_REQUEST);
    }
    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(authUser.getUsername(), password);
    Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
    SecurityContextHolder.getContext().setAuthentication(authentication);
    String token = tokenProvider.createToken(authentication);
    final JwtUserDto jwtUserDto = (JwtUserDto) authentication.getPrincipal();
    onlineUserService.save(jwtUserDto, token, request);
    Map<String, Object> authInfo = new HashMap<String, Object>(2) {
      {
        put("token", properties.getTokenStartWith() + token);
        put("user", jwtUserDto);
      }
    };
    if (loginProperties.isSingleLogin()) {
      onlineUserService.checkLoginOnUser(authUser.getUsername(), token);
    }
    return ResponseEntity.ok(authInfo);
  }

  @ApiOperation(value = "\u83b7\u53d6\u7528\u6237\u4fe1\u606f") @GetMapping(value = "/info") public ResponseEntity<Object> getUserInfo() {
    return ResponseEntity.ok(SecurityUtils.getCurrentUser());
  }

  @ApiOperation(value = "\u83b7\u53d6\u9a8c\u8bc1\u7801") @AnonymousGetMapping(value = "/code") public ResponseEntity<Object> getCode() {
    Captcha captcha = loginProperties.getCaptcha();
    String uuid = properties.getCodeKey() + IdUtil.simpleUUID();
    String captchaValue = captcha.text();
    if (captcha.getCharType() - 1 == LoginCodeEnum.ARITHMETIC.ordinal() && captchaValue.contains(".")) {
      captchaValue = captchaValue.split("\\.")[0];
    }
    redisUtils.set(uuid, captchaValue, loginProperties.getLoginCode().getExpiration(), TimeUnit.MINUTES);
    Map<String, Object> imgResult = new HashMap<String, Object>(2) {
      {
        put("img", captcha.toBase64());
        put("uuid", uuid);
      }
    };
    return ResponseEntity.ok(imgResult);
  }

  @ApiOperation(value = "\u9000\u51fa\u767b\u5f55") @AnonymousDeleteMapping(value = "/logout") public ResponseEntity<Object> logout(HttpServletRequest request) {
    onlineUserService.logout(tokenProvider.getToken(request));
    return new ResponseEntity<>(HttpStatus.OK);
  }
}