import werkzeug
import deepspeech
import os
import wave
import numpy as np
from flask import Response
from flask_restplus import Resource
from Restplus import api
from Endpoints.Utils import JsonTransformer

ns_endpoint = api.namespace('api/storage', description='description')

model_file_path = os.path.join('./deepspeech_model.pbmm')
scorer_file_path = os.path.join('./deepspeech_scorer.scorer')
model = deepspeech.Model(model_file_path)
model.enableExternalScorer(scorer_file_path)
upload_parser = api.parser()
upload_parser.add_argument('audio', type=werkzeug.datastructures.FileStorage, location='files')


@ns_endpoint.route("/upload", methods=['POST'])
class apiEndpoint(Resource):

    @api.expect(upload_parser)
    def post(self):
        args = upload_parser.parse_args()
        stream = args['audio'].stream
        wav_file = wave.open(stream, 'r')
        print(wav_file.getframerate())
        frames = wav_file.getnframes()
        print(frames)
        buffer = wav_file.readframes(frames)
        data16 = np.frombuffer(buffer, dtype=np.int16)
        transform_to_json = JsonTransformer()
        transcription = model.stt(data16)
        json_result = transform_to_json.transform(transcription)
        return Response(json_result, status=200, mimetype='application/json')
