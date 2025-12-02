package diet;


public class Customer implements Comparable<Customer> {
    private final String firstName;
    private final String lastName;
    private String email;
    private String phone;

    public Customer(String firstName, String lastName, String email, String phone){
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
    }
	
	public String getLastName() {
        return lastName;
	}
	
	public String getFirstName() {
        return firstName;
	}
	
	public String getEmail() {
        return email;
	}
	
	public String getPhone() {
        return phone;
	}
	
	public void SetEmail(String email) {
        this.email = email;
	}
	
	public void setPhone(String phone) {
        this.phone = phone;
	}
	
    @Override
    public int compareTo(Customer o) {
        int c = this.lastName.compareTo(o.lastName);
        if(c!=0) return c;
        return this.firstName.compareTo(o.firstName);
    }

    @Override
    public String toString() {
        return firstName + " " + lastName;
    }
}
