package com.example.demo;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api")
public class CompilerController {

    @PostMapping("/compile")
    public ResponseEntity<String> compileCode(@RequestBody String code) {
        String fileName = "Main.java";
        try {
            Files.write(Paths.get(fileName), code.getBytes());
            Process compileProcess = new ProcessBuilder("javac", fileName).start();
            compileProcess.waitFor();

            if (compileProcess.exitValue() == 0) {
                Process runProcess = new ProcessBuilder("java", "Main").start();
                String output = new String(runProcess.getInputStream().readAllBytes());
                return ResponseEntity.ok(output);
            } else {
                String errors = new String(compileProcess.getErrorStream().readAllBytes());
                return ResponseEntity.badRequest().body(errors);
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Compilation Error: " + e.getMessage());
        } finally {
            try {
                Files.deleteIfExists(Paths.get(fileName));
                Files.deleteIfExists(Paths.get("Main.class"));
            } catch (IOException ignored) {
            }
        }
    }
}
