package io.swagger.api;

import javax.xml.bind.annotation.XmlTransient;

@javax.xml.bind.annotation.XmlRootElement
<<<<<<< /usr/src/app/output/wordnik/swagger-codegen/6ab6d1fb349203823ae26f36c6e4bc838cf7941b/samples/server/petstore/springboot/src/main/java/io/swagger/api/ApiResponseMessage.java/left.java
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringBootServerCodegen", date = "2016-05-05T15:10:34.669+08:00")
||||||| /usr/src/app/output/wordnik/swagger-codegen/6ab6d1fb349203823ae26f36c6e4bc838cf7941b/samples/server/petstore/springboot/src/main/java/io/swagger/api/ApiResponseMessage.java/base.java
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringBootServerCodegen", date = "2016-05-04T16:34:30.253+02:00")
=======
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringBootServerCodegen", date = "2016-05-05T15:30:42.322+08:00")
>>>>>>> /usr/src/app/output/wordnik/swagger-codegen/6ab6d1fb349203823ae26f36c6e4bc838cf7941b/samples/server/petstore/springboot/src/main/java/io/swagger/api/ApiResponseMessage.java/right.java
public class ApiResponseMessage {
	public static final int ERROR = 1;
	public static final int WARNING = 2;
	public static final int INFO = 3;
	public static final int OK = 4;
	public static final int TOO_BUSY = 5;

	int code;
	String type;
	String message;
	
	public ApiResponseMessage(){}
	
	public ApiResponseMessage(int code, String message){
		this.code = code;
		switch(code){
		case ERROR:
			setType("error");
			break;
		case WARNING:
			setType("warning");
			break;
		case INFO:
			setType("info");
			break;
		case OK:
			setType("ok");
			break;
		case TOO_BUSY:
			setType("too busy");
			break;
		default:
			setType("unknown");
			break;
		}
		this.message = message;
	}

	@XmlTransient
	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
