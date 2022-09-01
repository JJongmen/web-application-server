# 실습을 위한 개발 환경 세팅
* https://github.com/slipp/web-application-server 프로젝트를 자신의 계정으로 Fork한다. Github 우측 상단의 Fork 버튼을 클릭하면 자신의 계정으로 Fork된다.
* Fork한 프로젝트를 eclipse 또는 터미널에서 clone 한다.
* Fork한 프로젝트를 eclipse로 import한 후에 Maven 빌드 도구를 활용해 eclipse 프로젝트로 변환한다.(mvn eclipse:clean eclipse:eclipse)
* 빌드가 성공하면 반드시 refresh(fn + f5)를 실행해야 한다.

# 웹 서버 시작 및 테스트
* webserver.WebServer 는 사용자의 요청을 받아 RequestHandler에 작업을 위임하는 클래스이다.
* 사용자 요청에 대한 모든 처리는 RequestHandler 클래스의 run() 메서드가 담당한다.
* WebServer를 실행한 후 브라우저에서 http://localhost:8080으로 접속해 "Hello World" 메시지가 출력되는지 확인한다.

# 각 요구사항별 학습 내용 정리
* 구현 단계에서는 각 요구사항을 구현하는데 집중한다. 
* 구현을 완료한 후 구현 과정에서 새롭게 알게된 내용, 궁금한 내용을 기록한다.
* 각 요구사항을 구현하는 것이 중요한 것이 아니라 구현 과정을 통해 학습한 내용을 인식하는 것이 배움에 중요하다. 

### 요구사항 1 - http://localhost:8080/index.html로 접속시 응답
* #### 새롭게 알게 된 내용
  * 원격 서버를 이용한 서버 배포
  * Stream을 통한 입출력
  * 프레임워크 없이 직접 HTTP 메시지를 파싱하는 것은 빡세다.
* #### 궁금한 내용
  * 큰 규모의 코드에서 테스트코드를 작성하는 방법 (테스트코드를 작성하기에 애매한 부분이 있었다.)
  * 원격 서버에서 수동으로 git pull , 빌드 후 서버 시작이 아닌 자동으로 하는 방법?
  * /index.html로 접속시 css, js, ico 파일들을 요청하고 있는데 이를 어떻게 응답해줘야할지 모르겠다.
  

### 요구사항 2 - get 방식으로 회원가입
* #### 궁금한 내용
  * 클라이언트 측에서 정보를 입력하지 않으면 null 값이 넘어오는데 제대로 된 값을 입력하지 않으면 애초에 넘어오지 않도록 설계하는 방법

### 요구사항 3 - post 방식으로 회원가입
* #### 새롭게 알게 된 내용
  * GET 방식에서는 URL 뒤에 파라미터가 붙어서 넘어 왔었는데 POST 방식에서는 Request Message Body에 데이터가 넘어온다.
* #### 고민한 내용
  * HTTP 요청 메시지를 한 줄씩 읽으면서 필요한 헤더만 저장하는게 나을지 아니면 Map<String, String> 형식으로 모든 헤더를 미리 저장해서 필요한 헤더를 꺼내 쓰는게 나을지 고민하였다.
  * 현재 요구사항에서는 필요한 헤더가 별로 없어서 한 줄씩 읽는 도중에 저장하였는데 요구사항이 많아질수록 헤더를 Map에 저장해놓는 것이 편할 것 같다.
  * 메서드가 하나의 일만 하도록 리팩토링을 하려고 노력했다.

### 요구사항 4 - redirect 방식으로 이동
* #### 새롭게 알게 된 내용
  * 302 Found HTTP 상태 코드는 Location 헤더에 이동할 URL을 넣어줘야 한다.
* #### 궁금한 내용
  * 302 상태 코드와 같이 상황별로 어떤 HTTP 상태 코드를 반환해줘야 하는지 고민하게 되었다.

### 요구사항 5 - cookie
* #### 고민한 내용
  * 요구사항 3에서 고민했던 내용인 헤더에 대한 정보가 필요해질 것 같아서 HttpRequestMessage 라는 클래스를 생성해서 요청 메시지를 저장하였다.
  * 응답 메시지의 헤더를 작성하는 메서드가 늘어남에 따라 추후에 계속 늘어나게 된다면 따로 클래스를 분리할 필요가 있을 것 같다.
  * 로그인 정보를 암호화를 안했기 때문에 서버에 보내는 과정에서 누군가 낚아챌 수 있을 것 같다.
  * 또한 logined 쿠키에 로그인 여부를 저장하였는데 쿠키를 변조해서 로그인을 시도할 수 있을 것 같다.
* #### 궁금한 내용
  * 보안에 문제가 있기 때문에 추후에 이 부분들에 관하여 공부할 필요가 있다.

### 요구사항 6 - 사용자 목록 출력
* #### 고민한 내용
  * 헤더의 값을 조회할 때 틀린 헤더를 인자로 넘겨주면 null을 반환하는 것이 맞는지 모르겠다.
* #### 궁금한 내용
  * 세션을 활용해 로그인 상태를 확인하는 방법


### 요구사항 7 - stylesheet 적용
* #### 새롭게 알게 된 내용
  * 파일 경로를 제대로 지정이 안되있어서 css 적용이 안된줄 알았는데 Content-Type이 text/html 형식으로 지정되있어서 적용이 안되던 거였다.

### 리팩토링 1 - 요청 데이터를 처리하는 로직을 별도의 클래스로 분리
* 고민한 내용
  * 이미 구현할 때 요청 데이터를 처리하는 로직들은 별도의 클래스로 분리하였었다.
  * 구현할 당시에는 웹에서 접속 없이 테스트코드를 어떻게 짤 수 있을까 고민하였었다.
* 새롭게 알게 된 내용
  * 미리 txt 파일에 웹에서 요청한 내용을 담아놓고 파일을 읽어서 테스트를 진행할 수 있는 것을 알게되었다.
### heroku 서버에 배포 후
* 