""" Auto Encoder Example.
Project: https://github.com/aymericdamien/TensorFlow-Examples/
"""
from __future__ import division, print_function, absolute_import

import tensorflow as tf
from tensorflow.python.framework import ops
import numpy as np
import matplotlib
matplotlib.use('agg')
import matplotlib.pyplot as plt
import csv

# Import MNIST data
from tensorflow.examples.tutorials.mnist import input_data
mnist = input_data.read_data_sets('/work/deogun/alali/AE/MNIST_data/', one_hot=True)

# Training Parameters
save_dir = 'out/ae/'
learning_rate = 0.001
max_num_epoches = 100
batch_size = 32
print('train examples: ', mnist.train.num_examples)

# Network Parameters
num_hidden_1 = 512 # 1st layer num features
num_hidden_2 = 256 # 2nd layer num features (the latent dim)
num_hidden_3 = 128 # 3nd layer num features (the latent dim)
num_hidden_4 = 64 # 2nd layer num features (the latent dim)
num_input = 784 # MNIST data input (img shape: 28*28)

# tf Graph input (only pictures)
X = tf.placeholder("float", [None, num_input])

def binaryRound(x):
    """
    Rounds a tensor whose values are in [0,1] to a tensor with values in {0, 1},
    using the straight through estimator for the gradient.
    """
    print('running binaryRound')
    g = tf.get_default_graph()

    with ops.name_scope("BinaryRound") as name:
        with g.gradient_override_map({"Round": "Identity"}):
            return tf.round(x, name=name)


# Encoder Hidden layers
e_layer_1 = tf.layers.dense(X, units=num_hidden_1, activation=tf.nn.relu, name='encoder_l1')
e_layer_2 = tf.layers.dense(e_layer_1, units=num_hidden_2, activation=tf.nn.relu, name='encoder_l2')
e_layer_3 = tf.layers.dense(e_layer_2, units=num_hidden_3, activation=tf.nn.relu, name='encoder_l3')
e_layer_4 = tf.layers.dense(e_layer_3, units=num_hidden_4, activation=tf.nn.sigmoid, name='encoder_output')
rounded = binaryRound(e_layer_4)
print('rounded: ', rounded)

# Decoder Hidden layers
d_layer_1 = tf.layers.dense(rounded, units=num_hidden_3, activation=tf.nn.relu, name='decoder_l1')
d_layer_2 = tf.layers.dense(d_layer_1, units=num_hidden_2, activation=tf.nn.relu, name='decoder_l2')
d_layer_3 = tf.layers.dense(d_layer_2, units=num_hidden_2, activation=tf.nn.relu, name='decoder_l3')
d_layer_4 = tf.layers.dense(d_layer_3, units=num_input, activation=tf.nn.relu, name='decoder_output')
    

# Construct model
#encoder_op = encoder(X)
decoder_op = d_layer_4
code = e_layer_4

# Prediction
y_pred = decoder_op
# Targets (Labels) are the input data.
y_true = X

# Define loss and optimizer, minimize the squared error
print('input shape', y_true.shape)
print('output shape', y_pred.shape)
loss = tf.reduce_mean(tf.pow(y_true - y_pred, 2))
optimizer = tf.train.RMSPropOptimizer(learning_rate).minimize(loss)
#optimizer = tf.train.AdamOptimizer(learning_rate).minimize(loss)
print('optimizer: ', optimizer)

#write output file
file0 = open(save_dir + 'floating_codings.csv', 'w', newline='')
writer0 = csv.writer(file0, delimiter=',')
file1 = open(save_dir + 'binary_codings.csv', 'w', newline='')
writer1 = csv.writer(file1, delimiter=',')

# Initialize the variables (i.e. assign their default value)
init = tf.global_variables_initializer()

# Start Training
# Start a new TF session
with tf.Session() as sess:

    # Run the initializer
    sess.run(init)

    # Training
    for epoch in range(max_num_epoches):
        train_loss_list = []
        for i in range(mnist.train.num_examples // batch_size):
            # Prepare Data
            # Get the next batch of MNIST data (only images are needed, not labels)
            batch_x, _ = mnist.train.next_batch(batch_size)

            # Run optimization op (backprop) and cost op (to get loss value)
            _, l = sess.run([optimizer, loss], feed_dict={X: batch_x})
            train_loss_list.append(l)
        # Display logs per step
        print('epoch:', epoch, ' Loss:', np.mean(train_loss_list))

    # Testing
    # Encode and decode images from test set and visualize their reconstruction.
    n = 100
    for i in range(n):
        print('test run: ', i+1)
        # MNIST test set
        image, label = mnist.test.next_batch(1)
        if(i%10 == 0):
            o_fig = plt.figure()
            plt.imshow(image.reshape((28, 28)))
            o_fig.savefig(save_dir + 'original_image{}.png'.format(i))
        label = np.argmax(label) #convert one_hot to int
        print('label: ', label)
        # Encode and decode the digit image
        probabilities, binary ,g = sess.run([code, rounded, decoder_op], feed_dict={X: image})
        #print(probabilities[0], ' length: ', len(probabilities[0]))
        #print(binary[0], ' length: ', len(binary[0]))
        #print('decoder output shape: {} type {}'.format(g.shape, type(g)))
        if(i%10 == 0):
            r_fig = plt.figure()
            plt.imshow(g.reshape((28, 28)))
            r_fig.savefig(save_dir + 'reconstructed_image{}.png'.format(i))
        
        #writing to binary file
        myBinary = binary[0].astype(int)
        print('myBinary: ', myBinary)
        myBinary = ''.join(map(str, myBinary))
        print('myBinary: ', myBinary)
        print_codings = np.append(myBinary, label)
        print('print_coding of size: ', print_codings.shape)
        writer1.writerow(print_codings)

        #write to floating file
        myCoding = probabilities[0]
        print('myCoding: ', myCoding)
        print_floating = np.append(myCoding, label)
        print('print_floating of size: ', print_floating.shape)
        writer0.writerow(print_floating)

file1.close()
file0.close()                
print('DONE')
