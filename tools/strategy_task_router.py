#!/usr/bin/env python3
"""Route module tasks from strategy task packs to coding agents."""

from __future__ import annotations

import argparse
import json
from dataclasses import dataclass
from pathlib import Path
from typing import Dict, Iterable, List, Set

import yaml


COPILOT_TEMPLATE = """You are implementing module: {module} in {path}.\nFollow strategy.MD global gates and this module's entry/exit criteria exactly.\n\nTasks:\n1) Scaffold/update Gradle module and include it in settings/build aggregation.\n2) Implement APIs/events/persistence per module criteria.\n3) Add unit + integration tests for happy path and negative path.\n4) Add observability (metrics/logging/correlation IDs) and security checks.\n5) Update docs/runbook under primus-platform/docs.\n\nDefinition of done:\n- All module exit criteria in strategy.MD are satisfied.\n- Build and tests pass for impacted modules.\n- Provide a short change log and residual risks.\n"""

CLAUDE_TEMPLATE = """Implement {module} using the module plan in strategy.MD.\nTreat strategy.MD as the contract for scope, entry criteria, and exit criteria.\n\nExecution requirements:\n- Keep changes limited to relevant modules and shared contracts.\n- Prefer incremental commits by subtask: scaffold -> domain -> APIs -> tests -> docs.\n- Validate with Gradle tests for touched modules.\n- If a dependency module is missing, create a minimal stub and document the follow-up.\n\nOutput format:\n1) Files changed\n2) Criteria satisfied (entry/exit checklist)\n3) Test results\n4) Open risks/blockers\n"""


@dataclass(frozen=True)
class Task:
    id: str
    module: str
    tier: int
    path: str
    depends_on: List[str]
    entry_criteria: List[str]
    exit_criteria: List[str]


@dataclass(frozen=True)
class StrategyPack:
    project: str
    tasks: List[Task]


@dataclass(frozen=True)
class Status:
    external_completed: Set[str]
    completed: Set[str]
    in_progress: Set[str]


def load_strategy_pack(path: Path) -> StrategyPack:
    raw = yaml.safe_load(path.read_text(encoding="utf-8"))
    tasks = []
    for item in raw.get("tasks", []):
        tasks.append(
            Task(
                id=item["id"],
                module=item["module"],
                tier=int(item["tier"]),
                path=item["path"],
                depends_on=list(item.get("depends_on", [])),
                entry_criteria=list(item.get("entry_criteria", [])),
                exit_criteria=list(item.get("exit_criteria", [])),
            )
        )
    return StrategyPack(project=raw.get("project", "unknown"), tasks=tasks)


def load_status(path: Path | None) -> Status:
    if path is None:
        return Status(external_completed=set(), completed=set(), in_progress=set())
    raw = json.loads(path.read_text(encoding="utf-8"))
    return Status(
        external_completed=set(raw.get("external_completed", [])),
        completed=set(raw.get("completed", [])),
        in_progress=set(raw.get("in_progress", [])),
    )


def unresolved_dependencies(task: Task, all_ids: Set[str], status: Status) -> List[str]:
    unresolved = []
    for dep in task.depends_on:
        if dep in all_ids:
            if dep not in status.completed:
                unresolved.append(dep)
        elif dep not in status.external_completed:
            unresolved.append(dep)
    return unresolved


def ready_tasks(pack: StrategyPack, status: Status) -> List[Task]:
    all_ids = {task.id for task in pack.tasks}
    ordered = sorted(pack.tasks, key=lambda t: (t.tier, t.id))
    result = []
    for task in ordered:
        if task.id in status.completed or task.id in status.in_progress:
            continue
        if not unresolved_dependencies(task, all_ids, status):
            result.append(task)
    return result


def build_prompt(task: Task, agent: str) -> str:
    template = COPILOT_TEMPLATE if agent == "copilot" else CLAUDE_TEMPLATE
    return template.format(module=task.module, path=task.path)


def print_next(pack: StrategyPack, status: Status, limit: int, agent: str) -> None:
    tasks = ready_tasks(pack, status)[:limit]
    if not tasks:
        print("No ready tasks. Resolve dependencies or mark external prerequisites completed.")
        return

    print(f"Project: {pack.project}")
    print("Ready tasks:")
    for task in tasks:
        print(f"- [{task.id}] tier={task.tier} module={task.module} path={task.path}")

    print("\nAssignment prompt for first ready task:\n")
    print(build_prompt(tasks[0], agent))


def find_task(pack: StrategyPack, task_id: str) -> Task:
    by_id: Dict[str, Task] = {task.id: task for task in pack.tasks}
    if task_id not in by_id:
        known = ", ".join(sorted(by_id))
        raise ValueError(f"Unknown task id '{task_id}'. Known: {known}")
    return by_id[task_id]


def build_parser() -> argparse.ArgumentParser:
    parser = argparse.ArgumentParser(description="Route strategy tasks to coding agents.")
    parser.add_argument("--tasks", default="strategy.tasks.yaml", help="Path to strategy task pack YAML")
    parser.add_argument("--status", default=None, help="Path to status JSON")
    parser.add_argument("--agent", choices=["copilot", "claude"], default="copilot")

    subparsers = parser.add_subparsers(dest="cmd", required=True)

    next_cmd = subparsers.add_parser("next", help="List next ready tasks")
    next_cmd.add_argument("--limit", type=int, default=5)

    prompt_cmd = subparsers.add_parser("prompt", help="Render prompt for one task")
    prompt_cmd.add_argument("--task-id", required=True)

    deps_cmd = subparsers.add_parser("deps", help="Show unresolved dependencies for each pending task")

    return parser


def run_deps(pack: StrategyPack, status: Status) -> None:
    all_ids = {task.id for task in pack.tasks}
    for task in sorted(pack.tasks, key=lambda t: (t.tier, t.id)):
        if task.id in status.completed:
            continue
        unresolved = unresolved_dependencies(task, all_ids, status)
        state = "in_progress" if task.id in status.in_progress else "pending"
        if unresolved:
            print(f"- [{task.id}] state={state} blocked_by={','.join(unresolved)}")
        else:
            print(f"- [{task.id}] state={state} ready")


def main(argv: Iterable[str] | None = None) -> int:
    parser = build_parser()
    args = parser.parse_args(argv)

    pack = load_strategy_pack(Path(args.tasks))
    status = load_status(Path(args.status)) if args.status else load_status(None)

    if args.cmd == "next":
        print_next(pack, status, args.limit, args.agent)
        return 0

    if args.cmd == "prompt":
        task = find_task(pack, args.task_id)
        print(build_prompt(task, args.agent))
        return 0

    if args.cmd == "deps":
        run_deps(pack, status)
        return 0

    parser.error(f"Unsupported command: {args.cmd}")
    return 2


if __name__ == "__main__":
    raise SystemExit(main())

