package diet;

import java.util.*;


/**
 * Represents a takeaway restaurant chain.
 * It allows managing restaurants, customers, and orders.
 */
public class Takeaway {
    private final Map<String,Restaurant> restaurants = new LinkedHashMap<>();
    private final List<Customer> customers = new ArrayList<>();

	/**
	 * Creates a new restaurant with a given name
	 *
	 * @param restaurantName name of the restaurant
	 * @return the new restaurant
	 */
	public Restaurant addRestaurant(String restaurantName) {
        Restaurant r = new Restaurant(restaurantName);
        restaurants.put(restaurantName, r);
        return r;
	}

	/**
	 * Retrieves the names of all restaurants
	 *
	 * @return collection of restaurant names
	 */
	public Collection<String> restaurants() {
        return restaurants.keySet();
	}

	/**
	 * Creates a new customer for the takeaway
	 * @param firstName first name of the customer
	 * @param lastName	last name of the customer
	 * @param email		email of the customer
	 * @param phoneNumber mobile phone number
	 *
	 * @return the object representing the newly created customer
	 */
	public Customer registerCustomer(String firstName, String lastName, String email, String phoneNumber) {
        Customer c = new Customer(firstName, lastName, email, phoneNumber);
        customers.add(c);
        return c;
	}

	/**
	 * Retrieves all registered customers
	 *
	 * @return sorted collection of customers
	 */
	public Collection<Customer> customers(){
        List<Customer> list = new ArrayList<>(customers);
        Collections.sort(list);
        return list;
	}


	/**
	 * Creates a new order for the chain.
	 *
	 * @param customer		 customer issuing the order
	 * @param restaurantName name of the restaurant that will take the order
	 * @param time	time of desired delivery
	 * @return order object
	 */
	public Order createOrder(Customer customer, String restaurantName, String time) {
        Restaurant r = restaurants.get(restaurantName);
        if (r == null) return null;
        String delivery = normalizeToOpening(time, r);
        Order o = new Order(r, customer, delivery);
        r.addOrder(o);
        return o;
	}

	/**
	 * Find all restaurants that are open at a given time.
	 *
	 * @param time the time with format {@code "HH:MM"}
	 * @return the sorted collection of restaurants
	 */
	public Collection<Restaurant> openRestaurants(String time){
        List<Restaurant> open = new ArrayList<>();
        for (Restaurant r : restaurants.values()){
            if (r.isOpenAt(time)) open.add(r);
        }
        open.sort(Comparator.comparing(Restaurant::getName));
        return open;
	}

    private static String normalizeToOpening(String time, Restaurant r){
        if (r.isOpenAt(time)) return pad(time);
        String cur = time;
        for (int i=0;i<24*60;i++){
            cur = plusOneMinute(cur);
            if (r.isOpenAt(cur)){
                String back = cur;
                while (true){
                    String prev = minusOneMinute(back);
                    if (!r.isOpenAt(prev)) break;
                    back = prev;
                }
                return pad(back);
            }
        }
        return pad(time);
    }
    private static String plusOneMinute(String t){
        String[] p = t.split(":");
        int h = Integer.parseInt(p[0]);
        int m = Integer.parseInt(p[1]);
        m++;
        if (m==60){ m=0; h=(h+1)%24; }
        return String.format("%02d:%02d", h, m);
    }
    private static String minusOneMinute(String t){
        String[] p = t.split(":");
        int h = Integer.parseInt(p[0]);
        int m = Integer.parseInt(p[1]);
        m--;
        if (m<0){ m=59; h=(h+23)%24; }
        return String.format("%02d:%02d", h, m);
    }
    private static String pad(String t){
        String[] p = t.split(":");
        return String.format("%02d:%02d", Integer.parseInt(p[0]), Integer.parseInt(p[1]));
	}
}
