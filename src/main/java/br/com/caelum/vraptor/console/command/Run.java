package br.com.caelum.vraptor.console.command;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

import br.com.caelum.vraptor.console.command.jetty.Main;

public class Run implements Command {
	
	@Override
	public void execute() throws Exception {
		System.out.println("Running");
		if (new File("jetty").exists()) {
			// use esse diretorio
			URLClassLoader loader = new URLClassLoader(new URL[] { new File(
					"jetty").toURL() }, this.getClass().getClassLoader());
			Class<?> type = loader.loadClass("br.....Main");
			Method method = type.getMethod("main",
					new Class[] { String[].class });
			Object instance = type.newInstance();
			method.invoke(instance, new String[] {});
		} else {
			Main.main(new String[] {});
		}
	}

}
