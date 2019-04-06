 /**
 * Filename: Main.java Project: p4 
 * Authors: Aron Denenberg, Ryan Ruenroeng, Nick Ferrentino, Jacob Bur, Deb Deppler 
 * Due Date: 12/16/18
 * 
 * Bugs or Other Notes: 
 * 
 * Define food data for all food items in application
 * 
 */
package application;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * This class represents the backend for managing all 
 * the operations associated with FoodItems
 * 
 * @author sapan (sapan@cs.wisc.edu), aron (adenenberg@cs.wisc.edu), ryan (ruenroeng@cs.wisc.edu), nick(nferrentino@cs.wisc.edu), jacob (bur@cs.wisc.edu)
 */
public class FoodData implements FoodDataADT<FoodItem> {
    
    // List of all the food items.
    private List<FoodItem> foodItemList;

    // Map of nutrients and their corresponding index
    private HashMap<String, BPTree<Double, FoodItem>> indexes;
    private BPTree<Double, FoodItem> calorieTree = new BPTree<Double, FoodItem>(5);
    private BPTree<Double, FoodItem> fatTree = new BPTree<Double, FoodItem>(5);
    private BPTree<Double, FoodItem> carbTree = new BPTree<Double, FoodItem>(5);
    private BPTree<Double, FoodItem> fiberTree = new BPTree<Double, FoodItem>(5);
    private BPTree<Double, FoodItem> proteinTree = new BPTree<Double, FoodItem>(5);
    

    
    
    /**
     * Public constructor
     */
    public FoodData() {
        indexes = new HashMap<String, BPTree<Double, FoodItem>>();
        indexes.put("calories", calorieTree);
        indexes.put("fat", fatTree);
        indexes.put("carbohydrate", carbTree);
        indexes.put("fiber", fiberTree);
        indexes.put("protein", proteinTree);
        foodItemList = new ArrayList<FoodItem>();
    }
    
 // Category to group certain foods together
    enum foodCategory{
    	Vegetable, Chicken, Beef, Pork, Lamb, Starches, Treats, Dairy
    }
    
    enum foodOrigin{
    	Chinese, Italian, Mediterranean, Japanese, SoutheastAsia, American, French, African, English, EasternEuropean, SouthAmerican
    }
    
    enum macros {
		carbohydrate,
		calories,
		fat,
		fiber,
		protein,
	}
    
    /**
     * clears all data stored in the object
     */
    public void clearFoodData() {
    	foodItemList.clear();
    	calorieTree.clear();
    	fatTree.clear();
    	carbTree.clear();
    	fiberTree.clear();
    	proteinTree.clear();
    }
    
    
    /**
     * Reads a file containing food data and loads it into the food data object to be available to main application GUI
     * 
     * (non-Javadoc)
     * @see skeleton.FoodDataADT#loadFoodItems(java.lang.String)
     * @param Filepath of the file containing the food information
     */
    @Override
    public void loadFoodItems(String filePath){ 
    	// reset food data each time we load from file
    	clearFoodData();
    	
    	// read file and parse it to get all necessary information for food data item
      try {
        BufferedReader inStream = new BufferedReader(new FileReader(filePath));
        String inLine;
        try {
    	while ((inLine = inStream.readLine()) != null) {
    		String[] foodInfo = inLine.split(",");
    		if (foodInfo.length != 14) {
    			continue;
    		}
    		String id = foodInfo[0].trim();
    		String name = foodInfo[1].trim();
    		macros calorieLabel = macros.valueOf(foodInfo[2].trim());
    		macros fatLabel = macros.valueOf(foodInfo[4].trim());
    		macros carbLabel = macros.valueOf(foodInfo[6].trim());
    		macros fiberLabel = macros.valueOf(foodInfo[8].trim());
    		macros proteinLabel = macros.valueOf(foodInfo[10].trim());
    		foodCategory category = Enum.valueOf(foodCategory.class, foodInfo[12].trim());
    		foodOrigin origin = foodOrigin.valueOf(foodInfo[13]);
    		if (!calorieLabel.equals(macros.calories) || !fatLabel.equals(macros.fat) || !carbLabel.equals(macros.carbohydrate) || !fiberLabel.equals(macros.fiber) || !proteinLabel.equals(macros.protein)) {
    			continue;
    		}
    		try {
    		// load each piece of data into the different field of the food data item
    		double calorieCount = Double.parseDouble(foodInfo[3].trim());
    		double fatCount = Double.parseDouble(foodInfo[5].trim());
    		double carbCount = Double.parseDouble(foodInfo[7].trim());
    		double fiberCount = Double.parseDouble(foodInfo[9].trim());
    		double proteinCount = Double.parseDouble(foodInfo[11].trim());
    		FoodItem newFood = new FoodItem(id, name);
    		newFood.addNutrient(calorieLabel.toString(), calorieCount);
    		newFood.addNutrient(fatLabel.toString(), fatCount);
    		newFood.addNutrient(carbLabel.toString(), carbCount);
    		newFood.addNutrient(fiberLabel.toString(), fiberCount);	
    		newFood.addNutrient(proteinLabel.toString(), proteinCount);
    		newFood.addCategory(category);
    		newFood.addOrigin(origin);
    		if (calorieCount<0||fatCount<0||carbCount<0||fiberCount<0||proteinCount<0) {
    		  continue;
    		}
    		
    		calorieTree.insert(calorieCount, newFood);
    		fatTree.insert(fatCount, newFood);
    		carbTree.insert(carbCount, newFood);
    		fiberTree.insert(fiberCount, newFood);
    		proteinTree.insert(proteinCount, newFood);
    		foodItemList.add(newFood);
    		
    		
    		} catch (NumberFormatException e) {
    			continue;
    		  } 
    		}
        } catch(IOException e) {
        	
        } finally {
        	inStream.close();
        	Collections.sort(foodItemList,(t1,t2) -> t1.getName().toLowerCase().compareTo(t2.getName().toLowerCase()));
        }
    	} catch (FileNotFoundException e) {
    		System.out.println("File was not found.");
    	} catch (IOException e) {
    	  System.out.print("Could not write to file.");
    	} 
      
    }
    

    /**
     * Filter all food items by whether they contain the the string passed in in their food name
     * @see skeleton.FoodDataADT#filterByName(java.lang.String)
     * @param substring is the string to check if it is contained in the food title
     * @return List of food items that contain the substring in their title
     */
    @Override
    public List<FoodItem> filterByName(String substring) {
    	if (substring.isEmpty()) {
    		return foodItemList;
    	}
    	List <FoodItem> filterList = new ArrayList<FoodItem>();
        for (FoodItem food : foodItemList) {
        	String foodName = food.getName().toUpperCase();
        	if (foodName.contains(substring)) {
        		filterList.add(food);
        	}
        }
        return filterList;
    }

    /**
     * Filters all food items base on passed in substring. Assumes substring contains 
     * nutrients stored in food data, a comparator, and a numeric value
     * @see skeleton.FoodDataADT#filterByNutrients(java.util.List)
     * @param rules string that contains the filter rules to use in the range search of fooditems
     * @list of food items that pass the filter rules
     */
    @Override
    public List<FoodItem> filterByNutrients(List<String> rules) {
    	List <FoodItem> filteredList = new ArrayList<FoodItem>();
    	for (FoodItem food : foodItemList) {
    		filteredList.add(food);
    	}
        for (String rule : rules) {
        	List<FoodItem> singleRuleResult;
        	String[] ruleParts = rule.split(" ");
        	String macro = ruleParts[0].trim();
        	String comparator = ruleParts[1].trim();
        	comparator = (comparator.equals("="))? "==": comparator;
        	double value = Double.parseDouble(ruleParts[2].trim());
        	switch(macro.charAt(2)) {
        	case 'r':
        		singleRuleResult = carbTree.rangeSearch(value, comparator);
        		break;
        	case 't':
        		singleRuleResult = fatTree.rangeSearch(value, comparator);
        		break;
        	case 'b':
        		singleRuleResult = fiberTree.rangeSearch(value, comparator);
        		break;
        	case 'l':
        		singleRuleResult = calorieTree.rangeSearch(value, comparator);
        		break;
        	default:
        		singleRuleResult = proteinTree.rangeSearch(value, comparator);
        	}
        	
        	filteredList.retainAll(singleRuleResult);
        	
        }
        return filteredList;
    }

    /*
     * Adds a food item to the ArrayList in the app. Sorts the list and inserts the new item into 
     * the appropriate BPTree indices.  
     * (non-Javadoc)
     * @see skeleton.FoodDataADT#addFoodItem(skeleton.FoodItem)
     */
    @Override
    public void addFoodItem(FoodItem foodItem) {
      foodItemList.add(foodItem);
      Collections.sort(foodItemList, (t1,t2) -> t1.getName().toLowerCase().compareTo(t2.getName().toLowerCase()));
      calorieTree.insert(foodItem.getNutrientValue("calories"), foodItem);
      fatTree.insert(foodItem.getNutrientValue("fat"), foodItem);
      carbTree.insert(foodItem.getNutrientValue("carbohydrate"), foodItem);
      fiberTree.insert(foodItem.getNutrientValue("fiber"), foodItem);
      proteinTree.insert(foodItem.getNutrientValue("protein"), foodItem);
    }

    /*
     * (non-Javadoc)
     * @see skeleton.FoodDataADT#getAllFoodItems()
     */
    @Override
    public List<FoodItem> getAllFoodItems() {
        return foodItemList;
    }
    
    /**
     * Save the list of food items in ascending order by name
     * 
     * @param filename name of the file where the data needs to be saved 
     */
    public void saveFoodItems(String filePath) {
      Iterator<FoodItem> itr = this.foodItemList.iterator();
      FoodItem currFood = null;
      String saveString = new String();
       try {
        BufferedWriter Writer = new BufferedWriter(new FileWriter(filePath));
        while (itr.hasNext()) {
          currFood = itr.next();
          StringBuilder sb = new StringBuilder();
          sb.append(currFood.getID()+",");
          sb.append(currFood.getName()+",");
          sb.append("calories"+","+currFood.getNutrientValue("calories")+",");
          sb.append("fat"+","+currFood.getNutrientValue("fat")+",");
          sb.append("carbohydrate"+","+currFood.getNutrientValue("carbohydrate")+",");
          sb.append("fiber"+","+currFood.getNutrientValue("fiber")+",");
          sb.append("protein"+","+currFood.getNutrientValue("protein"));
          saveString = sb.toString();
          Writer.write(saveString+"\n");
        } 
        Writer.close();
      } catch (Exception e) {
        System.out.println("Could not write to file.");
      }
    }
    
}

