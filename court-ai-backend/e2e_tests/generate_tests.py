import json
import os

def generate_tests():
    with open('../api-docs.json', encoding='utf-8-sig') as f:
        spec = json.load(f)

    paths = spec.get('paths', {})
    modules = {}

    for path, methods in paths.items():
        # group by first part of path, e.g. /auth/login -> auth
        parts = path.strip('/').split('/')
        if not parts: continue
        module = parts[0]
        
        if module not in modules:
            modules[module] = []
            
        for method, details in methods.items():
            modules[module].append({
                'path': path,
                'method': method.lower(),
                'details': details
            })

    for idx, (module, endpoints) in enumerate(modules.items(), 1):
        filename = f"test_{idx:02d}_{module}.py"
        with open(filename, 'w', encoding='utf-8') as f:
            f.write("import pytest\n")
            f.write("from api_client import ApiClient\n\n")
            
            for ep in endpoints:
                # create a safe function name
                safe_path = ep['path'].replace('/', '_').replace('{', '').replace('}', '').replace('-', '_')
                func_name = f"test_{ep['method']}{safe_path}"
                
                f.write(f"def {func_name}(admin_client, advocate_client, clerk_client, shared_data):\n")
                f.write(f"    # TODO: Implement positive and negative tests for {ep['method'].upper()} {ep['path']}\n")
                f.write(f"    # {ep['details'].get('summary', '')}\n")
                f.write(f"    pass\n\n")

if __name__ == '__main__':
    generate_tests()
