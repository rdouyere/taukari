package net.airvantage.taukari.dao;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class RunDao {

	private final String rootDirectory;

	public RunDao(String rootDirectory) {
		this.rootDirectory = rootDirectory;
	}

	public List<String> list(String rootPath) {
		List<String> ret = new ArrayList<String>();

		File root = new File(rootPath);
		if (root.exists()) {
			for (String path : root.list()) {
				ret.add(path);
			}
		}

		return ret;
	}

	public boolean exists(String name) {
		File f = new File(getDirectory(name));
		f.getParentFile().mkdirs();
		return f.exists() && f.isDirectory();
	}

	public void create(String name) {
		File f = new File(getDirectory(name));
		f.mkdirs();
	}

	private String getDirectory(String name) {
		return rootDirectory + File.separator + name;
	}
}
