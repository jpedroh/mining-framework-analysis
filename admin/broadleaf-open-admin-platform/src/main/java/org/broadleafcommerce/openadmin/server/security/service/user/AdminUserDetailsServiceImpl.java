package org.broadleafcommerce.openadmin.server.security.service.user;
import org.broadleafcommerce.openadmin.server.security.dao.AdminUserDao;
import org.broadleafcommerce.openadmin.server.security.domain.AdminRole;
import org.broadleafcommerce.openadmin.server.security.domain.AdminUser;
import org.broadleafcommerce.openadmin.server.security.service.AdminSecurityHelper;
import org.broadleafcommerce.openadmin.server.security.service.AdminSecurityService;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.annotation.Resource;

/**
 * @author Jeff Fischer
 */
@Component(value = "blAdminUserDetailsService") public class AdminUserDetailsServiceImpl implements UserDetailsService {
  @Resource(name = "blAdminUserDao") protected AdminUserDao adminUserDao;

  @Resource(name = "blAdminSecurityHelper") protected AdminSecurityHelper adminSecurityHelper;

  public static final String LEGACY_ROLE_PREFIX = "PERMISSION_";

  public static final String DEFAULT_SPRING_SECURITY_ROLE_PREFIX = "ROLE_";

  @Override public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException, DataAccessException {
    AdminUser adminUser = adminUserDao.readAdminUserByUserName(username);
    if (adminUser == null || adminUser.getActiveStatusFlag() == null || !adminUser.getActiveStatusFlag()) {
      throw new UsernameNotFoundException("The user was not found");
    }
    return buildDetails(username, adminUser);
  }

  protected UserDetails buildDetails(final String username, final AdminUser adminUser) {
    final List<SimpleGrantedAuthority> authorities = new ArrayList<>();
    addRoles(adminUser, authorities);
    addPermissions(adminUser, authorities);
    convertPermissionPrefixToRole(authorities);
    return createDetails(username, adminUser, authorities);
  }

  protected void addRoles(final AdminUser adminUser, final List<SimpleGrantedAuthority> authorities) {
    for (final AdminRole role : adminUser.getAllRoles()) {
      authorities.add(new SimpleGrantedAuthority(role.getName()));
      adminSecurityHelper.addAllPermissionsToAuthorities(authorities, role.getAllPermissions());
    }
  }

  protected void addPermissions(final AdminUser adminUser, final List<SimpleGrantedAuthority> authorities) {
    adminSecurityHelper.addAllPermissionsToAuthorities(authorities, adminUser.getAllPermissions());
    for (final String perm : AdminSecurityService.DEFAULT_PERMISSIONS) {
      authorities.add(new SimpleGrantedAuthority(perm));
    }
  }

  protected void convertPermissionPrefixToRole(final List<SimpleGrantedAuthority> authorities) {
    final ListIterator<SimpleGrantedAuthority> it = authorities.listIterator();
    while (it.hasNext()) {
      final SimpleGrantedAuthority auth = it.next();
      if (auth.getAuthority().startsWith(LEGACY_ROLE_PREFIX)) {
        it.add(new SimpleGrantedAuthority(DEFAULT_SPRING_SECURITY_ROLE_PREFIX + auth.getAuthority()));
        it.add(new SimpleGrantedAuthority(auth.getAuthority().replaceAll(LEGACY_ROLE_PREFIX, DEFAULT_SPRING_SECURITY_ROLE_PREFIX)));
      }
    }
  }

  protected UserDetails createDetails(final String username, final AdminUser adminUser, final List<SimpleGrantedAuthority> authorities) {
    return new AdminUserDetails(adminUser.getId(), username, adminUser.getPassword(), true, true, true, true, authorities);
  }
}