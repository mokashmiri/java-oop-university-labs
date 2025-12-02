package diet;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a complete menu.
 * 
 * It can be made up of both packaged products and servings of given recipes.
 *
 */
public class Menu implements NutritionalElement {
    private final String name;
    private final Food food;
    private static class RecipeServing { String recipe; double grams; }
    private final List<RecipeServing> servings = new ArrayList<>();
    private final List<String> products = new ArrayList<>();

    public Menu(String name, Food food){
        this.name = name;
        this.food = food;
    }

	/**
	 * Adds a given serving size of a recipe.
	 * The recipe is a name of a recipe defined in the {@code food}
	 * argument of the constructor.
	 * 
	 * @param recipe the name of the recipe to be used as ingredient
	 * @param quantity the amount in grams of the recipe to be used
	 * @return the same Menu to allow method chaining
	 */
    public Menu addRecipe(String recipe, double quantity) {
        RecipeServing rs = new RecipeServing();
        rs.recipe = recipe;
        rs.grams = quantity;
        servings.add(rs);
        return this;
	}

	/**
	 * Adds a unit of a packaged product.
	 * The product is a name of a product defined in the {@code food}
	 * argument of the constructor.
	 * 
	 * @param product the name of the product to be used as ingredient
	 * @return the same Menu to allow method chaining
	 */
    public Menu addProduct(String product) {
        products.add(product);
        return this;
	}

	@Override
	public String getName() {
        return name;
	}

	/**
	 * Total KCal in the menu
	 */
	@Override
	public double getCalories() {
        double total = 0.0;
        for (RecipeServing rs : servings){
            NutritionalElement rec = food.getRecipe(rs.recipe);
            if(rec!=null){
                total += rec.getCalories() * (rs.grams/100.0);
            }
        }
        for (String p : products){
            NutritionalElement prod = food.getProduct(p);
            if(prod!=null){
                total += prod.getCalories();
            }
        }
        return total;
	}

	/**
	 * Total proteins in the menu
	 */
	@Override
	public double getProteins() {
        double total = 0.0;
        for (RecipeServing rs : servings){
            NutritionalElement rec = food.getRecipe(rs.recipe);
            if(rec!=null){
                total += rec.getProteins() * (rs.grams/100.0);
            }
        }
        for (String p : products){
            NutritionalElement prod = food.getProduct(p);
            if(prod!=null){
                total += prod.getProteins();
            }
        }
        return total;
	}

	/**
	 * Total carbs in the menu
	 */
	@Override
	public double getCarbs() {
        double total = 0.0;
        for (RecipeServing rs : servings){
            NutritionalElement rec = food.getRecipe(rs.recipe);
            if(rec!=null){
                total += rec.getCarbs() * (rs.grams/100.0);
            }
        }
        for (String p : products){
            NutritionalElement prod = food.getProduct(p);
            if(prod!=null){
                total += prod.getCarbs();
            }
        }
        return total;
	}

	/**
	 * Total fats in the menu
	 */
	@Override
	public double getFat() {
        double total = 0.0;
        for (RecipeServing rs : servings){
            NutritionalElement rec = food.getRecipe(rs.recipe);
            if(rec!=null){
                total += rec.getFat() * (rs.grams/100.0);
            }
        }
        for (String p : products){
            NutritionalElement prod = food.getProduct(p);
            if(prod!=null){
                total += prod.getFat();
            }
        }
        return total;
	}

	/**
	 * Indicates whether the nutritional values returned by the other methods
	 * refer to a conventional 100g quantity of nutritional element,
	 * or to a unit of element.
	 * 
	 * For the {@link Menu} class it must always return {@code false}:
	 * nutritional values are provided for the whole menu.
	 * 
	 * @return boolean indicator
	 */
	@Override
	public boolean per100g() {
		return false;
	}
}