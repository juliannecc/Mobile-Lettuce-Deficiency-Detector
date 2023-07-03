import os

import pandas
import tensorflow as tf
from keras.models import load_model
import cv2
import numpy as np
import matplotlib.pyplot as plt

# Folders
source_name = 'validate-images'
target_root = 'validate-segmented'

# Load Model
model = load_model('../vggseg.h5')

# Class Names
folder = os.listdir(source_name)

for class_name in folder:
    current_folder = os.path.join(source_name, class_name)
    target_folder = os.path.join(target_root, class_name)
    print("--- current folder:", current_folder, "---")

    # Goes through all the images in a [class_name]'s folder
    images = os.listdir(current_folder)
    for img in images: 
        # Initiate image to input to model
        unmasked = os.path.join(current_folder, img)
        print("current image:", unmasked)

        ### Predict ###
        # Read Image
        im = cv2.imread(unmasked)

        # Image Setup
        imRGB = cv2.cvtColor(im, cv2.COLOR_BGR2RGB)
        im_arr = np.array(imRGB)
        im_resized = cv2.resize(im_arr, (256,256), interpolation = cv2.INTER_NEAREST)
        im_in = np.expand_dims(im_resized, axis=0)

        # Get mask
        f_pred = model.predict(im_in, batch_size=1)

        # Refine Mask
        mask = np.reshape(f_pred, (128, 128, -1))
        mask = mask[:, :, 0]
        mask[mask < 0.9],  mask[mask > 0.9] = 0, 255

        # Convert, Invert, and Resize Image
        mask = np.uint8(mask)
        mask = np.invert(mask)
        mask_resized = cv2.resize(mask, (512, 512), interpolation=cv2.INTER_NEAREST)
        im_arr = cv2.resize(im_arr, (512,512), interpolation=cv2.INTER_NEAREST)

        # Mask Image
        masked = cv2.bitwise_and(im_arr, im_arr, mask=mask_resized)
        masked = cv2.cvtColor(masked, cv2.COLOR_BGR2RGB)

        # Save image to target folder
        masked_name = os.path.join(target_folder, img.replace('.jpg', '.png'))
        print("done:", masked_name)
        cv2.imwrite(masked_name, masked)
