.
[타이틀] : 간단한 블랙박스(녹화기능, 플레이기능)

[기술 스택] : Android api

[설명] : MediaRecorder를 이용해서 녹화 및 재생하는 앱을 작성합니다. 이때, 녹화화면에 위도,경도,속도를 표시하고 compass를 이용해서 방향을 나타냅니다. array_list를 생성해서 녹화 시작시 시작위도, 경도와 녹화종료시 끝위도, 경도를 녹화된 파일과 함께 SQLite에 저장후, array_list의 item을click시 녹화된 영상과 시작위도, 경도 그리고 끝위도, 경도가 재생되는 앱을 작성합니다.

[코드 설명] : compass를 이용해서 방향을 나타내는 나침반을 그리고, Location Service를 이용해서 위도, 경도, 그리고 속도를 확인합니다. Recording에서는 mediaRecorder 클래스를 활용해서 비디오 화면을 녹화하고, 녹화중지시 SQLiteDatabase에 저장합니다. 저장시에는 시작하는 위도와 경도, 끝 위도와 경도를 녹화된 화면의 경로에 전달하고 이를 id별로 관리하게 됩니다. List에서는 ListView를 가지고와서 ArrayAdapter를 set합니다. ArrayAdapter는 SQLiteDatabase에 각각의 id값에 대한 column값들을 가지고 있는 ArrayList를 전달받아 생성되어 ListView 생성시 DB에 저장된 값중에서 array_list에 add된 id+date로 list가 형성됩니다. List를 클릭시 녹화된 영상이 재생되며, 재생된 영상에는 시작위도 경도와 끝 위도 경도가 표시되어 집니다.

[기획 및 개발 의도] : Android 공부(mysql, mediarecorder 활용)

[역할] : 개발 100%
