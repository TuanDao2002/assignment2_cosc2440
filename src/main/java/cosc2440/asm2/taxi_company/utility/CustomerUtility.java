package cosc2440.asm2.taxi_company.utility;

import cosc2440.asm2.taxi_company.model.Customer;
import cosc2440.asm2.taxi_company.model.Invoice;

import java.util.List;

public final class CustomerUtility {
    private CustomerUtility(){}

    public static boolean checkCustomerBookingIsFinalized(Customer findCustomer) {
        List<Invoice> findCustomerInvoiceList = findCustomer.getInvoiceList();
        // check if the latest booking of the customer is finalized or not
        return findCustomerInvoiceList.isEmpty()
                || findCustomerInvoiceList.get(findCustomerInvoiceList.size() - 1).getBooking().getDropOffDateTime() != null;
    }
}
