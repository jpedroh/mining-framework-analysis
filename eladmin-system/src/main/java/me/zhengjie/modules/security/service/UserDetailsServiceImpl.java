package me.zhengjie.modules.security.service;
import lombok.RequiredArgsConstructor;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.exception.EntityNotFoundException;
import me.zhengjie.modules.security.config.bean.LoginProperties;
import me.zhengjie.modules.security.service.dto.JwtUserDto;
import me.zhengjie.modules.system.service.DataService;
import me.zhengjie.modules.system.service.RoleService;
import me.zhengjie.modules.system.service.UserService;
import me.zhengjie.modules.system.service.dto.UserDto;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Zheng Jie
 * @date 2018-11-22
 */
@RequiredArgsConstructor @Service(value = "userDetailsService") public class UserDetailsServiceImpl implements UserDetailsService {
  private final UserService userService;

  private final RoleService roleService;

  private final DataService dataService;

  private final LoginProperties loginProperties;

  public void setEnableCache(boolean enableCache) {
    this.loginProperties.setCacheEnable(enableCache);
  }

  /**
     * 用户信息缓存
     *
     * @see {@link UserCacheClean}
     */
  static Map<String, JwtUserDto> userDtoCache = new ConcurrentHashMap<>();

  @Override public JwtUserDto loadUserByUsername(String username) {
    boolean searchDb = true;
    JwtUserDto jwtUserDto = null;
    if (loginProperties.isCacheEnable() && userDtoCache.containsKey(username)) {
      jwtUserDto = userDtoCache.get(username);
      searchDb = false;
    }
    if (searchDb) {
      UserDto user;
      try {
        user = userService.findByName(username);
      } catch (EntityNotFoundException e) {
        throw new UsernameNotFoundException("", e);
      }
      if (user == null) {
        throw new UsernameNotFoundException("");
      } else {
        if (!user.getEnabled()) {
          throw new BadRequestException("\u8d26\u53f7\u672a\u6fc0\u6d3b");
        }
        jwtUserDto = new JwtUserDto(user, dataService.getDeptIds(user), roleService.mapToGrantedAuthorities(user));
        userDtoCache.put(username, jwtUserDto);
      }
    }
    return jwtUserDto;
  }
}