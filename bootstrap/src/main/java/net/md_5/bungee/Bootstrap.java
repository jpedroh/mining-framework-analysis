package net.md_5.bungee;

public class Bootstrap {
  public static void main(String[] args) throws Exception {
    if (Float.parseFloat(System.getProperty("java.class.version")) < 52.0) {
      System.err.println(
<<<<<<< /usr/src/app/output/spigotmc/bungeecord/2d9d3d351318fa36f0fac2ce09dfe868e21de293/bootstrap/src/main/java/net/md_5/bungee/Bootstrap.java/left.java
      "*** \u041e\u0428\u0418\u0411\u041e\u0427\u041a\u0410 *** \u0411\u043e\u0442\u0424\u0438\u043b\u044c\u0442\u0435\u0440\u0443 \u043d\u0443\u0436\u043d\u0430 Java 8. \u0423\u0441\u0442\u0430\u043d\u043e\u0432\u0438\u0442\u0435 \u0435\u0451, \u0447\u0442\u043e \u0431\u044b \u0437\u0430\u043f\u0443\u0441\u0442\u0438\u0442\u044c \u0441\u0435\u0440\u0432\u0435\u0440!"
=======
      "*** ERROR *** BungeeCord requires Java 8 or above to function! Please download and install it!"
>>>>>>> /usr/src/app/output/spigotmc/bungeecord/2d9d3d351318fa36f0fac2ce09dfe868e21de293/bootstrap/src/main/java/net/md_5/bungee/Bootstrap.java/right.java
      );
      System.out.println("\u041f\u0440\u043e\u0432\u0435\u0440\u0438\u0442\u044c \u0432\u0435\u0440\u0441\u0438\u044e: java -version");
      return;
    }
    BungeeCordLauncher.main(args);
  }
}