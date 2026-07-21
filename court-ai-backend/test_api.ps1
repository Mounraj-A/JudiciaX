$ErrorActionPreference = 'Stop'
Write-Host "Logging in..."
$loginBody = @{ email = "advocate@judiciai.com"; password = "Admin@1234" } | ConvertTo-Json
$loginRes = Invoke-RestMethod -Uri "http://localhost:8080/api/v1/auth/login" -Method Post -Body $loginBody -ContentType "application/json"
$token = $loginRes.data.accessToken

Write-Host "Creating case..."
$createBody = @{
    caseTitle = "Test API Case"
    caseType = "CIVIL"
    priority = "LOW"
    petitionerName = "John Doe"
    respondentName = "Jane Doe"
} | ConvertTo-Json
$createRes = Invoke-RestMethod -Uri "http://localhost:8080/api/v1/advocate/cases" -Method Post -Body $createBody -ContentType "application/json" -Headers @{Authorization="Bearer $token"}
Write-Host "Created case with UUID: " $createRes.data.uuid

Write-Host "Fetching cases..."
$casesRes = Invoke-RestMethod -Uri "http://localhost:8080/api/v1/advocate/cases" -Method Get -Headers @{Authorization="Bearer $token"}
$casesRes.data.content | ConvertTo-Json -Depth 3
