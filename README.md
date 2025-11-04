# Pokedex - Spring Boot Application

## 📋 Description
A RESTful Pokédex API built with Spring Boot that provides information about Pokemon, including their descriptions, habitats, and legendary status. The application integrates with external Pokemon APIs to fetch and transform Pokemon data.

## 🛠️ Technologies
- Java 17
- Spring Boot 3.2.0
- Spring Web
- Lombok
- Maven
- TestNG (for testing)

## 📦 Project Structure
```
pokedex/
├── src/
│   ├── main/
│   │   ├── java/com/homechallenge/pokedex/
│   │   │   ├── PokedexApplication.java
│   │   │   ├── controller/
│   │   │   │   └── PokemonController.java
│   │   │   ├── service/
│   │   │   │   └── PokemonService.java
│   │   │   └── dto/
│   │   │       └── PokemonDTO.java
│   │   └── resources/
│   │       ├── application.yml
│   │       ├── application-dev.yml
│   │       └── application-prod.yml
│   └── test/
│       └── java/com/homechallenge/pokedex/
│           ├── controller/
│           │   └── PokemonControllerTest.java
│           └── service/
│               └── PokemonServiceTest.java
└── pom.xml
```

## 🚀 Getting Started

### Prerequisites
- JDK 17 or higher
- Maven 3.6+

### Build and Run
```bash
mvn clean install

# Run in development mode
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Or simply
mvn spring-boot:run
```

The application will be available at `http://localhost:8080`

## 🔍 Available Endpoints

### Pokemon API
```bash
# Get Pokemon information by name
GET http://localhost:8080/api/pokemon/{pokemonName}

# Get Pokemon information with translated description
GET http://localhost:8080/api/pokemon/translated/{pokemonName}
```

### Response Example
```json
{
  "id": 150,
  "name": "mewtwo",
  "description": "Created by a scientist after years of horrific gene splicing and dna engineering experiments, it was.",
  "habitat": "rare",
  "isLegendary": true
}
```

## 📝 Configuration

### Available Profiles
- **default**: Base configuration
- **dev**: Development configuration (DEBUG logging enabled)
- **prod**: Production configuration (minimal logging)

### Application Properties
- **Server Port**: `8080`
- **Application Name**: `pokedex`
- **Logging Level**: INFO (root), DEBUG (com.homechallenge)

## 🏗️ Features
- ✅ Fetch Pokemon information from external API
- ✅ Translate Pokemon descriptions
- ✅ RESTful API design
- ✅ Lombok integration for cleaner code
- ✅ Profile-based configuration (dev/prod)

## 📚 API Documentation
The application exposes REST endpoints under `/api/pokemon` base path. All responses are in JSON format.

## 🧪 Testing
```bash
# Run all tests
mvn test

# Run tests with coverage
mvn clean verify
```

