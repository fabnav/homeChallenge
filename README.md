# Pokedex - Spring Boot Application

## 📋 Description
A RESTful Pokédex API built with Spring Boot that provides information about Pokemon, including their descriptions, habitats, and legendary status. The application integrates with external Pokemon APIs to fetch and transform Pokemon data, with optional fun translations (Yoda or Shakespeare style).

## 🏗️ Design Decisions

### Architecture
- clear separation between Controller, Service, and DTO layers
- used `RestClient` for cleaner, more maintainable HTTP calls
- `AppConfig` bean for all external service URLs
- `PokemonNotFoundException` with proper HTTP status codes
- `PokemonUtils` for description extraction and cleaning logic

### Code Quality
- DTOs use records for immutability and clarity
- null-safe operations with proper fallbacks
- structured logging for debugging and monitoring
- separate configurations for dev/prod environments

### Testing Strategy
- Service layer and Controller tests with mocked dependencies
- Custom test helper for centralizing and simulating external API responses
- Tests for not-found scenarios, null handling, and translation fallbacks

### What I'd Add for Production

#### **Resilience & Reliability**
- ensure the application can handle failures, especially when dealing with external APIs.
- implement exponential backoff and circuit breakers.
- consider a proper timeout strategy.
- respect the rate limits of the free API.

#### **Caching**
- caching would improve performance and reduce external API calls, given that Pokemon data is relatively static.
- setup different TTLs between normal and translated requests.

#### **Observability**
- Prometheus + Grafana for monitoring

#### **Security**
- JWT tokens for API authentication
- enhanced input validation
- CORS policies for frontend integration

#### **Operational Excellence**
- helm charts for K8s orchestration
- GitHub Actions/GitLab CI for automated testing and deployment
- Terraform/CloudFormation for infrastructure as code

#### **API Documentation**
- OpenAPI/Swagger documentation

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

# Setup pre-commit hooks
make setup

# Start application
make start

# Run tests
make test

# Build Docker image and run
make dockup

# Stop and clean up
make dockclean

# Format code
make format
```

### Build and Run Locally

```bash
# Clean and build
mvn clean install

# Run with default profile
mvn spring-boot:run

# Run in development mode
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Run in production mode
mvn spring-boot:run -Dspring-boot.run.profiles=prod
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
