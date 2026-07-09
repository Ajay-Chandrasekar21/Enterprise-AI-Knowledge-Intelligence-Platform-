import requests

url = "https://api.github.com/repos/Ajay-Chandrasekar21/Enterprise-AI-Knowledge-Intelligence-Platform-/commits/main"
try:
    res = requests.get(url)
    data = res.json()
    print("Latest GitHub Commit Details:")
    print(f"  SHA: {data.get('sha')}")
    print(f"  Message: {data.get('commit', {}).get('message')}")
except Exception as e:
    print(f"Failed: {str(e)}")
