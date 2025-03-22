package me.zhengjie.modules.security.rest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.modules.security.service.OnlineUserService;
import me.zhengjie.modules.security.service.dto.OnlineUserDto;
import me.zhengjie.utils.PageResult;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;

/**
 * @author Zheng Jie
 */
@RestController @RequiredArgsConstructor @RequestMapping(value = "/auth/online") @Api(tags = "\u7cfb\u7edf\uff1a\u5728\u7ebf\u7528\u6237\u7ba1\u7406") public class OnlineController {
  private final OnlineUserService onlineUserService;

  @ApiOperation(value = "\u67e5\u8be2\u5728\u7ebf\u7528\u6237") @GetMapping @PreAuthorize(value = "@el.check()") public ResponseEntity<PageResult<OnlineUserDto>> queryOnlineUser(String username, Pageable pageable) {
    return new ResponseEntity<>(onlineUserService.getAll(username, pageable), HttpStatus.OK);
  }

  @ApiOperation(value = "\u5bfc\u51fa\u6570\u636e") @GetMapping(value = "/download") @PreAuthorize(value = "@el.check()") public void exportOnlineUser(HttpServletResponse response, String username) throws IOException {
    onlineUserService.download(onlineUserService.getAll(username), response);
  }

  @ApiOperation(value = "\u8e22\u51fa\u7528\u6237") @DeleteMapping @PreAuthorize(value = "@el.check()") public ResponseEntity<Object> deleteOnlineUser(@RequestBody Set<String> keys) throws Exception {

<<<<<<< /usr/src/app/output/elunez/eladmin/ba16a830ace07bc4f5cd27e3c288f7e1d2dce89d/eladmin-system/src/main/java/me/zhengjie/modules/security/rest/OnlineController.java/left.java
    throw new BadRequestException("\u6f14\u793a\u73af\u5883\u4e0d\u53ef\u64cd\u4f5c");
=======
    for (String token : keys) {
      token = EncryptUtils.desDecrypt(token);
      onlineUserService.logout(token);
    }
>>>>>>> /usr/src/app/output/elunez/eladmin/ba16a830ace07bc4f5cd27e3c288f7e1d2dce89d/eladmin-system/src/main/java/me/zhengjie/modules/security/rest/OnlineController.java/right.java
  }
}