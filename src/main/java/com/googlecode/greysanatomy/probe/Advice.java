package com.googlecode.greysanatomy.probe;

/**
 * ֪ͨ��
 *
 * @author vlinux
 */
public class Advice {
  public static class Target {
    private final String targetClassName;

    private final String targetBehaviorName;

    private final Object targetThis;

    public Target(String targetClassName, String targetBehaviorName, Object targetThis) {
      this.targetClassName = targetClassName;
      this.targetBehaviorName = targetBehaviorName;
      this.targetThis = targetThis;
    }

    /**
         * ��ȡ̽��Ŀ��������
         *
         * @return ��̽���Ŀ��������
         */
    public String getTargetClassName() {
      return targetClassName;
    }

    /**
         * ��ȡ̽��Ŀ����Ϊ(method/constructor)����
         *
         * @return ��̽�����Ϊ����
         */
    public String getTargetBehaviorName() {
      return targetBehaviorName;
    }

    /**
         * ��ȡ̽��Ŀ��ʵ��
         *
         * @return
         */
    public Object getTargetThis() {
      return targetThis;
    }
  }

  private final Target target;

  private final Object[] parameters;

  private final boolean isFinished;

  private Object returnObj;

  private Throwable throwException;

  /**
     * ̽�������캯��
     *
     * @param target
     * @param parameters
     * @param isFinished
     */
  public Advice(Target target, Object[] parameters, boolean isFinished) {
    this.target = target;
    this.parameters = parameters;
    this.isFinished = isFinished;
  }

  /**
     * �Ƿ����׳��쳣����
     *
     * @return true:�����쳣��ʽ����/false:�Է����쳣��ʽ����������δ����
     */
  public boolean isThrowException() {
    return isFinished() && null != throwException;
  }

  /**
     * �Ƿ����������ؽ���
     *
     * @return true:������������ʽ����/false:�Է�����������ʽ����������δ����
     */
  public boolean isReturn() {
    return isFinished() && !isThrowException();
  }

  /**
     * �Ƿ��Ѿ�����
     *
     * @return true:�Ѿ�����/false:��δ����
     */
  public boolean isFinished() {
    return isFinished;
  }

  public Target getTarget() {
    return target;
  }

  public Object getReturnObj() {
    return returnObj;
  }

  public void setReturnObj(Object returnObj) {
    this.returnObj = returnObj;
  }

  public Throwable getThrowException() {
    return throwException;
  }

  public void setThrowException(Throwable throwException) {
    this.throwException = throwException;
  }

  public Object[] getParameters() {
    return parameters;
  }

  /**
     * getParameters()�����ı�����ԭ��������̫TM����
     * @return
     */
  public Object[] getParams() {
    return parameters;
  }

  /**
     * getThrowException()�����ı���
     * @return
     */
  public Throwable getThrowExp() {
    return throwException;
  }
}