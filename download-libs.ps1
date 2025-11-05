# Download Libraries Script
$libDir = "lib"
New-Item -ItemType Directory -Path $libDir -Force | Out-Null

Write-Host "Downloading libraries..." -ForegroundColor Yellow

$libraries = @{
"twitter4j-core-4.1.2.jar" = "https://repo1.maven.org/maven2/org/twitter4j/twitter4j-core/4.1.2/twitter4j-core-4.1.2.jar"
"gson-2.11.0.jar" = "https://repo1.maven.org/maven2/com/google/code/gson/gson/2.11.0/gson-2.11.0.jar"
"okhttp-4.12.0.jar" = "https://repo1.maven.org/maven2/com/squareup/okhttp3/okhttp/4.12.0/okhttp-4.12.0.jar"
"okio-3.6.0.jar" = "https://repo1.maven.org/maven2/com/squareup/okio/okio/3.6.0/okio-3.6.0.jar"
"slf4j-api-2.0.9.jar" = "https://repo1.maven.org/maven2/org/slf4j/slf4j-api/2.0.9/slf4j-api-2.0.9.jar"
"slf4j-simple-2.0.9.jar" = "https://repo1.maven.org/maven2/org/slf4j/slf4j-simple/2.0.9/slf4j-simple-2.0.9.jar"
"guava-33.3.1-jre.jar" = "https://repo1.maven.org/maven2/com/google/guava/guava/33.3.1-jre/guava-33.3.1-jre.jar"
"kotlin-stdlib-1.9.10.jar" = "httpsS://repo1.maven.org/maven2/org/jetbrains/kotlin/kotlin-stdlib/1.9.10/kotlin-stdlib-1.9.10.jar"
}

foreach ($lib in $libraries.GetEnumerator()) {
$outputPath = Join-Path $libDir $lib.Key

if (Test-Path $outputPath) {
Write-Host "  - Already exists: $($lib.Key)" -ForegroundColor Gray
} else {
Write-Host "  - Downloading: $($lib.Key)" -ForegroundColor Cyan
try {
Invoke-WebRequest -Uri $lib.Value -OutFile $outputPath
Write-Host "  - Downloaded: $($lib.Key)" -ForegroundColor Green
} catch {
Write-Host "  - Failed: $($lib.Key)" -ForegroundColor Red
}
}
}

Write-Host ""
Write-Host "- All libraries downloaded!" -ForegroundColor Green
Write-Host "Location: $libDir" -ForegroundColor Yellow