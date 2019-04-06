package application;

public class WeightedFoodItem extends FoodItem {
	private int weight;
	private boolean visited;
	
	WeightedFoodItem(String id, String name, int weight){
		super(id, name);
		this.weight = weight;
		visited = false;
		
	}
		
	public int getWeight() {
		return weight;
	}
	
	public void setVisited(){
		visited = true;
	}
	
	public boolean isVisited() {
		return visited;
	}
}
