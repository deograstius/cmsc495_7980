#
# Create generators for the data set.
# In your working directory, you should have the physionet data unzipped.
#
# Once the data set is generated, it gets serialized and stored so that
# it doesn't need to be processed again. On subsequent runs, it will
# attempt to load the serialized data.
#
import os
import numpy as np

# Derived from the Physionet ECG categories
categories = { 'Bundle branch block' : 1,
               'Cardiomyopathy' : 2,
               'Dysrhythmia' : 3,
               'Healthy Control' : 0,
               'Heart failure (NYHA 2)' : 4,
               'Heart failure (NYHA 3)' : 5,
               'Heart failure (NYHA 4)' : 6,
               'Hypertrophy' : 7,
               'Myocardial infarction' : 8,
               'Myocarditis' : 9,
               'Palpitation' : 10,
               'Stable angina' : 11,
               'Unstable angina' : 12,
               'Valvular heart disease' : 13
             }

# Just a useful list of keys we will iterate on a few times
cats = categories.keys()

# Utility function to split a data pool up into one-second chunks
# with an eighth of a second step, producing around 8 samples for
# each one second block (minus end-padding).
def make_segments(data, targ, step=800, samples=800):
    segs = []
    labs = []
    for i in range(0, len(data) - samples, step):
        channel_data = []
        for cv in data[i:i+samples]:
            channel_data.append([np.asarray(cv)])
        cd = np.asarray(channel_data)
        mn, mx = cd.min(), cd.max()
        cd = (cd - mn) / (mx - mn)
        segs.append(cd)
        labs.append(targ)
    return segs, labs

# this needs to be refactored big-time. Originally it was
# just ripped out of the research notebook.
def get_data(root_path):
    train_data = []
    train_labs = []
    # we need to generate the training data from the raw physionet data
    for c in cats:
        targ = categories[c]
        path = root_path + c
        files = os.listdir(path)
        time_pool = []
        for f in files:
            with open(path + '/' + f, 'r') as f:
                for l in f.readlines():
                    time_val, sep, sig_val = l.partition(',')
                    time_pool.append(float(sig_val))
            # remove me
            # remove me
            # remove me
            # remove me
            # remove me
            # break
        time_pool = np.asarray(time_pool)
        segs, labs = make_segments(time_pool, targ)
        for i in range(0, len(segs)):
            train_data.append(segs[i])
            train_labs.append(labs[i])

    train_data = np.asarray(train_data)
    train_labs = np.asarray(train_labs)
    print(train_data.shape, train_labs.shape)
    return train_data, train_labs
