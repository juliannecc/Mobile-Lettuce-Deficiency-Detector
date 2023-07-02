import re
import numpy as np
import csv
fields = ['Group', 'Date', 'Class', 'Type', 'Number', 'Link']

with open("text.txt", "r") as f:
    files = f.read().splitlines()
    for i in range(len(files)):
        files[i] = files[i].replace('Dataset-Deleted-Unusable','').split("\\")
        files[i].pop(0)
        files[i].insert(0,f'{files[i][1]}{files[i][2]}')
        files[i].append(f'https://raw.githubusercontent.com/queaaa/Deficient-Lettuce-Plants/main/{files[i][1]}/{files[i][2]}/{files[i][3]}/{files[i][4]}')
    # files.insert(0,fields)
print(files)

with open('unstrat_noquotes', 'w',newline='', encoding='utf-8') as f:
     
    # using csv.writer method from CSV package
    write = csv.writer(f, quoting=csv.QUOTE_NONE)
     
    write.writerow(fields)
    write.writerows(files)
    