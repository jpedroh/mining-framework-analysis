package br.com.caelum.vraptor.console.command;

import java.io.File;

public class Restart implements Command {

	@Override
	public void execute() throws Exception {
		new Maven().execute(new CommandLine("compile"));
		if (new File("jetty").exists()) {
			customJetty();
		} else {
			RunningServer.restart();
		}
	}
<<<<<<< /usr/src/app/output/caelum/vraptor-console/75017316626095be7d4b6cbb4c1532561e1ff8a1/src/main/java/br/com/caelum/vraptor/console/command/Restart.java/left.java
	
	private void customNotImplementedJetty() throws MalformedURLException,
			ClassNotFoundException, NoSuchMethodException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException {
		// use esse diretorio
		URLClassLoader loader = new URLClassLoader(new URL[] { new File(
				"jetty").toURL() }, this.getClass().getClassLoader());
		Class<?> type;
		try {
			type = loader.loadClass(this.getClass().getPackage().getName() + ".Main");
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
		Method method = type.getMethod("main",
				new Class[] { String[].class });
		Object instance = type.newInstance();
		method.invoke(instance, new String[] {});
||||||| /usr/src/app/output/caelum/vraptor-console/75017316626095be7d4b6cbb4c1532561e1ff8a1/src/main/java/br/com/caelum/vraptor/console/command/Restart.java/base.java
	
	private void customNotImplementedJetty() throws MalformedURLException,
			ClassNotFoundException, NoSuchMethodException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException {
		// use esse diretorio
		URLClassLoader loader = new URLClassLoader(new URL[] { new File(
				"jetty").toURL() }, this.getClass().getClassLoader());
		Class<?> type = loader.loadClass("br.....Main");
		Method method = type.getMethod("main",
				new Class[] { String[].class });
		Object instance = type.newInstance();
		method.invoke(instance, new String[] {});
=======

	private void customJetty() {
		throw new UnsupportedOperationException("/jetty dir found, but we haven't implemented " +
				"custom jetty servers support, contact the developers for more info");
		
>>>>>>> /usr/src/app/output/caelum/vraptor-console/75017316626095be7d4b6cbb4c1532561e1ff8a1/src/main/java/br/com/caelum/vraptor/console/command/Restart.java/right.java
	}
	

}
