package dk.sebsa.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author sebs
 */
public class MavenUtils {
    public static String[] getArtifactVersionsInverted(String metadata) {
        List<String> versions = new ArrayList<>();
        String versionsTag = metadata.split("<versions>")[1].split("</versions>")[0];
        versionsTag = versionsTag.strip().replaceAll(" ", "");

        String[] versionTags = versionsTag.split("<version>");
        for (String tag : versionTags) {
            String versionTag = tag.split("</version>")[0];
            versions.add(0, versionTag);
        }

        return versions.toArray(new String[0]);
    }
}
