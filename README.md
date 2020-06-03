# authorization-server
# AuthorizationServerConfiguration is for oauth POC
# LixoAuthServerConfig  for Lixo Gateway integration with OAuth
# WebSecurityConfiguration -> auth.authenticationProvider(customAuthenticationProvider); for custom auth provider
#   -->  auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());  
# URL to get the token -> http://localhost:8081/oauth/token
# Method :: POST
# Postman: 
# Authorization tab -> username ->lixo , password -> lixoKey  , select dropdown  TYPE as -> basic auth
# Header tab -> authorization -> value genarated by default in postman and content type as json
#Body -> select form data
# grant_type -> password
# username -> Prabhu ( stored in DB)
# password ->Prabhu ( stored in DB as BCrypt format)



