import tensorflow as tf

ph_d = tf.config.list_physical_devices('GPU')
for dev in ph_d:
    print(dev)