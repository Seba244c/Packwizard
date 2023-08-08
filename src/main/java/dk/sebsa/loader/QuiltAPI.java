package dk.sebsa.loader;

import dk.sebsa.utils.APIUtils;
import dk.sebsa.utils.MavenUtils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author sebs
 */
public class QuiltAPI implements LoaderAPI {
    public static QuiltAPI instance = new QuiltAPI();
    private String[] cacheLoaderVersions;

    @Override
    public String[] loaderVersions() {
        if(cacheLoaderVersions == null) {
            cacheLoaderVersions = MavenUtils.getArtifactVersionsInverted(APIUtils.getURL("https://maven.quiltmc.org/repository/release/org/quiltmc/quilt-loader/maven-metadata.xml"));
        }

        return cacheLoaderVersions;
    }
}
