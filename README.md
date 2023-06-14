# JWTAuthApi
JWT OAUTH API 흐름

![1](https://github.com/min-woo-Song/JWTAuthApi/assets/77622672/bf5ac860-3c14-4502-ab72-06826ee8c7a7)

## 클래스 설명
 - JwtAuthenticationToken: Security의 UsernamePasswordAuthenticationToken을 대체한다. 생성자는 두개로 각각 미인증, 인증 토큰을 생성하며 인증성공 시 SecurityContextHolder에 들어가는 객체

 - JwtAuthenticationFilter: 헤더에 담긴 토큰값을 가져오고 토큰이 있으면 getAuthentication(token)을 호출하여 미인증 JwtAuthenticationToken을 생성하고 JwtProvider의 authenticate(token)에 넘겨주고 검증한다.<br> 검증 성공시 SecurityContextHolder에 인증정보를 저장한다. 검증 실패 시 토큰의 상태 별 오류를 반환한다.<br>
                            
 - JwtAuthenticationProvider: Filter로부터 넘어온 token을 검증하고 인증된 JwtAuthenticationToken을 생성하여 반환한다.<br>

 - JwtTokenizer: Jwt 생성, 파싱 하는 역할을 하는 객체

# 소셜 로그인
 - CustomOAuth2UserService: OAuth2 로그인 시 DefaultOAuth2UserService 대신 동작한다. 가입된 소셜타입, 유저 정보를 가져오고 이미 가입된 사용자라면 업데이트, 첫 회원이라면 회원가입을 진행시킨다. <br>
 - OAuth2AuthenticationSuccessHandler: 로그인 인증 성공시 호출된다. JWT를 생성하고 클라이언트에 넘겨준다. <br>
 - CookieUtils: 쿠키 생성, 삭제, 조회, 직렬, 역직렬화
 - CookieAuthorizationRequestRepository: Authorication request를 Cookie에 저장하기 위한 객체
   redirect_uri 쿠키 : Authorization request시 파라미터로 넘어온 redirect_uri를 담고 나중에 application.yml의 authorizedRedirectUri와 일치하는지 확인시 사용
                            


## 회원가입 // Header AccessToken X
![1](https://github.com/min-woo-Song/JWTAuthApi/assets/77622672/df88b503-d43a-428e-84dc-882aefb43a1e)

## 로그인 // Header AccessToken X
![2](https://github.com/min-woo-Song/JWTAuthApi/assets/77622672/5674208e-0b5f-44ce-a8bf-419020e3d008)

## RefreshToken 갱신 // Header AccessToken X
![3](https://github.com/min-woo-Song/JWTAuthApi/assets/77622672/ff39c1d9-d688-4124-b43a-0832c40506da)

## logout // Header AccessToken O
![4](https://github.com/min-woo-Song/JWTAuthApi/assets/77622672/c8c18f6d-ea57-4e19-99cb-3ab254951e66)
