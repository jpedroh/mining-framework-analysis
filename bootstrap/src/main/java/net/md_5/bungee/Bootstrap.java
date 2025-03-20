package net.md_5.bungee;

public class Bootstrap
{

    public static void main(String[] args) throws Exception
    {
        if ( Float.parseFloat( System.getProperty( "java.class.version" ) ) < 52.0 ) //BotFilter
        {
<<<<<<< /usr/src/app/output/spigotmc/bungeecord/2d9d3d351318fa36f0fac2ce09dfe868e21de293/bootstrap/src/main/java/net/md_5/bungee/Bootstrap.java/left.java
            System.err.println( "*** ОШИБОЧКА *** БотФильтеру нужна Java 8. Установите её, что бы запустить сервер!" );//BotFilter
||||||| /usr/src/app/output/spigotmc/bungeecord/2d9d3d351318fa36f0fac2ce09dfe868e21de293/bootstrap/src/main/java/net/md_5/bungee/Bootstrap.java/base.java
            System.err.println( "*** ERROR *** BungeeCord requires Java 7 or above to function! Please download and install it!" );//BotFilter
=======
            System.err.println( "*** ERROR *** BungeeCord requires Java 8 or above to function! Please download and install it!" );//BotFilter
>>>>>>> /usr/src/app/output/spigotmc/bungeecord/2d9d3d351318fa36f0fac2ce09dfe868e21de293/bootstrap/src/main/java/net/md_5/bungee/Bootstrap.java/right.java
            System.out.println( "Проверить версию: java -version" );//BotFilter
            return;
        }

        BungeeCordLauncher.main( args );
    }
}
