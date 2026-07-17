# Sequence Diagrams

## Export Sequence Diagram

```
User/Client -> API Gateway: POST /exports
    Note over API Gateway: Authenticate & Validate
    API Gateway -> Contract Service: Load Contract
    Contract Service -> Contract Service: Validate Contract
    Contract Service --> API Gateway: Contract OK
    
    API Gateway -> Export Service: Create Export Job
    Export Service -> Job Queue: Enqueue Export Job
    
    API Gateway --> User/Client: 201 Created (exportId)
    
    par Export Processing
        Job Queue -> Export Worker: Dequeue Job
        Export Worker -> Data Source: Query Data with Filters
        Data Source --> Export Worker: Return Data
        
        Export Worker -> Validator: Validate Against Contract
        Validator --> Export Worker: Validation Result
        
        Export Worker -> Transformer: Transform to Output Format
        Transformer --> Export Worker: Formatted Data
        
        Export Worker -> Storage: Upload Data
        Storage --> Export Worker: Storage Metadata
        
        Export Worker -> Export Service: Update Status
    end
    
    User/Client -> API Gateway: GET /exports/{exportId}
    API Gateway -> Export Service: Get Export Status
    Export Service --> API Gateway: Export Details
    API Gateway --> User/Client: 200 OK
```

## Retrieval Sequence Diagram

```
User/Client -> API Gateway: GET /exports/{exportId}/retrieve?format=JSON
    Note over API Gateway: Authenticate & Authorize
    
    API Gateway -> Auth Service: Check Permissions
    Auth Service --> API Gateway: Permission Granted
    
    API Gateway -> Export Service: Validate Export Exists
    Export Service -> Storage Metadata: Check Integrity
    Storage Metadata --> Export Service: Checksum Valid
    
    Export Service -> Storage: Retrieve Data
    
    alt Data Size < Threshold
        Storage --> Export Service: Return All Data
        Export Service -> Transformer: Transform Format
        Transformer --> Export Service: Formatted Data
        Export Service --> API Gateway: Data
    else Large Dataset
        Storage --> Streaming Service: Begin Stream
        Streaming Service -> Transformer: Stream Transform
        Transformer --> API Gateway: Paginated Response
        par Optional Requests
            User/Client -> API Gateway: GET /exports/{exportId}/retrieve?page=2
            API Gateway --> Streaming Service: Get Next Page
        end
    end
    
    API Gateway --> User/Client: 200 OK (Data)
```

## UI Generation Sequence Diagram

```
Developer -> Generator CLI: generateUI --contract=UserProfile
    Generator CLI -> Contract Loader: Load Contract
    Contract Loader -> Contract Store: Fetch UserProfile Contract
    Contract Store --> Contract Loader: Contract Definition
    Contract Loader --> Generator CLI: Contract Loaded
    
    Generator CLI -> TypeScript Generator: Generate Models
    TypeScript Generator -> Template Engine: Render user.model.ts
    Template Engine --> TypeScript Generator: user.model.ts
    
    Generator CLI -> Component Generator: Generate Components
    
    par Generate Form
        Component Generator -> Form Template: Render form component
        Form Template --> Component Generator: user-form.component.ts
        Component Generator -> Form Template: Render form template
        Form Template --> Component Generator: user-form.component.html
    end
    
    par Generate Table
        Component Generator -> Table Template: Render table component
        Table Template --> Component Generator: user-table.component.ts
        Component Generator -> Table Template: Render table template
        Table Template --> Component Generator: user-table.component.html
    end
    
    Generator CLI -> Service Generator: Generate API Service
    Service Generator --> Generator CLI: user.service.ts
    
    Generator CLI -> Router Generator: Generate Routes
    Router Generator --> Generator CLI: app-routing.module.ts
    
    Generator CLI --> Developer: Generation Complete
    Developer -> File System: Files written to src/app/users/
```

## Authentication Sequence Diagram

```
User -> Client App: Enter Credentials
    Client App -> Auth Service: POST /auth/login
    Auth Service -> User Store: Validate Credentials
    
    alt Credentials Valid
        User Store --> Auth Service: User Found
        Auth Service -> JwtGenerator: Generate Token
        JwtGenerator --> Auth Service: JWT Token
        Auth Service --> Client App: 200 OK {token}
        Client App -> Storage: Save Token (localStorage)
    else Credentials Invalid
        User Store --> Auth Service: User Not Found
        Auth Service --> Client App: 401 Unauthorized
    end
    
    Client App -> API Gateway: GET /exports {Authorization: Bearer token}
    API Gateway -> Auth Middleware: Verify Token
    Auth Middleware -> JWT Validator: Validate Signature
    
    alt Token Valid
        JWT Validator --> Auth Middleware: Token Valid
        Auth Middleware -> RBAC Service: Check Permissions
        RBAC Service --> Auth Middleware: Permission Granted
        Auth Middleware --> API Gateway: Next()
    else Token Expired
        JWT Validator --> Auth Middleware: Token Expired
        Auth Middleware --> API Gateway: 401 Unauthorized
        API Gateway --> Client App: 401
        Client App -> Auth Service: POST /auth/refresh
        Auth Service --> Client App: New Token
    end
```

## Data Contract Validation Sequence Diagram

```
User -> API: POST /exports (with data)
    API -> Validator: Validate Against Contract
    
    Validator -> Type Checker: Check Field Types
    Type Checker --> Validator: Type Check Result
    
    Validator -> Required Checker: Check Required Fields
    Required Checker --> Validator: Required Fields Result
    
    Validator -> Pattern Matcher: Validate Patterns
    Pattern Matcher --> Validator: Pattern Check Result
    
    Validator -> Range Checker: Validate Ranges
    Range Checker --> Validator: Range Check Result
    
    alt All Validations Pass
        Validator --> API: Validation Passed
        API -> Storage: Store Data
        Storage --> API: Stored Successfully
        API --> User: 201 Created
    else Validation Fails
        Validator --> API: Validation Errors {errors: [...]}
        API --> User: 400 Bad Request {errors}
    end
```

## Storage Backend Selection Sequence Diagram

```
Export Worker -> Storage Selector: Select Backend
    Storage Selector -> Config: Read Storage Config
    Config --> Storage Selector: Available Backends
    
    alt NAS Available and Space > 50GB
        Storage Selector --> Export Worker: Use NAS
        Export Worker -> NAS Driver: Connect to NAS
        NAS Driver -> NAS Server: SMB/NFS Protocol
        NAS Server --> NAS Driver: Connected
        Export Worker -> NAS Driver: Write File
    else S3 Available
        Storage Selector --> Export Worker: Use S3
        Export Worker -> S3 Driver: Connect to AWS
        S3 Driver -> AWS API: Authenticate
        AWS API --> S3 Driver: Authenticated
        Export Worker -> S3 Driver: Upload Object
    else PostgreSQL Available
        Storage Selector --> Export Worker: Use PostgreSQL
        Export Worker -> DB Driver: Connect to DB
        DB Driver -> PostgreSQL: TCP Connection
        PostgreSQL --> DB Driver: Connected
        Export Worker -> DB Driver: Insert Records
    else Fallback
        Storage Selector --> Export Worker: Use Default (NAS)
    end
```

