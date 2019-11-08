# 소리가 보인다(Visible Voice)

> 2019 SW challenge 공모전 참가작
>
> 자연어 처리(Google Speech-to-Text)를 통한 음성파일 가이드 생성 어플리 케이션

## 소개 영상

<iframe width="640" height="360" src="https://youtu.be/dTMh03qzab4" frameborder="0" gesture="media" allowfullscreen=""></iframe>


## 대표 기능 소개

> 유저에게 편리한 인터페이스를 통해 음성파일 가이드 서비스 제공. 

![image](https://user-images.githubusercontent.com/36303777/68439799-4ba21d00-020c-11ea-8fa8-2376b1886e71.png)



### 메인화면

> 버튼별 기능

![image](https://user-images.githubusercontent.com/36303777/68449859-24f3de80-022c-11ea-92db-b28f4487eb6c.png)



### 텍스트 접근

> 해당텍스트를 터치하여 원하는 음악재생시점으로 재생.

![KakaoTalk_20191108_132039115](https://user-images.githubusercontent.com/36303777/68450175-6b960880-022d-11ea-97dd-a2fac8b575ae.gif)



### Seekbar 이동

> Seekbar 이동시에도 해당시점 가사 확인가능.

![KakaoTalk_20191108_132313925](https://user-images.githubusercontent.com/36303777/68450237-a861ff80-022d-11ea-864b-845e71916a2b.gif)

### 키워드 검색 기능

> 키워드 검색을 통해 키워드가 있는 문단 접근 가능. 선택 후 원하는 음악 재생시점으로 재생.

![KakaoTalk_20191108_132829075](https://user-images.githubusercontent.com/36303777/68450254-b6b01b80-022d-11ea-9cee-b4c7ad0f5f9b.gif)

## 워드클라우드와 텍스트

> 워드클라우드 이미지와 텍스트뷰 간의 이동은 제스처를 통해 접근가능.

![KakaoTalk_Video_20191108_1348_01_362](https://user-images.githubusercontent.com/36303777/68450514-b2383280-022e-11ea-9887-720e7b662479.gif)



## System Diagram

![image](https://user-images.githubusercontent.com/36303777/68442134-6af07880-0213-11ea-8cfc-444505fedd3b.png)







## Android 버전

- ProjectName : VisibleVoice
- Language : Java
- Minimum API level : API 25: Android 7.1.1 (Nougat)

## 참고
- DrawerLayout, NavigationView 구현 시 참고 :  https://duzi077.tistory.com/167
- 음악 재생 구현 참고 :  http://blog.naver.com/PostView.nhn?blogId=tkddlf4209&logNo=220746210643&redirect=Dlog&widgetTypeCall=true
