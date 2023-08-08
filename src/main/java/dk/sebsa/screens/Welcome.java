package dk.sebsa.screens;

import dk.sebsa.Wizard;
import dk.sebsa.utils.ImGUIUtils;
import dk.sebsa.utils.Utils;
import imgui.ImGui;
import imgui.type.ImBoolean;

import java.io.File;
import java.io.IOException;

/**
 * @author sebs
 */
public class Welcome extends Window {
    private final ImBoolean createProject;
    private boolean disabled;

    public Welcome(ImBoolean createProject) {
        super(new ImBoolean(true));
        this.createProject = createProject;
    }

    @Override
    public void draw() {
        // Welcome screen
        ImGui.text("Packwizard");
        ImGui.text("By Seba244c");

        if(createProject.get()) { ImGui.beginDisabled(); disabled = true; }
        if(ImGui.button("Open Project")) {
            try {
                Wizard.openProject(new File(Utils.pickFolderDialog()));
            } catch (IOException e) {
                ImGUIUtils.errorPopup("Failed to open project");
            }
        }

        if(ImGui.button("Create Project")) createProject.set(true);

        if(disabled) { ImGui.endDisabled(); disabled = false; }
    }

    @Override
    public String title() {
        return "Welcome";
    }

    @Override
    public void render() {
        ImGui.begin(title());
        draw();
        ImGui.end();
    }
}
