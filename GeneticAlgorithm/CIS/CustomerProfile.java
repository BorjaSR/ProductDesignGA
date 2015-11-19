import java.util.HashMap;

public class CustomerProfile {
	
	public HashMap<Attribute, Integer> CustomerProfile;
	
		public CustomerProfile(HashMap<Attribute, Integer> customerProfile) {
			super();
			CustomerProfile = customerProfile;
		}

		public HashMap<Attribute, Integer> getCustomerProfile() {
			return CustomerProfile;
		}

		public void setCustomerProfile(HashMap<Attribute, Integer> customerProfile) {
			CustomerProfile = customerProfile;
		}
}

