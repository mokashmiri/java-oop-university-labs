package diet;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import diet.Order.OrderStatus;

/**
 * Represents a restaurant class with given opening times and a set of menus.
 */
public class Restaurant {
	private final String name;
	private final List<int[]> intervals = new ArrayList<>();
	private final Map<String,Menu> menus = new LinkedHashMap<>();
	private final List<Order> orders = new ArrayList<>();

	public Restaurant(String name){
		this.name = name;
	}
	
	/**
	 * retrieves the name of the restaurant.
	 *
	 * @return name of the restaurant
	 */
	public String getName() {
		return name;
	}

	/**
	 * Define opening times.
	 * Accepts an array of strings (even number of elements) in the format {@code "HH:MM"},
	 * so that the closing hours follow the opening hours
	 * (e.g., for a restaurant opened from 8:15 until 14:00 and from 19:00 until 00:00,
	 * arguments would be {@code "08:15", "14:00", "19:00", "00:00"}).
	 *
	 * @param hm sequence of opening and closing times
	 */
	public void setHours(String ... hm) {
		intervals.clear();
		for (int i=0; i<hm.length; i+=2){
			int start = toMinutes(hm[i]);
			int end = toMinutes(hm[i+1]);
			intervals.add(new int[]{start,end});
		}
	}

	private static int toMinutes(String hhmm){
		String[] p = hhmm.split(":");
		int h = Integer.parseInt(p[0]);
		int m = Integer.parseInt(p[1]);
		return h*60+m;
	}

	/**
	 * Checks whether the restaurant is open at the given time.
	 *
	 * @param time time to check
	 * @return {@code true} is the restaurant is open at that time
	 */
	public boolean isOpenAt(String time){
		int t = toMinutes(time);
		for (int[] iv : intervals){
			int s = iv[0], e = iv[1];
			if (s<=e){
				if (t>=s && t<=e) return true;
			} else { // wraps midnight
				if (t>=s || t<=e) return true;
			}
		}
		return false;
	}

	/**
	 * Adds a menu to the list of menus offered by the restaurant
	 *
	 * @param menu	the menu
	 */
	public void addMenu(Menu menu) {
		menus.put(menu.getName(), menu);
	}

	/**
	 * Gets the restaurant menu with the given name
	 *
	 * @param name	name of the required menu
	 * @return menu with the given name
	 */
	public Menu getMenu(String name) {
		return menus.get(name);
	}

	void addOrder(Order order){
		orders.add(order);
	}

	/**
	 * Retrieve all order with a given status with all the relative details in text format.
	 *
	 * @param status the status to be matched
	 * @return textual representation of orders
	 */
	public String ordersWithStatus(OrderStatus status) {
		StringBuilder sb = new StringBuilder();
		orders.stream()
			.filter(o -> o.getStatus()==status)
			.sorted(Comparator.comparing((Order o)->o.getCustomer().getLastName())
						  .thenComparing(o->o.getCustomer().getFirstName())
						  .thenComparing(Order::getDeliveryTime))
			.forEach(o -> sb.append(o.toString().trim()).append("\n"));
		return sb.toString();
	}
}
