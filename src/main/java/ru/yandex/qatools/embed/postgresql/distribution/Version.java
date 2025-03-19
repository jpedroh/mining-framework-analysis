package ru.yandex.qatools.embed.postgresql.distribution;

import de.flapdoodle.embed.process.distribution.IVersion;

/**
 * PostgreSQL Version enum
 */
public enum Version implements IVersion {

    V9_6_0("9.6.0-1"),
    V9_6_1("9.6.1-1"),
    V9_5_5("9.5.5-1"),
    V9_4_10("9.4.10-1"),
    V9_3_15("9.3.15-1"),
    @Deprecated V9_2_19("9.2.19-1"),
    @Deprecated V9_1_24("9.1.24-1"),
    ;

    private final String specificVersion;

    Version(String vName) {
        this.specificVersion = vName;
    }

    @Override
    public String asInDownloadPath() {
        return specificVersion;
    }

    @Override
    public String toString() {
        return "Version{" + specificVersion + '}';
    }

    public enum Main implements IVersion {
        @Deprecated V9_1(V9_1_24),
        @Deprecated V9_2(V9_2_19),
        V9_3(V9_3_15),
        @Deprecated
        V9_4(V9_4_10),
<<<<<<< /usr/src/app/output/yandex-qatools/postgresql-embedded/ee78f139b59e3ca8a016ad361b96d95fc4440360/src/main/java/ru/yandex/qatools/embed/postgresql/distribution/Version.java/left.java
        V9_5(V9_5_5),
        V9_6(V9_6_1),
||||||| /usr/src/app/output/yandex-qatools/postgresql-embedded/ee78f139b59e3ca8a016ad361b96d95fc4440360/src/main/java/ru/yandex/qatools/embed/postgresql/distribution/Version.java/base.java
        V9_5(V9_5_5),
=======
        V9_5(V9_5_5),
        V9_6(V9_6_0),
>>>>>>> /usr/src/app/output/yandex-qatools/postgresql-embedded/ee78f139b59e3ca8a016ad361b96d95fc4440360/src/main/java/ru/yandex/qatools/embed/postgresql/distribution/Version.java/right.java
        PRODUCTION(V9_6);

        private final IVersion _latest;

        Main(IVersion latest) {
            _latest = latest;
        }

        @Override
        public String asInDownloadPath() {
            return _latest.asInDownloadPath();
        }
    }
}
