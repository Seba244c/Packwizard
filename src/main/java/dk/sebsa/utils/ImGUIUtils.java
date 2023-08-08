package dk.sebsa.utils;

import dk.sebsa.Main;
import imgui.ImGui;
import imgui.flag.ImGuiPopupFlags;
import imgui.flag.ImGuiWindowFlags;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @author sebs
 */
public class ImGUIUtils {
    public static String fatalError = "";
    public static String error = "";

    public static void fatalPopup(String error) {
        fatalError = error;
    }

    public static void fatalPopup(String error, Exception e) {
        fatalError = error + "\n" + stackTrace(e);
    }

    public static void errorPopup(String errorIn) {
        error = errorIn;
    }

    public static void errorPopup(String errorIn, Exception e) {
        error = errorIn + "\n" + stackTrace(e);
    }

    public static String stackTrace(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }

    public static void renderPopups(Main app) {
        if(ImGui.beginPopupModal("FatalError")) {
            ImGui.text("A fatal error has occured: ");
            ImGui.text(fatalError);

            if(ImGui.button("Quit")) app.quit();

            ImGui.endPopup();
        } else if(!fatalError.equals("")) ImGui.openPopup("FatalError");
        if(ImGui.beginPopupModal("Error")) {
            ImGui.text("An error has occured: ");
            ImGui.text(error);

            if(ImGui.button("Ok")) { error = ""; ImGui.closeCurrentPopup(); }
            ImGui.sameLine();
            if(ImGui.button("Quit")) app.quit();

            ImGui.endPopup();
        } else if(!error.equals("")) ImGui.openPopup("Error");
    }
}
