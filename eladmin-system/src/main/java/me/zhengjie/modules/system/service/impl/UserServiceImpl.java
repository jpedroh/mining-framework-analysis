package me.zhengjie.modules.system.service.impl;
import lombok.RequiredArgsConstructor;
import me.zhengjie.config.FileProperties;
import me.zhengjie.modules.security.service.UserCacheClean;
import me.zhengjie.modules.system.domain.User;
import me.zhengjie.exception.EntityExistException;
import me.zhengjie.exception.EntityNotFoundException;
import me.zhengjie.modules.system.repository.UserRepository;
import me.zhengjie.modules.system.service.UserService;
import me.zhengjie.modules.system.service.dto.JobSmallDto;
import me.zhengjie.modules.system.service.dto.RoleSmallDto;
import me.zhengjie.modules.system.service.dto.UserDto;
import me.zhengjie.modules.system.service.dto.UserQueryCriteria;
import me.zhengjie.modules.system.service.mapstruct.UserMapper;
import me.zhengjie.utils.*;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotBlank;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Zheng Jie
 * @date 2018-11-23
 */
@Service @RequiredArgsConstructor @CacheConfig(cacheNames = "user") public class UserServiceImpl implements UserService {
  private final UserRepository userRepository;

  private final UserMapper userMapper;

  private final FileProperties properties;

  private final RedisUtils redisUtils;

  private final UserCacheClean userCacheClean;

  @Override public Object queryAll(UserQueryCriteria criteria, Pageable pageable) {
    Page<User> page = userRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder), pageable);
    return PageUtil.toPage(page.map(userMapper::toDto));
  }

  @Override public List<UserDto> queryAll(UserQueryCriteria criteria) {
    List<User> users = userRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder));
    return userMapper.toDto(users);
  }

  @Override @Cacheable(key = "\'id:\' + #p0") @Transactional(rollbackFor = Exception.class) public UserDto findById(long id) {
    User user = userRepository.findById(id).orElseGet(User::new);
    ValidationUtil.isNull(user.getId(), "User", "id", id);
    return userMapper.toDto(user);
  }

  @Override @Transactional(rollbackFor = Exception.class) public void create(User resources) {
    if (userRepository.findByUsername(resources.getUsername()) != null) {
      throw new EntityExistException(User.class, "username", resources.getUsername());
    }
    if (userRepository.findByEmail(resources.getEmail()) != null) {
      throw new EntityExistException(User.class, "email", resources.getEmail());
    }
    userRepository.save(resources);
  }

  @Override @Transactional(rollbackFor = Exception.class) public void update(User resources) {
    User user = userRepository.findById(resources.getId()).orElseGet(User::new);
    ValidationUtil.isNull(user.getId(), "User", "id", resources.getId());
    User user1 = userRepository.findByUsername(resources.getUsername());
    User user2 = userRepository.findByEmail(resources.getEmail());
    if (user1 != null && !user.getId().equals(user1.getId())) {
      throw new EntityExistException(User.class, "username", resources.getUsername());
    }
    if (user2 != null && !user.getId().equals(user2.getId())) {
      throw new EntityExistException(User.class, "email", resources.getEmail());
    }
    if (!resources.getRoles().equals(user.getRoles())) {
      redisUtils.del(CacheKey.DATE_USER + resources.getId());
      redisUtils.del(CacheKey.MENU_USER + resources.getId());
      redisUtils.del(CacheKey.ROLE_AUTH + resources.getId());
    }
    if (!resources.getUsername().equals(user.getUsername())) {
      redisUtils.del("user::username:" + user.getUsername());
    }
    user.setUsername(resources.getUsername());
    user.setEmail(resources.getEmail());
    user.setEnabled(resources.getEnabled());
    user.setRoles(resources.getRoles());
    user.setDept(resources.getDept());
    user.setJobs(resources.getJobs());
    user.setPhone(resources.getPhone());
    user.setNickName(resources.getNickName());
    user.setGender(resources.getGender());
    userRepository.save(user);
    delCaches(user.getId(), user.getUsername());
  }

  @Override @Transactional(rollbackFor = Exception.class) public void updateCenter(User resources) {
    User user = userRepository.findById(resources.getId()).orElseGet(User::new);
    user.setNickName(resources.getNickName());
    user.setPhone(resources.getPhone());
    user.setGender(resources.getGender());
    userRepository.save(user);
    delCaches(user.getId(), user.getUsername());
  }

  @Override @Transactional(rollbackFor = Exception.class) public void delete(Set<Long> ids) {
    for (Long id : ids) {
      UserDto user = findById(id);
      delCaches(user.getId(), user.getUsername());
    }
    userRepository.deleteAllByIdIn(ids);
  }

  @Override @Cacheable(key = "\'username:\' + #p0") public UserDto findByName(String userName) {
    User user = userRepository.findByUsername(userName);
    if (user == null) {
      throw new EntityNotFoundException(User.class, "name", userName);
    } else {
      return userMapper.toDto(user);
    }
  }

  @Override @Transactional(rollbackFor = Exception.class) public void updatePass(String username, String pass) {
    userRepository.updatePass(username, pass, new Date());
    redisUtils.del("user::username:" + username);
    flushCache(username);
  }

  @Override @Transactional(rollbackFor = Exception.class) public Map<String, String> updateAvatar(MultipartFile multipartFile) {
    User user = userRepository.findByUsername(SecurityUtils.getCurrentUsername());
    String oldPath = user.getAvatarPath();
    File file = FileUtil.upload(multipartFile, properties.getPath().getAvatar());
    user.setAvatarPath(Objects.requireNonNull(file).getPath());
    user.setAvatarName(file.getName());
    userRepository.save(user);
    if (StringUtils.isNotBlank(oldPath)) {
      FileUtil.del(oldPath);
    }
    @NotBlank String username = user.getUsername();
    redisUtils.del(CacheKey.USER_NAME + username);
    flushCache(username);
    return new HashMap<String, String>(1) {
      {
        put("avatar", file.getName());
      }
    };
  }

  @Override @Transactional(rollbackFor = Exception.class) public void updateEmail(String username, String email) {
    userRepository.updateEmail(username, email);
    redisUtils.del(CacheKey.USER_NAME + username);
    flushCache(username);
  }

  @Override public void download(List<UserDto> queryAll, HttpServletResponse response) throws IOException {
    List<Map<String, Object>> list = new ArrayList<>();
    for (UserDto userDTO : queryAll) {
      List<String> roles = userDTO.getRoles().stream().map(RoleSmallDto::getName).collect(Collectors.toList());
      Map<String, Object> map = new LinkedHashMap<>();
      map.put("\u7528\u6237\u540d", userDTO.getUsername());
      map.put("\u89d2\u8272", roles);
      map.put("\u90e8\u95e8", userDTO.getDept().getName());
      map.put("\u5c97\u4f4d", userDTO.getJobs().stream().map(JobSmallDto::getName).collect(Collectors.toList()));
      map.put("\u90ae\u7bb1", userDTO.getEmail());
      map.put("\u72b6\u6001", userDTO.getEnabled() ? "\u542f\u7528" : "\u7981\u7528");
      map.put("\u624b\u673a\u53f7\u7801", userDTO.getPhone());
      map.put("\u4fee\u6539\u5bc6\u7801\u7684\u65f6\u95f4", userDTO.getPwdResetTime());
      map.put("\u521b\u5efa\u65e5\u671f", userDTO.getCreateTime());
      list.add(map);
    }
    FileUtil.downloadExcel(list, response);
  }

  /**
     * 清理缓存
     *
     * @param id /
     */
  public void delCaches(Long id, String username) {
    redisUtils.del(CacheKey.USER_ID + id);
    redisUtils.del(CacheKey.USER_NAME + username);
    flushCache(username);
  }

  /**
     * 清理 登陆时 用户缓存信息
     *
     * @param username
     */
  private void flushCache(String username) {
    userCacheClean.cleanUserCache(username);
  }
}