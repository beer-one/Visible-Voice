#-*- coding:utf-8 -*-
# Python program to generate WordCloud 

# importing all necessery modules 
from wordcloud import WordCloud, STOPWORDS 
import matplotlib.pyplot as plt 
import pandas as pd 
from konlpy.tag import Okt
#from konlpy.utils import pprint

text = "억지로 하는 일은 질색 내 성격 성격 성격 성격 성격 주황색 주황색 성격상 난 못해 블랙 타이 영혼 담지 못해 화잍 라이 그만해 잇츠 어 레드 라잍 그 사이에 난 주황색을 칠하지 주황색 주황색 주황색 주황색 "
text = text.decode("utf-8")

#get nouns
okt = Okt()
nouns = okt.nouns(text)

#make a concated straing with nouns
concated_nouns = ''
for noun in nouns:
    concated_nouns = concated_nouns + ' ' + noun

#print(concated_nouns)

#set font
font_path = 'NanumGothic.ttf'
#set stopwords
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
plt.savefig("wordcloud.png")

#plt.show() 