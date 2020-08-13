# Store: Consumer-Entrepreneur Connection System
광운대학교 2020학년도 1학기 참빛설계학기 프로젝트입니다.

# Developers
박태성(FrontEnd), 오성수(BackEnd)

# Develop Spec
Android Studio, Google Firebase

# 기능
- Front-end는 drawble 경로에 있는 layout의 xml파일들을 추가하여 구현하였다. 큰 틀로는 로그인에 필요한 xml과, 게시판에 필요한 xml을 나누었고 로그인 이후 이동되는 창은 Fragment로 구현하였다. fragment란 화면 요소를 fragment 단위로 나누어 관리하여 레이아웃을 분리하여 관리할 수 있고, 이를 활용해 자유게시판, 인기게시판, 설정 등의 다양한 기능을 fragment로 관리하였다. 지역에 대한 목록은 구->동으로 여러 가지 구에 대한 정보가 먼저 보여 지고 구에 해당 하는 동이 보여야하므로, expandable listview를 사용해 정렬해주었다. 게시판은 한 지역에 대한 여러 글들로 구성되어 있고, 이 글들을 한 레이아웃에 관리하여야하므로 listview를 사용해 정렬해주었다. 설정 창은 포인트 관리, 게정 관리, 활동 기록 등 기능에 따라 분류되어 있으므로, gridview를 사용해 분류해주었다.
 
- 많은 Data들을 관리해주기 위해서 Back-end는 기본적으로 firestore DB에 대한 연동이 되어 있어야하고, firebase assistant를 이용해 쉽게 연동할 수 있었다. 회원가입을 할 때 회원의 정보가 firestore에 저장이 되고, Google 이나 Facebook을 이용한 SNS로그인을 할 때도 첫 로그인시 해당 정보가 firestore에 저장이 된다. firestore에 저장된 유저의 정보를 활용해 로그인을 할 수 있고 App을 들어갔을 때 이미 로그인이 되어있다면 onstart기능을 통해 로그인 화면이 아닌 메인 화면으로 이동할 수 있게 된다, 로그인 후 이동된 화면에 인기 게시판이 구현되어 있고 해당 화면은 firestore에 저장된 data를 가져와 자신이 설정한 지역의 인기 게시글을 보여준다.
 
- 인기게시판이 아닌 자유게시판과 지역게시판과 같은 경우에는 database에 저장된 정보들이 activity에서 해당 게시판에 알맞게 보여질 것이다. 이를 구성하는 게시판에 목록은 제목, 작성자, 날짜, 조회수 등으로 구현이 되어있으며, 날짜순, 조회순, 공감순으로 정렬이 가능하다. 검색 또한 제공되며 제목, 작성자, 날짜에 대해서 검색이 가능하고, text change listener를 사용해 텍스트가 변할 때마다 그에 맞게 게시판 목록이 변할 수 있도록 해주었다. 게시판은 글쓰기, 삭제, 공감, 댓글 기능을 제공하고 공감, 댓글, 글쓰기의 기능은 누구나 가능하도록 하였지만, 삭제기능은 댓글이나 글의 작성자만이 할 수 있도록 UID를 통해서 다른 사용자의 접근을 막아주었다. 지역목록에서 즐겨찾기 목록이 존재하고 자신이 등록한 지역이 추가 되어있을 것이고, 해당 정보는 database를 통해 자신의 uid를 통해 가져오게 된다.
 
- App을 잠시 나갔을 경우에 Background service에 대한 기능도 제공한다. Back ground에 있을 때도 app이 실행 중이므로 기본 channel에 대한 notification을 통해 알림이 보이도록 했다. 그리고 back ground에 있을 때 다른 유저가 자신의 글에 공감 혹은 댓글을 남겼을 때, 해당 user의 닉네임과 profile에 대한 notification을 통해 알림이 오도록 하였다. 그리고 알림의 개수에 따라 app의 icon옆에 오는 badge의 count를 늘려주어 알림 개수 또한 확인이 가능하다. notification에 대한 클릭으로 바로 해당 글로 이동할 수 있으며, 이동 시에는 foreground로 이동한 것이므로 background service가 종료된다. foreground에서는 우측 상단에 있는 알림 메뉴의 이미지에 대한 변경을 통해 알림이 왔는지를 확인 할 수 있고, 알림 메뉴에 대한 클릭을 통해 자세한 알림 정보를 확인 할 수 있다.
