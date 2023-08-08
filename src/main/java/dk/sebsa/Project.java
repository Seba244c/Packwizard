package dk.sebsa;

import dk.sebsa.enums.ModLoaders;
import dk.sebsa.screens.Changelog;
import dk.sebsa.utils.FileUtils;
import lombok.Builder;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author sebs
 */
public class Project {
    public final File dir;
    private final File packFile;
    @Getter private String name;
    @Getter private String author;
    @Getter private String version;
    @Getter private List<Mod> mods;
    @Getter private List<String> modIds;
    @Getter private List<File> overrides;
    @Getter private List<String> mcVersions;
    @Getter private ModLoaders loader;

    public Project(File dir) throws IOException {
        this.dir = dir;
        this.packFile = new File(dir.getPath()+"/pack.toml");
        Changelog.load(dir);
        reloadFromDisk();
    }

    public void reloadFromDisk() throws IOException {
        mods = new ArrayList<>();
        overrides = new ArrayList<>();
        mcVersions = new ArrayList<>();
        modIds = new ArrayList<>();

        List<String> pack = FileUtils.readAllLinesList(FileUtils.loadFile(packFile.getPath()));
        for(String line : pack) {
            if(line.startsWith("name")) this.name = line.split("\"")[1].split("\"")[0];
            else if(line.startsWith("author")) this.author = line.split("\"")[1].split("\"")[0];
            else if(line.startsWith("version")) this.version = line.split("\"")[1].split("\"")[0];
            else if(line.startsWith("minecraft")) this.mcVersions.add(line.split("\"")[1].split("\"")[0]);
            else if(line.startsWith("quilt")) this.loader = ModLoaders.Quilt;
            else if(line.startsWith("acceptable-game-versions")) {
                if(line.split("= ")[1].equals("[]")) continue;
                for(String v : line.split("\\[")[1]
                        .split("]")[0]
                        .split(",")) {
                    this.mcVersions.add(v.split("\"")[1].split("\"")[0]);
                }
            }
        }

        // Scan for installed content
        List<File> files = getContent(dir);
        for(File f : files) {
            if(f.getName().endsWith(".pw.toml")) { mods.add(Mod.parseMod(f)); modIds.add(mods.get(mods.size()-1).id()); }
            else overrides.add(f);
        }
    }


    private List<File> getContent(final File folder) {
        List<File> files = new ArrayList<>();
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                if(fileEntry.getName().startsWith(".")) continue;
                files.addAll(getContent(fileEntry));
            } else {
                if(fileEntry.getName().equals("index.toml") || fileEntry.getName().equals("pack.toml")) continue;
                files.add(fileEntry);
            }
        }

        return files;
    }

    @Builder
    public record Mod(String name, String id, String filename, String side, String slug) {
        public static Mod parseMod(File f) throws IOException {
            List<String> modInfo = FileUtils.readAllLinesList(FileUtils.loadFile(f.getPath()));
            ModBuilder mod = Mod.builder().slug(f.getName().split("\\.")[0]);
            for(String line : modInfo) {
                if(line.startsWith("name")) mod.name(line.split("\"")[1].split("\"")[0]);
                else if(line.startsWith("project-id")) mod.id(line.split(" ")[1]);
                else if(line.startsWith("mod-id")) mod.id(line.split("\"")[1].split("\"")[0]);
                else if(line.startsWith("filename")) mod.filename(line.split("\"")[1].split("\"")[0]);
                else if(line.startsWith("side")) mod.side(line.split("\"")[1].split("\"")[0]);
            }

            return mod.build();
        }
    }
}
