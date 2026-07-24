import tempfile
import unittest
from pathlib import Path

from strategy_task_router import (
    Task,
    build_prompt,
    load_status,
    load_strategy_pack,
    ready_tasks,
    unresolved_dependencies,
)


class StrategyTaskRouterTests(unittest.TestCase):
    def setUp(self):
        self.repo_root = Path(__file__).resolve().parents[1]
        self.tasks_file = self.repo_root / "strategy.tasks.yaml"

    def test_load_strategy_pack(self):
        pack = load_strategy_pack(self.tasks_file)
        self.assertEqual(pack.project, "primus-platform")
        self.assertGreaterEqual(len(pack.tasks), 10)

    def test_ready_tasks_respects_dependencies(self):
        pack = load_strategy_pack(self.tasks_file)

        with tempfile.TemporaryDirectory() as tmp:
            status_file = Path(tmp) / "status.json"
            status_file.write_text(
                '{"external_completed":["tier1-baseline"],"completed":["T2-AUDIT"],"in_progress":[]}',
                encoding="utf-8",
            )
            status = load_status(status_file)

        ready = ready_tasks(pack, status)
        ready_ids = [t.id for t in ready]

        self.assertIn("T2-APPROVAL", ready_ids)
        self.assertNotIn("T2-SERVER-GOV", ready_ids)

    def test_unresolved_dependencies_handles_external(self):
        task = Task(
            id="X",
            module="x",
            tier=2,
            path="x",
            depends_on=["tier1-baseline", "T2-AUDIT"],
            entry_criteria=[],
            exit_criteria=[],
        )

        with tempfile.TemporaryDirectory() as tmp:
            status_file = Path(tmp) / "status.json"
            status_file.write_text(
                '{"external_completed":["tier1-baseline"],"completed":[],"in_progress":[]}',
                encoding="utf-8",
            )
            status = load_status(status_file)

        unresolved = unresolved_dependencies(task, {"T2-AUDIT"}, status)
        self.assertEqual(unresolved, ["T2-AUDIT"])

    def test_prompt_templates(self):
        task = Task(
            id="T2-AUDIT",
            module="primus-audit",
            tier=2,
            path="primus-platform/primus-services/primus-audit",
            depends_on=[],
            entry_criteria=[],
            exit_criteria=[],
        )
        self.assertIn("primus-audit", build_prompt(task, "copilot"))
        self.assertIn("primus-audit", build_prompt(task, "claude"))


if __name__ == "__main__":
    unittest.main()

