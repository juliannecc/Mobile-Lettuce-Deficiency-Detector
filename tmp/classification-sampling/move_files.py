import os
import shutil

csv_file = "test"

# Open CSV File
with open (f"{csv_file}.csv", 'r') as f:
    file_arr = f.read().splitlines()
    # remove header
    file_arr.pop(0)

FN_count = 0
N_count = 0
K_count = 0

count = 0

for i in file_arr:
    # Setup
    i_arr = i.split(',')
    class_name = i_arr[1]
    type_name = i_arr[2]
    f_name = i_arr[3]

    # File Count
    if class_name == "FN": 
        FN_count += 1
        count = FN_count
    if class_name == "-N": 
        N_count += 1
        count = N_count
    if class_name == "-K": 
        K_count += 1
        count = K_count

    # Finds the file to be copied
    source = os.path.join('Flash-No-Flash', class_name, type_name, f_name)
    target = os.path.join(csv_file, class_name)
    shutil.copy(source, target)
    og = os.path.join(target, f_name)
    renamed = os.path.join(csv_file, class_name, f"{str(count)}.jpg")
    os.rename(og,renamed)
    print(renamed)
