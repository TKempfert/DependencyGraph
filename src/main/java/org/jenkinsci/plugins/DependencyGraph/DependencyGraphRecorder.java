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
import hudson.model.Result;

/**
 * Integrates the functionality that is performed as a post build step.
 */
public class DependencyGraphRecorder extends Recorder {
	/**
	 * Determines whether all depencencies or only direct dependencies
	 * are shown.<br />
	 * true: only direct dependencies
	 * false: all dependencies<br />
	 */
	private boolean directOnly = false;
	
	public boolean getDirectOnly() {
		return directOnly;
	}

	/**
	 * Logger.
	 */
	private static final Logger LOGGER = Logger
			.getLogger(DependencyGraphRecorder.class.getName());

	/**
	 * Constructs a {@link DependencyGraphRecorder}.
	 */
	@DataBoundConstructor
	public DependencyGraphRecorder(boolean directOnly) {
		this.directOnly = directOnly;
		LOGGER.info("DependencyGraph is activated");
	}

	/**
	 * Gets the {@link DependencyGraphAction} as the project action. This is applicable for
	 * each job and only when there is at least one successful build in the job.
	 * @param project
	 *            the project
	 * @return copied {@link DependencyGraphAction} of the last successful build
	 */
	@Override
	public final Action getProjectAction(final AbstractProject<?, ?> project) {
		DependencyGraphAction action = null;
		if ((action = project.getLastSuccessfulBuild().getAction(DependencyGraphAction.class)) != null) {
			action = new DependencyGraphAction(action);
		}
		return action;
	}

	/**
	 * Adds {@link DependencyGraphAction} to the build actions. This is applicable for each build.
	 * @param build
	 *            the build
	 * @param launcher
	 *            the launcher
	 * @param listener
	 *            the listener
	 * @return true
	 * @throws InterruptedException
	 * @throws IOException
	 */
	@Override
	public final boolean perform(final AbstractBuild<?, ?> build,
			final Launcher launcher, final BuildListener listener)
					throws InterruptedException, IOException {
		try {
			// Test for build success: must be at least unstable (no fatal errors)
			// guarantees that dependencies could be resolved correctly
			Result res = build.getResult();
			if (res != null && res.isBetterOrEqualTo(Result.UNSTABLE)) {

				String workspace = pathToString(build.getProject().getWorkspace());

				ReportFinder finder = new ReportFinder(workspace);
				String path = finder.getBuildDir() + "/";

				// Extract current build number from environment variable
				String image = "report_" + build.getEnvironment(listener).get("BUILD_NUMBER");

				// Convert dependency information from report to image files (svg, jpg)
				// and get number of direct/indirect dependencies
				build.getActions().add(new DependencyGraphAction(
						path + image + ".svg", 
						path + image + ".jpg", 
						IvyReportParser.xmlToDot(finder.getReportLocation(), 
								path + image + ".dot", 
								directOnly)
						)
						);
				ShellExecutor.dotToImages(path, image);
			}
		} catch(Exception e) {
			// Catch all exceptions in order to not distort the build result
			e.printStackTrace();
		}
		
		return true;
	}

	/**
	 * Converts a FilePath to a String and cuts off the "file:" at the beginning.
	 * @param workspace
	 * 					the FilePath
	 * @return String representation of the FilePath
	 */
	public static String pathToString(FilePath workspace){
		String path = null;
		try {
			path = workspace.toURI().toString().replace("file:", "");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return path;
	}



	/**
	 * Gets the required monitor service.
	 * @return the BuildStepMonitor
	 */
	public final BuildStepMonitor getRequiredMonitorService() {
		return BuildStepMonitor.NONE;
	}
}
