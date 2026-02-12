# Prompt Engineering Session 1 – PowerPoint Slide Deck  
## From Basic Text to Markdown Magic

This document provides a **complete slide‑by‑slide outline** for your 75‑90 minute session.  
Each slide includes:

- **Slide title** and **layout suggestion**
- **Content** (text, bullet points, code blocks)
- **Visual / Diagram description** (what to draw or place on the slide)
- **Presenter notes** (what to say, engagement tips)

Use this as a blueprint. Copy the content into PowerPoint, Google Slides, or Keynote, and add the suggested diagrams and icons.

---

## SLIDE 1: Title Slide
**Layout:** Centered Title + Subtitle + Your Name/Date  

**Content:**  
# Prompt Engineering  
## Session 1: From Basic Text to Markdown Magic  
[Your Name]  
[Date]  

**Visual:**  
- Background: Abstract network / chat bubble pattern  
- Icon: A large chat bubble with a pencil and # symbol inside  

**Presenter Notes:**  
Welcome everyone. Today we begin our journey into prompt engineering – the art of talking to AI. By the end of this session, you’ll never write a prompt the same way again.

---

## SLIDE 2: Agenda
**Layout:** Simple numbered list or timeline graphic  

**Content:**  
1. What is Prompt Engineering? (5 min)  
2. Anatomy of a Prompt (10 min)  
3. Your Prompt Toolkit – 6 Types (5 min)  
4. Live Demos – Basic Text Prompts (20 min)  
5. Why Markdown? (5 min)  
6. Live Demos – Markdown‑Powered Prompts (25 min)  
7. Key Takeaways & Homework (5 min)  

**Visual:**  
- Horizontal timeline with 7 milestones, each labelled.  
- Icons: 💬, 🧩, 🧰, 🎬, 🔖, ⚡, 🏁  

**Presenter Notes:**  
We have a packed agenda. We’ll move from theory to practice, and you’ll see real‑time transformations in the LLM.

---

## SLIDE 3: The “Brilliant Intern” Analogy
**Layout:** Image + Text side by side  

**Content:**  
**Think of the LLM as a brilliant, eager, but very literal intern.**  
- It can do amazing things – but needs crystal‑clear instructions.  
- Vague request → vague output.  
- Structured request → professional output.  

**Visual:**  
- Illustration: A smiling robot holding a notebook, looking puzzled next to a messy note, then happy next to a clean checklist.  

**Presenter Notes:**  
This is the core mindset shift. We’re not just asking questions; we’re giving instructions to a super‑smart teammate.

---

## SLIDE 4: Anatomy of a Prompt – The Four Pillars
**Layout:** Four large pillars / columns with icons  

**Content:**  

| Pillar          | Icon | Purpose                          |
|-----------------|------|----------------------------------|
| **Instruction** | 🎯   | What you want the model to do    |
| **Context**     | 🌍   | Background, persona, constraints |
| **Input Data**  | 📄   | The text to work on              |
| **Output Format** | 📐 | How the answer should look       |

**Visual:**  
- Four columns, each with a bold title, icon, and short description.  
- Connecting lines show they form a complete structure.  

**Presenter Notes:**  
Every great prompt can be built from these four blocks. If you’re missing one, the model has to guess – and guesses are often wrong.

---

## SLIDE 5: Poor vs. Structured Prompt – Live Demo Preview
**Layout:** Side‑by‑side comparison  

**Content:**  

| Poor Prompt                  | Structured Prompt                          |
|------------------------------|--------------------------------------------|
| Tell me about renewable energy. | **Instruction:** Explain 3 main types. <br>**Context:** High school student. <br>**Output:** Simple language + comparison table. |

**Visual:**  
- Left side: red background, messy text.  
- Right side: green background, clean boxes for each component.  
- An arrow pointing right labelled “Add Structure”.  

**Presenter Notes:**  
Let’s see this in action. I’ll paste both into the LLM and you’ll see the difference instantly.

---

## SLIDE 6: Your Prompt Toolkit – 6 Types
**Layout:** Grid of 6 cards, each with type name, icon, and brief description  

**Content:**  

| **Zero‑Shot** 🎲 | **Few‑Shot** 📚 | **Persona** 🎭 |
|------------------|-----------------|----------------|
| Direct instruction, no examples | Give 1‑3 examples to teach pattern | Assign a role or perspective |

| **Constraint‑Based** ⚖️ | **Comparative** 🔍 | **Multi‑Step** 🧗 |
|-------------------------|--------------------|--------------------|
| Rules on length, format, keywords | Compare two or more items | Break complex task into steps |

**Visual:**  
- Six equally sized cards arranged 2x3.  
- Each card has a coloured header, icon, and short bullet.  

**Presenter Notes:**  
These six types are your vocabulary. We’ll practice each one in plain text first, then supercharge them with markdown.

---

## SLIDE 7: Demo 1 – Zero‑Shot (Plain Text)
**Layout:** Prompt on left, Output screenshot on right  

**Content:**  

**Prompt:**  
> Explain what blockchain is.  

**Output (excerpt):**  
Blockchain is a distributed ledger technology…  

**Visual:**  
- Show a clean copy‑paste of the actual LLM response.  
- Optional: add a “Simple” badge.  

**Presenter Notes:**  
Zero‑shot works fine for general knowledge. But watch how we can improve it later.

---

## SLIDE 8: Demo 2 – Few‑Shot (Plain Text)
**Layout:** Prompt + Example + Output  

**Content:**  

**Prompt:**  
Convert movie titles into emojis.  
Example: "The Lion King" → 🦁👑  
Example: "Titanic" → 🚢❄️  
Now convert "Jurassic Park" and "Inception".  

**Output:**  
Jurassic Park → 🦖🌴🚙  
Inception → 💤🌀⏰  

**Visual:**  
- Speech bubble showing the pattern.  
- Emojis highlighted.  

**Presenter Notes:**  
With just two examples, the model learns the pattern perfectly. Few‑shot is like giving a mini‑training session.

---

## SLIDE 9: Demo 3 – Persona (Plain Text)
**Layout:** Role description + Prompt + Output snippet  

**Content:**  

**Persona:** Experienced cybersecurity consultant  
**Audience:** Small business owner  
**Output:** Simple, non‑technical language  

**Output excerpt:**  
“Two‑factor authentication is like having a second lock on your door…”  

**Visual:**  
- Icon of a security shield + person.  
- Quote box with the analogy.  

**Presenter Notes:**  
The persona changes everything – tone, vocabulary, examples. Always consider *who* is speaking.

---

## SLIDE 10: Demo 4 – Constraint (Plain Text)
**Layout:** Constraint in bold + Prompt + Output  

**Content:**  

**Constraint:** Summarise in **one sentence**, start with “Overall”.  

**Prompt:** [paste long review]  
**Output:** “Overall, customers love the battery life but find the ear cushions uncomfortable.”  

**Visual:**  
- A ruler icon (measurement) next to “one sentence”.  
- The output in a neat text box.  

**Presenter Notes:**  
If you don’t set constraints, the model will write a paragraph. If you need one sentence, say so explicitly.

---

## SLIDE 11: Demo 5 – Comparative (Plain Text)
**Layout:** Comparison topic + criteria + Output format  

**Content:**  

**Prompt:**  
Compare electric cars and hydrogen fuel cell cars. Focus on refuelling time, environmental impact, infrastructure.  

**Output:**  
(Usually paragraph form or bullet list)  

**Visual:**  
- Two car icons side by side.  
- Bullet list showing the model’s output structure.  

**Presenter Notes:**  
The model does compare, but the output structure is inconsistent. We’ll fix that with markdown.

---

## SLIDE 12: Demo 6 – Multi‑Step (Plain Text)
**Layout:** Numbered steps + Output summary  

**Content:**  

**Prompt:**  
Step 1: List 3 causes of project delays.  
Step 2: For each, suggest a prevention.  
Step 3: Write a one‑paragraph summary.  

**Output:**  
Causes: vague requirements, scope creep, poor communication…  

**Visual:**  
- Staircase diagram showing steps 1→2→3.  
- Output shown as three distinct sections.  

**Presenter Notes:**  
Multi‑step prompts force the model to reason step by step. It’s like giving a recipe instead of saying “cook something”.

---

## SLIDE 13: Transition – Why Markdown?
**Layout:** Central question + 4 benefit cards  

**Content:**  

**Why should we use Markdown in our prompts?**  

| 🎯 **Visual Clarity** | ♻️ **Reusability** |
|-----------------------|---------------------|
| Both you and the LLM see the structure instantly | Save templates, swap content |

| 🖨️ **Output Mirroring** | 🧠 **Precision** |
|-------------------------|------------------|
| Model copies your formatting | Headings, tables, lists remove ambiguity |

**Visual:**  
- Four colourful cards arranged around the question.  
- Icons: eye, recycle, copy, target.  

**Presenter Notes:**  
Markdown isn’t just for output. When you put markdown *in* the prompt, the LLM understands your request much better – and returns beautifully formatted answers.

---

## SLIDE 14: Markdown Cheat Sheet (Quick Reference)
**Layout:** Grid of common markdown syntax examples  

**Content:**  

| Element       | Syntax                          |
|---------------|---------------------------------|
| Heading       | `# H1`, `## H2`                |
| Bold          | `**text**`                    |
| Italic        | `*text*`                      |
| Bullet list   | `- item`                      |
| Numbered list | `1. item`                     |
| Table         | `\| col1 \| col2 \|`          |
| Code block    | \`\`\`language               |
| Quote         | `> text`                      |

**Visual:**  
- Clean table layout.  
- Each syntax highlighted in a code style font.  

**Presenter Notes:**  
You don’t need to be a markdown expert – just these few elements will cover 90% of your structured prompts.

---

## SLIDE 15: Demo 1a – Zero‑Shot + Markdown
**Layout:** Before (plain) vs. After (markdown) side by side  

**Content:**  

| Before (Plain) | After (Markdown) |
|----------------|------------------|
| Explain what blockchain is. | `# Request`<br>Explain blockchain in simple terms.<br><br>`## Format`<br>- Use **bullet points**<br>- End with *italics* summary |

**Visual:**  
- Left: plain text, grey background.  
- Right: markdown syntax in a code block, coloured background.  
- Arrow: “Adds structure”.  

**Presenter Notes:**  
Same question, but now we’ve told the model *how* to answer. Watch the output become scannable and professional.

---

## SLIDE 16: Demo 2a – Few‑Shot + Markdown Table
**Layout:** Before/After with table focus  

**Content:**  

**Before:** Few‑shot with plain examples.  
**After:**  

```markdown
| Term | Category | Simple Definition |
|------|----------|-------------------|
| API  | Software | ...              |
| IDE  | Tools    | ...              |
|      |          |                  |
| Blockchain | Technology | ?          |
| SEO  | Marketing | ?                |
```

**Visual:**  
- Highlight the table structure.  
- Show how the model fills the empty cells.  

**Presenter Notes:**  
The table becomes a fill‑in‑the‑blanks exercise. This is incredibly powerful for data extraction and consistent formatting.

---

## SLIDE 17: Demo 3a – Persona + Markdown Headings
**Layout:** Prompt with headings / Output character voice  

**Content:**  

**Prompt:**  
```markdown
# Persona
You are Marie Curie, physicist and chemist.

# Task
Explain radioactivity to 12‑year‑olds.

# Tone
Curious, encouraging, historically aware.
```

**Output:**  
“Imagine you have a magic flashlight that can see through skin…”  

**Visual:**  
- Portrait of Marie Curie (public domain image).  
- Quote bubble with her “voice”.  

**Presenter Notes:**  
Headings organise the prompt into clear sections. The model not only answers correctly – it stays in character.

---

## SLIDE 18: Demo 4a – Constraint + Markdown Bullets
**Layout:** Constraint list in markdown / Output snippet  

**Content:**  

```markdown
# Constraint
Write a tweet‑length pitch for a productivity app.

# Must include
- App name: "FocusFlow"
- One key feature (max 6 words)
- A call to action

# Format
Single sentence, no hashtags.
```

**Output:**  
“FocusFlow helps you block distractions – try it free today.”  

**Visual:**  
- Checklist with checkmarks next to each requirement.  
- Twitter bird icon (or X logo) for “tweet‑length”.  

**Presenter Notes:**  
The bullet list acts as a checklist for both you and the model. Nothing is forgotten.

---

## SLIDE 19: Demo 5a – Comparative + Markdown Table
**Layout:** Empty table prompt / Filled table output  

**Content:**  

**Prompt (empty table):**  
```markdown
| Criteria         | React | Vue |
|------------------|-------|-----|
| Learning curve   |       |     |
| Popularity       |       |     |
| Ideal use case   |       |     |
```

**Output (filled table):**  
| Criteria         | React | Vue |
|------------------|-------|-----|
| Learning curve   | Medium | Easy |
| Popularity       | Very high | High |
| Ideal use case   | Complex SPAs | Progressive enhancement |

**Visual:**  
- Animated transition: empty cells → filled cells.  
- Green checkmark on completion.  

**Presenter Notes:**  
This is one of my favourite demos. You give the LLM a skeleton table, and it becomes a comparison expert. Perfect for decision matrices.

---

## SLIDE 20: Demo 6a – Multi‑Step + Markdown Sections
**Layout:** Step‑by‑step markdown / Output with headings  

**Content:**  

```markdown
# Step 1 – Brainstorm
List 5 names for a sustainable coffee shop.

# Step 2 – Evaluate
For each name, one **pro** and one **con**.

# Step 3 – Recommend
Pick the best name and explain why in one sentence.
```

**Output:**  
Structured with headings, bullet lists, and a final recommendation.  

**Visual:**  
- Three vertical panels labelled Step 1, 2, 3.  
- Each panel shows the corresponding part of the output.  

**Presenter Notes:**  
The model follows your section headings exactly. This is how you build complex workflows, one step at a time.

---

## SLIDE 21: Combining Types & Markdown
**Layout:** Example of a hybrid prompt  

**Content:**  

```markdown
# Persona
You are a seasoned product manager.

# Task
Compare Agile and Waterfall methodologies.

# Constraints
- 5 bullet points max
- Include one real‑world analogy

# Output Format
Present as a markdown table with columns:
| Aspect | Agile | Waterfall |
```

**Visual:**  
- Overlapping icons: persona 🎭 + comparative 🔍 + constraint ⚖️ + table 📊.  
- Show that you can mix and match.  

**Presenter Notes:**  
You’re not limited to one type. The magic happens when you combine persona, constraint, and a structured output format. This is advanced prompt engineering made simple.

---

## SLIDE 22: Key Takeaways
**Layout:** 5 large numbered circles or icons  

**Content:**  

1. 🧱 **Four Pillars** – Instruction, Context, Input, Output Format.  
2. 🧰 **Six Types** – Zero‑shot, Few‑shot, Persona, Constraint, Comparative, Multi‑step.  
3. ✍️ **Markdown is Grammar** – Use headings, tables, lists to structure prompts.  
4. 🔁 **Mirror Effect** – Model mimics your formatting.  
5. 🚀 **Combine & Conquer** – Mix types and markdown for superpowers.  

**Visual:**  
- Five cards or a numbered infographic.  
- Bright, memorable icons.  

**Presenter Notes:**  
These five ideas are your takeaway. Write them down, share them, use them tomorrow.

---

## SLIDE 23: Homework Challenge
**Layout:** Call‑to‑action with example  

**Content:**  

**Your mission, should you choose to accept it:**  

1. Pick **one** prompt type (e.g., Comparative).  
2. Write a **plain‑text** version.  
3. Rewrite it with **markdown** (headings, table, or bullet list).  
4. Run both in an LLM.  
5. Bring the before/after to Session 2!  

**Example:**  
_Before:_ “Compare iOS and Android.”  
_After:_ (markdown table with empty cells)  

**Visual:**  
- Detective badge or “Mission” stamp.  
- Side‑by‑side before/after placeholder.  

**Presenter Notes:**  
This is not optional – it’s how you lock in the learning. I can’t wait to see your transformations.

---

## SLIDE 24: Resources & Further Reading
**Layout:** List of links with QR codes (optional)  

**Content:**  

- 📘 [OpenAI Prompt Engineering Guide](https://platform.openai.com/docs/guides/prompt-engineering)  
- 📗 [Anthropic Prompt Engineering Resources](https://docs.anthropic.com/claude/docs/prompt-engineering)  
- 📙 [Markdown Cheat Sheet](https://www.markdownguide.org/cheat-sheet/)  
- 📕 [Awesome Prompt Engineering (GitHub)](https://github.com/promptslab/Awesome-Prompt-Engineering)  

**Visual:**  
- Book icons or link symbols.  
- Optional: generate QR codes for each URL to make it mobile‑friendly.  

**Presenter Notes:**  
Bookmark these. The field moves fast, and these guides are kept up to date.

---

## SLIDE 25: Q&A / Thank You
**Layout:** Centered thank you + contact info  

**Content:**  

# Thank You!  

**Questions?**  

[Your Name]  
[Your Email / LinkedIn]  
[Session 2 Date / Teaser]  

**Visual:**  
- Friendly illustration of a robot waving.  
- Chat bubbles floating around.  

**Presenter Notes:**  
Thank you for your attention and participation. Let’s open the floor for questions – or share your biggest “aha” moment from today.

---

# 🎨 Diagram Creation Guidelines

To create the diagrams mentioned above, use simple shapes and icons available in PowerPoint/Google Slides:

### 1. Four Pillars Diagram (Slide 4)
- Draw four tall rectangles side by side.
- Top: Icon (🎯, 🌍, 📄, 📐) inside a circle.
- Middle: Bold title (Instruction, Context, Input Data, Output Format).
- Bottom: Short description.
- Connect them with a horizontal bar labelled “Prompt”.

### 2. Six Prompt Types Grid (Slide 6)
- Use the “SmartArt” grid layout (2x3).
- Each cell: coloured background, icon, title, one‑line description.
- Icons: 🎲, 📚, 🎭, ⚖️, 🔍, 🧗.

### 3. Before/After Comparison (Multiple Slides)
- Two columns: left (reddish) “Before: Plain Text”, right (greenish) “After: Markdown”.
- Use a large arrow pointing right.
- Show actual prompt text in monospace font.

### 4. Table Fill Animation (Slide 19)
- Two slides or an animation: first shows empty table, second shows table with filled cells.
- Highlight the added content with a green fade-in.

### 5. Combined Types Overlap (Slide 21)
- Use overlapping circles (Venn‑like) each with an icon.
- The intersection contains a star or a checkmark.

---

This complete slide deck covers every segment of your session with visual support and clear presenter notes. Simply copy the content, insert the suggested diagrams/icons, and you are ready to deliver an engaging, high‑impact training.  

**Good luck!** 🎤📊🚀
