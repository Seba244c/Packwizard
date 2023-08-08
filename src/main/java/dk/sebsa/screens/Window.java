package dk.sebsa.screens;

import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;

/**
 * @author sebs
 */
public abstract class Window {
    protected final ImBoolean bool;
    public Window(ImBoolean bool) {
        this.bool = bool;
    }

    public void render() {
        if(!bool.get()) return;
        ImGui.begin(title(), bool);
        draw();
        ImGui.end();
    }

    public abstract void draw();
    public abstract String title();
}
