# Quick Setup Guide

Fast track to getting started with Primus development.

## 1-Minute Setup

```bash
# Clone and navigate
cd /Users/ratnarijal/Desktop/Project/ai-sytem-design

# Java & Gradle build
cd primus-platform
./gradlew build

# Python tools (for task management)
cd ..
python3 -m venv .venv
source .venv/bin/activate
python -m pip install -r requirements.txt
```

## View Your First Task

```bash
# See what's ready to work on
python tools/strategy_task_router.py --status strategy.status.example.json next --limit 3
```

## Implement a Module

```bash
# Get implementation guidance
python tools/strategy_task_router.py prompt --task-id T2-AUDIT --agent copilot

# (Copy the prompt → feed to your IDE or coding agent)
```

## Quick Commands

| Command | Purpose |
|---------|---------|
| `./gradlew build` | Build entire platform |
| `./gradlew test` | Run all tests |
| `./gradlew :primus-platform:primus-server:build` | Build single module |
| `python tools/strategy_task_router.py next --limit 5` | List next 5 ready tasks |
| `python tools/strategy_task_router.py deps` | Show task dependencies |
| `source .venv/bin/activate` | Activate Python venv |

## Documentation Index

| Document | For Whom | Read Time |
|----------|----------|-----------|
| [README.md](./README.md) | Everyone | 10 min |
| [strategy.MD](./strategy.MD) | Team leads, developers | 20 min |
| [CONTRIBUTING.md](./CONTRIBUTING.md) | Contributors | 15 min |
| [primus-platform/EXECUTIVE_SUMMARY.md](./primus-platform/EXECUTIVE_SUMMARY.md) | Managers, executives | 30 min |
| [primus-platform/PRIMUS_ARCHITECTURE.md](./primus-platform/PRIMUS_ARCHITECTURE.md) | Architects | 1 hour |
| [primus-platform/docs/planning/TIER_1_10_PLAN_AND_TODO.md](./primus-platform/docs/planning/TIER_1_10_PLAN_AND_TODO.md) | Tech leads | 30 min |

## Troubleshooting

**"Gradle build fails"**
```bash
./gradlew clean build # Clean build
./gradlew --info build # Verbose output
```

**"Python venv issues"**
```bash
rm -rf .venv  # Start fresh
python3 -m venv .venv
source .venv/bin/activate
python -m pip install --upgrade pip
python -m pip install -r requirements.txt
```

**"Can't find module"**
```bash
./gradlew projects # List all modules
grep "my-module" settings.gradle # Check if it's in settings
```

---

See [CONTRIBUTING.md](./CONTRIBUTING.md) for detailed development guide.

