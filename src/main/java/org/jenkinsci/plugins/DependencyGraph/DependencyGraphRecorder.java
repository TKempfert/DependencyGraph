package org.jenkinsci.plugins.DependencyGraph;

import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Action;
import hudson.model.BuildListener;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Recorder;
import hudson.FilePath;
import java.io.IOException;
import java.util.logging.Logger;
import jenkins.model.Jenkins;
import org.kohsuke.stapler.DataBoundConstructor;
import hudson.EnvVars;


public class DependencyGraphRecorder extends Recorder {

	/**
	 * Logger.
	 */
	private static final Logger LOGGER = Logger
			.getLogger(DependencyGraphRecorder.class.getName());

	/**
	 * Constructs a {link DependencyGraphRecorder}
	 */
	@DataBoundConstructor
	public DependencyGraphRecorder() {
		LOGGER.info("DependencyGraph is activated");
	}

	/**
	 * Gets the RoundhouseAction as the project action. This is applicable for
	 * each job and only when there's at least one build in the job.
	 * param project
	 *            the project
	 * return the project action
	 */
	@Override
	public final Action getProjectAction(final AbstractProject<?, ?> project) {
		DependencyGraphAction action = null;
		if ((action = project.getLastSuccessfulBuild().getAction(DependencyGraphAction.class)) != null) {
			action = new DependencyGraphAction(action.getSVG(), action.getJPG());
		}
		return action;
	}

	/**
	 * Adds RoundhouseAction to the build actions. This is applicable for each
	 * build.
	 * param build
	 *            the build
	 * param launcher
	 *            the launcher
	 * param listener
	 *            the listener
	 * return true
	 * throws InterruptedException
	 *             when there's an interruption
	 * throws IOException
	 *             when there's an IO error
	 */
	@Override
	public final boolean perform(final AbstractBuild<?, ?> build,
			final Launcher launcher, final BuildListener listener)
					throws InterruptedException, IOException {

		String workspace = pathToString(build.getProject().getWorkspace());
		boolean showIndirect = true;
		Jenkins j = Jenkins.getInstance();
		if (j!=null) {
			DependencyGraphDescriptor desc = j.getDescriptorByType(DependencyGraphDescriptor.class);
			if (desc != null) {
				showIndirect = desc.showIndirect();
			}
		}
		String buildDir = IvyReportParser.getBuildDir(workspace + "/build.xml");
		String path = workspace + "/" + buildDir + "/";
		String projectName = IvyReportParser.getProjectName(workspace + "/build.xml");
		//String image = "report" + System.getProperty("jenkins.buildNumber");
		
		// Get environment variables in order to extract current build number
		//EnvVars envVars = new EnvVars();
		//envVars = build.getEnvironment(listener);
		String image = "report_" + build.getEnvironment(listener).get("BUILD_NUMBER");
		//String image = "report_blubb3";
		
		// Convert dependency information from report to image files (svg, jpg)
		IvyReportParser.xmlToDot(path + "org.apache-" + projectName + "-default.xml", 
								path + image + ".dot", 
								showIndirect);
		ShellExecutor.dotToImages(path, image);

		build.getActions().add(new DependencyGraphAction(image + ".svg", image + ".jpg"));
		return true;
	}

	public static String pathToString(FilePath workspace){
		String path = null;
		try {
			path = workspace.toURI().toString().substring(5);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return path;
	}



	/**
	 * Gets the required monitor service.
	 * return the BuildStepMonitor
	 */
	public final BuildStepMonitor getRequiredMonitorService() {
		return BuildStepMonitor.NONE;
	}
}
