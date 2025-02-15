## Train NL 

## Overview
This project is a Train Information Application for NS (Nederlandse Spoorwegen) built using Java, Spring Boot, and Vaadin. It displays a list of trains with their status, direction, planned departure time, and track information.

## Prerequisites
- JDK 17 or higher
- Maven 3.6.0 or higher

## Installation
1. Clone the repository:
    ```sh
    git clone https://github.com/your-repo/train-info-app.git
    cd train-info-app
    ```

2. Build the project using Maven:
    ```sh
    mvn clean install
    ```

3. Run the application:
    ```sh
    mvn spring-boot:run
    ```

## Usage
Once the application is running, open your web browser and navigate to `http://localhost:8080` to view the train list.

## API Key Configuration
To fetch train information from the NS API, you need to obtain an API key from ns.nl. Follow these steps:

1. Visit the [NS API portal](https://apiportal.ns.nl/).
2. Register for an account or log in if you already have one.
3. Create a new application and request an API key.
4. Once you have the API key, add it to the `application.properties` file:
    ```properties
    subscriptionKey=YOUR_API_KEY_HERE
    ```
## Setting UIC Code for Stations
To set the UIC code for a station, add the following property to the application.properties file:
```properties
stationUicCode=UIC_CODE
```
## Retrieving UIC Codes
To retrieve the UIC code for a specific station, you can find it [here](https://www.rijdendetreinen.nl/en/open-data/stations#downloads). The UIC code is a unique identifier for each station.
## License
This project is licensed under the MIT License.