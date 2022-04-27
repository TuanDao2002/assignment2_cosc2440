package cosc2440.asm2.taxi_company.utility;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class PagingUtility {
    private PagingUtility(){}

    public static <T> ResponseEntity<List<T>> getAll(List<T> list, int pageSize, int pageNumber) {
        // return empty if the retrieve Bookings are null or not found or the page size is less than 1 or page number is negative
        if (list == null || list.isEmpty() || pageSize < 1 || pageNumber < 0) {
            return new ResponseEntity<>(new ArrayList<>(), new HttpHeaders(), HttpStatus.OK);
        }

        Pageable paging = PageRequest.of(pageNumber, pageSize);
        int start = (int)paging.getOffset();
        int end = Math.min((start + paging.getPageSize()), list.size());

        // return empty if the page's start index is greater than page's end index
        if (start >= end) {
            return new ResponseEntity<>(new ArrayList<>(), new HttpHeaders(), HttpStatus.OK);
        }

        Page<T> pagedResult = new PageImpl<>(list.subList(start, end), paging, list.size());

        List<T> returnedList;
        if (pagedResult.hasContent()) {
            returnedList = pagedResult.getContent();
        } else {
            returnedList = new ArrayList<>();
        }

        return new ResponseEntity<>(returnedList, new HttpHeaders(), HttpStatus.OK);
    }
}
