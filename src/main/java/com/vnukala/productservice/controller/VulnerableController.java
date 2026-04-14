package com.vnukala.productservice.controller;

import java.sql.*;
import org.springframework.web.bind.annotation.*;

@RestController
public class VulnerableController {

    @GetMapping("/test")
    public String test(@RequestParam String name) throws Exception {

        Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/test", "root", "password");

        Statement stmt = conn.createStatement();

        // ❌ SQL Injection vulnerability
        String query = "SELECT * FROM products WHERE name = '" + name + "'";

        ResultSet rs = stmt.executeQuery(query);

        return "Executed";
    }
}

