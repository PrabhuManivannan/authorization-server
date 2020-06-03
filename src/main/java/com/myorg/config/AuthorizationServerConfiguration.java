package com.myorg.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter{

	
	  @Autowired private PasswordEncoder passwordEncoder;
	  
	@Autowired private DataSource dataSource;
	 
    
    @Autowired
	private UserDetailsService userDetailsService;

 	/*
	 * @Autowired private AuthenticationManager authenticationManager;
	 */

    private String clientid = "lixo";
    private String clientSecret = "{bcrypt}lixoKey";
    private String privateKey = "1234";
    private String publicKey = "12345";

    @Autowired
    //@Qualifier("authenticationManagerBean")
    private AuthenticationManager authenticationManager;
    
    @Bean
    public JwtAccessTokenConverter tokenEnhancer() {
       JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
       converter.setSigningKey(privateKey);
      // converter.setVerifierKey(publicKey);
       return converter;
    }
    @Bean
    public JwtTokenStore tokenStore() {
       return new JwtTokenStore(tokenEnhancer());
    }
	/*
	 * @Bean TokenStore jdbcTokenStore() { return new JdbcTokenStore(dataSource); }
	 */

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security.checkTokenAccess("isAuthenticated()").tokenKeyAccess("permitAll()");
       // .passwordEncoder(passwordEncoder);

    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
      clients.jdbc(dataSource).passwordEncoder(passwordEncoder);
    	
		/*
		 * clients.inMemory().withClient(clientid).secret(clientSecret).scopes("read",
		 * "write") .authorizedGrantTypes("password",
		 * "refresh_token","authorization_code","client_credentials").
		 * accessTokenValiditySeconds(20000) .refreshTokenValiditySeconds(20000);
		 */

    }

   
	/*
	 * @Override public void configure(AuthorizationServerEndpointsConfigurer
	 * endpoints) throws Exception { endpoints.tokenStore(jdbcTokenStore());
	 * endpoints.authenticationManager(authenticationManager); }
	 */
	/*
	 * @Bean
	 * 
	 * @Primary public DefaultTokenServices tokenServices() { DefaultTokenServices
	 * defaultTokenServices = new DefaultTokenServices();
	 * defaultTokenServices.setTokenStore(tokenStore());
	 * defaultTokenServices.setSupportRefreshToken(true); return
	 * defaultTokenServices; }
	 */
    
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
       endpoints.authenticationManager(authenticationManager).tokenStore(tokenStore())
       .accessTokenConverter(tokenEnhancer()).userDetailsService(userDetailsService);;
    }
}