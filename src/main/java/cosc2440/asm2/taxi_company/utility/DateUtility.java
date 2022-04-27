package cosc2440.asm2.taxi_company.utility;

import cosc2440.asm2.taxi_company.model.Booking;
import cosc2440.asm2.taxi_company.model.Customer;
import cosc2440.asm2.taxi_company.model.Driver;
import cosc2440.asm2.taxi_company.model.Invoice;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public final class DateUtility {
    // format of the input date from the request
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-uuuu");
    private static final DateTimeFormatter dateTimeFormatter = Booking.getDateTimeFormatter();
    private DateUtility(){}

    public static boolean validateDatetimeOf(Booking findBooking) {
        if (findBooking.getPickUpDatetime() == null || findBooking.getDropOffDateTime() == null) return true;

        // convert pick-up and drop-off date time strings from Booking to LocalDateTime
        LocalDateTime pickUp = LocalDateTime.parse(findBooking.getPickUpDatetime(), Booking.getDateTimeFormatter());
        LocalDateTime dropOff = LocalDateTime.parse(findBooking.getDropOffDateTime(), Booking.getDateTimeFormatter());

        // check if drop-off date time is after pick-up date time. If not, it will be invalid
        return dropOff.isAfter(pickUp);
    }

    public static boolean isPeriodValid(String startDate, String endDate) {
        LocalDate startDateObj = StringToLocalDate(startDate);
        LocalDate endDateObj = StringToLocalDate(endDate);

        if (startDateObj == null || endDateObj == null) return false;
        return endDateObj.isAfter(startDateObj);
    }

    public static LocalDate StringToLocalDate(String dateString) {
        LocalDate verifiedDateObj;
        try {
            verifiedDateObj = LocalDate.parse(dateString, dateFormatter);
        } catch (DateTimeParseException e) {
            return null;
        }

        return verifiedDateObj;
    }

    public static LocalDateTime StringToLocalDateTime(String dateTimeString) {
        LocalDateTime verifiedDateObj;
        try {
            verifiedDateObj = LocalDateTime.parse(dateTimeString, dateTimeFormatter);
        } catch (DateTimeParseException e) {
            return null;
        }

        return verifiedDateObj;
    }

    // check if the new pick-up datetime is after the drop-off datetime of the latest booking of customer and invoice
    public static boolean checkPickUpDatetimeIsValid(Customer customer, Driver driver, String newPickUpDatetime) {
        LocalDateTime pickUpDatetimeOfNewBooking = DateUtility.StringToLocalDateTime(newPickUpDatetime);
        if (pickUpDatetimeOfNewBooking == null) return false;

        if (customer.getInvoiceList().isEmpty() && driver.getInvoiceList().isEmpty()) return true;

        if (!customer.getInvoiceList().isEmpty()) {
            List<Invoice> findCustomerInvoiceList = customer.getInvoiceList();
            LocalDateTime dropOffDatetimeOfLatestBooking = findCustomerInvoiceList.get(findCustomerInvoiceList.size() - 1).getBooking().getDropOffDatetimeObj();
            if (!dropOffDatetimeOfLatestBooking.isBefore(pickUpDatetimeOfNewBooking)) {
                return false;
            }
        }

        if (!driver.getInvoiceList().isEmpty()) {
            List<Invoice> findDriverInvoiceList = driver.getInvoiceList();
            LocalDateTime dropOffDatetimeOfLatestBooking = findDriverInvoiceList.get(findDriverInvoiceList.size() - 1).getBooking().getDropOffDatetimeObj();
            return dropOffDatetimeOfLatestBooking.isBefore(pickUpDatetimeOfNewBooking);
        }

        return true;
    }

    public static String displayCustomerLatestBookingDropOff(Customer customer) {
        if (customer.getInvoiceList().isEmpty()) return "";
        String customerDropOffDatetimeLatestBooking = customer.getInvoiceList().get(customer.getInvoiceList().size() - 1).getBooking().getDropOffDateTime();
        return "\nThe drop-of date time of the customer's latest booking is at: " + customerDropOffDatetimeLatestBooking;
    }

    public static String displayDriverLatestBookingDropOff(Driver driver) {
        if (driver.getInvoiceList().isEmpty()) return "";
        String driverDropOffDatetimeLatestBooking = driver.getInvoiceList().get(driver.getInvoiceList().size() - 1).getBooking().getDropOffDateTime();
        return "\nThe drop-of date time of the driver's latest booking is at: " + driverDropOffDatetimeLatestBooking;
    }

    public static List<Invoice> invoiceListFilterByPeriod(List<Invoice> invoiceList, String startDate, String endDate) {
        if (startDate != null) {
            LocalDate start = DateUtility.StringToLocalDate(startDate);
            if (start == null) return null;
            invoiceList.removeIf(invoice -> invoice.getBooking().getPickUpDatetimeObj().isBefore(start.atStartOfDay()));
        }

        if (endDate != null) {
            LocalDate end = DateUtility.StringToLocalDate(endDate);
            if (end == null) return null;
            invoiceList.removeIf(invoice -> invoice.getBooking().getPickUpDatetimeObj().isAfter(end.atStartOfDay()));
        }

        return invoiceList;
    }
}
