package dk.sebsa.utils;

import dk.sebsa.enums.ModLoaders;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * @author sebs
 */
public class APIUtils {
    private static final Map<String, String> cache = new HashMap<>();

    public static JSONObject labrinthSearchProjects(String search, int offset, String[] mcVersions, ModLoaders modLoader) throws IOException, JSONException {
        // Generate Facets
        StringBuilder facets = new StringBuilder("[");
        if(modLoader.equals(ModLoaders.Quilt)) { facets.append("[\"categories:quilt\",\"categories:fabric\"]"); }
        else facets.append("[\"categories:").append(modLoader.name().toLowerCase()).append("\"]");
        facets.append(",[\"project_type!=modpack\"]");
        facets.append(",[");
        for(int i = 0; i < mcVersions.length; i++) {
            if(i > 0) facets.append(",\"versions:");
            else facets.append("\"versions:");
            facets.append(mcVersions[i]);
            facets.append("\"");
        }
        facets.append("]]");

        // Gen query string
        String charset = "UTF-8";
        String query = String.format("query=%s&offset=%s&limit=%s&facets=%s",
                URLEncoder.encode(search, charset),
                URLEncoder.encode(String.valueOf(offset), charset),
                URLEncoder.encode(String.valueOf(5), charset),
                URLEncoder.encode(facets.toString()), charset);

        // Create connection
        URL url = new URL("https://api.modrinth.com/v2/search" + "?" + query);

        // Parse json
        return new JSONObject(getRequest(url, charset));
    }

    public static String getRequest(URL url, String charsetIn) throws IOException {
        System.out.println("GetRequest: " + url);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept-Charset", charsetIn);
        conn.setRequestProperty("User-Agent", "Seba244c/Packwizard/1.0.0-SNAPSHOT (ssnoer@proton.me)");

        // Send request
        conn.connect();
        int rep = conn.getResponseCode();
        for (Map.Entry<String, List<String>> header : conn.getHeaderFields().entrySet()) {
            System.out.println(header.getKey() + "=" + header.getValue());
        }

        // Get Charset
        String contentType = conn.getHeaderField("Content-Type");
        String charset = null;
        for (String param : contentType.replace(" ", "").split(";")) {
            if (param.startsWith("charset=")) {
                charset = param.split("=", 2)[1];
                break;
            }
        }

        // Parse
        if (charset != null || contentType.equalsIgnoreCase("application/json")) {
            StringBuilder out = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(charset == null ? new InputStreamReader(conn.getInputStream()) : new InputStreamReader(conn.getInputStream(), charset))) {
                for (String line; (line = reader.readLine()) != null;) {
                     out.append(line);
                }
            }
            return out.toString();
        } else {
            throw new RuntimeException("APIUtils cannot accept binary content!");
        }
    }

    public static String getURL(String s) {
        return cache.computeIfAbsent(s, s1 -> {
            try {
                URL url = new URL(s1);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.connect();

                int responsecode = conn.getResponseCode();

                if (responsecode != 200) {
                    throw new RuntimeException("HttpResponseCode: " + responsecode);
                } else {

                    String inline = "";
                    Scanner scanner = new Scanner(url.openStream());

                    //Write all the JSON data into a string using a scanner
                    while (scanner.hasNext()) {
                        inline += scanner.nextLine();
                    }

                    //Close the scanner
                    scanner.close();

                    return inline;
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
