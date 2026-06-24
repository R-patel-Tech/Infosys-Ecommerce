package tests;

import java.util.ArrayList;
import java.util.List;
import org.testng.annotations.DataProvider;

public final class TestDataProvider {
    private TestDataProvider() {
    }

    @DataProvider(name = "orderSummaryData")
    public static Object[][] orderSummaryData() {
        List<CheckoutDataset> datasets = new ArrayList<>();
        datasets.add(new CheckoutDataset(
                "T101",
                1,
                false,
                1,
                "Order Summary One",
                "9876543210",
                "221 Baker Street",
                "London",
                "Greater London",
                "560001",
                "COD"
        ));
        datasets.add(new CheckoutDataset(
                "T102",
                2,
                false,
                1,
                "Order Summary Two",
                "9876543211",
                "221 Baker Street",
                "London",
                "Greater London",
                "560001",
                "COD"
        ));
        datasets.add(new CheckoutDataset(
                "T104",
                1,
                true,
                1,
                "Order Summary Three",
                "9876543212",
                "221 Baker Street",
                "London",
                "Greater London",
                "560001",
                "COD"
        ));

        Object[][] data = new Object[datasets.size()][1];
        for (int i = 0; i < datasets.size(); i++) {
            data[i][0] = datasets.get(i);
        }
        return data;
    }

    public static final class CheckoutDataset {
        private final String scenarioName;
        private final int primaryQuantity;
        private final boolean multipleProducts;
        private final int secondaryQuantity;
        private final String fullName;
        private final String phoneNumber;
        private final String address;
        private final String city;
        private final String state;
        private final String pincode;
        private final String paymentMethod;

        public CheckoutDataset(
                String scenarioName,
                int primaryQuantity,
                boolean multipleProducts,
                int secondaryQuantity,
                String fullName,
                String phoneNumber,
                String address,
                String city,
                String state,
                String pincode,
                String paymentMethod) {
            this.scenarioName = scenarioName;
            this.primaryQuantity = primaryQuantity;
            this.multipleProducts = multipleProducts;
            this.secondaryQuantity = secondaryQuantity;
            this.fullName = fullName;
            this.phoneNumber = phoneNumber;
            this.address = address;
            this.city = city;
            this.state = state;
            this.pincode = pincode;
            this.paymentMethod = paymentMethod;
        }

        public String getScenarioName() {
            return scenarioName;
        }

        public int getPrimaryQuantity() {
            return primaryQuantity;
        }

        public boolean isMultipleProducts() {
            return multipleProducts;
        }

        public int getSecondaryQuantity() {
            return secondaryQuantity;
        }

        public String getFullName() {
            return fullName;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public String getAddress() {
            return address;
        }

        public String getCity() {
            return city;
        }

        public String getState() {
            return state;
        }

        public String getPincode() {
            return pincode;
        }

        public String getPaymentMethod() {
            return paymentMethod;
        }
    }
}
