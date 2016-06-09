package org.jenkinsci.plugins.DependencyGraph;

import java.io.BufferedWriter;
import java.io.File;
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

public class IvyReportParser {
	// String input: name/location of the input file (ivy report, xml)
	// String output: name of the output file to be created (graphviz dot file)
	// boolean indirect: false - show only direct dependencies, true - also show indirect dependencies
	public static void xmlToDot(String input, String output, boolean indirect) {
		String org;
		String name;
		String rev;
		String loc;

		String callerOrg;
		String callerName;
		String callerRev;

		String shapeName;

		Shape compareShape = null;
		boolean isDirect = true;
		boolean containsDirect = false;

		try {			
			ArrayList<Shape> shapeL = new ArrayList<Shape>();
			ListIterator<Shape> it;

			File outputFile = new File(output);
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile), Charset.defaultCharset()));

			bw.write("digraph G {");
			bw.newLine();

			File inputFile = new File(input);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(inputFile);
			doc.getDocumentElement().normalize();

			// If only direct dependencies are to be displayed:
			// Get info for root element to later test against
			if (!indirect) {
				NodeList infoL = doc.getElementsByTagName("info");
				if (infoL.getLength() > 0) {
					Element info = (Element) infoL.item(0);
					org = info.getAttribute("organisation");
					name = info.getAttribute("module");
					rev = info.getAttribute("revision");
					compareShape = new Shape("root", org, name, rev);
				}
			}

			NodeList nList = doc.getElementsByTagName("dependencies");
			assert(nList.getLength() == 1);

			Shape tempShape;
			Shape tempCaller;
			Shape temp;
			boolean exists = false;

			Element dependencies = (Element) nList.item(0);

			// Traverse list of all modules to extract info,
			// different revisions and caller info
			NodeList modules = dependencies.getElementsByTagName("module");
			for (int m = 0; m < modules.getLength(); m++) {

				Element module = (Element) modules.item(m);
				org = module.getAttribute("organisation");
				name = module.getAttribute("name");

				NodeList revList = module.getElementsByTagName("revision");
				for(int r = 0; r < revList.getLength(); r++) {
					Element revision = (Element) revList.item(r);
					rev = revision.getAttribute("name");

					shapeName = "shape" + shapeL.size();
					// Look if the source location of the module is known
					NodeList meta = revision.getElementsByTagName("metadata-artifact");
					if (meta.getLength() > 0) {
						loc = ((Element)meta.item(0)).getAttribute("origin-location");
						loc = loc.replace("\\", "/");
						tempShape = new Shape(shapeName, org, name, rev, loc);
					}
					else {
						tempShape = new Shape(shapeName, org, name, rev);
					}

					// Each module should be added to shapeL only once:
					// check whether module already exists
					it = shapeL.listIterator();
					while(it.hasNext()) {
						temp = it.next();
						if (temp.isEqual(tempShape)) {
							tempShape = temp;
							exists = true;
							break;
						}	
					}
					if (!exists) {shapeL.add(tempShape);}
					exists = false;

					containsDirect = false;

					// Traverse list of all callers and write dependency information
					// into graphviz file
					NodeList callers = revision.getElementsByTagName("caller");
					for (int c = 0; c < callers.getLength(); c++) {
						Element caller = (Element) callers.item(c);
						callerOrg = caller.getAttribute("organisation");
						callerName = caller.getAttribute("name");
						callerRev = caller.getAttribute("callerrev");

						// If only direct dependencies are to be displayed:
						// Test if it is a direct dependency
						if (!indirect) {
							if (compareShape.isEqual(new Shape("vgl",callerOrg,callerName,callerRev))) {
								isDirect = true;
								containsDirect = true;
							}
							else isDirect = false;
						}
						if (indirect || isDirect) {
							shapeName = "shape" + shapeL.size();
							tempCaller = new Shape(shapeName, callerOrg, callerName, callerRev);
							it = shapeL.listIterator();
							while(it.hasNext()) {
								temp = it.next();
								if (temp.isEqual(tempCaller)) {
									tempCaller = temp;
									exists = true;
									break;
								}	
							}
							if (!exists) {shapeL.add(tempCaller);}
							exists = false;
							bw.write(tempCaller.getShapeName() + " -> " + tempShape.getShapeName());
							bw.newLine();
						}
					}
					// If only direct dependencies are considered
					// and a module is not called by the root element
					// then remove it from the list of modules
					if (!indirect && !containsDirect) {
						shapeL.remove(tempShape);
					}
				}
			}
			// Write display info for the dependencies into the file
			it = shapeL.listIterator();
			while(it.hasNext()) {
				bw.write(it.next().toString());
				bw.newLine();
			}
			bw.write("}");
			bw.close();

		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (SAXException se) {
			se.printStackTrace();
		}
	}

	// input: complete path including filename of build.xml
	// output: name of the directory where the ivy report is placed
	public static String getBuildDir(String buildXML) {
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
}
