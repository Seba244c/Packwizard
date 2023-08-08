package dk.sebsa.screens;

import dk.sebsa.Project;
import dk.sebsa.Wizard;
import dk.sebsa.utils.APIUtils;
import dk.sebsa.utils.ImGUIUtils;
import imgui.ImGui;
import imgui.flag.ImGuiTableFlags;
import imgui.type.ImBoolean;
import imgui.type.ImString;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * @author sebs
 */
public class AddContent extends Window {
    private final ImString searchInput = new ImString();
    // Query data
    private JSONObject query;
    private String countString;
    private int page = 0;
    // End

    public AddContent(ImBoolean bool) {
        super(bool);
    }

    @Override
    public void draw() {
        ImGui.inputText("##", searchInput);
        ImGui.sameLine();
        ImGui.beginDisabled(searchInput.getLength()<2);
        if(ImGui.button("Search")) {
            page = 0;
            query();
        }
        ImGui.endDisabled();

        if(query != null) {
            int total = query.getInt("total_hits");
            // Page
            ImGui.beginDisabled(page<1);
            if(ImGui.button("<-")) {
                page -= 1; query();
            } ImGui.endDisabled();

            ImGui.sameLine();
            ImGui.text(countString);
            ImGui.sameLine();

            ImGui.beginDisabled(page*5+5 > total);
            if(ImGui.button("->")) {
                page += 1; query();
            } ImGui.endDisabled();

            // Table
            if(ImGui.beginTable("##", 4, ImGuiTableFlags.ScrollY | ImGuiTableFlags.Borders | ImGuiTableFlags.RowBg)) {
                ImGui.tableSetupColumn("Actions");
                ImGui.tableSetupColumn("Name");
                ImGui.tableSetupColumn("Author");
                ImGui.tableSetupColumn("Description");
                ImGui.tableHeadersRow();

                for(Object hitObject : query.getJSONArray("hits")) {
                    JSONObject hit = (JSONObject) hitObject;
                    String queryProjectId = hit.getString("project_id");

                    // Install
                    ImGui.tableNextColumn();
                    ImGui.beginDisabled(Wizard.getCurrentProject().getModIds().contains(queryProjectId));
                    if(ImGui.button("Details##"+ queryProjectId)) {
                        ImGUIUtils.errorPopup("Coming soon (TM)");
                    } ImGui.sameLine();
                    if(ImGui.button("Install##"+ queryProjectId)) {
                        Wizard.install(queryProjectId);
                    }
                    ImGui.endDisabled();

                    // Name
                    ImGui.tableNextColumn();
                    ImGui.text(hit.getString("title"));

                    /// Author
                    ImGui.tableNextColumn();
                    ImGui.text(hit.getString("author"));

                    /// Desc
                    ImGui.tableNextColumn();
                    ImGui.text(hit.getString("description"));
                }

                ImGui.endTable();
            }
        }
    }

    private void query() {
        try {
            query = APIUtils.labrinthSearchProjects(searchInput.get(), page*5, Wizard.getCurrentProject().getMcVersions().toArray(new String[0]), Wizard.getCurrentProject().getLoader());
            countString = "Showing " + (query.getJSONArray("hits").length() + page*5) + " out of " + query.getInt("total_hits");
        } catch (Exception e) { ImGUIUtils.errorPopup(e.getClass().getName() + " when getting search results:", e); }
    }

    @Override
    public String title() {
        return "Add Content";
    }
}
