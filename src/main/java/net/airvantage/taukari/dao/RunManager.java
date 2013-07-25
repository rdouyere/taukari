package net.airvantage.taukari.dao;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages "runs": a place where input and processed files are stored.
 * <p>
 * "run" may not be the best word but it is the one now!
 * </p>
 * <p>
 * This manager is not much than a file manager for the moment but it could do
 * more later. Including manage meta-data.
 * </p>
 */
public class RunManager {

	/**
	 * Lists the content of the directory which are also directories.
	 * 
	 * @param path
	 *            the full path of the directory to list
	 * @return a list of directories inside the given one.
	 */
	public List<String> list(String path) {
		List<String> ret = new ArrayList<String>();

		File root = new File(path);
		if (root.exists() && root.isDirectory()) {
			for (File file : root.listFiles()) {
				if (file.isDirectory()) {
					ret.add(file.getName());
				}
			}
		}

		return ret;
	}

	/**
	 * Tests if a path already exists or not.
	 * 
	 * @param path
	 *            the full path to test
	 * @return true of a file or directory already exists, else false
	 */
	public boolean exists(String path) {
		File f = new File(path);
		f.getParentFile().mkdirs();
		return f.exists() && f.isDirectory();
	}

	/**
	 * Creates a directory.
	 * 
	 * @param path
	 *            the full path.
	 */
	public void create(String path) {
		File f = new File(path);
		f.mkdirs();
	}

}
