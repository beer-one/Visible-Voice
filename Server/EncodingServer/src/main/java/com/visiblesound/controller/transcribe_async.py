#-*- coding:utf-8 -*-

import argparse
import io
import os
import json
# run below command before run this script on the shell
# export GOOGLE_APPLICATION_CREDENTIALS=/home/ubuntu/2019SWChallenge/SpeechToText-09ef9c3df95e.json


# [START speech_transcribe_async_gcs]
def transcribe_gcs(gcs_uri):
    """Asynchronously transcribes the audio file specified by the gcs_uri."""
    from google.cloud import speech
    from google.cloud.speech import enums
    from google.cloud.speech import types
    
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
    response = operation.result(timeout=90)

    # Each result is for a consecutive portion of the audio. Iterate through
    # them to get the transcripts for the entire audio file.



 
    transcript = "" 
    sentence_list = []
    contents = {}
    for k in response.results:
        
        
        alternatives = k.alternatives
        sentence = []
        #print(alternatives)
        for alternative in alternatives:
            #print(alternative)

            transcript = transcript + alternative.transcript.encode('utf-8')
            #f.write('Transcript: {}\n'.format(alternative.transcript.encode('utf-8')))
          
            
            for word_info in alternative.words:
                content = {}
                content["word"] = word_info.word.encode('utf-8')
                content["start_time"]  = str(word_info.start_time.seconds + word_info.start_time.nanos * 1e-9)
                
                #print(type(word_info.start_time))
                #print(str(word_info.start_time))
                sentence.append(content)
                #f.write('Word: {}, start_time: {}, end_time: {}\n'.format(
                #    word.encode('utf-8'),
                #    start_time.seconds + start_time.nanos * 1e-9)
        sentence_list.append(sentence)  
        transcript += "\n" 

 
    contents["Transcript"] = transcript
    contents["sentences"] = sentence_list
    #pickle.dump(contents,f)
    with io.open("upload/results/"+gcs_uri.split("/")[-1][:-len("flac")] + "json",'w') as f :
        f.write(json.dumps(contents,ensure_ascii=False).decode('utf-8'))
    print("finished")

# [END speech_transcribe_async_gcs]

if __name__ == '__main__':
    parser = argparse.ArgumentParser(
        description=__doc__,
        formatter_class=argparse.RawDescriptionHelpFormatter)
    parser.add_argument(
        'path', help='File or GCS path for audio file to be recognized')
    args = parser.parse_args()
    if args.path.startswith('gs://'):
        transcribe_gcs(args.path)
        
        #gs://visible_voice/out01.flac
    else:
        transcribe_file(args.path)