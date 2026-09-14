#!/usr/bin/env python3
"""Validate deployment prerequisites from a small JSON configuration.

The checker is dependency-free, making it suitable for a CI runner or a
developer workstation. It checks required files, directories, executables,
and environment variables, then prints a report and returns a CI-friendly
exit code.

Example configuration::

    {
      "required_files": ["README.md"],
      "required_directories": ["02-devops-cicd"],
      "required_commands": ["git", "python3"],
      "required_env": ["DEPLOY_ENV"],
      "optional_env": ["NOTIFICATION_WEBHOOK"]
    }

Usage::

    python scripts/deployment_readiness_check.py
    python scripts/deployment_readiness_check.py --config readiness.json
    python scripts/deployment_readiness_check.py --config readiness.json --output artifacts/readiness-report.json
"""

from __future__ import annotations

import argparse
import json
import os
import shutil
import sys
from dataclasses import asdict, dataclass
from pathlib import Path
from typing import Any


DEFAULT_CONFIG: dict[str, list[str]] = {
    "required_files": ["README.md"],
    "required_directories": ["02-devops-cicd"],
    "required_commands": ["git", "python3"],
    "required_env": [],
    "optional_env": [],
}


@dataclass(frozen=True)
class CheckResult:
    """Normalized result for one readiness check."""

    category: str
    target: str
    status: str
    message: str


class ConfigurationError(ValueError):
    """Raised when the readiness configuration is malformed."""


class DeploymentReadinessChecker:
    """Evaluate the configured deployment prerequisites."""

    def __init__(self, root: Path, config: dict[str, list[str]]) -> None:
        self.root = root.resolve()
        self.config = config

    def run(self) -> list[CheckResult]:
        """Run every configured check in a stable order."""
        results: list[CheckResult] = []
        results.extend(self._check_paths("file", "required_files", is_file=True))
        results.extend(
            self._check_paths("directory", "required_directories", is_file=False)
        )
        results.extend(self._check_commands())
        results.extend(self._check_environment("required_env", "fail"))
        results.extend(self._check_environment("optional_env", "warn"))
        return results

    def _check_paths(
        self, category: str, config_key: str, *, is_file: bool
    ) -> list[CheckResult]:
        results: list[CheckResult] = []
        for target in self.config[config_key]:
            path = self._resolve_path(target)
            found = path.is_file() if is_file else path.is_dir()
            expected = "file" if is_file else "directory"
            if found:
                results.append(
                    CheckResult(category, target, "pass", f"Found {expected}")
                )
            else:
                results.append(
                    CheckResult(
                        category,
                        target,
                        "fail",
                        f"Missing {expected} at {path}",
                    )
                )
        return results

    def _check_commands(self) -> list[CheckResult]:
        results: list[CheckResult] = []
        for command in self.config["required_commands"]:
            executable = shutil.which(command)
            if executable:
                results.append(
                    CheckResult(
                        "command", command, "pass", f"Available at {executable}"
                    )
                )
            else:
                results.append(
                    CheckResult(
                        "command", command, "fail", "Executable not found on PATH"
                    )
                )
        return results

    def _check_environment(
        self, config_key: str, missing_status: str
    ) -> list[CheckResult]:
        results: list[CheckResult] = []
        for variable in self.config[config_key]:
            if os.environ.get(variable):
                results.append(
                    CheckResult("environment", variable, "pass", "Variable is set")
                )
                continue

            message = (
                "Optional variable is not set"
                if missing_status == "warn"
                else "Required variable is not set"
            )
            results.append(
                CheckResult("environment", variable, missing_status, message)
            )
        return results

    def _resolve_path(self, target: str) -> Path:
        path = Path(target)
        if path.is_absolute():
            raise ConfigurationError(
                f"Paths must be relative to the readiness root: {target}"
            )
        return self.root / path


def load_config(path: Path | None) -> dict[str, list[str]]:
    """Load a JSON configuration and validate its known fields."""
    loaded_config: dict[str, Any] = {}
    if path:
        try:
            with path.open(encoding="utf-8") as config_file:
                loaded = json.load(config_file)
        except FileNotFoundError as exc:
            raise ConfigurationError(f"Configuration file not found: {path}") from exc
        except json.JSONDecodeError as exc:
            raise ConfigurationError(f"Invalid JSON in {path}: {exc}") from exc
        if not isinstance(loaded, dict):
            raise ConfigurationError("Configuration must be a JSON object")
        loaded_config = loaded

    config = {**DEFAULT_CONFIG, **loaded_config}
    for key in DEFAULT_CONFIG:
        value = config[key]
        if not isinstance(value, list) or not all(
            isinstance(item, str) for item in value
        ):
            raise ConfigurationError(f"'{key}' must be a list of strings")
    return config


def build_report(root: Path, results: list[CheckResult]) -> dict[str, Any]:
    """Build a JSON-serializable report without exposing environment values."""
    summary = {"pass": 0, "warn": 0, "fail": 0}
    for result in results:
        summary[result.status] += 1

    return {
        "root": str(root.resolve()),
        "summary": summary,
        "ready": summary["fail"] == 0,
        "checks": [asdict(result) for result in results],
    }


def print_report(report: dict[str, Any]) -> None:
    """Print the report in a format readable in CI logs."""
    for check in report["checks"]:
        print(
            f"[{check['status'].upper():4}] {check['category']:<11} "
            f"{check['target']}: {check['message']}"
        )

    summary = report["summary"]
    state = "READY" if report["ready"] else "NOT READY"
    print(
        f"\n{state} - {summary['pass']} passed, "
        f"{summary['warn']} warnings, {summary['fail']} failed"
    )


def parse_args() -> argparse.Namespace:
    """Parse command-line arguments."""
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument(
        "--root",
        type=Path,
        default=Path(__file__).resolve().parents[1],
        help="Directory to validate (default: repository root)",
    )
    parser.add_argument(
        "--config", type=Path, help="JSON file containing readiness requirements"
    )
    parser.add_argument(
        "--output", type=Path, help="Optional path for a JSON report"
    )
    return parser.parse_args()


def main() -> int:
    """Run the checker and return 0 when no required check failed."""
    args = parse_args()
    try:
        config = load_config(args.config)
        results = DeploymentReadinessChecker(args.root, config).run()
    except ConfigurationError as exc:
        print(f"Configuration error: {exc}", file=sys.stderr)
        return 2

    report = build_report(args.root, results)
    print_report(report)

    if args.output:
        args.output.parent.mkdir(parents=True, exist_ok=True)
        args.output.write_text(json.dumps(report, indent=2) + "\n", encoding="utf-8")
        print(f"Report written to {args.output}")

    return 0 if report["ready"] else 1


if __name__ == "__main__":
    raise SystemExit(main())
