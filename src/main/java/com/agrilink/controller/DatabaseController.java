package com.agrilink.controller;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/public/db")
public class DatabaseController {
    private final JdbcTemplate jdbc;

    public DatabaseController(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @GetMapping("/tables")
    public List<String> listTables() {
        return List.of("users", "analysis_history", "buyers", "crop_listings", "transport_requests", "rescue_requests");
    }

    @GetMapping("/query")
    public Map<String, Object> queryTable(@RequestParam(defaultValue = "users") String table) {
        String safeTable = switch (table.toLowerCase()) {
            case "analysis_history" -> "analysis_history";
            case "buyers" -> "buyers";
            case "crop_listings" -> "crop_listings";
            case "transport_requests" -> "transport_requests";
            case "rescue_requests" -> "rescue_requests";
            default -> "users";
        };
        try {
            List<Map<String, Object>> rows = jdbc.queryForList("SELECT * FROM " + safeTable + " ORDER BY id DESC LIMIT 100");
            return Map.of("success", true, "table", safeTable, "rows", rows, "count", rows.size());
        } catch (Exception e) {
            return Map.of("success", false, "error", e.getMessage() == null ? "Query failed" : e.getMessage());
        }
    }

    @PostMapping("/sql")
    public Map<String, Object> runSql(@RequestBody Map<String, String> req) {
        String sql = req.get("sql");
        if (sql == null || !sql.trim().toLowerCase().startsWith("select")) {
            return Map.of("success", false, "error", "Only SELECT queries are permitted.");
        }
        try {
            List<Map<String, Object>> rows = jdbc.queryForList(sql);
            return Map.of("success", true, "rows", rows, "count", rows.size());
        } catch (Exception e) {
            return Map.of("success", false, "error", e.getMessage() == null ? "SQL Execution Error" : e.getMessage());
        }
    }
}
