package dk.sebsa.screens;

import dk.sebsa.Wizard;
import dk.sebsa.utils.ImGUIUtils;
import imgui.ImGui;
import imgui.type.ImBoolean;
import imgui.type.ImString;

/**
 * @author sebs
 */
public class Versioning extends Window {
    private final Changelog.ChangelogWindow changelogWindow;
    private final ImBoolean changelog = new ImBoolean(false);
    public Versioning(ImBoolean bool) {
        super(bool);
        changelogWindow = new Changelog.ChangelogWindow(changelog);
    }

    @Override
    public void draw() {
        ImGui.text("Working on: " + Wizard.currentProject.getVersion());
        ImGui.inputText("##", new ImString("1.0.0"));
        ImGui.sameLine();
        if(ImGui.button("Set")) {
            ImGUIUtils.errorPopup("Functionality not implemented!");
        }

        if(ImGui.button("Export...")) {
            ImGUIUtils.errorPopup("Functionality not implemented!");
        }
        ImGui.sameLine();
        if(ImGui.button("Changelog")) changelog.set(!changelog.get());

        if(changelog.get()) changelogWindow.render();
    }

    @Override
    public String title() {
        return "Versioning";
    }
}
