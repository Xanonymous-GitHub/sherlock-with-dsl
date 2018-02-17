package sherlock.extraction;

import java.io.File;
import java.io.FilenameFilter;

class GZipFilenameFilter implements FilenameFilter{
	/* 
	 *  Return true if and only if file ends with one of the extension names:
	 * 	.gz
	 * 	.GZ
	 * 	.tgz
	 * 	.TGZ
	 */
	 @Override
	public boolean accept(File dir, String name) {
		 File f = new File(dir, name);
		 FileFilter ff = new FileFilter();

		 return (!ff.accept(f) && !f.isDirectory()								// File is not a regular file, is not a directory
				 && (name.endsWith(".gz") || name.endsWith(".GZ")					// and ends with a .gz, .GZ,
				 || name.endsWith("tgz") || name.endsWith("TGZ"))); 				// tgz or TGZ then return TRUE - is a GZipFile
	 }
		
}
