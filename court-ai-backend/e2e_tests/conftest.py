import pytest
from api_client import ApiClient

@pytest.fixture(scope="session")
def admin_client():
    client = ApiClient()
    res = client.post("/auth/login", json={"email": "admin@courtai.com", "password": "Admin@123!"}, expected_status=200)
    token = res.json()["data"]["accessToken"]
    return ApiClient(token)

@pytest.fixture(scope="session")
def advocate_client():
    client = ApiClient()
    # Assuming advocate exists. If not, maybe register?
    res = client.post("/auth/login", expected_status=[200, 401], json={"email": "advocate.test@judiciai.com", "password": "Test@1234"})
    if res.status_code == 401:
        # Register and approve if not found/unauthorized
        client.post("/auth/register", json={
            "fullName":"Test Advocate", "email":"advocate.test@judiciai.com",
            "phoneNumber":"9800000001", "password":"Test@1234", "confirmPassword":"Test@1234"
        })
        # Login again
        res = client.post("/auth/login", expected_status=200, json={"email": "advocate.test@judiciai.com", "password": "Test@1234"})
    
    token = res.json()["data"]["accessToken"]
    return ApiClient(token)

@pytest.fixture(scope="session")
def clerk_client():
    client = ApiClient()
    res = client.post("/auth/login", expected_status=200, json={"email": "clerk.test@judiciai.com", "password": "Clerk@1234"})
    token = res.json()["data"]["accessToken"]
    return ApiClient(token)

@pytest.fixture(scope="session")
def shared_data():
    return {}
