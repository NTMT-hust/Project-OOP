# Download Libraries Script for YouTube API
$libDir = "lib"
New-Item -ItemType Directory -Path $libDir -Force | Out-Null

Write-Host "Downloading YouTube API libraries..." -ForegroundColor Yellow

# --- START: Robust Download Fix ---
try {
    [Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12
    Write-Host "  - Set security protocol to Tls12" -ForegroundColor DarkGray
} catch {
    Write-Warning "  - Could not set Tls12, downloads may fail."
}

$webClient = New-Object System.Net.WebClient
# --- END: Robust Download Fix ---

$libraries = @{
    "google-api-services-youtube-v3-rev20250422-2.0.0.jar" = "https://repo1.maven.org/maven2/com/google/apis/google-api-services-youtube/v3-rev20250422-2.0.0/google-api-services-youtube-v3-rev20250422-2.0.0.jar"
    "google-api-client-2.4.0.jar" = "https://repo1.maven.org/maven2/com/google/api-client/google-api-client/2.4.0/google-api-client-2.4.0.jar"
    "google-oauth-client-1.35.0.jar" = "https://repo1.maven.org/maven2/com/google/oauth-client/google-oauth-client/1.35.0/google-oauth-client-1.35.0.jar"
    "google-http-client-1.46.0.jar" = "https://repo1.maven.org/maven2/com/google/http-client/google-http-client/1.46.0/google-http-client-1.46.0.jar"
    "google-http-client-gson-1.46.0.jar" = "https://repo1.maven.org/maven2/com/google/http-client/google-http-client-gson/1.46.0/google-http-client-gson-1.46.0.jar"
    "jsr305-3.0.2.jar" = "https://repo1.maven.org/maven2/com/google/code/findbugs/jsr305/3.0.2/jsr305-3.0.2.jar"
}

foreach ($lib in $libraries.GetEnumerator()) {
    $outputPath = Join-Path $libDir $lib.Key

    if (Test-Path $outputPath) {
        Write-Host "  v Already exists: $($lib.Key)" -ForegroundColor Gray
    } else {
        Write-Host "  -> Downloading: $($lib.Key)" -ForegroundColor Cyan
        try {
            $webClient.DownloadFile($lib.Value, $outputPath)
            Write-Host "  v Downloaded: $($lib.Key)" -ForegroundColor Green
        } catch {
            Write-Host "  x Failed: $($lib.Key)" -ForegroundColor Red
            Write-Host "    Error: $($_.Exception.Message)" -ForegroundColor DarkGray
        }
    }
}

$webClient.Dispose()

Write-Host "`nAll libraries downloaded!" -ForegroundColor Green
Write-Host "Location: $libDir" -ForegroundColor Yellow
