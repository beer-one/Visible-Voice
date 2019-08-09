import io
import os

from google.cloud import speech
from google.cloud.speech import enums
from google.cloud.speech import types

GOOGLE_APPLICATION_CREDENTIALS = "SpeechToText-09ef9c3df95e.json"

client = speech.SpeechClient()

with io.open("sample4.flac","rb") as audio_file:
    content = audio_file.read()

audio = types.RecognitionAudio(content=content)
config = types.RecognitionConfig(
    encoding='FLAC',
    sample_rate_hertz=16000,\
    language_code='ko-KR',
    enable_word_time_offsets=True)

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

        for word_info in alternative.words:
            word = word_info.word
            start_time = word_info.start_time
            end_time = word_info.end_time
            print('Word: {}, start_time: {}, end_time: {}'.format(
                word,
                start_time.seconds + start_time.nanos * 1e-9,
                end_time.seconds + end_time.nanos * 1e-9))
