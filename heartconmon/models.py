
from pickle import dump, load

import numpy as np
import keras

# Since the network we are modeling is based on residual networks, the standard
# keras Sequential model will not work since it doesn't natively support the
# shortcut element of residual networks.
from keras import layers, models

# Activations and normalizations
from keras.layers import BatchNormalization, Dropout, ReLU, Activation, Softmax

# Actual NN layers
from keras.layers import Conv1D, MaxPooling1D, Dense, Input, InputLayer, Flatten

_models = {'default' : 'medium.h5', 'tiny' : 'tiny.h5', 'medium' : 'medium.h5', 'large' : 'large.h5'}

def _create_tiny_network(num_classes=14, dropout=0.25, filter_inc=6, starting_filters=32, kern_size=16):
    return _create_residual_network(3, num_classes=num_classes, dropout=dropout, filter_inc=filter_inc, starting_filters=starting_filters, kern_size=kern_size)

def _create_medium_network(num_classes=14, dropout=0.25, filter_inc=4, starting_filters=64, kern_size=16):
    return _create_residual_network(8, num_classes=num_classes, dropout=dropout, filter_inc=filter_inc, starting_filters=starting_filters, kern_size=kern_size)

def _create_large_network(num_classes=14, dropout=0.25, filter_inc=4, starting_filters=64, kern_size=16):
    return _create_residual_network(15, num_classes=num_classes, dropout=dropout, filter_inc=filter_inc, starting_filters=starting_filters, kern_size=kern_size)

def _create_residual_network(resi_layer,num_classes=14, dropout=0.25, filter_inc=4, starting_filters=64, kern_size=16):
    # Based on the network described in this paper, with fewer residual layers: https://arxiv.org/pdf/1707.01836.pdf
    # Our input, 200 samples of 1 time channel
    input = Input(shape=(200, 1,))
    
    # Initial convolutional layer with the standard normalization and activation
    # Primarily this will likely identify high-level features
    prev_layer = Conv1D(starting_filters, kern_size, padding='same')(input)
    prev_layer = BatchNormalization()(prev_layer)
    prev_layer = ReLU()(prev_layer)

    # The second convolutional layer is the setup for repeated residual layers
    shortcut = prev_layer
    prev_layer = Conv1D(starting_filters, kern_size, padding='same')(prev_layer)
    prev_layer = BatchNormalization()(prev_layer)
    prev_layer = ReLU()(prev_layer)
    prev_layer = Dropout(dropout)(prev_layer)
    prev_layer = Conv1D(starting_filters, kern_size, padding='same')(prev_layer)
    
    shortcut = MaxPooling1D(pool_size=1)(shortcut)
    prev_layer = layers.add([prev_layer, shortcut])
   
    # resi_layer repeated residual layers
    factor = 0
    for x in range(1,resi_layer+1):
        # shortcut referece necessary for the residual network model
        shortcut = prev_layer

        prev_layer = BatchNormalization()(prev_layer)
        prev_layer = ReLU()(prev_layer)
        prev_layer = Dropout(dropout)(prev_layer)
        prev_layer = Conv1D(starting_filters*(2**factor), kern_size, padding='same')(prev_layer)
        
        prev_layer = BatchNormalization()(prev_layer)
        prev_layer = ReLU()(prev_layer)
        prev_layer = Dropout(dropout)(prev_layer)
        prev_layer = Conv1D(starting_filters*(2**factor), kern_size, padding='same')(prev_layer)

        #shortcut = Conv1D(starting_filters*(2**factor), kern_size, padding='same')(shortcut)
        shortcut = Conv1D(starting_filters*(2**factor), 1, padding='same')(shortcut)
        shortcut = MaxPooling1D(pool_size=1)(shortcut)
        # finally, combine the layers to implement the residual connection
        prev_layer = layers.add([prev_layer, shortcut])
        
        if x % filter_inc == 0:
            factor += 1
            
    # final normalization of the layers before classification
    prev_layer = BatchNormalization()(prev_layer)
    prev_layer = ReLU()(prev_layer)
    # Flattening increases the size, we should experiment if it helps or not
    prev_layer = Flatten()(prev_layer)
    prev_layer = Dense(num_classes)(prev_layer)
    prev_layer = Softmax()(prev_layer)

    # specify the model
    model = keras.models.Model(inputs=[input], outputs=[prev_layer])

    # the training APIs might recompile to change the opimizations/metrics/etc
    model.compile(loss='categorical_crossentropy',
          optimizer=keras.optimizers.Adam(lr=0.0001, beta_1=0.9, beta_2=0.999, epsilon=None, decay=0.0, amsgrad=False),
          metrics=['accuracy'])
    return model

def train_model(model, ecg_data, ecg_labs, lr=0.001, decay=0.0001, epochs=1, validation_split=0.20):
    model.compile(loss='categorical_crossentropy',
          optimizer=keras.optimizers.Adam(lr=lr, beta_1=0.9, beta_2=0.999, epsilon=None, decay=decay, amsgrad=False),
          metrics=['accuracy'])
    model.fit(ecg_data, ecg_labs, epochs=epochs, validation_split=validation_split)

def save_model(model, name):
    if name in _models.keys():
        model.save(_models[name])
    else:
        model.save(name)

    