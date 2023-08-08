package dk.sebsa.screens;

import dk.sebsa.Project;
import dk.sebsa.Wizard;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiTableFlags;
import imgui.type.ImBoolean;

/**
 * @author sebs
 */
public class Content extends Window {
    public Content(ImBoolean bool) {
        super(bool);
    }

    @Override
    public void draw() {
        if(ImGui.button("Update All")) {
            Wizard.updateAll();
        }

        if(ImGui.beginTable("##", 4, ImGuiTableFlags.ScrollY | ImGuiTableFlags.Borders | ImGuiTableFlags.RowBg)) {
            ImGui.tableSetupColumn("Name");
            ImGui.tableSetupColumn("Side");
            ImGui.tableSetupColumn("Filename");
            ImGui.tableSetupColumn("Action");
            ImGui.tableHeadersRow();

            for(Project.Mod mod : Wizard.currentProject.getMods()) {
                ImGui.tableNextColumn();
                ImGui.text(mod.name());
                ImGui.tableNextColumn();
                ImGui.text(mod.side());
                ImGui.tableNextColumn();
                ImGui.text(mod.filename());
                ImGui.tableNextColumn();
                if(ImGui.button("Remove##"+mod.id())) {
                    Wizard.removeMod(mod);
                }
            }

            ImGui.endTable();
        }
    }

    @Override
    public String title() {
        return "Installed Content";
    }
}
