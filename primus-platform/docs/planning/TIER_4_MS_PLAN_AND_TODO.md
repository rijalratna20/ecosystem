# Tier 4 Plan: Microservices, Requirements, and TODO

## Purpose
Tier 4 focuses on user experience and communication. The goal is to provide reliable user notifications and improve workflow usability for export/retrieval operations.

## Tier 4 Outcomes (Target)
- Notification service supports core delivery channels
- User-level notification preferences available
- UX improvements for search, filtering, and request tracking
- Better onboarding for first-time users

## Microservices in Tier 4

### 1) `primus-notification` (New Service)
**Role**
- Sends event-driven notifications to configured channels

**Required Features**
- Channel adapters (email/webhook initially)
- Template rendering for event types
- Retry and dead-letter handling
- Delivery status tracking

### 2) `primus-server` (Event Publisher)
**Role**
- Emits domain events for notification triggers

**Required Features**
- Event publishing for export success/failure and approval actions
- Event payload standardization
- Notification preference lookup hooks

### 3) `primus-ui` (UX and Notification Preferences)
**Role**
- Provides user-facing notification settings and workflow views

**Required Features**
- Notification preferences page
- Saved searches and reusable filters
- Improved request status and error messaging
- Quick-start onboarding path

## Tier 4 TODO List

### A. Notification Foundations
- [ ] Define notification event schema and templates
- [ ] Build `primus-notification` with email and webhook adapters
- [ ] Implement retry and dead-letter queue handling
- [ ] Add delivery history query endpoint

### B. Event and Preference Integration
- [ ] Add event emission hooks in `primus-server`
- [ ] Implement user preference storage and lookup
- [ ] Add per-event opt-in/opt-out behavior

### C. UI Delivery
- [ ] Build notification preferences screens in `primus-ui`
- [ ] Add saved search/filter UX for exports/retrievals
- [ ] Improve status/error states for long-running workflows
- [ ] Add first-time setup guidance flow

### D. Testing and Operations
- [ ] Integration tests for end-to-end notification delivery
- [ ] Template rendering tests for each event type
- [ ] Failure tests for unreachable channels
- [ ] Operator runbook for notification incidents

## Exit Criteria for Tier 4
- [ ] Users can configure notification preferences
- [ ] Core workflow events trigger notifications reliably
- [ ] UX improvements reduce request and tracking friction
- [ ] Notification service operationally documented

