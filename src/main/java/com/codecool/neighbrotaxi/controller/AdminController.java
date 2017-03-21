package com.codecool.neighbrotaxi.controller;


import com.codecool.neighbrotaxi.model.entities.Role;
import com.codecool.neighbrotaxi.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Controller
@CrossOrigin
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    /**
     * This route is the home route of the admin UI. After a successful login the server redirects here.
     * @return The name of the html template to render.
     */
    @RequestMapping(value = "/home", method = RequestMethod.GET)
    public String home() {
        return "admin_page";
    }

    /**
     * This route add all users from the database into the Model object, then returns the renderable templates name.
     * @param model Its a Model object which is autowired automatically by the spring,
     *              and it is passed to the rendering process, and it can use its stored variables.
     * @return the name of the template to render in String.
     */
    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public String getAllUsers(Model model) {
        if (adminService.getAllUser() == null) return "admin_users";
        if (adminService.getAdminUser() == null) return "admin_users";
        model.addAttribute("user_list", adminService.getAllUser());
        model.addAttribute("role_list", adminService.getAllRole());
        model.addAttribute("admin_list", adminService.getAdminUser());
        return "admin_users";
    }

    /**
     * The route adds all users from the database into the Model object, then returns a renderable template's name.
     * @param model it is a Model object which is autowired automatically by Spring, and is passed to the rendering process,
     *              and it can use its stored variables.
     * @return the name of the template.
     */
    @RequestMapping(value = "/roles", method = RequestMethod.GET)
    public String getAllRoles(Model model) {
        if (adminService.getAllRole() == null) return "admin_roles";
        model.addAttribute("role_list", adminService.getAllRole());
        return "admin_roles";
    }

    /**
     * Delete a user from the database with the adminService's deleteUser method.
     * @param userID Its the Id of the user in string given in the url, as path variable.
     * @return A String, and with it the spring redirect to the /admin/users route.
     */
    @RequestMapping(value = "/user/delete/{userID}", method = RequestMethod.DELETE)
        public String deleteUser(@PathVariable(value = "userID") String userID) {
        ResponseEntity<String> response = null;
        adminService.deleteUser(Integer.parseInt(userID));
        response = new ResponseEntity<String>(HttpStatus.NO_CONTENT);
        return "redirect:/admin/users";
    }

    /**
     * Add a role to the database with the adminService's addRole method.
     * @param name It is the name of the role that the admin wants to create, as a path variable.
     * @return As string, which is a redirect and is a route to /admin/roles.
     */
    @RequestMapping(value = "/add-role", method = RequestMethod.POST)
    public String addRole(@RequestParam(value = "name") String name) {
        for (Role role : adminService.getAllRole()) {
            if (Objects.equals(role.getName(), name.toUpperCase())) return "redirect:/admin/roles";
        }

        Role newRole = new Role();
        newRole.setName(name.toUpperCase());
        adminService.addRole(newRole);
        return "redirect:/admin/roles";
    }

    /**
     * Delete a role from the database with the adminService's deleteRole method.
     * @param roleID Its the Id of a role in string given in the url, as path variable.
     * @return A String, and with it the spring redirect to the /admin/roles route.
     */
    @RequestMapping(value = "/role/delete/{roleID}", method = RequestMethod.DELETE)
    public String deleteRole(@PathVariable(value = "roleID") String roleID, Model model) {
        if (!adminService.deleteRole(Integer.parseInt(roleID))) {
            System.out.println("inIF");
            model.addAttribute("error", "Cannot delete admin or user roles");
        }
        return getAllRoles(model);
    }

    @RequestMapping(value = "/user/role/add/{userID}", method = RequestMethod.POST)
    public String addRoleToUser(
            @PathVariable("userID") String userID,
            @RequestParam(value = "id", required=false) List<String>id) {
        Set<Role> roles = new HashSet<>();
        if (id == null) {
            adminService.addRoleToUser(roles, Integer.parseInt(userID));
            return "redirect:/admin/users";
        }

        for (Role role : adminService.getAllRole()) {
            for (String roleId : id) {
                if (role.getId().toString().equals(roleId)) roles.add(role);
                roles.forEach(System.out::println);
            }
        }

        for (String roleId : id) {
            for (Role role : adminService.getAllRole())
            if (roleId.equals(role.getId().toString())) {
                adminService.addRoleToUser(roles, Integer.parseInt(userID));
            }
        }
        return "redirect:/admin/users";
    }
}