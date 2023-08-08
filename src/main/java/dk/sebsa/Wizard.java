package dk.sebsa;

import dk.sebsa.enums.ModLoaders;
import dk.sebsa.screens.Changelog;
import dk.sebsa.screens.Window;
import dk.sebsa.utils.FileUtils;
import dk.sebsa.utils.ImGUIUtils;
import dk.sebsa.utils.Utils;
import imgui.ImGui;
import imgui.type.ImBoolean;
import lombok.Getter;
import lombok.SneakyThrows;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author sebs
 */
public class Wizard {
    private static final List<String> commandLog = new ArrayList<>();
    @Getter private static Project currentProject = null;
    public static boolean projectLoaded() { return currentProject != null; }

    // LIB FILE
    private static final Map<String, File> libFiles = new HashMap<>();
    private static String packwizPath;
    private static String gitPath;

    public static void unloadProject() throws IOException {
        // Save main state
        File changesFile = new File(currentProject.dir.getPath()+"/.packwiz_state.txt");
        FileWriter myWriter = new FileWriter(changesFile);
        StringBuilder sb = new StringBuilder();
        sb.append(Main.windowContent.get()).append(",");
        sb.append(Main.windowSettings.get()).append(",");
        sb.append(Main.windowAddContent.get()).append(",");
        sb.append(Main.windowVersion.get());
        myWriter.write(sb.toString());
        myWriter.close();

        // Unload Main
        Main.windowContent.set(false);
        Main.windowSettings.set(false);
        Main.windowAddContent.set(false);
        Main.windowVersion.set(false);

        // Save changelog
        Changelog.save(currentProject.dir);
        currentProject = null;
    }

    public static Window init() {
        // Load lib files
        for(File f : FileUtils.listFilesInFolder(new File(System.getProperty("user.dir") + "/lib/"))) {
            libFiles.put(f.getName(), f);
        }
        packwizPath = libFiles.get("packwiz.exe").getAbsolutePath();

        if(packwizPath.equals("")) { ImGUIUtils.fatalPopup("Cannot find packwiz.exe"); return null; }
        gitPath = logCommand("git --exec-path", null) + "/git.exe";
        if(!new File(gitPath).exists()) { ImGUIUtils.fatalPopup("Cannot find git.exe"); return null; }

        return new CommandLog(Main.windowCommandHistory, commandLog);
    }

    @SneakyThrows
    public static void createProject(String path, String name, String version, String author, String mcVersion, ModLoaders loader, String loaderVersion, ImBoolean initGit) {
        File dir = new File(path);
        dir.mkdirs();

        StringBuilder cmd = new StringBuilder(packwizPath);
        cmd.append(" init -y");
        cmd.append(" --name \"").append(name).append("\"");
        cmd.append(" --version ").append(version);
        cmd.append(" --author \"").append(author).append("\"");
        cmd.append(" --mc-version ").append(mcVersion);
        cmd.append(" --modloader ").append(loader);
        if(loader == ModLoaders.Quilt)
            cmd.append(" --quilt-version ").append(loaderVersion);

        logCommand(cmd.toString(), dir);

        logCommandC(String.format("copy %s %s", libFiles.get(".packwizignore").getAbsolutePath(), dir.getAbsolutePath()), dir);
        if(initGit.get()) {
            logCommandC(String.format("copy %s %s", libFiles.get(".gitignore").getAbsolutePath(), dir.getAbsolutePath()), dir);
            logCommandC(String.format("copy %s %s", libFiles.get(".gitattributes").getAbsolutePath(), dir.getAbsolutePath()), dir);
            logCommand(gitPath + " config --global --add safe.directory '" + dir.getAbsolutePath() + "'", dir);
            logCommand(gitPath + " init .", dir);
            logCommand(gitPath + " add *", dir);
            logCommand(gitPath + " commit -m \"Initial Commit\"", dir);
        }

        if(!new File(path+"/pack.toml").exists()) ImGUIUtils.fatalPopup("Failed to create project");
        else openProject(dir);
    }

    public static void openProject(File dir) throws IOException {
        if(currentProject!=null) {
            try {
                unloadProject();
            } catch (IOException e) {
                ImGUIUtils.errorPopup("Failed to save project to disk");
                return;
            }
        }

        currentProject = new Project(dir);

        // Load main state
        File changesFile = new File(currentProject.dir.getPath()+"/.packwiz_state.txt");
        if(changesFile.exists()) {
            String[] bools = FileUtils.readAllLinesList(FileUtils.loadFile(changesFile.getPath())).get(0).split(",");
            for(int i = 0; i < bools.length; i++) {
                if(i==0) Main.windowContent.set(Boolean.parseBoolean(bools[i]));
                else if(i==1) Main.windowSettings.set(Boolean.parseBoolean(bools[i]));
                else if(i==2) Main.windowAddContent.set(Boolean.parseBoolean(bools[i]));
                else if(i==3) Main.windowVersion.set(Boolean.parseBoolean(bools[i]));
            }
        }
    }

    public static String logCommandC(String command, File dir) {
        return logCommand("cmd.exe /c " + command, dir);
    }

    public static String logCommand(String command, File dir) {
        System.out.println(" $ " + command);
        String out;
        try {
            out = runCommand(command, dir);
        } catch (IOException e) {
            out = ImGUIUtils.stackTrace(e);
        }

        commandLog.add(0, out);
        commandLog.add(0, command);
        return out;
    }

    public static String runCommand(String command, File dir) throws IOException {
        Process process = Runtime.getRuntime().exec(command, null, dir);
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line = "";
        StringBuilder out = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            if(!out.toString().equals("")) out.append("\n"); // This is so it can get the git path
            out.append(line);
        }

        return out.toString();
    }

    public static void updateAll() {

    }

    public static void addVersion(String s) {
        try {
            logCommand("packwiz settings av -a " + s, currentProject.dir);
            currentProject.reloadFromDisk();
        } catch (IOException e) { ImGUIUtils.errorPopup("Failed to reload project"); }
    }

    public static void removeVersion(String s) {
        try {
            logCommand("packwiz settings av -r " + s, currentProject.dir);
            currentProject.reloadFromDisk();
        } catch (IOException e) { ImGUIUtils.errorPopup("Failed to reload project"); }
    }

    private static final Pattern patternProjectName = Pattern.compile("\"(.*?)\"");
    private static final Pattern patternProjectFile = Pattern.compile("\\((.*)\\)");
    public static void install(String s) {
        if(currentProject.getModIds().contains(s)) return;
        try {
            String out = logCommand("packwiz modrinth add --yes --project-id " + s, currentProject.dir);

            for(String line : out.split("\n")) {
                if(!line.startsWith("Dependency") && ! line.startsWith("Project")) continue;
                String name = Utils.getRegexGroup(line, patternProjectName);
                String file = Utils.getRegexGroup(line, patternProjectFile);
                Changelog.change(String.format("Added %s (%s)", name, file));
            }

            currentProject.reloadFromDisk();
        } catch (IOException e) { ImGUIUtils.errorPopup("Failed to reload project"); }
    }

    public static void removeMod(Project.Mod s) {
        if(!currentProject.getModIds().contains(s.id())) ImGUIUtils.errorPopup("Cannot remove mod that is not installed");
        try {
            logCommand("packwiz remove --yes " + s.slug(), currentProject.dir);
            Changelog.change(String.format("Removed %s", s.name()));
            currentProject.reloadFromDisk();
        } catch (IOException e) { ImGUIUtils.errorPopup("Failed to reload project"); }
    }

    public static class CommandLog extends Window {
        private final List<String> history;

        public CommandLog(ImBoolean bool, List<String> history) {
            super(bool);
            this.history = history;
        }

        @Override
        public void draw() {
            for(String log : history) {
                ImGui.text(log);
            }
        }

        @Override
        public String title() {
            return "Command History";
        }
    }
}
