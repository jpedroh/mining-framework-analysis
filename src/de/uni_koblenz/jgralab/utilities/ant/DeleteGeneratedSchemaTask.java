/*
 * JGraLab - The Java graph laboratory
 * (c) 2006-2010 Institute for Software Technology
 *               University of Koblenz-Landau, Germany
 * 
 *               ist@uni-koblenz.de
 * 
 * Please report bugs to http://serres.uni-koblenz.de/bugzilla
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package de.uni_koblenz.jgralab.utilities.ant;

import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.resources.FileResource;

import de.uni_koblenz.jgralab.GraphIO;
import de.uni_koblenz.jgralab.GraphIOException;
import de.uni_koblenz.jgralab.schema.Schema;

/**
 * This task deletes generated schema files according to a schema contained in a
 * tg file.
 * <ul>
 * <li><code>schemaFile</code> is the filename of the tg file, containing the
 * schema. If more than one schema should be deleted, a nested fileset can be
 * used instead. If this parameter is set and a nested fileset is present, both
 * will be taken.</li>
 * <li><code>sourcePath</code> the source location of Java's base package. The
 * generated schema will be deleted from this path. If multiple schemas have to
 * be deleted from different base package locations (e.g. "nomal" schemas from
 * "src" and test schemas from "testit"), the task must be called multiple
 * times, once for each base package location.</li>
 * </ul>
 * 
 * @author ist@uni-koblenz.de
 * 
 */
public class DeleteGeneratedSchemaTask extends Task {

	private Set<String> tgFiles;
	private String sourcePath;

	public DeleteGeneratedSchemaTask() {
		tgFiles = new HashSet<String>();
	}

	public void setSchemaFile(String filename) {
		tgFiles.add(filename);
	}

	public void setSourcePath(String sourcePath) {
		this.sourcePath = sourcePath;
	}

	public void addConfiguredFileset(FileSet files) {
		Iterator<?> fileIterator = files.iterator();
		while (fileIterator.hasNext()) {
			Object current = fileIterator.next();
			if (current instanceof FileResource) {
				File currentFile = ((FileResource) current).getFile();
				tgFiles.add(currentFile.getAbsolutePath());
			}
		}
	}

	@Override
	public void execute() {
		try {
			for (String currentTG : tgFiles) {
				if (new File(currentTG).exists()) {
					Schema schema = GraphIO.loadSchemaFromFile(currentTG);
					deleteGeneratedSchema(sourcePath, schema);
				} else {
					System.err
							.println("Warning: could not delete generated schema files: \""
									+ currentTG + "\" could not be found.");
				}
			}
		} catch (GraphIOException e) {
			throw new BuildException(e);
		}
	}

	public static void deleteGeneratedSchema(String commitPath, Schema schema)
			throws GraphIOException {
		String toDelete = commitPath + File.separator + schema.getPathName();
		File directory = new File(toDelete);
		if (directory.exists()) {
			assert (directory.isDirectory());
			deleteTree(directory);
			System.out.println("Deleted schema from directory "
					+ directory.getAbsolutePath());
		}
	}

	public static void deleteTree(File toDelete) {
		if (toDelete.isFile()) {
			toDelete.delete();
		} else {
			for (File current : toDelete.listFiles()) {
				deleteTree(current);
			}
			toDelete.delete();
		}
	}

}
