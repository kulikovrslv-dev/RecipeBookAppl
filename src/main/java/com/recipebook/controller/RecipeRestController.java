package com.recipebook.controller;

import com.recipebook.dto.RecipeDTO;
import com.recipebook.service.ParserService;
import com.recipebook.service.RecipeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api")
public class RecipeRestController {

    private final RecipeService recipeService;
    private final ParserService parserService;

    public RecipeRestController(RecipeService recipeService, ParserService parserService) {
        this.recipeService = recipeService;
        this.parserService = parserService;
    }

    @GetMapping("/recipes")
    public List<RecipeDTO> getAllRecipes() {
        return recipeService.getAllRecipes();
    }

    @GetMapping("/recipes/{id}")
    public ResponseEntity<RecipeDTO> getRecipeById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(recipeService.getRecipeById(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/recipes")
    public ResponseEntity<RecipeDTO> createRecipe(@RequestBody RecipeDTO dto) {
        RecipeDTO created = recipeService.createRecipe(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/recipes/{id}")
    public ResponseEntity<RecipeDTO> updateRecipe(@PathVariable Long id, @RequestBody RecipeDTO dto) {
        try {
            RecipeDTO updated = recipeService.updateRecipe(id, dto);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/recipes/{id}")
    public ResponseEntity<Void> deleteRecipe(@PathVariable Long id) {
        try {
            recipeService.deleteRecipe(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/parser/import")
    public ResponseEntity<String> triggerParser() {
        int count = parserService.parseAndImportRecipes();
        return ResponseEntity.ok("Успішно імпортовано через Jsoup: " + count);
    }
}