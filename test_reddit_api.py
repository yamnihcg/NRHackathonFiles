import requests 
import json 
import pandas as pd

# Set up Reddit API Access

auth = requests.auth.HTTPBasicAuth('KfGuUEqmDAE8MiGaUscuyg', 'njiLz3F8MsBTGg_RELgiza2jYVa-mQ')
login_data = {'grant_type': 'password', 'username': 'cgharpureNR', 'password': 'DataNerd2001'}
headers = {'User-Agent': 'cgharpureNR'}

res = requests.post('https://www.reddit.com/api/v1/access_token',
                    auth=auth, data=login_data, headers=headers)

TOKEN = res.json()['access_token']
headers = {**headers, **{'Authorization': f"bearer {TOKEN}"}}

# while the token is valid (~2 hours) we just add headers=headers to our requests
requests.get('https://oauth.reddit.com/api/v1/me', headers=headers)

res = requests.get("https://oauth.reddit.com/r/InternHelpForNR1/hot",
                   headers=headers)


# Construct Dictionary For Reddit Posts 

posts_json = {}
posts_json['Post Titles'] = []
post_titles = []
post_upvotes = []
for post in res.json()['data']['children']:
    post_titles.append(post['data']['title'])
    post_upvotes.append(post['data']['ups'])

# Use Pandas to convert our dictionary to .csv 

titles_df = {'Post Titles': post_titles, 'Post Upvotes': post_upvotes}
reddit_data = pd.DataFrame(data=titles_df)
reddit_data.to_csv('reddit_info.csv')
with open('posts.json', 'w') as f:
    json.dump(posts_json, f)
