package org.jenkinsci.plugins.DependencyGraph;

import java.io.File;
import java.io.FileFilter;
import java.util.regex.Pattern;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.ListIterator;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import java.nio.charset.Charset;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import org.xml.sax.SAXException;

public class ReportFinder {
		private String reportLocation;
		private String buildDir;
		private String projectName;
		
		public ReportFinder(String workspace) {
			projectName = findProjectName(workspace + "/build.xml");
			buildDir = findBuildDir(workspace +"/build.xml");
			reportLocation = findReportLocation(workspace + "/" + buildDir, projectName);
		}
		
		public String getBuildDir() {
			return buildDir;
		}
		
		public String getReportLocation() {
			return reportLocation;
		}
		
		public String getProjectName() {
			return projectName;
		}
	
		private static String findReportLocation(String path, String projectName) {
			//String projectName = getProjectName(workspace + "/build.xml");
			//String path = workspace + "/" + getBuildDir(workspace + "/build.xml");
			String file = "";
			
			File root = new File(path);
			String regex = ".*" + Pattern.quote(projectName) + ".*\\.xml";
			
			File[] matching;
			if(!root.isDirectory()) {
			        throw new IllegalArgumentException(root+" is no directory.");
			}
			final Pattern p = Pattern.compile(regex); // careful: could also throw an exception!   
			
			matching = root.listFiles(new FileFilter(){
			        @Override
			        public boolean accept(File file) {
			            return p.matcher(file.getName()).matches();
			        }
			    });
			if (matching != null) {
				file = matching[0].toString();
			}
			return file;
		}
	
		// input: complete path including filename of build.xml
		// output: name of the directory where the ivy report is placed
		private static String findBuildDir(String buildXML) {
			String buildDir = "build";
			String name;

			try {			
				File inputFile = new File(buildXML);
				DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
				Document doc = dBuilder.parse(inputFile);
				doc.getDocumentElement().normalize();

				NodeList nList = doc.getElementsByTagName("project");
				assert(nList.getLength() == 1);

				Element project = (Element) nList.item(0);

				// Traverse list of all targets to find report target
				NodeList targets = project.getElementsByTagName("target");
				for (int m = 0; m < targets.getLength(); m++) {

					Element target = (Element) targets.item(m);
					name = target.getAttribute("name");

					// Check if it is the correct target with name="report" attribute
					if (name.equals("report")) {
						NodeList reports = target.getElementsByTagName("ivy:report");
						assert(nList.getLength()==1);
						Element report = (Element) reports.item(0);
						buildDir = report.getAttribute("todir");

						// buildDir can look like ${name.dir} or like name
						// remove surrounding ${ .dir}
						buildDir = buildDir.replace("${", "").replace(".dir}", "");
					}
				}

			} catch (IOException e) {
				e.printStackTrace();
			} catch (ParserConfigurationException pce) {
				pce.printStackTrace();
			} catch (SAXException se) {
				se.printStackTrace();
			}
			return buildDir;
		}
	
	// input: path + filename of build.xml
	// output: name of the output path for the ivy report
	private static String findProjectName(String buildXML) {
		String pname = "";

		try {			
			File inputFile = new File(buildXML);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(inputFile);
			doc.getDocumentElement().normalize();

			NodeList nList = doc.getElementsByTagName("project");
			assert(nList.getLength() == 1);

			Element project = (Element) nList.item(0);
			pname = project.getAttribute("name");

		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (SAXException se) {
			se.printStackTrace();
		}

		return pname;
	}
	
}