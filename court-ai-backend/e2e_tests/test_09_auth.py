import pytest
from api_client import ApiClient

def test_auth_login_positive_and_negative(admin_client):
    client = ApiClient()
    # Positive: Login Admin
    res = client.post("/auth/login", expected_status=200, json={"email": "admin@courtai.com", "password": "Admin@123!"})
    token = res.json()["data"]["accessToken"]
    assert token is not None
    # Negative: Invalid Password
    client.post("/auth/login", expected_status=401, json={"email": "admin@courtai.com", "password": "WrongPassword"})
    # Negative: Non-existent User
    client.post("/auth/login", expected_status=401, json={"email": "nonexistent@courtai.com", "password": "WrongPassword"})

def test_auth_register_positive_and_negative():
    client = ApiClient()
    # Positive: Register new Advocate
    email = "new.advocate.e2e@judiciai.com"
    res = client.post("/auth/register", expected_status=[201, 409], json={
        "fullName": "E2E Advocate",
        "email": email,
        "phoneNumber": "9999999999",
        "password": "Password@123",
        "confirmPassword": "Password@123"
    })
    # Negative: Missing fields
    client.post("/auth/register", expected_status=400, json={
        "email": "bad@email.com"
    })
    # Negative: Password mismatch
    client.post("/auth/register", expected_status=400, json={
        "fullName": "E2E Advocate",
        "email": "another@judiciai.com",
        "phoneNumber": "9999999998",
        "password": "Password@123",
        "confirmPassword": "Mismatch@123"
    })

def test_auth_refresh(admin_client):
    res = admin_client.post("/auth/login", expected_status=200, json={"email": "admin@courtai.com", "password": "Admin@123!"})
    refresh_token = res.json()["data"]["refreshToken"]
    
    # Positive
    res2 = admin_client.post("/auth/refresh", expected_status=200, json={"refreshToken": refresh_token})
    assert res2.json()["data"]["accessToken"] is not None
    
    # Negative
    admin_client.post("/auth/refresh", expected_status=401, json={"refreshToken": "invalid_refresh_token"})

def test_auth_forgot_password():
    client = ApiClient()
    # Positive
    client.post("/auth/forgot-password", expected_status=200, json={"email": "admin@courtai.com"})
    # Negative
    client.post("/auth/forgot-password", expected_status=404, json={"email": "notfound@courtai.com"})

def test_auth_reset_password():
    client = ApiClient()
    # Negative: Invalid Token
    client.post("/auth/reset-password", expected_status=400, json={"token": "invalid_token", "newPassword": "NewPassword@123"})

def test_auth_send_otp():
    client = ApiClient()
    # Negative (assuming user not logged in)
    client.post("/auth/send-otp", expected_status=[401, 403, 400], json={"phoneNumber": "9999999999"})

def test_auth_verify_otp():
    client = ApiClient()
    # Negative
    client.post("/auth/verify-otp", expected_status=[401, 403, 400], json={"phoneNumber": "9999999999", "otp": "123456"})

def test_auth_verify_email():
    client = ApiClient()
    # Negative
    client.post("/auth/verify-email", expected_status=[400, 404], json={"token": "invalid_token"})

def test_auth_resend_verification_email():
    client = ApiClient()
    # Positive/Negative
    client.post("/auth/resend-verification-email", expected_status=[200, 404, 400], json={"email": "admin@courtai.com"})

def test_auth_logout(admin_client):
    res = admin_client.post("/auth/login", expected_status=200, json={"email": "admin@courtai.com", "password": "Admin@123!"})
    token = res.json()["data"]["accessToken"]
    client = ApiClient(token)
    
    # Positive
    client.post("/auth/logout", expected_status=200, json={"refreshToken": res.json()["data"]["refreshToken"]})
    # Negative: Try to access protected route after logout (if token invalidated)
    client.get("/admin/ping", expected_status=[401, 403])
