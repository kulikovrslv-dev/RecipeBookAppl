package com.recipebook.service;

import com.recipebook.entity.Category;
import com.recipebook.entity.Recipe;
import com.recipebook.repository.CategoryRepository;
import com.recipebook.repository.RecipeRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Service
public class ParserService {

    private final RecipeRepository recipeRepository;
    private final CategoryRepository categoryRepository;

    public ParserService(RecipeRepository recipeRepository, CategoryRepository categoryRepository) {
        this.recipeRepository = recipeRepository;
        this.categoryRepository = categoryRepository;
    }


    @Transactional
    public int parseAndImportRecipes() {
        int importedCount = 0;
        String demoUrl = "https://uk.wikipedia.org/wiki/%D0%9A%D0%B0%D1%82%D0%B5%D0%B3%D0%BE%D1%80%D1%96%D1%8F:%D0%A1%D1%82%D1%80%D0%B0%D0%B2%D0%B8_%D0%B7%D0%B0_%D0%B0%D0%BB%D1%84%D0%B0%D0%B2%D1%96%D1%82%D0%BE%D0%BC";

        try {
            Document doc = Jsoup.connect(demoUrl)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .timeout(5000)
                    .get();

            Category parsedCategory = categoryRepository.findByName("Світові страви")
                    .orElseGet(() -> categoryRepository.save(new Category("Світові страви", "Страви з відкритих джерел")));

            Elements links = doc.select(".mw-category-group a");

            int limit = 0;
            for (Element link : links) {
                if (limit >= 3) break;
                String recipeTitle = link.text();
                String recipeUrl = link.absUrl("href");

                Document recipeDoc = Jsoup.connect(recipeUrl).timeout(3000).get();
                Element infoParagraph = recipeDoc.select(".mw-parser-output > p").first();
                String description = (infoParagraph != null) ? infoParagraph.text() : "Смачна історична страва.";

                // Обрезаем длинный текст
                if (description.length() > 250) {
                    description = description.substring(0, 247) + "...";
                }

                Recipe recipe = new Recipe();
                recipe.setTitle(recipeTitle);
                recipe.setDescription(description);
                recipe.setIngredients("Спеції, Інгрідієнти по смаку, Історичний колорит");
                recipe.setInstructions("1. Вивчити рецепт. 2. Приготувати згідно традиціям.");
                recipe.setCategory(parsedCategory);

                recipeRepository.save(recipe);
                importedCount++;
                limit++;
            }

        } catch (IOException e) {
            System.out.println("Мережа недоступна, запуск локальної версії...");
            importedCount = runFallbackParsing();
        }

        return importedCount;
    }

    private int runFallbackParsing() {
        String mockHtml = "<html><body>" +
                "<div class='recipe-item'>" +
                "  <h2 class='title'>Локальна піца маргарита</h2>" +
                "  <p class='desc'>Класична італьянська піца з моцарелою.</p>" +
                "  <span class='ingr'>Тісто, томатний соус, сир моцарела, базилік</span>" +
                "</div>" +
                "<div class='recipe-item'>" +
                "  <h2 class='title'>Локальний Салат Цезар</h2>" +
                "  <p class='desc'>Відомий салат з куркою гриль, грінками та соусом.</p>" +
                "  <span class='ingr'>Куряча грудка, салат ромен, грінки, пармезан, соус Цезар</span>" +
                "</div>" +
                "</body></html>";

        Document doc = Jsoup.parse(mockHtml);
        Category parsedCategory = categoryRepository.findByName("Італійська кухня")
                .orElseGet(() -> categoryRepository.save(new Category("Італійська кухня", "Вишукані страви")));

        Elements elements = doc.select(".recipe-item");
        int count = 0;

        for (Element el : elements) {
            String title = el.select(".title").text();
            String desc = el.select(".desc").text();
            String ingr = el.select(".ingr").text();

            Recipe recipe = new Recipe(
                    title,
                    desc,
                    ingr,
                    "1. Підготувати інградієнти. 2. Перемішати/спекти. 3. Подати гарячим.",
                    parsedCategory
            );
            recipeRepository.save(recipe);
            count++;
        }
        return count;
    }
}