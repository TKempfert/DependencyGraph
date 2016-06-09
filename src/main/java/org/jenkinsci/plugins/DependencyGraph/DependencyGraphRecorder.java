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
		Action action = null;
		if (project.getLastBuild() != null) {
			action = new DependencyGraphAction();
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
		IvyReportParser.xmlToDot(path + "org.apache-hello-ivy-default.xml", 
								path + "report.dot", 
								showIndirect);
		ShellExecutor.dotToImages(path, "report");

		build.getActions().add(new DependencyGraphAction());
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
