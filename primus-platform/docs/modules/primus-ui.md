# primus-ui Module

## Overview

Main Angular UI application for Primus platform with generated components, forms, and data management interfaces.

## Module Location

```
primus-platform/primus-ui/primus-angular/
```

## Purpose

Provides web-based user interface for:
- Contract browsing and management
- Export creation and monitoring
- Data retrieval and filtering
- User administration
- System monitoring and reporting

## Project Structure

```
primus-angular/
├── src/
│   ├── app/
│   │   ├── core/
│   │   │   ├── services/
│   │   │   │   ├── api.service.ts
│   │   │   │   ├── auth.service.ts
│   │   │   │   └── storage.service.ts
│   │   │   ├── guards/
│   │   │   │   ├── auth.guard.ts
│   │   │   │   └── permission.guard.ts
│   │   │   └── interceptors/
│   │   │       └── auth.interceptor.ts
│   │   ├── shared/
│   │   │   ├── components/
│   │   │   ├── directives/
│   │   │   ├── pipes/
│   │   │   └── models/
│   │   ├── features/
│   │   │   ├── contracts/
│   │   │   ├── exports/
│   │   │   ├── data/
│   │   │   └── admin/
│   │   └── app.module.ts
│   ├── assets/
│   └── styles/
├── angular.json
├── package.json
└── tsconfig.json
```

## Key Modules

### Core Module

```typescript
import { NgModule } from '@angular/core';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { AuthInterceptor } from './interceptors/auth.interceptor';

@NgModule({
  imports: [HttpClientModule],
  providers: [
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthInterceptor,
      multi: true
    }
  ]
})
export class CoreModule { }
```

### Feature Modules

#### Contracts Module

```typescript
@NgModule({
  declarations: [
    ContractListComponent,
    ContractDetailComponent,
    ContractFormComponent
  ],
  imports: [
    CommonModule,
    MaterialModule,
    ReactiveFormsModule
  ]
})
export class ContractsModule { }
```

#### Exports Module

```typescript
@NgModule({
  declarations: [
    ExportListComponent,
    ExportDetailComponent,
    ExportFormComponent,
    ExportStatusComponent
  ],
  imports: [
    CommonModule,
    MaterialModule,
    ReactiveFormsModule
  ]
})
export class ExportsModule { }
```

## Generated Components

### Auto-Generated Form

```typescript
// Generated from UserProfile contract
@Component({
  selector: 'app-user-profile-form',
  templateUrl: './user-profile-form.component.html',
  styleUrls: ['./user-profile-form.component.css']
})
export class UserProfileFormComponent implements OnInit {
  form: FormGroup;
  submitted = false;
  
  constructor(
    private fb: FormBuilder,
    private service: UserProfileService
  ) {
    this.form = this.fb.group({
      userId: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      fullName: ['', Validators.required],
      status: ['ACTIVE', Validators.required]
    });
  }
  
  onSubmit() {
    if (this.form.valid) {
      this.service.create(this.form.value).subscribe(
        result => console.log('Success', result),
        error => console.error('Error', error)
      );
    }
  }
}
```

### Auto-Generated Table

```typescript
// Generated from UserProfile contract
@Component({
  selector: 'app-user-profile-table',
  templateUrl: './user-profile-table.component.html'
})
export class UserProfileTableComponent implements OnInit {
  displayedColumns: string[] = ['userId', 'email', 'fullName', 'status', 'actions'];
  dataSource: MatTableDataSource<UserProfile>;
  
  @ViewChild(MatPaginator) paginator: MatPaginator;
  @ViewChild(MatSort) sort: MatSort;
  
  constructor(private service: UserProfileService) { }
  
  ngOnInit() {
    this.loadData();
  }
  
  loadData() {
    this.service.list().subscribe(
      data => this.dataSource = new MatTableDataSource(data)
    );
  }
}
```

## Services

### API Service

```typescript
@Injectable({ providedIn: 'root' })
export class ApiService {
  private baseUrl = '/api/v1';
  
  constructor(private http: HttpClient) { }
  
  getExports() {
    return this.http.get<Export[]>(`${this.baseUrl}/exports`);
  }
  
  createExport(request: ExportRequest) {
    return this.http.post<Export>(
      `${this.baseUrl}/exports`,
      request
    );
  }
  
  retrieveExport(exportId: string) {
    return this.http.get(
      `${this.baseUrl}/exports/${exportId}/retrieve`,
      { responseType: 'blob' }
    );
  }
}
```

### Auth Service

```typescript
@Injectable({ providedIn: 'root' })
export class AuthService {
  private currentUser: BehaviorSubject<User | null>;
  
  constructor(private http: HttpClient) {
    this.currentUser = new BehaviorSubject<User | null>(null);
  }
  
  login(username: string, password: string) {
    return this.http.post('/api/v1/auth/login', {
      username, password
    }).pipe(
      tap(response => {
        localStorage.setItem('token', response.accessToken);
        this.currentUser.next(response.user);
      })
    );
  }
  
  logout() {
    localStorage.removeItem('token');
    this.currentUser.next(null);
  }
  
  get currentUserValue() {
    return this.currentUser.value;
  }
}
```

## Installation & Setup

```bash
# Install dependencies
npm install

# Development server
ng serve

# Build for production
ng build --prod

# Run tests
ng test

# Run e2e tests
ng e2e
```

## Material Design Integration

```typescript
import { MatModule } from './material.module';

@NgModule({
  imports: [MatModule]
})
export class AppModule { }
```

## Environment Configuration

```typescript
// environment.ts
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080/api/v1'
};

// environment.prod.ts
export const environment = {
  production: true,
  apiUrl: 'https://api.primus.company.com/api/v1'
};
```

## Testing

```bash
# Unit tests
ng test

# Code coverage
ng test --code-coverage

# E2E tests
ng e2e
```

## Build & Deployment

```bash
# Production build
ng build --prod --aot

# Docker build
docker build -t primus-ui:1.0.0 .

# Push to registry
docker push docker.company.com/primus-ui:1.0.0
```

## Best Practices

1. **Component Structure**: One component per file
2. **Service Separation**: Business logic in services, not components
3. **State Management**: Use RxJS for state management
4. **Type Safety**: Always use TypeScript types
5. **Testing**: Aim for >80% code coverage
6. **Performance**: Use OnPush change detection, lazy loading

