import io
import os

from google.cloud import speech
from google.cloud.speech import enums
from google.cloud.speech import types

GOOGLE_APPLICATION_CREDENTIALS = "SpeechToText-09ef9c3df95e.json"

client = speech.SpeechClient()

with io.open("sample2.flac","rb") as audio_file:
    content = audio_file.read()

audio = types.RecognitionAudio(content=content)
config = types.RecognitionConfig(
    encoding='FLAC',
    sample_rate_hertz=16000,\
    language_code='ko-KR')

response = client.recognize(config,audio)

print("Waiting for operation to complete...")
print(len(response.results))

for k in response.results:
    alternatives = k.alternatives
    print(alternatives)
    for alternative in alternatives:
        #print(alternative)
        print('Transcript: {}'.format(alternative.transcript))
        print('Confidence: {}'.format(alternative.confidence))