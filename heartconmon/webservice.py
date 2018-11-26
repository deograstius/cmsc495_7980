#!/usr/bin/python
#
# Reference: https://www.acmesystems.it/python_http
#

from http.server import BaseHTTPRequestHandler,HTTPServer
from os import curdir, sep
import cgi
import traceback
# for now, so we can simplify design we will use the threading-only
# version of the multiprocessing module.
from multiprocessing.dummy import Process
import numpy

import heartconmon

_model_ref = None
_started = False
_server = None

TARGET_FREQ = 125
MIN_TIME_SZ = 

def get_record_from_dynamo(sessionId):
    # TODO reach out to the DynamoDB instance and grab a record
    # For now, return a test record that has 30 seconds of beats
    return test_record

def get_updated_record(sessionId):
    # TODO pull record data
    record = get_record_from_dynamo(sessionId)

def get_ready_unprocessed_data(record):
    if record.active:
        # if data is being added, wait until there is at least 5 seconds
        # of data to process
        if (record.processed_count + record.freq*5) < len(record.data):
            # we have valid data to process
            return True, record.data[record.processed_count:]
        else:
            # can't make any annotations if we don't have enough data
            return True, None
    else:
        # session is archived so process whatever remains
        return False, record.data[record.processed_count:]

def resample(freq, data):
    in_time = len(data)/freq
    out_samples = in_time * TARGET_FREQ
    return numpy.interp(numpy.linspace(0, new_num_points), numpy.linspace(0, len(data)), data, left=0, right=0)

def normalize(data):
    mn = numpy.amin(data)
    mx = numpy.amax(data)
    data = (data - mn) / (mx-mn)
    return data

def convert_to_beat_stream(data):
    # normally we'd find a beat and make more isolated segments,
    # that is a TODO for now. At this point, just chunk it up.
    beats = []
    for x in range(0, len(data), NORMALIZED_BEAT_TIME)
        beats.append({'start': x, 'stop' : x+NORMALIZED_BEAT_TIME, 'data' : data[x:x+NORMALIZED_BEAT_TIME]})
    return beats

def classify_beats(beats):
    for beat in beats:
        beat['type'] = 0 # normal for now
    return beats

def update_record(record, beats)
    # we need to reconvert the times back to the original
    for beat in beats:
        beat['start'] = (beat['start'] * record.freq)/TARGET_FREQ
        beat['stop'] = (beat['stop'] * record.freq)/TARGET_FREQ
    # the end of the beats will tell us where to update the processed_count
    record.processed_count = beats[-1]['stop']
    record.processed_beats.append(beats)

def save_record(record):
    # send something to 
    pass

def handle_session(sessionId):
    # number of seconds maximum to try to process the data
    # 60 seconds * 30 = 1800 ==> 30 minutes
    remaining_time = 1800
    while remaining_time > 0:
        record = get_updated_record(sessionId)
        active, data = get_ready_unprocessed_data(record)
        if data is None:
            time.sleep(2)
            continue
        normalized_data = normalize(resample(record.source_freq, data))
        beats = convert_to_beat_stream(data)
        classified_beats = classify_beats(beats)
        update_record(record, classified_beats)
        save_record(record)
        remaining_time = update_remaining_time(remaining_time)
        if not active:
            break

def start_handler_thread(sessionId)
    # TODO convert to a thread pool and queue items for later processing
    Process(target=handle_session, args=sessionId)

class webservice_handler(BaseHTTPRequestHandler):
    def do_GET(self):
        self.send_response(200)
        self.send_header('Content-type', 'text/html')
        self.end_headers()
        self.wfile.write('<html><form action="/" method="POST"><input type="text" name="ecgdata"><input type="text" name="freq"><input type="submit" value="Submit"></form></html>'.encode('utf-8'))
        
    def do_POST(self):
        global model_ref
        try:
            # get the form data from the HTTP request
            form = cgi.FieldStorage(
                fp=self.rfile, 
                headers=self.headers,
                environ={'REQUEST_METHOD':'POST',
                         'CONTENT_TYPE':self.headers['Content-Type'],})

            freq = float(form['freq'].value)
            ecgd = [f for f in form['ecgdata'].value.split(',')]

            print('received %d points at %fHz' % (len(ecgd), freq))
            prediction = heartconmon.predict(ecgd, freq, _model_ref)
            print('Prediction: %s' % (repr(prediction)))
            
            # TODO convert prediction into a human readable form, and run
            #    expanded diagnostics to determine the locations in the data
            #    that highlight the predictions.
#             pred_code = numpy.argmax(prediction)
#             pred_text = heartconmon._xlat(pred_code)
            
            self.send_response(200)
            self.send_header('Content-type', 'text/html')
            self.end_headers()
            diag = '%s' % (repr(prediction))
            self.wfile.write(diag.encode('utf-8'))

        except Exception as e:
            traceback.print_exc()
            self.send_error(500, 'Internal Error')

def _run_service(addr, port):
    global _server
    _server = HTTPServer((addr, port), webservice_handler)
    _server.serve_forever()
            
def start(addr='0.0.0.0', port=8080, model_name='default'):
    global _model_ref
    global _started
    global _proc_ref
    
    if not _started:
        _model_ref = model_name
        _started = True
        _proc_ref = Process(target=_run_service, args=(addr, port))
        _proc_ref.start()
    return

def stop():
    global _proc_ref
    global _term_sig
    global _started
    global _server
    if _server is not None:
        _server.shutdown()
        _proc_ref.join()
        _server = None
        _started = False
        _proc_ref = None
    return
    
    
    
    
