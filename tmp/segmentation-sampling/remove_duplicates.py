from collections import Counter
import csv

fields = ['Group', 'Date', 'Class', 'Type', 'Number', 'Link']

# Source
with open ("train.csv", 'r') as tef:
    test_files = tef.read().splitlines()

# Where to remove
with open ("unstrat_unq.csv", 'r') as trf:
    train_files = trf.read().splitlines()

print(test_files)

lst = [y for x in [test_files, train_files] for y in x]
removed = [k for k, v in Counter(lst).items() if v == 1]

with open('new_population.csv', 'w',newline='', encoding='utf-8') as f:
    for i in removed:
        f.write(i)
        f.write('\n')
    