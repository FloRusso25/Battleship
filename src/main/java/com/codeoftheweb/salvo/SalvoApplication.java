package com.codeoftheweb.salvo;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;


@SpringBootApplication
public class SalvoApplication extends SpringBootServletInitializer{

    public static void main(String[] args) {
            SpringApplication.run(SalvoApplication.class, args);
    }
    
    @Bean 
    public PasswordEncoder passEncoder(){
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
    
    @Bean
    public CommandLineRunner initData(PlayerRepository playerRepository, GameRepository gameRepository, GamePlayerRepository gPlRepository, ScoreRepository sRep){
        return(args)->{
            Player jack=playerRepository.save(new Player("j.bauer@ctu.gov", "Jack", "Bauer", passEncoder().encode("24")));
            Player chloe=playerRepository.save(new Player("c.obrian@ctu.gov", "Chloe", "O'Brian", passEncoder().encode("42")));
            Player kim=playerRepository.save(new Player("kim_bauer@gmail.com", "Kim", "Bauer", passEncoder().encode("kb")));
            Player tony=playerRepository.save(new Player("t.almeida@ctu.gov", "Tony", "Almeida", passEncoder().encode("mole")));
            Player palmer=playerRepository.save(new Player("d.palmer@whitehouse.gov", "Palmer", "Palmer", passEncoder().encode("pal")));
            
            Game game1=gameRepository.save(new Game(LocalDateTime.now()));
            Game game2=gameRepository.save(new Game(LocalDateTime.now().plusHours(1)));
            Game game3=gameRepository.save(new Game(LocalDateTime.now().plusHours(2)));
            Game game4=gameRepository.save(new Game(LocalDateTime.now().plusHours(3)));
            
            Score scJ=sRep.save(new Score(game1, jack, 837, Score.statusScore.WON));
            Score scCh=sRep.save(new Score(game1, chloe, 259, Score.statusScore.LOST));
            
            List<String> shipLoc1=Arrays.asList("H2", "H3", "H4");
            List<String> shipLoc2=Arrays.asList("B5", "C5", "D5");
            List<String> shipLoc3=Arrays.asList("E1", "F1", "G1");
            
            Ship shipDes=new Ship(Ship.typeShip.DESTROYER, shipLoc1); 
            Ship shipBat=new Ship(Ship.typeShip.BATTLESHIP, shipLoc2);
            Ship shipSub=new Ship(Ship.typeShip.SUBMARINE, shipLoc3);
//          Ship shipDes=new Ship(Ship.typeShip.DESTROYER);
//          Ship shipPB=new Ship(Ship.typeShip.PATROLBOAT));
            
            List<String> salvo1=Arrays.asList("B5", "C5", "F1");
            List<String> salvo2=Arrays.asList("B4", "B5", "B6");
            List<String> salvo3=Arrays.asList("H2", "D5");
            List<String> salvo4=Arrays.asList("E1", "H3", "A2");
            
            GamePlayer gp1= gPlRepository.save(new GamePlayer(game1, jack));
            GamePlayer gp2= gPlRepository.save(new GamePlayer(game1, chloe));
            GamePlayer gp3= gPlRepository.save(new GamePlayer(game2, jack));
            GamePlayer gp4= gPlRepository.save(new GamePlayer(game2, chloe));
            GamePlayer gp5= gPlRepository.save(new GamePlayer(game3, chloe));
            GamePlayer gp6= gPlRepository.save(new GamePlayer(game3, tony));
            GamePlayer gp7= gPlRepository.save(new GamePlayer(game4, palmer));
            
                 
            
            gp1.addShip(shipDes);
            gp1.addShip(shipSub);
            gp1.addSalvo(new Salvo(1, salvo1));
            gp1.addSalvo(new Salvo(2, salvo3));
            gp2.addShip(shipBat);
            gp2.addSalvo(new Salvo(1, salvo2));
            gp2.addSalvo(new Salvo(2, salvo4));
            
                 
            gPlRepository.save(gp1);
            gPlRepository.save(gp2);
            
        };
    }
    
}


@Configuration
class WebSecurityConfiguration extends GlobalAuthenticationConfigurerAdapter {

  @Autowired
  PlayerRepository playerRepository;

  @Override
  public void init(AuthenticationManagerBuilder auth) throws Exception {
    auth.userDetailsService(inputName-> {
      Player player = playerRepository.findByUserName(inputName);
        if (player != null) {
          return new User(player.getUserName(), player.getPass(), 
                  AuthorityUtils.createAuthorityList("USER"));
        } else {
          throw new UsernameNotFoundException("Unknown user: " + inputName);
        }
    });
  }
  
  
}


@EnableWebSecurity
@Configuration
class WebSecurityConfig extends WebSecurityConfigurerAdapter{
    
    @Override
    protected void configure(HttpSecurity http) throws Exception{
        
        
        http.authorizeRequests().antMatchers("/api/game_view/**").hasAnyAuthority("USER", "ADMIN")
        .antMatchers("/rest/**").hasAuthority("ADMIN");
        
        http.formLogin().usernameParameter("userName").passwordParameter("pass").loginPage("/api/login");
        http.logout().logoutUrl("/api/logout");
        
        http.csrf().disable();
        http.exceptionHandling().authenticationEntryPoint((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));
        http.formLogin().successHandler((req, res, auth) -> clearAuthenticationAttributes(req));
        http.formLogin().failureHandler((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));
        http.logout().logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler());
    }

    private void clearAuthenticationAttributes(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
          session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
        }
    }
}








