## Admin Account

### `api/v1/admin`

#### POST
__관리자 계정 생성__
<br>
관리자 계정은 최초에 한 개이며, 추가로 필요할 시 생성 할 수 있음.

##### Request Header:
```json
{
  "Content-Type": "application/json",
  "Data-Type": "json",
  "Authorization": "JWT(인가된 클라이언트만 관리자 계정 회원가입 가능)"
}
```

##### Request Body:
```json
{
  "id": "관리자 id",
  "pw": "관리자 pw",
  "name": "이름(Optional)",
  "email": "이메일(Optional)",
  "phone": "전화번호(Optional)"
}
```

##### Response:

###### 201 Created
회원가입이 정상적으로 수행됨.

###### 401 Unauthorized
인가받지 않은 사용자.

###### 422 Unprocessable Entity
입력된 정보가 유효하지 않음.
- response body
```json
{
  "msg": "유효성 검사 결과 메세지"
}
```

###### 400 Bad Request
올바르지 않은 요청.

---

### `api/v1/admin/{id}`

#### PUT

__관리자 계정 정보 수정__


##### Request Header:
```json
{
  "Content-Type": "application/json",
  "Data-Type": "json",
  "Authorization": "JWT(토큰에 포함된 id와 정보를 수정하고자 하는 계정의 id가 일치해야함)"
}
```

##### Request Body:
```json
{
  "pw": "관리자 pw",
  "name": "이름(Optional)",
  "email": "이메일(Optional)",
  "phone": "전화번호(Optional)"
}
```

##### Response: 
###### 200 Ok
관리자 정보 수정이 정상적으로 수행됨

###### 401 Unauthorized
인가받지 않은 사용자임.

###### 403 Forbidden
인가된 사용자이나, id가 일치하지 않음.

###### 422 Unprocessable Entity
입력된 정보가 유효하지 않음.
- response body: 
```json
{
  "msg": "유효성 검사 결과 메세지"
}
```

###### 400 Bad Request
올바르지 않은 요청
 - response body: 
```json
{
  "msg": "에러 메세지"
}
```

---

### `api/v1/admin/login`


#### POST
__관리자 계정 로그인__

##### Request Header: 
```json
{
  "Content-Type": "application/json",
  "Data-Type": "json"
}
```

##### Request Body: 
```json
{
  "id": "관리자 ID 입력",
  "pw": "관리자 비밀번호 입력"
}
```

##### Response:

###### 200 Ok

로그인이 정상적으로 수행됨.

- response header: 
```json
{
  "Authorization": "관리자 권한 인가를 위한 JWT"
}
```

###### 401 Unauthorized

아이디는 존재하나, 비밀번호가 일치하지 않음.

###### 404 Not Found

아이디가 존재하지 않음.

###### 400 Bad Request

올바르지 않은 요청.

---

## Board

### `api/v1/board/{board_id}`

#### GET
__게시판의 게시글을 불러온다.__

##### Request Header: 
```json
{
  "Content-Type": "application/json",
  "Data-Type": "json"
}
```

##### Query Parameter: 

- size: 불러올 게시물의 수(default: 24)
- page: 게시판 페이지(default: 1)
> ex) https://localhost:9000/api/v1/board/3?size=15&page=3

##### Response Body:  

```json
{
  "now_page": "현재 페이지 번호",
  "page_count": "전체 페이지 수",
  "posts": [
    {
      "board_id": 1,
      "post_id": 1,
      "title": "게시글 제목",
      "thumbnail": "게시글 썸네일(base64)",
      "content": "게시글 내용",
      "sequence": 1
    },
    {
      "board_id": 1,
      "post_id": 2,
      "title": "게시글 제목",
      "thumbnail": "게시글 썸네일(base64)",
      "content": "게시글 내용",
      "sequence": 2
    },
    {
      "board_id": 1,
      "post_id": 3,
      "title": "게시글 제목",
      "thumbnail": "게시글 썸네일(base64)",
      "content": "게시글 내용",
      "sequence": 3
    }
  ]
}
```
<br>

#### POST

__게시판에 게시글을 작성한다.__

##### Request Header: 
```json
{
  "Content-Type": "application/json",
  "Data-Type": "json",
  "Authorization": "JWT"
}
```
##### Request Body: 
```json
{
  "title": "게시글 제목(200자 미만)",
  "thumbnail": "게시글 썸네일(base64)",
  "content": "게시글 내용(2000자 미만)",
  "images": [
    "이미지(base64)",
    "이미지(base64)",
    "이미지(base64)",
    "이미지(base64)"
  ]
}
```

##### Response:

###### 201 Created
게시글이 정상적으로 등록됨.

###### 401 Unauthorized
인가받지 않은 사용자임.

###### 422 Unprocessable Entity
입력된 정보가 유효하지 않음.
 - response body: 
```json
{
  "msg": "유효성 검사 결과 메세지"
}
```

###### 400 Bad Request
올바르지 않은 요청
- response body: 
```json
{
  "msg": "에러 메세지"
}
```
