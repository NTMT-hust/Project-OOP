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
    # --- OpenCensus (Telemetry, required for HttpRequest) ---
    "opencensus-api-0.31.1.jar" = "https://repo1.maven.org/maven2/io/opencensus/opencensus-api/0.31.1/opencensus-api-0.31.1.jar"
    "opencensus-contrib-http-util-0.31.1.jar" = "https://repo1.maven.org/maven2/io/opencensus/opencensus-contrib-http-util/0.31.1/opencensus-contrib-http-util-0.31.1.jar"

    # --- Optional but recommended ---
    "protobuf-java-3.23.4.jar" = "https://repo1.maven.org/maven2/com/google/protobuf/protobuf-java/3.23.4/protobuf-java-3.23.4.jar"
    "slf4j-api-2.0.9.jar" = "https://repo1.maven.org/maven2/org/slf4j/slf4j-api/2.0.9/slf4j-api-2.0.9.jar"

    # --- gRPC (required by OpenCensus) ---
    "grpc-context-1.64.0.jar" = "https://repo1.maven.org/maven2/io/grpc/grpc-context/1.64.0/grpc-context-1.64.0.jar"
    "grpc-api-1.64.0.jar" = "https://repo1.maven.org/maven2/io/grpc/grpc-api/1.64.0/grpc-api-1.64.0.jar"
    "grpc-core-1.64.0.jar" = "https://repo1.maven.org/maven2/io/grpc/grpc-core/1.64.0/grpc-core-1.64.0.jar"
    # RSS Parser
    "rome-1.19.0.jar" = "https://repo1.maven.org/maven2/com/rometools/rome/1.19.0/rome-1.19.0.jar"
    "rome-utils-1.19.0.jar" = "https://repo1.maven.org/maven2/com/rometools/rome-utils/1.19.0/rome-utils-1.19.0.jar"
    "jdom2-2.0.6.1.jar" = "https://repo1.maven.org/maven2/org/jdom/jdom2/2.0.6.1/jdom2-2.0.6.1.jar"
    
    # Google Custom Search (optional)
    "google-api-services-customsearch-v1-rev20230920-2.0.0.jar" = "https://repo1.maven.org/maven2/com/google/apis/google-api-services-customsearch/v1-rev20230920-2.0.0/google-api-services-customsearch-v1-rev20230920-2.0.0.jar"

    # JSON Parser
    "https://repo1.maven.org/maven2/org/json/json/20230227/json-20230227.jar" = "$libDir\json-20230227.jar"

    # OkHttp for HTTP requests
    "https://repo1.maven.org/maven2/com/squareup/okhttp3/okhttp/4.12.0/okhttp-4.12.0.jar" = "$libDir\okhttp-4.12.0.jar"

    # Okio (dependency of OkHttp)
    "https://repo1.maven.org/maven2/com/squareup/okio/okio-jvm/3.9.0/okio-jvm-3.9.0.jar" = "$libDir\okio-jvm-3.9.0.jar"


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
