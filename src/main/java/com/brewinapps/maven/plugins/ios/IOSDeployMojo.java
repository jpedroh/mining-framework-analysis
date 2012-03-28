package com.brewinapps.maven.plugins.ios;

import java.util.HashMap;
import java.util.Map;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;


/**
 * 
 * @author Brewin' Apps AS
 * @goal deploy
 */
public class IOSDeployMojo extends AbstractMojo {
	
	/**
	 * iOS Source Directory
	 * @parameter
	 * 		expression="${ios.sourceDir}"
	 * 		default-value=""
	 */
	private String sourceDir;	
	
	/**
	 * HockeyApp Api Token
	 * @parameter
	 * 		expression="${ios.hockeyAppToken}"
	 */
	private String hockeyAppToken;
			
	/**
	 * TestFlight Release Notes
	 * @parameter
	 * 		expression="${ios.releaseNotes}"
	 * 		default-value="Release generated by ios-maven-plugin"
	 */
	private String releaseNotes;
	
	/**
	 * iOS Target Directory
	 * @parameter
	 * 		expression="${ios.targetDir}"
	 * 		default-value="target"
	 */
	private String targetDir;
	
	/**
	 * iOS Target Directory
	 * @parameter
	 * 		expression="${ios.appName}"
	 * @required
	 */
	private String appName;	
	
	/**
	 * iOS configuration
	 * @parameter
	 * 		expression="${ios.configuration}"
	 * 		default-value="Release"
	 */
	private String configuration;	
		
	/**
	* The maven project.
	* 
	* @parameter expression="${project}"
	* @required
	* @readonly
	*/
	protected MavenProject project;	
	
	/**
	 * 
	 */
	public void execute() throws MojoExecutionException, MojoFailureException {
		try {
			String appDir = project.getBasedir().getAbsoluteFile() + 
					"/" + targetDir + "/" + configuration + "-iphoneos/";			
			
			Map<String, String> properties = new HashMap<String, String>();
			properties.put("sourceDir", sourceDir);
			properties.put("appName", appName);
			properties.put("hockeyAppToken", hockeyAppToken);
			properties.put("releaseNotes", releaseNotes);
			properties.put("appDir", appDir);
			properties.put("baseDir", project.getBasedir().toString());
			properties.put("configuration", configuration);
			properties.put("targetDir", targetDir);
			
			ProjectDeployer.deploy(properties);
		} catch (IOSException e) {
			System.out.println(e.getMessage());
			throw new MojoExecutionException(e.getMessage());
		}
	}

}
