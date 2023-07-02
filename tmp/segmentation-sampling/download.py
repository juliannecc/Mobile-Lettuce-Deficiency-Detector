import urllib.request
import os

with open("train_add.csv", 'r') as f: 
    files = f.read().splitlines()
    for i in range(len(files)):
        files[i] = files[i].split(',')
files.pop(0)

print(files)
for file in files: 
    if file[2] == "-K": 
        print(file[4])
        urllib.request.urlretrieve(f"{file[5]}", os.path.join("-K", f"{file[1]}{file[3]}{file[4]}"))   
    if file[2] == "-N": 
        print(file[4])
        urllib.request.urlretrieve(f"{file[5]}", os.path.join("-N", f"{file[1]}{file[3]}{file[4]}"))   
    if file[2] == "FN":
        print(file[4])
        urllib.request.urlretrieve(f"{file[5]}", os.path.join("FN", f"{file[1]}{file[3]}{file[4]}"))   
