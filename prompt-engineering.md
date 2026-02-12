# Session 1: Prompt Engineering – From Basic Text to Markdown Magic  
## Complete Reference Guide – All Segments in One Document

Use this as your master plan for conducting a 75–90 minute live session. Copy the entire document, adapt it to your style, and run the demos directly from the provided prompt library.

---

## Session Overview

| Item          | Details |
|---------------|---------|
| **Duration**  | 75–90 minutes |
| **Format**    | Live lecture with real‑time LLM chat demonstrations (ChatGPT, Claude, Gemini) |
| **Audience**  | Beginners in prompt engineering, technical and non‑technical |
| **Goal**      | Transform how attendees think about writing prompts – from simple queries to structured, markdown‑powered instructions that yield high‑quality, reusable outputs. |
| **Core Focus**| Six prompt types + markdown formatting |

---

## Learning Objectives

By the end of this session, participants will be able to:

1. **Identify six common types of prompts** (zero‑shot, few‑shot, persona, constraint‑based, comparative, multi‑step) and know when to use each.
2. **Write clear, specific prompts** that reduce ambiguity and improve LLM output.
3. **Use plain text prompts** effectively for Q&A, summarisation, role‑playing, comparisons, and multi‑step tasks.
4. **Structure prompts with markdown** (headings, lists, tables, code blocks) to control output format and quality.
5. **Recognise how prompt structure and type influence LLM behaviour** – and how to combine them for maximum effect.

---

## Detailed Session Outline

| Segment | Duration | Content |
|---------|----------|---------|
| **1. Introduction** | 5 min | What is prompt engineering? Why does it matter? The “brilliant intern” analogy. |
| **2. Anatomy of a Prompt** | 10 min | The four pillars: Instruction, Context, Input Data, Output Format. Poor vs. structured prompt comparison. |
| **3. Types of Prompts – Your Toolkit** | 5 min | Overview of six prompt types with definitions and use cases. |
| **4. Basic Text Prompts – Live Demos** | 20 min | One demo for each prompt type in plain text (no markdown). Show the power of type alone. |
| **5. Why Markdown?** | 5 min | The case for structured prompts: clarity, reusability, visual parsing, output mirroring. |
| **6. Markdown‑Driven Prompts – Live Demos** | 25 min | Revisit each prompt type with markdown enhancements – headings, tables, bullet lists, code blocks, sections. |
| **7. Wrap‑Up & Q&A** | 5 min | Key takeaways, homework challenge, resource links. |

---

# SEGMENT 1: Introduction (5 min)

**Key Message:**  
Prompt engineering is the art of communicating with LLMs to get *useful* outputs. It’s not about “magic words” – it’s about structure, clarity, and iterative refinement.

**Engagement Opener:**  
> *“Think of the LLM as an extremely brilliant but very literal intern. It can do amazing things, but it needs crystal‑clear instructions. Today we learn how to give those instructions – from simple sentences to beautifully formatted markdown.”*

---

# SEGMENT 2: Anatomy of a Prompt (10 min)

Introduce the four components that transform a vague query into a well‑engineered prompt.

| Component | Purpose | Example |
|----------|---------|---------|
| **Instruction** | What you want the model to do | “Summarise the following article in three bullet points.” |
| **Context** | Background that shapes the response | “You are an expert in climate science.” |
| **Input Data** | The content the model should work on | “Article: [text]” |
| **Output Format** | How you want the answer structured | “Use headings and a table at the end.” |

**Demonstration – Poor vs. Structured Prompt (Live)**  

**Poor prompt:**  
> Tell me about renewable energy.

**Structured prompt:**  
> **Instruction:** Explain the three main types of renewable energy.  
> **Context:** Assume I am a high school student with basic science knowledge.  
> **Output Format:** Use simple language, and provide a comparison table at the end.

*Run both in the LLM and compare outputs. Highlight how the structured prompt yields a targeted, organised answer.*

---

# SEGMENT 3: Types of Prompts – Your Toolkit (5 min)

Introduce the six prompt types that attendees will practice.

| Prompt Type | Description | When to Use |
|------------|-------------|-------------|
| **Zero‑Shot** | Direct instruction with no examples | Quick Q&A, simple tasks |
| **Few‑Shot** | Provide 1–3 examples before the task | Teaching a pattern, formatting, style transfer |
| **Persona / Role** | Assign a role or perspective | Advice, specialised tone, creative writing |
| **Constraint‑Based** | Impose rules (length, format, keywords) | Summaries, structured outputs |
| **Comparative** | Ask for comparison or analysis | Decision support, feature breakdown |
| **Multi‑Step / Chain** | Break a complex task into sequential steps | Planning, reasoning, workflows |

> **Insight:** Each type can be expressed in plain text, but **markdown makes them dramatically clearer**.

---

# SEGMENT 4: Basic Text Prompts – Live Demos (20 min)

*Goal: Demonstrate each prompt type in its simplest form. No markdown yet – just the power of choosing the right type.*

---

### Demo 1: Zero‑Shot
**Prompt:**  
> Explain what blockchain is.

**Observation:** Straight answer, no frills. Good for general knowledge.

---

### Demo 2: Few‑Shot (Pattern Teaching)
**Prompt:**  
> Convert movie titles into emojis.  
> Example: "The Lion King" → 🦁👑  
> Example: "Titanic" → 🚢❄️  
> Now convert "Jurassic Park" and "Inception".

**Observation:** The model learns the pattern from examples and applies it correctly.

---

### Demo 3: Persona / Role
**Prompt:**  
> You are an experienced cybersecurity consultant.  
> Explain to a small business owner why they need two‑factor authentication. Use simple, non‑technical language.

**Observation:** Tone and content shift dramatically based on the persona.

---

### Demo 4: Constraint‑Based
**Prompt:**  
> Summarise the following product review in **one sentence**. Start with the word "Overall".  
> *[paste a long review]*

**Observation:** Enforcing a strict format yields concise, predictable output.

---

### Demo 5: Comparative
**Prompt:**  
> Compare electric cars and hydrogen fuel cell cars.  
> Focus on: refuelling time, environmental impact, and current infrastructure.

**Observation:** The model naturally structures the comparison, often in paragraphs or lists.

---

### Demo 6: Multi‑Step / Chain
**Prompt:**  
> Step 1: List three common causes of project delays in software development.  
> Step 2: For each cause, suggest one preventative measure.  
> Step 3: Write a one‑paragraph summary of these measures.

**Observation:** The model follows the sequence and produces a coherent, layered answer.

---

# SEGMENT 5: Why Markdown? (5 min)

**Transition:**  
*“Now that you’ve seen how a few extra words can change everything, let’s look at how we can use formatting to speak the LLM’s native language – and get outputs we can copy‑paste directly into documents.”*

**Benefits of using markdown in prompts:**
- **Visual clarity** – both you and the LLM can parse the prompt easily.
- **Output structure** – the model often mimics the format you use.
- **Reusability** – markdown templates can be saved and adapted.
- **Precision** – headings separate sections, lists enumerate items, tables enforce columns.

**Show a side‑by‑side:**  
A plain text prompt that is a wall of text vs. the same prompt beautifully formatted with headings, bullet points, and a table.

---

# SEGMENT 6: Markdown‑Driven Prompts – Live Demos (25 min)

*Goal: Revisit the same six prompt types, but this time **encode them with markdown syntax** to gain precision and better output structure.*

---

### Demo 1 (Zero‑Shot + Markdown)

**Plain text (reminder):**  
> Explain what blockchain is.

**Markdown version:**  
```markdown
# Request
Explain blockchain in simple terms.

## Format
- Use **bullet points** for key concepts.
- End with a **one‑sentence summary** in *italics*.
```

**Outcome:** The model returns a clean, formatted list and italic summary.

---

### Demo 2 (Few‑Shot + Markdown Table)

**Plain text (reminder):**  
> Convert movie titles into emojis.  
> Example: "The Lion King" → 🦁👑  
> Example: "Titanic" → 🚢❄️  
> Now convert "Jurassic Park" and "Inception".

**Markdown version:**  
```markdown
You are a translator for technical jargon.  
I will give you a term and a category; you provide a simple definition.

| Term | Category | Simple Definition |
|------|----------|-------------------|
| API  | Software | A way for two programs to talk to each other. |
| IDE  | Tools    | An application where you write and test code. |

Now do the same for:
| Term      | Category      | Simple Definition |
|-----------|---------------|-------------------|
| Blockchain | Technology   |                  |
| SEO       | Marketing     |                  |
```

**Outcome:** The model completes the table with consistent definitions. **Few‑shot learning supercharged by markdown**.

---

### Demo 3 (Persona + Markdown Headings)

**Plain text (reminder):**  
> You are an experienced cybersecurity consultant. Explain to a small business owner why they need two‑factor authentication. Use simple, non‑technical language.

**Markdown version:**  
```markdown
# Persona
You are Marie Curie, the physicist and chemist.

# Task
Explain radioactivity to a classroom of 12‑year‑olds.

# Tone
Curious, encouraging, and historically aware.

# Output Style
Use two short paragraphs and include one analogy.
```

**Outcome:** The answer is not just informative – it’s written **in character**, with a friendly, historical flavour.

---

### Demo 4 (Constraint + Markdown Bullets)

**Plain text (reminder):**  
> Summarise the following product review in one sentence. Start with the word "Overall".  
> [paste a long review]

**Markdown version:**  
```markdown
# Constraint
Write a tweet‑length pitch for a new productivity app.

# Must include
- App name: "FocusFlow"
- One key feature (max 6 words)
- A call to action

# Format
Use a single sentence, no hashtags.
```

**Outcome:** Perfectly constrained output, easy to copy‑paste.

---

### Demo 5 (Comparative + Markdown Table)

**Plain text (reminder):**  
> Compare electric cars and hydrogen fuel cell cars.  
> Focus on: refuelling time, environmental impact, and current infrastructure.

**Markdown version:**  
```markdown
# Comparison Request
Compare **React** and **Vue** according to these criteria:

| Criteria         | React | Vue |
|------------------|-------|-----|
| Learning curve   |       |     |
| Popularity       |       |     |
| Ideal use case   |       |     |

Fill the table with concise entries.
```

**Outcome:** The model fills the table, giving a clean, side‑by‑side comparison.

---

### Demo 6 (Multi‑Step + Markdown Sections)

**Plain text (reminder):**  
> Step 1: List three common causes of project delays in software development.  
> Step 2: For each cause, suggest one preventative measure.  
> Step 3: Write a one‑paragraph summary of these measures.

**Markdown version:**  
```markdown
# Step 1 – Brainstorm
List 5 potential names for a sustainable coffee shop.

# Step 2 – Evaluate
For each name, write one **pro** and one **con**.

# Step 3 – Recommend
Pick the best name and explain why in one sentence.

Use headings and bullet points in your response.
```

**Outcome:** The model produces a structured, easy‑to‑follow answer that mirrors the prompt’s organisation.

---

# SEGMENT 7: Wrap‑Up & Q&A (5 min)

## Key Takeaways

1. **Every prompt can be broken into** Instruction, Context, Input, Output.
2. **Prompt types are your vocabulary** – zero‑shot, few‑shot, persona, constraint, comparative, multi‑step.
3. **Markdown is your grammar** – it structures that vocabulary into clear, executable instructions.
4. **The model mirrors your formatting** – feed it clean markdown, get clean markdown back.
5. **Combine types and formatting** – a persona + constraint + markdown table is incredibly powerful.

## Homework Challenge

> *“Choose one of the six prompt types. Write a plain‑text version and a markdown‑enhanced version. Run both in an LLM. Bring the before/after to the next session!”*

## Resources

- [OpenAI Prompt Engineering Guide](https://platform.openai.com/docs/guides/prompt-engineering)
- [Anthropic’s Prompt Engineering Resources](https://docs.anthropic.com/claude/docs/prompt-engineering)
- [Markdown Cheat Sheet](https://www.markdownguide.org/cheat-sheet/)

---

# Instructor Tips for a Smooth Live Session

1. **Prepare the LLM in advance** – clear the conversation or start a new thread to avoid context bleed.
2. **Use a prepared prompt library** – all demos are provided below; copy‑paste to avoid typos.
3. **Show failures too** – a “bad” prompt that produces a vague answer makes the good ones shine.
4. **Encourage chat participation** – ask attendees what they would add or change.
5. **Keep it fast‑paced** – 3‑4 minutes per demo is enough; the wow factor is in the contrast.
6. **Use side‑by‑side views** – show plain text and markdown versions on two tabs or split screen.

---

# Complete Prompt Library – Copy‑Paste Ready for Your Session

Save the following block into a text file. Each demo is separated and ready to be pasted into your LLM chat.

```markdown
--- 1. ZERO-SHOT (plain) ---
Explain what blockchain is.

--- 1a. ZERO-SHOT + MARKDOWN ---
# Request
Explain blockchain in simple terms.

## Format
- Use **bullet points** for key concepts.
- End with a one‑sentence summary in *italics*.

--- 2. FEW-SHOT (plain) ---
Convert movie titles into emojis.
Example: "The Lion King" → 🦁👑
Example: "Titanic" → 🚢❄️
Now convert "Jurassic Park" and "Inception".

--- 2a. FEW-SHOT + MARKDOWN TABLE ---
You are a translator for technical jargon.
I will give you a term and a category; you provide a simple definition.

| Term | Category | Simple Definition |
|------|----------|-------------------|
| API  | Software | A way for two programs to talk to each other. |
| IDE  | Tools    | An application where you write and test code. |

Now do the same for:
| Term      | Category      | Simple Definition |
|-----------|---------------|-------------------|
| Blockchain | Technology   |                  |
| SEO       | Marketing     |                  |

--- 3. PERSONA (plain) ---
You are an experienced cybersecurity consultant.
Explain to a small business owner why they need two‑factor authentication. Use simple, non‑technical language.

--- 3a. PERSONA + MARKDOWN HEADINGS ---
# Persona
You are Marie Curie, the physicist and chemist.

# Task
Explain radioactivity to a classroom of 12‑year‑olds.

# Tone
Curious, encouraging, and historically aware.

# Output Style
Use two short paragraphs and include one analogy.

--- 4. CONSTRAINT (plain) ---
Summarise the following product review in one sentence. Start with the word "Overall".
[paste a long review here]

--- 4a. CONSTRAINT + MARKDOWN BULLETS ---
# Constraint
Write a tweet‑length pitch for a new productivity app.

# Must include
- App name: "FocusFlow"
- One key feature (max 6 words)
- A call to action

# Format
Use a single sentence, no hashtags.

--- 5. COMPARATIVE (plain) ---
Compare electric cars and hydrogen fuel cell cars.
Focus on: refuelling time, environmental impact, and current infrastructure.

--- 5a. COMPARATIVE + MARKDOWN TABLE ---
# Comparison Request
Compare **React** and **Vue** according to these criteria:

| Criteria         | React | Vue |
|------------------|-------|-----|
| Learning curve   |       |     |
| Popularity       |       |     |
| Ideal use case   |       |     |

Fill the table with concise entries.

--- 6. MULTI-STEP (plain) ---
Step 1: List three common causes of project delays in software development.
Step 2: For each cause, suggest one preventative measure.
Step 3: Write a one‑paragraph summary of these measures.

--- 6a. MULTI-STEP + MARKDOWN SECTIONS ---
# Step 1 – Brainstorm
List 5 potential names for a sustainable coffee shop.

# Step 2 – Evaluate
For each name, write one **pro** and one **con**.

# Step 3 – Recommend
Pick the best name and explain why in one sentence.

Use headings and bullet points in your response.
```

---

# Bonus: Quick Reference Card – Prompt Types & Markdown

Print this or keep it open during your session.

| Prompt Type     | Best Used For                          | Markdown Enhancement               |
|-----------------|----------------------------------------|------------------------------------|
| **Zero‑Shot**   | Simple Q&A, definitions                | Headings, bullet lists             |
| **Few‑Shot**    | Teaching patterns, style transfer      | Tables for examples, code blocks   |
| **Persona**     | Role‑specific advice, creative writing | Headings for persona/task/tone     |
| **Constraint**  | Summaries, strict formatting           | Bullet lists, bold for keywords    |
| **Comparative** | Feature breakdown, decision support    | Tables with empty cells to fill    |
| **Multi‑Step**  | Workflows, planning, reasoning         | Numbered headings, sections        |

---

**You are now ready to deliver a powerful, engaging, and highly practical session on prompt engineering.**  

*Good luck!* 🚀



# Session 1: Prompt Engineering – From Basic Text to Markdown Magic

## Session Overview
**Duration:** 60–75 minutes  
**Format:** Live lecture with real‑time LLM chat demonstrations (e.g., ChatGPT, Claude, Gemini)  
**Audience:** Beginners in prompt engineering, technical and non‑technical  
**Goal:** Transform the way attendees think about writing prompts – from simple text queries to structured, markdown‑powered instructions that yield high‑quality, reusable outputs.

---

## Learning Objectives
By the end of this session, participants will be able to:
- Identify the core components of an effective prompt.
- Write clear, specific prompts that reduce ambiguity.
- Use plain text prompts for common tasks (Q&A, summarisation, brainstorming).
- Structure prompts with markdown (headings, lists, tables, code blocks) to control output format.
- Recognise how prompt structure influences LLM behaviour and output quality.

---

## Session Outline

| Segment | Duration | Content |
|--------|----------|---------|
| **1. Introduction** | 5 min | What is prompt engineering? Why does it matter? |
| **2. Anatomy of a Prompt** | 10 min | The four pillars: Instruction, Context, Input Data, Output Format |
| **3. Basic Text Prompts** | 15 min | Live demos: Q&A, summarisation, role‑playing, creative tasks |
| **4. Why Markdown?** | 5 min | The case for structured prompts – clarity, reusability, visual parsing |
| **5. Markdown‑Driven Prompts** | 20 min | Live demos: headings, bullet lists, tables, code blocks, and mixed structures |
| **6. Wrap‑Up & Q&A** | 5 min | Key takeaways, resources, homework challenge |

---

# Detailed Session Script with Real‑Time Demo Prompts

## 1. Introduction (5 min)
**Key Message:** Prompt engineering is the art of communicating with LLMs to get *useful* outputs. It’s not about “magic words” – it’s about structure, clarity, and iterative refinement.

**Engagement opener:**  
> *“Think of the LLM as an extremely brilliant but very literal intern. It can do amazing things, but it needs crystal‑clear instructions. Today we learn how to give those instructions – from simple sentences to beautifully formatted markdown.”*

---

## 2. Anatomy of a Prompt (10 min)
Introduce the four components that transform a vague query into a well‑engineered prompt.

| Component | Purpose | Example |
|----------|---------|---------|
| **Instruction** | What you want the model to do | “Summarise the following article in three bullet points.” |
| **Context** | Background information that shapes the response | “You are an expert in climate science.” |
| **Input Data** | The content the model should work on | “Article: [text]” |
| **Output Format** | How you want the answer structured | “Use headings and a table at the end.” |

**Demonstration:**  
Show a “poor” prompt vs. a “structured” prompt side by side.

**Poor prompt:**  
> Tell me about renewable energy.

**Structured prompt:**  
> **Instruction:** Explain the three main types of renewable energy.  
> **Context:** Assume I am a high school student with basic science knowledge.  
> **Output Format:** Use simple language, and provide a comparison table at the end.

*Run both in the LLM and compare the outputs.*

---

## 3. Basic Text Prompts – Live Demos (15 min)
*Goal: Show that even without markdown, specificity and context dramatically improve results.*

### Demo 1: Simple Q&A → Enhanced with Context
**Prompt A:**  
> What is the capital of France?

**Prompt B:**  
> You are a tour guide speaking to a group of 10‑year‑olds.  
> What is the capital of France, and can you tell me one interesting fact about it that kids would love?

**Observations:** Prompt B adds persona and audience, yielding a more engaging answer.

### Demo 2: Summarisation with Length Constraint
**Prompt A:**  
> Summarise this: [paste a long paragraph]

**Prompt B:**  
> Summarise the following text in exactly 3 sentences. Start each sentence with a key theme word in **bold**.  
> [text]

**Observations:** The constraint forces conciseness and a specific structure.

### Demo 3: Role‑Playing / Persona
**Prompt:**  
> Act as a career coach. I have 5 years of marketing experience and want to move into product management.  
> Give me 3 actionable steps I should take next month.

**Takeaway:** Role prompts shape tone and perspective.

---

## 4. Why Markdown? (5 min)
**Transition:**  
*“Now that you’ve seen how a few extra words can change everything, let’s look at how we can use formatting to speak the LLM’s native language – and get outputs we can copy‑paste directly into documents.”*

**Benefits of using markdown in prompts:**
- **Visual clarity** – both you and the LLM can parse the prompt easily.
- **Output structure** – the model often mimics the format you use.
- **Reusability** – markdown templates can be saved and adapted.
- **Precision** – headings separate sections, lists enumerate items, tables enforce columns.

**Show a side‑by‑side:**  
A plain text prompt that is a wall of text vs. the same prompt beautifully formatted with headings, bullet points, and a table.

---

## 5. Markdown‑Driven Prompts – Live Demos (20 min)
*Demonstrate how markdown syntax in the prompt shapes the output – often the LLM will reply in similar markdown.*

### Demo 4: Using Headings to Separate Instructions
**Prompt:**  
```markdown
# Task
Write a product description for a noise‑cancelling headphone.

# Target Audience
Frequent travelers, especially those who work on planes.

# Key Features
- 30 hours battery life
- Foldable design
- Built‑in microphone for calls

# Tone
Professional but friendly. Keep it under 100 words.
```

**Observation:** The LLM respects each section and often returns the answer with appropriate headings.

---

### Demo 5: Bullet Lists for Clear Requests
**Prompt:**  
```markdown
List 5 benefits of meditation. For each benefit:
- Give a one‑sentence explanation
- Provide a short real‑life example

Format your answer as a bullet list.
```

**Observation:** The model replicates the bullet‑list structure in its response, making it scannable.

---

### Demo 6: Tables to Force Structured Data
**Prompt:**  
```markdown
Compare Python, JavaScript, and C++ according to the following criteria:
- Primary use cases
- Learning curve (Easy/Medium/Hard)
- Typing (Static/Dynamic)

Present the comparison in a **markdown table**.
```

**Observation:** The LLM generates a neatly formatted table. This is extremely useful for data extraction and side‑by‑side comparisons.

---

### Demo 7: Code Blocks for Programming Tasks
**Prompt:**  
```markdown
Write a Python function that takes a list of numbers and returns the sum of squares.

Put the code inside a single code block with syntax highlighting (```python).  
After the code, explain how the function works in 2 bullet points.
```

**Observation:** The output is immediately copyable and professional.

---

### Demo 8: Combining Markdown Elements
**Prompt:**  
```markdown
# Meeting Agenda Generator

**Role:** Executive Assistant  
**Meeting Topic:** Q3 Marketing Review  
**Attendees:** Marketing Director, Sales Lead, Product Manager

Please generate:
1. A **table** with columns: Time, Agenda Item, Owner
2. Followed by a **bullet list** of three preparation tasks for attendees
3. End with a **quote** to inspire the team

Use appropriate markdown formatting.
```

**Observation:** The model produces a complex, well‑organised output that mirrors the requested structure.

---

## 6. Wrap‑Up & Q&A (5 min)
**Key Takeaways:**
- Every prompt can be broken into Instruction, Context, Input, Output.
- Specificity beats length – a few well‑chosen words > long rambling.
- Markdown is not just for output – use it in your prompts to guide the model.
- The model mimics your formatting: feed it clean markdown, get clean markdown back.

**Homework Challenge for Attendees:**  
*“Take a prompt you used last week and rewrite it using at least two markdown elements (e.g., headings + a table). Run it and see how the response changes. Bring your before/after to the next session!”*

**Resources:**  
- [OpenAI Prompt Engineering Guide](https://platform.openai.com/docs/guides/prompt-engineering)  
- [Anthropic’s Prompt Engineering Resources](https://docs.anthropic.com/claude/docs/prompt-engineering)  
- [Markdown Cheat Sheet](https://www.markdownguide.org/cheat-sheet/)

---

# Instructor Tips for a Smooth Live Session

1. **Prepare the LLM in advance** – clear the conversation or start a new thread to avoid context bleed.
2. **Use copy‑pasted prompts** – typing live can cause typos; pre‑write all demos in a text file.
3. **Show failures too** – a “bad” prompt that produces a vague answer makes the good ones shine.
4. **Encourage chat participation** – ask attendees what they would add or change.
5. **Keep it fast‑paced** – 3‑4 minutes per demo is enough; the wow factor is in the contrast.

---

# Example Prompt File for the Session (Copy‑Paste Ready)

You can copy the following block into a text file to run all demos quickly:

```markdown
--- DEMO 1: Q&A with Persona ---
You are a tour guide speaking to 10‑year‑olds.  
What is the capital of France, and tell me one fun fact about it?

--- DEMO 2: Summarisation with Constraint ---
Summarise this text in exactly 3 sentences. Start each sentence with a key theme word in **bold**.
[Paste any article]

--- DEMO 3: Role Play ---
Act as a career coach. I have 5 years marketing experience, want to move into product management.  
Give me 3 actionable steps for next month.

--- DEMO 4: Headings ---
# Task
Write a product description for noise‑cancelling headphones.

# Target Audience
Frequent travelers who work on planes.

# Key Features
- 30h battery
- Foldable
- Built‑in mic

# Tone
Professional, friendly, <100 words.

--- DEMO 5: Bullet Lists ---
List 5 benefits of meditation. For each:
- One‑sentence explanation
- Short real‑life example

Use a bullet list.

--- DEMO 6: Tables ---
Compare Python, JavaScript, C++ on:
- Primary use cases
- Learning curve (Easy/Medium/Hard)
- Typing (Static/Dynamic)

Present as a markdown table.

--- DEMO 7: Code Blocks ---
Write a Python function sum_of_squares(numbers).  
Put code in a ```python block. After code, explain in 2 bullet points.

--- DEMO 8: Combined Markdown ---
# Meeting Agenda Generator
**Role:** Executive Assistant  
**Topic:** Q3 Marketing Review  
**Attendees:** Marketing Director, Sales Lead, Product Manager

Generate:
1. A **table** with columns: Time, Agenda Item, Owner
2. A **bullet list** of 3 preparation tasks
3. An inspiring **quote**

Use markdown.
```

---

This session plan gives you a **progressive journey** from raw text to structured markdown, with concrete, runnable examples. Attendees will leave with a new mindset: **prompts are not just questions – they are mini‑programs that orchestrate the LLM’s behaviour.**

Good luck with your session! 🚀
