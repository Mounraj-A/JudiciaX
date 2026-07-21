import requests
import json

url = "http://127.0.0.1:8000/legal/extract"
payload = {
    "documentUuid": "TEST-1234",
    "ocrText": "IN THE HIGH COURT OF JUDICATURE\nBEFORE HON'BLE MR. JUSTICE RAMESH KUMAR\nPetitioner: John Doe\nFIR No. 123/2023\nW.P. No. 456/2023"
}
headers = {'Content-Type': 'application/json'}

response = requests.post(url, json=payload, headers=headers)
print("Status Code:", response.status_code)
print(json.dumps(response.json(), indent=2))
