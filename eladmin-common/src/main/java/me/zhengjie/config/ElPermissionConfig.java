package me.zhengjie.config;
import me.zhengjie.utils.SecurityUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service(value = "el") public class ElPermissionConfig {
  public Boolean check(String... permissions) {
    String anonymous = "anonymous";
    if (Arrays.asList(permissions).contains(anonymous)) {
      return true;
    }
    List<String> elPermissions = SecurityUtils.getUserDetails().getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
    if (elPermissions.contains("admin")) {
      return true;
    }
    return 
<<<<<<< /usr/src/app/output/elunez/eladmin/8480f0e1d52dc7622b1b0b89ecc30b9379cd8a8f/eladmin-common/src/main/java/me/zhengjie/config/ElPermissionConfig.java/left.java
    Arrays.stream(permissions).filter(elPermissions::contains).collect(Collectors.toList()).size() > 0
=======
    elPermissions.contains("admin") || Arrays.stream(permissions).anyMatch(elPermissions::contains)
>>>>>>> /usr/src/app/output/elunez/eladmin/8480f0e1d52dc7622b1b0b89ecc30b9379cd8a8f/eladmin-common/src/main/java/me/zhengjie/config/ElPermissionConfig.java/right.java
    ;
  }
}