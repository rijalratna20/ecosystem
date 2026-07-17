# Primus Architecture (Compact, Capability-Driven)

This document summarizes the recommended compact repository and module layout, packaging guidance, and phased extraction strategy.

## Recommended repo layout (compact)

```
primus-parent
├─ primus-common
├─ primus-contract
├─ primus-annotations
├─ primus-sdk
├─ primus-auth-client
├─ primus-metadata
├─ primus-plugin
├─ primus-server
│  ├─ audit/
│  ├─ approval/
│  ├─ notification/
│  ├─ monitoring/
│  ├─ scheduler/
│  ├─ configuration/
│  ├─ transforms/
│  └─ storage/ (adapters)
├─ primus-nas
├─ primus-search
├─ primus-ui
├─ shadow-server
└─ shadow-ui
```

## Packaging guidance

- Start with the compact modules above.
- Implement governance, ops and transforms as packages inside `primus-server`.
- Keep `primus-metadata` standalone from day one (it is the platform's core).
- SDKs are separate repositories/artifacts and have independent release cadence.

## Storage SPI

- Define `StorageProvider` interface that adapters implement (NAS, S3, DB, Azure, GCS).
- `primus-nas` is a reference adapter for on-prem filesystem-backed storage.
- Cloud adapters (S3/GCS/Azure) are either small modules or packages; extract to modules when you need separate deployment.

## Eventing

- Use an Event Bus abstraction in `primus-server`.
- Start with a simple in-process publisher; wire Kafka/Rabbit later by configuring the event backend.

## Extraction rules (when to split)

1. If a package reaches >1k LOC and is reused externally, extract it.
2. If a capability needs its own scaling/HA topology, extract it.
3. If a capability requires different operational owners or separate release cadence, extract it.

## Phase roadmap (summary)

- Phase 0: Compact baseline (primus-server packages)
- Phase 1: Promote audit, approval, api-gateway, encryption, monitoring (production MVP)
- Phase 2: Notifications, scheduler, config, CLI, data-lineage (operations)
- Phase 3: Storage adapters (S3/DB), SDKs, backup/recovery, encryption/HSM


## Quick links
- docs/00-overview.md (platform overview)
- docs/MISSING_MODULES_ANALYSIS.md (detailed capabilities & mapping)
- docs/ENTERPRISE_SETTINGS.GRADLE.md (Gradle template)
- docs/transformation-example.yaml (transformation pipeline example)
- docs/EVENT_SCHEMAS.md (example events)

