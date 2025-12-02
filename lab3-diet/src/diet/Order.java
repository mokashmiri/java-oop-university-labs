package diet;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Represents and order issued by an {@link Customer} for a {@link Restaurant}.
 *
 * When an order is printed to a string is should look like:
 * <pre>
 *  RESTAURANT_NAME, USER_FIRST_NAME USER_LAST_NAME : DELIVERY(HH:MM):
 *  	MENU_NAME_1->MENU_QUANTITY_1
 *  	...
 *  	MENU_NAME_k->MENU_QUANTITY_k
 * </pre>
 */
public class Order {
    private final Restaurant restaurant;
    private final Customer customer;
    private final String deliveryTime;
    private final Map<String,Integer> menus = new LinkedHashMap<>();
    private PaymentMethod paymentMethod = PaymentMethod.CASH;
    private OrderStatus status = OrderStatus.ORDERED;

    public Order(Restaurant restaurant, Customer customer, String deliveryTime){
        this.restaurant = restaurant;
        this.customer = customer;
        this.deliveryTime = deliveryTime;
    }

	/**
	 * Possible order statuses
	 */
	public enum OrderStatus {
		ORDERED, READY, DELIVERED
	}

	/**
	 * Accepted payment methods
	 */
	public enum PaymentMethod {
		PAID, CASH, CARD
	}

	/**
	 * Set payment method
	 * @param pm the payment method
	 */
	public void setPaymentMethod(PaymentMethod pm) {
        this.paymentMethod = pm;
	}

	/**
	 * Retrieves current payment method
	 * @return the current method
	 */
	public PaymentMethod getPaymentMethod() {
        return paymentMethod;
	}

	/**
	 * Set the new status for the order
	 * @param os new status
	 */
	public void setStatus(OrderStatus os) {
        this.status = os;
	}

	/**
	 * Retrieves the current status of the order
	 *
	 * @return current status
	 */
	public OrderStatus getStatus() {
        return status;
	}

	/**
	 * Add a new menu to the order with a given quantity
	 *
	 * @param menu	menu to be added
	 * @param quantity quantity
	 * @return the order itself (allows method chaining)
	 */
	public Order addMenus(String menu, int quantity) {
        menus.put(menu, quantity);
        return this;
	}
    
    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append(restaurant.getName()).append(", ")
          .append(customer.getFirstName()).append(" ")
          .append(customer.getLastName()).append(" : (")
          .append(deliveryTime)
          .append("):\n");
        menus.entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .forEach(e -> sb.append("\t").append(e.getKey()).append("->").append(e.getValue()).append("\n"));
        return sb.toString();
	}
	
    Customer getCustomer(){
        return customer;
    }

    String getDeliveryTime(){
        return deliveryTime;
    }
}
