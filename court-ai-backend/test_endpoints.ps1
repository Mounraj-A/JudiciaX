##############################################################################
#  JudiciaX Court-AI Backend ? Comprehensive Endpoint Test Script (Fixed)
#  Server  : http://localhost:8080/api/v1
#  Run     : powershell -ExecutionPolicy Bypass -File test_endpoints.ps1
##############################################################################

$BASE_URL = "http://localhost:8080/api/v1"
$PASS     = 0
$FAIL     = 0
$ERRORS   = [System.Collections.Generic.List[string]]::new()
$script:totalTests = 0

function Print-Header([string]$title) {
    Write-Host "`n==========================================================" -ForegroundColor Cyan
    Write-Host "  $title" -ForegroundColor Cyan
    Write-Host "==========================================================" -ForegroundColor Cyan
}

# Core HTTP helper
function Invoke-Api {
    param(
        [string]    $Label,
        [string]    $Method,
        [string]    $Url,
        [hashtable] $Headers = @{},
        [string]    $Body    = $null,
        [int[]]     $Expect  = @(200)
    )

    $script:totalTests++
    $h = @{ "Content-Type" = "application/json" }
    foreach ($k in $Headers.Keys) { $h[$k] = $Headers[$k] }

    $statusCode = 0
    $rawBody    = ""
    $content    = $null

    try {
        $params = @{
            Uri             = $Url
            Method          = $Method
            Headers         = $h
            UseBasicParsing = $true
            TimeoutSec      = 15
            ErrorAction     = "Stop"
        }
        # Only attach body for non-GET/HEAD methods
        if ($null -ne $Body -and $Method -notin @("GET","HEAD","DELETE")) {
            $params["Body"] = $Body
        }

        $resp       = Invoke-WebRequest @params
        $statusCode = [int]$resp.StatusCode
        $rawBody    = $resp.Content
    }
    catch {
        $ex = $_.Exception
        # Walk inner exceptions to find a Response
        while ($null -ne $ex) {
            if ($null -ne $ex.Response) {
                $statusCode = [int]$ex.Response.StatusCode
                try {
                    $st  = $ex.Response.GetResponseStream()
                    $rdr = New-Object System.IO.StreamReader($st)
                    $rawBody = $rdr.ReadToEnd()
                } catch {}
                break
            }
            $ex = $ex.InnerException
        }
        if ($statusCode -eq 0) {
            Write-Host ("  [ERR] {0} - {1}" -f $Label, $_.Exception.Message) -ForegroundColor Red
            $script:FAIL++
            $script:ERRORS.Add("[NET] $Label")
            return $null
        }
    }

    try { $content = $rawBody | ConvertFrom-Json } catch {}

    if ($statusCode -in $Expect) {
        Write-Host ("  PASS [{0}] {1}" -f $statusCode, $Label) -ForegroundColor Green
        $script:PASS++
        return $content
    } else {
        $msg = ""; try { $msg = " - " + $content.message } catch {}
        Write-Host ("  FAIL [{0}] {1} (want:{2}){3}" -f $statusCode, $Label, ($Expect -join "/"), $msg) -ForegroundColor Red
        $script:FAIL++
        $script:ERRORS.Add("[$statusCode] $Label")
        return $null
    }
}

##############################################################################
# 0. HEALTH
##############################################################################
Print-Header "0. HEALTH CHECK"
Invoke-Api "GET /actuator/health" "GET" "$BASE_URL/actuator/health"

##############################################################################
# 1. AUTH
##############################################################################
Print-Header "1. AUTH - REGISTER / LOGIN"

# Register advocate (409 allowed if already exists)
$regBody = '{"fullName":"Test Advocate","email":"advocate.test@judiciai.com","phoneNumber":"9800000001","password":"Test@1234","confirmPassword":"Test@1234"}'
Invoke-Api "POST /auth/register (advocate)" "POST" "$BASE_URL/auth/register" -Body $regBody -Expect @(201,409)

# Ensure advocate is ACTIVE in DB
$env:PGPASSWORD = "postgres"
& "C:\Program Files\PostgreSQL\18\bin\psql.exe" -U postgres -d courtai_db `
  -f "d:\Projects\JudiciaX\court-ai-backend\fix_advocate.sql" | Out-Null
& "C:\Program Files\PostgreSQL\18\bin\psql.exe" -U postgres -d courtai_db `
  -c "UPDATE users SET account_status='ACTIVE',is_active=TRUE,is_email_verified=TRUE,is_locked=FALSE WHERE email='advocate.test@judiciai.com';" | Out-Null
Write-Host "  [DB] Advocate activated" -ForegroundColor DarkYellow

# Admin login
$adminRes = Invoke-Api "POST /auth/login (admin)" "POST" "$BASE_URL/auth/login" -Body '{"email":"admin@courtai.com","password":"Admin@123!"}'
$ADMIN_TOKEN = $null; try { $ADMIN_TOKEN = $adminRes.data.accessToken } catch {}
if ($ADMIN_TOKEN) { Write-Host "  Admin token captured OK" -ForegroundColor DarkGreen }
else              { Write-Host "  WARNING: no admin token" -ForegroundColor Yellow }

# Advocate login
$advRes = Invoke-Api "POST /auth/login (advocate)" "POST" "$BASE_URL/auth/login" -Body '{"email":"advocate.test@judiciai.com","password":"Test@1234"}'
$ADV_TOKEN = $null; try { $ADV_TOKEN = $advRes.data.accessToken } catch {}
if ($ADV_TOKEN) { Write-Host "  Advocate token captured OK" -ForegroundColor DarkGreen }

# Other auth endpoints
Invoke-Api "POST /auth/forgot-password"           "POST" "$BASE_URL/auth/forgot-password"           -Body '{"email":"admin@courtai.com"}'
Invoke-Api "POST /auth/resend-verification-email" "POST" "$BASE_URL/auth/resend-verification-email" -Body '{"email":"advocate.test@judiciai.com"}'
Invoke-Api "POST /auth/refresh (invalid->400)"    "POST" "$BASE_URL/auth/refresh"                   -Body '{"refreshToken":"bad-token-xyz"}' -Expect @(400,401,403)

##############################################################################
# 2. USER - SELF SERVICE
##############################################################################
Print-Header "2. USER - SELF-SERVICE PROFILE"
if ($ADV_TOKEN) {
    $aH = @{ Authorization = "Bearer $ADV_TOKEN" }
    Invoke-Api "GET /users/me"                  "GET"  "$BASE_URL/users/me"                  -Headers $aH
    Invoke-Api "PUT /users/me"                  "PUT"  "$BASE_URL/users/me"                  -Headers $aH -Body '{"fullName":"Test Advocate Updated","phoneNumber":"9800000001"}'
    Invoke-Api "GET /users/me/sessions"         "GET"  "$BASE_URL/users/me/sessions"         -Headers $aH
    Invoke-Api "PUT /users/me/privacy-settings" "PUT"  "$BASE_URL/users/me/privacy-settings" -Headers $aH -Body '{"receiveEmailNotifications":true,"receiveSmsNotifications":false}'
} else { Write-Host "  SKIP (no advocate token)" -ForegroundColor Yellow }

##############################################################################
# 3. ADMIN - USER MANAGEMENT
##############################################################################
Print-Header "3. ADMIN - USER MANAGEMENT"
$caseUuid = $null
if ($ADMIN_TOKEN) {
    $adH = @{ Authorization = "Bearer $ADMIN_TOKEN" }

    # List users (paginated)
    $uRes  = Invoke-Api "GET /admin/users"    "GET" "$BASE_URL/admin/users"   -Headers $adH
    $u0    = $null; try { $u0 = $uRes.data.content[0].uuid } catch {}
    if ($u0) {
        Write-Host "  First user UUID: $u0" -ForegroundColor DarkGreen
        Invoke-Api "GET /admin/users/{uuid}" "GET" "$BASE_URL/admin/users/$u0" -Headers $adH
    }

    # Approve / lock / unlock advocate
    $advUuid = "99990000-0000-0000-0000-000000000001"
    Invoke-Api "POST /admin/users/{uuid}/approve" "POST" "$BASE_URL/admin/users/$advUuid/approve" -Headers $adH
    Invoke-Api "POST /admin/users/{uuid}/lock"    "POST" "$BASE_URL/admin/users/$advUuid/lock"    -Headers $adH
    Invoke-Api "POST /admin/users/{uuid}/unlock"  "POST" "$BASE_URL/admin/users/$advUuid/unlock"  -Headers $adH

    # Admin reset password for advocate
    Invoke-Api "POST /admin/users/{uuid}/reset-password" "POST" "$BASE_URL/admin/users/$advUuid/reset-password" -Headers $adH

    # Admin role assignment (assign ROLE_ADVOCATE back)
    Invoke-Api "PUT /admin/users/{uuid}/assign-role?role=ROLE_ADVOCATE" "PUT" "$BASE_URL/admin/users/$advUuid/assign-role?role=ROLE_ADVOCATE" -Headers $adH

    # Dashboard and ping
    Invoke-Api "GET /admin/dashboard" "GET" "$BASE_URL/admin/dashboard" -Headers $adH
    Invoke-Api "GET /admin/ping"      "GET" "$BASE_URL/admin/ping"      -Headers $adH

    # Create a judge user (correct fields: username, firstName, lastName)
    $judgeBody = '{"username":"test_judge","firstName":"Test","lastName":"Judge","email":"judge.test@judiciai.com","password":"Judge@1234","phoneNumber":"9123456789","role":"ROLE_JUDGE"}'
    Invoke-Api "POST /users (create judge)" "POST" "$BASE_URL/users" -Headers $adH -Body $judgeBody -Expect @(201,409)

    # List all users
    Invoke-Api "GET /users (admin list)" "GET" "$BASE_URL/users" -Headers $adH

    # Get specific user
    Invoke-Api "GET /users/{uuid}" "GET" "$BASE_URL/users/$advUuid" -Headers $adH

} else { Write-Host "  SKIP (no admin token)" -ForegroundColor Yellow }

##############################################################################
# 4. ADVOCATE MODULE
##############################################################################
Print-Header "4. ADVOCATE MODULE"
if ($ADV_TOKEN) {
    $aH = @{ Authorization = "Bearer $ADV_TOKEN" }

    # Profile
    Invoke-Api "GET /advocate/profile"    "GET" "$BASE_URL/advocate/profile"  -Headers $aH
    Invoke-Api "GET /advocate/dashboard"  "GET" "$BASE_URL/advocate/dashboard" -Headers $aH
    Invoke-Api "PUT /advocate/profile"    "PUT" "$BASE_URL/advocate/profile"   -Headers $aH `
        -Body '{"barCouncilNumber":"MAH/MUM/2020/12345","specialization":"Criminal Law","yearsOfPractice":5,"stateBarCouncil":"Maharashtra Bar Council"}'

    # File a new case (include courtUuid so clerk court-ownership check passes)
    $caseJson = '{"caseTitle":"Test Case Property Dispute","caseDescription":"Endpoint test case","caseType":"CIVIL","petitionerName":"John Doe","respondentName":"Jane Smith","filingDate":"2026-07-12","priority":"MEDIUM","courtUuid":"8ef2323a-8273-457a-8d26-7446637e9731"}'
    $caseRes  = Invoke-Api "POST /advocate/cases (file case)" "POST" "$BASE_URL/advocate/cases" -Headers $aH -Body $caseJson -Expect @(201)
    try { $caseUuid = $caseRes.data.uuid } catch {}
    if ($caseUuid) { Write-Host "  Case UUID: $caseUuid" -ForegroundColor DarkGreen }

    # List / filter / search
    Invoke-Api "GET /advocate/cases"                     "GET" "$BASE_URL/advocate/cases"                     -Headers $aH
    Invoke-Api "GET /advocate/cases?page=0&size=5"       "GET" "$BASE_URL/advocate/cases?page=0&size=5"       -Headers $aH
    Invoke-Api "GET /advocate/cases/status/SUBMITTED"    "GET" "$BASE_URL/advocate/cases/status/SUBMITTED"    -Headers $aH
    Invoke-Api "GET /advocate/cases/search?keyword=test" "GET" "$BASE_URL/advocate/cases/search?keyword=test" -Headers $aH

    if ($caseUuid) {
        Invoke-Api "GET /advocate/cases/{uuid}"           "GET"  "$BASE_URL/advocate/cases/$caseUuid"           -Headers $aH
        Invoke-Api "PUT /advocate/cases/{uuid}"           "PUT"  "$BASE_URL/advocate/cases/$caseUuid"           -Headers $aH -Body '{"caseTitle":"Test Case Updated","caseDescription":"Updated"}'
        Invoke-Api "GET /advocate/cases/{uuid}/evidence"  "GET"  "$BASE_URL/advocate/cases/$caseUuid/evidence"  -Headers $aH
        Invoke-Api "GET /advocate/cases/{uuid}/documents" "GET"  "$BASE_URL/advocate/cases/$caseUuid/documents" -Headers $aH
        Invoke-Api "GET /advocate/cases/{uuid}/ai"        "GET"  "$BASE_URL/advocate/cases/$caseUuid/ai"        -Headers $aH -Expect @(200,404)
    }

    Invoke-Api "GET  /advocate/hearings"                 "GET"   "$BASE_URL/advocate/hearings"                  -Headers $aH
    Invoke-Api "GET  /advocate/notifications"            "GET"   "$BASE_URL/advocate/notifications"             -Headers $aH
    Invoke-Api "PATCH /advocate/notifications/read-all" "PATCH" "$BASE_URL/advocate/notifications/read-all"    -Headers $aH
} else { Write-Host "  SKIP (no advocate token)" -ForegroundColor Yellow }

##############################################################################
# 5. CLERK MODULE
##############################################################################
Print-Header "5. CLERK MODULE"
if ($ADMIN_TOKEN) {
    $adH = @{ Authorization = "Bearer $ADMIN_TOKEN" }

    # Create clerk
    $clerkBody = '{"username":"test_clerk","firstName":"Test","lastName":"Clerk","email":"clerk.test@judiciai.com","password":"Clerk@1234","phoneNumber":"9111222333","role":"ROLE_CLERK"}'
    Invoke-Api "POST /users (create clerk)" "POST" "$BASE_URL/users" -Headers $adH -Body $clerkBody -Expect @(201,409)

    # Ensure clerk is ACTIVE
    $env:PGPASSWORD = "postgres"
    & "C:\Program Files\PostgreSQL\18\bin\psql.exe" -U postgres -d courtai_db `
      -c "UPDATE users SET account_status='ACTIVE',is_active=TRUE,is_email_verified=TRUE,is_locked=FALSE WHERE email='clerk.test@judiciai.com';" | Out-Null
    Write-Host "  [DB] Clerk activated" -ForegroundColor DarkYellow

    # Login as clerk
    $clerkRes = Invoke-Api "POST /auth/login (clerk)" "POST" "$BASE_URL/auth/login" -Body '{"email":"clerk.test@judiciai.com","password":"Clerk@1234"}'
    $CLERK_TOKEN = $null; try { $CLERK_TOKEN = $clerkRes.data.accessToken } catch {}
    if ($CLERK_TOKEN) { Write-Host "  Clerk token captured OK" -ForegroundColor DarkGreen }

    if ($CLERK_TOKEN) {
        $cH = @{ Authorization = "Bearer $CLERK_TOKEN" }

        Invoke-Api "GET /clerk/profile"    "GET" "$BASE_URL/clerk/profile"    -Headers $cH
        Invoke-Api "GET /clerk/dashboard"  "GET" "$BASE_URL/clerk/dashboard"  -Headers $cH
        Invoke-Api "GET /clerk/cases"      "GET" "$BASE_URL/clerk/cases"      -Headers $cH
        Invoke-Api "GET /clerk/cases/pending"            "GET" "$BASE_URL/clerk/cases/pending"             -Headers $cH
        Invoke-Api "GET /clerk/cases/search?keyword=test" "GET" "$BASE_URL/clerk/cases/search?keyword=test" -Headers $cH

        if ($caseUuid) {
            Invoke-Api "GET  /clerk/cases/{uuid}"                  "GET"  "$BASE_URL/clerk/cases/$caseUuid"                  -Headers $cH
            Invoke-Api "GET  /clerk/cases/{uuid}/timeline"         "GET"  "$BASE_URL/clerk/cases/$caseUuid/timeline"         -Headers $cH
            Invoke-Api "GET  /clerk/cases/{uuid}/objections"       "GET"  "$BASE_URL/clerk/cases/$caseUuid/objections"       -Headers $cH
            Invoke-Api "POST /clerk/cases/{uuid}/duplicate-check"  "POST" "$BASE_URL/clerk/cases/$caseUuid/duplicate-check"  -Headers $cH

            # Advance to SUBMITTED (advocate normally submits; do it via DB for test)
            Write-Host "  [DB] Advancing case to SUBMITTED..." -ForegroundColor DarkYellow
            $env:PGPASSWORD = "postgres"
            & "C:\Program Files\PostgreSQL\18\bin\psql.exe" -U postgres -d courtai_db `
              -c "UPDATE case_files SET status='SUBMITTED' WHERE uuid='$caseUuid';" | Out-Null

            Invoke-Api "PUT /clerk/cases/{uuid}/open-scrutiny" "PUT" "$BASE_URL/clerk/cases/$caseUuid/open-scrutiny" -Headers $cH -Body '{"remarks":"Opening scrutiny - test"}'

            # Verify requires UNDER_SCRUTINY (open-scrutiny sets it; if it already is, skip DB step)
            Invoke-Api "PUT /clerk/cases/{uuid}/verify" "PUT" "$BASE_URL/clerk/cases/$caseUuid/verify" -Headers $cH -Body '{"remarks":"Jurisdiction verified"}'

            # Objections require UNDER_SCRUTINY — re-set to UNDER_SCRUTINY for objection test
            Write-Host "  [DB] Re-setting case to UNDER_SCRUTINY for objection test..." -ForegroundColor DarkYellow
            & "C:\Program Files\PostgreSQL\18\bin\psql.exe" -U postgres -d courtai_db `
              -c "UPDATE case_files SET status='UNDER_SCRUTINY' WHERE uuid='$caseUuid';" | Out-Null

            $objBody = '{"objectionType":"INCOMPLETE_INFORMATION","reason":"Missing ID proof - test"}'
            Invoke-Api "POST /clerk/cases/{uuid}/objections" "POST" "$BASE_URL/clerk/cases/$caseUuid/objections" -Headers $cH -Body $objBody -Expect @(200,201)

            # Register requires UNDER_SCRUTINY — re-set again
            Write-Host "  [DB] Re-setting case to UNDER_SCRUTINY for register test..." -ForegroundColor DarkYellow
            & "C:\Program Files\PostgreSQL\18\bin\psql.exe" -U postgres -d courtai_db `
              -c "UPDATE case_files SET status='UNDER_SCRUTINY' WHERE uuid='$caseUuid';" | Out-Null

            Invoke-Api "PUT /clerk/cases/{uuid}/register" "PUT" "$BASE_URL/clerk/cases/$caseUuid/register" -Headers $cH -Body '{"remarks":"Officially registered - test"}'

            Invoke-Api "GET /clerk/cases/{uuid}/documents" "GET" "$BASE_URL/clerk/cases/$caseUuid/documents" -Headers $cH
            Invoke-Api "GET /clerk/cases/{uuid}/evidence"  "GET" "$BASE_URL/clerk/cases/$caseUuid/evidence"  -Headers $cH
        } else { Write-Host "  (no case UUID - skipping case-specific clerk endpoints)" -ForegroundColor Yellow }
    } else { Write-Host "  SKIP clerk endpoints (no token)" -ForegroundColor Yellow }
} else { Write-Host "  SKIP (no admin token)" -ForegroundColor Yellow }

##############################################################################
# 6. RBAC - ROLES
##############################################################################
Print-Header "6. RBAC - ROLE MANAGEMENT"
if ($ADMIN_TOKEN) {
    $adH = @{ Authorization = "Bearer $ADMIN_TOKEN" }
    $rRes  = Invoke-Api "GET /rbac/roles" "GET" "$BASE_URL/rbac/roles" -Headers $adH
    $rUuid = $null; try { $rUuid = $rRes.data[0].uuid } catch {}
    if ($rUuid) {
        Write-Host "  First role UUID: $rUuid" -ForegroundColor DarkGreen
        Invoke-Api "GET /rbac/roles/{uuid}"             "GET" "$BASE_URL/rbac/roles/$rUuid"             -Headers $adH
        Invoke-Api "GET /rbac/roles/{uuid}/permissions" "GET" "$BASE_URL/rbac/roles/$rUuid/permissions" -Headers $adH
    }
} else { Write-Host "  SKIP (no admin token)" -ForegroundColor Yellow }

##############################################################################
# 7. DASHBOARD
##############################################################################
Print-Header "7. DASHBOARD"
if ($ADV_TOKEN) {
    $aH = @{ Authorization = "Bearer $ADV_TOKEN" }
    Invoke-Api "GET /dashboard" "GET" "$BASE_URL/dashboard" -Headers $aH
}

##############################################################################
# 8. LOGOUT
##############################################################################
Print-Header "8. LOGOUT"
if ($ADV_TOKEN) {
    Invoke-Api "POST /auth/logout (advocate)" "POST" "$BASE_URL/auth/logout" -Headers @{ Authorization = "Bearer $ADV_TOKEN" }
}
if ($ADMIN_TOKEN) {
    Invoke-Api "POST /auth/logout (admin)"    "POST" "$BASE_URL/auth/logout" -Headers @{ Authorization = "Bearer $ADMIN_TOKEN" }
}

##############################################################################
# SUMMARY
##############################################################################
$fc = if ($FAIL -gt 0) { "Red" } else { "Green" }
Write-Host "`n==========================================================" -ForegroundColor Cyan
Write-Host "  TEST RESULTS SUMMARY" -ForegroundColor Cyan
Write-Host "==========================================================" -ForegroundColor Cyan
Write-Host ("  Total  : {0}" -f $script:totalTests) -ForegroundColor White
Write-Host ("  PASSED : {0}" -f $PASS) -ForegroundColor Green
Write-Host ("  FAILED : {0}" -f $FAIL) -ForegroundColor $fc

if ($ERRORS.Count -gt 0) {
    Write-Host "`n  Failed Endpoints:" -ForegroundColor Red
    $ERRORS | ForEach-Object { Write-Host "    > $_" -ForegroundColor Red }
}
Write-Host "`n  Swagger  : http://localhost:8080/api/v1/swagger-ui.html" -ForegroundColor DarkCyan
Write-Host "  API Docs : http://localhost:8080/api/v1/api-docs"           -ForegroundColor DarkCyan
Write-Host ""