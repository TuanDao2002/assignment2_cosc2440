# Car Booking System

### Course Information
- University: Royal Melbourne Institution of Technology (RMIT)
- Campus: Saigon South (SGS), Vietnam


- Course: Software Architecture Design and Implementation (or Further Programming)
- Course code: COSC2440
- Lecturer: Mr. Minh Vu Thanh


- Assignment number: 2
- Assignment title: Build a backend
- Assignment type: Group assignment - code and report submission

### Contributors
- Supervisor: Mr. Minh Vu Thanh
- Developer: Dao Kha Tuan (s3877347)
- Developer: Nguyen Luu Quoc Bao (s3877698)
- Developer: Nguyen Trong Minh Long (s3878694)
- Developer: Bui Quang An (s3877482)

### Project Information
- Project name: Car Booking System
- Purpose: Manage car booking of users and invoices for company
- Functionalities: basic CRUD (Create - Read - Update - Delete) booking, connect the system to work as a real system
- Technologies used: Java (jdk16), JUnit4, Spring MVC (CRUD repository)


- Project started date: 21 April 2022
- Project finished date: 15 May 2022


### To Run
- Run the Main.java or type in terminal
```
./mvnw spring:run
```

### API call
#### Manage car
1. Get all car
- GET request: http://localhost:8080/car (it will automatically call with pageSize = 20 and pageNumber = 0)
- GET request with custom paging and size: http://localhost:8080/car?page=0&&size=20
- GET request all available car: http://localhost:8080/car?getByAvailable=true

2. Get one car
- GET request car by id: http://localhost:8080/car/{id}
- GET request car by attribute: http://localhost:8080/car/attribute?attributeName=model&attributeValue=GLC63 (custom attribute name and attribute value). Note: available attribute are make, model, licensePlate

3. Add car
- POST request: http://localhost:8080/admin/car with body:
```
{
    "make": "Toyota",
    "model": "GLC63",
    "color": "white",
    "convertible": "false",
    "rating": "5.0",
    "licensePlate": "59F-23537",
    "ratePerKilometer": "44.22",
    "available": "true"
}
```

4. Update car
- PUT request: http://localhost:8080/admin/car with body:
```
 {
    "vin": "2",
    "make": "Mercedes",
    "model": "GLC300",
    "color": "red",
    "convertible": "true",
    "rating": "3.0",
    "licensePlate": "0x237",
    "ratePerKilometer": "32.34",
    "available": "true"
} 
 ```

5. Delete car
- DELETE request: http://localhost:8080/admin/car/{id}

6. Get car by day used
- GET request: http://localhost:8080/admin/car/day?month={month}&year={year}. It will respond with a List<Map<String:Integer>>:
```
[
    // Each object contains plate : day used
    {
        "59F-23537": 2
    }
]
```

#### Manage driver
1. Get all driver
- GET request: http://localhost:8080/admin/driver (it will automatically call with pageSize = 20 and pageNumber = 0)
- GET request with custom paging and size: http://localhost:8080/admin/driver?page=0&&size=20

2. Get one driver
- GET request car by id: http://localhost:8080/admin/driver/{id}
- GET request car by attribute: http://localhost:8080/admin/driver/attribute?attributeName=phoneNumber&attributeValue=0903123456 (custom attribute name and attribute value). Note: available attribute are license number and phone number

3. Add driver
- POST request: http://localhost:8080/admin/driver with body:
```
{
    "licenseNumber": "02245462",
    "phoneNumber": "0903123456",
    "rating": "4.4"
}
```

4. Update driver
- PUT request: http://localhost:8080/admin/driver with body:
```
{
    "id": "2",
    "licenseNumber": "1234557",
    "phoneNumber": "09323456",
    "rating": "2.25"
}
```

5. Delete driver
- DELETE request: http://localhost:8080/admin/driver/{id}

6. Assign car to driver
- GET request: http://localhost:8080/driver/pick?carVIN={carVIN}&driverId={driverId}

#### Manage customer
1. Get all customer
- GET request: http://localhost:8080/admin/customer (it will automatically call with pageSize = 20 and pageNumber = 0)
- GET request with custom paging and size: http://localhost:8080/admin/customer?page=0&&size=20

2. Get one customer
- GET request: http://localhost:8080/admin/customer/{id}

3. Add customer
- POST request: http://localhost:8080/admin/customer with body:
```
{
    "name": "Quoc Bao",
    "phoneNumber": "0909 90 009",
    "address": "Quan 3"
}
```

4. Update customer
- PUT request: http://localhost:8080/admin/customer with body: 
```
{
    "id": "1",
    "name": "Quoc Bao",
    "phoneNumber": "0909 90 009",
    "address": "Quan 5"
}
```

5. Delete customer
- DELETE request: http://localhost:8080/admin/customer/1

#### Manage booking
1. Get all booking
- GET request: http://localhost:8080/admin/booking (it will automatically call with pageSize = 20 and pageNumber = 0)
- GET request with custom paging and size: http://localhost:8080/admin/booking?page=0&&size=20

2. Get one booking
- GET request: http://localhost:8080/booking/{id}

3. Add booking
- POST request: http://localhost:8080/admin/booking with body:
```
{
    "startLocation": "Vietnam",
    "endLocation": "USA",
    "pickUpDatetime": "12:09:09 12-09-2020",
    "invoice": {
        "driver": {
            "id": "1"
        },
        "customer":{
            "id": "2"
        }
    }
}
```

4. Update booking
- PUT request: http://localhost:8080/admin/booking with body:
```
{
    "bookingID": "1",
    "startLocation": "Vietnam",
    "endLocation": "Lao",
    "pickUpDatetime": "12:09:09 12-09-2020",
    "invoice": {
        "driver": {
            "id": "1"
        },
        "customer":{
            "id": "2"
        }
    }
}
```

5. Delete booking
- DELETE request: http://localhost:8080/admin/booking/{id}

6. Booking created from car booked
- POST request: http://localhost:8080/booking/bookCar?carVIN={carVIN}&customerID={customerId}&startLocation={startLocation}&endLocation={endLocation}&pickUpDatetime=hh:mm:ss dd-MM-uuuu

7. Finalize booking (drop customer)
- POST request: http://localhost:8080/admin/booking/finalize?bookingID={bookingID}&dropOffDatetime=hh:mm:ss dd-MM-uuuu&distance={distance}

#### Manage invoice
1. Get all invoice
- GET request: http://localhost:8080/admin/invoice (it will automatically call with pageSize = 20 and pageNumber = 0)
- GET request with custom paging and size: http://localhost:8080/admin/invoice?page=0&&size=20

2. Get one invoice
- GET request: http://localhost:8080/admin/invoice/{id}

3. Add invoice
- POST request: http://localhost:8080/admin/invoice with body:
```
{
    "totalCharge": "345",
    "driver": {
        "id": "1"
    },
    "booking": {
        "startLocation": "hcm",
        "endLocation": "new york",
        "pickUpDatetime": "09:09:09 09-09-2022"
    },
    "customer": {
        "id": "1"
    }
}
```

4. Update invoice
- PUT request: http://localhost:8080/admin/invoice with body:
```
{
    "invoiceID: "1",
    "totalCharge": "345",
    "driver": {
        "id": "1"
    },
    "booking": {
        "startLocation": "hcm",
        "endLocation": "new york",
        "pickUpDatetime": "09:09:09 09-09-2022"
    },
    "customer": {
        "id": "1"
    }
}
```

5. Delete invoice
- DELETE request: http://localhost:8080/admin/invoice/{id}

6. Get revenue
- GET request all revenue: http://localhost:8080/admin/invoice/revenue
- GET request revenue by period: http://localhost:8080/admin/invoice/revenue?startDate={dd-mm-yyyy}&endDate={dd-mm-yyyy}
- GET request revenue by customer: http://localhost:8080/admin/invoice/revenue?customerId={customerId}
- GET request revenue by driver: http://localhost:8080/admin/invoice/revenue?driverId={driverId}
- GET request revenue by customer in a period: http://localhost:8080/admin/invoice/revenue?startDate={dd-mm-yyyy}&endDate={dd-mm-yyyy}&customerId={customerId}
- GET request revenue by driver in a period: http://localhost:8080/admin/invoice/revenue?startDate={dd-mm-yyyy}&endDate={dd-mm-yyyy}&driverId={driverId}


### Supporting tools used
- LucidChart: for drawing UML, including use case diagram and class diagram (attached on zip file)
- IntelliJ: for configuration and coding the app
- Microsoft Word 2022: for documenting technical report
- Spring initializer: for initializing the project
- Postman API: to test the APIs

### References
