import requests

url = "https://api.github.com/repos/Ajay-Chandrasekar21/Enterprise-AI-Knowledge-Intelligence-Platform-/actions/runs"
try:
    res = requests.get(url)
    data = res.json()
    if "workflow_runs" in data and len(data["workflow_runs"]) > 0:
        latest = data["workflow_runs"][0]
        print(f"Latest Workflow Run Details:")
        print(f"  Name: {latest.get('name')}")
        print(f"  Event: {latest.get('event')}")
        print(f"  Status: {latest.get('status')}")
        print(f"  Conclusion: {latest.get('conclusion')}")
        print(f"  HTML URL: {latest.get('html_url')}")
    else:
        print("No workflow runs found.")
except Exception as e:
    print(f"Failed to query GitHub actions: {str(e)}")
