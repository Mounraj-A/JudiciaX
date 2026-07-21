$ErrorActionPreference = 'Stop'
$loginBody = @{ email = "advocate@judiciai.com"; password = "Admin@1234" } | ConvertTo-Json
$loginRes = Invoke-RestMethod -Uri "http://localhost:8080/api/v1/auth/login" -Method Post -Body $loginBody -ContentType "application/json"
$token = $loginRes.data.accessToken

$casesRes = Invoke-RestMethod -Uri "http://localhost:8080/api/v1/advocate/cases" -Method Get -Headers @{Authorization="Bearer $token"}
$casesRes | ConvertTo-Json -Depth 5
