import requests

run_id = "29036795184"
url = f"https://api.github.com/repos/Ajay-Chandrasekar21/Enterprise-AI-Knowledge-Intelligence-Platform-/actions/runs/{run_id}/jobs"
try:
    res = requests.get(url)
    data = res.json()
    if "jobs" in data and len(data["jobs"]) > 0:
        job_id = data["jobs"][0]["id"]
        print(f"Job ID: {job_id}")
        
        # Query job logs
        log_url = f"https://api.github.com/repos/Ajay-Chandrasekar21/Enterprise-AI-Knowledge-Intelligence-Platform-/actions/jobs/{job_id}/logs"
        log_res = requests.get(log_url)
        # Note: Github API redirects logs download to a temporary zip/txt URL
        print("Log Status Code:", log_res.status_code)
        # Print last 50 lines of logs
        log_lines = log_res.text.split("\n")
        print("\n=== Last 50 lines of GitHub Actions Logs ===")
        for line in log_lines[-100:]:
            print(line)
    else:
        print("No jobs found.")
except Exception as e:
    print(f"Failed: {str(e)}")
