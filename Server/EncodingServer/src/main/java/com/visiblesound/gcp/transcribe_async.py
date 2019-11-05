#-*- coding:utf-8 -*-

import argparse
import io
import os
import json
# run below command before run this script on the shell
# export GOOGLE_APPLICATION_CREDENTIALS=/home/ubuntu/2019SWChallenge/SpeechToText-09ef9c3df95e.json

storagePath = "/home/vvuser/"

# [START speech_transcribe_async_gcs]
def transcribe_gcs(gcs_uri, username, filename):
    """Asynchronously transcribes the audio file specified by the gcs_uri."""
    from google.cloud import speech
    from google.cloud.speech import enums
    from google.cloud.speech import types
    
    gcs_uri += (username + '/' + filename)
    print(gcs_uri)
    
    client = speech.SpeechClient()

    audio = types.RecognitionAudio(uri=gcs_uri)
    config = types.RecognitionConfig(
        encoding=enums.RecognitionConfig.AudioEncoding.FLAC,
        sample_rate_hertz=16000,
        language_code='ko-KR',
        audio_channel_count=1,
        #use_enhanced=True,
        enable_word_time_offsets=True)

    operation = client.long_running_recognize(config, audio)

    print('Waiting for operation to complete...')
    response = operation.result(timeout=3600)

    # Each result is for a consecutive portion of the audio. Iterate through
    # them to get the transcripts for the entire audio file.
 
    transcript = "" 
    sentences = []
    contents = {}
    for k in response.results:
                
        alternatives = k.alternatives
        sentence = {}
        #print(alternatives)
        for alternative in alternatives:
            #print(alternative)

            transcript = transcript + alternative.transcript.encode('utf-8')
            #f.write('Transcript: {}\n'.format(alternative.transcript.encode('utf-8')))
            sentence["sentence"] = alternative.transcript.encode('utf-8')
            words = []

            for word_info in alternative.words:
                content = {}
                content["word"] = word_info.word.encode('utf-8')
                content["start_time"]  = str(word_info.start_time.seconds + word_info.start_time.nanos * 1e-9)
                words.append(content)
            sentence["words"] = words
        
        sentences.append(sentence)  
        transcript += "\n" 

 
    contents["transcript"] = transcript
    contents["sentences"] = sentences
    #pickle.dump(contents,f)
    with io.open(storagePath + username + "/" + gcs_uri.split("/")[-1][:-len("flac")] + "json",'w') as f :
        f.write(json.dumps(contents,ensure_ascii=False).decode('utf-8'))
    print("finished")

# [END speech_transcribe_async_gcs]

if __name__ == '__main__':
    parser = argparse.ArgumentParser(
        description=__doc__,
        formatter_class=argparse.RawDescriptionHelpFormatter)
    parser.add_argument('basepath', help='File or GCS base path for audio file to be recognized')
    parser.add_argument('username', help='user directory')
    parser.add_argument('filename', help='filename')

    args = parser.parse_args()
    if args.basepath.startswith('gs://'):
        transcribe_gcs(args.basepath, args.username, args.filename)
        
        #gs://visible_voice/out01.flac
    else:
        transcribe_file(args.basepath)
