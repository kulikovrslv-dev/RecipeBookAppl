package com.recipebook;

import com.recipebook.entity.Category;
import com.recipebook.entity.Recipe;
import com.recipebook.repository.CategoryRepository;
import com.recipebook.repository.RecipeRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class RecipeBookApplication {

    public static void main(String[] args) {
        SpringApplication.run(RecipeBookApplication.class, args);
    }

    @Bean
    public CommandLineRunner initData(CategoryRepository categoryRepository, RecipeRepository recipeRepository) {
        return args -> {
            Category soupCategory = categoryRepository.save(new Category("Супи", "Горячі перші страви"));
            Category dessertCategory = categoryRepository.save(new Category("Десерти", "Солодкі страви та випічка"));
            Category saladCategory = categoryRepository.save(new Category("Салати", "Легкі холодні закуски"));

            recipeRepository.save(new Recipe(
                    "Український Борщ",
                    "Наваристий класичний червоний борщ із буряком та свинячими реберцями..",
                    "Свинячі реберця 500г, Буряк 2шт, Капуста 300г, Картопля 3шт, Морква 1шт, Цибуля 1шт, Томатна паста 2 ст.л., Часник, Зелень.",
                    "1. Зварити бульйон з реберець.\n2 Обсмажити на сковороді цибулю, терту моркву і буряк з додаванням томатної пасти.\n3 У киплячий бульйон додати нарізану картоплю і нашатковану капусту.\n4 Через 15 хвилин перекласти в каструлю зажарку.\n5 Додати спеції, часник та дати настоятися.",
                    soupCategory
            ));

        };
    }
}