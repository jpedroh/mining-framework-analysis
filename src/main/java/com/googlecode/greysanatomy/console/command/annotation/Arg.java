<<<<<<< /usr/src/app/output/oldmanpushcart/greys-anatomy/26f8fde516d364d9aadbda98ad78f6c4255470e3/src/main/java/com/googlecode/greysanatomy/console/command/annotation/Arg.java/left.java
package com.googlecode.greysanatomy.console.command.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Arg {

    /**
     * ���������еĲ�������
     *
     * @return ��������
     */
    public String name();

    /**
     * �Ƿ����
     *
     * @return
     */
    public boolean isRequired() default true;

    /**
     * ����ע��
     *
     * @return
     */
    public String description() default "";

    /**
     * ����У��
     *
     * @return
     */
    public ArgVerifier[] verify() default {};

}
||||||| /usr/src/app/output/oldmanpushcart/greys-anatomy/26f8fde516d364d9aadbda98ad78f6c4255470e3/src/main/java/com/googlecode/greysanatomy/console/command/annotation/Arg.java/base.java
package com.googlecode.greysanatomy.console.command.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Arg {

	/**
	 * ���������еĲ�������
	 * @return ��������
	 */
	public String name();
	
	/**
	 * �Ƿ����
	 * @return
	 */
	public boolean isRequired() default true;
	
	/**
	 * ����ע��
	 * @return
	 */
	public String description() default "";
	
	/**
	 * ����У��
	 * @return
	 */
	public ArgVerifier[] verify() default {};
	
}
=======
fatal: path 'src/main/java/com/googlecode/greysanatomy/console/command/annotation/Arg.java' does not exist in '0bee9a9d50380719ed4e8fe485a1bf1988fa4bf5'
>>>>>>> /usr/src/app/output/oldmanpushcart/greys-anatomy/26f8fde516d364d9aadbda98ad78f6c4255470e3/src/main/java/com/googlecode/greysanatomy/console/command/annotation/Arg.java/right.java
