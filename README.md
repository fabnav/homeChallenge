# Pokedex - Spring Boot Application

## 📋 Description
A RESTful Pokédex API built with Spring Boot that provides information about Pokemon, including their descriptions, habitats, and legendary status. The application integrates with external Pokemon APIs to fetch and transform Pokemon data, with optional fun translations (Yoda or Shakespeare style).

## 🛠️ Technologies
- Java 17
- Spring Boot 3.2.0
- Spring Web
- Spring Boot Actuator (for health checks)
- Lombok
- Maven
- TestNG (for testing)
- Mockito (for mocking)
- Docker & Docker Compose
- Spotless (code formatting)

## 📦 Project Structure
```
pokedex/
├── src/
│   ├── main/
│   │   ├── java/com/homechallenge/pokedex/
│   │   │   ├── PokedexApplication.java
│   │   │   ├── config/
│   │   │   │   └── AppConfig.java
│   │   │   ├── controller/
│   │   │   │   └── PokemonController.java
│   │   │   ├── service/
│   │   │   │   └── PokemonService.java
│   │   │   ├── dto/
│   │   │   │   └── PokemonDTO.java
│   │   │   ├── exception/
│   │   │   │   └── PokemonNotFoundException.java
│   │   │   └── util/
│   │   │       └── PokemonUtils.java
│   │   └── resources/
│   │       ├── application.yml
│   │       ├── application-dev.yml
│   │       └── application-prod.yml
│   └── test/
│       └── java/com/homechallenge/pokedex/
│           ├── controller/
│           │   └── PokemonControllerTest.java
│           ├── service/
│           │   └── PokemonServiceTest.java
│           └── helper/
│               └── HttpRequestHelper.java
├── Dockerfile
├── docker-compose.yml
├── .dockerignore
├── Makefile
├── DOCKER.md
└── pom.xml
```

## 🚀 Getting Started

### Prerequisites
- JDK 17 or higher
- Maven 3.6+
- Docker (optional, for containerized deployment)
- Docker Compose (optional)

### Quick Start with Makefile

The easiest way to work with the project:

```bash
# Show all available commands
make help

# Run tests
make test

# Format code
make format

# Build Docker image and run
make dockup

# Stop and clean up
make dockclean
```

### Build and Run Locally

```bash
# Clean and build
mvn clean install

# Run in development mode
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Run in production mode
mvn spring-boot:run -Dspring-boot.run.profiles=prod

# Or simply (uses default profile)
mvn spring-boot:run
```

The application will be available at `http://localhost:8080`

## 🔍 Available Endpoints

### Pokemon API

#### Get Pokemon Information
```bash
GET /pokemon/{pokemonName}

# Example
curl http://localhost:8080/pokemon/pikachu
```

**Response:**
```json
{
  "id": 25,
  "name": "pikachu",
  "description": "When several of these POKéMON gather, their electricity could build and cause lightning storms.",
  "habitat": "forest",
  "isLegendary": false
}
```

#### Get Pokemon with Translated Description
```bash
GET /pokemon/translated/{pokemonName}

# Example - Cave habitat or legendary Pokemon get Yoda translation
curl http://localhost:8080/pokemon/translated/mewtwo
```

**Response:**
```json
{
  "id": 150,
  "name": "mewtwo",
  "description": "Created by a scientist after years of horrific gene splicing and dna engineering experiments, it was.",
  "habitat": "rare",
  "isLegendary": true
}
```

**Translation Rules:**
- 🧙 **Yoda translation**: For legendary Pokemon or cave habitat
- 🎭 **Shakespeare translation**: For all other Pokemon
- If translation fails, returns original description

### Health Check (Actuator)

```bash
GET /actuator/health

# Example
curl http://localhost:8080/actuator/health
```

**Response:**
```json
{
  "status": "UP"
}
```

## 📝 Configuration

### Available Profiles
- **default**: Base configuration
- **dev**: Development configuration (DEBUG logging enabled)
- **prod**: Production configuration (minimal logging)

### Application Configuration
- **Server Port**: `8080`
- **Application Name**: `pokedex`
- **Logging Level**: INFO (root), DEBUG (com.homechallenge)
- **PokeAPI Base URL**: `https://pokeapi.co/api/v2`
- **FunTranslations API**: `https://api.funtranslations.com`

## 📈 API Examples

### Get Pikachu
```bash
curl http://localhost:8080/pokemon/pikachu | jq
```

### Get Mewtwo with Translation
```bash
curl http://localhost:8080/pokemon/translated/mewtwo | jq
```

### Get Zubat with Translation (Cave habitat → Yoda)
```bash
curl http://localhost:8080/pokemon/translated/zubat | jq
```

### Get Pikachu with Translation (Forest habitat → Shakespeare)
```bash
curl http://localhost:8080/pokemon/translated/pikachu | jq
```

### Check Health
```bash
curl http://localhost:8080/actuator/health | jq
```

**Built with ❤️ using Spring Boot and Docker**
