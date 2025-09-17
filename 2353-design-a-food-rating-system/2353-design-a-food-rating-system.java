import java.util.*;

class FoodRatings {
    // Maps to store food -> cuisine and food -> rating
    private Map<String, String> foodToCuisine;
    private Map<String, Integer> foodToRating;

    // cuisine -> max-heap of Food objects
    private Map<String, PriorityQueue<Food>> cuisineToFoods;

    public FoodRatings(String[] foods, String[] cuisines, int[] ratings) {
        foodToCuisine = new HashMap<>();
        foodToRating = new HashMap<>();
        cuisineToFoods = new HashMap<>();

        for (int i = 0; i < foods.length; i++) {
            String food = foods[i];
            String cuisine = cuisines[i];
            int rating = ratings[i];

            foodToCuisine.put(food, cuisine);
            foodToRating.put(food, rating);

            // Each cuisine has its own priority queue
            cuisineToFoods.putIfAbsent(cuisine, new PriorityQueue<>(
                (a, b) -> a.rating != b.rating ? b.rating - a.rating : a.name.compareTo(b.name)
            ));

            cuisineToFoods.get(cuisine).offer(new Food(food, rating));
        }
    }
    
    public void changeRating(String food, int newRating) {
        String cuisine = foodToCuisine.get(food);
        foodToRating.put(food, newRating);
        // Push updated rating into the heap
        cuisineToFoods.get(cuisine).offer(new Food(food, newRating));
    }
    
    public String highestRated(String cuisine) {
        PriorityQueue<Food> pq = cuisineToFoods.get(cuisine);
        while (true) {
            Food top = pq.peek();
            // Ensure top is the latest rating
            if (foodToRating.get(top.name) == top.rating) {
                return top.name;
            }
            pq.poll(); // remove outdated entry
        }
    }

    // Helper class to represent food
    private static class Food {
        String name;
        int rating;
        Food(String n, int r) {
            name = n;
            rating = r;
        }
    }
}

/**
 * Your FoodRatings object will be instantiated and called as such:
 * FoodRatings obj = new FoodRatings(foods, cuisines, ratings);
 * obj.changeRating(food,newRating);
 * String param_2 = obj.highestRated(cuisine);
 */

