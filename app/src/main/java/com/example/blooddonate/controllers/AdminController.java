package com.example.blooddonate.controllers;

import com.example.blooddonate.callbacks.LoginCallback;
import com.example.blooddonate.services.AdminService;

public class AdminController {
    private AdminService adminService;

    public AdminController adminController;

    public AdminController() {
        this.adminService = new AdminService();
    }

    public void adminLogin(String emai, String password, LoginCallback cb) {
        adminService.adminLogin(emai, password, cb);
    }
}
