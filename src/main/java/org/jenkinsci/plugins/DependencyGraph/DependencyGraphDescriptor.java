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
	
    /**
     * Constructs a {@link DependencyGraphDescriptor}.
     */
    public DependencyGraphDescriptor() {
        super(DependencyGraphRecorder.class);
    }

    /**
     * Gets the descriptor display name, used in the post step checkbox
     * description.
     * @return the descriptor display name
     */
    @Override
    public final String getDisplayName() {
        return "Show Dependency Graph";
    }

    /**
     * This descriptor is always applicable.
     * @param clazz
     *            the class
     * @return true
     */
    @Override
    public final boolean isApplicable(
            final Class<? extends AbstractProject> clazz) {
        return true;
    }
}
