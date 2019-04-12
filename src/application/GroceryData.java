package application;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

import com.sun.corba.se.impl.encoding.OSFCodeSetRegistry.Entry;

public class GroceryData {
	private Map<FoodItem, Map<FoodItem, Integer>> foodsMap;
	private List<FoodItem> historicItems;
	
	
	public GroceryData() {
		foodsMap = new HashMap<FoodItem, Map<FoodItem, Integer>>();
	}
	
	public Map<FoodItem, Map<FoodItem, Integer>> getFoodsMap(){
		return foodsMap;
	}
	
	public boolean foodExists(FoodItem food) {
		return foodsMap.get(food) != null;
	}
	
	public boolean linkExists(FoodItem item1, FoodItem item2) {
		Set<Map.Entry<FoodItem, Integer>> links = foodsMap.get(item1).entrySet();
		for (Map.Entry<FoodItem, Integer> foodEntry : links) {
			FoodItem food = foodEntry.getKey();
			if (food == item2) {
				return true;
			}
		}
		return false;
	}
	
	public void addGroceryItem(FoodItem food, FoodData foodMaster) {
		foodsMap.put(food, new HashMap<FoodItem, Integer>());
		foodMaster.getAllFoodItems().stream()
				.filter(t1 -> t1.getCategory() == food.getCategory())
    			.filter(t2 -> t2.getWeight() >= food.getWeight())
    			.forEach(t3 -> addFoodItemLinkage(food, t3, 1));
		}
	
	public void addFoodItemLinkage(FoodItem item1, FoodItem item2, Integer weight) {
		if (linkExists(item1, item2)) {
			return;
		}
		foodsMap.get(item1).put(item2, weight);
		}
	
	public void addHistoricItem(FoodItem food) {
		if (historicItems.contains(food)) {
			return;
		}
		historicItems.add(food);
	}
	
	public List<FoodItem> sortItemsBasedOnRecommendations() {
		Queue<FoodItem> pq = new PriorityQueue<FoodItem>();
		Map<FoodItem, Integer> weights = new HashMap<FoodItem, Integer>();
		List<FoodItem> returnList = new ArrayList<FoodItem>();
		@SuppressWarnings("unchecked")
		List<FoodItem> foodsList = (List<FoodItem>)foodsMap.keySet();
		foodsList.addAll(historicItems);
		foodsList.sort((p1, p2) -> p1.getCategory().ordinal() - p2.getCategory().ordinal());
		for (FoodItem item : foodsList) {
			weights.put(item, 0);
			returnList.addAll(fibonacciPath(item, pq, weights));
		}
		
		return returnList;
		
		
	}
	
	public List<FoodItem> fibonacciPath(FoodItem item, Queue<FoodItem> pq, Map<FoodItem, Integer> weights){
		List <FoodItem> returnList = new ArrayList<FoodItem>();
		Map<FoodItem, Integer> mappedItems = foodsMap.get(item);
		for (FoodItem mappedItem : mappedItems.keySet()) {
			if (!weights.containsKey(mappedItem)) {
				weights.put(mappedItem, weights.get(item) + mappedItems.get(mappedItem));
				pq.add(mappedItem);
				returnList.add(mappedItem);
			} else {
				if (weights.get(mappedItem) > (weights.get(item) + mappedItems.get(mappedItem))) {
					weights.replace(mappedItem, (weights.get(item) + mappedItems.get(mappedItem)));
				}
			}
			
		}
		
		returnList.addAll(fibonacciPath(pq.remove(), pq, weights));
		returnList.sort((p1, p2) -> mappedItems.get(p1) - mappedItems.get(p2));
		return returnList;
		
	}

}
