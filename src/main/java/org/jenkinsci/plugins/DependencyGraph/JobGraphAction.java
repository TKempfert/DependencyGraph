package org.jenkinsci.plugins.DependencyGraph;

import hudson.model.Action;
import hudson.model.AbstractProject;

/**
 * Action that gets the dependency graph from the last successful build.
 */
public final class JobGraphAction implements Action {
	private AbstractProject<?, ?> project;
	
	/**
	 * Constructs a {@link JobGraphAction}
	 * @param project
	 * 				the project
	 */
	public JobGraphAction(AbstractProject<?, ?> project) {
		super();
		this.project = project;
	}
	
	/**
	 * Checks if it is ok to display the graph (refers to corresponding function of the build action)
	 */
	public boolean getOk() {
		return project.getLastSuccessfulBuild().getAction(DependencyGraphAction.class).getOk();
	}
	
	/**
	 * Extracts information from the last successful build.
	 * @return the file name of the dependency graph as a jpg image
	 */
	public String getJpg() {
		return project.getLastSuccessfulBuild().getAction(DependencyGraphAction.class).getJpg();
	}
	
	/**
	 * Extracts information from the last successful build.
	 * @return the file name of the dependency graph as an svg image
	 */
	public String getSvg() {
		return project.getLastSuccessfulBuild().getAction(DependencyGraphAction.class).getSvg();
	}
	
	/**
	 * Extracts information from the last successful build.
	 * @return the name of the Jenkins Job
	 */
	public String getJobName() {
		return project.getLastSuccessfulBuild().getAction(DependencyGraphAction.class).getJobName();
	}
	
	/**
	 * Extracts information from the last successfull build.
	 * @return the build directory relative to the workspace as a String
	 */
	public String getBuildDir() {
		return project.getLastSuccessfulBuild().getAction(DependencyGraphAction.class).getBuildDir();
	}
	
	/**
	 * Extracts information from the last successful build.
	 * @return overall number of dependencies
	 */
	public String getNIndirect() {
		return project.getLastSuccessfulBuild().getAction(DependencyGraphAction.class).getNIndirect();
	}
	
	/**
	 * Extracts information from the last successful build.
	 * @return number of direct dependencies
	 */
	public String getNDirect() {
		return project.getLastSuccessfulBuild().getAction(DependencyGraphAction.class).getNDirect();
	}
	
	/**
	 * Gets the action display name.
	 * @return the display name
	 */
	public String getDisplayName() {
		return "Dependency Graph";
	}

	/**
	 * Gets the URL name for this action.
	 * @return the URL name
	 */
	public String getUrlName() {
		return "DependencyGraph";
	}
	
    /**
     * This action doesn't provide an icon file.
     * @return null
     */
    public String getIconFileName() {
        return null;
    }



}
