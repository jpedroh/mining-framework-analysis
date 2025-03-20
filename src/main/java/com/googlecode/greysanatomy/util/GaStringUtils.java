package com.googlecode.greysanatomy.util;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Scanner;

/**
 * �ַ�������������
 *
 * @author vlinux
 */
public class GaStringUtils {

    public static final String LINE = "---------------------------------------------------------------\n";

    /**
     * ����URL-ENCODE
     *
     * @param str
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String encode(String str) throws UnsupportedEncodingException {
        if (StringUtils.isBlank(str)) {
            return StringUtils.EMPTY;
        }
        return URLEncoder.encode(str, "utf-8");
    }
    /**
     * ����URL-DECODE
     *
     * @param str
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String decode(String str) throws UnsupportedEncodingException {
        if (StringUtils.isBlank(str)) {
            return StringUtils.EMPTY;
        }
        return URLDecoder.decode(str, "utf-8");
    }
    /**
     * ����һ���ַ���
     *
     * @param obj
     * @return
     */
	public static String newString(Object obj) {
	    if (null == obj) {
	        return StringUtils.EMPTY;
	    }
	    return obj.toString();
	}
    /**
     * չʾlogo
     *
     * @return
     */
	public static String getLogo() {
	    final StringBuilder logoSB = new StringBuilder();
	    final Scanner scanner = new Scanner(Object.class.getResourceAsStream("/com/googlecode/greysanatomy/res/logo.txt"));
	    while (scanner.hasNextLine()) {
	        logoSB.append(scanner.nextLine()).append("\n");
	    }
	    return logoSB.toString();
	}
    /**
     * �����
     *
     * @param sb
     * @param c
     * @param str
     */
	public static void rightFill(StringBuilder sb, int c, String str) {
	    for (int i = 0; i < c; i++) {
	        sb.append(str);
	    }
	}
    /**
     * ��ȡ����ִ�ж�ջ��Ϣ
     *
     * @return
     */
	public static String getStack() {
	    final StackTraceElement[] stes = Thread.currentThread().getStackTrace();
	    final StringBuilder stSB = new StringBuilder()
	            .append("Thread Info:").append(Thread.currentThread().getName()).append("\n");

	    if (ArrayUtils.isEmpty(stes)
	            || stes.length == 1) {
	        return stSB.toString();
	    }

	    for (int index = 4; index < stes.length; index++) {
	        final StackTraceElement ste = stes[index];
	        stSB.append(index == 2 ? "  " : "    at ")
	                .append(ste.getClassName()).append(".")
	                .append(ste.getMethodName())
	                .append("(").append(ste.getFileName()).append(":").append(ste.getLineNumber()).append(")\n");
	    }

	    return stSB.toString();
	}
    public static final String ABORT_MSG = "Press Ctrl+D to abort this job.";
    /**
     * ����URL-ENCODE
     *
     * @param str
     * @return
     * @throws UnsupportedEncodingException
     */
    /**
     * ����URL-DECODE
     *
     * @param str
     * @return
     * @throws UnsupportedEncodingException
     */
    /**
     * ����һ���ַ���
     *
     * @param obj
     * @return
     */
    /**
     * չʾlogo
     *
     * @return
     */
    /**
     * �����
     *
     * @param sb
     * @param c
     * @param str
     */
    /**
     * ��ȡ����ִ�ж�ջ��Ϣ
     *
     * @return
     */
    /**
     * ������
     *
     * @param name
     * @param progress
     * @param total
     * @return
     */
    public static String progress(String name, int progress, int total) {

        final StringBuilder sb = new StringBuilder();
        final int f = progress * 100 / total;
        sb.append(String.format("%s: %3d", name, f)).append("%[");
        for (int index = 1; index <= 100; index++) {
            if (index <= f) {
                sb.append("#");
            } else {
                sb.append(" ");
            }
        }
        sb.append("]");

        return sb.toString();

    }
    /**
     * ����ժҪ
     *
     * @param str
     * @param length
     * @return
     */
    public static String summary(String str, int length) {

        final StringBuilder sb = new StringBuilder();

        if (StringUtils.length(str) <= length) {
            sb.append(str);
        } else if (length <= 3) {
            sb.append(StringUtils.substring(str, 0, 3));
        } else {
            sb.append(StringUtils.substring(str, 0, length - 3)).append("...");
        }

        return sb.toString();

    }

}
