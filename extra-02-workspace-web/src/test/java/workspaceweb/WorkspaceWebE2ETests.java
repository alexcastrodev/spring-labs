package workspaceweb;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
class WorkspaceWebE2ETests {

    @Autowired
    private MockMvc mvc;

    private final ObjectMapper json = new ObjectMapper();

    @Test
    void fullLifecycle_create_add_suspend_remove() throws Exception {
        Long userId = createUser("Alice");
        Long workspaceId = createWorkspace("Acme");

        // initially: no memberships, not on search
        mvc.perform(get("/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.onSearch").value(false))
                .andExpect(jsonPath("$.memberships.length()").value(0));

        // add member -> ACTIVE, on search
        mvc.perform(post("/workspaces/{w}/members/{u}", workspaceId, userId))
                .andExpect(status().isNoContent());

        mvc.perform(get("/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.onSearch").value(true))
                .andExpect(jsonPath("$.memberships.length()").value(1))
                .andExpect(jsonPath("$.memberships[0].workspaceId").value(workspaceId))
                .andExpect(jsonPath("$.memberships[0].status").value("ACTIVE"));

        // suspend -> SUSPENDED row remains, off search
        mvc.perform(post("/workspaces/{w}/members/{u}/suspend", workspaceId, userId))
                .andExpect(status().isNoContent());

        mvc.perform(get("/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.onSearch").value(false))
                .andExpect(jsonPath("$.memberships.length()").value(1))
                .andExpect(jsonPath("$.memberships[0].status").value("SUSPENDED"));

        // re-add reactivates
        mvc.perform(post("/workspaces/{w}/members/{u}", workspaceId, userId))
                .andExpect(status().isNoContent());

        mvc.perform(get("/users/{id}", userId))
                .andExpect(jsonPath("$.onSearch").value(true))
                .andExpect(jsonPath("$.memberships[0].status").value("ACTIVE"));

        // remove -> hard delete, off search, no memberships
        mvc.perform(delete("/workspaces/{w}/members/{u}", workspaceId, userId))
                .andExpect(status().isNoContent());

        mvc.perform(get("/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.onSearch").value(false))
                .andExpect(jsonPath("$.memberships.length()").value(0));
    }

    @Test
    void getUnknownUser_returns404() throws Exception {
        mvc.perform(get("/users/{id}", 999_999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void createUserReturnsLocationHeader() throws Exception {
        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fullName\":\"Bob\"}"))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));
    }

    @Test
    void blankFullNameMakesUserOffSearchEvenWithActiveMembership() throws Exception {
        Long userId = createUser("");
        Long workspaceId = createWorkspace("Acme");

        mvc.perform(post("/workspaces/{w}/members/{u}", workspaceId, userId))
                .andExpect(status().isNoContent());

        mvc.perform(get("/users/{id}", userId))
                .andExpect(jsonPath("$.onSearch").value(false))
                .andExpect(jsonPath("$.memberships[0].status").value("ACTIVE"));
    }

    private Long createUser(String fullName) throws Exception {
        MvcResult result = mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fullName\":\"" + fullName + "\"}"))
                .andExpect(status().isCreated())
                .andReturn();
        JsonNode node = json.readTree(result.getResponse().getContentAsString());
        return node.get("id").asLong();
    }

    private Long createWorkspace(String name) throws Exception {
        MvcResult result = mvc.perform(post("/workspaces")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"" + name + "\"}"))
                .andExpect(status().isCreated())
                .andReturn();
        JsonNode node = json.readTree(result.getResponse().getContentAsString());
        return node.get("id").asLong();
    }
}
