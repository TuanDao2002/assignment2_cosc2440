package cosc2440.asm2.taxi_company.utility;

import cosc2440.asm2.taxi_company.model.Booking;

import java.time.LocalDateTime;

public final class DateComparator {
    private DateComparator(){}

    public static boolean validateDatetimeOf(Booking findBooking) {
        if (findBooking.getPickUpDatetime() == null || findBooking.getDropOffDateTime() == null) return true;

        // convert pick-up and drop-off date time strings from Booking to LocalDateTime
        LocalDateTime pickUp = LocalDateTime.parse(findBooking.getPickUpDatetime(), Booking.getDateTimeFormatter());
        LocalDateTime dropOff = LocalDateTime.parse(findBooking.getDropOffDateTime(), Booking.getDateTimeFormatter());

        // check if drop-off date time is after pick-up date time. If not, it will be invalid
        return dropOff.isAfter(pickUp);
    }
}
