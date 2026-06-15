package com.recipebook.controller;

import com.recipebook.dto.RecipeDTO;
import com.recipebook.service.RecipeService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/")
public class RecipeController {

    private final RecipeService recipeService;

    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }
    @GetMapping
    public String index(Model model) {
        model.addAttribute("recipes", recipeService.getAllRecipes());
        model.addAttribute("categories", recipeService.getAllCategories());
        model.addAttribute("newRecipe", new RecipeDTO(null, "", "", "", "", null, ""));
        return "recipes";
    }

    @PostMapping("/recipes/add")
    public String addRecipe(@ModelAttribute("newRecipe") RecipeDTO recipeDTO) {
        recipeService.createRecipe(recipeDTO);
        return "redirect:/";
    }

    @GetMapping("/recipes/delete/{id}")
    public String deleteRecipe(@PathVariable("id") Long id) {
        recipeService.deleteRecipe(id);
        return "redirect:/";
    }
}