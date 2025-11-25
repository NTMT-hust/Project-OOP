# # Download Libraries Script
# $libDir = "lib"
# New-Item -ItemType Directory -Path $libDir -Force | Out-Null

# Write-Host "Downloading libraries..." -ForegroundColor Yellow

# $libraries = @{
#     "twitter4j-core-4.1.2.jar" = "https://repo1.maven.org/maven2/org/twitter4j/twitter4j-core/4.1.2/twitter4j-core-4.1.2.jar"
#     "gson-2.11.0.jar" = "https://repo1.maven.org/maven2/com/google/code/gson/gson/2.11.0/gson-2.11.0.jar"
#     "okhttp-4.12.0.jar" = "https://repo1.maven.org/maven2/com/squareup/okhttp3/okhttp/4.12.0/okhttp-4.12.0.jar"
#     "okio-3.6.0.jar" = "https://repo1.maven.org/maven2/com/squareup/okio/okio/3.6.0/okio-3.6.0.jar"
#     "slf4j-api-2.0.9.jar" = "https://repo1.maven.org/maven2/org/slf4j/slf4j-api/2.0.9/slf4j-api-2.0.9.jar"
#     "slf4j-simple-2.0.9.jar" = "https://repo1.maven.org/maven2/org/slf4j/slf4j-simple/2.0.9/slf4j-simple-2.0.9.jar"
#     "guava-33.3.1-jre.jar" = "https://repo1.maven.org/maven2/com/google/guava/guava/33.3.1-jre/guava-33.3.1-jre.jar"
#     "kotlin-stdlib-1.9.10.jar" = "httpsS://repo1.maven.org/maven2/org/jetbrains/kotlin/kotlin-stdlib/1.9.10/kotlin-stdlib-1.9.10.jar"
#     # twitter api libs
#     "twitter4j-core-4.0.7.jar"   = "https://repo1.maven.org/maven2/org/twitter4j/twitter4j-core/4.0.7/twitter4j-core-4.0.7.jar"
#     "twitter4j-async-4.0.7.jar"  = "https://repo1.maven.org/maven2/org/twitter4j/twitter4j-async/4.0.7/twitter4j-async-4.0.7.jar"
#     "twitter4j-stream-4.0.7.jar" = "https://repo1.maven.org/maven2/org/twitter4j/twitter4j-stream/4.0.7/twitter4j-stream-4.0.7.jar"
#     "slf4j-api-2.0.7.jar"        = "https://repo1.maven.org/maven2/org/slf4j/slf4j-api/2.0.7/slf4j-api-2.0.7.jar"
#     "slf4j-simple-2.0.7.jar"     = "https://repo1.maven.org/maven2/org/slf4j/slf4j-simple/2.0.7/slf4j-simple-2.0.7.jar"
#     # --- SELENIUM (Browser Automation) ---
#     "selenium-java-4.21.0.jar"            = "$MavenCentral/org/seleniumhq/selenium/selenium-java/4.21.0/selenium-java-4.21.0.jar"
#     "selenium-api-4.21.0.jar"             = "$MavenCentral/org/seleniumhq/selenium/selenium-api/4.21.0/selenium-api-4.21.0.jar"
#     "selenium-remote-driver-4.21.0.jar"   = "$MavenCentral/org/seleniumhq/selenium/selenium-remote-driver/4.21.0/selenium-remote-driver-4.21.0.jar"
#     "selenium-chrome-driver-4.21.0.jar"   = "$MavenCentral/org/seleniumhq/selenium/selenium-chrome-driver/4.21.0/selenium-chrome-driver-4.21.0.jar"
#     "selenium-chromium-driver-4.21.0.jar" = "$MavenCentral/org/seleniumhq/selenium/selenium-chromium-driver/4.21.0/selenium-chromium-driver-4.21.0.jar"
#     "selenium-support-4.21.0.jar"         = "$MavenCentral/org/seleniumhq/selenium/selenium-support/4.21.0/selenium-support-4.21.0.jar"
#     "selenium-http-4.21.0.jar"            = "$MavenCentral/org/seleniumhq/selenium/selenium-http/4.21.0/selenium-http-4.21.0.jar"
#     "selenium-json-4.21.0.jar"            = "$MavenCentral/org/seleniumhq/selenium/selenium-json/4.21.0/selenium-json-4.21.0.jar"
#     "selenium-manager-4.21.0.jar"         = "$MavenCentral/org/seleniumhq/selenium/selenium-manager/4.21.0/selenium-manager-4.21.0.jar"
#     "selenium-os-4.21.0.jar"              = "$MavenCentral/org/seleniumhq/selenium/selenium-os/4.21.0/selenium-os-4.21.0.jar"
    
#     # --- DEPENDENCIES (Guava, ByteBuddy, etc) ---
#     "guava-33.2.0-jre.jar"                = "$MavenCentral/com/google/guava/guava/33.2.0-jre/guava-33.2.0-jre.jar"
#     "failureaccess-1.0.1.jar"             = "$MavenCentral/com/google/guava/failureaccess/1.0.1/failureaccess-1.0.1.jar" 
#     "auto-service-annotations-1.1.1.jar"  = "$MavenCentral/com/google/auto/service/auto-service-annotations/1.1.1/auto-service-annotations-1.1.1.jar"
#     "byte-buddy-1.14.15.jar"              = "$MavenCentral/net/bytebuddy/byte-buddy/1.14.15/byte-buddy-1.14.15.jar"
#     "failsafe-3.3.2.jar"                  = "$MavenCentral/dev/failsafe/failsafe/3.3.2/failsafe-3.3.2.jar"

#     # --- WEBDRIVER MANAGER ---
#     "webdrivermanager-5.8.0.jar"          = "$MavenCentral/io/github/bonigarcia/webdrivermanager/5.8.0/webdrivermanager-5.8.0.jar"
#     "commons-io-2.16.1.jar"               = "$MavenCentral/commons-io/commons-io/2.16.1/commons-io-2.16.1.jar"
#     "commons-lang3-3.14.0.jar"            = "$MavenCentral/org/apache/commons/commons-lang3/3.14.0/commons-lang3-3.14.0.jar"
#     "httpclient5-5.2.1.jar"               = "$MavenCentral/org/apache/httpcomponents/client5/httpclient5/5.2.1/httpclient5-5.2.1.jar"
    
#     # --- JACKSON (JSON) ---
#     "jackson-databind-2.17.0.jar"         = "$MavenCentral/com/fasterxml/jackson/core/jackson-databind/2.17.0/jackson-databind-2.17.0.jar"
#     "jackson-core-2.17.0.jar"             = "$MavenCentral/com/fasterxml/jackson/core/jackson-core/2.17.0/jackson-core-2.17.0.jar"
#     "jackson-annotations-2.17.0.jar"      = "$MavenCentral/com/fasterxml/jackson/core/jackson-annotations/2.17.0/jackson-annotations-2.17.0.jar"

#     # --- JSOUP (HTML) ---
#     "jsoup-1.17.2.jar"                    = "$MavenCentral/org/jsoup/jsoup/1.17.2/jsoup-1.17.2.jar"
# }

# foreach ($lib in $libraries.GetEnumerator()) {
# $outputPath = Join-Path $libDir $lib.Key

# if (Test-Path $outputPath) {
# Write-Host "  - Already exists: $($lib.Key)" -ForegroundColor Gray
# } else {
# Write-Host "  - Downloading: $($lib.Key)" -ForegroundColor Cyan
# try {
# Invoke-WebRequest -Uri $lib.Value -OutFile $outputPath
# Write-Host "  - Downloaded: $($lib.Key)" -ForegroundColor Green
# } catch {
# Write-Host "  - Failed: $($lib.Key)" -ForegroundColor Red
# }
# }
# }

# Write-Host ""
# Write-Host "- All libraries downloaded!" -ForegroundColor Green
# Write-Host "Location: $libDir" -ForegroundColor Yellow

<#
.SYNOPSIS
    Downloads dependencies for Facebook Selenium Scraper.
    FIXES: Enforces TLS 1.2 and adds User-Agent to bypass 403/Connection errors.
#>

# --- CRITICAL FIX: Force PowerShell to use TLS 1.2 ---
[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12

$LibDir = "lib"
$MavenCentral = "https://repo1.maven.org/maven2"

# 1. Create lib directory if it doesn't exist
if (!(Test-Path -Path $LibDir)) {
    New-Item -ItemType Directory -Path $LibDir | Out-Null
    Write-Host "Created '$LibDir' folder." -ForegroundColor Green
}

# 2. List of JARs
$Jars = @{
    # --- SELENIUM (Browser Automation) ---
    "selenium-java-4.21.0.jar"            = "$MavenCentral/org/seleniumhq/selenium/selenium-java/4.21.0/selenium-java-4.21.0.jar"
    "selenium-api-4.21.0.jar"             = "$MavenCentral/org/seleniumhq/selenium/selenium-api/4.21.0/selenium-api-4.21.0.jar"
    "selenium-remote-driver-4.21.0.jar"   = "$MavenCentral/org/seleniumhq/selenium/selenium-remote-driver/4.21.0/selenium-remote-driver-4.21.0.jar"
    "selenium-chrome-driver-4.21.0.jar"   = "$MavenCentral/org/seleniumhq/selenium/selenium-chrome-driver/4.21.0/selenium-chrome-driver-4.21.0.jar"
    "selenium-chromium-driver-4.21.0.jar" = "$MavenCentral/org/seleniumhq/selenium/selenium-chromium-driver/4.21.0/selenium-chromium-driver-4.21.0.jar"
    "selenium-support-4.21.0.jar"         = "$MavenCentral/org/seleniumhq/selenium/selenium-support/4.21.0/selenium-support-4.21.0.jar"
    "selenium-http-4.21.0.jar"            = "$MavenCentral/org/seleniumhq/selenium/selenium-http/4.21.0/selenium-http-4.21.0.jar"
    "selenium-json-4.21.0.jar"            = "$MavenCentral/org/seleniumhq/selenium/selenium-json/4.21.0/selenium-json-4.21.0.jar"
    "selenium-manager-4.21.0.jar"         = "$MavenCentral/org/seleniumhq/selenium/selenium-manager/4.21.0/selenium-manager-4.21.0.jar"
    "selenium-os-4.21.0.jar"              = "$MavenCentral/org/seleniumhq/selenium/selenium-os/4.21.0/selenium-os-4.21.0.jar"
    
    # --- DEPENDENCIES (Guava, ByteBuddy, etc) ---
    "guava-33.2.0-jre.jar"                = "$MavenCentral/com/google/guava/guava/33.2.0-jre/guava-33.2.0-jre.jar"
    "failureaccess-1.0.1.jar"             = "$MavenCentral/com/google/guava/failureaccess/1.0.1/failureaccess-1.0.1.jar" 
    "auto-service-annotations-1.1.1.jar"  = "$MavenCentral/com/google/auto/service/auto-service-annotations/1.1.1/auto-service-annotations-1.1.1.jar"
    "byte-buddy-1.14.15.jar"              = "$MavenCentral/net/bytebuddy/byte-buddy/1.14.15/byte-buddy-1.14.15.jar"
    "failsafe-3.3.2.jar"                  = "$MavenCentral/dev/failsafe/failsafe/3.3.2/failsafe-3.3.2.jar"

    # --- WEBDRIVER MANAGER ---
    "webdrivermanager-5.8.0.jar"          = "$MavenCentral/io/github/bonigarcia/webdrivermanager/5.8.0/webdrivermanager-5.8.0.jar"
    "commons-io-2.16.1.jar"               = "$MavenCentral/commons-io/commons-io/2.16.1/commons-io-2.16.1.jar"
    "commons-lang3-3.14.0.jar"            = "$MavenCentral/org/apache/commons/commons-lang3/3.14.0/commons-lang3-3.14.0.jar"
    "httpclient5-5.2.1.jar"               = "$MavenCentral/org/apache/httpcomponents/client5/httpclient5/5.2.1/httpclient5-5.2.1.jar"
    
    # --- JACKSON (JSON) ---
    "jackson-databind-2.17.0.jar"         = "$MavenCentral/com/fasterxml/jackson/core/jackson-databind/2.17.0/jackson-databind-2.17.0.jar"
    "jackson-core-2.17.0.jar"             = "$MavenCentral/com/fasterxml/jackson/core/jackson-core/2.17.0/jackson-core-2.17.0.jar"
    "jackson-annotations-2.17.0.jar"      = "$MavenCentral/com/fasterxml/jackson/core/jackson-annotations/2.17.0/jackson-annotations-2.17.0.jar"

    # --- JSOUP (HTML) ---
    "jsoup-1.17.2.jar"                    = "$MavenCentral/org/jsoup/jsoup/1.17.2/jsoup-1.17.2.jar"
}

# 3. Download Loop
Write-Host "Starting Download with TLS 1.2..." -ForegroundColor Cyan

foreach ($Name in $Jars.Keys) {
    $Url = $Jars[$Name]
    $OutFile = Join-Path $LibDir $Name

    # Check if file exists AND has size > 0 (to fix previous failed downloads)
    if ((Test-Path $OutFile) -and ((Get-Item $OutFile).Length -gt 0)) {
        Write-Host "Skipping: $Name (Already exists)" -ForegroundColor Gray
    }
    else {
        Write-Host "Downloading: $Name ..." -NoNewline
        try {
            # UserAgent is required or Maven might block the script
            Invoke-WebRequest -Uri $Url -OutFile $OutFile -UserAgent "Mozilla/5.0 (Windows NT 10.0; Win64; x64)" -ErrorAction Stop
            Write-Host " OK" -ForegroundColor Green
        } catch {
            Write-Host " FAILED" -ForegroundColor Red
            Write-Host "  Error: $($_.Exception.Message)" -ForegroundColor Yellow
        }
    }
}

Write-Host "`nDone." -ForegroundColor Green