#-*- coding:utf-8 -*-
# Python program to generate WordCloud 

# importing all necessery modules 
from wordcloud import WordCloud, STOPWORDS 
import matplotlib
matplotlib.use('Agg')
import matplotlib.pyplot as plt 
import pandas as pd 
from konlpy.tag import Okt
import sys
import os
import json


#get argvs
input_json_path = sys.argv[1] #jsonFile
output_path = sys.argv[2] #output png path

# read file
print("input_json_path: " + input_json_path)
print("output_path: " + output_path)
with open(input_json_path, 'r') as f:
    data=f.read()

# parse file
obj = json.loads(data)

# show values
transcript = obj['transcript'].encode('utf-8')
print("parsed Transcript: ",transcript)


# input text for the test
# text = "억지로 하는 일은 질색 내 성격 성격 성격 성격 성격 주황색 주황색 성격상 난 못해 블랙 타이 영혼 담지 못해 화이트 라이 그만해 어 레드 라이트 그 사이에 난 주황색을 칠하지 주황색 주황색 주황색 주황색 "

#set input text here
text = transcript
text = text.decode("utf-8")

#get nouns with Konlpy
okt = Okt()
nouns = okt.nouns(text)

#make a concated straing with nouns
concated_nouns = ''
for noun in nouns:
    concated_nouns = concated_nouns + ' ' + noun

#set font and stopwords
print("pwd: ",os.getcwd())

#font_path = 'NanumGothic.ttf' 
#stopword_path = 'stopwords.txt'
font_path = 'src/main/java/com/visiblesound/wordcloud/NanumGothic.ttf'
stopword_path = 'src/main/java/com/visiblesound/wordcloud/stopwords.txt'
stopwords = set()

#read stop words from the file
if len(concated_nouns) > 100 :
    f_stopwords = open(stopword_path,"r")
    stopword_list_from_file = f_stopwords.readlines()
    for sw in stopword_list_from_file:
        stopwords.add(sw.strip('\n').decode('utf-8'))
    f_stopwords.close()

#init Wordcloud
wordcloud = WordCloud(width = 800, height = 800, 
                font_path = font_path,
                background_color ='white', 
                stopwords = stopwords, 
                min_font_size = 10)

#generate word cloud with nouns
wordcloud.generate(concated_nouns) 

# plot the WordCloud image                        
plt.figure(figsize = (8, 8), facecolor = None) 
plt.imshow(wordcloud) 
plt.axis("off") 
plt.tight_layout(pad = 0) 
plt.savefig(output_path) 
