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
    
    
    
    
