# shadow-ui Module

## Overview

Experimental Angular UI for testing new features and UI patterns for Primus platform.

## Module Location

```
primus-platform/primus-ui/shadow-angular/
```

## Purpose

Shadow UI serves as a testing ground for:
- New feature implementations
- UI/UX improvements
- Performance optimizations
- Alternative UI patterns
- A/B testing different designs

## Relationship to primus-angular

```
primus-angular (Stable)
       │
       ├── shadow-angular (Experimental)
       │   ├── New Features Branch
       │   ├── UI Improvements
       │   ├── Performance Tests
       │   └── Design Experiments
       │
       └── → Proven features promoted to stable
```

## Project Structure

```
shadow-angular/
├── src/
│   ├── app/
│   │   ├── experiments/
│   │   │   ├── new-export-flow/
│   │   │   ├── advanced-filtering/
│   │   │   ├── real-time-updates/
│   │   │   └── performance-optimizations/
│   │   ├── core/
│   │   ├── shared/
│   │   └── app.module.ts
│   ├── environments/
│   └── styles/
├── angular.json
├── package.json
└── README.md
```

## Key Experimental Features

### 1. Real-Time Updates Experiment

```typescript
@Component({
  selector: 'app-export-status-realtime',
  templateUrl: './export-status-realtime.component.html'
})
export class ExportStatusRealtimeComponent implements OnInit {
  export$: Observable<Export>;
  
  constructor(
    private exportService: ExportService,
    private socketService: SocketService
  ) { }
  
  ngOnInit() {
    // Use WebSocket for real-time updates
    this.export$ = this.socketService
      .onExportUpdate()
      .pipe(
        switchMap(update => 
          this.exportService.getExport(update.exportId)
        )
      );
  }
}
```

### 2. Advanced Filtering Experiment

```typescript
@Component({
  selector: 'app-advanced-filter',
  template: `
    <div class="filter-builder">
      <app-filter-condition 
        *ngFor="let condition of conditions"
        [condition]="condition"
        (changed)="updateFilter()">
      </app-filter-condition>
      <button (click)="addCondition()">Add Condition</button>
    </div>
  `
})
export class AdvancedFilterComponent {
  conditions: FilterCondition[] = [];
  
  updateFilter() {
    const query = this.buildComplexQuery(this.conditions);
    this.emitFilter(query);
  }
  
  private buildComplexQuery(conditions: FilterCondition[]) {
    // Build complex nested filter query
    return conditions.reduce((query, cond) => ({
      ...query,
      [cond.field]: { [cond.operator]: cond.value }
    }), {});
  }
}
```

### 3. Performance Optimization Experiment

```typescript
@Component({
  selector: 'app-virtualized-table',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class VirtualizedTableComponent {
  
  constructor(
    private cdr: ChangeDetectorRef,
    private zone: NgZone
  ) { }
  
  // Virtual scrolling for large datasets
  ngOnInit() {
    this.items$ = this.itemService.getItems()
      .pipe(
        debounceTime(300),
        distinctUntilChanged(),
        shareReplay(1)
      );
  }
}
```

## A/B Testing Integration

### Experiment Configuration

```typescript
export interface ExperimentConfig {
  name: string;
  enabled: boolean;
  variant: 'control' | 'treatment';
  trafficPercentage: number;
}

@Injectable({ providedIn: 'root' })
export class ExperimentService {
  
  constructor(private shadowApi: ShadowApiService) { }
  
  isFeatureEnabled(featureName: string): Observable<boolean> {
    return this.shadowApi
      .checkExperiment(featureName)
      .pipe(
        map(experiment => experiment.enabled && 
                          this.isUserInTreatment(experiment))
      );
  }
}
```

### Feature Flags

```typescript
// app.component.ts
export class AppComponent implements OnInit {
  
  constructor(private experimentService: ExperimentService) { }
  
  ngOnInit() {
    this.experimentService
      .isFeatureEnabled('advanced-export-flow')
      .subscribe(enabled => {
        // Render advanced or standard flow
      });
  }
}
```

## Configuration

```yaml
# environment.shadow.ts
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8081/api/v1',
  shadowMode: true,
  experiments: {
    enableRealtimeUpdates: true,
    enableAdvancedFiltering: true,
    enableVirtualization: true
  }
};
```

## Analytics & Metrics

```typescript
@Service
export class ExperimentMetricsService {
  
  trackEvent(eventName: string, metadata: any) {
    this.analyticsService.track(eventName, {
      ...metadata,
      userVariant: this.experimentService.getVariant(),
      timestamp: new Date()
    });
  }
  
  trackMetric(metricName: string, value: number) {
    this.metricsService.record(metricName, value);
  }
}
```

## Testing

```bash
# Run shadow-specific tests
ng test --configuration=shadow

# Run experiments test suite
ng test --include='**/experiments/**/*.spec.ts'

# E2E tests for experiments
ng e2e --configuration=shadow
```

## Promoting Features to Stable

### Promotion Checklist

1. **Performance**: Feature meets performance benchmarks
2. **Reliability**: 99.5% uptime in shadow environment
3. **User Feedback**: Positive user testing results
4. **Code Quality**: >85% test coverage
5. **Documentation**: Complete feature documentation

### Promotion Process

```bash
# 1. Merge shadow branch to feature branch
git merge shadow/new-export-flow

# 2. Run integration tests
npm test -- --include='**/integration/**'

# 3. Build for production
npm run build:prod

# 4. Deploy to staging
npm run deploy:staging

# 5. Run smoke tests
npm run smoke-test

# 6. Deploy to production
npm run deploy:prod
```

## Experimentation Workflow

```
Feature Request
    ↓
Prototype in shadow-angular
    ↓
A/B Test with Users
    ↓
Analyze Metrics
    ↓
Promote to primus-angular (if successful)
    ↓
Deploy to Production
```

## Documentation

- Feature proposals: `/docs/experiments/`
- Results & learnings: Shared wiki/knowledge base
- Status tracking: GitHub projects

## Best Practices

1. **Isolation**: Keep experiments isolated from stable code
2. **Metrics**: Always measure feature impact
3. **Cleanup**: Remove failed experiments
4. **Documentation**: Document learnings
5. **Gradual Rollout**: Start with small percentage of users

