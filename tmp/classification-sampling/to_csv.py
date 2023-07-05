import numpy as np
import csv

fields = ['Group', 'Class', 'Type', 'Number']

with open("text.txt", "r") as f:
    files = f.read().splitlines()
    for i in range(len(files)):
        # Remove Flash-No-Flash from line and convert to list 
        files[i] = files[i].replace('Flash-No-Flash','').split("\\")
        files[i].pop(0)
        # Create Group/Stratum (Class_Type): {FN_Flash, FN_No Flash, -K_Flash, -K_No Flash, -N_Flash, -N_No Flash}
        files[i].insert(0,f'{files[i][0]}_{files[i][1]}') 

# Write to CSV File
with open('population.csv', 'w',newline='', encoding='utf-8') as f:
    write = csv.writer(f, quoting=csv.QUOTE_ALL)
    # writes the fields 
    write.writerow(fields)
    # writes the modified content to file
    write.writerows(files)
