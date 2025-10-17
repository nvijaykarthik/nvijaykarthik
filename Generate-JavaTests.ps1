<#
.SYNOPSIS
  Generate unit tests for Java (Spring Boot) and Angular files using an LLM via HTTP API.

.DESCRIPTION
  Scans source directory for .java and .ts/.js files, builds prompts, calls LLM, writes test files.
  Java tests are placed under src/test/java preserving package structure and include SpringBoot/H2 skeleton.
  Angular tests create .spec.ts files using TestBed and HttpClientTestingModule.

.PARAMETER SourceDir
  Root of the project to scan (default: current directory).

.PARAMETER OutDir
  Root for writing generated tests (default: SourceDir).

.PARAMETER ApiUrl
  Full endpoint URL for your LLM provider.

.PARAMETER ApiKey
  API key (put it on the command line or set environment variable LLM_API_KEY).

.PARAMETER Model
  Optional model name to pass to LLM (provider dependent).

.PARAMETER Languages
  "java", "angular"

.PARAMETER DryRun
  If set, will log actions but not write files.

.EXAMPLE
  .\Generate-UnitTests.ps1 -SourceDir . -ApiUrl "https://api.example.com/v1/generate" -ApiKey "sk-..." -Languages java

#>

param(
    [string]$SourceDir = (Get-Location).Path,
    [string]$OutDir = $null,
    [Parameter(Mandatory=$true)][string]$ApiUrl,
    [string]$ApiKey = $env:LLM_API_KEY,
    [string]$Model = "gpt-4o-mini",          # optional: adjust for your LLM
    [ValidateSet("java","angular","all")]
    [string]$Languages = "java",
    [switch]$DryRun,
    [int]$MaxTokens = 2000,
    [int]$ChunkSize = 15000   # if file bigger than this, chunk or summarize
)

if (-not $ApiKey) {
    Write-Error "API key not provided. Set -ApiKey or environment variable LLM_API_KEY."
    exit 1
}

if (-not $OutDir) { $OutDir = $SourceDir }

Write-Host "SourceDir: $SourceDir"
Write-Host "OutDir: $OutDir"
Write-Host "API URL: $ApiUrl"
Write-Host "Languages: $Languages"
Write-Host "DryRun: $($DryRun.IsPresent)"

function Get-PomDependencies {
    param([string]$pomPath)
    if (-not (Test-Path $pomPath)) { return $null }
    try {
        [xml]$pom = Get-Content $pomPath -Raw
        $deps = @()
        foreach ($d in $pom.project.dependencies.dependency) {
            $deps += [PSCustomObject]@{
                groupId = $d.groupId
                artifactId = $d.artifactId
                version = $d.version
            }
        }
        return $deps
    } catch {
        return $null
    }
}

function Build-JavaPrompt {
    param(
        [string]$filePath,
        [string]$fileContent,
        $pomDeps
    )
    $relPath = Resolve-Path $filePath -Relative -ErrorAction SilentlyContinue
    $fileSnippet = $fileContent
    if ($fileContent.Length -gt $ChunkSize) {
        # if file too big, include header, methods signatures, and class fields only
        $first = $fileContent.Substring(0, [math]::Min(8000, $fileContent.Length))
        $fileSnippet = $first + "`n/* TRUNCATED - file too large, include only top portion. */"
    }
    $depsText = if ($pomDeps) {
        $pomDeps | ForEach-Object { "$($_.groupId):$($_.artifactId):$($_.version)" } -join "; "
    } else { "No pom.xml found or unable to parse dependencies" }

    $prompt = @"
You are an expert Java developer and test writer. Generate a JUnit 5 test class for the following Java source file. The application uses Spring Boot and H2 in-memory DB for integration tests. The generated test should:

- Place tests in a package matching the source class's package.
- Donot Use Spring Boot Test annotations when necessary (e.g., @SpringBootTest, @DataJpaTest, @WebMvcTest) 
- Use only mockito and mock data for test cases.
- Try to generate the testcases that covers 85% of code.
- Generate mock data as well, based on the if conditions in the code.
- Create tests for public methods, focusing on typical edge cases, happy path, and at least one negative case.
- Include imports, @BeforeEach setup, and necessary mocking wiring.
- Use AssertJ for assertions (or JUnit assertions if necessary) or use Spring assertions
- Ensure the test class filename ends with "Test" and the test methods are annotated with @Test.

Project dependency summary (from pom.xml):
$depsText

Source file path: $relPath

Source file contents:
----
$fileSnippet
----

Return only the generated Java test class code (no analysis). Do not include extra wrappers or comments outside the Java file.
"@
    return $prompt
}

function Build-AngularPrompt {
    param(
        [string]$filePath,
        [string]$fileContent,
        [string]$angularModuleInfo
    )
    $relPath = Resolve-Path $filePath -Relative -ErrorAction SilentlyContinue
    $fileSnippet = $fileContent
    if ($fileContent.Length -gt $ChunkSize) {
        $fileSnippet = $fileContent.Substring(0,$ChunkSize) + "`n/* TRUNCATED */"
    }

    $prompt = @"
You are an expert Angular developer and test writer. Generate a Jasmine/Karma spec file for the following Angular TypeScript source file.

- If the file is a component, create a TestBed-based spec using ComponentFixture, including async compilation, detection of changes, and basic DOM assertions.
- If the file is a service, create a TestBed spec and use HttpClientTestingModule if the service uses HttpClient.
- Mock external services or provide simple spies.
- Provide imports, TestBed.configureTestingModule, beforeEach setup, and at least 3 unit tests (happy path, input handling, error path).
- Place the spec file name next to the source file with suffix `.spec.ts`.

Project module info:
$angularModuleInfo

Source file path: $relPath

Source file contents:
----
$fileSnippet
----

Return only the content of the .spec.ts file. Do not wrap in extra commentary.
"@
    return $prompt
}

function Call-LLM {
    param(
        [string]$Prompt,
        [string]$contentType = "application/json"
    )

    $body = @{
        model = $Model
        prompt = $Prompt
        max_tokens = $MaxTokens
        temperature = 0.0
    } | ConvertTo-Json -Compress

    try {
        # NOTE: adapt headers and body to your provider
        $headers = @{
            "Authorization" = "Bearer $ApiKey"
            "Content-Type"  = $contentType
        }

        $resp = Invoke-RestMethod -Uri $ApiUrl -Method Post -Headers $headers -Body $body -TimeoutSec 120
        # Common provider: response.choices[0].text
        if ($null -ne $resp.choices -and $resp.choices.Count -gt 0) {
            return $resp.choices[0].text
        }
        # Some providers return "result" or "generated_text"
        if ($resp | Get-Member -Name result -ErrorAction SilentlyContinue) {
            return $resp.result
        }
        if ($resp | Get-Member -Name generated_text -ErrorAction SilentlyContinue) {
            return $resp.generated_text
        }
        # Fallback: entire response as string
        return ($resp | ConvertTo-Json -Compress)
    } catch {
        Write-Warning "LLM call failed: $($_.Exception.Message)"
        return $null
    }
}

# find project files
$javaFiles = @()
$tsFiles = @()
if ($Languages -in @("java")) {
    $javaFiles = Get-ChildItem -Path $SourceDir -Recurse -Include *.java -File -ErrorAction SilentlyContinue
}
if ($Languages -in @("angular")) {
    $tsFiles = Get-ChildItem -Path $SourceDir -Recurse -Include *.ts,*.js -File -ErrorAction SilentlyContinue
}

# try find pom.xml
$pom = Get-ChildItem -Path $SourceDir -Recurse -Filter pom.xml -File -ErrorAction SilentlyContinue | Select-Object -First 1
$pomDeps = $null
if ($pom) {
    $pomDeps = Get-PomDependencies -pomPath $pom.FullName
    Write-Host "Found pom.xml at $($pom.FullName)"
} else {
    Write-Host "No pom.xml located under $SourceDir"
}

# simple angular module info (look for angular.json or package.json)
#$angularInfo = ""
#$ngJson = Get-ChildItem -Path $SourceDir -Recurse -Filter angular.json -File -ErrorAction SilentlyContinue | Select-Object -First 1
#$pkgJson = Get-ChildItem -Path $SourceDir -Recurse -Filter package.json -File -ErrorAction SilentlyContinue | Select-Object -First 1
#if ($ngJson) { $angularInfo += "Found angular.json at $($ngJson.FullName)`n" }
#if ($pkgJson) { $angularInfo += "Found package.json at $($pkgJson.FullName)`n" }

# process Java files
foreach ($f in $javaFiles) {
    Write-Host "Processing Java: $($f.FullName)"
    $content = Get-Content -Raw -Path $f.FullName -ErrorAction SilentlyContinue
    if (-not $content) { Write-Warning "Empty or unreadable file: $($f.FullName)"; continue }
    $prompt = Build-JavaPrompt -filePath $f.FullName -fileContent $content -pomDeps $pomDeps
    Write-Host "Calling LLM for Java test generation..."
    $testCode = Call-LLM -Prompt $prompt
    if (-not $testCode) { Write-Warning "No test code returned for $($f.FullName)"; continue }

    # Determine package and test path: read package line
    $pkgLine = ($content -split "`n" | Where-Object { $_ -match '^\s*package\s+' } | Select-Object -First 1)
    if ($pkgLine) {
        $pkg = $pkgLine -replace '^\s*package\s+','' -replace ';.*$','' -replace '\s+',''
        $pkgPath = $pkg -replace '\.','\'
    } else {
        $pkgPath = ""
    }

    # Determine class name
    $classLine = ($content -split "`n" | Where-Object { $_ -match 'class\s+\w+' } | Select-Object -First 1)
    if ($classLine -match 'class\s+([A-Za-z0-9_]+)') { $className = $matches[1] } else { $className = [IO.Path]::GetFileNameWithoutExtension($f.Name) }

    $testFileName = "$className" + "Test.java"
    $testFilePath = Join-Path -Path $OutDir -ChildPath ("src\test\java\" + $pkgPath)
    if (-not (Test-Path $testFilePath)) {
        if (-not $DryRun) { New-Item -ItemType Directory -Path $testFilePath -Force | Out-Null }
        else { Write-Host "DRYRUN: would create directory $testFilePath" }
    }
    $fullTestFile = Join-Path $testFilePath $testFileName
    if ($DryRun) {
        Write-Host "DRYRUN: would write Java test to $fullTestFile"
    } else {
        Set-Content -Path $fullTestFile -Value $testCode -Encoding UTF8
        Write-Host "Wrote Java test: $fullTestFile"
    }
}

# process Angular/TS files
foreach ($f in $tsFiles) {
    # skip spec files already
    if ($f.Name -like "*.spec.*") { continue }
    # skip .d.ts
    if ($f.Extension -eq ".d.ts") { continue }

    # Only process likely Angular files (component, service, module) by scanning for @Component, @Injectable
    $content = Get-Content -Raw -Path $f.FullName -ErrorAction SilentlyContinue
    if (-not $content) { continue }
    if ($Languages -eq "angular" -or $Languages -eq "all") {
        $isAngular = ($content -match "@Component" -or $content -match "@Injectable" -or $content -match "NgModule" -or $content -match "HttpClient")
        if (-not $isAngular) { continue }
        Write-Host "Processing Angular file: $($f.FullName)"
        $prompt = Build-AngularPrompt -filePath $f.FullName -fileContent $content -angularModuleInfo $angularInfo
        Write-Host "Calling LLM for Angular test generation..."
        $specCode = Call-LLM -Prompt $prompt
        if (-not $specCode) { Write-Warning "No spec code returned for $($f.FullName)"; continue }

        $specFileName = [IO.Path]::GetFileNameWithoutExtension($f.Name) + ".spec.ts"
        $specFullPath = Join-Path -Path $f.DirectoryName -ChildPath $specFileName
        if ($DryRun) {
            Write-Host "DRYRUN: would write Angular spec to $specFullPath"
        } else {
            Set-Content -Path $specFullPath -Value $specCode -Encoding UTF8
            Write-Host "Wrote Angular spec: $specFullPath"
        }
    }
}

Write-Host "Done. Generated tests for: Java($($javaFiles.Count)), Angular($($tsFiles.Count))"
