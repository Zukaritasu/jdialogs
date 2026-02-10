
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import java.util.Arrays;

public class VersionUpdater {
	public static void main(String[] args) {
		if (args.length == 0 || args[0].trim().isEmpty()) {
			System.out.println("Uso: java VersionUpdater 1.9.6");
			return;
		}

		String parts[] = args[0].split("\\.");

		if (parts.length != 3) {
			System.err.println("Error: Version format must be X.X.X");
			return;
		}

		Arrays.stream(parts).forEach(Integer::parseInt);

		updatePomXML(parts, "../pom.xml");
		updateREADME(parts, "../README.md");
		updateresourceRC(parts, "../vstudio/resource.rc");
	}

	private static void updatePomXML(String[] version, String filePath) {
		try {
			String xmlData = Files.readString(Path.of(filePath));
			String dotVer = String.join(".", version);

			xmlData = xmlData.replaceFirst("(?i)(<version>)[^<]+(</version>)",
					"$1" + dotVer + "$2");

			xmlData = xmlData.replaceFirst("(?i)(<tag>jdialogs-)[^<]+(</tag>)",
					"$1" + dotVer + "$2");

			Files.writeString(Path.of(filePath), xmlData);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void updateREADME(String[] version, String filePath) {
		try {
			String readme = Files.readString(Path.of(filePath));
			String dotVer = String.join(".", version);

			readme = readme.replaceAll("(?s)(<artifactId>jdialogs</artifactId>\\s*<version>)[^<]+(</version>)", 
                            "$1" + dotVer + "$2");

			readme = readme.replaceAll("(com\\.zukadev:jdialogs:)[^']+", "$1" + dotVer);

			Files.writeString(Path.of(filePath), readme);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void updateresourceRC(String[] version, String filePath) {
		try {
			// FILEVERSION X,X,X,1
			// PRODUCTVERSION X,X,X,1
			// VALUE "FileVersion", "X.X.X.1"
			// VALUE "ProductVersion", "X.X.X.1"

			String rcData = Files.readString(Path.of(filePath));

			String commaVer = String.join(",", version) + ",1";
			String dotVer = String.join(".", version) + ".1";

			rcData = rcData.replaceAll("(?i)(FILEVERSION|PRODUCTVERSION) \\d+,\\d+,\\d+,\\d+", "$1 " + commaVer);
			rcData = rcData.replaceAll("(?i)(\"FileVersion\"|\"ProductVersion\"), \"[^\"]+\"",
					"$1, \"" + dotVer + "\"");

			Files.writeString(Path.of(filePath), rcData);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
