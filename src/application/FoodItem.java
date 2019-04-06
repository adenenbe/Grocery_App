/**
 * Filename: Main.java Project: p4 
 * Authors: Aron Denenberg, Ryan Ruenroeng, Nick Ferrentino, Jacob Bur 
 * Due Date: 12/16/18
 * 
 * Bugs or Other Notes: 
 * 
 * Define a single food item
 * 
 */
package application;
import java.util.HashMap;

/**
 * This class represents a food item with all its properties.
 * 
 * @author aron(adenenberg@cs.wisc.edu), ryan(ruenroeng@cs.wisc.edu), nick(nferrentino@cs.wisc.edu), jacob(bur@cs.wisc.edu)
 */
public class FoodItem {
    // The name of the food item.
    private String name;

    // The id of the food item.
    private String id;

    // Map of nutrients and value.
    private HashMap<String, Double> nutrients;
    
    // List of Food attributes
    private FoodData.foodCategory category;
    private FoodData.foodOrigin origin;
    private int weight;
    

    
    
    /**
     * Constructor
     * @param name name of the food item
     * @param id unique id of the food item 
     */
    public FoodItem(String id, String name) {
    	this.id=id;
    	this.name=name;
    	this.nutrients= new HashMap<String,Double>();
    	this.weight = 10;
    }
    
    /**
     * Gets the name of the food item
     * 
     * @return name of the food item
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the unique id of the food item
     * 
     * @return id of the food item
     */
    public String getID() {
        return id;
    }
    
    /**
     * Gets the nutrients of the food item
     * 
     * @return nutrients of the food item
     */
    public HashMap<String, Double> getNutrients() {
        return nutrients;
    }

    /**
     * Adds a nutrient and its value to this food. 
     * If nutrient already exists, updates its value.
     */
    public void addNutrient(String name, double value) {
        nutrients.put(name, value);
    }

    /**
     * Returns the value of the given nutrient for this food item. 
     * If not present, then returns 0.
     */
    public double getNutrientValue(String name) {
        return nutrients.get(name) == null ? 0 : nutrients.get(name);
        
    }
    
    public void addCategory(FoodData.foodCategory category) {
    	this.category = category;
    }
    
    public FoodData.foodCategory getCategory(){
    	return category;
    }
    
    public void addOrigin(FoodData.foodOrigin origin) {
    	this.origin = origin;
    }
    
    public FoodData.foodOrigin getOrigin() {
    	return origin;
    }
    
    public int getWeight() {
    	return weight;
    }
    
    
}
