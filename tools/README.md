# Strategy Task Router

This tool reads `strategy.tasks.yaml` and helps pick the next implementation task for coding agents.

## Files
- `strategy.tasks.yaml`: agent-consumable backlog derived from `strategy.MD`
- `strategy.status.example.json`: example progress/status file
- `tools/strategy_task_router.py`: CLI for task routing and prompt generation
- `tools/test_strategy_task_router.py`: unit tests

## Install
```bash
python3 -m pip install -r requirements.txt
```

## Quick Try
```bash
python3 tools/strategy_task_router.py --status strategy.status.example.json next --limit 3
python3 tools/strategy_task_router.py --agent claude prompt --task-id T2-APPROVAL
python3 tools/strategy_task_router.py --status strategy.status.example.json deps
```

## Status File Format
```json
{
  "external_completed": ["tier1-baseline"],
  "completed": ["T2-AUDIT"],
  "in_progress": ["T2-APPROVAL"]
}
```

## Notes
- `external_completed` is for prerequisites that are not task IDs (for example `tier1-baseline`).
- A task is `ready` only when all dependencies are completed.

