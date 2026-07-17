# Gradle Module Structure

## Module Dependency Table

| Module | Type | Path | Depends On | Consumers | Purpose |
|--------|------|------|-----------|-----------|---------|
| **primus-common** | Library | `primus-libs/primus-common` | None | All modules | Shared DTOs, exceptions, utilities |
| **primus-utils** | Library | `primus-libs/primus-utils` | primus-common | Export, Retrieval, Core | Helper utilities, string/date/collection utils |
| **primus-security** | Library | `primus-libs/primus-security` | primus-common | All services | JWT, OAuth2, RBAC, authentication |
| **primus-core** | Library | `primus-libs/primus-core` | primus-common, primus-utils, primus-security | Export, Retrieval, Services | Core business logic, transformations |
| **primus-export** | Library | `primus-annotations/primus-export` | primus-core | SDK, Services | Export engine, validation, transformation |
| **primus-retrieval** | Library | `primus-libs/primus-retrieval` | primus-core, primus-common | SDK, Services | Retrieval engine, caching, filtering |
| **primus-annotations** | Library | `primus-annotations/` | primus-common | All modules | Annotation framework, contract parsing |
| **primus-api** | Library | `primus-annotations/primus-api` | primus-common | SDK, Services | API contracts and decorators |
| **authenticator** | Library | `primus-annotations/authenticator` | primus-security | Services | Auth annotations and interceptors |
| **primus-client-sdk** | Library | `primus-libs/primus-client-sdk` | primus-core, primus-export, primus-retrieval | Applications | REST client SDK for applications |
| **primus** (Main Service) | Application | `primus-services/primus` | primus-core, primus-export, primus-retrieval, primus-security | External | Main microservice (registration, export, retrieve) |
| **primus-nas** | Application | `primus-services/primus-nas` | primus-core, primus-common | primus-server | NAS storage backend integration |
| **shadow-test** | Application | `primus-services/shadow-test` | primus-core, primus-export, primus-retrieval | External | Testing & validation service, A/B testing |
| **spring-client** | Library | `primus-services/spring-client` | primus-client-sdk, primus-security | Applications | Spring Boot integration client |
| **primus-angular** | UI (Angular) | `primus-ui/primus-angular` | None (front-end) | Browser | Main Angular UI application |
| **shadow-angular** | UI (Angular) | `primus-ui/shadow-angular` | None (front-end) | Browser | Experimental Angular UI |

## Module Organization

```
primus-platform/
│
├── primus-annotations/                          # Annotation Layer
│   ├── primus-export                           (Library)
│   ├── primus-api                              (Library)
│   └── authenticator                           (Library)
│
├── primus-libs/                                 # Library Layer
│   ├── primus-common                           (Library)
│   ├── primus-utils                            (Library)
│   ├── primus-security                         (Library)
│   ├── primus-core                             (Library)
│   ├── primus-retrieval                        (Library)
│   └── primus-client-sdk                       (Library)
│
├── primus-services/                             # Service Layer
│   ├── primus                                  (Spring Boot Application)
│   ├── primus-nas                              (Spring Boot Service)
│   ├── shadow-test                             (Spring Boot Service)
│   └── spring-client                           (Library)
│
└── primus-ui/                                   # UI Layer
    ├── primus-angular                          (Angular Application)
    └── shadow-angular                          (Angular Application)
```

## Dependency Graph

### Library Dependencies

```
┌─────────────────────────────────────┐
│      External Dependencies          │
│  Spring Boot, Jackson, JWT, etc.    │
└──────────────┬──────────────────────┘
               │
      ┌────────▼──────────┐
      │  primus-common    │  (No deps)
      └────────┬──────────┘
               │
   ┌───────────┼───────────────┬──────────────┐
   │           │               │              │
   ▼           ▼               ▼              ▼
primus-utils  primus-   primus-core  primus-
              security              annotations
   │           │           │              │
   └─────┬─────┴─────┬─────┴──────┬───────┘
         │           │            │
         │      ┌────▼────┐       │
         │      │ primus- │       │
         │      │ export  │       │
         │      └────┬────┘       │
         │           │            │
         └───┬───────┼────┬───────┘
             │       │    │
             ▼       ▼    ▼
      ┌────────────────────────┐
      │ primus-client-sdk      │
      │ (Consumed by Apps)     │
      └────────────────────────┘

primus-retrieval  ◄─── Depends on primus-core, primus-common
                       Used by primus-server, primus-client-sdk
```

### Service Dependencies

```
External Dependencies
         │
    ┌────┴──────────────┬───────────────┐
    │                  │               │
    ▼                  ▼               ▼
primus-core      primus-export    primus-retrieval
    │                 │                  │
    └─────────┬───────┴──────────────────┘
              │
       ┌──────▼──────────────────┐
       │   primus-server         │
       │   (Main Service)        │
       └──────┬──────────────────┘
              │
    ┌─────────┼──────────┐
    │         │          │
    ▼         ▼          ▼
primus-nas shadow-test spring-client
```

## Build Commands

### Build All Modules
```bash
./gradlew clean build
```

### Build Specific Module
```bash
./gradlew :primus-platform:primus-libs:primus-common:build
./gradlew :primus-platform:primus-services:primus:build
```

### Run Tests
```bash
./gradlew test
./gradlew :primus-platform:primus-core:test
```

### Build Specific Service
```bash
# Build and run primus-server
./gradlew :primus-platform:primus-services:primus:bootRun

# Build and run primus-nas
./gradlew :primus-platform:primus-services:primus-nas:bootRun
```

### Generate Gradle Wrapper
```bash
gradle wrapper --gradle-version=8.1
```

### List All Projects
```bash
./gradlew projects
```

### View Dependency Tree
```bash
./gradlew dependencies
./gradlew :primus-platform:primus-services:primus:dependencies
```

## Module Definitions (build.gradle)

### Library Module Template
```gradle
plugins {
    id 'java-library'
}

dependencies {
    api 'external:dependency'  // Exposed to consumers
    implementation 'internal:dependency'  // Hidden from consumers
    testImplementation 'test:framework'
}
```

### Service Module Template
```gradle
plugins {
    id 'java'
    id 'org.springframework.boot' version '3.1.0'
    id 'io.spring.dependency-management' version '1.1.0'
}

dependencies {
    implementation project(':primus-platform:primus-core')
    implementation 'org.springframework.boot:spring-boot-starter-web'
}

springBoot {
    mainClass = 'com.primus.service.ServiceApplication'
}
```

### Angular Module Template (package.json)
```json
{
  "name": "primus-angular",
  "version": "1.0.0",
  "scripts": {
    "ng": "ng",
    "start": "ng serve",
    "build": "ng build",
    "test": "ng test"
  },
  "dependencies": {
    "@angular/core": "^16.0.0"
  }
}
```

## Module Load Order

Gradle automatically resolves dependencies in correct order:

1. **primus-common** - No dependencies
2. **primus-utils** - Depends on primus-common
3. **primus-security** - Depends on primus-common
4. **primus-core** - Depends on primus-common, primus-utils, primus-security
5. **primus-export** - Depends on primus-core
6. **primus-retrieval** - Depends on primus-core
7. **primus-client-sdk** - Depends on export, retrieval, core
8. **primus-server** - Depends on core, export, retrieval, security
9. **primus-nas** - Depends on core, common
10. **shadow-test** - Depends on core, export, retrieval
11. **UI modules** - No backend dependencies

## Adding New Modules

1. **Create directory**:
```bash
mkdir -p primus-platform/new-module
```

2. **Add to settings.gradle**:
```gradle
include ':primus-platform:new-module'
```

3. **Create build.gradle**:
```gradle
plugins {
    id 'java'
}

dependencies {
    implementation project(':primus-platform:primus-common')
}
```

4. **Create source layout**:
```bash
mkdir -p src/main/{java,resources}
mkdir -p src/test/{java,resources}
```

5. **Build**:
```bash
./gradlew :primus-platform:new-module:build
```

Next → [Data Contracts](./03-contracts.md)

