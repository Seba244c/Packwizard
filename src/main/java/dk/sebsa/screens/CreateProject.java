package dk.sebsa.screens;

import dk.sebsa.Wizard;
import dk.sebsa.enums.ModLoaders;
import dk.sebsa.loader.LoaderAPI;
import dk.sebsa.utils.Utils;
import imgui.ImGui;
import imgui.flag.ImGuiInputTextFlags;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;
import imgui.type.ImInt;
import imgui.type.ImString;

/**
 * @author sebs
 */
public class CreateProject extends Window {
    private final ImString browseString = new ImString();
    private final ImString projectName = new ImString("Unnamed Project");
    private final ImString author = new ImString("Me!");
    private final ImString startingVersion = new ImString("1.0.0");
    private final ImInt modLoader = new ImInt(-1);
    private final ImString mcVersion = new ImString("1.20.1");
    private final ImInt loaderVerison = new ImInt(0);
    private final ImBoolean initGit = new ImBoolean(true);

    public CreateProject(ImBoolean bool) {
        super(bool);
    }

    @Override
    public void draw() {
        // Filepath
        if (ImGui.button("Browse")) {
            browseString.set(Utils.pickFolderDialog());
        }
        ImGui.sameLine();
        ImGui.inputText("Folder Path", browseString, ImGuiInputTextFlags.CallbackResize);
        // Other project info
        ImGui.inputText("Project Name", projectName, ImGuiInputTextFlags.CallbackResize);
        ImGui.inputText("Project Version", startingVersion, ImGuiInputTextFlags.CallbackResize);
        ImGui.inputText("Author", author, ImGuiInputTextFlags.CallbackResize);
        ImGui.inputText("MC Version", mcVersion);
        ImGui.combo("ModLoader", modLoader, Utils.enumStrings(ModLoaders.class));

        if (modLoader.intValue() > -1) {
            ModLoaders modLoaderD = ModLoaders.values()[modLoader.get()];
            String[] loaderVersions = LoaderAPI.getApi(modLoaderD).loaderVersions();
            ImGui.combo("Loader Version", loaderVerison, loaderVersions);
            ImGui.checkbox("Init Git", initGit);
            if (ImGui.button("Create!")) {
                Wizard.createProject(browseString.get(), projectName.get(), startingVersion.get(), author.get(), mcVersion.get(), modLoaderD, loaderVersions[loaderVerison.get()], initGit);
                bool.set(false);
            }
        }
    }

    @Override
    public String title() {
        return "Create a project!";
    }
}
