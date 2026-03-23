# Terminal Chat Application (TUI) – Full Specification

---

## 1. Overview

Build a **full-screen terminal-based chat application (TUI)** that interacts with a backend chat API, maintains **persistent local session history**, and provides an **interactive, keyboard-driven user interface** with real-time updates.

The application should behave like a **ChatGPT-style terminal client** with:

* Continuous conversational interaction
* Persistent multi-session storage
* Interactive history selection
* Full Markdown rendering of responses
* Non-blocking UI with loader animation
* Always-visible command hints via status bar

This is a **stateful, event-driven terminal application**, not a traditional CLI tool.

---

## 2. Core Functional Requirements

### 2.1 Chat Interaction

* User types input in a dedicated input area
* Application sends request to API:

```json
{
  "message": "<user input>",
  "sessionId": "<session id>"
}
```

* API responds:

```json
{
  "content": "<response>"
}
```

* Response is rendered in chat display
* Conversation continues until user exits

---

## 3. Session Persistence

### 3.1 Storage Structure

```
~/.chat-cli/
  sessions/
    <session-id>.json
  index.json
```

---

### 3.2 Session File

Each session file contains:

* `sessionId` (UUID)
* `title` (derived from first user message)
* `createdAt` timestamp
* `updatedAt` timestamp
* `messages` array:

  * `role`: "user" or "assistant"
  * `content`: string

---

### 3.3 Index File

Contains:

* `sessions` list:

  * `sessionId`
  * `title`
  * `updatedAt`

---

### 3.4 Session Lifecycle

* **Create**

  * On application startup
  * On `/new` command

* **Update**

  * Persist every message immediately

* **Load**

  * Via history selector UI

* **Title**

  * First user message (truncated)

---

## 4. Commands

| Command    | Description                  |
| ---------- | ---------------------------- |
| `/exit`    | Exit application             |
| `/new`     | Start new session            |
| `/history` | Open session selector        |
| `/clear`   | Clear chat window (optional) |
| `/help`    | Show command descriptions    |

---

## 5. Terminal UI

### 5.1 Layout

```
┌──────────────────────────────┐
│ Chat Display (scrollable)    │
│                              │
│ You: ...                     │
│ Bot: ...                     │
│                              │
├──────────────────────────────┤
│ > Input Area                 │
├──────────────────────────────┤
│ [ /exit | /new | /history | /help ] │
└──────────────────────────────┘
```

---

### 5.2 UI Capabilities

* Full-screen terminal mode
* Scrollable chat history
* Persistent input field
* Keyboard-driven interaction
* Real-time rendering updates
* Non-blocking UI
* Unicode-safe rendering

---

## 6. Status Bar (Command Discovery)

* Always visible at bottom of screen
* Displays available commands:

```
/exit | /new | /history | /help
```

### Requirements

* Non-interactive
* Fixed position (not scrollable)
* Visible in all states:

  * idle
  * typing
  * loading
  * rendering

---

## 7. Loader / Activity Indicator

### 7.1 Requirement

Display an animated loader while API call is in progress.

---

### 7.2 Behavior

* Starts immediately after user submits input
* Stops when API response is received

---

### 7.3 Example (Inline)

```
You: What is Kafka?
Bot: [thinking... ⠋⠙⠹⠸]
```

---

### 7.4 Animation Requirements

* Must be animated (not static)
* Refresh interval: 100–200 ms
* Must not block UI

---

### 7.5 Concurrency

* Loader runs asynchronously
* API call must not freeze UI

---

## 8. History Selection UI

### 8.1 Trigger

* `/history`

---

### 8.2 Behavior

* Display list of sessions
* Navigate using arrow keys (↑ ↓)
* Press Enter to select
* Selected session loads into chat window

---

### 8.3 UI Type

* Modal / overlay inside terminal
* Must not terminate main UI

---

## 9. Configuration (JSON)

### File: `config.json`

```json
{
  "api": {
    "url": "http://localhost:8080/chat",
    "timeout": 10
  },
  "storage": {
    "base_dir": ".chat-cli"
  },
  "ui": {
    "fullscreen": true,
    "show_timestamps": true,
    "loader_enabled": true,
    "markdown_enabled": true
  }
}
```

---

### Requirements

* Loaded at startup
* Extensible
* Optional dynamic reload support

---

## 10. Full Markdown Rendering

### 10.1 Requirement

All chat responses must support full Markdown rendering.

---

### 10.2 Supported Features

* Headers (`#`, `##`, `###`)
* Bold and italic
* Inline code
* Code blocks (with syntax highlighting)
* Lists (ordered/unordered)
* Tables
* Blockquotes
* Horizontal rules
* Nested structures

---

### 10.3 Rendering Behavior

* Markdown must be rendered as formatted terminal output
* Raw Markdown syntax must not be displayed
* Code blocks must preserve indentation
* Tables must align correctly

---

### 10.4 Constraints

* Must support multiline content
* Must not break UI layout
* Must not block UI thread

---

## 11. Architecture

### Components

1. **API Client**

   * Handles HTTP communication
   * Stateless

2. **Session Store**

   * Manages session files and index
   * Handles persistence

3. **UI Layer (TUI)**

   * Renders layout
   * Handles input
   * Displays loader and markdown

4. **Controller**

   * Coordinates:

     * user input
     * API calls
     * session updates
     * UI updates

---

## 12. Data Flow

```
User Input
   ↓
UI Event Handler
   ↓
Controller
   ↓
Start Loader
   ↓
API Client Call
   ↓
Stop Loader
   ↓
Session Store (persist)
   ↓
Markdown Render
   ↓
UI Update
```

---

## 13. Execution Model

* Event-driven architecture
* Continuous UI loop
* Non-blocking rendering
* API calls handled asynchronously or via threads

---

## 14. State Machine

```mermaid
stateDiagram-v2
    [*] --> INITIALIZE
    INITIALIZE --> IDLE
    IDLE --> USER_INPUT
    USER_INPUT --> LOADING
    LOADING --> RESPONSE_RENDER
    RESPONSE_RENDER --> IDLE
    IDLE --> HISTORY_SELECTION
    HISTORY_SELECTION --> IDLE
    IDLE --> EXIT
```

---

## 15. Event Flow

```mermaid
flowchart TD
    A[User Input] --> B[UI Event Handler]
    B --> C[Controller]
    C --> D[Start Loader]
    D --> E[API Call]
    E --> F[Stop Loader]
    F --> G[Persist Session]
    G --> H[Render Markdown]
    H --> I[Update UI]
```

---

## 16. Non-Functional Requirements

* Cross-platform terminal compatibility
* UTF-8 support
* Smooth rendering (no flicker)
* Fault-tolerant API handling
* Lightweight runtime
* Scalable for large sessions

---

## 17. Final Summary

Build a **full-screen, event-driven terminal chat application** with:

* JSON-based configuration
* Persistent session storage
* Interactive history selection
* Fixed status bar for command discovery
* Non-blocking animated loader
* Full Markdown rendering support

The system should function as a **terminal-native ChatGPT-like client** with professional UX and extensible architecture.
