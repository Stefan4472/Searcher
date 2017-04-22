package pathfinder;

/**
 * A class to store a tuple of two Strings representing connected addresses. The equals() method has been overridden
 * so that two AddressTuples will be equal if they share the same addresses, regardless of order. The hasCode() method
 * also makes this distinction, hashing so that two AddressTuples with the same Strings will be equivalent regardless
 * of String order.
 */
public class AddressTuple {

    private String address1, address2;

    // constructor for two strings
    public AddressTuple(String address1, String address2) {
        this.address1 = address1;
        this.address2 = address2;
    }

    // takes two nodes and gets their addresses
    public AddressTuple(LocationNode node1, LocationNode node2) {
        address1 = node1.getAddress();
        address2 = node2.getAddress();
    }

    public String getAddress1() {
        return address1;
    }

    public String getAddress2() {
        return address2;
    }

    @Override // objects are equal if they share the same two addresses, regardless of order
    public boolean equals(Object o) {
        if (o == null) {
            throw new NullPointerException();
        } else if (!(o instanceof  AddressTuple)) {
            return false;
        } else {
            AddressTuple other = (AddressTuple) o;
            return (address1.equals(other.getAddress1()) && address2.equals(other.getAddress2())) ||
                    address1.equals(other.getAddress2()) && address2.equals(other.getAddress1());
        }
    }

    @Override // basic hash function. Hashes the two addresses independently to get the same value for any tuples that
    // have the same two addresses
    public int hashCode() {
        int hash = 0;
        for (int i = 0; i < address1.length(); i++) {
            hash += 13 * address1.charAt(i);
        }
        for (int i = 0; i < address2.length(); i++) {
            hash += 13 * address2.charAt(i);
        }
        return hash;
    }

    @Override
    public String toString() {
        return "AddressTuple(" + address1 + "," + address2 + ")";
    }
}
