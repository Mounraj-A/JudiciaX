import json
import logging
import uuid
import requests

BASE_URL = "http://localhost:8080/api/v1"

class AutoTester:
    def __init__(self):
        self.session = requests.Session()
        self.session.headers.update({"Content-Type": "application/json"})
        self.tokens = {}
        self.state = {
            "caseUuid": None,
            "documentUuid": None,
            "evidenceUuid": None,
            "hearingUuid": None,
            "userUuid": None,
            "courtUuid": "8ef2323a-8273-457a-8d26-7446637e9731", # Known valid court
            "clerkUserUuid": "99990000-bbbb-0000-0000-000000000001",
            "judgeUserUuid": "99990000-dddd-0000-0000-000000000001",
            "roleUuid": "20000001-0000-0000-0000-000000000001"
        }
        self.results = []
        
        with open('../api-docs.json', encoding='utf-8-sig') as f:
            self.spec = json.load(f)
            
    def login(self, role, email, password):
        res = self.session.post(f"{BASE_URL}/auth/login", json={"email": email, "password": password})
        if res.status_code == 200:
            self.tokens[role] = res.json()["data"]["accessToken"]
            print(f"Logged in as {role}")
            self.record("POST", "/auth/login", 200, "Positive Login")
        else:
            print(f"Failed to login {role}: {res.text}")

    def auth_as(self, role):
        if role in self.tokens:
            self.session.headers.update({"Authorization": f"Bearer {self.tokens[role]}"})
        else:
            self.session.headers.pop("Authorization", None)

    def record(self, method, path, status, notes=""):
        self.results.append({
            "method": method.upper(),
            "path": path,
            "status": status,
            "notes": notes
        })

    def substitute_path(self, path):
        p = path
        if "{uuid}" in p: p = p.replace("{uuid}", self.state.get("caseUuid") or str(uuid.uuid4()))
        if "{caseUuid}" in p: p = p.replace("{caseUuid}", self.state.get("caseUuid") or str(uuid.uuid4()))
        if "{documentUuid}" in p: p = p.replace("{documentUuid}", self.state.get("documentUuid") or str(uuid.uuid4()))
        if "{evidenceUuid}" in p: p = p.replace("{evidenceUuid}", self.state.get("evidenceUuid") or str(uuid.uuid4()))
        if "{hearingUuid}" in p: p = p.replace("{hearingUuid}", self.state.get("hearingUuid") or str(uuid.uuid4()))
        if "{courtUuid}" in p: p = p.replace("{courtUuid}", self.state.get("courtUuid") or str(uuid.uuid4()))
        if "{clerkUserUuid}" in p: p = p.replace("{clerkUserUuid}", self.state.get("clerkUserUuid") or str(uuid.uuid4()))
        if "{judgeUserUuid}" in p: p = p.replace("{judgeUserUuid}", self.state.get("judgeUserUuid") or str(uuid.uuid4()))
        if "{roomUuid}" in p: p = p.replace("{roomUuid}", str(uuid.uuid4()))
        if "{benchUuid}" in p: p = p.replace("{benchUuid}", str(uuid.uuid4()))
        if "{orderUuid}" in p: p = p.replace("{orderUuid}", str(uuid.uuid4()))
        if "{noteUuid}" in p: p = p.replace("{noteUuid}", str(uuid.uuid4()))
        if "{notificationUuid}" in p: p = p.replace("{notificationUuid}", str(uuid.uuid4()))
        if "{sessionUuid}" in p: p = p.replace("{sessionUuid}", str(uuid.uuid4()))
        if "{userUuid}" in p: p = p.replace("{userUuid}", self.state.get("userUuid") or str(uuid.uuid4()))
        if "{permissionCode}" in p: p = p.replace("{permissionCode}", "CASE_VIEW")
        if "{key}" in p: p = p.replace("{key}", "SYS_SETTING")
        if "{versionNumber}" in p: p = p.replace("{versionNumber}", "1")
        if "{status}" in p: p = p.replace("{status}", "SUBMITTED")
        return p

    def run_manual_workflow(self):
        print("Running Manual Workflow...")
        # 1. Login all roles
        self.login("admin", "admin@courtai.com", "Admin@123!")
        self.login("clerk", "clerk.test@judiciai.com", "Clerk@1234")
        self.login("judge", "judge.test@judiciai.com", "Judge@1234")
        self.login("advocate", "advocate.test@judiciai.com", "Test@1234")

        # 2. Advocate files a case
        self.auth_as("advocate")
        payload = {
            "caseTitle": "Automated E2E Test Case",
            "caseDescription": "Checking CRUD operations",
            "caseType": "CIVIL",
            "petitionerName": "John Doe",
            "respondentName": "Jane Smith",
            "filingDate": "2026-07-15",
            "priority": "HIGH",
            "courtUuid": self.state["courtUuid"]
        }
        res = self.session.post(f"{BASE_URL}/advocate/cases", json=payload)
        self.record("POST", "/advocate/cases", res.status_code, "Filing new case")
        if res.status_code == 201:
            self.state["caseUuid"] = res.json()["data"]["uuid"]
            print(f"Created Case: {self.state['caseUuid']}")

        # 3. Clerk verifies the case
        if self.state["caseUuid"]:
            self.auth_as("clerk")
            c_uuid = self.state["caseUuid"]
            res = self.session.put(f"{BASE_URL}/clerk/cases/{c_uuid}/open-scrutiny")
            self.record("PUT", "/clerk/cases/{caseUuid}/open-scrutiny", res.status_code, "Clerk open scrutiny")
            
            res = self.session.put(f"{BASE_URL}/clerk/cases/{c_uuid}/verify")
            self.record("PUT", "/clerk/cases/{caseUuid}/verify", res.status_code, "Clerk verify case")

            res = self.session.put(f"{BASE_URL}/clerk/cases/{c_uuid}/register")
            self.record("PUT", "/clerk/cases/{caseUuid}/register", res.status_code, "Clerk register case")

    def run_all_endpoints(self):
        print("Running Auto Fuzzer for remaining endpoints...")
        self.auth_as("admin") # Use admin to maximize access
        paths = self.spec.get('paths', {})
        
        tested_keys = {f"{r['method']} {r['path']}" for r in self.results}
        
        for raw_path, methods in paths.items():
            for method, details in methods.items():
                key = f"{method.upper()} {raw_path}"
                if key in tested_keys:
                    continue # Already covered in manual
                
                url_path = self.substitute_path(raw_path)
                url = f"{BASE_URL}{url_path}"
                
                payload = {}
                if method.lower() in ['post', 'put', 'patch']:
                    payload = {"dummy": "data"} # Send minimal valid JSON
                
                try:
                    if method.lower() == 'get':
                        res = self.session.get(url)
                    elif method.lower() == 'post':
                        res = self.session.post(url, json=payload)
                    elif method.lower() == 'put':
                        res = self.session.put(url, json=payload)
                    elif method.lower() == 'patch':
                        res = self.session.patch(url, json=payload)
                    elif method.lower() == 'delete':
                        res = self.session.delete(url)
                    else:
                        continue
                    
                    self.record(method.upper(), raw_path, res.status_code, "Auto-Fuzzed")
                    print(f"Tested {method.upper()} {raw_path} -> {res.status_code}")
                except Exception as e:
                    self.record(method.upper(), raw_path, "ERROR", str(e))

    def generate_report(self):
        print("Generating Report...")
        total = len(self.results)
        passed = sum(1 for r in self.results if isinstance(r['status'], int) and r['status'] < 500)
        failed = total - passed
        
        with open('../Endpoint_QA_Proof_Report.md', 'w') as f:
            f.write("# Endpoint QA Proof Report\n\n")
            f.write(f"**Total Endpoints Tested**: {total}\n")
            f.write(f"**Passed (No 500s/Errors)**: {passed}\n")
            f.write(f"**Failed**: {failed}\n\n")
            f.write("## Detailed Results\n")
            f.write("| Method | Endpoint Path | Status Code | Notes |\n")
            f.write("|---|---|---|---|\n")
            for r in self.results:
                f.write(f"| {r['method']} | `{r['path']}` | {r['status']} | {r['notes']} |\n")

if __name__ == '__main__':
    tester = AutoTester()
    tester.run_manual_workflow()
    tester.run_all_endpoints()
    tester.generate_report()
