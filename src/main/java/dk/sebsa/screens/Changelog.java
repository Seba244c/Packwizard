package dk.sebsa.screens;

import dk.sebsa.utils.FileUtils;
import dk.sebsa.utils.Logger;
import imgui.ImGui;
import imgui.type.ImBoolean;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author sebs
 */
public class Changelog {
    public static List<String> changes;

    public static void load(File dir) throws IOException {
        Logger.log("Loading changelog");
        File changesFile = new File(dir.getPath()+"/.packwiz_changes.txt");

        if(changesFile.exists()) {
            Logger.log("Loading previous changelog");
            changes = FileUtils.readAllLinesList(FileUtils.loadFile(dir.getPath()+"/.packwiz_changes.txt"));
        }
        else { Logger.log("New change file created"); changes = new ArrayList<>(); changes.add("# Changes"); }
    }

    public static void save(File dir) throws IOException {
        Logger.log("Saving changes");
        File changesFile = new File(dir.getPath()+"/.packwiz_changes.txt");
        FileWriter myWriter = new FileWriter(changesFile);
        StringBuilder sb = new StringBuilder();

        for(String s : changes) {
            sb.append(s).append("\n");
        }

        myWriter.write(sb.toString());
        myWriter.close();
    }

    public static void change(String change) {
        Logger.log("CHANGE: " + change);
        changes.add("* " + change);
    }

    public static class ChangelogWindow extends Window {
        public ChangelogWindow(ImBoolean bool) {
            super(bool);
        }

        @Override
        public void draw() {
            for(String change : Changelog.changes) {
                ImGui.text(change);
            }
        }

        @Override
        public String title() {
            return "Changelog";
        }
    }
}
