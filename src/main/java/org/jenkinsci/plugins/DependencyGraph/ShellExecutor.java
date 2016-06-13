package org.jenkinsci.plugins.DependencyGraph;

import hudson.FilePath;
import hudson.tasks.BatchFile;
import hudson.tasks.CommandInterpreter;
import hudson.util.FormValidation;
import hudson.model.AbstractProject;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.Builder;
import hudson.tasks.BuildStepDescriptor;
import jenkins.tasks.SimpleBuildStep;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.QueryParameter;
import hudson.model.Result;

import javax.servlet.ServletException;
import java.io.IOException;
import hudson.tasks.Shell;

/**
 * Helper class that executes the necessary shell scripts.
 */
public class ShellExecutor {

	/**
	 * Converts a .dot file to .svg and .jpg in the same directory.
	 * @param path
	 * 				path to the directory where the .dot file is stored
	 * @param file
	 * 				(input and output) file name without ending
	 * @throws IOException
	 */
	public static void dotToImages(String path, String file) throws IOException{
		String svgScript = "dot -Tsvg " + path + file + ".dot -o " 
				+ path + file + ".svg ";
		String jpgScript = "dot -Tjpg " + path + file + ".dot -o " 
				+ path + file + ".jpg ";
		Runtime.getRuntime().exec(svgScript);
		Runtime.getRuntime().exec(jpgScript);
	}

}

