# primus-ui

Angular-based UI for Primus Platform.

## Overview

`primus-ui` provides the web-based user interface for the Primus Platform, allowing users to search, export, and retrieve data through an intuitive dashboard.

## Quick Start

### Build and Run

```bash
cd primus-platform/primus-ui

# Install dependencies
npm install

# Dev server
npm start

# Build for production
npm run build

# Run tests
npm test
```

### Access UI

```
http://localhost:4200
```

## Features

### Search Interface
- Full-text search across multiple collections
- Faceted filtering
- Saved searches
- Sort and pagination

### Export Management
- Create new exports with collection selection
- View export history and status
- Download exported data
- Track export progress

### Data Retrieval
- Browse retrieved data
- View collection details
- Inspect relationships
- Export to multiple formats (JSON, CSV, XML)

### Administration
- Application registration
- User management
- Audit log viewer
- Configuration management

## Architecture

```
src/
├── app/
│   ├── core/
│   │   ├── guards/
│   │   ├── interceptors/
│   │   └── services/
│   ├── features/
│   │   ├── search/
│   │   ├── export/
│   │   ├── retrieval/
│   │   └── admin/
│   ├── shared/
│   │   ├── components/
│   │   ├── directives/
│   │   └── pipes/
│   └── app.component.ts
├── assets/
├── environments/
└── styles/
```

## Configuration

```typescript
// environment.ts
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080/primus/api/v1',
  enableLogging: true,
  cacheTimeout: 300000
};
```

## Key Components

### Search Component
```typescript
// Features: full-text search, filtering, facets
SearchComponent
├── SearchInputComponent
├── FiltersComponent
├── FacetsComponent
└── ResultsGridComponent
```

### Export Component
```typescript
// Features: create export, monitor progress, download
ExportComponent
├── ExportFormComponent
├── ExportHistoryComponent
└── ExportDetailComponent
```

### Authentication
```typescript
// AuthGuard protects routes
CanActivate: AuthGuard
```

## Testing

```bash
# Unit tests
npm test

# E2E tests
npm run e2e

# Coverage
npm run test:coverage
```

## Dependencies

### Node Modules
- `@angular/core` — Angular framework
- `@angular/material` — Material design
- `@ngrx/store` — State management
- `rxjs` — Reactive extensions
- `moment` — Date/time handling

## Build

### Development Build
```bash
npm start
```

### Production Build
```bash
npm run build
```

### Docker Build
```bash
docker build -t primus-ui:latest .
docker run -p 80:4200 primus-ui:latest
```

## Testing

```bash
./npm test
```

## Module Dependencies

- Backend: `primus-server` (REST API)

## Troubleshooting

**Issue**: "Cannot connect to API"
- **Solution**: Verify `apiUrl` in environment.ts matches backend URL

**Issue**: "401 Unauthorized"
- **Solution**: Ensure JWT token is stored in localStorage after login

**Issue**: "CORS error"
- **Solution**: Verify backend has CORS headers enabled

**Issue**: "Blank page"
- **Solution**: Check browser console for errors, verify Angular build completed

## Best Practices

✅ Use reactive forms for validation  
✅ Implement proper error handling  
✅ Use RxJS operators for data transformation  
✅ Keep components focused and reusable  
✅ Test all user interactions  

---

**Module Status**: ✅ Implemented (v0.1)  
**Tier**: User Interface  
**Last Updated**: July 2026

