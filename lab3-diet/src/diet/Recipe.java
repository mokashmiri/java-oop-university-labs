package diet;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Represents a recipe of the diet.
 * 
 * A recipe consists of a a set of ingredients that are given amounts of raw materials.
 * The overall nutritional values of a recipe can be computed
 * on the basis of the ingredients' values and are expressed per 100g
 * 
 *
 */
public class Recipe implements NutritionalElement {
    private final String name;
    private final Food food;
    private final Map<String,Double> ingredients = new LinkedHashMap<>();

    public Recipe(String name, Food food){
        this.name = name;
        this.food = food;
    }
	
	/**
	 * Adds the given quantity of an ingredient to the recipe.
	 * The ingredient is a raw material.
	 * 
	 * @param material the name of the raw material to be used as ingredient
	 * @param quantity the amount in grams of the raw material to be used
	 * @return the same Recipe object, it allows method chaining.
	 */
	public Recipe addIngredient(String material, double quantity) {
        ingredients.put(material, quantity);
        return this;
	}

	@Override
	public String getName() {
        return name;
	}

	
	@Override
	public double getCalories() {
        double totalGrams = ingredients.values().stream().mapToDouble(Double::doubleValue).sum();
        if (totalGrams == 0) return 0.0;
        double sum = 0.0;
        for (Map.Entry<String,Double> e : ingredients.entrySet()){
            NutritionalElement rm = food.getRawMaterial(e.getKey());
            if(rm!=null){
                sum += rm.getCalories() * (e.getValue()/100.0);
            }
        }
        return sum * (100.0/totalGrams);
	}
	

	@Override
	public double getProteins() {
        double totalGrams = ingredients.values().stream().mapToDouble(Double::doubleValue).sum();
        if (totalGrams == 0) return 0.0;
        double sum = 0.0;
        for (Map.Entry<String,Double> e : ingredients.entrySet()){
            NutritionalElement rm = food.getRawMaterial(e.getKey());
            if(rm!=null){
                sum += rm.getProteins() * (e.getValue()/100.0);
            }
        }
        return sum * (100.0/totalGrams);
	}

	@Override
	public double getCarbs() {
        double totalGrams = ingredients.values().stream().mapToDouble(Double::doubleValue).sum();
        if (totalGrams == 0) return 0.0;
        double sum = 0.0;
        for (Map.Entry<String,Double> e : ingredients.entrySet()){
            NutritionalElement rm = food.getRawMaterial(e.getKey());
            if(rm!=null){
                sum += rm.getCarbs() * (e.getValue()/100.0);
            }
        }
        return sum * (100.0/totalGrams);
	}

	@Override
	public double getFat() {
        double totalGrams = ingredients.values().stream().mapToDouble(Double::doubleValue).sum();
        if (totalGrams == 0) return 0.0;
        double sum = 0.0;
        for (Map.Entry<String,Double> e : ingredients.entrySet()){
            NutritionalElement rm = food.getRawMaterial(e.getKey());
            if(rm!=null){
                sum += rm.getFat() * (e.getValue()/100.0);
            }
        }
        return sum * (100.0/totalGrams);
	}

	/**
	 * Indicates whether the nutritional values returned by the other methods
	 * refer to a conventional 100g quantity of nutritional element,
	 * or to a unit of element.
	 * 
	 * For the {@link Recipe} class it must always return {@code true}:
	 * a recipe expresses nutritional values per 100g
	 * 
	 * @return boolean indicator
	 */
	@Override
	public boolean per100g() {
		return true;
	}
	
}
