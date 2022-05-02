package cosc2440.asm2.taxi_company.controller;

import cosc2440.asm2.taxi_company.model.Invoice;
import cosc2440.asm2.taxi_company.service.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class InvoiceController {
    @Autowired
    private InvoiceService invoiceService;

    // controller for invoice
    @RequestMapping(path = "/admin/invoice", method = RequestMethod.GET)
    public ResponseEntity<List<Invoice>> getAllInvoices(@RequestParam(value = "page", defaultValue = "0") int page,
                                                        @RequestParam(value = "size", defaultValue = "20") int size,
                                                        @RequestParam(value = "matchDate", required = false) String matchDate,
                                                        @RequestParam(value = "startDate", required = false) String startDate,
                                                        @RequestParam(value = "endDate", required = false) String endDate) {
        return invoiceService.getAll(page, size, matchDate, startDate, endDate);
    }

    @RequestMapping(path = "/admin/invoice/{invoiceID}", method = RequestMethod.GET)
    public Invoice getInvoiceById(@PathVariable Long invoiceID) {
        return invoiceService.getOne(invoiceID);
    }

    @RequestMapping(path = "/admin/invoice/byDriverID", method = RequestMethod.GET)
    public ResponseEntity<List<Invoice>> getByDriverID(@RequestParam(value = "page", defaultValue = "0") int page,
                                                       @RequestParam(value = "size", defaultValue = "20") int size,
                                                       @RequestParam(value = "driverID") Long driverID,
                                                       @RequestParam(value = "startDate", required = false) String startDate,
                                                       @RequestParam(value = "endDate", required = false) String endDate) {
        return invoiceService.getByDriverID(page, size, driverID, startDate, endDate);
    }

    @RequestMapping(path = "/admin/invoice/byCustomerID", method = RequestMethod.GET)
    public ResponseEntity<List<Invoice>> getByCustomerID(@RequestParam(value = "page", defaultValue = "0") int page,
                                                         @RequestParam(value = "size", defaultValue = "20") int size,
                                                         @RequestParam(value = "customerID") Long customerID,
                                                         @RequestParam(value = "startDate", required = false) String startDate,
                                                         @RequestParam(value = "endDate", required = false) String endDate) {
        return invoiceService.getByCustomerID(page, size, customerID, startDate, endDate);
    }

    @RequestMapping(path = "/admin/invoice", method = RequestMethod.POST)
    public String addInvoice(@RequestBody Invoice invoice) {
        return invoiceService.add(invoice);
    }

    @RequestMapping(path = "/admin/invoice", method = RequestMethod.PUT)
    public String updateInvoice(@RequestBody Invoice invoice) {
        return invoiceService.update(invoice);
    }

    @RequestMapping(path = "/admin/invoice/{invoiceID}", method = RequestMethod.DELETE)
    public String deleteInvoice(@PathVariable Long invoiceID) {
        return invoiceService.delete(invoiceID);
    }

    @RequestMapping(path = "/admin/revenue", method = RequestMethod.GET)
    public double getRevenue(@RequestParam(value = "startDate", required = false) String startDate,
                             @RequestParam(value = "endDate", required = false) String endDate,
                             @RequestParam(value = "driverId", required = false) Long driverId,
                             @RequestParam(value = "customerId", required = false) Long customerId) {
        return driverId != null ? invoiceService.getRevenueByDriver(startDate, endDate, driverId) :
                customerId != null ? invoiceService.getRevenueByCustomer(startDate, endDate, customerId) :
                        invoiceService.getRevenue(startDate, endDate);
    }

}
