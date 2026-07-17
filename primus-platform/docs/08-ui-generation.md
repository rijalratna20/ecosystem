# UI Generation

## Overview

Primus automatically generates Angular web UIs from data contracts, eliminating manual form and table development.

## UI Generation Architecture

### Generation Pipeline

```
Contract Definition
    ↓
Parse Contract Metadata
    ↓
Generate TypeScript Models
    ↓
Generate Angular Components
    ↓
Generate Routing & Guards
    ↓
Build UI Bundle
```

## Component Generation

### 1. Form Components

**Generated for**: CREATE, UPDATE operations

```typescript
// Auto-generated form component
export class UserProfileFormComponent {
  form: FormGroup;
  
  constructor(fb: FormBuilder) {
    this.form = fb.group({
      userId: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      createdDate: ['', Validators.required],
      metadata: fb.group({
        tier: ['FREE', Validators.required]
      })
    });
  }
  
  onSubmit() {
    if (this.form.valid) {
      this.exportService.submitForm(this.form.value);
    }
  }
}
```

### 2. Table Components

**Generated for**: LIST, READ operations

```typescript
// Auto-generated table component
export class UserProfileTableComponent {
  columns: Column[] = [
    { name: 'userId', label: 'User ID', type: 'string', sortable: true },
    { name: 'email', label: 'Email', type: 'email', searchable: true },
    { name: 'createdDate', label: 'Created', type: 'date', sortable: true }
  ];
  
  dataSource: MatTableDataSource;
  
  onExport() {
    this.exportService.exportTable(this.dataSource.data);
  }
}
```

### 3. Detail Components

**Generated for**: READ single record

```typescript
// Auto-generated detail view
export class UserProfileDetailComponent implements OnInit {
  profile: UserProfile;
  
  ngOnInit() {
    this.route.params.subscribe(params => {
      this.loadProfile(params['id']);
    });
  }
  
  loadProfile(id: string) {
    this.exportService.getProfile(id).subscribe(
      profile => this.profile = profile
    );
  }
}
```

## Generated API Client

### Service Generation

```typescript
// Auto-generated API service
export class UserProfileService extends BaseExportService {
  private readonly endpoint = '/api/v1/user-profiles';
  
  list(filter?: UserProfileFilter): Observable<UserProfile[]> {
    return this.http.get<UserProfile[]>(`${this.endpoint}`, {
      params: this.buildParams(filter)
    });
  }
  
  get(id: string): Observable<UserProfile> {
    return this.http.get<UserProfile>(`${this.endpoint}/${id}`);
  }
  
  create(data: UserProfile): Observable<UserProfile> {
    return this.http.post<UserProfile>(this.endpoint, data);
  }
  
  update(id: string, data: UserProfile): Observable<UserProfile> {
    return this.http.put<UserProfile>(`${this.endpoint}/${id}`, data);
  }
  
  delete(id: string): Observable<void> {
    return this.http.delete<void>(`${this.endpoint}/${id}`);
  }
  
  export(filter?: UserProfileFilter): Observable<Blob> {
    return this.http.post<Blob>(
      `${this.endpoint}/export`,
      filter,
      { responseType: 'blob' as 'json' }
    );
  }
}
```

## UI Routing

### Auto-Generated Routes

```typescript
// Auto-generated routing module
const routes: Routes = [
  {
    path: 'user-profiles',
    component: UserProfileLayoutComponent,
    canActivate: [AuthGuard],
    children: [
      {
        path: '',
        component: UserProfileTableComponent
      },
      {
        path: 'create',
        component: UserProfileFormComponent,
        canActivate: [PermissionGuard],
        data: { requiredRole: 'editor' }
      },
      {
        path: ':id',
        component: UserProfileDetailComponent,
        canActivate: [AuthGuard]
      },
      {
        path: ':id/edit',
        component: UserProfileFormComponent,
        canActivate: [PermissionGuard],
        data: { requiredRole: 'editor' }
      }
    ]
  }
];
```

## UI Customization

### Template Overrides

```typescript
// Contract with UI hints
@ApiContract(name = "UserProfile")
public class UserProfile {
    @ApiField
    @UiComponent(type = "text-input", label = "User ID")
    private String userId;
    
    @ApiField
    @UiComponent(type = "email-input")
    private String email;
    
    @ApiField
    @UiComponent(type = "date-picker", format = "yyyy-MM-dd")
    private LocalDate createdDate;
    
    @ApiField
    @UiComponent(type = "select", options = "TIER_OPTIONS")
    private String tier;
}
```

### Component Customization

```typescript
// Custom component extends generated base
export class CustomUserProfileFormComponent 
  extends GeneratedUserProfileFormComponent {
  
  ngOnInit() {
    super.ngOnInit();
    
    // Custom initialization
    this.form.get('email')?.valueChanges
      .pipe(debounceTime(300))
      .subscribe(email => this.validateEmail(email));
  }
  
  validateEmail(email: string) {
    // Custom validation logic
  }
}
```

## Generation Options

### Command Line

```bash
# Generate UI for contract
./gradlew generateUI --contract=UserProfile --output=src/app

# Generate with custom template
./gradlew generateUI \
  --contract=UserProfile \
  --template=custom-template.groovy \
  --output=src/app
```

### Configuration File

```yaml
ui-generation:
  contracts:
    - name: UserProfile
      module: primus-angular
      generateForms: true
      generateTables: true
      generateDetails: true
      generateServices: true
  
  styling:
    framework: material
    theme: default
    responsive: true
  
  routing:
    autogenerate: true
    lazyLoad: true

