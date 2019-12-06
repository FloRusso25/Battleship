/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codeoftheweb.salvo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
/**
 *
 * @author alumno
 */
@RepositoryRestResource
public interface PlayerRepository extends JpaRepository<Player, Long>{
    Player  findByUserName (String userName);
   
}

