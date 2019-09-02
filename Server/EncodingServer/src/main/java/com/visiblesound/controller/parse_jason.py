import json

# read file
with open('/upload/results/out09.json', 'r') as f:
    data=f.read()

# parse file
obj = json.loads(data)

# show values
print(obj['Transcript'].encode('utf-8'))
