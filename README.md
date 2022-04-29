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
- Technologies used: Java (jdk16), JUnit5, Spring MVC


- Project started date: 21 April 2022
- Project finished date: 15 May 2022


### To Run
- Run the Main.java or type "./mvnw spring:run" in terminal

### API call
#### Manage car
1. Get all car
- GET request: http://localhost:8080/car (it will automatically call with pageSize = 5 and pageNumber = 0)
- GET request with custom paging and size: http://localhost:8080/car?page=0&size=5
- GET request all available car: http://localhost:8080/car?getByAvailable=true

2. Get one car
- GET request car by id: http://localhost:8080/car/{id}
- GET request car by attribute: http://localhost:8080/car/attribute?attributeName=model&attributeValue=GLC63 (custom attribute name and attribute value). Note: available attribute are make, model, licensePlate

3. Add car
- POST request: http://localhost:8080/car with body:
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
- PUT request: http://localhost:8080/car with body:
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
- DELETE request: http://localhost:8080/car/{id}

6. Get car by day used
- GET request: http://localhost:8080/car/day?month={month}&year={year}. It will respond with a List<Map<String:Integer>>:
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
- GET request: http://localhost:8080/driver (it will automatically call with pageSize = 5 and pageNumber = 0)
- GET request with custom paging and size: http://localhost:8080/driver?page=0&size=5

2. Get one driver
- GET request car by id: http://localhost:8080/driver/{id}
- GET request car by attribute: http://localhost:8080/driver/attribute?attributeName=phoneNumber&attributeValue=0903123456 (custom attribute name and attribute value). Note: available attribute are license number and phone number

3. Add driver
- POST request: http://localhost:8080/driver with body:
```
{
    "licenseNumber": "02245462",
    "phoneNumber": "0903123456",
    "rating": "4.4"
}
```

4. Update driver
- PUT request: http://localhost:8080/driver with body:
```
{
    "id": "2",
    "licenseNumber": "1234557",
    "phoneNumber": "09323456",
    "rating": "2.25"
}
```

5. Delete driver
- DELETE request: http://localhost:8080/driver/{id}

6. Assign car to driver
- GET request: http://localhost:8080/driver/pick?carVIN={carVIN}&driverId={driverId}

#### Manage customer
1. Get all customer
- GET request: http://localhost:8080/customer (it will automatically call with pageSize = 5 and pageNumber = 0)
- GET request with custom paging and size: http://localhost:8080/customer?page=0&size=5

2. Get one customer
- GET request: 

### Supporting tools used
- LucidChart: for drawing UML, including use case diagram and class diagram (attached on zip file)
- IntelliJ: for configuration and coding the app
- Microsoft Word 2022: for documenting technical report
- Spring initializer: for initializing the project

### References
