package com.roncoo.pay.permission.utils;
import com.roncoo.pay.permission.entity.PmsOperator;
import org.apache.shiro.crypto.RandomNumberGenerator;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.util.ByteSource;

/**
 * 生成密码工具类
 *
 * 龙果学院：www.roncoo.com
 * 
 * @author：shenjialong
 */
public class PasswordHelper {
  private static RandomNumberGenerator randomNumberGenerator = new SecureRandomNumberGenerator();

  private static String algorithmName = "md5";

  private static String hashIteration = "2";

  private static int hashIterations = Integer.valueOf(hashIteration);

  public static void encryptPassword(PmsOperator pmsOperator) {
    pmsOperator.setsalt(randomNumberGenerator.nextBytes().toHex());
    String newPassword = new SimpleHash(algorithmName, pmsOperator.getLoginPwd(), ByteSource.Util.bytes(pmsOperator.getCredentialsSalt()), hashIterations).toHex();
    pmsOperator.setLoginPwd(newPassword);
  }

  /**
	 * 加密密码
	 * 
	 * @param loginPwd
	 *            明文密码
	 * @param salt
	 * @return
	 */
  public static String getPwd(String loginPwd, String salt) {
    String newPassword = new SimpleHash(algorithmName, loginPwd, ByteSource.Util.bytes(salt), hashIterations).toHex();
    return newPassword;
  }

  public static void main(String[] args) {
    System.out.println(getPwd("roncoo.123", "admin_roncoo8d78869f470951332959580424d4bf4f"));
  }
}