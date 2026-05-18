# Media Ratings Platform

**GitHub Repository:** https://github.com/DeeQay/FHTW-SWEN1_MRP

Eine REST-API für Medien-Bewertungen mit Rating-System, Favoriten und Empfehlungen.

## Features

- **User Management**: Registrierung, Login mit tokenbasierter Authentifizierung
- **Media Management**: Erstellen, Bearbeiten und Löschen von Medien
- **Rating-System**: 5-Sterne-Bewertungen mit Kommentaren
- **Like-System**: Ratings können geliked werden
- **Favorites**: Medien als Favoriten markieren
- **Search & Filter**: Suche nach Titel, Genre, MediaType, Jahr, Altersfreigabe und Bewertung
- **Sortierung**: Nach Titel, Jahr oder Bewertung
- **User Profile**: Statistiken wie Total Ratings, Average Score und Favorite Genre
- **Leaderboard**: Top-User nach Anzahl der Ratings
- **Recommendations**: Genre-basierte und Content-basierte Empfehlungen

## Technologien

- Java 21
- Maven
- PostgreSQL
- Docker & Docker Compose
- Jackson
- Lombok
- JUnit 5
- Mockito
- com.sun.net.httpserver als eigener HTTP-Server ohne Framework

## Setup

### Voraussetzungen

- Java 21 oder höher
- Maven
- Docker Desktop
- Docker Compose

### Lokale Konfiguration

Für die lokale Entwicklung wird eine `.env` Datei verwendet. Diese Datei enthält lokale Zugangsdaten und wird nicht ins Repository committed.

Die `.env` Datei liegt im Hauptordner des Projekts, also neben `pom.xml`, `docker-compose.yml` und `azure-pipelines.yml`.

Beispiel:

```env
POSTGRES_DB=mrp_db
POSTGRES_USER=mrp_user
POSTGRES_PASSWORD=<local-password>

DB_URL=jdbc:postgresql://localhost:5432/mrp_db
DB_USERNAME=mrp_user
DB_PASSWORD=<local-password>

Die produktive oder pipelinebasierte Bereitstellung der Secrets erfolgt nicht über Git, sondern über Azure DevOps Secret Variables oder Azure Key Vault.

Installation und Start
PostgreSQL mit Docker Compose starten:
docker compose up -d
Projekt bauen und Tests ausführen:
mvn clean package
Server starten:
java -jar target/Media-Ratings-Platform-1.0-SNAPSHOT.jar

Der Server läuft danach auf:

http://localhost:8080
Datenbank-Konfiguration
Host: localhost:5432
Datenbank: mrp_db
Benutzer: mrp_user
Passwort: wird nicht im Repository gespeichert, sondern lokal über .env und in Azure DevOps über Secret Variables oder Azure Key Vault bereitgestellt.
API Endpoints
Authentifizierung
POST /api/users/register - User registrieren
POST /api/users/login - Login mit Token
Media
GET /api/media - Alle Medien mit Filter und Sortierung
POST /api/media - Medium erstellen
GET /api/media/{id} - Einzelnes Medium abrufen
PUT /api/media/{id} - Medium bearbeiten
DELETE /api/media/{id} - Medium löschen
Ratings und Likes
POST /api/media/{mediaId}/rate - Rating erstellen
PUT /api/ratings/{ratingId} - Rating bearbeiten
DELETE /api/ratings/{ratingId} - Rating löschen
POST /api/ratings/{ratingId}/confirm - Kommentar bestätigen
POST /api/ratings/{ratingId}/like - Rating liken
DELETE /api/ratings/{ratingId}/like - Like entfernen
Favorites
POST /api/media/{mediaId}/favorite - Medium als Favorit markieren
DELETE /api/media/{mediaId}/favorite - Favorit entfernen
GET /api/users/{username}/favorites - Favoritenliste abrufen
User Profile und Leaderboard
GET /api/users/{username}/profile - Profil mit Statistiken abrufen
PUT /api/users/{username}/profile - Profil aktualisieren
GET /api/users/{username}/ratings - Rating-History abrufen
GET /api/users/{username}/recommendations - Empfehlungen abrufen
GET /api/leaderboard - Top-User abrufen

Die vollständige API-Spezifikation befindet sich in:

openapi-mrp.yaml
Testing

Die Tests können lokal mit Maven ausgeführt werden:

mvn test

Für API-Tests kann die Postman Collection importiert werden:

postman_collection.json
Architektur

Das Projekt verwendet eine klassische Schichtenarchitektur:

Controller -> Service -> DAO -> PostgreSQL
Controller

Die Controller sind für HTTP-Routing, Request-Verarbeitung und Response-Erstellung zuständig.

Service

Die Service-Schicht enthält die Geschäftslogik, zum Beispiel Registrierung, Login, Medienverwaltung, Ratings, Favoriten und Empfehlungen.

DAO

Die DAO-Schicht übernimmt den direkten Datenbankzugriff auf PostgreSQL.

Entities

Wichtige Entities:

User
Media
Rating
RatingLike
Favorite
DTOs

DTOs werden für Request- und Response-Objekte verwendet.

Weitere Details befinden sich in:

protocol.md
Azure DevOps

Für das DevOps-Projekt wurde eine Azure Pipeline vorbereitet.

Die Pipeline führt folgende Schritte aus:

PostgreSQL für Tests mit Docker Compose starten
Maven Build und Tests ausführen
Eine ausführbare JAR-Datei erzeugen
Die JAR-Datei als Pipeline Artifact veröffentlichen

Die Pipeline-Konfiguration befindet sich in:

azure-pipelines.yml

Secrets wie Datenbankpasswörter werden nicht im Repository gespeichert. In Azure DevOps werden diese Werte über Secret Variables oder Azure Key Vault bereitgestellt.



