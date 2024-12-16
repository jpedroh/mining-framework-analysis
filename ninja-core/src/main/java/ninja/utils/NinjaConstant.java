  package  ninja . utils ;   public interface NinjaConstant  {  String  MODE_KEY_NAME = "ninja.mode" ;  String  MODE_TEST = "test" ;  String  MODE_DEV = "dev" ;  String  MODE_PROD = "prod" ;  String  VIEWS_DIR = "views" ;  String  CONTROLLERS_DIR = "controllers" ;  String  MODELS_DIR = "models" ;  String  LOCATION_VIEW_FTL_HTML_NOT_FOUND = "views/system/404notFound.ftl.html" ;  String  LOCATION_VIEW_FTL_HTML_BAD_REQUEST = "views/system/400badRequest.ftl.html" ;  String  LOCATION_VIEW_FTL_HTML_INTERNAL_SERVER_ERROR = "views/system/500internalServerError.ftl.html" ;  String  LOCATION_VIEW_FTL_HTML_UNAUTHORIZED = "views/system/401unauthorized.ftl.html" ;  String  LOCATION_VIEW_FTL_HTML_FORBIDDEN = "views/system/403forbidden.ftl.html" ;  String  I18N_NINJA_SYSTEM_BAD_REQUEST_TEXT_KEY = "ninja.system.bad_request.text" ;  String  I18N_NINJA_SYSTEM_BAD_REQUEST_TEXT_DEFAULT = "Oops. That''s a bad request and all we know." ;  String  I18N_NINJA_SYSTEM_INTERNAL_SERVER_ERROR_TEXT_KEY = "ninja.system.internal_server_error.text" ;  String  I18N_NINJA_SYSTEM_INTERNAL_SERVER_ERROR_TEXT_DEFAULT = "Oops. That''s an internal server error and all we know." ;  String  I18N_NINJA_SYSTEM_NOT_FOUND_TEXT_KEY = "ninja.system.not_found.text" ;  String  I18N_NINJA_SYSTEM_NOT_FOUND_TEXT_DEFAULT = "Oops. The requested route cannot be found." ;  String  I18N_NINJA_SYSTEM_UNAUTHORIZED_REQUEST_TEXT_KEY = "ninja.system.unauthorized.text" ;  String  I18N_NINJA_SYSTEM_UNAUTHORIZED_REQUEST_TEXT_DEFAULT = "Oops. You are unauthorized." ;  String  I18N_NINJA_SYSTEM_FORBIDDEN_REQUEST_TEXT_KEY = "ninja.system.forbidden.text" ;  String  I18N_NINJA_SYSTEM_FORBIDDEN_REQUEST_TEXT_DEFAULT = "Oops. That''s forbidden and all we know." ;   final String  applicationLanguages = "application.languages" ;   final String  LANG_COOKIE_SUFFIX = "_LANG" ;   final String  APPLICATION_MODULES_BASE_PACKAGE = "application.modules.package" ;   final String  applicationCookiePrefix = "application.cookie.prefix" ;   final String  applicationCookieDomain = "application.cookie.domain" ;   final String  applicationCookieEncrypted = "application.cookie.encryption" ;   final String  applicationName = "application.name" ;   final String  applicationSecret = "application.secret" ;   final String  serverName = "application.server.name" ;   final String  sessionExpireTimeInSeconds = "application.session.expire_time_in_seconds" ;   final String  sessionSendOnlyIfChanged = "application.session.send_only_if_changed" ;   final String  sessionTransferredOverHttpsOnly = "application.session.transferred_over_https_only" ;   final String  sessionHttpOnly = "application.session.http_only" ;   final String 
<<<<<<<
 FILE_UPLOADS_IN_MEMORY = "file.uploads.in_memory"
=======
 LOCATION_VIEW_HTML_INTERNAL_SERVER_ERROR_KEY = "application.views.500internalServerError"
>>>>>>>
 ;   final String 
<<<<<<<
 FILE_UPLOADS_MAX_FILE_SIZE = "file.uploads.file.size.max"
=======
 LOCATION_VIEW_HTML_NOT_FOUND_KEY = "application.views.404notFound"
>>>>>>>
 ;   final String 
<<<<<<<
 FILE_UPLOADS_MAX_REQUEST_SIZE = "file.uploads.total.size.max"
=======
 LOCATION_VIEW_HTML_BAD_REQUEST_KEY = "application.views.400badRequest"
>>>>>>>
 ;   final String 
<<<<<<<
 FILE_UPLOADS_DIRECTORY = "file.uploads.directory"
=======
 LOCATION_VIEW_HTML_UNAUTHORIZED_KEY = "application.views.401unauthorized"
>>>>>>>
 ;  String  DIAGNOSTICS_KEY_NAME = "application.diagnostics" ;   public final String  CACHE_IMPLEMENTATION = "cache.implementation" ;   public final String  MEMCACHED_HOST = "memcached.host" ;   public final String  MEMCACHED_USER = "memcached.user" ;   public final String  MEMCACHED_PASSWORD = "memcached.password" ;   final String  SESSION_SUFFIX = "_SESSION" ;   final String  FLASH_SUFFIX = "_FLASH" ;   final String  UNI_CODE_NULL_ENTITY = "\u0000" ;   final String  UTF_8 = "utf-8" ;   final String  HTTP_CACHE_CONTROL = "http.cache_control" ;   final String  HTTP_CACHE_CONTROL_DEFAULT = "3600" ;   final String  HTTP_USE_ETAG = "http.useETag" ;   final boolean  HTTP_USE_ETAG_DEFAULT = true ;   final String  NINJA_MIGRATION_RUN = "ninja.migration.run" ;  String  PERSISTENCE_UNIT_NAME = "ninja.jpa.persistence_unit_name" ;  String  DB_CONNECTION_URL = "db.connection.url" ;  String  DB_CONNECTION_USERNAME = "db.connection.username" ;  String  DB_CONNECTION_PASSWORD = "db.connection.password" ;  String  NINJA_JSONP_CALLBACK_PARAMETER = "ninja.jsonp.callbackParameter" ;  String  AUTHENTICITY_TOKEN = "authenticityToken" ;  String  LOCATION_VIEW_HTML_FORBIDDEN_KEY = "application.views.403forbidden" ; }