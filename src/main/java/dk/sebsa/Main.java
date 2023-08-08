package dk.sebsa;

import dk.sebsa.screens.*;
import dk.sebsa.utils.ImGUIUtils;
import dk.sebsa.utils.Utils;
import imgui.ImGui;
import imgui.app.Application;
import imgui.app.Configuration;
import imgui.type.ImBoolean;
import lombok.SneakyThrows;
import org.lwjgl.glfw.GLFW;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author sebs
 */
public class Main extends Application {
    public static List<Window> windows = new ArrayList<>();
    private Welcome welcomeScreen;
    public final ImBoolean createProject = new ImBoolean(false);

    @Override
    protected void preRun() {
        super.preRun();
        welcomeScreen = new Welcome(createProject);
        Window wizardInitAndWindow = Wizard.init();
        if(wizardInitAndWindow!=null) windows.add(wizardInitAndWindow);
        windows.add(new Versioning(windowVersion));
        windows.add(new AddContent(windowAddContent));
        windows.add(new CreateProject(createProject));
        windows.add(new Content(windowContent));
        windows.add(new Settings(windowSettings));
    }

    @Override
    protected void configure(Configuration config) {
        config.setTitle("Packwizard");
        config.setWidth(900);
        config.setHeight(650);
    }

    public static ImBoolean windowCommandHistory = new ImBoolean(true);
    public static ImBoolean windowVersion = new ImBoolean(false);
    public static ImBoolean windowContent = new ImBoolean(false);
    public static ImBoolean windowAddContent = new ImBoolean(false);
    public static ImBoolean windowSettings = new ImBoolean(false);

    @Override
    public void process() {
        // Welcome state
        if(!Wizard.projectLoaded()) welcomeScreen.render();

        // Menubar
        ImGui.beginMainMenuBar();
        if(ImGui.beginMenu("File")) {
            if(ImGui.menuItem("Open Another Project...")) {
                try {
                    Wizard.openProject(new File(Utils.pickFolderDialog()));
                } catch (IOException e) {
                    ImGUIUtils.errorPopup("Failed to open project");
                }
            }

            if(ImGui.menuItem("Create a new Project")) createProject.set(true);
            if(ImGui.menuItem("Close Project")) try { Wizard.unloadProject(); } catch (IOException e) {
                ImGUIUtils.errorPopup("Failed to save project to disk");
            }
            if(ImGui.menuItem("Quit")) quit();
            ImGui.endMenu();
        }

        if(Wizard.projectLoaded()) {
            if(ImGui.beginMenu("Windows")) {
                ImGui.menuItem("Versioning & Export", null, windowVersion);
                ImGui.menuItem("Command History", null, windowCommandHistory);
                ImGui.menuItem("Install Content", null, windowAddContent);
                ImGui.menuItem("Installed Content", null, windowContent);
                ImGui.menuItem("Settings", null, windowSettings);
                ImGui.endMenu();
            }

            ImGui.text("Editing " + Wizard.getCurrentProject().getName() + ", by " + Wizard.getCurrentProject().getAuthor());
        }
        ImGui.endMainMenuBar();

        // Render all screens
        for(Window w : windows) {
            w.render();
        }
        ImGUIUtils.renderPopups(this);
    }

    public void quit() {
        GLFW.glfwSetWindowShouldClose(handle, true);
    }

    public static void main(String[] args) {
        launch(new Main());
    }

    @SneakyThrows
    @Override
    protected void postRun() {
        if(Wizard.projectLoaded()) { Wizard.unloadProject(); }
    }
}