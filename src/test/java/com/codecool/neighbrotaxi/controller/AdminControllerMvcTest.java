package com.codecool.neighbrotaxi.controller;

import com.codecool.neighbrotaxi.NeighBroTaxiApplicationTests;
import com.codecool.neighbrotaxi.model.entities.Role;
import com.codecool.neighbrotaxi.model.entities.User;
import com.codecool.neighbrotaxi.service.AdminService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@Transactional
@MockBean(AdminService.class)
public class AdminControllerMvcTest extends NeighBroTaxiApplicationTests {
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private AdminService adminServiceMock;

    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

    }

    @Test
    public void home() throws Exception {
        mockMvc.perform(get("/admin/home"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin_page"));
    }

    @Test
    public void getAllUsers_NoUser_ShouldNotAddAttributeToModel() throws Exception {
        when(adminServiceMock.getAllUser()).thenReturn(null);

        mockMvc.perform(get("/admin/users"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin_users"))
                .andExpect(model().size(0));
    }

    @Test
    public void getAllUsers_ThereAreUsers_ShouldAddValidAttributeToModel() throws Exception {
        ArrayList<User> users = new ArrayList<>(Arrays.asList(new User(), new User()));
        when(adminServiceMock.getAllUser()).thenReturn(users);

        mockMvc.perform(get("/admin/users"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin_users"))
                .andExpect(model().attribute("user_list", users));
    }

    @Test
    public void getAllRoles_NoRole_ShouldNotAddAttributeToModel() throws Exception {
        when(adminServiceMock.getAllRole()).thenReturn(null);

        mockMvc.perform(get("/admin/roles"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin_roles"))
                .andExpect(model().size(0));
    }

    @Test
    public void getAllRoles_ThereAreRoles_ShouldAddValidAttributeToModel() throws Exception {
        ArrayList<Role> roles = new ArrayList<>(Arrays.asList(new Role(), new Role()));
        when(adminServiceMock.getAllRole()).thenReturn(roles);

        mockMvc.perform(get("/admin/roles"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin_roles"))
                .andExpect(model().attribute("role_list", roles));
    }

    @Test
    public void deleteUser_RedirectToValidUrl() throws Exception {
        doNothing().when(adminServiceMock).deleteUser(anyInt());

        mockMvc.perform(delete("/admin/user/delete/{userID}", 1))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/users"));
    }

    @Test
    public void deleteUser_CallDeleteUserMethod() throws Exception {
        doNothing().when(adminServiceMock).deleteUser(anyInt());

        mockMvc.perform(delete("/admin/user/delete/{userID}", 1));

        verify(adminServiceMock, times(1)).deleteUser(1);
        verifyZeroInteractions(adminServiceMock);
    }

    @Test
    public void addRole_RoleAlreadyInDb_ShouldRedirectToValidUrl() throws Exception {
        Role role = new Role();
        role.setName("NAME");
        ArrayList<Role> roles = new ArrayList<>(Arrays.asList(role));
        when(adminServiceMock.getAllRole()).thenReturn(roles);

        mockMvc.perform(post("/admin/add-role")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("name", "name"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/roles"));
    }

    @Test
    public void addRole_RoleAlreadyInDb_ShouldNotCallAddRoleMethod() throws Exception {
        Role role = new Role();
        role.setName("NAME");
        ArrayList<Role> roles = new ArrayList<>(Arrays.asList(role));
        when(adminServiceMock.getAllRole()).thenReturn(roles);

        mockMvc.perform(post("/admin/add-role")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("name", "name"));

        verify(adminServiceMock, never()).addRole(any());
    }

    @Test
    public void addRole_RoleIsNotInDb_ShouldRedirectToValidUrl() throws Exception {
        ArrayList<Role> roles = new ArrayList<>(Arrays.asList(new Role()));
        when(adminServiceMock.getAllRole()).thenReturn(roles);

        mockMvc.perform(post("/admin/add-role")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("name", "name"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/roles"));
    }

    @Test
    public void addRole_RoleIsNotInDb_ShouldCallAddRoleMethod() throws Exception {
        ArrayList<Role> roles = new ArrayList<>(Arrays.asList(new Role()));
        when(adminServiceMock.getAllRole()).thenReturn(roles);

        mockMvc.perform(post("/admin/add-role")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("name", "name"));

        verify(adminServiceMock, times(1)).addRole(any());
    }

    @Test
    public void deleteRole_AdminServiceDeleteRoleReturnsFalse_AddErrorMessageIntoModelAttr() throws Exception {
        when(adminServiceMock.deleteRole(any())).thenReturn(false);

        mockMvc.perform(delete("/admin/role/delete/{roleId}", 1))
                .andExpect(status().isOk())
                .andExpect(view().name("admin_roles"))
                .andExpect(model().attribute("error", "Cannot delete admin or user roles"));
    }

    @Test
    public void deleteRole_AdminServiceDeleteRoleReturnsTrue_ShouldNotAddAnythingIntoModel() throws Exception {
        when(adminServiceMock.deleteRole(anyInt())).thenReturn(true);

        mockMvc.perform(delete("/admin/role/delete/{roleId}", 1))
                .andExpect(status().isOk())
                .andExpect(view().name("admin_roles"))
                .andExpect(model().attributeDoesNotExist("error"));
    }
}
