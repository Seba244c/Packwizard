package dk.sebsa.screens;

import dk.sebsa.Wizard;
import imgui.ImGui;
import imgui.type.ImBoolean;
import imgui.type.ImInt;
import imgui.type.ImString;

import java.util.List;

/**
 * @author sebs
 */
public class Settings extends Window {
    private final ImInt curentlySelectedVersion = new ImInt(-1);
    private final ImString inputVersion = new ImString();

    public Settings(ImBoolean bool) {
        super(bool);
    }

    @Override
    public void draw() {
        ImGui.text("Acceptable Versions");
        ImGui.inputText("##", inputVersion);
        List<String> mcVersions = Wizard.getCurrentProject().getMcVersions();

        ImGui.beginDisabled(!inputVersion.get().matches("[0-9](\\.[0-9]+)+") || mcVersions.contains(inputVersion.get()));
        if(ImGui.button("Add")) {
            Wizard.addVersion(inputVersion.get());
            inputVersion.set("");
        }
        ImGui.endDisabled();

        if(mcVersions.size() > 1) {
            ImGui.sameLine();
            if(ImGui.button("Remove Selected")) {
                Wizard.removeVersion(mcVersions.get(curentlySelectedVersion.get()));
                curentlySelectedVersion.set(-1);
            }
        }

        ImGui.listBox("##", curentlySelectedVersion, mcVersions.toArray(new String[0]));
    }

    @Override
    public String title() {
        return "Settings";
    }
}
