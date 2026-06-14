package com.recipebook.dto;


public record RecipeDTO(
        Long id,
        String title,
        String description,
        String ingredients,
        String instructions,
        Long categoryId,
        String categoryName
) {}