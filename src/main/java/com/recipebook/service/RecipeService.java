package com.recipebook.service;

import com.recipebook.dto.CategoryDTO;
import com.recipebook.dto.RecipeDTO;
import com.recipebook.entity.Category;
import com.recipebook.entity.Recipe;
import com.recipebook.repository.CategoryRepository;
import com.recipebook.repository.RecipeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final CategoryRepository categoryRepository;

    public RecipeService(RecipeRepository recipeRepository, CategoryRepository categoryRepository) {
        this.recipeRepository = recipeRepository;
        this.categoryRepository = categoryRepository;
    }


    @Transactional(readOnly = true)
    public List<CategoryDTO> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(cat -> new CategoryDTO(cat.getId(), cat.getName(), cat.getDescription()))
                .collect(Collectors.toList());
    }

    @Transactional
    public CategoryDTO createCategory(CategoryDTO dto) {
        Category category = new Category(dto.name(), dto.description());
        Category saved = categoryRepository.save(category);
        return new CategoryDTO(saved.getId(), saved.getName(), saved.getDescription());
    }


    @Transactional(readOnly = true)
    public List<RecipeDTO> getAllRecipes() {
        return recipeRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public RecipeDTO getRecipeById(Long id) {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Рецепт не знайдено з ID: " + id));
        return convertToDTO(recipe);
    }

    @Transactional
    public RecipeDTO createRecipe(RecipeDTO dto) {
        Category category = categoryRepository.findById(dto.categoryId())
                .orElseThrow(() -> new IllegalArgumentException("Категорія не знайденаа з ID: " + dto.categoryId()));

        Recipe recipe = new Recipe(
                dto.title(),
                dto.description(),
                dto.ingredients(),
                dto.instructions(),
                category
        );

        Recipe saved = recipeRepository.save(recipe);
        return convertToDTO(saved);
    }

    @Transactional
    public RecipeDTO updateRecipe(Long id, RecipeDTO dto) {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Рецепт не знайдено з ID: " + id));

        Category category = categoryRepository.findById(dto.categoryId())
                .orElseThrow(() -> new IllegalArgumentException("Категорія не знайденаа з ID: " + dto.categoryId()));

        recipe.setTitle(dto.title());
        recipe.setDescription(dto.description());
        recipe.setIngredients(dto.ingredients());
        recipe.setInstructions(dto.instructions());
        recipe.setCategory(category);

        Recipe updated = recipeRepository.save(recipe);
        return convertToDTO(updated);
    }

    @Transactional
    public void deleteRecipe(Long id) {
        if (!recipeRepository.existsById(id)) {
            throw new IllegalArgumentException("Рецепт не знайдено з ID: " + id);
        }
        recipeRepository.deleteById(id);
    }

    private RecipeDTO convertToDTO(Recipe recipe) {
        return new RecipeDTO(
                recipe.getId(),
                recipe.getTitle(),
                recipe.getDescription(),
                recipe.getIngredients(),
                recipe.getInstructions(),
                recipe.getCategory().getId(),
                recipe.getCategory().getName()
        );
    }
}