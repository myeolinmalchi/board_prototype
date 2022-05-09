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

## POST

### `api/v1/post`

#### GET

__여러 게시물의 정보를 불러온다. (이미지 제외, 썸네일 포함)__

##### Request Header: 
```json
{
  "Content-Type": "application/json",
  "Data-Type": "json"
}
```

##### Query Parameter: 
- size: 불러올 게시물의 수(default: 24)
- page: 불러올 페이지(default: 1)
- keyword(Optional) 
  - keyword가 포함된 제목의 게시글을 검색한다.
  - 값이 없을 경우 모든 게시글을 불러온다.
   
- boardId(Optional)
  - 해당 게시판의 게시글을 불러온다.
  - 값이 없을 경우 모든 게시판의 게시글을 불러온다.

##### Response Body: 

```json
{
  "nowPage": "현재 페이지 번호",
  "pageCount": "전체 페이지 수",
  "posts": [
    {
      "boardId": 1,
      "postId": 1,
      "title": "게시글 제목",
      "thumbnail": "게시글 썸네일(base64)",
      "content": "게시글 내용",
      "sequence": 1,
      "addedDate": 1652086339000
    },
    {
      "boardId": 1,
      "postId": 2,
      "title": "게시글 제목",
      "thumbnail": "게시글 썸네일(base64)",
      "content": "게시글 내용",
      "sequence": 2,
      "addedDate": 1652086339000
    },
    {
      "boardId": 1,
      "postId": 3,
      "title": "게시글 제목",
      "thumbnail": "게시글 썸네일(base64)",
      "content": "게시글 내용",
      "sequence": 3,
      "addedDate": 1652086339000
    }
  ]
}
```

#### POST

__게시글을 등록한다.__

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

###### 200 Ok
게시글이 정상적으로 등록됨.

###### 401 Unauthorized
인가받지 않은 사용자임.

###### 422 Unprocessable Entity
게시글의 제목 또는 내용의 길이가 너무 길 경우.
- response body:
```json
{
  "type": "content 또는 title",
  "length": "작성한 content 또는 title의 길이",
  "max": "content 또는 title의 최대 길이"
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

### `api/v1/post/{post_id}`

#### GET

__한 게시글의 정보를 불러온다. (이미지 포함)__

##### Request Header:
```json
{
  "Content-Type": "application/json",
  "Data-Type": "json"
}
```

##### Response Body: 
```json
{
  "postId": 12345,
  "boardId": 12345,
  "title": "게시글 제목",
  "thumbnail": "게시글 썸네일(base64)",
  "content": "게시글 내용",
  "sequence": 12345,
  "images": [
    {
      "postImageId": 1,
      "image": "이미지(base64)",
      "sequence": 1
    },
    {
      "postImageId": 2,
      "image": "이미지(base64)",
      "sequence": 2
    },
    {
      "postImageId": 3,
      "image": "이미지(base64)",
      "sequence": 3
    }
  ]
}
```

#### PUT

__게시글의 정보를 수정한다. (순서 제외)__

프론트에서 게시글 수정 로직: 
- 게시글의 정보를 불러온다.(`GET api/v1/post/{postId}`)
- 불러온 게시글의 정보를 게시글 에디터 페이지에 띄운다.
- 에디터에 입력된 모든 정보를 포함하여 서버에 수정 요청을 한다.(`PUT api/v1/post/{postId}`)

이하 `POST api/v1/post`과 동일.

---

### `api/v1/thumbnail`

#### GET

##### Request Header: 
```json
{
  "Content-Type": "application/json",
  "Data-Type": "json"
}
```

##### Query Parameter: 
- size: 불러올 썸네일의 개수(default: 5)
- boardId(Optional): 
    - 썸네일을 불러올 게시판
    - 값이 없을 경우, 모든 게시판에서 썸네일을 불러옴.

##### Response Body:

```json
[
  {
    "postId": 1,
    "boardId": 1,
    "thumbnail": "썸네일 이미지(base64)"
  },
  {
    "postId": 2,
    "boardId": 1,
    "thumbnail": "썸네일 이미지(base 64)"
  },
  {
    "postId": 3,
    "boardId": 2,
    "thumbnail": "썸네일 이미지(base64)"
  },
  {
    "postId": 4,
    "boardId": 3,
    "thumbnail": "썸네일 이미지(base64)"
  }
]
```
