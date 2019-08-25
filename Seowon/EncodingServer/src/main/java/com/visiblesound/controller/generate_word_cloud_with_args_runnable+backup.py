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

#get argvs
input_text = sys.argv[1]
output_path = sys.argv[2]

# input text for the test
# text = "억지로 하는 일은 질색 내 성격 성격 성격 성격 성격 주황색 주황색 성격상 난 못해 블랙 타이 영혼 담지 못해 화이트 라이 그만해 어 레드 라이트 그 사이에 난 주황색을 칠하지 주황색 주황색 주황색 주황색 "

#set input text here
text = input_text
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
font_path = 'src/main/java/com/visiblesound/controller/NanumGothic.ttf'
stopwords = set(STOPWORDS)   

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