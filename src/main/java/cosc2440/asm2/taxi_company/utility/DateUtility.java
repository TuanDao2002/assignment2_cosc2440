package cosc2440.asm2.taxi_company.utility;

import cosc2440.asm2.taxi_company.model.Booking;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

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
}
