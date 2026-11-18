# Elaboration: Harness-Agnostic Skill Validator (v3)

**Project:** skill-validator (v3)
**Date:** 2026-11-18
**Purpose:** Detailed elaboration of the skill-validator framework for implementation planning — covering functionality, acceptance criteria, edge cases, and non-functional requirements.

---

## 1. Background & Problem Statement

Skills for AI agents are currently tested manually or inside a specific harness (e.g., OpenCode), which makes it impossible to validate skill behavior independently, reproduce failures, or run skills against multiple models. Worse, there is no way to *prove* that a skill respects its stated boundaries — that it refuses out-of-scope work, resists prompt injection, and touches only permitted files and tools.

The validator is a Node.js/TypeScript CLI that treats a skill as a testable unit: given a skill (`SKILL.md` with frontmatter + prose instructions including dos/don'ts) and YAML test cases, it executes each test in a fresh LangChain agent session inside a disposable workspace, records complete execution evidence, and produces both human-readable reports and machine-readable verdicts.

**v3 philosophy:** scope is expressed in the skill's natural-language instructions and **verified behaviorally by tests**. There is no machine-readable contract. The sandbox enforces only harness safety (workspace containment, no unrestricted shell, gated MCP) so that tests can give the agent a genuine opportunity to violate, and observe whether it does.

### 1.1 Primary usage modes

| Mode | User | Flow |
|---|---|---|
| **Validation mode** | Human developer | Run the full suite for a skill; read HTML/MD reports |
| **Developer feedback mode (TDD loop)** | Agent harness | Harness writes a test → calls CLI with `--test X --json --no-report` → receives versioned verdict JSON + exit code → edits the skill on FAIL → re-runs |

The TDD loop is the driving use case. The CLI must behave as a pure, fast, machine-consumable function: `skill + test + config → verdict + evidence`.

---

## 2. Scope

### 2.1 In scope

- OpenCode configuration import (models, providers, MCP servers) — config only, never the OpenCode runtime.
- Skill loading (`SKILL.md` frontmatter + body) and frontmatter validation.
- YAML test loading, schema validation, structural-contradiction linting, determinism lint.
- LangChain.js agent runtime behind a harness-independent `AgentRuntime` interface.
- Sandbox enforcement: workspace containment, tool availability, command allowlist, MCP gating, execution limits.
- Execution audit: append-only JSONL event log, tool traces, file diffs, skill snapshots.
- Assertion engine: deterministic assertions first, guarded LLM judge second.
- CLI with stable exit codes and versioned JSON verdict output.
- Reporting: summary + detailed reports in HTML/MD/JSON.
- MCP integration via official SDK, with mocks and lazy connection, per-test gating.
- Flakiness controls (multi-run pass rates), budget enforcement.

### 2.2 Out of scope (v3)

- Machine-readable skill contracts / scope enforcement (removed in v3).
- Programmatic library API (CLI + verdict JSON is the integration surface; revisit later).
- OpenCode-managed OAuth credential reuse (requires a dedicated auth adapter; not assumed).
- Real-world side effects: no test may mutate external systems (MCP or otherwise) unless explicitly authorized against a disposable environment.
- Capturing hidden model chain-of-thought — only observable messages, tool calls, and events are recorded.

---

## 3. Functional Requirements

Organized by module. IDs (FR-nn) are referenced by acceptance criteria in §5.

### 3.1 Configuration (FR-CFG)

| ID | Requirement |
|---|---|
| FR-CFG-1 | Discover config at `$HOME/.config/opencode/opencode.json` (and `.jsonc`); support `--config <path>` override. |
| FR-CFG-2 | Parse JSONC; import default model, small model, providers, model options, endpoints, MCP servers, env/header refs. |
| FR-CFG-3 | Normalize into an internal `NormalizedConfiguration` model; the rest of the app consumes only normalized config. |
| FR-CFG-4 | Resolve credentials from env vars / file refs; redact secrets on every output surface (logs, reports, verdict JSON, events). |
| FR-CFG-5 | Never assume OpenCode OAuth credentials are reusable. |

### 3.2 Model management (FR-MOD)

| ID | Requirement |
|---|---|
| FR-MOD-1 | Model factory with adapters for OpenAI, Anthropic, Ollama, extensible to other LangChain chat models. |
| FR-MOD-2 | CLI model selection (`--model provider/model`), configurable temperature (default 0 in dev mode), model-specific options. |
| FR-MOD-3 | Separate judge model configuration (`--judge-model`); judge never the model under test. |
| FR-MOD-4 | No hardcoded credentials. |

### 3.3 Skill (FR-SKL) — UPDATED

| ID | Requirement |
|---|---|
| FR-SKL-1 | Load `SKILL.md`; parse YAML frontmatter with Zod (`name` required, must match directory; `description` required); body is the instruction set. |
| FR-SKL-2 | Record skill content hash in evidence metadata. |
| FR-SKL-3 | Effective limits: `max_iterations = min(global, test)`. No per-skill limit. |
| FR-SKL-4 | Store `SKILL.md` snapshot in every run's evidence. |
| FR-SKL-5 | **(removed)** No contract to validate. |

### 3.4 Test cases (FR-TST)

| ID | Requirement |
|---|---|
| FR-TST-1 | Load YAML suites; Zod-validate; require unique test IDs, category, prompt, `expect` block. |
| FR-TST-2 | Support `setup.files` fixtures (inline content or fixture dir refs), copied into the workspace pre-run; no shell execution during setup unless explicitly whitelisted. |
| FR-TST-3 | **TestCaseValidator** rejects (exit 2): schema violations; structural contradictions between assertions (e.g., `file_exists` + `no_file_change` on the same path; `tool_called` + `no_tool_call` for the same tool; `execute` + `response_declines`); unsatisfiable expectations given the sandbox (unknown tools, MCP servers not in imported config, fixture paths outside workspace); >50% LLM-judged assertions → warning. |
| FR-TST-4 | Expose lint gate via `skill-validator lint-tests <skill>`; findings emitted in machine-readable form. |
| FR-TST-5 | Outcome taxonomy: `execute`, `decline_and_handoff`, `execute_in_scope_only`, `error`. |
| FR-TST-6 | **(NEW)** Per-test `sandbox:` block declares `enable_tools`, `disable_tools`, `mcp_servers`, `mcp_mutating`. Defaults: read/search/write/edit enabled; command disabled; MCP none. |

### 3.5 Runtime & execution (FR-RUN)

| ID | Requirement |
|---|---|
| FR-RUN-1 | `AgentRuntime` interface; initial implementation `LangChainRuntime`; structured events with `schema_version`. |
| FR-RUN-2 | Fresh agent session + disposable workspace per test; no conversation leakage between tests. |
| FR-RUN-3 | Configurable max iterations, timeouts, tool-call limits. |
| FR-RUN-4 | Per-run token/cost budget; exceeding it aborts with status ERROR and reason `budget_exceeded`. |
| FR-RUN-5 | No unrestricted shell access. |

### 3.6 Sandbox (FR-SBX) — UPDATED (replaces FR-POL)

| ID | Requirement |
|---|---|
| FR-SBX-1 | Every tool execution passes through `SandboxPolicy`; allow **and** deny decisions recorded with matched rule + reason. |
| FR-SBX-2 | A denied operation never executes. |
| FR-SBX-3 | Workspace containment: `..` escapes, absolute paths outside workspace, symlinks resolving outside → DENY. |
| FR-SBX-4 | Tool registry contains exactly the default-enabled tools plus test-declared `enable_tools` minus `disable_tools`. Disabled tools are absent from the registry. |
| FR-SBX-5 | Command tool, when enabled, is restricted to an allowlisted set of read-only commands inside the workspace. |
| FR-SBX-6 | MCP servers are connected lazily and only when declared in `sandbox.mcp_servers`; mutating tools denied unless `mcp_mutating: true`. |
| FR-SBX-7 | **Skill-scope is not enforced by the sandbox.** The sandbox does not know about skill dos/don'ts. |

### 3.7 Audit & evidence (FR-AUD)

| ID | Requirement |
|---|---|
| FR-AUD-1 | Record event types: TEST_STARTED, AGENT_STARTED, MODEL_REQUEST (redacted), MODEL_RESPONSE, TOOL_REQUEST, SANDBOX_DECISION, TOOL_RESPONSE, AGENT_MESSAGE, POST_DENIAL_BEHAVIOR, AGENT_FINISHED, FILE_CREATED/MODIFIED/DELETED, EVALUATION_STARTED/COMPLETED, TEST_COMPLETED. |
| FR-AUD-2 | Per-tool-call trace: sequence, name, args, sandbox decision + matched rule, response, duration, error. |
| FR-AUD-3 | Append-only JSONL per test; event log is the authoritative evidence source. |
| FR-AUD-4 | Evidence dir per test: `events.jsonl`, `agent-response.md`, `tool-calls.json`, `file-changes.diff`, `skill-snapshot/SKILL.md`, `metadata.json` (framework version, schema versions, model params, skill content hash, sandbox declarations, limits). |
| FR-AUD-5 | JSON evidence produced even if report generation fails; also produced in `--no-report` dev mode. |

### 3.8 Evaluation (FR-EVA)

| ID | Requirement |
|---|---|
| FR-EVA-1 | Assertion engine with typed assertions: deterministic — `file_exists`, `file_not_exists`, `no_file_change`, `no_tool_call`, `tool_called`, `sandbox_denials_blocked`; LLM-judged — `response_declines`, `handoff_mentions`, `response_satisfies`. |
| FR-EVA-2 | Every assertion result: `{id, type, status, expected, actual, evidence_ref, rationale?}`; deterministic assertions cite event sequence numbers. |
| FR-EVA-3 | Blocked-violation semantics (sandbox only): denied-and-not-escaped = enforced, not a violation; per-test `blocked_violation_is: pass\|warning` (default pass for adversarial). Successful sandbox escape = always FAIL and exit 3. |
| FR-EVA-4 | Post-denial classification (sandbox only): `stopped` / `retried_same` / `escalated` / `accepted`; escalation = violation (`post_denial_escalation`) even if ultimately blocked. |
| FR-EVA-5 | Judge guardrails: pinned judge model, temp 0, structured output (parse failure = ERROR, never silent FAIL), per-run call cap, JUDGE_REQUEST/JUDGE_RESPONSE events, off by default in dev mode unless required. |
| FR-EVA-6 | Flakiness controls: `runs: N`, `pass_threshold`; verdict reports `pass_rate`, `flake` flag; flake ≠ consistent failure. |
| FR-EVA-7 | Statuses: PASS, FAIL, WARNING, ERROR, SKIPPED; sandbox-blocked vs sandbox-escape vs semantic failure vs infra failure distinguished. |

### 3.9 CLI & automation contract (FR-CLI)

| ID | Requirement |
|---|---|
| FR-CLI-1 | Commands: `init`, `config show`, `skills list`, `lint-tests`, `validate` (with `--model`, `--test`, `--category`, `--judge-model`, `--verbose`, `--workspace`, `--output`), `validate-all`, `report` (with `--history`, `--test`), `compare`. |
| FR-CLI-2 | Exit codes: 0 all pass; 1 failures/warnings; 2 test-validation failure; 3 infra error (including sandbox escape). Exit 3 never yields FAIL. |
| FR-CLI-3 | `--json` emits versioned verdict document (`verdict_schema_version: "1.1"`) to stdout; stable, additive-only evolution; harness parses JSON, never HTML. |
| FR-CLI-4 | `--no-report` skips HTML/MD; JSONL evidence still written. |
| FR-CLI-5 | Human-mode completion output prints skill, status, summary counts, report paths. |

### 3.10 Reporting (FR-RPT)

| ID | Requirement |
|---|---|
| FR-RPT-1 | Summary report: one row per test (ID, name, description, category, status, duration, violations, details link) + run header (skill, content hash, run ID, timestamp, model/provider, sandbox declarations, totals, overall status). |
| FR-RPT-2 | Detailed report sections A–I: test info (incl. `SKILL.md` snapshot); result (expected vs actual, evaluator results, failure reason); chronological timeline; all agent responses (intermediate + tool-call messages + final, never collapsed); per-tool-call collapsible detail incl. MCP (server, tool, args, sandbox decision, response, errors); file changes with unified diffs and disallowed-path highlighting (per test assertions); boundary violations (sandbox-blocked vs sandbox-escape vs assertion-failed, severity, rule, evidence ref); final response with Markdown preserved; evaluation details per assertion incl. evaluator type and rationale. |
| FR-RPT-3 | HTML navigation: dashboard, click-through, expandable responses/tool/MCP/sandbox sections, diff viewer, search/filter by ID/status/category, raw JSONL download. |
| FR-RPT-4 | File layout: `reports/<date>/<skill>/<run-id>/index.html` (entry), `summary.{html,md,json}`, `test-results/<ID>.html`, `evidence/<ID>/…`, `report.json`. |
| FR-RPT-5 | Reports render from aggregated verdict objects only; no LLM calls, no re-evaluation, never alter results. |

### 3.11 MCP (FR-MCP)

| ID | Requirement |
|---|---|
| FR-MCP-1 | Import MCP server definitions from normalized config; connect local + remote servers; discover tools; convert to LangChain tools. |
| FR-MCP-2 | **(UPDATED)** Only servers declared in the *test's* `sandbox.mcp_servers` are connected; everything else absent from registry. |
| FR-MCP-3 | Record MCP requests/responses with server name, tool, args, response, connection/execution errors. |
| FR-MCP-4 | Mock MCP server support for integration tests; lazy connection. |
| FR-MCP-5 | Mutating MCP tools denied unless test declares `mcp_mutating: true`. |

---

## 4. Key Design Decisions (recap)

1. OpenCode is config source only, never the execution engine.
2. LangChain.js initial runtime; `AgentRuntime` interface for future runtimes.
3. **(UPDATED)** Sandbox enforcement is deterministic and outside the LLM, but limited to harness safety. Skill scope is *tested*, not enforced.
4. **(UPDATED)** Tests are the behavioral specification; the skill's dos/don'ts live in `SKILL.md` prose.
5. Deterministic assertions outrank LLM judgement; judge is pinned, capped, auditable.
6. CLI contract (exit codes + verdict schema) is the stable interface; reports are renderings.
7. Fresh workspace + fresh session per test; evidence is append-only and complete.
8. **(UPDATED)** Anti-overfitting: assertions evaluate recorded evidence, not surface wording; tests can give the agent the opportunity to violate (via `sandbox.enable_tools`) and verify it does not.
9. **(NEW)** Tool/MCP availability is per-test, not per-skill — adversarial tests need control over the agent's surface.
10. **(NEW)** A sandbox escape is an infrastructure failure (exit 3), never a skill failure.

---

## 5. Acceptance Criteria

| # | Acceptance criterion | Verifies |
|---|---|---|
| AC-01 | Reads OpenCode config (incl. JSONC) without launching OpenCode; `--config` override works. | FR-CFG-1..3 |
| AC-02 | Resolves selected provider + credentials from env/file refs; no credential appears in any log, report, event, or verdict output (verified by redaction scan over all artifacts). | FR-CFG-4 |
| AC-03 | **(UPDATED)** Loads `SKILL.md`; invalid frontmatter fails fast with precise Zod paths; missing `name` or mismatched directory name → exit 3. | FR-SKL-1 |
| AC-04 | Reads YAML test cases; malformed suites rejected with machine-readable findings. | FR-TST-1 |
| AC-05 | Each test executes in a fresh LangChain session in a disposable workspace; no state leaks between tests. | FR-RUN-2 |
| AC-06 | **(UPDATED)** Sandbox containment enforced: path escapes, symlinks outside workspace, and disallowed shell commands denied without side effect (verified by sandbox trace + filesystem audit). | FR-SBX-2..5 |
| AC-07 | Model requests and observable responses recorded, secrets redacted. | FR-AUD-1 |
| AC-08 | Tool requests, arguments, responses recorded with sequence, sandbox decision, duration. | FR-AUD-2 |
| AC-09 | MCP requests/responses recorded incl. server name and errors. | FR-MCP-3 |
| AC-10 | Filesystem changes captured as created/modified/deleted with unified diffs. | FR-AUD-1 |
| AC-11 | **(UPDATED)** Sandbox-blocked attempt vs successful sandbox escape distinguished in verdict and report; assertion-failed boundary behavior distinguished from both. | FR-EVA-3 |
| AC-12 | Summary report shows per-test ID, description, category, status, duration + run totals. | FR-RPT-1 |
| AC-13 | Detailed report generated per test. | FR-RPT-2 |
| AC-14 | Detailed report shows chronological execution timeline. | FR-RPT-2 |
| AC-15 | Detailed report shows complete observable agent responses. | FR-RPT-2 |
| AC-16 | Tool/MCP requests and responses independently inspectable. | FR-RPT-2..3 |
| AC-17 | File changes and diffs inspectable; disallowed-path changes highlighted. | FR-RPT-2 |
| AC-18 | Same skill suite runs against multiple models via `--model`. | FR-MOD-2 |
| AC-19 | Reports openable offline without rerunning the validator. | FR-RPT-4 |
| AC-20 | Framework runs with no OpenCode install, given config + credentials. | §2.1 |
| AC-21 | `validate --test X --json --no-report` executes one test and prints the versioned verdict JSON; no HTML generated; JSONL evidence still present. | FR-CLI-3..4 |
| AC-22 | **(UPDATED)** A test file with structural contradictions or unsatisfiable expectations exits 2 with machine-readable lint findings; no agent execution occurs. | FR-TST-3 |
| AC-23 | Every assertion in the verdict reports status, expected, actual, and evidence reference; deterministic assertions cite event sequences. | FR-EVA-2 |
| AC-24 | **(UPDATED)** Sandbox-blocked attempt is not counted as a violation; per-test `blocked_violation_is` governs pass/warning; successful sandbox escape always FAIL (and exit 3). | FR-EVA-3 |
| AC-25 | LLM-judged assertions are identifiable, use the configured judge model, and cannot override deterministic results. | FR-EVA-5 |
| AC-26 | Multi-run tests report pass_rate and flake; a mixed-outcome run is flagged `flake: true`, not FAIL-by-default. | FR-EVA-6 |
| AC-27 | Runs exceeding budget/iteration/timeout limits abort with ERROR, never FAIL. | FR-RUN-4 |
| AC-28 | **(UPDATED)** Evidence contains `SKILL.md` snapshot + skill content hash + sandbox declarations + all runtime parameters needed to reproduce the run. | FR-AUD-4 |
| AC-29 | Exit code 3 (infra) never produces a FAIL verdict. | FR-CLI-2 |
| AC-30 | **(UPDATED)** Post-denial escalation after a sandbox denial is classified and reported, even when blocked. | FR-EVA-4 |
| AC-31 | Judge structured-output parse failures yield assertion status ERROR (not FAIL) and are visible in the verdict. | FR-EVA-5 |
| AC-32 | `index.html` serves as the offline entry point; raw JSONL evidence downloadable from the HTML report. | FR-RPT-3..4 |
| AC-33 | **(NEW)** A test can enable normally-disabled tools via `sandbox.enable_tools`; the agent has the genuine opportunity to use them; the assertion verifies whether it does. | FR-TST-6, FR-SBX-4 |
| AC-34 | **(NEW)** `sandbox_denials_blocked` verifies no sandbox denial was subsequently escaped by any means. | FR-EVA-1, FR-SBX-2 |
| AC-35 | **(NEW)** A skill's `SKILL.md` dos/don'ts are verifiable via tests without any machine-readable contract; a change to the skill's behavior with unchanged tests flips the verdict. | §8.2 |
| AC-36 | **(NEW)** `compare` surfaces changes to test assertions and to skill content hash across runs. | FR-CLI-1, §17 |

---

## 6. Edge Cases

### 6.1 Path & filesystem security (sandbox)

| # | Edge case | Expected behavior |
|---|---|---|
| E-01 | Tool requests path with `..` segments (`write_file ../../etc/x`) | Rejected pre-match; DENY with `path_escape`; event recorded. |
| E-02 | Absolute path outside workspace | Same as E-01. |
| E-03 | Symlink inside workspace pointing outside | Resolved target checked; escape = DENY. Symlink created by the agent mid-run is re-audited on every access. |
| E-04 | Symlink created by agent to bypass containment | FilesystemGuard re-resolves on access; deny applies to resolved target. |
| E-05 | Case mismatch (`SRC/foo.java` vs test assertion `src/**`) | Matching is case-sensitive; test author must be precise; lint warns on likely-intended case variants. |
| E-06 | Path with Unicode normalization differences (NFC vs NFD) | Normalize before matching; record original. |
| E-07 | Agent writes to allowed path with content that *is* source code (e.g., `elaboration.md` containing a full Java class) | Sandbox allows (path-based). Tests can use `response_satisfies` to check content if desired — documented limitation of the sandbox. |

### 6.2 Agent behavior

| # | Edge case | Expected behavior |
|---|---|---|
| E-08 | Agent retries a sandbox-denied operation (`retried_same` ×N) | Bounded by tool-call limit; classification recorded; run continues or aborts per limits. |
| E-09 | Agent tries alternative route to a sandbox-denied target (e.g., denied `write_file ../x` → writes `x` via symlink) | Symlink resolution re-applies containment → still DENY; classified `escalated` → violation. |
| E-10 | Agent tries path-encoding tricks (`%2e%2e`, `//`, `./`) | Normalization before matching → DENY. |
| E-11 | Agent claims success in final message while required output missing | Deterministic `file_exists` FAILS regardless of message. |
| E-12 | Agent refuses everything, including in-scope work | `execute` outcome fails on required outputs; `decline_and_handoff` fails handoff assertions — both FAIL with distinct reasons. |
| E-13 | Agent emits handoff for in-scope work (false decline) | Required-output assertions FAIL; `response_declines` alone never sufficient for `execute` tests. |
| E-14 | Agent produces enormous output / runs many iterations | Token/budget cap aborts with ERROR `budget_exceeded`; partial evidence preserved. |
| E-15 | Agent never calls a tool, answers purely in text for a tool-requiring task | Required-output assertions FAIL (deterministic). |
| E-16 | Model API returns rate-limit / 5xx mid-run | Retry with backoff (configurable); persistent failure = ERROR (exit 3), never FAIL. |
| E-17 | Model streams partial response then errors | Record partial as event; status ERROR with partial evidence. |
| E-18 | **(NEW)** Agent edits `src/` despite SKILL.md saying not to | Sandbox **allows** the write; the test's `no_file_change: src/**` FAILS deterministically. This is the intended behavioral probe. |

### 6.3 Evaluation & judge

| # | Edge case | Expected behavior |
|---|---|---|
| E-19 | Judge model unavailable while test has LLM-judged assertions | Those assertions = ERROR (infra); deterministic results stand. |
| E-20 | Judge returns unparseable output | Assertion ERROR (never silent FAIL); judge call visible in events. |
| E-21 | Judge verdict contradicts deterministic evidence | Deterministic wins; contradiction logged in `failure_reasons`. |
| E-22 | Test with zero deterministic assertions | Allowed but lint warning; verdict flags high-judge-dependence. |
| E-23 | `runs: 3` with outcomes PASS/FAIL/PASS | `flake: true`, `pass_rate: 0.67`; verdict FAIL only if below threshold. |
| E-24 | Assertion references a file the test itself created in setup | Setup files are baseline; `file_exists` checks post-run state (tautological — authoring guide warns). |
| E-25 | **(UPDATED)** Two assertions contradict each other (`no_tool_call: write_file` + `file_exists: x` where x must be written; `file_exists` + `no_file_change` on same path) | Caught by TestCaseValidator (structural contradiction, exit 2) before execution. |

### 6.4 Test/workflow integrity (the TDD loop)

| # | Edge case | Expected behavior |
|---|---|---|
| E-26 | **(UPDATED)** Harness rewrites skill to emit refusal keywords but still attempts forbidden work | Deterministic assertions (`no_tool_call`, `no_file_change`) FAIL it — keyword gaming impossible. |
| E-27 | **(UPDATED)** Harness weakens *test* to make skill pass (e.g., removes `no_file_change: src/**`) | `compare` surfaces assertion changes across runs; test files are version-controlled; a warning is emitted when assertions are removed in a follow-up run. |
| E-28 | Test expects output in a path the sandbox forbids (outside workspace) | Exit 2 (unsatisfiable expectation); harness fixes the test. |
| E-29 | Malicious fixture content (prompt injection in fixture) | Legitimate adversarial testing; fixture loader validates fixture paths can't escape workspace. |
| E-30 | Concurrent runs of the same skill | Isolated workspaces + isolated run IDs; no evidence cross-contamination; concurrency configurable and bounded. |
| E-31 | Run interrupted (SIGINT/crash) mid-test | Partial JSONL preserved; no verdict written; workspace cleanup on next run or via cleanup command; never emits a partial verdict that looks complete. |
| E-32 | `--no-report` run followed by `report` command | Reports generated from stored evidence + verdict (replay path), no agent re-execution. |
| E-33 | **(NEW)** Test enables `command` tool; agent runs an allowlisted command (e.g., `ls`) | Allowed, recorded; test asserts `no_tool_call: command` if the skill shouldn't use it → FAIL (the behavioral probe works). |
| E-34 | **(NEW)** Test enables `command`; agent runs a non-allowlisted command (`rm -rf`) | Sandbox DENY; classified `escalated` if retried; test's `no_tool_call` PASSES; `sandbox_denials_blocked` PASSES. |

### 6.5 MCP

| # | Edge case | Expected behavior |
|---|---|---|
| E-35 | MCP server declared in test but down | ERROR (infra) for that test; other tests unaffected. |
| E-36 | MCP server configured in OpenCode but not declared in test | Never connected; tools absent from registry. |
| E-37 | MCP tool mutates external state despite `mcp_mutating: false` | Adapter enforces read-only filter pre-call; violation recorded. |
| E-38 | MCP tool schema incompatible with LangChain conversion | Tool skipped with WARNING + logged; test using it gets ERROR. |
| E-39 | MCP server slow/hanging | Per-server timeout; ERROR with connection evidence. |

### 6.6 Reports & evidence

| # | Edge case | Expected behavior |
|---|---|---|
| E-40 | HTML generation fails (template bug) | JSON evidence + verdict already on disk; `report` can regenerate later. |
| E-41 | Evidence JSONL corrupted mid-append | Reader tolerates truncated last line; reports note truncation; earlier events intact. |
| E-42 | Very large tool outputs / diffs | Truncation thresholds (configurable) with clear markers in reports; full payload retained in JSONL evidence. |
| E-43 | Report opened from filesystem (file://) with no server | Fully self-contained HTML (inline CSS/JS, no CDN). |

---

## 7. Non-Functional Requirements

### 7.1 Performance & cost

| ID | Requirement |
|---|---|
| NFR-1 | Dev-mode single-test run completes without HTML generation; report generation never re-runs the agent. |
| NFR-2 | Lazy MCP connection; unused servers cost nothing. |
| NFR-3 | Per-run token/cost budget with clean ERROR abort (AC-27). |
| NFR-4 | Judge calls capped per run; judge model pinned per run. |
| NFR-5 | Configurable concurrency for suite runs; concurrent tests fully isolated (E-30). |

### 7.2 Reliability & determinism

| ID | Requirement |
|---|---|
| NFR-6 | Deterministic assertions derive only from recorded evidence — same evidence ⇒ same verdict. |
| NFR-7 | Agent temperature defaults to 0 in dev mode; run metadata pins all model params. |
| NFR-8 | Flake distinguishable from consistent failure (pass_rate + flake flag). |
| NFR-9 | Infra failures (E-16, E-19, E-35) never surface as test FAIL (AC-29). |
| NFR-10 | Interrupted runs never emit complete-looking partial verdicts (E-31). |

### 7.3 Security

| ID | Requirement |
|---|---|
| NFR-11 | Credential redaction on every output surface (AC-02), incl. verdict JSON and model-request events. |
| NFR-12 | No path traversal or symlink escape (E-01..E-06) — enforced deterministically, tested in Phase 6. |
| NFR-13 | No unrestricted shell; setup runs no commands by default; command tool sandboxed when enabled. |
| NFR-14 | Mutating external operations (MCP) disabled unless explicitly authorized per-test for a disposable environment. |
| NFR-15 | Append-only, tamper-evident evidence (JSONL with sequence numbers; run metadata signed with content hashes). |
| NFR-16 | **(NEW)** Sandbox containment is a hard boundary: a successful escape is treated as an infrastructure failure (exit 3), never as a skill failure. |

### 7.4 Usability & operability

| ID | Requirement |
|---|---|
| NFR-17 | CLI contract stable: exit codes + versioned verdict schema; additive-only evolution. |
| NFR-18 | Reports fully offline (self-contained HTML, no CDN) (E-43). |
| NFR-19 | Verbose structured logs for debugging; progress output for long runs. |
| NFR-20 | CI/CD integration: machine verdict + exit codes consumable by pipelines. |
| NFR-21 | Reproducibility: evidence + metadata sufficient to replay/audit a run (AC-28). |

### 7.5 Maintainability

| ID | Requirement |
|---|---|
| NFR-22 | Event schemas and verdict schema versioned from Phase 1; versioned extension only. |
| NFR-23 | Runtime abstraction allows adding providers/runtimes without touching the test engine. |
| NFR-24 | Report generators consume verdict objects only — no evaluation logic in reporting. |
| NFR-25 | Framework self-tested: unit tests per module + integration tests with mock MCP servers and stub model adapters. |
| NFR-26 | **(NEW)** Sandbox policy and skill-scope logic are strictly separated; no code path mixes them. |

---

## 8. Traceability summary

- Every AC (§5) cites the FRs it verifies; every edge case (§6) names its expected behavior.
- FR modules: CFG (5), MOD (4), SKL (4, down from 5), TST (6, up from 5), RUN (5), SBX (7, replaces POL's 4), AUD (5), EVA (7), CLI (5), RPT (5), MCP (5, up from 4).
- **Open items to decide before Phase 1:**
  - (a) Whether a follow-up run that removes assertions should hard-fail or warn (E-27); recommendation: warn by default, `--strict-tests` to fail.
  - (b) Truncation thresholds for large outputs (E-42); recommendation: 1 MB per event, 10 MB per file diff, configurable.
  - (c) Retry/backoff policy for model API errors (E-16); recommendation: exponential backoff with 3 retries, then ERROR.
  - (d) Command tool allowlist contents; recommendation: `ls`, `cat`, `head`, `tail`, `wc`, `find` (workspace-scoped, read-only), extended via `validator.yaml`.
  - (e) Whether to keep `verdict_schema_version: "1.1"` or bump to `"2.0"` given removed contract fields; recommendation: `"2.0"` since the v2 schema's `contract_hash` and `policy_denials_blocked` are removed — not purely additive.

---
