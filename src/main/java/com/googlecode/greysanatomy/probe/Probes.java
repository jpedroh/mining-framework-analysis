package com.googlecode.greysanatomy.probe;
import com.googlecode.greysanatomy.probe.Advice.Target;
import javassist.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static com.googlecode.greysanatomy.probe.ProbeJobs.getJobListeners;
import static com.googlecode.greysanatomy.probe.ProbeJobs.isListener;
import static java.lang.String.format;
import static javassist.Modifier.*;

/**
 * ̽��㴥����<br/>
 * ����ĵ��У�һ����4��̽��㣬���Ƿֱ��Ӧ<br/>
 * fucntion f()
 * {
 * // probe:_before()
 * try {
 * do something...
 * // probe:_success()
 * } catch(Throwable t) {
 * // probe:_throws();
 * throw t;
 * } finally {
 * // probe:_finish();
 * }
 * <p/>
 * }
 *
 * @author vlinux
 */
public class Probes {
  private static final Logger logger = LoggerFactory.getLogger("greysanatomy");

  private static final String jobsClass = "com.googlecode.greysanatomy.probe.ProbeJobs";

  private static final String probesClass = "com.googlecode.greysanatomy.probe.Probes";

  /**
     * ����Target
     *
     * @param targetClass
     * @param targetConstructor
     * @param targetMethod
     * @param targetThis
     * @return
     */
  private static Target newTarget(String targetClassName, String targetBehaviorName, Object targetThis) {
    return new Target(targetClassName, targetBehaviorName, targetThis);
  }

  /**
     * ִ��ǰ��
     *
     * @param id
     * @param targetClass
     * @param targetConstructor
     * @param targetMethod
     * @param targetThis
     * @param args
     */
  public static void doBefore(int id, String targetClassName, String targetBehaviorName, Object targetThis, Object[] args) {
    if (isListener(id, AdviceListener.class)) {
      try {
        Advice p = new Advice(newTarget(targetClassName, targetBehaviorName, targetThis), args, false);
        ((AdviceListener) getJobListeners(id)).onBefore(p);
      } catch (Throwable t) {
        logger.warn("error at doBefore", t);
      }
    }
  }

  /**
     * ִ�гɹ�
     *
     * @param id
     * @param targetClass
     * @param targetConstructor
     * @param targetMethod
     * @param targetThis
     * @param args
     * @param returnObj
     */
  public static void doSuccess(int id, String targetClassName, String targetBehaviorName, Object targetThis, Object[] args, Object returnObj) {
    if (isListener(id, AdviceListener.class)) {
      try {
        Advice p = new Advice(newTarget(targetClassName, targetBehaviorName, targetThis), args, false);
        p.setReturnObj(returnObj);
        ((AdviceListener) getJobListeners(id)).onSuccess(p);
      } catch (Throwable t) {
        logger.warn("error at onSuccess", t);
      }
      doFinish(id, targetClassName, targetBehaviorName, targetThis, args, returnObj, null);
    }
  }

  /**
     * ִ���쳣
     *
     * @param id
     * @param targetClass
     * @param targetConstructor
     * @param targetMethod
     * @param targetThis
     * @param args
     * @param throwException
     */
  public static void doException(int id, String targetClassName, String targetBehaviorName, Object targetThis, Object[] args, Throwable throwException) {
    if (isListener(id, AdviceListener.class)) {
      try {
        Advice p = new Advice(newTarget(targetClassName, targetBehaviorName, targetThis), args, false);
        p.setThrowException(throwException);
        ((AdviceListener) getJobListeners(id)).onException(p);
      } catch (Throwable t) {
        logger.warn("error at onException", t);
      }
      doFinish(id, targetClassName, targetBehaviorName, targetThis, args, null, throwException);
    }
  }

  /**
     * ִ�����
     *
     * @param id
     * @param targetClass
     * @param targetMethod
     * @param targetThis
     * @param args
     * @param returnObj
     * @param throwException
     * @Param targetConstructor
     */
  public static void doFinish(int id, String targetClassName, String targetBehaviorName, Object targetThis, Object[] args, Object returnObj, Throwable throwException) {
    if (isListener(id, AdviceListener.class)) {
      try {
        Advice p = new Advice(newTarget(targetClassName, targetBehaviorName, targetThis), args, true);
        p.setThrowException(throwException);
        p.setReturnObj(returnObj);
        ((AdviceListener) getJobListeners(id)).onFinish(p);
      } catch (Throwable t) {
        logger.warn("error at onFinish", t);
      }
    }
  }

  /**
     * �Ƿ���˵���ǰ̽���Ŀ��
     *
     * @param cc
     * @param cb
     * @return
     */
  private static boolean isIngore(CtClass cc, CtBehavior cb) {
    final int ccMod = cc.getModifiers();
    final int cbMod = cb.getModifiers();
    if (isInterface(ccMod) || isAbstract(cbMod) || cc.getName().startsWith("com.googlecode.greysanatomy.")) {
      return true;
    }
    return false;
  }

  /**
     * ���̽����
     *
     * @param id
     * @param cc
     * @param cb
     * @throws CannotCompileException
     * @throws NotFoundException
     * @throws ClassNotFoundException
     */
  public static void mine(int id, CtClass cc, CtBehavior cb) throws CannotCompileException, NotFoundException {
    if (isIngore(cc, cb)) {
      return;
    }
    final String javassistThis = isStatic(cb.getModifiers()) ? "null" : "this";
    if (isListener(id, AdviceListener.class)) {
      if (cb.getMethodInfo().isMethod()) {
        mineProbeForMethod(cb, id, cc.getName(), cb.getName(), javassistThis);
      } else {
        if (cb.getMethodInfo().isConstructor()) {
          mineProbeForConstructor(cb, id, cc.getName(), cb.getName(), javassistThis);
        }
      }
    }
  }

  /**
     * �����캯�����
     *
     * @param cb
     * @param id
     * @param javassistClass
     * @param javassistConstructor
     * @param javassistMethod
     * @param javassistThis
     * @throws CannotCompileException
     * @throws NotFoundException
     */
  private static void mineProbeForConstructor(CtBehavior cb, int id, String targetClassName, String targetBehaviorName, String javassistThis) throws CannotCompileException, NotFoundException {
    cb.addCatch(format("{if(%s.isJobAlive(%s)){%s.doBefore(%s,\"%s\",\"%s\",%s,$args);%s.doException(%s,\"%s\",\"%s\",%s,$args,$e);}throw $e;}", jobsClass, id, probesClass, id, targetClassName, targetBehaviorName, javassistThis, probesClass, id, targetClassName, targetBehaviorName, javassistThis), ClassPool.getDefault().get("java.lang.Throwable"));
    cb.insertAfter(format("{if(%s.isJobAlive(%s)){%s.doBefore(%s,\"%s\",\"%s\",%s,$args);%s.doSuccess(%s,\"%s\",\"%s\",%s,$args,($w)$_);}}", jobsClass, id, probesClass, id, targetClassName, targetBehaviorName, javassistThis, probesClass, id, targetClassName, targetBehaviorName, javassistThis));
  }

  /**
     * �����������
     *
     * @param cb
     * @param id
     * @param javassistClass
     * @param javassistConstructor
     * @param javassistMethod
     * @param javassistThis
     * @throws CannotCompileException
     * @throws NotFoundException
     */
  private static void mineProbeForMethod(CtBehavior cb, int id, String targetClassName, String targetBehaviorName, String javassistThis) throws CannotCompileException, NotFoundException {
    cb.insertBefore(format("{if(%s.isJobAlive(%s))%s.doBefore(%s,\"%s\",\"%s\",%s,$args);}", jobsClass, id, probesClass, id, targetClassName, targetBehaviorName, javassistThis));
    cb.addCatch(format("{if(%s.isJobAlive(%s))%s.doException(%s,\"%s\",\"%s\",%s,$args,$e);throw $e;}", jobsClass, id, probesClass, id, targetClassName, targetBehaviorName, javassistThis), ClassPool.getDefault().get("java.lang.Throwable"));
    cb.insertAfter(format("{if(%s.isJobAlive(%s))%s.doSuccess(%s,\"%s\",\"%s\",%s,$args,($w)$_);}", jobsClass, id, probesClass, id, targetClassName, targetBehaviorName, javassistThis));
  }
}