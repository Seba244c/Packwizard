package dk.sebsa.screens;

import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;

/**
 * @author sebs
 */
public class Export extends Window {

    public Export(ImBoolean bool) {
        super(bool);
    }

    @Override
    public void draw() {

    }

    @Override
    public String title() {
        return "Export...";
    }
}
