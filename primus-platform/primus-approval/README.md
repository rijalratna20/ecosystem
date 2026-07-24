# primus-approval

Multi-level approval workflow for sensitive data exports.

## Overview

`primus-approval` implements policy-driven, multi-level approval workflows to gate access to sensitive data exports, ensuring proper authorization before sensitive information leaves production systems.

## Module Status

🔴 **NOT YET IMPLEMENTED (Tier 2 - Phase 1)**

**Target Launch**: Week 8 of Phase 1  
**Phase**: v1.0 (Production) delivery  
**Criticality**: CRITICAL - Blocks production deployment  

## Critical Requirements

### Entry Criteria (Before Starting)
- ✅ Approval state machine agreed (PENDING/APPROVED/REJECTED/EXPIRED)
- ✅ Sensitive data classification tags available
- ✅ Depends on: `primus-audit`, `primus-server`

### Exit Criteria (Definition of Done)
- [ ] Approval APIs and policy evaluator working
- [ ] SLA/escalation behavior verified
- [ ] Sensitive flows blocked without valid approval

## Purpose

Implements governance policy enforcement:
- **Prevent data leaks**: Require approval for sensitive data exports
- **Enforce SLAs**: Track approval timelines
- **Audit trail**: Record all approval decisions
- **Escalation**: Auto-escalate stalled approvals

## Key Features (Planned)

### Approval Lifecycle

```
PENDING → APPROVED → COMPLETED
       ↓
    REJECTED
       ↓
    ESCALATED
```

### Data Classification

```
PUBLIC      — No approval needed
INTERNAL    — Manager approval
SENSITIVE   — Manager + Security
RESTRICTED  — Manager + Security + Compliance
```

### APIs (Planned)

```
# Create approval request
POST /api/v1/approvals
{
  "exportId": "exp_123456",
  "requiredApprovals": 2,
  "sensitivityLevel": "SENSITIVE",
  "reason": "UAT testing"
}

# Get pending approvals
GET /api/v1/approvals/pending?approver=user_123

# Approve/Reject
POST /api/v1/approvals/{id}/approve
{
  "comments": "Approved for UAT",
  "expiresIn": "7d"
}

POST /api/v1/approvals/{id}/reject
{
  "reason": "Insufficient justification"
}

# Query approval history
GET /api/v1/approvals/{id}/history
```

## Approval States

### PENDING
- Waiting for approval(s)
- Notifications sent to approvers
- SLA timer running

### APPROVED
- All required approvals received
- Export can proceed
- Recorded in audit trail

### REJECTED
- Request denied
- Export blocked
- Reason captured

### EXPIRED
- SLA timeout (e.g., 24 hours)
- Auto-escalation triggered
- Notification sent to manager

## Technical Design (Planned)

### Policy Engine
- Rule-based policy evaluation
- Support for:
  - Role-based rules (approvers by role)
  - Time-based rules (business hours only)
  - Context-based rules (IP whitelist, network, etc.)

### SLA Management
- Configurable SLA times per sensitivity level
- Escalation chain on timeout
- Reminders at intervals (4hrs, 12hrs, etc.)

### Notification Integration
- Event-driven notifications to approvers
- Email with approval links
- Slack/Teams integration

## Configuration (Planned)

```properties
primus.approval.enabled=true

# Policies
primus.approval.policies.default-approvers=manager,security
primus.approval.policies.sensitive-approvers=manager,security,compliance

# SLA
primus.approval.sla.standard-hours=24
primus.approval.sla.escalation-after-hours=4

# Notifications
primus.approval.notifications.enabled=true
primus.approval.notifications.channels=email,slack
```

## Sensitivity Levels (Planned)

| Level | Fields | Approvals | SLA | Examples |
|-------|--------|-----------|-----|----------|
| **PUBLIC** | Non-sensitive | 0 | N/A | Account numbers, transaction dates |
| **INTERNAL** | Low-risk | 1 (Manager) | 24h | Department, general customer info |
| **SENSITIVE** | Medium-risk | 2 (Manager + Security) | 24h | SSN, date of birth |
| **RESTRICTED** | High-risk | 3 (Manager + Security + Compliance) | 4h | Credit cards, banking info |

## Workflow Example

```
1. User requests export of SENSITIVE data
   → Approval request created
   → Status: PENDING

2. Manager notification sent
   → Manager reviews in Slack
   → Manager approves

3. Security notification sent
   → Security team reviews request
   → Security approves

4. Approvals complete
   → Status: APPROVED
   → Export proceeds
   → Audit event recorded
```

## Module Dependencies

- `primus-common` — shared types
- `primus-audit` — event logging
- `primus-server` — orchestration
- `primus-notification` — (Tier 4) for notifications

## Testing Strategy

### Unit Tests
- Policy evaluation rules
- State machine transitions
- SLA calculations

### Integration Tests
- End-to-end approval flow
- Multi-approver workflows
- Escalation triggers
- Notification delivery

### Performance Tests
- Policy evaluation latency
- Concurrent approvals

## Deployment Notes

- Deploy after `primus-audit` (Tier 2)
- Before public release to production
- Requires notification service for external approvers
- Configure approer roles and approval matrix beforehand

## Related Modules

- **primus-audit** (Tier 2) — Logs approval events
- **primus-server** (Core) — Enforces approval gates
- **primus-notification** (Tier 4) — Sends notifications

---

**Tier**: 2 (Governance - Phase 1)  
**Status**: 🔴 NOT STARTED  
**Priority**: CRITICAL  
**Target Start**: August 8, 2026  
**Target Completion**: August 22, 2026

