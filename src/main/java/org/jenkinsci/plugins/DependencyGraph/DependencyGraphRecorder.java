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
import java.io.File;

/**
 * Integrates the functionality that is performed as a post build step.
 */
public class DependencyGraphRecorder extends Recorder {
	/**
	 * Determines whether all depencencies or only direct dependencies
	 * are shown.<br>
	 * true: only direct dependencies<br>
	 * false: all dependencies
	 */
	private boolean directOnly = false;

	/**
	 * Logger.
	 */
	private static final Logger LOGGER = Logger.getLogger(DependencyGraphRecorder.class.getName());

	/**
	 * Constructs a {@link DependencyGraphRecorder}.
	 */
	@DataBoundConstructor
	public DependencyGraphRecorder(boolean directOnly) {
		this.directOnly = directOnly;
		LOGGER.info("DependencyGraph is activated");
	}

	/**
	 * @return boolean that indicates whether all dependencies are shown (false)
	 * or only direct dependencies (true)
	 */
	public boolean getDirectOnly() {
		return directOnly;
	}

	/**
	 * Gets the {@link DependencyGraphAction} as the project action. This is applicable for
	 * each job and only when there is at least one successful build  in the job since the 
	 * plugin was activated.
	 * @param project
	 *            the project
	 * @return copied {@link DependencyGraphAction} of the last successful build
	 */
	@Override
	public final Action getProjectAction(final AbstractProject<?, ?> project) {
		if (project.getLastSuccessfulBuild().getAction(DependencyGraphAction.class) != null) {
			return new JobGraphAction(project);
		}
		return null;
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
	 */
	@Override
	public final boolean perform(final AbstractBuild<?, ?> build,
			final Launcher launcher, final BuildListener listener) {
		try {
			// Test for build success: must be at least unstable (no fatal errors)
			// guarantees that dependencies could be resolved correctly
			Result res = build.getResult();
			if (res != null && res.isBetterOrEqualTo(Result.UNSTABLE)) {

				String workspace = pathToString(build.getProject().getWorkspace());

				ReportFinder finder = new ReportFinder(workspace);
				String path = workspace + "/" + finder.getBuildDir() + "/";

				// Extract current build number from environment variable
				String image = "report_" + build.getEnvironment(listener).get("BUILD_NUMBER");

				// Convert dependency information from report to image files (svg, jpg)
				// and get number of direct/indirect dependencies
				int[] n = IvyReportParser.xmlToDot(finder.getReportLocation(), 
						path + image + ".dot", 
						!directOnly);
				if (n != null) {
					ShellExecutor.dotToImages(path, image);
					//if ((new File(path + image + ".svg")).exists()) {
						build.getActions().add(new DependencyGraphAction(
								image + ".svg", 
								image + ".jpg", 
								n,
								workspace,
								finder.getBuildDir(),
								build.getEnvironment(listener).get("JOB_NAME")
								)
								);
					//}
				}
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
	private static String pathToString(FilePath workspace){
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
