import requests

run_id = "29045675561"
url = f"https://api.github.com/repos/Ajay-Chandrasekar21/Enterprise-AI-Knowledge-Intelligence-Platform-/actions/runs/{run_id}/jobs"
try:
    res = requests.get(url)
    data = res.json()
    if "jobs" in data:
        for job in data["jobs"]:
            print(f"Job: {job.get('name')}")
            print(f"  Status: {job.get('status')}")
            print(f"  Conclusion: {job.get('conclusion')}")
            print(f"  Steps:")
            for step in job.get("steps", []):
                print(f"    - Step: {step.get('name')} (Status: {step.get('status')}, Conclusion: {step.get('conclusion')})")
    else:
        print("No jobs found for run.")
except Exception as e:
    print(f"Failed: {str(e)}")
