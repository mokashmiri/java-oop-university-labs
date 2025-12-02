package diet;

import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Facade class for the diet management.
 * It allows defining and retrieving raw materials and products.
 *
 */
public class Food {
    private final Map<String,NutritionalElement> rawMaterials = new LinkedHashMap<>();
    private final Map<String,NutritionalElement> products = new LinkedHashMap<>();
    private final Map<String,Recipe> recipes = new LinkedHashMap<>();

	/**
	 * Define a new raw material.
	 * The nutritional values are specified for a conventional 100g quantity
	 * @param name unique name of the raw material
	 * @param calories calories per 100g
	 * @param proteins proteins per 100g
	 * @param carbs carbs per 100g
	 * @param fat fats per 100g
	 */
	public void defineRawMaterial(String name, double calories, double proteins, double carbs, double fat) {
		rawMaterials.put(name, new SimpleElement(name, calories, proteins, carbs, fat, true));
	}

	/**
	 * Retrieves the collection of all defined raw materials
	 * @return collection of raw materials though the {@link NutritionalElement} interface
	 */
	public Collection<NutritionalElement> rawMaterials() {
		return rawMaterials.values().stream()
			.sorted(Comparator.comparing(NutritionalElement::getName))
			.collect(Collectors.toList());
	}

	/**
	 * Retrieves a specific raw material, given its name
	 * @param name  name of the raw material
	 * @return  a raw material though the {@link NutritionalElement} interface
	 */
	public NutritionalElement getRawMaterial(String name) {
		return rawMaterials.get(name);
	}

	/**
	 * Define a new packaged product.
	 * The nutritional values are specified for a unit of the product
	 * @param name unique name of the product
	 * @param calories calories for a product unit
	 * @param proteins proteins for a product unit
	 * @param carbs carbs for a product unit
	 * @param fat fats for a product unit
	 */
	public void defineProduct(String name, double calories, double proteins, double carbs, double fat) {
		products.put(name, new SimpleElement(name, calories, proteins, carbs, fat, false));
	}

	/**
	 * Retrieves the collection of all defined products
	 * @return collection of products though the {@link NutritionalElement} interface
	 */
	public Collection<NutritionalElement> products() {
		return products.values().stream()
			.sorted(Comparator.comparing(NutritionalElement::getName))
			.collect(Collectors.toList());
	}

	/**
	 * Retrieves a specific product, given its name
	 * @param name  name of the product
	 * @return  a product though the {@link NutritionalElement} interface
	 */
	public NutritionalElement getProduct(String name) {
		return products.get(name);
	}

	/**
	 * Creates a new recipe stored in this Food container.
	 *  
	 * @param name name of the recipe
	 * @return the newly created Recipe object
	 */
	public Recipe createRecipe(String name) {
		Recipe r = new Recipe(name, this);
		recipes.put(name, r);
		return r;
	}
	
	/**
	 * Retrieves the collection of all defined recipes
	 * @return collection of recipes though the {@link NutritionalElement} interface
	 */
	public Collection<NutritionalElement> recipes() {
		return recipes.values().stream()
			.map(e->(NutritionalElement)e)
			.sorted(Comparator.comparing(NutritionalElement::getName))
			.collect(Collectors.toList());
	}

	/**
	 * Retrieves a specific recipe, given its name
	 * @param name  name of the recipe
	 * @return  a recipe though the {@link NutritionalElement} interface
	 */
	public NutritionalElement getRecipe(String name) {
		return recipes.get(name);
	}

	/**
	 * Creates a new menu
	 * 
	 * @param name name of the menu
	 * @return the newly created menu
	 */
	public Menu createMenu(String name) {
		return new Menu(name, this);
	}

    private static class SimpleElement implements NutritionalElement {
        private final String name;
        private final double calories;
        private final double proteins;
        private final double carbs;
        private final double fat;
        private final boolean per100g;

        SimpleElement(String name, double calories, double proteins, double carbs, double fat, boolean per100g){
            this.name = name;
            this.calories = calories;
            this.proteins = proteins;
            this.carbs = carbs;
            this.fat = fat;
            this.per100g = per100g;
        }

        @Override public String getName(){ return name; }
        @Override public double getCalories(){ return calories; }
        @Override public double getProteins(){ return proteins; }
        @Override public double getCarbs(){ return carbs; }
        @Override public double getFat(){ return fat; }
        @Override public boolean per100g(){ return per100g; }
	}
}