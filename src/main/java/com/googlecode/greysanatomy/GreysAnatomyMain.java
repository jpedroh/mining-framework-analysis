package com.googlecode.greysanatomy;

import com.googlecode.greysanatomy.console.client.ConsoleClient;
import com.googlecode.greysanatomy.exception.PIDNotMatchException;
import com.googlecode.greysanatomy.util.HostUtils;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * Hello world!
 */
public class GreysAnatomyMain {

    private static final Logger logger = LoggerFactory.getLogger("greysanatomy");
    public static final String JARFILE = GreysAnatomyMain.class.getProtectionDomain().getCodeSource().getLocation().getFile();

    public GreysAnatomyMain(String[] args) throws Exception {
<<<<<<< /usr/src/app/output/oldmanpushcart/greys-anatomy/26f8fde516d364d9aadbda98ad78f6c4255470e3/src/main/java/com/googlecode/greysanatomy/GreysAnatomyMain.java/left.java
    
        // ���������ļ�
        Configer configer = analyzeConfiger(args);

        // ����Ǳ���IP,���Լ���Agent
        if (HostUtils.isLocalHostIp(configer.getTargetIp())) {
            // ����agent
            attachAgent(configer);
        }

        // �������̨
        if(activeConsoleClient(configer)) {

            logger.info("attach done! pid={}; host={}; JarFile={}", new Object[]{
                    configer.getJavaPid(),
                    configer.getTargetIp() + ":" + configer.getTargetPort(),
                    JARFILE});
        }

||||||| /usr/src/app/output/oldmanpushcart/greys-anatomy/26f8fde516d364d9aadbda98ad78f6c4255470e3/src/main/java/com/googlecode/greysanatomy/GreysAnatomyMain.java/base.java
    	
    	// ���������ļ�
    	Configer configer = parsetConfiger(args);
    	
    	// ����agent
    	attachAgent(configer);
    	
    	// �������̨
    	activeConsoleClient(configer);
    	
    	logger.info("attach done! pid={}; port={}; JarFile={}", new Object[]{
    			configer.getJavaPid(), 
    			configer.getConsolePort(), 
    			JARFILE});
=======
    
        // ���������ļ�
        Configer configer = analyzeConfiger(args);

        // ����Ǳ���IP,���Լ���Agent
        if (HostUtils.isLocalHostIp(configer.getTargetIp())) {
            // ����agent
            attachAgent(configer);
        }

        // �������̨
        if (activeConsoleClient(configer)) {

    //            logger.info("attach done! pid={}; host={}; JarFile={}", new Object[]{
    //                    configer.getJavaPid(),
    //                    configer.getTargetIp() + ":" + configer.getTargetPort(),
    //                    JARFILE});


        }

>>>>>>> /usr/src/app/output/oldmanpushcart/greys-anatomy/26f8fde516d364d9aadbda98ad78f6c4255470e3/src/main/java/com/googlecode/greysanatomy/GreysAnatomyMain.java/right.java
    }

    /**
     * ����configer
     *
     * @param args
     * @return
     */
	private Configer analyzeConfiger(String[] args) {
	    final OptionParser parser = new OptionParser();
	    parser.accepts("pid").withRequiredArg().ofType(int.class).required();
	    parser.accepts("target").withOptionalArg().ofType(String.class);
	    parser.accepts("multi").withOptionalArg().ofType(int.class);

	    final OptionSet os = parser.parse(args);
	    final Configer configer = new Configer();

	    if (os.has("target")) {
	        final String[] strSplit = ((String) os.valueOf("target")).split(":");
	        configer.setTargetIp(strSplit[0]);
	        configer.setTargetPort(Integer.valueOf(strSplit[1]));
	    }

	    if (os.has("multi")
	            && (Integer) os.valueOf("multi") == 1) {
	        configer.setMulti(true);
	    } else {
	        configer.setMulti(false);
	    }

	    configer.setJavaPid((Integer) os.valueOf("pid"));
	    return configer;
	}
    /**
     * ����Agent
     *
     * @param configer
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws SecurityException
     * @throws IllegalArgumentException
     */
	private void attachAgent(Configer configer) throws IOException, ClassNotFoundException, IllegalArgumentException, SecurityException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {

	    final ClassLoader loader = Thread.currentThread().getContextClassLoader();
	    final Class<?> vmdClass = loader.loadClass("com.sun.tools.attach.VirtualMachineDescriptor");
	    final Class<?> vmClass = loader.loadClass("com.sun.tools.attach.VirtualMachine");

	    Object attachVmdObj = null;
	    for (Object obj : (List<?>) vmClass.getMethod("list", (Class<?>[]) null).invoke(null, (Object[]) null)) {
	        if (((String) vmdClass.getMethod("id", (Class<?>[]) null).invoke(obj, (Object[]) null)).equals("" + configer.getJavaPid())) {
	            attachVmdObj = obj;
	        }
	    }

	    if (null == attachVmdObj) {
	        throw new IllegalArgumentException("pid:" + configer.getJavaPid() + " not existed.");
	    }

	    Object vmObj = null;
	    try {
	        vmObj = vmClass.getMethod("attach", vmdClass).invoke(null, attachVmdObj);
	        vmClass.getMethod("loadAgent", String.class, String.class).invoke(vmObj, JARFILE, configer.toString());
	    } finally {
	        if (null != vmObj) {
	            vmClass.getMethod("detach", (Class<?>[]) null).invoke(vmObj, (Object[]) null);
	        }
	    }

	}
    /**
     * �������̨�ͻ���
     *
     * @param configer
     * @throws Exception
     */
    private boolean activeConsoleClient(Configer configer) throws Exception {
        try {
            ConsoleClient.getInstance(configer);
            return true;
        } catch (java.rmi.ConnectException ce) {
            logger.warn("target{{}:{}} RMI was shutdown, console will be exit.", configer.getTargetIp(), configer.getTargetPort());
        } catch (PIDNotMatchException pidnme) {
            logger.warn("target{{}:{}} PID was not match, console will be exit.", configer.getTargetIp(), configer.getTargetPort());
        }
        return false;
    }
    /**
     * ����configer
     *
     * @param args
     * @return
     */
    /**
     * ����Agent
     *
     * @param configer
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws SecurityException
     * @throws IllegalArgumentException
     */
    /**
     * �������̨�ͻ���
     *
     * @param configer
     * @throws Exception
     */
	
	
	
	public static void main(String[] args) {

	    try {
	        new GreysAnatomyMain(args);
	    } catch (Throwable t) {
	        logger.error("start greys-anatomy failed. because " + t.getMessage(), t);
	        System.exit(-1);
	    }

	}
}
