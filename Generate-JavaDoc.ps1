<#
.SYNOPSIS
    Generates JavaDoc comments for Java source files using an LLM API.

.DESCRIPTION
    This script scans a given directory for Java files, sends their content
    to an LLM (like OpenAI GPT models), and writes back enhanced files
    with generated JavaDoc comments.

.PARAMETER SourceDir
    The root folder containing Java files.

.PARAMETER OutputDir
    Destination folder for output files.

.PARAMETER ApiKey
    LLM API key.

.PARAMETER ApiUrl
    Endpoint URL for the LLM (e.g., https://api.openai.com/v1/chat/completions).

.EXAMPLE
    ./Generate-JavaDoc.ps1 -SourceDir "./src/main/java" -OutputDir "./docs" -ApiKey $env:OPENAI_API_KEY -ApiUrl "https://api.openai.com/v1/chat/completions"
#>

param(
    [Parameter(Mandatory = $true)] [string]$SourceDir,
    [Parameter(Mandatory = $true)] [string]$OutputDir,
    [Parameter(Mandatory = $true)] [string]$ApiKey,
    [Parameter(Mandatory = $true)] [string]$ApiUrl,
    [string]$Model = "gpt-4o-mini",
    [int]$MaxTokens = 4096
)

# ------------------------
# Helper: Prompt Generator
# ------------------------
function Get-JavaDocPrompt {
    param (
        [string]$relPath,
        [string]$fileContent
    )

    return @"
You are an expert Java developer and API documentation writer. Enhance the following Java source file by adding **complete JavaDoc comments**.

Guidelines:
- Use standard JavaDoc format (/** ... */) above each class, method, constructor, and public field.
- Include clear and concise descriptions of purpose and behavior.
- For each method, include:
  - @param tags with detailed descriptions for all parameters.
  - @return tag describing what the method returns.
  - @throws tags for any exceptions thrown.
- For overloaded methods, differentiate documentation based on parameter purpose.
- For classes and interfaces, describe their purpose and main responsibilities.
- Preserve code structure, imports, and indentation exactly as provided.
- Do NOT modify logic or method names.
- Do NOT add comments outside of JavaDoc blocks.
- Return only the updated Java source code with added JavaDoc.

Source file path: $relPath

Source file contents:
----
$fileContent
----
"@
}

# ------------------------
# Main Script
# ------------------------

# Ensure output directory exists
if (-not (Test-Path $OutputDir)) {
    New-Item -ItemType Directory -Path $OutputDir | Out-Null
}

$javaFiles = Get-ChildItem -Path $SourceDir -Recurse -Filter "*.java"

foreach ($file in $javaFiles) {
    Write-Host "`n📘 Processing: $($file.FullName)"

    $fileContent = Get-Content -Path $file.FullName -Raw
    $relPath = $file.FullName.Substring($SourceDir.Length).TrimStart('\', '/')

    # Build the LLM prompt using helper
    $prompt = Get-JavaDocPrompt -relPath $relPath -fileContent $fileContent

    # Build JSON body
    $body = @{
        model = $Model
        max_tokens = $MaxTokens
        messages = @(
            @{ role = "system"; content = "You are an expert Java developer and API documentation writer." },
            @{ role = "user"; content = $prompt }
        )
    } | ConvertTo-Json -Depth 10

    $headers = @{
        "Authorization" = "Bearer $ApiKey"
        "Content-Type"  = "application/json"
    }

    $startTime = Get-Date
    try {
        $response = Invoke-RestMethod -Uri $ApiUrl -Headers $headers -Method Post -Body $body -ErrorAction Stop
        $generated = $response.choices[0].message.content

        # Remove markdown artifacts like ```java or ```
        $cleaned = $generated -replace '```java', '' -replace '```', ''

        # Mirror directory structure
        $outputPath = Join-Path $OutputDir $relPath
        $outputDir = Split-Path $outputPath -Parent
        if (-not (Test-Path $outputDir)) { New-Item -ItemType Directory -Path $outputDir -Force | Out-Null }

        # Save without BOM (important for Java compiler)
        [System.IO.File]::WriteAllText($outputPath, $cleaned, (New-Object System.Text.UTF8Encoding($false)))

        $endTime = Get-Date
        $duration = ($endTime - $startTime).TotalSeconds
        Write-Host "✅ JavaDoc generated for $($file.Name) in $duration seconds"
    }
    catch {
        Write-Host "❌ Error processing $($file.Name): $_"
    }
}