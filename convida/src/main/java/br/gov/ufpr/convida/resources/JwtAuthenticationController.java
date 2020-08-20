package br.gov.ufpr.convida.resources;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

import br.gov.ufpr.convida.config.JwtTokenUtil;
import br.gov.ufpr.convida.domain.AccountCredentials;
import br.gov.ufpr.convida.domain.JwtResponse;
import br.gov.ufpr.convida.domain.RespostaLogin;
import br.gov.ufpr.convida.domain.User;
import br.gov.ufpr.convida.repository.UserRepository;
import br.gov.ufpr.convida.security.LdapConnection;
import br.gov.ufpr.convida.services.JwtUserDetailsService;

@RestController
@CrossOrigin
public class JwtAuthenticationController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private JwtUserDetailsService userDetailsService;
    @Autowired
    UserRepository user;
    @Autowired
    private PasswordEncoder bcrypt;

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AccountCredentials authenticationRequest)
            throws Exception {

        if (authenticationRequest.getUsername().endsWith("@ufpr.br")) {
            

            LdapConnection auth = new LdapConnection();
            if (auth.connectToLDAP(authenticationRequest.getUsername(), authenticationRequest.getPassword()) == true) {
                User newUser = user.findByLogin(authenticationRequest.getUsername());
                
                if (newUser == null) {
                    User u = new User();
                    u.setLogin((authenticationRequest.getUsername()));
                    u.setPassword(bcrypt.encode(authenticationRequest.getPassword()));
                    u.setEmail(authenticationRequest.getUsername());
                    user.insert(u);
                    String userId = u.getId();

                    
                    final UserDetails userDetails = userDetailsService
                            .loadUserByUsername(authenticationRequest.getUsername());
                    
      
                    
                    RespostaLogin r = new RespostaLogin();
                   
                    
                    final String token = jwtTokenUtil.generateToken(userDetails);
                    
                    r.setUserId(userId);
                    r.setToken(token);
                    
                    return ResponseEntity.ok().body(r);

                } else {
     
                	
                   
                    final UserDetails userDetails = userDetailsService
                            .loadUserByUsername(authenticationRequest.getUsername());
                    final String token = jwtTokenUtil.generateToken(userDetails);
                    
                    RespostaLogin r = new RespostaLogin();
                    r.setUserId(newUser.getId());
                    r.setToken(token);
                    
                    
                    return ResponseEntity.ok().body(r);
                }
            } else {
                return ResponseEntity.status(405).build();
            }
        } else {
        	
        		String url = "https://www.prppg.ufpr.br/siga/autenticacaoterceiros/discente/graduacao";
        		
        		HttpHeaders httpHeaders = new HttpHeaders();
        		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        	
        		JSONObject json = new JSONObject();
        		json.put("cpf", authenticationRequest.getUsername());
        		json.put("senha", authenticationRequest.getPassword());
        		json.put("token", "da39a3ee5e6b4b0d3255bfef95601890afd80709");
        		
        		HttpEntity <String> httpEntity = new HttpEntity <String> (json.toString(), httpHeaders);
        		RestTemplate restTemplate = new RestTemplate();
        		
        		try{
        		String response = restTemplate.postForObject(url, httpEntity, String.class);
	
        		User newUser = user.findByLogin(authenticationRequest.getUsername());
        		
        		if(newUser == null){
        				
        				User u = new User();
                        u.setLogin((authenticationRequest.getUsername()));
                        u.setPassword(bcrypt.encode(authenticationRequest.getPassword()));
                        user.insert(u);
                        
                        String userId = u.getId();
                        
                        final UserDetails userDetails = userDetailsService
                                .loadUserByUsername(authenticationRequest.getUsername());
                        RespostaLogin r = new RespostaLogin();
                        final String token = jwtTokenUtil.generateToken(userDetails);
                        
                        r.setUserId(userId);
                        r.setToken(token);
                        
                        return ResponseEntity.ok().body(r);
        				
        				
        			}else {
        			
        				RespostaLogin r = new RespostaLogin();
        				final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
        				final String token = jwtTokenUtil.generateToken(userDetails);
        				r.setUserId(newUser.getId());
        				r.setToken(token);
        				return ResponseEntity.ok().body(r);
        			}
        		
        		}catch(Exception e){
        			e.printStackTrace();
        			return ResponseEntity.status(401).build();
        		}			
        }
        		
    }

    private void authenticate(String username, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }
    }
}