package com.googlecode.greysanatomy.console;

import com.googlecode.greysanatomy.Configer;
import com.googlecode.greysanatomy.console.command.Command;
import com.googlecode.greysanatomy.console.command.Commands;
import com.googlecode.greysanatomy.console.command.ShutdownCommand;
import com.googlecode.greysanatomy.console.command.QuitCommand;
import com.googlecode.greysanatomy.console.rmi.RespResult;
import com.googlecode.greysanatomy.console.rmi.req.ReqCmd;
import com.googlecode.greysanatomy.console.rmi.req.ReqGetResult;
import com.googlecode.greysanatomy.console.rmi.req.ReqKillJob;
import com.googlecode.greysanatomy.console.server.ConsoleServerService;
import com.googlecode.greysanatomy.exception.ConsoleException;
import com.googlecode.greysanatomy.util.GaStringUtils;
import jline.console.ConsoleReader;
import jline.console.KeyMap;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.rmi.NoSuchObjectException;
import static org.apache.commons.lang.StringUtils.EMPTY;
import static org.apache.commons.lang.StringUtils.isBlank;
import java.io.IOException;
import java.io.Writer;

/**
 * ����̨
 *
 * @author vlinux
 */
public class GreysAnatomyConsole {

    private static final Logger logger = LoggerFactory.getLogger("greysanatomy");

    private final Configer configer;
    private final ConsoleReader console;

    private volatile boolean isF = true;
    private volatile boolean isShutdown = false;

	private final long sessionId;

<<<<<<< /usr/src/app/output/oldmanpushcart/greys-anatomy/26f8fde516d364d9aadbda98ad78f6c4255470e3/src/main/java/com/googlecode/greysanatomy/console/GreysAnatomyConsole.java/left.java
	private String jobId;
||||||| /usr/src/app/output/oldmanpushcart/greys-anatomy/26f8fde516d364d9aadbda98ad78f6c4255470e3/src/main/java/com/googlecode/greysanatomy/console/GreysAnatomyConsole.java/base.java
	private String jobId;
=======
	private int jobId;
>>>>>>> /usr/src/app/output/oldmanpushcart/greys-anatomy/26f8fde516d364d9aadbda98ad78f6c4255470e3/src/main/java/com/googlecode/greysanatomy/console/GreysAnatomyConsole.java/right.java

    /**
     * ����GA����̨
     *
     * @param configer
     * @throws IOException
     */

	public GreysAnatomyConsole(Configer configer, long sessionId) throws IOException {
	    this.console = new ConsoleReader(System.in, System.out);
	    this.configer = configer;
	    this.sessionId = sessionId;
	    write(GaStringUtils.getLogo());
	    Commands.getInstance().registCompleter(console);
	}

    /**
     * ����̨������
     *
     * @author vlinux
     */

	private class GaConsoleInputer implements Runnable {

	    private final ConsoleServerService consoleServer;

	    private GaConsoleInputer(ConsoleServerService consoleServer) {
	        this.consoleServer = consoleServer;
	    }

	    @Override
	    public void run() {
	        while (true) {
	            try {
	                //����̨������
	                doRead();
	            } catch (ConsoleException ce) {
	                write("Error : "+ce.getMessage()+"\n");
	                write("Please type help for more information...\n\n");
	            } catch (Exception e) {
	                // �����ǿ���̨������ô��
	                logger.warn("console read failed.", e);
	            }
	        }
	    }

	    private void doRead() throws Exception {
<<<<<<< /usr/src/app/output/oldmanpushcart/greys-anatomy/26f8fde516d364d9aadbda98ad78f6c4255470e3/src/main/java/com/googlecode/greysanatomy/console/GreysAnatomyConsole.java/left.java
	        final String prompt = isF ? configer.getConsolePrompt() : EMPTY;
	        final ReqCmd reqCmd = new ReqCmd(console.readLine(prompt), sessionId);

	    			/*
	         * ���������ǿհ��ַ������ߵ�ǰ����̨û�����Ϊ�����
	    			 * �������������ȡ����
	    			 */
	        if (isBlank(reqCmd.getCommand()) || !isF) {
	            return;
	        }

	        final Command command;
	        try {
	            command = Commands.getInstance().newRiscCommand(reqCmd.getCommand());
	        } catch (Exception e) {
	            throw new ConsoleException(e.getMessage());
	        }


	        if (command != null) {
	            path = command.getRedirectPath();
	            if (!StringUtils.isEmpty(path)) {
	                //������֮ǰ�Ȱ��ض����ļ������ã����û��Ȩ�޻��������⣬�Ͳ���������
	                try {
	                    new File(path).createNewFile();
	                } catch (Exception e) {
	                    final String msg = String.format("create path:%s failed. %s", path, e.getMessage());
	                    logger.warn(msg, e);
	                    write(msg);
	                    return;
	                }
	            }
	        } else {
	            //���������ڣ��ͻ��˲����쳣����������˴���������Ҫ��path���
	            path = EMPTY;
	        }

	        // ������״̬���Ϊδ���
	        isF = false;

	        // �û�ִ����һ��shutdown����,�ն���Ҫ�˳�
	        if (command instanceof ShutdownCommand) {
	            isShutdown = true;
	        }

	        // ������������
	        RespResult result = consoleServer.postCmd(reqCmd);
	        jobId = result.getJobId();
||||||| /usr/src/app/output/oldmanpushcart/greys-anatomy/26f8fde516d364d9aadbda98ad78f6c4255470e3/src/main/java/com/googlecode/greysanatomy/console/GreysAnatomyConsole.java/base.java
	    	final String prompt = isF ? configer.getConsolePrompt() : EMPTY;
	    	final ReqCmd reqCmd = new ReqCmd(console.readLine(prompt), sessionId);
	    	
	    	/*
	    	 * ���������ǿհ��ַ������ߵ�ǰ����̨û�����Ϊ�����
	    	 * �������������ȡ����
	    	 */
	    	if( isBlank(reqCmd.getCommand()) || !isF ) {
	    		return;
	    	}
	    	
	    	final Command command = Commands.getInstance().newCommand(reqCmd.getCommand());
	    	
	    	if( command != null ) {
	    		path = command.getRedirectPath();
	    		if(!StringUtils.isEmpty(path)){
	    			//������֮ǰ�Ȱ��ض����ļ������ã����û��Ȩ�޻��������⣬�Ͳ���������
	    			try{
	    				new File(path).createNewFile();
	    			}catch(Exception e){
	    				final String msg = String.format("create path:%s failed. %s", path, e.getMessage());
	    				logger.warn(msg, e);
	    				write(msg);
	    				return;
	    			}
	    		}
	    	}else{
	    		//���������ڣ��ͻ��˲����쳣����������˴���������Ҫ��path���
	    		path = EMPTY;
	    	}
	    	
	    	// ������״̬���Ϊδ���
	    	isF = false;
	    	
	    	// ������������
	    	RespResult result =	consolServer.postCmd(reqCmd);
	    	jobId = result.getJobId();
=======
	        final String prompt = isF ? configer.getConsolePrompt() : EMPTY;
	        final ReqCmd reqCmd = new ReqCmd(console.readLine(prompt), sessionId);

	    			/*
	         * ���������ǿհ��ַ������ߵ�ǰ����̨û�����Ϊ�����
	    			 * �������������ȡ����
	    			 */
	        if (isBlank(reqCmd.getCommand()) || !isF) {
	            return;
	        }

	        final Command command;
	        try {
	            command = Commands.getInstance().newRiscCommand(reqCmd.getCommand());
	        } catch (Exception e) {
	            throw new ConsoleException(e.getMessage());
	        }

	        // ������״̬���Ϊδ���
	        isF = false;

	        // �û�ִ����һ��shutdown����,�ն���Ҫ�˳�
	        if (command instanceof ShutdownCommand
	                || command instanceof QuitCommand) {
	            isQuit = true;
	        }


	        // ������������
	        RespResult result = consoleServer.postCmd(reqCmd);
	        jobId = result.getJobId();
>>>>>>> /usr/src/app/output/oldmanpushcart/greys-anatomy/26f8fde516d364d9aadbda98ad78f6c4255470e3/src/main/java/com/googlecode/greysanatomy/console/GreysAnatomyConsole.java/right.java
	    }

	}

    /**
     * ����̨�����
     *
     * @author chengtongda
     */

	private class GaConsoleOutputer implements Runnable {

	    private final ConsoleServerService consoleServer;
	    private int currentJob;
	    private int pos = 0;

	    private GaConsoleOutputer(ConsoleServerService consoleServer) {
	        this.consoleServer = consoleServer;
	    }

	    @Override
	    public void run() {
	        while (true) {
	            try {
	                //����̨д����
	                doWrite();
	                //ÿ500ms��һ�ν��
	                Thread.sleep(500);
	            } catch (NoSuchObjectException nsoe) {
	                // Ŀ��RMI�ر�,��Ҫ�˳�����̨
	                logger.warn("target RMI's server was closed, console will be exit.");
	                break;
	            } catch (Exception e) {
	                logger.warn("console write failed.", e);
	            }
	        }
	    }

	    private void doWrite() throws Exception {
<<<<<<< /usr/src/app/output/oldmanpushcart/greys-anatomy/26f8fde516d364d9aadbda98ad78f6c4255470e3/src/main/java/com/googlecode/greysanatomy/console/GreysAnatomyConsole.java/left.java
	        //��������������û��ע���job  �򲻶�
	        if (isF || sessionId == 0 || StringUtils.isEmpty(jobId)) {
	            return;
	        }

	        //�����ǰ��ȡ�����job��������ִ�е�job�����0��ʼ��
	        if (!StringUtils.equals(currentJob, jobId)) {
	            pos = 0;
	            currentJob = jobId;
	        }

	        RespResult resp = consoleServer.getCmdExecuteResult(new ReqGetResult(jobId, sessionId, pos));
	        pos = resp.getPos();

	        //��д�ض���
	        try {
	            writeToFile(resp.getMessage(), path);
	        } catch (IOException e) {
	            //�ض���д�ļ������쳣ʱ����Ҫkill��job ��ִ����
	            consoleServer.killJob(new ReqKillJob(sessionId, jobId));
	            isF = true;
	            logger.warn("writeToFile failed.", e);
	            write(path + ":" + e.getMessage());
	            return;
	        }

	        write(resp);

	        if (isShutdown) {
	            logger.info("greys console will be shutdown.");
	            System.exit(0);
	        }

||||||| /usr/src/app/output/oldmanpushcart/greys-anatomy/26f8fde516d364d9aadbda98ad78f6c4255470e3/src/main/java/com/googlecode/greysanatomy/console/GreysAnatomyConsole.java/base.java
	    	//��������������û��ע���job  �򲻶�
	    	if(isF || sessionId == 0 || StringUtils.isEmpty(jobId)){
	    		return;
	    	}
	    	
	    	//�����ǰ��ȡ�����job��������ִ�е�job�����0��ʼ��
	    	if(!StringUtils.equals(currentJob, jobId)){
	    		pos = 0;
	    		currentJob = jobId;
	    	}
	    	
	    	RespResult resp = consolServer.getCmdExecuteResult(new ReqGetResult(jobId, sessionId, pos));
	    	pos = resp.getPos();
	    	
	    	//��д�ض���
	    	try{
	    		writeToFile(resp.getMessage(), path);
	    	}catch(IOException e){
	    		//�ض���д�ļ������쳣ʱ����Ҫkill��job ��ִ����
	    		consolServer.killJob(new ReqKillJob(sessionId, jobId));
	    		isF = true;
	    		logger.warn("writeToFile failed.", e);
	    		write(path + ":" + e.getMessage());
	    		return;
	    	}
	    	
	    	write(resp);
=======
	        //��������������û��ע���job  �򲻶�
	        if (isF
	                || sessionId == 0
	    //                    || StringUtils.isEmpty(jobId)) {
	                || jobId == 0) {
	            return;
	        }

	        //�����ǰ��ȡ�����job��������ִ�е�job�����0��ʼ��
	    //            if (!StringUtils.equals(currentJob, jobId)) {
	        if (currentJob != jobId) {
	            pos = 0;
	            currentJob = jobId;
	        }

	        RespResult resp = consoleServer.getCmdExecuteResult(new ReqGetResult(jobId, sessionId, pos));
	        pos = resp.getPos();

	        write(resp);

	        if (isQuit) {
	            logger.info("greys console will be shutdown.");
	            System.exit(0);
	        }

>>>>>>> /usr/src/app/output/oldmanpushcart/greys-anatomy/26f8fde516d364d9aadbda98ad78f6c4255470e3/src/main/java/com/googlecode/greysanatomy/console/GreysAnatomyConsole.java/right.java
	    }

	}

    /**
     * ����console
     *
     * @param consoleServer
     */

	public synchronized void start(final ConsoleServerService consoleServer) {
	    this.console.getKeys().bind("" + KeyMap.CTRL_D, new ActionListener() {

	        @Override
	        public void actionPerformed(ActionEvent e) {
	            if (!isF) {
	                try {
	                    isF = true;
	                    write("abort it.\n");
	                    redrawLine();
	                    consoleServer.killJob(new ReqKillJob(sessionId, jobId));
	                } catch (Exception e1) {
	                    // �����ǿ���̨������ô��
	                    logger.warn("killJob failed.", e);
	                }
	            }
	        }

	    });
	    new Thread(new GaConsoleInputer(consoleServer), "ga-console-inputer").start();
	    new Thread(new GaConsoleOutputer(consoleServer), "ga-console-outputer").start();
	}

    /**
     * �����̨���������Ϣ
     *
     * @param resp
     */

<<<<<<< /usr/src/app/output/oldmanpushcart/greys-anatomy/26f8fde516d364d9aadbda98ad78f6c4255470e3/src/main/java/com/googlecode/greysanatomy/console/GreysAnatomyConsole.java/left.java
    private void write(RespResult resp) {
        if (!isF) {
            String content = resp.getMessage();
            if (resp.isFinish()) {
                isF = true;
                //content += "\n------------------------------end------------------------------\n";
                content += "\n";
            }
            if (!StringUtils.isEmpty(content)) {
                write(content);
            }
        }
    }
||||||| /usr/src/app/output/oldmanpushcart/greys-anatomy/26f8fde516d364d9aadbda98ad78f6c4255470e3/src/main/java/com/googlecode/greysanatomy/console/GreysAnatomyConsole.java/base.java
=======
    private void write(RespResult resp) throws IOException {
        if (!isF) {
            String content = resp.getMessage();
            if (resp.isFinish()) {
                isF = true;
                //content += "\n------------------------------end------------------------------\n";
                content += "\n";
            }
            if (!StringUtils.isEmpty(content)) {
                write(content);
                redrawLine();
            }
        }
    }
>>>>>>> /usr/src/app/output/oldmanpushcart/greys-anatomy/26f8fde516d364d9aadbda98ad78f6c4255470e3/src/main/java/com/googlecode/greysanatomy/console/GreysAnatomyConsole.java/right.java

    /**
     * �����Ϣ
     *
     * @param message
     */

	private void write(String message) {
	    final Writer writer = console.getOutput();
	    try {
	        writer.write(message);
	        writer.flush();
	    } catch (IOException e) {
	        // ����̨дʧ�ܣ�����ô��
	        logger.warn("console write failed.", e);
	    }

	}

    /**
     * �����Ϣ���ļ�
     *
     * @param message
     * @param path
     * @throws IOException
     */

    private volatile boolean isQuit = false;

    /**
     * ����GA����̨
     *
     * @param configer
     * @throws IOException
     */

    /**
     * ����̨������
     *
     * @author vlinux
     */

    /**
     * ����̨�����
     *
     * @author chengtongda
     */

    /**
     * ����console
     *
     * @param consoleServer RMIͨѶ�õ�ConsoleServer
     */

    private synchronized void redrawLine() throws IOException {
        final String prompt = isF ? configer.getConsolePrompt() : EMPTY;
        console.setPrompt(prompt);
        console.redrawLine();
        console.flush();
    }

    /**
     * �����̨���������Ϣ
     *
     * @param resp ���ر�����Ϣ
     */

    /**
     * �����Ϣ
     *
     * @param message ����ı�����
     */
}
