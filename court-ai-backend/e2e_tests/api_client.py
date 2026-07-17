import requests
import json

BASE_URL = "http://localhost:8080/api/v1"

class ApiClient:
    def __init__(self, token=None):
        self.session = requests.Session()
        if token:
            self.session.headers.update({"Authorization": f"Bearer {token}"})
        self.session.headers.update({"Content-Type": "application/json"})

    def request(self, method, endpoint, expected_status=None, **kwargs):
        url = f"{BASE_URL}{endpoint}"
        if 'json' in kwargs:
            kwargs['data'] = json.dumps(kwargs.pop('json'))
        
        response = self.session.request(method, url, **kwargs)
        
        if expected_status is not None:
            if isinstance(expected_status, int):
                expected_status = [expected_status]
            assert response.status_code in expected_status, \
                f"Expected {expected_status} but got {response.status_code} for {method} {url}: {response.text}"
                
        return response

    def get(self, endpoint, expected_status=200, **kwargs):
        return self.request("GET", endpoint, expected_status, **kwargs)

    def post(self, endpoint, expected_status=200, **kwargs):
        return self.request("POST", endpoint, expected_status, **kwargs)

    def put(self, endpoint, expected_status=200, **kwargs):
        return self.request("PUT", endpoint, expected_status, **kwargs)
        
    def patch(self, endpoint, expected_status=200, **kwargs):
        return self.request("PATCH", endpoint, expected_status, **kwargs)

    def delete(self, endpoint, expected_status=200, **kwargs):
        return self.request("DELETE", endpoint, expected_status, **kwargs)
