package org.jenkinsci.plugins.DependencyGraph;

import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Publisher;

/**
 * This class provides build step description.
 */
@Extension
public class DependencyGraphDescriptor extends BuildStepDescriptor<Publisher> {
	private boolean indirect = true;
	
    /**
     * Constructs a {link DependencyGraphDescriptor}.
     */
    public DependencyGraphDescriptor() {
        super(DependencyGraphRecorder.class);
    }
    
    public boolean showIndirect() {
    	return indirect;
    }

    /**
     * Gets the descriptor display name, used in the post step checkbox
     * description.
     * return the descriptor display name
     */
    @Override
    public final String getDisplayName() {
        return "Show Dependency Graph";
    }

    /**
     * Checks whether this descriptor is applicable.
     * param clazz
     *            the class
     * return true - of course the beard is applicable
     */
    @Override
    public final boolean isApplicable(
            final Class<? extends AbstractProject> clazz) {
        return true;
    }
}
